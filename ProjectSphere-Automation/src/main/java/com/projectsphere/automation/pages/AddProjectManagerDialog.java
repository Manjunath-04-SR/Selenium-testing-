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

public class AddProjectManagerDialog {

    private static final Logger logger = LoggerFactory.getLogger(AddProjectManagerDialog.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators — based on real DOM ─────────────────────────────────────────
    // <div class="ax-modal-backdrop">
    //   <div class="ax-modal wide">
    //     <header class="ax-modal-head">
    //       <div class="ax-modal-title">Add Project Manager</div>
    //       <button class="ax-modal-close">×</button>
    //     </header>
    //     <div class="ax-modal-body">
    //       <form class="ax-form">
    //         <input formcontrolname="employeeId"  type="number">
    //         <input formcontrolname="phoneNumber" type="tel"      placeholder="10-digit number">
    //         <input formcontrolname="firstName"   type="text">
    //         <input formcontrolname="lastName"    type="text">
    //         <input formcontrolname="email"       type="email">
    //         <input formcontrolname="password"    type="password">
    //       </form>
    //     </div>
    //     <footer class="ax-modal-foot">
    //       <button class="ax-btn ghost">Close</button>
    //       <button class="ax-btn primary"> Register Manager </button>
    //     </footer>
    //   </div>
    // </div>

    // All locators scoped inside ax-modal-backdrop to avoid matching hidden page elements
    private final By dialogContainer  = By.cssSelector("div.ax-modal-backdrop");
    private final By empIdField       = By.cssSelector("div.ax-modal-backdrop input[formcontrolname='employeeId']");
    private final By phoneField       = By.cssSelector("div.ax-modal-backdrop input[formcontrolname='phoneNumber']");
    private final By firstNameField   = By.cssSelector("div.ax-modal-backdrop input[formcontrolname='firstName']");
    private final By lastNameField    = By.cssSelector("div.ax-modal-backdrop input[formcontrolname='lastName']");
    private final By emailField       = By.cssSelector("div.ax-modal-backdrop input[formcontrolname='email']");
    private final By passwordField    = By.cssSelector("div.ax-modal-backdrop input[formcontrolname='password']");

    // "Register Manager" submit button
    private final By registerBtn      = By.cssSelector("div.ax-modal-backdrop footer.ax-modal-foot button.ax-btn.primary");

    // "Close" button in footer
    private final By closeBtn         = By.cssSelector("div.ax-modal-backdrop footer.ax-modal-foot button.ax-btn.ghost");

    // X button in header
    private final By xCloseBtn        = By.cssSelector("div.ax-modal-backdrop button.ax-modal-close");

    // Validation error: <small class="ax-field-err"> or <div class="ax-err"> (duplicate email)
    private final By validationError  = By.cssSelector("div.ax-modal-backdrop small.ax-field-err, div.ax-modal-backdrop div.ax-err");

    public AddProjectManagerDialog(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("AddProjectManagerDialog initialized");
    }

    public boolean isDialogVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(dialogContainer));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public AddProjectManagerDialog enterEmployeeId(String empId) {
        type(empIdField, empId, "Employee ID");
        return this;
    }

    public AddProjectManagerDialog enterPhone(String phone) {
        type(phoneField, phone, "Phone Number");
        return this;
    }

    public AddProjectManagerDialog enterFirstName(String firstName) {
        type(firstNameField, firstName, "First Name");
        return this;
    }

    public AddProjectManagerDialog enterLastName(String lastName) {
        type(lastNameField, lastName, "Last Name");
        return this;
    }

    public AddProjectManagerDialog enterEmail(String email) {
        type(emailField, email, "Email");
        return this;
    }

    public AddProjectManagerDialog enterPassword(String password) {
        type(passwordField, password, "Password");
        return this;
    }

    /** Clicks "Register Manager" and returns to the Manage PMs page. */
    public ManageProjectManagersPage clickSave() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(registerBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Register Manager button");
        } catch (Exception e) {
            logger.warn("Register Manager button not found: {}", e.getMessage());
        }
        return new ManageProjectManagersPage(driver);
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
