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

public class LoginPage {

    private static final Logger logger = LoggerFactory.getLogger(LoginPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators (exact — derived from inspected Angular HTML) ───────────────
    //
    // Angular uses formcontrolname (not name/id) for reactive form controls.
    // The login form structure:
    //   <input type="email"    formcontrolname="email"    placeholder="Enter your email">
    //   <input type="password" formcontrolname="password" placeholder="Enter your password">
    //   <button type="submit"  class="btn-sign">Sign In</button>
    //   <span class="forgot">Forgot password?</span>

    private final By emailField        = By.cssSelector("input[formcontrolname='email']");
    private final By passwordField     = By.cssSelector("input[formcontrolname='password']");
    private final By signInButton      = By.cssSelector("button.btn-sign");
    private final By forgotPasswordLink = By.cssSelector("span.forgot");

    // Error messages — covers both:
    //  a) Angular reactive-form inline validation (e.g. "Email is required.")
    //  b) Backend auth errors shown as a toast/banner (e.g. "User not found.")
    // We exclude inputs, forms, labels and any element whose text > 250 chars
    // (to avoid matching the entire login-box container).
    private final By errorMessage = By.xpath(
        "//*[" +
            "contains(@class,'error') or contains(@class,'Error') or " +
            "contains(@class,'alert') or contains(@class,'Alert') or " +
            "contains(@class,'danger') or contains(@class,'invalid') or " +
            "contains(@class,'toast') or contains(@class,'Toast') or " +
            "contains(@class,'msg') or contains(@class,'Msg') or " +
            "@role='alert'" +
        "]" +
        "[normalize-space()!='']" +
        "[not(self::input)]" +
        "[not(self::form)]" +
        "[not(self::label)]" +
        "[string-length(normalize-space()) < 250]"
    );

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("LoginPage initialized with explicit wait={}s", AppConstants.EXPLICIT_WAIT);
    }

    // ── Page-load helper ──────────────────────────────────────────────────────

    public LoginPage waitForPageLoad() {
        // BaseTest.setUp() already waited 90s for the email field to appear,
        // so here we just verify visibility with a short poll.
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
        } catch (Exception e) {
            logger.warn("waitForPageLoad: email field not visible — {}", e.getMessage());
        }
        logger.info("Login page ready. Current URL: {}", driver.getCurrentUrl());
        return this;
    }

    // ── Field finders ─────────────────────────────────────────────────────────

    private WebElement findEmailField() {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
        logger.debug("Email field located: placeholder='{}'", el.getAttribute("placeholder"));
        return el;
    }

    private WebElement findPasswordField() {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
        logger.debug("Password field located");
        return el;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public LoginPage enterEmail(String email) {
        WebElement el = findEmailField();
        el.clear();
        el.sendKeys(email);
        logger.info("Entered email: {}", email);
        return this;
    }

    public LoginPage enterPassword(String password) {
        WebElement el = findPasswordField();
        el.clear();
        el.sendKeys(password);
        logger.info("Entered password: [hidden]");
        return this;
    }

    public void clickSignIn() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(signInButton));
        btn.click();
        logger.info("Clicked Sign In button (button.btn-sign)");
    }

    public AdminDashboardPage loginAsAdmin(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickSignIn();
        logger.info("Attempting Admin login with: {}", email);
        return new AdminDashboardPage(driver);
    }

    public PMDashboardPage loginAsPM(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickSignIn();
        logger.info("Attempting PM login with: {}", email);
        return new PMDashboardPage(driver);
    }

    public DevDashboardPage loginAsDeveloper(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickSignIn();
        logger.info("Attempting Developer login with: {}", email);
        return new DevDashboardPage(driver);
    }

    public ForgotPasswordPage clickForgotPassword() {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(forgotPasswordLink));
        logger.info("Forgot password link: text='{}' tag='{}'", el.getText(), el.getTagName());
        el.click();
        logger.info("Clicked Forgot Password link");
        return new ForgotPasswordPage(driver);
    }

    public boolean isErrorDisplayed() {
        // Use a longer timeout here: Render free-tier auth API responses can take
        // up to 90-120 s (backend cold-start / rate limiting).  For instant
        // Angular client-side errors (empty field) the element appears in <1 s,
        // so the extra headroom costs nothing in those scenarios.
        WebDriverWait backendWait = new WebDriverWait(driver,
                Duration.ofSeconds(AppConstants.BACKEND_ERROR_WAIT));
        try {
            WebElement err = backendWait.until(
                    ExpectedConditions.visibilityOfElementLocated(errorMessage));
            logger.info("Error message displayed: '{}'", err.getText());
            return true;
        } catch (Exception e) {
            logger.warn("No error element found after {}s: {}",
                    AppConstants.BACKEND_ERROR_WAIT, e.getMessage());
            return false;
        }
    }

    public String getErrorMessage() {
        WebDriverWait backendWait = new WebDriverWait(driver,
                Duration.ofSeconds(AppConstants.BACKEND_ERROR_WAIT));
        try {
            return backendWait.until(
                    ExpectedConditions.visibilityOfElementLocated(errorMessage))
                    .getText().trim();
        } catch (Exception e) {
            logger.warn("Could not find error message element after {}s",
                    AppConstants.BACKEND_ERROR_WAIT);
            return "";
        }
    }

    public boolean isLoginPageDisplayed() {
        try {
            return wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.tagName("input")),
                    ExpectedConditions.visibilityOfElementLocated(signInButton)
            )) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEmailFieldVisible() {
        try {
            return findEmailField().isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPasswordFieldVisible() {
        try {
            return findPasswordField().isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSignInButtonVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(signInButton)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
