package com.projectsphere.automation.cucumber.stepdefs;

import com.projectsphere.automation.cucumber.context.ScenarioContext;
import com.projectsphere.automation.pages.CreateProjectDialog;
import com.projectsphere.automation.pages.MyProjectsPMPage;
import com.projectsphere.automation.pages.MyTeamsPMPage;
import com.projectsphere.automation.pages.PMDashboardPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Step definitions for PM Dashboard feature.
 * Reuses existing page objects — no page object code is duplicated.
 */
public class PMSteps {

    private static final Logger logger = LoggerFactory.getLogger(PMSteps.class);

    private final ScenarioContext context;

    public PMSteps(ScenarioContext context) {
        this.context = context;
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @Then("the PM dashboard should show quick action cards")
    public void thePMDashboardShouldShowQuickActionCards() {
        PMDashboardPage dashboard = new PMDashboardPage(context.getDriver());
        Assert.assertTrue(dashboard.isDashboardDisplayed(),
                "PM dashboard should be loaded with quick action cards");
        logger.info("PM dashboard quick action cards verified");
    }

    // ── My Projects ───────────────────────────────────────────────────────────

    @When("the PM navigates to My Projects")
    public void thePMNavigatesToMyProjects() {
        PMDashboardPage dashboard = new PMDashboardPage(context.getDriver());
        dashboard.navigateToMyProjects();
        logger.info("Navigated to My Projects");
    }

    @Then("the My Projects page should be displayed")
    public void theMyProjectsPageShouldBeDisplayed() {
        MyProjectsPMPage page = new MyProjectsPMPage(context.getDriver());
        Assert.assertTrue(page.isPageDisplayed(),
                "My Projects page should be visible");
        logger.info("My Projects page verified");
    }

    // ── My Teams ──────────────────────────────────────────────────────────────

    @When("the PM navigates to My Teams")
    public void thePMNavigatesToMyTeams() {
        PMDashboardPage dashboard = new PMDashboardPage(context.getDriver());
        dashboard.navigateToMyTeams();
        logger.info("Navigated to My Teams");
    }

    @Then("the My Teams page should be displayed")
    public void theMyTeamsPageShouldBeDisplayed() {
        MyTeamsPMPage page = new MyTeamsPMPage(context.getDriver());
        Assert.assertTrue(page.isPageDisplayed(),
                "My Teams page should be visible");
        logger.info("My Teams page verified");
    }

    // ── Create Project ────────────────────────────────────────────────────────

    @When("the PM clicks New Project button")
    public void thePMClicksNewProjectButton() {
        PMDashboardPage dashboard = new PMDashboardPage(context.getDriver());
        dashboard.clickAddProject();
        logger.info("Clicked New Project button");
    }

    @Then("the Create Project dialog should appear")
    public void theCreateProjectDialogShouldAppear() {
        CreateProjectDialog dialog = new CreateProjectDialog(context.getDriver());
        Assert.assertTrue(dialog.isDialogVisible(),
                "Create Project dialog should be visible");
        logger.info("Create Project dialog verified");
    }

    @When("the PM creates a project with name {string}")
    public void thePMCreatesAProjectWithName(String projectName) {
        CreateProjectDialog dialog = new CreateProjectDialog(context.getDriver());
        PMDashboardPage dashboardAfter = dialog
                .enterProjectName(projectName)
                .enterDescription("BDD Cucumber test project")
                .clickCreateProject();
        Assert.assertTrue(dashboardAfter.isDashboardDisplayed(),
                "PM dashboard should be visible after project creation");
        logger.info("Created project: {}", projectName);
    }

    @Then("the project {string} should appear in My Projects list")
    public void theProjectShouldAppearInMyProjectsList(String projectName) {
        PMDashboardPage dashboard = new PMDashboardPage(context.getDriver());
        MyProjectsPMPage projectsPage = dashboard.navigateToMyProjects();
        Assert.assertTrue(projectsPage.isProjectInList(projectName),
                "Project '" + projectName + "' should appear in the My Projects list");
        logger.info("Project '{}' verified in My Projects list", projectName);
    }
}
