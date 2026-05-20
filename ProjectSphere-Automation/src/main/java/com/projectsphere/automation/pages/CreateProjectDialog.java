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

public class CreateProjectDialog {

    private static final Logger logger = LoggerFactory.getLogger(CreateProjectDialog.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators — based on real DOM ──────────────────────────────────────────
    // <div class="ax-modal-backdrop">
    //   <div class="ax-modal wide">
    //     <header class="ax-modal-head">New Project</header>
    //     <div class="ax-modal-body">
    //       <form class="ax-form">
    //         <input formcontrolname="projectName" type="text">
    //         <textarea formcontrolname="description" rows="3">
    //         <span class="ax-status-locked">Planned</span>  ← status is locked, not a dropdown
    //         <select formcontrolname="domain">
    //       </form>
    //     </div>
    //     <footer class="ax-modal-foot">
    //       <button class="ax-btn ghost">Cancel</button>
    //       <button class="ax-btn primary"> Create Project </button>
    //     </footer>
    //   </div>
    // </div>

    private final By dialogContainer   = By.cssSelector("div.ax-modal-backdrop");
    private final By projectNameField  = By.cssSelector("div.ax-modal-backdrop input[formcontrolname='projectName']");
    private final By descriptionField  = By.cssSelector("div.ax-modal-backdrop textarea[formcontrolname='description']");
    private final By domainDropdown    = By.cssSelector("div.ax-modal-backdrop select[formcontrolname='domain']");
    private final By createProjectBtn  = By.cssSelector("div.ax-modal-backdrop footer.ax-modal-foot button.ax-btn.primary");
    private final By cancelBtn         = By.cssSelector("div.ax-modal-backdrop footer.ax-modal-foot button.ax-btn.ghost");
    private final By validationError   = By.cssSelector("div.ax-modal-backdrop small.ax-field-err, div.ax-modal-backdrop div.ax-err");

    public CreateProjectDialog(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("CreateProjectDialog initialized");
    }

    public boolean isDialogVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(dialogContainer));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areFieldsVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(projectNameField));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public CreateProjectDialog enterProjectName(String name) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(projectNameField));
        el.clear();
        el.sendKeys(name);
        logger.info("Entered project name: {}", name);
        return this;
    }

    public CreateProjectDialog enterDescription(String desc) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionField));
            el.clear();
            el.sendKeys(desc);
            logger.info("Entered description");
        } catch (Exception e) {
            logger.warn("Description field not found");
        }
        return this;
    }

    public CreateProjectDialog selectDomain(String domain) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(domainDropdown)))
                    .selectByValue(domain);
            logger.info("Selected domain: {}", domain);
        } catch (Exception e) {
            try {
                new Select(driver.findElement(domainDropdown))
                        .selectByVisibleText(domain);
            } catch (Exception ex) {
                logger.warn("Domain dropdown unavailable: {}", ex.getMessage());
            }
        }
        return this;
    }

    public PMDashboardPage clickCreateProject() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(createProjectBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Create Project button");
            // Wait for dialog to close before returning
            wait.until(ExpectedConditions.invisibilityOfElementLocated(dialogContainer));
        } catch (Exception e) {
            logger.warn("Create Project button not found: {}", e.getMessage());
        }
        return new PMDashboardPage(driver);
    }

    public void clickCancel() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(cancelBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Cancel");
        } catch (Exception e) {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        }
    }

    public boolean isValidationErrorDisplayed() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(validationError));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
