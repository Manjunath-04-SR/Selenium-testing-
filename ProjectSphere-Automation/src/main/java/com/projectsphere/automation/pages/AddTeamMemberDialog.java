package com.projectsphere.automation.pages;

import com.projectsphere.automation.constants.AppConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class AddTeamMemberDialog {

    private static final Logger logger = LoggerFactory.getLogger(AddTeamMemberDialog.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators — based on real DOM ─────────────────────────────────────────
    // <div class="ax-modal-backdrop">
    //   <div class="ax-modal wide">
    //     <header class="ax-modal-head">Add Team Member</header>
    //     <div class="ax-modal-body">
    //       <form class="ax-form">
    //         <input formcontrolname="employeeId"  type="number">
    //         <input formcontrolname="phoneNumber" type="tel"      maxlength="10">
    //         <input formcontrolname="firstName"   type="text">
    //         <input formcontrolname="lastName"    type="text">
    //         <input formcontrolname="email"       type="email">
    //         <input formcontrolname="password"    type="password">
    //       </form>
    //     </div>
    //     <footer class="ax-modal-foot">
    //       <button class="ax-btn ghost">Close</button>
    //       <button class="ax-btn primary"> Register Member </button>
    //     </footer>
    //   </div>
    // </div>

    private final By dialogContainer = By.cssSelector("div.ax-modal-backdrop");
    private final By empIdField      = By.cssSelector("div.ax-modal-backdrop input[formcontrolname='employeeId']");
    private final By phoneField      = By.cssSelector("div.ax-modal-backdrop input[formcontrolname='phoneNumber']");
    private final By firstNameField  = By.cssSelector("div.ax-modal-backdrop input[formcontrolname='firstName']");
    private final By lastNameField   = By.cssSelector("div.ax-modal-backdrop input[formcontrolname='lastName']");
    private final By emailField      = By.cssSelector("div.ax-modal-backdrop input[formcontrolname='email']");
    private final By passwordField   = By.cssSelector("div.ax-modal-backdrop input[formcontrolname='password']");
    // "Bulk Actions" inline link that switches to file upload mode
    private final By bulkActionsLink = By.cssSelector("div.ax-modal-backdrop a.ax-modal-inline-link");
    private final By fileInput       = By.cssSelector("div.ax-modal-backdrop input[type='file']");
    // Success / error after bulk upload
    private final By bulkResult      = By.cssSelector("div.ax-modal-backdrop div.ax-ok, div.ax-modal-backdrop div.ax-err, div.ax-modal-backdrop div.ax-alert");

    // "Register Member" submit button (manual entry — in footer)
    private final By registerBtn      = By.cssSelector("div.ax-modal-backdrop footer.ax-modal-foot button.ax-btn.primary");
    // "Register" button in Bulk Actions modal — inside div.ax-upload-row (NOT the footer)
    private final By bulkRegisterBtn  = By.cssSelector("div.ax-modal-backdrop div.ax-upload-row button.ax-btn.primary");
    // "Close" button
    private final By closeBtn         = By.cssSelector("div.ax-modal-backdrop footer.ax-modal-foot button.ax-btn.ghost");
    // Validation / server error
    private final By validationError  = By.cssSelector("div.ax-modal-backdrop small.ax-field-err, div.ax-modal-backdrop div.ax-err");
    // Bulk upload result — success (div.ax-ok) or error (div.ax-err / any red feedback element)
    private final By bulkUploadResult = By.cssSelector(
        "div.ax-modal-backdrop div.ax-ok, " +
        "div.ax-modal-backdrop div.ax-err, " +
        "div.ax-modal-backdrop .bulk-result, " +
        "div.ax-modal-backdrop .ax-alert, " +
        "div.ax-modal-backdrop p.ax-err, " +
        "div.ax-modal-backdrop span.ax-err");

    public AddTeamMemberDialog(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("AddTeamMemberDialog initialized");
    }

    public boolean isDialogVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(dialogContainer));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isBulkUploadOptionVisible() {
        try {
            return driver.findElement(fileInput).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isManualRegistrationVisible() {
        try {
            return driver.findElement(empIdField).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** No mode-selection cards in the real dialog — form is shown directly. */
    public AddTeamMemberDialog selectManualEntry() {
        logger.info("selectManualEntry: form shown directly, no mode card to click");
        return this;
    }

    public AddTeamMemberDialog enterEmployeeId(String empId) {
        type(empIdField, empId, "Employee ID");
        return this;
    }

    public AddTeamMemberDialog enterEmpId(String empId) {
        return enterEmployeeId(empId);
    }

    public AddTeamMemberDialog enterPhone(String phone) {
        type(phoneField, phone, "Phone Number");
        return this;
    }

    public AddTeamMemberDialog enterFirstName(String firstName) {
        type(firstNameField, firstName, "First Name");
        return this;
    }

    public AddTeamMemberDialog enterLastName(String lastName) {
        type(lastNameField, lastName, "Last Name");
        return this;
    }

    public AddTeamMemberDialog enterEmail(String email) {
        type(emailField, email, "Email");
        return this;
    }

    public AddTeamMemberDialog enterPassword(String password) {
        type(passwordField, password, "Password");
        return this;
    }

    /** Clicks the "Bulk Actions" link inside the dialog to switch to file-upload mode. */
    public AddTeamMemberDialog clickBulkActionsLink() {
        try {
            WebElement link = wait.until(ExpectedConditions.elementToBeClickable(bulkActionsLink));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
            logger.info("Clicked Bulk Actions link");
            // Wait for file input to appear after Angular renders the upload UI
            wait.until(ExpectedConditions.presenceOfElementLocated(fileInput));
        } catch (Exception e) {
            logger.warn("Bulk Actions link not found: {}", e.getMessage());
        }
        return this;
    }

    public AddTeamMemberDialog uploadFile(String filePath) {
        try {
            WebElement fileEl = wait.until(ExpectedConditions.presenceOfElementLocated(fileInput));
            fileEl.sendKeys(filePath);
            logger.info("Uploaded file: {}", filePath);
        } catch (Exception e) {
            logger.warn("File input not found: {}", e.getMessage());
        }
        return this;
    }

    public void clickRegister() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(registerBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Register Member button");
        } catch (Exception e) {
            logger.warn("Register Member button not found: {}", e.getMessage());
        }
    }

    public void clickClose() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(closeBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Close button");
        } catch (Exception e) {
            logger.warn("Close button not found: {}", e.getMessage());
        }
    }

    /** Clicks the Register button inside the Bulk Actions upload row. */
    public void clickBulkRegister() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(bulkRegisterBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked bulk Register button");
        } catch (Exception e) {
            logger.warn("Bulk Register button not found: {}", e.getMessage());
        }
    }

    /** Waits up to 15s for ANY result (success or error) to appear after bulk register click. */
    public boolean waitForBulkResult() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOfElementLocated(bulkUploadResult));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Quick check (3s) — returns true only if success message (div.ax-ok) is visible. */
    public boolean isBulkSuccessDisplayed() {
        try {
            By successLocator = By.cssSelector("div.ax-modal-backdrop div.ax-ok");
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(ExpectedConditions.visibilityOfElementLocated(successLocator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isBulkResultDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(bulkUploadResult)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidationErrorDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(validationError)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private void type(By locator, String text, String fieldName) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            el.clear();
            el.sendKeys(text);
            logger.info("Entered {}: {}", fieldName, fieldName.equals("Password") ? "[hidden]" : text);
        } catch (Exception e) {
            logger.warn("Field '{}' not found: {}", fieldName, e.getMessage());
        }
    }
}
