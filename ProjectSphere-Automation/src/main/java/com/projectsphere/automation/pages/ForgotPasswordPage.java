package com.projectsphere.automation.pages;

import com.projectsphere.automation.constants.AppConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class ForgotPasswordPage {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators — based on real DOM ──────────────────────────────────────────
    // After clicking Forgot Password on login page (with email pre-filled),
    // the app jumps directly to the Reset Password form:
    //   <h2>Reset password</h2>
    //   <p>Set a new password for <strong>admin@projectsphere.com</strong></p>
    //   <input formcontrolname="newPassword"     type="password" placeholder="Enter new password">
    //   <input formcontrolname="confirmPassword" type="password" placeholder="Re-enter new password">
    //   <button type="submit" class="btn-sign">Reset Password</button>
    //   <span class="forgot"> Change email </span>  ← back link

    // Reset password page heading
    private final By resetHeading      = By.xpath("//h2[contains(normalize-space(),'Reset password')]");

    // New Password — formcontrolname is the reliable Angular attribute
    private final By newPasswordField  = By.cssSelector("input[formcontrolname='newPassword']");

    // Confirm Password — placeholder is "Re-enter new password" (NOT "confirm")
    private final By confirmPassField  = By.cssSelector("input[formcontrolname='confirmPassword']");

    // Reset Password submit button
    private final By submitBtn         = By.cssSelector("button.btn-sign[type='submit']");

    // "Change email" back link — it's a <span class="forgot">, not an <a>
    private final By backToLoginLink   = By.xpath(
        "//span[contains(@class,'forgot')] | " +
        "//a[contains(normalize-space(),'Login') or contains(normalize-space(),'Back')]");

    // Success toast shown after successful reset
    private final By successMessage    = By.cssSelector(
        ".ps-toast--success, [class*='toast--success'], .ps-toast-msg:not(:empty)");

    // Real DOM: <div class="login-error"><span class="mi">error_outline</span> No account found for that email </div>
    private final By errorMessage      = By.cssSelector("div.login-error");

    public ForgotPasswordPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("ForgotPasswordPage initialized");
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    /** Returns true when the Reset Password page is shown (heading visible). */
    public boolean isForgotPasswordPageDisplayed() {
        try {
            return wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(resetHeading),
                    ExpectedConditions.visibilityOfElementLocated(newPasswordField)
            )) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public ForgotPasswordPage enterNewPassword(String password) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(newPasswordField));
        el.clear();
        el.sendKeys(password);
        logger.info("Entered new password");
        return this;
    }

    public ForgotPasswordPage enterConfirmPassword(String password) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(confirmPassField));
        el.clear();
        el.sendKeys(password);
        logger.info("Entered confirm password");
        return this;
    }

    /** Clicks the Reset Password submit button and returns LoginPage. */
    public LoginPage clickResetPassword() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
        btn.click();
        logger.info("Clicked Reset Password button");
        return new LoginPage(driver);
    }

    /** Kept for backward compatibility. */
    public void clickSubmit() {
        wait.until(ExpectedConditions.elementToBeClickable(submitBtn)).click();
        logger.info("Clicked submit on reset password page");
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isErrorDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public LoginPage navigateBackToLogin() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(backToLoginLink));
        link.click();
        logger.info("Navigated back to Login page");
        return new LoginPage(driver);
    }
}
