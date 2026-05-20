package com.projectsphere.automation.pages;

import com.projectsphere.automation.constants.AppConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class EditTeamPage {

    private static final Logger logger = LoggerFactory.getLogger(EditTeamPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators — based on real DOM ──────────────────────────────────────────
    // Edit Team opens as an inline form inside <section class="ax-section">
    // Fields:
    //   teamName       : input[formcontrolname='teamName']
    //   project chip   : div.ax-selected-chip  (read-only chip, not a select)
    //   members search : div.ax-search input   (search developers)
    //   member add btn : div.ax-picker-row:not(.selected) button.ax-picker-action.primary
    //   scrum master   : select[formcontrolname='scrumMasterId']
    //     options: value="0: null" → None, "1: 3" → scrum master, "2: 8" → Jayavarman P, etc.
    // Footer:
    //   Cancel : div.ax-form-foot button.ax-btn.ghost
    //   Save   : div.ax-form-foot button.ax-btn.primary  (type="submit")
    // Success: div.ax-ok

    private final By pageSection         = By.cssSelector("section.ax-section");
    private final By teamNameField       = By.cssSelector("section.ax-section input[formcontrolname='teamName']");
    private final By projectChip         = By.cssSelector("section.ax-section div.ax-selected-chip");
    private final By searchMemberInput   = By.cssSelector("section.ax-section div.ax-search input");
    // Add (+) button: button.ax-picker-action.primary title="Add"
    private final By memberAddButtons    = By.cssSelector("section.ax-section div.ax-picker-row button.ax-picker-action.primary[title='Add']");
    // Remove (-) button: button.ax-picker-action (no primary) title="Remove"
    private final By memberRemoveBtns    = By.cssSelector("section.ax-section div.ax-picker-row button.ax-picker-action[title='Remove']");
    private final By memberChips         = By.cssSelector("section.ax-section div.ax-chips span.ax-chip");
    private final By scrumMasterDropdown = By.cssSelector("section.ax-section select[formcontrolname='scrumMasterId']");
    private final By saveChangesBtn      = By.cssSelector("section.ax-section div.ax-form-foot button.ax-btn.primary");
    private final By cancelBtn           = By.cssSelector("section.ax-section div.ax-form-foot button.ax-btn.ghost");
    private final By successMessage      = By.cssSelector("div.ax-ok");

    public EditTeamPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("EditTeamPage initialized");
    }

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(pageSection),
                    ExpectedConditions.visibilityOfElementLocated(teamNameField)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public EditTeamPage updateTeamName(String name) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(teamNameField));
        el.clear();
        el.sendKeys(name);
        logger.info("Updated team name to: {}", name);
        return this;
    }

    public String getTeamName() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(teamNameField)).getAttribute("value");
        } catch (Exception e) {
            return "";
        }
    }

    public EditTeamPage searchAndAddMember(String nameOrEmail) {
        try {
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchMemberInput));
            input.clear();
            input.sendKeys(nameOrEmail);
            logger.info("Searched for member: {}", nameOrEmail);
            // Wait for Add button to appear in filtered results, then JS click
            WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(memberAddButtons));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addBtn);
            logger.info("Clicked + (Add) button for member: {}", nameOrEmail);
            // Wait for row to become selected (member added)
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("section.ax-section div.ax-picker-row.selected")));
        } catch (Exception e) {
            logger.warn("Could not add member '{}': {}", nameOrEmail, e.getMessage());
        }
        return this;
    }

    public EditTeamPage removeFirstMember() {
        try {
            List<WebElement> removeButtons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(memberRemoveBtns));
            if (!removeButtons.isEmpty()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", removeButtons.get(0));
                logger.info("Clicked − (Remove) button for first member");
                // Wait for the selected row to go back to unselected
                wait.until(ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector("section.ax-section div.ax-picker-row.selected")));
            }
        } catch (Exception e) {
            logger.warn("No remove buttons found: {}", e.getMessage());
        }
        return this;
    }

    public EditTeamPage selectScrumMaster(String memberName) {
        try {
            Select sel = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(scrumMasterDropdown)));
            sel.selectByVisibleText(memberName);
            logger.info("Selected scrum master: {}", memberName);
        } catch (Exception e) {
            try {
                Select sel = new Select(driver.findElement(scrumMasterDropdown));
                sel.selectByIndex(1);
                logger.info("Selected first available scrum master");
            } catch (Exception ex) {
                logger.warn("Could not select scrum master: {}", ex.getMessage());
            }
        }
        return this;
    }

    public List<WebElement> getMembers() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(memberChips));
        } catch (Exception e) {
            return List.of();
        }
    }

    /** Returns true if any "In this team" tag is visible (member lock indicator). */
    public boolean isLockIndicatorVisible() {
        try {
            By locator = By.cssSelector("section.ax-section span.ax-picker-tag.ok");
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Clicks Save Changes — stays on same inline form; check isSuccessMessageDisplayed(). */
    public EditTeamPage clickSaveChanges() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(saveChangesBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Save Changes on Edit Team");
        } catch (Exception e) {
            logger.warn("Save Changes button not found: {}", e.getMessage());
        }
        return this;
    }

    public ManageTeamsAdminPage clickCancel() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(cancelBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Cancel on Edit Team");
        } catch (Exception e) {
            logger.warn("Cancel button not found: {}", e.getMessage());
        }
        return new ManageTeamsAdminPage(driver);
    }

    public boolean isProjectChipDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(projectChip)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
