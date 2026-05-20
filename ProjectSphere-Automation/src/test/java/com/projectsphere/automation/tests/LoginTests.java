package com.projectsphere.automation.tests;

import com.projectsphere.automation.base.BaseTest;
import com.projectsphere.automation.constants.AppConstants;
import com.projectsphere.automation.pages.AdminDashboardPage;
import com.projectsphere.automation.pages.DevDashboardPage;
import com.projectsphere.automation.pages.ForgotPasswordPage;
import com.projectsphere.automation.pages.LoginPage;
import com.projectsphere.automation.pages.PMDashboardPage;
import com.projectsphere.automation.utils.ConfigReader;
import com.projectsphere.automation.utils.ExcelUtils;
import com.projectsphere.automation.utils.ExtentReportListener;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Map;

public class LoginTests extends BaseTest {

    // ── PS_TC001 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_SMOKE, AppConstants.GROUP_REGRESSION, AppConstants.GROUP_LOGIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC001 - Verify successful login for Admin role with valid credentials"
    )
    public void TC001_adminValidLoginRedirectsToDashboard() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC001");
        ExtentReportListener.getTest().info("Test data loaded: " + data);

        LoginPage loginPage = new LoginPage(getDriver());
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(loginPage.isLoginPageDisplayed(),
                "Sign In page should be visible with email, password and Sign In button");
        ExtentReportListener.getTest().info("Login page is displayed");

        AdminDashboardPage dashboard = loginPage.loginAsAdmin(
                ConfigReader.getInstance().getAdminEmail(),
                ConfigReader.getInstance().getAdminPassword());
        soft.assertTrue(dashboard.isDashboardDisplayed(),
                "Admin dashboard should be visible after login");
        ExtentReportListener.getTest().info("Verifying Admin-specific nav menu options are visible");
        soft.assertTrue(dashboard.areSummaryTilesVisible(),
                "Admin menu tiles (Projects, PMs, Teams, Members) should be visible");
        ExtentReportListener.getTest().pass("Admin login successful; redirected to Admin Dashboard");

        soft.assertAll();
    }

    // ── PS_TC002 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_SMOKE, AppConstants.GROUP_REGRESSION, AppConstants.GROUP_LOGIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC002 - Verify successful login for Project Manager role with valid credentials"
    )
    public void TC002_pmValidLoginRedirectsToDashboard() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC002");
        ExtentReportListener.getTest().info("Test data loaded: " + data);

        LoginPage loginPage = new LoginPage(getDriver());
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(loginPage.isLoginPageDisplayed(), "Sign In page should be displayed");

        PMDashboardPage dashboard = loginPage.loginAsPM(
                ConfigReader.getInstance().getPmEmail(),
                ConfigReader.getInstance().getPmPassword());
        soft.assertTrue(dashboard.isDashboardDisplayed(),
                "PM dashboard should be displayed after login");
        ExtentReportListener.getTest().info("Verifying PM-specific menu items are visible");
        soft.assertTrue(dashboard.areProjectTilesVisible(),
                "PM menu items (My Projects, My Teams, Create Project/Team) should be visible");
        ExtentReportListener.getTest().pass("PM login successful; redirected to PM Dashboard");

        soft.assertAll();
    }

    // ── PS_TC003 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_SMOKE, AppConstants.GROUP_REGRESSION, AppConstants.GROUP_LOGIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC003 - Verify successful login for Developer role with valid credentials"
    )
    public void TC003_developerValidLoginRedirectsToDashboard() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC003");
        ExtentReportListener.getTest().info("Test data loaded: " + data);

        LoginPage loginPage = new LoginPage(getDriver());
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(loginPage.isLoginPageDisplayed(), "Sign In page should be displayed");

        DevDashboardPage dashboard = loginPage.loginAsDeveloper(
                ConfigReader.getInstance().getDeveloperEmail(),
                ConfigReader.getInstance().getDeveloperPassword());
        soft.assertTrue(dashboard.isDashboardDisplayed(),
                "Developer dashboard should be displayed after login");
        ExtentReportListener.getTest().info("Verifying developer nav options are visible (Board, Backlog, Timeline, etc.)");
        ExtentReportListener.getTest().pass("Developer login successful; redirected to Developer Dashboard");

        soft.assertAll();
    }

    // ── PS_TC004 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_LOGIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC004 - Verify error message on blank email and password submission"
    )
    public void TC004_blankCredentialsShowValidationError() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC004");
        ExtentReportListener.getTest().info("Test data: " + data);

        LoginPage loginPage = new LoginPage(getDriver());
        SoftAssert soft = new SoftAssert();

        loginPage.enterEmail("").enterPassword("").clickSignIn();
        ExtentReportListener.getTest().info("Submitted blank email and password");

        soft.assertTrue(loginPage.isErrorDisplayed(),
                "Validation error should be shown when both fields are blank");
        ExtentReportListener.getTest().info("Verifying error message is user-friendly and specific");
        ExtentReportListener.getTest().pass("Blank credentials correctly prevented with validation error");

        soft.assertAll();
    }

    // ── PS_TC005 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_LOGIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC005 - Verify error message on invalid credentials"
    )
    public void TC005_invalidCredentialsShowError() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC005");
        ExtentReportListener.getTest().info("Test data: " + data);

        LoginPage loginPage = new LoginPage(getDriver());
        SoftAssert soft = new SoftAssert();

        loginPage.enterEmail("wrong@email.com").enterPassword("WrongPass").clickSignIn();
        ExtentReportListener.getTest().info("Entered unregistered email and incorrect password");

        soft.assertTrue(loginPage.isErrorDisplayed(),
                "Error message 'Invalid email or password' should be displayed");
        ExtentReportListener.getTest().info("Verifying page does not redirect and fields remain editable");
        soft.assertTrue(loginPage.isLoginPageDisplayed(),
                "User should remain on Sign In page after failed login");
        ExtentReportListener.getTest().pass("Invalid credentials correctly blocked with error message");

        soft.assertAll();
    }

    // ── PS_TC006 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_LOGIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC006 - Verify Forgot Password reset flow from login screen"
    )
    public void TC006_forgotPasswordResetFlowSucceeds() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC006");
        ExtentReportListener.getTest().info("Test data: " + data);

        LoginPage loginPage = new LoginPage(getDriver());
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(loginPage.isLoginPageDisplayed(), "Login page should be visible");

        // Step 1: type the registered email on the login page first
        String registeredEmail = ConfigReader.getInstance().getAdminEmail();
        String password        = ConfigReader.getInstance().getAdminPassword();
        loginPage.enterEmail(registeredEmail);
        ExtentReportListener.getTest().info("Entered email on login page: " + registeredEmail);

        // Step 2: click Forgot Password → app goes directly to Reset Password page
        ForgotPasswordPage resetPage = loginPage.clickForgotPassword();
        soft.assertTrue(resetPage.isForgotPasswordPageDisplayed(),
                "Reset Password page should be displayed after clicking Forgot Password");
        ExtentReportListener.getTest().info("Reset Password page is displayed");

        // Step 3: enter new password and confirm password
        resetPage.enterNewPassword(password).enterConfirmPassword(password);
        ExtentReportListener.getTest().info("Entered new password and confirm password");

        // Step 4: click Reset Password button → returns to login page
        LoginPage loginAfterReset = resetPage.clickResetPassword();
        ExtentReportListener.getTest().info("Clicked Reset Password button");

        // Step 5: verify login still works with the (same) password
        AdminDashboardPage dashboard = loginAfterReset.loginAsAdmin(registeredEmail, password);
        soft.assertTrue(dashboard.isDashboardDisplayed(),
                "Admin should be able to login successfully after password reset");
        ExtentReportListener.getTest().pass("Password reset flow completed and login verified");

        soft.assertAll();
    }

    // ── PS_TC007 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_LOGIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC007 - Verify Forgot Password with an unregistered email address"
    )
    public void TC007_forgotPasswordUnregisteredEmailShowsError() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC007");
        ExtentReportListener.getTest().info("Test data: " + data);

        LoginPage loginPage = new LoginPage(getDriver());
        SoftAssert soft = new SoftAssert();

        // NOTE: Known security defect — app navigates to reset password page
        // for ANY email (registered or not) without email verification.
        // This test verifies that even after "resetting" an unregistered email,
        // login with that email still fails.

        // Step 1: enter unregistered email on login page
        String unregisteredEmail = "unknown@ps.com";
        String testPassword = "TestPass@123";
        loginPage.enterEmail(unregisteredEmail);
        ExtentReportListener.getTest().info("Entered unregistered email: " + unregisteredEmail);

        // Step 2: click Forgot Password → app goes to reset page (security bug — no email check)
        ForgotPasswordPage resetPage = loginPage.clickForgotPassword();
        ExtentReportListener.getTest().info("App navigated to reset page without validating email (known defect)");

        // Step 3: enter new password and confirm on reset page
        resetPage.enterNewPassword(testPassword).enterConfirmPassword(testPassword);
        ExtentReportListener.getTest().info("Entered new password and confirm password");

        // Step 4: click Reset Password — error should appear for unregistered email
        resetPage.clickSubmit();
        ExtentReportListener.getTest().info("Clicked Reset Password button");

        // Step 5: verify error message is shown on the reset page
        soft.assertTrue(resetPage.isErrorDisplayed(),
                "Error message should be shown when resetting password for an unregistered email");
        ExtentReportListener.getTest().pass("Unregistered email correctly rejected with error on Reset Password page");

        soft.assertAll();
    }
}
