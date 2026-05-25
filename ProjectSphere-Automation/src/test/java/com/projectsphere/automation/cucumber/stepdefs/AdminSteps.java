package com.projectsphere.automation.cucumber.stepdefs;

import com.projectsphere.automation.cucumber.context.ScenarioContext;
import com.projectsphere.automation.pages.AdminDashboardPage;
import com.projectsphere.automation.pages.CreateTeamDialog;
import com.projectsphere.automation.pages.EditTeamPage;
import com.projectsphere.automation.pages.ManageTeamsAdminPage;
import com.projectsphere.automation.pages.ManageProjectsPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Step definitions for Admin Dashboard feature.
 * Reuses existing page objects — no page object code is duplicated.
 */
public class AdminSteps {

    private static final Logger logger = LoggerFactory.getLogger(AdminSteps.class);

    private final ScenarioContext context;

    public AdminSteps(ScenarioContext context) {
        this.context = context;
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @Then("the admin dashboard should show KPI tiles")
    public void theAdminDashboardShouldShowKpiTiles() {
        AdminDashboardPage dashboard = new AdminDashboardPage(context.getDriver());
        Assert.assertTrue(dashboard.isDashboardDisplayed(),
                "Admin dashboard KPI tiles should be visible");
        logger.info("Admin dashboard KPI tiles verified");
    }

    // ── Manage Projects ───────────────────────────────────────────────────────

    @When("the admin navigates to Manage Projects")
    public void theAdminNavigatesToManageProjects() {
        AdminDashboardPage dashboard = new AdminDashboardPage(context.getDriver());
        dashboard.navigateToProjects();
        logger.info("Navigated to Manage Projects");
    }

    @Then("the Manage Projects page should be displayed")
    public void theManageProjectsPageShouldBeDisplayed() {
        ManageProjectsPage page = new ManageProjectsPage(context.getDriver());
        Assert.assertTrue(page.isPageDisplayed(),
                "Manage Projects page should be displayed");
        logger.info("Manage Projects page verified");
    }

    // ── Manage Teams ──────────────────────────────────────────────────────────

    @When("the admin navigates to Manage Teams")
    public void theAdminNavigatesToManageTeams() {
        AdminDashboardPage dashboard = new AdminDashboardPage(context.getDriver());
        dashboard.navigateToTeams();
        logger.info("Navigated to Manage Teams");
    }

    @Then("the Manage Teams page should be displayed")
    public void theManageTeamsPageShouldBeDisplayed() {
        ManageTeamsAdminPage page = new ManageTeamsAdminPage(context.getDriver());
        Assert.assertTrue(page.isPageDisplayed(),
                "Manage Teams page should be displayed");
        logger.info("Manage Teams page verified");
    }

    @When("the admin clicks New Team button")
    public void theAdminClicksNewTeamButton() {
        // button.ax-btn.primary with text "New Team" on ManageTeamsAdminPage
        By newTeamBtn = By.cssSelector("button.ax-btn.primary");
        WebDriverWait wait = new WebDriverWait(context.getDriver(),
                Duration.ofSeconds(60));
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(newTeamBtn));
        ((JavascriptExecutor) context.getDriver()).executeScript("arguments[0].click();", btn);
        logger.info("Clicked New Team button");
    }

    @Then("the Create Team dialog should appear")
    public void theCreateTeamDialogShouldAppear() {
        CreateTeamDialog dialog = new CreateTeamDialog(context.getDriver());
        Assert.assertTrue(dialog.isDialogVisible(),
                "Create Team dialog should be visible");
        logger.info("Create Team dialog verified");
    }

    @Then("the dialog should show team name field and project dropdown")
    public void theDialogShouldShowTeamNameAndProjectDropdown() {
        CreateTeamDialog dialog = new CreateTeamDialog(context.getDriver());
        Assert.assertTrue(dialog.isTeamNameAndProjectDropdownVisible(),
                "Team name field and project dropdown should be visible");
        logger.info("Create Team dialog fields verified");
    }

    @When("the admin creates a team with name {string}")
    public void theAdminCreatesATeamWithName(String teamName) {
        CreateTeamDialog dialog = new CreateTeamDialog(context.getDriver());
        dialog.enterTeamName(teamName)
              .selectProject(null)
              .clickCreateTeam();
        logger.info("Created team with name: {}", teamName);
    }

    @Then("the Edit Team page should be displayed")
    public void theEditTeamPageShouldBeDisplayed() {
        EditTeamPage page = new EditTeamPage(context.getDriver());
        Assert.assertTrue(page.isPageDisplayed(),
                "Edit Team page should be displayed after team creation");
        logger.info("Edit Team page verified");
    }
}
