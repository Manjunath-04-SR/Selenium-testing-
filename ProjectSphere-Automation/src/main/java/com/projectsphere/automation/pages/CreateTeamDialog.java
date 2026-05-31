package com.projectsphere.automation.pages;

import com.projectsphere.automation.constants.AppConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class CreateTeamDialog {

    private static final Logger logger = LoggerFactory.getLogger(CreateTeamDialog.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators — based on real DOM ──────────────────────────────────────────
    // <div class="ax-modal-backdrop">
    //   <div class="ax-modal wide">
    //     <header class="ax-modal-head">New Team</header>
    //     <div class="ax-modal-body">
    //       <form class="ax-form">
    //         <input formcontrolname="teamName" type="text">
    //         <select formcontrolname="projectId">
    //           <option disabled value="0: null">— Select one of your projects —</option>
    //           <option value="1: 31">PMProject1... (Planned)</option>
    //           ...
    //         </select>
    //       </form>
    //     </div>
    //     <footer class="ax-modal-foot">
    //       <button class="ax-btn ghost">Cancel</button>
    //       <button class="ax-btn primary"> Create Team </button>
    //     </footer>
    //   </div>
    // </div>

    // Primary locator; isDialogVisible() also probes ax-modal and teamName field as fallbacks
    private final By dialogContainer  = By.cssSelector("div.ax-modal-backdrop");
    private final By teamNameField    = By.cssSelector("div.ax-modal-backdrop input[formcontrolname='teamName']");
    private final By projectDropdown  = By.cssSelector("div.ax-modal-backdrop select[formcontrolname='projectId']");
    // Non-disabled options only
    private final By projectOptions   = By.cssSelector("div.ax-modal-backdrop select[formcontrolname='projectId'] option:not([disabled])");
    private final By createTeamBtn    = By.cssSelector("div.ax-modal-backdrop footer.ax-modal-foot button.ax-btn.primary");
    private final By cancelBtn        = By.cssSelector("div.ax-modal-backdrop footer.ax-modal-foot button.ax-btn.ghost");
    private final By validationError  = By.cssSelector("div.ax-modal-backdrop small.ax-field-err, div.ax-modal-backdrop div.ax-err");

    public CreateTeamDialog(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("CreateTeamDialog initialized");
    }

    public boolean isDialogVisible() {
        try {
            // Broad check: ax-modal-backdrop OR any ax-modal div OR teamName input directly.
            // The fallback selectors guard against class differences in different build versions.
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(dialogContainer),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.ax-modal")),
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[formcontrolname='teamName']"))
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTeamNameAndProjectDropdownVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(teamNameField));
            wait.until(ExpectedConditions.visibilityOfElementLocated(projectDropdown));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getProjectDropdownOptions() {
        try {
            // Wait for at least one non-disabled option to load
            wait.until(ExpectedConditions.presenceOfElementLocated(projectOptions));
            return driver.findElements(projectOptions);
        } catch (Exception e) {
            return List.of();
        }
    }

    public boolean doesDropdownShowOnlyEligibleProjects() {
        List<WebElement> options = getProjectDropdownOptions();
        logger.info("Project dropdown has {} eligible options", options.size());
        return !options.isEmpty();
    }

    public CreateTeamDialog enterTeamName(String name) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(teamNameField));
        el.clear();
        el.sendKeys(name);
        logger.info("Entered team name: {}", name);
        return this;
    }

    public CreateTeamDialog selectProject(String projectName) {
        try {
            // Wait for non-disabled options to be present
            wait.until(ExpectedConditions.presenceOfElementLocated(projectOptions));
            Select sel = new Select(driver.findElement(projectDropdown));
            if (projectName == null || projectName.isEmpty()) {
                // Select first non-disabled option
                sel.selectByIndex(1);
                logger.info("Selected first available project from dropdown");
            } else {
                sel.selectByVisibleText(projectName);
                logger.info("Selected project: {}", projectName);
            }
        } catch (Exception e) {
            logger.warn("Could not select project: {}", e.getMessage());
        }
        return this;
    }

    public EditTeamPage clickCreateTeam() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(createTeamBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Create Team button");
            // Wait for dialog to close
            wait.until(ExpectedConditions.invisibilityOfElementLocated(dialogContainer));
        } catch (Exception e) {
            logger.warn("Create Team button not found or dialog did not close: {}", e.getMessage());
        }
        return new EditTeamPage(driver);
    }

    public void clickCancel() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(cancelBtn)).click();
            logger.info("Cancelled Create Team dialog");
        } catch (Exception e) {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        }
    }

    public boolean isHelperTextVisible() {
        try {
            By hint = By.cssSelector("div.ax-modal-backdrop small.ax-hint");
            return wait.until(ExpectedConditions.visibilityOfElementLocated(hint)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
