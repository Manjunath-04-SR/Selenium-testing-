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
import org.openqa.selenium.interactions.Actions;
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
        // navigateToTeams() clicks the KPI tile — we must wait for the page to render
        // before any following step tries to interact with ManageTeams elements
        ManageTeamsAdminPage teamsPage = dashboard.navigateToTeams();
        teamsPage.isPageDisplayed(); // blocks until app-teams-list / h1.ax-page-title visible
        logger.info("Navigated to Manage Teams — page fully loaded");
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
        WebDriverWait wait = new WebDriverWait(context.getDriver(), Duration.ofSeconds(60));

        // Target by text "New Team" — specific, avoids other primary buttons
        By newTeamBtn = By.xpath(
            "//button[contains(@class,'ax-btn') and contains(@class,'primary')" +
            " and contains(normalize-space(),'New Team')]");

        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(newTeamBtn));

        // Scroll into view so it's not clipped by header/footer
        ((JavascriptExecutor) context.getDriver())
            .executeScript("arguments[0].scrollIntoView({block:'center'})", btn);

        // Strategy 1: dispatchEvent — fires a proper bubbling MouseEvent that Angular's
        // change detection can observe, even when native Selenium click is swallowed.
        ((JavascriptExecutor) context.getDriver()).executeScript(
            "arguments[0].dispatchEvent(" +
            "  new MouseEvent('click',{bubbles:true,cancelable:true,view:window})" +
            ")", btn);
        logger.info("Dispatched click on New Team button");

        // Modal detection — broad OR covers: ax-modal-backdrop, ax-modal (any variant),
        // or the teamName input itself (fallback when backdrop class differs in live build)
        boolean modalOpened = false;
        try {
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.ax-modal-backdrop")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.ax-modal")),
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[formcontrolname='teamName']"))
            ));
            modalOpened = true;
            logger.info("New Team modal detected after dispatchEvent click");
        } catch (Exception firstAttempt) {
            logger.warn("Modal not detected after dispatchEvent — retrying with Actions click");
        }

        if (!modalOpened) {
            // Strategy 2: Actions click — moves mouse to element centre and clicks,
            // triggering native browser events that Angular's zone.js will pick up.
            WebElement btnRetry = wait.until(ExpectedConditions.elementToBeClickable(newTeamBtn));
            ((JavascriptExecutor) context.getDriver())
                .executeScript("arguments[0].scrollIntoView({block:'center'})", btnRetry);
            try {
                new Actions(context.getDriver()).moveToElement(btnRetry).click().perform();
            } catch (Exception e) {
                btnRetry.click();
            }
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.ax-modal-backdrop")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.ax-modal")),
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[formcontrolname='teamName']"))
            ));
            logger.info("New Team modal confirmed open after Actions retry");
        }
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
