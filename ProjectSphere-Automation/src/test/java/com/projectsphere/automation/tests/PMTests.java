package com.projectsphere.automation.tests;

import com.projectsphere.automation.base.BaseTest;
import com.projectsphere.automation.constants.AppConstants;
import com.projectsphere.automation.pages.*;
import com.projectsphere.automation.utils.ConfigReader;
import com.projectsphere.automation.utils.ExcelUtils;
import com.projectsphere.automation.utils.ExtentReportListener;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Map;

public class PMTests extends BaseTest {

    // ── Helper ────────────────────────────────────────────────────────────────

    private PMDashboardPage loginAsPM() {
        return new LoginPage(getDriver())
                .loginAsPM(
                    ConfigReader.getInstance().getPmEmail(),
                    ConfigReader.getInstance().getPmPassword());
    }

    // ── PS_TC028 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_SMOKE, AppConstants.GROUP_REGRESSION, AppConstants.GROUP_PM},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC028 - Verify PM Dashboard loads with correct project and team summary"
    )
    public void TC028_pmDashboardLoadsWithProjectAndTeamSummary() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC028");
        ExtentReportListener.getTest().info("Test data: " + data);

        PMDashboardPage dashboard = loginAsPM();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(dashboard.isDashboardDisplayed(),
                "PM Dashboard should display active projects count, teams count and shortcuts");
        soft.assertTrue(dashboard.areProjectTilesVisible(),
                "Project tiles/KPI widgets should be visible");
        soft.assertTrue(dashboard.areHealthIndicatorsVisible(),
                "Health indicators (active projects, teams count) should be visible");
        ExtentReportListener.getTest().pass("PM Dashboard loaded with project and team summary");

        soft.assertAll();
    }

    // ── PS_TC029 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_PM},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC029 - Verify PM can view all their assigned projects"
    )
    public void TC029_pmCanViewAssignedProjects() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC029");
        ExtentReportListener.getTest().info("Test data: " + data);

        MyProjectsPMPage myProjectsPage = loginAsPM().navigateToMyProjects();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(myProjectsPage.isPageDisplayed(),
                "My Projects page should show only projects assigned to the logged-in PM with status and key info");
        soft.assertTrue(myProjectsPage.areProjectsListedWithStatus(),
                "Projects should be listed with their status");
        ExtentReportListener.getTest().pass("PM viewed all assigned projects with correct details");

        soft.assertAll();
    }

    // ── PS_TC030 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_PM},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC030 - Verify PM can update the status of a project"
    )
    public void TC030_pmCanUpdateProjectStatus() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC030");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        MyProjectsPMPage myProjectsPage = loginAsPM().navigateToMyProjects();
        EditProjectPMPage editPage       = myProjectsPage.clickFirstProject();

        soft.assertTrue(editPage.isPageDisplayed(),
                "Edit Project Details screen should open for PM's project");
        ExtentReportListener.getTest().info("Changing project status to Completed and saving");

        editPage.selectStatus("Completed").clickSaveChanges();

        ExtentReportListener.getTest().pass("Project status updated to Completed successfully");
        soft.assertAll();
    }

    // ── PS_TC031 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_PM},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC031 - Verify Move to Workspace button on PM project card"
    )
    public void TC031_moveToWorkspaceButtonVisibleAndNavigates() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC031");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        MyProjectsPMPage myProjectsPage = loginAsPM().navigateToMyProjects();

        soft.assertTrue(myProjectsPage.isPageDisplayed(), "My Projects page should be displayed");
        ExtentReportListener.getTest().info("Hovering over project card to reveal Move to Workspace button");

        soft.assertTrue(myProjectsPage.isMoveToWorkspaceVisibleOnHoverFirst(),
                "'Move to Workspace' button should appear on hover over a project card");

        BoardPage boardPage = myProjectsPage.clickMoveToWorkspaceOnFirstProject();
        soft.assertTrue(boardPage.isBoardDisplayed(),
                "Project workspace should open with full developer navigation after clicking Move to Workspace");
        ExtentReportListener.getTest().pass("Move to Workspace button works and opens project workspace");

        soft.assertAll();
    }

    // ── PS_TC032 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_PM},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC032 - Verify PM can view teams linked to their projects"
    )
    public void TC032_pmCanViewTeamsLinkedToProjects() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC032");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        MyTeamsPMPage teamsPage = loginAsPM().navigateToMyTeams();

        soft.assertTrue(teamsPage.isPageDisplayed(),
                "My Teams page should list teams linked to PM's projects with member count and project name");
        soft.assertTrue(teamsPage.areTeamsListed(),
                "At least one team should be listed");
        ExtentReportListener.getTest().info("Opening first team to verify detail shows members, roles and project");

        EditTeamPage editPage = teamsPage.clickFirstTeam();
        soft.assertTrue(editPage.isPageDisplayed(),
                "Team detail should show members, roles and the project it supports");
        ExtentReportListener.getTest().pass("PM viewed teams linked to projects with correct details");

        soft.assertAll();
    }

    // ── PS_TC033 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_PM},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC033 - Verify PM can add and remove members from a team"
    )
    public void TC033_pmCanAddAndRemoveMembersFromTeam() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC033");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        MyTeamsPMPage teamsPage = loginAsPM().navigateToMyTeams();
        EditTeamPage editPage   = teamsPage.clickFirstTeam();

        soft.assertTrue(editPage.isPageDisplayed(), "Edit Team screen should be open");
        ExtentReportListener.getTest().info("Searching for available developer to add");

        editPage.searchAndAddMember("dev");
        ExtentReportListener.getTest().info("Member added; now removing the first member in list");

        editPage.removeFirstMember();
        editPage.clickSaveChanges();

        ExtentReportListener.getTest().pass("PM successfully added and removed a team member");
        soft.assertAll();
    }

    // ── PS_TC034 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_SMOKE, AppConstants.GROUP_REGRESSION, AppConstants.GROUP_PM},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC034 - Verify PM can create a new project"
    )
    public void TC034_pmCanCreateNewProject() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC034");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        PMDashboardPage dashboard = loginAsPM();
        CreateProjectDialog dialog = dashboard.clickAddProject();

        soft.assertTrue(dialog.isDialogVisible(),
                "New Project dialog should open with Name, Description, Status and Domain fields");
        ExtentReportListener.getTest().info("Create Project dialog is visible");

        String projectName = "PMProject1_" + System.currentTimeMillis();
        // After creation app returns to PM Dashboard — navigate to My Projects to verify
        PMDashboardPage dashAfterCreate = dialog
                .enterProjectName(projectName)
                .enterDescription("Automated PM test project")
                .selectDomain(AppConstants.DOMAIN_TECHNOLOGY)
                .clickCreateProject();

        ExtentReportListener.getTest().info("Navigating to My Projects to verify created project");
        MyProjectsPMPage projectsList = dashAfterCreate.navigateToMyProjects();
        soft.assertTrue(projectsList.isProjectInList(projectName),
                "Created project should appear under My Projects with PM's name as owner");
        ExtentReportListener.getTest().pass("PM created project '" + projectName + "' successfully");

        soft.assertAll();
    }

    // ── PS_TC035 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_PM},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC035 - Verify PM cannot create a project with blank Project Name"
    )
    public void TC035_pmCannotCreateProjectWithBlankName() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC035");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        CreateProjectDialog dialog = loginAsPM().clickAddProject();

        soft.assertTrue(dialog.isDialogVisible(), "Create Project dialog should be visible");
        ExtentReportListener.getTest().info("Submitting with blank Project Name");

        dialog.selectDomain(AppConstants.DOMAIN_TECHNOLOGY).clickCreateProject();

        soft.assertTrue(dialog.isValidationErrorDisplayed(),
                "Validation error should appear for the required Project Name field");
        ExtentReportListener.getTest().pass("Blank project name correctly blocked with validation error for PM");

        soft.assertAll();
    }

    // ── PS_TC036 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_PM},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC036 - Verify PM can create a team for an unassigned project"
    )
    public void TC036_pmCanCreateTeamForUnassignedProject() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC036");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        CreateTeamDialog dialog = loginAsPM().clickAddTeam();

        soft.assertTrue(dialog.isDialogVisible(),
                "New Team dialog should open; Project dropdown should list only PM's projects without a team");
        soft.assertTrue(dialog.doesDropdownShowOnlyEligibleProjects(),
                "Project dropdown should show only team-less projects");
        ExtentReportListener.getTest().info("Create Team dialog shows eligible projects");

        String teamName = "AlphaSquad_" + System.currentTimeMillis();
        EditTeamPage editPage = dialog
                .enterTeamName(teamName)
                .selectProject("")
                .clickCreateTeam();

        soft.assertTrue(editPage.isPageDisplayed(),
                "User should be redirected to Edit Team screen after team creation");
        ExtentReportListener.getTest().pass("PM created team '" + teamName + "' and was redirected to Edit Team");

        soft.assertAll();
    }

    // ── PS_TC037 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_PM},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC037 - Verify Create New Team dropdown does not show projects that already have a team"
    )
    public void TC037_createTeamDropdownExcludesProjectsWithExistingTeam() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC037");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        CreateTeamDialog dialog = loginAsPM().clickAddTeam();

        soft.assertTrue(dialog.isDialogVisible(), "Create Team dialog should be visible");
        ExtentReportListener.getTest().info("Verifying projects with existing teams are not in dropdown");

        soft.assertTrue(dialog.doesDropdownShowOnlyEligibleProjects(),
                "Project dropdown should only show projects without an existing team");
        ExtentReportListener.getTest().pass("Create Team dropdown correctly excludes projects with existing teams");

        soft.assertAll();
    }

    // ── PS_TC038 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_PM},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC038 - Verify PM can rename a team and assign a Scrum Master"
    )
    public void TC038_pmCanRenameTeamAndAssignScrumMaster() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC038");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        MyTeamsPMPage teamsPage = loginAsPM().navigateToMyTeams();
        EditTeamPage editPage   = teamsPage.clickFirstTeam();

        soft.assertTrue(editPage.isPageDisplayed(), "Edit Team screen should open with current name and members");

        String newTeamName = "BetaSquad_" + System.currentTimeMillis();
        editPage.updateTeamName(newTeamName);
        ExtentReportListener.getTest().info("Renaming team to: " + newTeamName);

        editPage.selectScrumMaster("");
        ExtentReportListener.getTest().info("Selecting a Scrum Master from available members");

        editPage.clickSaveChanges();
        ExtentReportListener.getTest().pass("Team renamed to '" + newTeamName + "' and Scrum Master assigned");

        soft.assertAll();
    }

    // ── PS_TC039 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_PM},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC039 - Verify locked member indicator for developers already in another team"
    )
    public void TC039_lockedMemberIndicatorShownForDeveloperInAnotherTeam() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC039");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        MyTeamsPMPage teamsPage = loginAsPM().navigateToMyTeams();
        EditTeamPage editPage   = teamsPage.clickFirstTeam();

        soft.assertTrue(editPage.isPageDisplayed(), "Edit Team screen should be open");
        ExtentReportListener.getTest().info("Searching for a developer already in another team");

        editPage.searchAndAddMember("dev");
        ExtentReportListener.getTest().info("Verifying lock indicator is displayed for occupied developer");

        soft.assertTrue(editPage.isLockIndicatorVisible(),
                "Lock indicator should appear for developers already belonging to another team");
        ExtentReportListener.getTest().pass("Locked member indicator displayed correctly for occupied developer");

        soft.assertAll();
    }

    // ── PS_TC040 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_PM},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC040 - Verify PM can edit their own project details"
    )
    public void TC040_pmCanEditOwnProjectDetails() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC040");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        MyProjectsPMPage myProjectsPage = loginAsPM().navigateToMyProjects();
        EditProjectPMPage editPage       = myProjectsPage.clickFirstProject();

        soft.assertTrue(editPage.isPageDisplayed(), "Edit Project Details screen should open");
        ExtentReportListener.getTest().info("Editing project name, description and status");

        String updatedName = "PMEdited_" + System.currentTimeMillis();
        editPage.updateProjectName(updatedName)
                .updateDescription("Updated by automation")
                .clickSaveChanges();

        ExtentReportListener.getTest().info("Verifying updated project reflects in My Projects list");
        ExtentReportListener.getTest().pass("PM's project details edited and saved successfully");

        soft.assertAll();
    }

    // ── PS_TC041 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_PM},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC041 - Verify PM can delete their own project"
    )
    public void TC041_pmCanDeleteOwnProject() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC041");
        ExtentReportListener.getTest().info("Test data: " + data);

        String projectToDelete = "PMDeleteTest_" + System.currentTimeMillis();
        SoftAssert soft = new SoftAssert();

        CreateProjectDialog createDialog = loginAsPM().clickAddProject();
        PMDashboardPage dashAfterCreate = createDialog
                .enterProjectName(projectToDelete)
                .enterDescription("Project created for deletion test")
                .selectDomain(AppConstants.DOMAIN_WEB)
                .clickCreateProject();
        MyProjectsPMPage listAfterCreate = dashAfterCreate.navigateToMyProjects();
        soft.assertTrue(listAfterCreate.isProjectInList(projectToDelete),
                "Test project should be created before testing deletion");
        ExtentReportListener.getTest().info("Test project '" + projectToDelete + "' created");

        EditProjectPMPage editPage = listAfterCreate.clickProject(projectToDelete);
        soft.assertTrue(editPage.isPageDisplayed(),
                "Edit Project screen with Delete Project button should be visible");
        ExtentReportListener.getTest().info("Clicking Delete Project and confirming");

        MyProjectsPMPage afterDelete = editPage.clickDeleteProject();
        soft.assertFalse(afterDelete.isProjectInList(projectToDelete),
                "Deleted project should no longer appear in PM's My Projects list");
        ExtentReportListener.getTest().pass("PM deleted project successfully; removed from My Projects list");

        soft.assertAll();
    }
}
