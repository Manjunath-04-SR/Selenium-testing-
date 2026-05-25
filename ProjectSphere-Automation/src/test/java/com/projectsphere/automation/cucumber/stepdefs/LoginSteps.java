package com.projectsphere.automation.cucumber.stepdefs;

import com.projectsphere.automation.cucumber.context.ScenarioContext;
import com.projectsphere.automation.pages.AdminDashboardPage;
import com.projectsphere.automation.pages.LoginPage;
import com.projectsphere.automation.pages.PMDashboardPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Step definitions for Login feature.
 * Reuses existing LoginPage, AdminDashboardPage, PMDashboardPage page objects.
 */
public class LoginSteps {

    private static final Logger logger = LoggerFactory.getLogger(LoginSteps.class);

    private final ScenarioContext context;
    private LoginPage loginPage;

    public LoginSteps(ScenarioContext context) {
        this.context = context;
    }

    // ── Given steps ───────────────────────────────────────────────────────────

    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        loginPage = new LoginPage(context.getDriver());
        loginPage.waitForPageLoad();
        logger.info("Login page loaded and ready");
    }

    @Given("the admin is logged in")
    public void theAdminIsLoggedIn() {
        loginPage = new LoginPage(context.getDriver());
        loginPage.waitForPageLoad();
        AdminDashboardPage dashboard = loginPage.loginAsAdmin(
                context.getConfig().getAdminEmail(),
                context.getConfig().getAdminPassword()
        );
        Assert.assertTrue(dashboard.isDashboardDisplayed(),
                "Admin dashboard should be visible after login");
        logger.info("Admin logged in successfully");
    }

    @Given("the PM is logged in")
    public void thePMIsLoggedIn() {
        loginPage = new LoginPage(context.getDriver());
        loginPage.waitForPageLoad();
        PMDashboardPage dashboard = loginPage.loginAsPM(
                context.getConfig().getPmEmail(),
                context.getConfig().getPmPassword()
        );
        Assert.assertTrue(dashboard.isDashboardDisplayed(),
                "PM dashboard should be visible after login");
        logger.info("PM logged in successfully");
    }

    // ── When steps ────────────────────────────────────────────────────────────

    @When("the admin enters valid credentials and clicks Sign In")
    public void theAdminEntersValidCredentialsAndClicksSignIn() {
        loginPage = new LoginPage(context.getDriver());
        loginPage.waitForPageLoad();
        loginPage.enterEmail(context.getConfig().getAdminEmail());
        loginPage.enterPassword(context.getConfig().getAdminPassword());
        loginPage.clickSignIn();
        logger.info("Admin credentials entered and Sign In clicked");
    }

    @When("the PM enters valid credentials and clicks Sign In")
    public void thePMEntersValidCredentialsAndClicksSignIn() {
        loginPage = new LoginPage(context.getDriver());
        loginPage.waitForPageLoad();
        loginPage.enterEmail(context.getConfig().getPmEmail());
        loginPage.enterPassword(context.getConfig().getPmPassword());
        loginPage.clickSignIn();
        logger.info("PM credentials entered and Sign In clicked");
    }

    @When("the user enters email {string} and password {string}")
    public void theUserEntersEmailAndPassword(String email, String password) {
        loginPage = new LoginPage(context.getDriver());
        loginPage.waitForPageLoad();
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        loginPage.clickSignIn();
        logger.info("Entered email: {} and clicked Sign In", email);
    }

    // ── Then steps ────────────────────────────────────────────────────────────

    @Then("the admin dashboard should be displayed")
    public void theAdminDashboardShouldBeDisplayed() {
        AdminDashboardPage dashboard = new AdminDashboardPage(context.getDriver());
        Assert.assertTrue(dashboard.isDashboardDisplayed(),
                "Admin dashboard should be displayed after successful login");
        logger.info("Admin dashboard verified");
    }

    @Then("the PM dashboard should be displayed")
    public void thePMDashboardShouldBeDisplayed() {
        PMDashboardPage dashboard = new PMDashboardPage(context.getDriver());
        Assert.assertTrue(dashboard.isDashboardDisplayed(),
                "PM dashboard should be displayed after successful login");
        logger.info("PM dashboard verified");
    }

    @Then("a login error message should be displayed")
    public void aLoginErrorMessageShouldBeDisplayed() {
        loginPage = new LoginPage(context.getDriver());
        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "An error message should appear for invalid credentials");
        logger.info("Login error message verified");
    }

    @Then("the login page should display email field, password field and Sign In button")
    public void theLoginPageShouldDisplayRequiredFields() {
        loginPage = new LoginPage(context.getDriver());
        Assert.assertTrue(loginPage.isEmailFieldVisible(),    "Email field should be visible");
        Assert.assertTrue(loginPage.isPasswordFieldVisible(), "Password field should be visible");
        Assert.assertTrue(loginPage.isSignInButtonVisible(),  "Sign In button should be visible");
        logger.info("All login page fields verified");
    }
}
