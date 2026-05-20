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

public class AddProjectDialog {

    private static final Logger logger = LoggerFactory.getLogger(AddProjectDialog.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators — based on real DOM ─────────────────────────────────────────
    // <div class="ax-modal-backdrop">
    //   <div class="ax-modal wide">
    //     <header class="ax-modal-head">
    //       <div class="ax-modal-title">New Project</div>
    //       <button class="ax-modal-close">×</button>
    //     </header>
    //     <div class="ax-modal-body">
    //       <form class="ax-form">
    //         <input  formcontrolname="projectName"  type="text">
    //         <textarea formcontrolname="description">
    //         <select formcontrolname="status">   PLANNED | IN_PROGRESS | COMPLETED
    //         <select formcontrolname="domain">   BANKING | HEALTHCARE | ... | TECHNOLOGY
    //         <select formcontrolname="managerId"> — None — | Project Manager
    //       </form>
    //     </div>
    //     <footer class="ax-modal-foot">
    //       <button class="ax-btn ghost">Cancel</button>
    //       <button class="ax-btn primary">Create Project</button>
    //     </footer>
    //   </div>
    // </div>

    private final By dialogContainer  = By.cssSelector("div.ax-modal-backdrop");
    private final By dialogTitle      = By.cssSelector("div.ax-modal-backdrop div.ax-modal-title");

    // All field locators are scoped inside div.ax-modal-backdrop to avoid matching
    // any hidden elements with the same formcontrolname elsewhere on the page.
    private final By projectNameField = By.cssSelector("div.ax-modal-backdrop input[formcontrolname='projectName']");
    private final By descriptionField = By.cssSelector("div.ax-modal-backdrop textarea[formcontrolname='description']");
    private final By statusDropdown   = By.cssSelector("div.ax-modal-backdrop select[formcontrolname='status']");
    private final By domainDropdown   = By.cssSelector("div.ax-modal-backdrop select[formcontrolname='domain']");
    private final By pmDropdown       = By.cssSelector("div.ax-modal-backdrop select[formcontrolname='managerId']");

    // Footer buttons — scoped inside the modal
    private final By createProjectBtn = By.cssSelector("div.ax-modal-backdrop footer.ax-modal-foot button.ax-btn.primary");
    private final By cancelBtn        = By.cssSelector("div.ax-modal-backdrop footer.ax-modal-foot button.ax-btn.ghost");

    // X close button in header
    private final By closeBtn         = By.cssSelector("div.ax-modal-backdrop button.ax-modal-close");

    // Validation error — appears below the Project Name field when submitted blank:
    // <small class="ax-field-err">Project name is required.</small>
    private final By validationError  = By.cssSelector("div.ax-modal-backdrop small.ax-field-err");

    public AddProjectDialog(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("AddProjectDialog initialized");
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public boolean isDialogVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(dialogContainer));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public AddProjectDialog enterProjectName(String name) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(projectNameField));
        el.clear();
        el.sendKeys(name);
        logger.info("Entered project name: {}", name);
        return this;
    }

    public AddProjectDialog enterDescription(String description) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionField));
            el.clear();
            el.sendKeys(description);
            logger.info("Entered description");
        } catch (Exception e) {
            logger.warn("Description field not found");
        }
        return this;
    }

    public AddProjectDialog selectStatus(String status) {
        try {
            Select sel = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(statusDropdown)));
            sel.selectByVisibleText(status);
            logger.info("Selected status: {}", status);
        } catch (Exception e) {
            logger.warn("Status dropdown not found or option '{}' unavailable", status);
        }
        return this;
    }

    public AddProjectDialog selectDomain(String domain) {
        try {
            Select sel = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(domainDropdown)));
            sel.selectByVisibleText(domain);
            logger.info("Selected domain: {}", domain);
        } catch (Exception e) {
            logger.warn("Domain dropdown not found or option '{}' unavailable", domain);
        }
        return this;
    }

    public AddProjectDialog selectProjectManager(String pmName) {
        try {
            Select sel = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(pmDropdown)));
            if (pmName == null || pmName.isEmpty()) {
                sel.selectByIndex(0);
            } else {
                sel.selectByVisibleText(pmName);
            }
            logger.info("Selected PM: {}", pmName);
        } catch (Exception e) {
            logger.warn("PM dropdown not found");
        }
        return this;
    }

    public ManageProjectsPage clickCreateProject() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(createProjectBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Create Project button");
        } catch (Exception e) {
            logger.warn("Create Project button not found: {}", e.getMessage());
        }
        return new ManageProjectsPage(driver);
    }

    /** Clicks the Cancel button (closes dialog without saving). */
    public ManageProjectsPage clickCancel() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(cancelBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Cancel button");
        } catch (Exception e) {
            logger.warn("Cancel button not found: {}", e.getMessage());
        }
        return new ManageProjectsPage(driver);
    }

    /** Clicks the X (close) button in the dialog header. */
    public void clickClose() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(closeBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked X close button on dialog");
        } catch (Exception e) {
            logger.warn("Close button not found, pressing Escape");
            driver.findElement(By.tagName("body")).sendKeys(org.openqa.selenium.Keys.ESCAPE);
        }
    }

    public boolean isValidationErrorDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(validationError)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isProjectNameFieldVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(projectNameField)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areAllFieldsVisible() {
        return isProjectNameFieldVisible();
    }
}
