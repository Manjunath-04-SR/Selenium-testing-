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

public class EditProjectAdminPage {

    private static final Logger logger = LoggerFactory.getLogger(EditProjectAdminPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators — based on real DOM ──────────────────────────────────────────
    // Component : <app-project-edit>
    // Page title: <h1 class="ax-page-title">ProjectName</h1>
    // Form      : <form class="ax-form">
    //   <input  formcontrolname="projectName"  type="text">
    //   <textarea formcontrolname="description">
    //   <select formcontrolname="status">   PLANNED | IN_PROGRESS | COMPLETED
    //   <select formcontrolname="domain">   BANKING | HEALTHCARE | ... | TECHNOLOGY
    //   <select formcontrolname="managerId">  — None — | Project Manager
    // Footer    : <div class="ax-form-foot">
    //   <button class="ax-btn ghost"   type="button">Cancel</button>
    //   <button class="ax-btn primary" type="submit">Save Changes</button>
    // Header    : <button class="ax-btn danger">Delete Project</button>  (page header, NOT in form)
    //             <button class="ax-btn ghost">Back</button>              (page header)

    private final By pageComponent      = By.cssSelector("app-project-edit");
    private final By pageHeading        = By.cssSelector("h1.ax-page-title");

    private final By projectNameField   = By.cssSelector("input[formcontrolname='projectName']");
    private final By descriptionField   = By.cssSelector("textarea[formcontrolname='description']");
    private final By statusDropdown     = By.cssSelector("select[formcontrolname='status']");
    private final By domainDropdown     = By.cssSelector("select[formcontrolname='domain']");
    private final By pmDropdown         = By.cssSelector("select[formcontrolname='managerId']");

    // Save / Cancel are inside div.ax-form-foot (distinguishes Cancel from Back in page header)
    private final By saveChangesBtn     = By.cssSelector("div.ax-form-foot button.ax-btn.primary");
    private final By cancelBtn          = By.cssSelector("div.ax-form-foot button.ax-btn.ghost");

    // Delete button is in the page header with the 'danger' class — no confirmation dialog
    private final By deleteProjectBtn   = By.cssSelector("button.ax-btn.danger");

    // Back button in page header (same ghost class but outside form footer)
    private final By backBtn            = By.xpath("//button[contains(@class,'ax-btn') and contains(@class,'ghost') and normalize-space()='Back']");

    // Success message shown on the same page after saving:
    // <div class="ax-ok"><span class="mi">check_circle</span> Saved successfully.</div>
    private final By successMessage     = By.cssSelector("div.ax-ok");

    public EditProjectAdminPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("EditProjectAdminPage initialized");
    }

    // ── State ─────────────────────────────────────────────────────────────────

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(pageComponent),
                    ExpectedConditions.visibilityOfElementLocated(projectNameField)
            ));
            logger.info("Edit Project page is displayed");
            return true;
        } catch (Exception e) {
            logger.warn("Edit Project page not found: {}", e.getMessage());
            return false;
        }
    }

    /** Returns true if the project name field has a pre-populated value. */
    public boolean isPrePopulated() {
        try {
            String val = wait.until(ExpectedConditions.visibilityOfElementLocated(projectNameField))
                             .getAttribute("value");
            return val != null && !val.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns the current value shown in the project name field. */
    public String getProjectName() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(projectNameField))
                       .getAttribute("value");
        } catch (Exception e) {
            return "";
        }
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public EditProjectAdminPage updateProjectName(String name) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(projectNameField));
        el.clear();
        el.sendKeys(name);
        logger.info("Updated project name to: {}", name);
        return this;
    }

    public EditProjectAdminPage updateDescription(String desc) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionField));
            el.clear();
            el.sendKeys(desc);
            logger.info("Updated description");
        } catch (Exception e) {
            logger.warn("Description field not found");
        }
        return this;
    }

    public EditProjectAdminPage selectStatus(String status) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(statusDropdown)))
                    .selectByVisibleText(status);
            logger.info("Selected status: {}", status);
        } catch (Exception e) {
            logger.warn("Status dropdown issue: {}", e.getMessage());
        }
        return this;
    }

    public EditProjectAdminPage selectDomain(String domain) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(domainDropdown)))
                    .selectByVisibleText(domain);
            logger.info("Selected domain: {}", domain);
        } catch (Exception e) {
            logger.warn("Domain dropdown issue: {}", e.getMessage());
        }
        return this;
    }

    public EditProjectAdminPage reassignPM(String pmName) {
        try {
            Select sel = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(pmDropdown)));
            if (pmName == null || pmName.isEmpty()) {
                sel.selectByIndex(0);
            } else {
                sel.selectByVisibleText(pmName);
            }
            logger.info("Reassigned PM to: {}", pmName);
        } catch (Exception e) {
            logger.warn("PM dropdown issue: {}", e.getMessage());
        }
        return this;
    }

    /** Clicks Save Changes — stays on EditProjectAdminPage; check isSuccessMessageDisplayed(). */
    public EditProjectAdminPage clickSaveChanges() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(saveChangesBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Save Changes");
        } catch (Exception e) {
            logger.warn("Save Changes button not found: {}", e.getMessage());
        }
        return this;
    }

    /** Clicks Cancel (inside the form footer) and returns to the Manage Projects page. */
    public ManageProjectsPage clickCancel() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(cancelBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Cancel");
        } catch (Exception e) {
            logger.warn("Cancel button not found: {}", e.getMessage());
        }
        return new ManageProjectsPage(driver);
    }

    /** Clicks the Back button in the page header and returns to the Manage Projects page. */
    public ManageProjectsPage clickBack() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(backBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Back");
        } catch (Exception e) {
            logger.warn("Back button not found: {}", e.getMessage());
        }
        return new ManageProjectsPage(driver);
    }

    /**
     * Clicks Delete Project (no confirmation dialog — deletion is immediate).
     * Returns to the Manage Projects page.
     */
    public ManageProjectsPage clickDeleteProject() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(deleteProjectBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Delete Project — deletion is immediate (no confirmation dialog)");
        } catch (Exception e) {
            logger.warn("Delete Project button not found: {}", e.getMessage());
        }
        return new ManageProjectsPage(driver);
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
