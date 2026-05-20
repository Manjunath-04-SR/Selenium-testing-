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

public class EditProjectManagerPage {

    private static final Logger logger = LoggerFactory.getLogger(EditProjectManagerPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators — based on real DOM ─────────────────────────────────────────
    // Clicking a PM card opens an INLINE edit form (NOT a modal):
    //   Container : <section class="ax-section">
    //   Title     : <h3 class="ax-section-title">Edit Project Manager Details</h3>
    //   Fields    : firstName, lastName, email, phoneNumber, isActive
    //   Footer    : <div class="ax-form-foot">
    //                 <button type="button" class="ax-btn ghost">Cancel</button>
    //                 <button type="submit" class="ax-btn primary">Save Changes</button>
    //               </div>
    //   Status dropdown: formcontrolname='isActive'
    //     option value="0: true"  → "Active"
    //     option value="1: false" → "Inactive"

    private final By pageSection      = By.cssSelector("section.ax-section");
    private final By firstNameField   = By.cssSelector("section.ax-section input[formcontrolname='firstName']");
    private final By lastNameField    = By.cssSelector("section.ax-section input[formcontrolname='lastName']");
    private final By emailField       = By.cssSelector("section.ax-section input[formcontrolname='email']");
    private final By phoneField       = By.cssSelector("section.ax-section input[formcontrolname='phoneNumber']");
    private final By statusDropdown   = By.cssSelector("section.ax-section select[formcontrolname='isActive']");

    // Save / Cancel buttons inside the form footer
    private final By saveBtn          = By.cssSelector("section.ax-section div.ax-form-foot button.ax-btn.primary");
    private final By closeBtn         = By.cssSelector("section.ax-section div.ax-form-foot button.ax-btn.ghost");

    // Success message after save: <div class="ax-ok">Saved successfully.</div>
    private final By successMessage   = By.cssSelector("div.ax-ok");

    public EditProjectManagerPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("EditProjectManagerPage initialized");
    }

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(pageSection));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPrePopulated() {
        try {
            String val = wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameField))
                             .getAttribute("value");
            return val != null && !val.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public EditProjectManagerPage updateFirstName(String name) {
        type(firstNameField, name, "First Name");
        return this;
    }

    public EditProjectManagerPage updateLastName(String name) {
        type(lastNameField, name, "Last Name");
        return this;
    }

    public EditProjectManagerPage updateEmail(String email) {
        type(emailField, email, "Email");
        return this;
    }

    public EditProjectManagerPage updatePhone(String phone) {
        type(phoneField, phone, "Phone Number");
        return this;
    }

    public EditProjectManagerPage setStatusInactive() {
        selectStatus("Inactive");
        return this;
    }

    public EditProjectManagerPage setStatusActive() {
        selectStatus("Active");
        return this;
    }

    private void selectStatus(String status) {
        try {
            Select sel = new Select(wait.until(
                    ExpectedConditions.visibilityOfElementLocated(statusDropdown)));
            // isActive dropdown: value="0: true" → Active, value="1: false" → Inactive
            if ("Active".equalsIgnoreCase(status)) {
                sel.selectByValue("0: true");
            } else {
                sel.selectByValue("1: false");
            }
            logger.info("Set status to: {}", status);
        } catch (Exception e) {
            logger.warn("Status dropdown issue: {}", e.getMessage());
        }
    }

    /** Clicks Save Changes — stays on the inline form; check isSuccessMessageDisplayed(). */
    public EditProjectManagerPage clickSaveChanges() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(saveBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Save Changes on Edit PM form");
        } catch (Exception e) {
            logger.warn("Save Changes button not found: {}", e.getMessage());
        }
        return this;
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage))
                       .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Clicks Cancel on the inline edit form and returns to the PM list. */
    public ManageProjectManagersPage clickClose() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(closeBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Cancel on Edit PM form");
        } catch (Exception e) {
            logger.warn("Cancel button not found: {}", e.getMessage());
        }
        return new ManageProjectManagersPage(driver);
    }

    private void type(By locator, String text, String fieldName) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            el.clear();
            el.sendKeys(text);
            logger.info("Updated {}: {}", fieldName, text);
        } catch (Exception e) {
            logger.warn("Field '{}' not found: {}", fieldName, e.getMessage());
        }
    }
}
