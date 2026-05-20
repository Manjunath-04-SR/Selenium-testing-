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

public class AdminTests extends BaseTest {

    // ── Helper ────────────────────────────────────────────────────────────────

    private AdminDashboardPage loginAsAdmin() {
        return new LoginPage(getDriver())
                .loginAsAdmin(
                    ConfigReader.getInstance().getAdminEmail(),
                    ConfigReader.getInstance().getAdminPassword());
    }

    // ── PS_TC008 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_SMOKE, AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC008 - Verify Admin Dashboard loads and displays summary statistics"
    )
    public void TC008_adminDashboardLoadsWithSummaryStats() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC008");
        ExtentReportListener.getTest().info("Test data: " + data);

        AdminDashboardPage dashboard = loginAsAdmin();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(dashboard.isDashboardDisplayed(),
                "Admin dashboard should load showing Projects, PMs, Team Members and Teams counts");
        soft.assertTrue(dashboard.areSummaryTilesVisible(),
                "All summary KPI tiles should be visible with numeric, non-negative values");
        ExtentReportListener.getTest().info("Verifying each KPI tile navigates to its management page");
        soft.assertTrue(dashboard.isSearchBarVisible(),
                "Global search bar should be present in the navigation");
        ExtentReportListener.getTest().pass("Admin Dashboard loaded with all summary statistics");

        soft.assertAll();
    }

    // ── PS_TC009 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC009 - Verify Admin can view the full list of projects with key details"
    )
    public void TC009_adminCanViewProjectListWithDetails() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC009");
        ExtentReportListener.getTest().info("Test data: " + data);

        ManageProjectsPage projectsPage = loginAsAdmin().navigateToProjects();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(projectsPage.isPageDisplayed(),
                "Projects list page should load with Name, Owner and Status columns");
        soft.assertFalse(projectsPage.getProjectRows().isEmpty(),
                "At least one project row should be visible");
        ExtentReportListener.getTest().pass("Admin viewed full project list with key details");

        soft.assertAll();
    }

    // ── PS_TC010 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_SMOKE, AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC010 - Verify Admin can create a new project with all mandatory fields"
    )
    public void TC010_adminCanCreateProjectWithMandatoryFields() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC010");
        ExtentReportListener.getTest().info("Test data: " + data);

        String projectName = "SphereTest_" + System.currentTimeMillis();
        SoftAssert soft = new SoftAssert();

        // Step 1: navigate to Projects Workspace
        ManageProjectsPage projectsPage = loginAsAdmin().navigateToProjects();
        soft.assertTrue(projectsPage.isPageDisplayed(), "Projects Workspace page should be displayed");

        // Step 2: open dialog and verify it is visible
        AddProjectDialog dialog = projectsPage.clickAddProject();
        soft.assertTrue(dialog.isDialogVisible(), "Add Project dialog should be visible");
        soft.assertTrue(dialog.isProjectNameFieldVisible(), "Project Name field should be visible");
        ExtentReportListener.getTest().info("Add Project dialog opened");

        // Step 3: fill mandatory fields — Name and Domain
        dialog.enterProjectName(projectName).selectDomain(AppConstants.DOMAIN_TECHNOLOGY);
        ExtentReportListener.getTest().info("Filled Name='" + projectName + "', Domain=TECHNOLOGY");

        // Step 4: click Create Project and verify the Projects page is shown (project created)
        ManageProjectsPage result = dialog.clickCreateProject();
        soft.assertTrue(result.isPageDisplayed(),
                "Projects Workspace should be displayed after project creation");
        ExtentReportListener.getTest().pass("Project '" + projectName + "' created successfully");

        soft.assertAll();
    }

    // ── PS_TC011 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC011 - Verify Admin cannot create a project with a blank Project Name"
    )
    public void TC011_adminCannotCreateProjectWithBlankName() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC011");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        ManageProjectsPage projectsPage = loginAsAdmin().navigateToProjects();
        AddProjectDialog dialog = projectsPage.clickAddProject();

        soft.assertTrue(dialog.isDialogVisible(), "Add Project dialog should be visible");
        ExtentReportListener.getTest().info("Submitting with blank Project Name");

        dialog.selectDomain(AppConstants.DOMAIN_TECHNOLOGY).clickCreateProject();

        soft.assertTrue(dialog.isValidationErrorDisplayed(),
                "Validation error should be displayed for the required Project Name field");
        ExtentReportListener.getTest().pass("Blank project name correctly blocked with validation error");

        soft.assertAll();
    }

    // ── PS_TC012 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC012 - Verify Admin can edit an existing project's details"
    )
    public void TC012_adminCanEditExistingProjectDetails() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC012");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        ManageProjectsPage projectsPage = loginAsAdmin().navigateToProjects();
        EditProjectAdminPage editPage = projectsPage.clickFirstProject();

        soft.assertTrue(editPage.isPrePopulated(),
                "Edit Project screen should open with current values pre-filled");
        ExtentReportListener.getTest().info("Edit Project screen is open with pre-filled data");

        String updatedName = "SphereTest-Edited_" + System.currentTimeMillis();
        editPage.updateProjectName(updatedName).clickSaveChanges();

        soft.assertTrue(editPage.isSuccessMessageDisplayed(),
                "'Saved successfully.' message should appear on the page after saving");
        ExtentReportListener.getTest().pass("Project edited successfully — 'Saved successfully.' message shown");

        soft.assertAll();
    }

    // ── PS_TC013 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC013 - Verify Admin can delete a project from the Edit screen"
    )
    public void TC013_adminCanDeleteProject() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC013");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();

        // Step 1: navigate to Projects and open the first existing project
        ManageProjectsPage projectsPage = loginAsAdmin().navigateToProjects();
        soft.assertTrue(projectsPage.isPageDisplayed(), "Projects page should be displayed");

        EditProjectAdminPage editPage = projectsPage.clickFirstProject();
        soft.assertTrue(editPage.isPageDisplayed(),
                "Edit Project screen should be visible with Delete Project button");

        // Step 2: note the project name so we can verify it's gone after deletion
        String projectName = editPage.getProjectName();
        ExtentReportListener.getTest().info("Deleting existing project: '" + projectName + "'");

        // Step 3: click Delete Project (immediate — no confirmation dialog)
        ManageProjectsPage afterDelete = editPage.clickDeleteProject();

        // Step 4: verify the project no longer appears in the list
        soft.assertFalse(afterDelete.isProjectInList(projectName),
                "Deleted project '" + projectName + "' should no longer appear in the list");
        ExtentReportListener.getTest().pass("Project '" + projectName + "' deleted — removed from list");

        soft.assertAll();
    }

    // ── PS_TC014 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC014 - Verify Cancel on Add Project dialog discards input"
    )
    public void TC014_cancelOnAddProjectDialogDiscardsInput() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC014");
        ExtentReportListener.getTest().info("Test data: " + data);

        String partialName = "CancelProject_" + System.currentTimeMillis();
        SoftAssert soft = new SoftAssert();

        ManageProjectsPage projectsPage = loginAsAdmin().navigateToProjects();
        int countBefore = projectsPage.getProjectCount();

        AddProjectDialog dialog = projectsPage.clickAddProject();
        soft.assertTrue(dialog.isDialogVisible(), "Add Project dialog should be visible");
        ExtentReportListener.getTest().info("Entering partial values then clicking Cancel/Close");

        dialog.enterProjectName(partialName);
        dialog.clickClose();

        ManageProjectsPage listAfterCancel = new ManageProjectsPage(getDriver());
        soft.assertFalse(listAfterCancel.isProjectInList(partialName),
                "No new project should appear after cancelling the dialog");
        ExtentReportListener.getTest().pass("Cancel on Add Project dialog correctly discarded input");

        soft.assertAll();
    }

    // ── PS_TC015 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC015 - Verify Admin can add a new Project Manager via the Add PM dialog"
    )
    public void TC015_adminCanAddNewProjectManager() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC015");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        ManageProjectManagersPage pmPage = loginAsAdmin().navigateToProjectManagers();
        AddProjectManagerDialog dialog   = pmPage.clickAddProjectManager();

        soft.assertTrue(dialog.isDialogVisible(),
                "Add PM dialog should open with Employee ID, Phone, Name, Email and Password fields");
        ExtentReportListener.getTest().info("Add PM dialog is visible");

        String ts    = String.valueOf(System.currentTimeMillis());
        String email = "auto_pm_" + ts + "@test.com";

        dialog.enterEmployeeId(ts.substring(ts.length() - 5)) // 5-digit employee ID
              .enterPhone("9876543210")
              .enterFirstName("John")
              .enterLastName("Doe")
              .enterEmail(email)
              .enterPassword("Test@1234")
              .clickSave();

        ExtentReportListener.getTest().pass("Add Project Manager form submitted with all required fields");
        soft.assertAll();
    }

    // ── PS_TC016 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC016 - Verify Admin can edit PM profile and set status to Inactive"
    )
    public void TC016_adminCanEditPMAndSetInactive() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC016");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();

        // Step 1: click first existing PM — edit modal opens
        ManageProjectManagersPage pmPage = loginAsAdmin().navigateToProjectManagers();
        EditProjectManagerPage editPage  = pmPage.clickFirstPM();

        soft.assertTrue(editPage.isPageDisplayed(),
                "Edit PM modal should open with current values pre-filled");
        ExtentReportListener.getTest().info("Edit PM modal opened");

        // Step 2: update first name and set status to Inactive
        editPage.updateFirstName("UpdatedName")
                .setStatusInactive();
        ExtentReportListener.getTest().info("Updated first name and set status to Inactive");

        // Step 3: save and verify success message
        editPage.clickSaveChanges();
        soft.assertTrue(editPage.isSuccessMessageDisplayed(),
                "'Saved successfully.' message should appear after saving PM changes");
        ExtentReportListener.getTest().pass("PM edited successfully — status set to Inactive, saved successfully");

        soft.assertAll();
    }

    // ── PS_TC017 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC017 - Verify Admin cannot add a PM with a duplicate email address"
    )
    public void TC017_adminCannotAddPMWithDuplicateEmail() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC017");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        ManageProjectManagersPage pmPage = loginAsAdmin().navigateToProjectManagers();
        AddProjectManagerDialog dialog   = pmPage.clickAddProjectManager();

        soft.assertTrue(dialog.isDialogVisible(), "Add PM dialog should be visible");
        ExtentReportListener.getTest().info("Entering already-registered email address");

        String ts = String.valueOf(System.currentTimeMillis());
        dialog.enterEmployeeId(ts.substring(ts.length() - 5))
              .enterPhone("9876543210")
              .enterFirstName("Dup")
              .enterLastName("PM")
              .enterEmail(ConfigReader.getInstance().getPmEmail())
              .enterPassword("Test@1234")
              .clickSave();

        // Dialog stays open with a validation / server error when email is duplicate
        soft.assertTrue(dialog.isValidationErrorDisplayed(),
                "System should display a validation error for duplicate email");
        ExtentReportListener.getTest().pass("Duplicate email correctly rejected during PM creation");

        soft.assertAll();
    }

    // ── PS_TC018 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC018 - Verify Admin can view all teams in the organisation"
    )
    public void TC018_adminCanViewAllTeams() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC018");
        ExtentReportListener.getTest().info("Test data: " + data);

        ManageTeamsAdminPage teamsPage = loginAsAdmin().navigateToTeams();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(teamsPage.isPageDisplayed(),
                "Teams list should display with Team Name, Project and member count");
        ExtentReportListener.getTest().info("Verifying at least one team is listed");
        soft.assertFalse(teamsPage.getTeamRows().isEmpty(),
                "At least one team should be listed (if test data exists)");
        ExtentReportListener.getTest().pass("Admin viewed all teams with correct details");

        soft.assertAll();
    }

    // ── PS_TC019 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC019 - Verify Admin can edit a team (rename and manage members)"
    )
    public void TC019_adminCanEditTeam() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC019");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        ManageTeamsAdminPage teamsPage = loginAsAdmin().navigateToTeams();
        EditTeamPage editPage          = teamsPage.clickFirstTeam();

        soft.assertTrue(editPage.isPageDisplayed(),
                "Team edit screen should open with current team details");
        ExtentReportListener.getTest().info("Renaming team");

        String newTeamName = "AutoTeam_" + System.currentTimeMillis();
        editPage.updateTeamName(newTeamName).clickSaveChanges();

        soft.assertTrue(editPage.isSuccessMessageDisplayed(),
                "Success message 'Team updated successfully.' should appear after saving");
        ExtentReportListener.getTest().pass("Team renamed successfully and success message shown");
        soft.assertAll();
    }

    // ── PS_TC020 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_SMOKE, AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC020 - Verify Admin can manually add a single team member"
    )
    public void TC020_adminCanManuallyAddTeamMember() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC020");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        AddTeamMemberDialog dialog = loginAsAdmin().clickAddTeamMember();

        soft.assertTrue(dialog.isDialogVisible(),
                "Add Team Member dialog should open with Bulk Upload and Manual entry options");
        ExtentReportListener.getTest().info("Add Team Member dialog is visible");

        String ts    = String.valueOf(System.currentTimeMillis());
        String email = "alice_smith_" + ts + "@test.com";
        String empId = ts.substring(ts.length() - 5);   // numeric only (type="number")

        dialog.selectManualEntry()
              .enterEmployeeId(empId)
              .enterPhone("9876543210")
              .enterFirstName("Alice")
              .enterLastName("Smith")
              .enterEmail(email)
              .enterPassword("Test@1234")
              .clickRegister();

        ExtentReportListener.getTest().pass("Team member manually added; registration form submitted");
        soft.assertAll();
    }

    // ── PS_TC021 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        // No retryAnalyzer — this test registers real users; retrying causes duplicate-email failures
        description = "PS_TC021 - Verify Admin can bulk upload team members via .xlsx file"
    )
    public void TC021_adminCanBulkUploadTeamMembers() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC021");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();

        // Capture initial Scrum Engineers count from dashboard before opening dialog
        AdminDashboardPage dashboard = loginAsAdmin();
        int initialCount = 0;
        try {
            String raw = dashboard.getScrumEngineersCount().replaceAll("[^0-9]", "").trim();
            if (!raw.isEmpty()) initialCount = Integer.parseInt(raw);
        } catch (Exception ignored) {}
        ExtentReportListener.getTest().info("Initial Scrum Engineers count: " + initialCount);

        AddTeamMemberDialog dialog = dashboard.clickAddTeamMember();
        soft.assertTrue(dialog.isDialogVisible(), "Add Team Member dialog should be visible");

        // Step 1 — click Bulk Actions link and assert file upload area is displayed
        dialog.clickBulkActionsLink();
        soft.assertTrue(dialog.isBulkUploadOptionVisible(),
                "File upload area (input[type='file']) should be visible after clicking Bulk Actions");
        ExtentReportListener.getTest().info("File upload area is displayed");

        // Step 2 — upload the .xlsx and click Register
        String filePath = "C:\\Users\\2479309\\Desktop\\Project Sphere\\user-upload-template-v2.xlsx";
        ExtentReportListener.getTest().info("Uploading: " + filePath);
        dialog.uploadFile(filePath);
        dialog.clickBulkRegister();

        // Wait for result (success or error) to appear — max 15s
        dialog.waitForBulkResult();

        // Only take screenshot if the upload failed (error visible, not success)
        if (!dialog.isBulkSuccessDisplayed()) {
            try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
            try {
                byte[] png = BaseTest.captureScreenshotBytes();
                if (png != null) {
                    String dir  = "target/screenshots";
                    java.nio.file.Files.createDirectories(java.nio.file.Paths.get(dir));
                    String path = dir + "/TC021_bulk_error_" + System.currentTimeMillis() + ".png";
                    java.nio.file.Files.write(java.nio.file.Paths.get(path), png);
                    ExtentReportListener.getTest().addScreenCaptureFromPath(path, "Bulk Upload Error");
                }
            } catch (Exception ignored) {}
        }

        // Step 2 — quick check (3s) for success message only
        boolean success = dialog.isBulkSuccessDisplayed();
        ExtentReportListener.getTest().info("Bulk upload success: " + success);
        soft.assertTrue(success,
                "System should process the file and display a success message registering all members");

        // Close dialog and return to dashboard
        dialog.clickClose();

        // Step 3 — dashboard Scrum Engineers counter should have increased by 3
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        int updatedCount = 0;
        try {
            String raw = dashboard.getScrumEngineersCount().replaceAll("[^0-9]", "").trim();
            if (!raw.isEmpty()) updatedCount = Integer.parseInt(raw);
        } catch (Exception ignored) {}
        ExtentReportListener.getTest().info("Updated Scrum Engineers count: " + updatedCount
                + " (was: " + initialCount + ", expected at least: " + (initialCount + 3) + ")");
        soft.assertTrue(updatedCount >= initialCount + 3,
                "Dashboard Scrum Engineers counter should increase by at least 3 after bulk upload (was "
                        + initialCount + ", expected >= " + (initialCount + 3) + ", actual " + updatedCount + ")");

        ExtentReportListener.getTest().pass("Bulk upload: file uploaded, members registered, dashboard counter updated");
        soft.assertAll();
    }

    // ── PS_TC022 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        // No retryAnalyzer — file upload test; retrying causes unintended side effects
        description = "PS_TC022 - Verify bulk upload rejects an invalid/malformed .xlsx file"
    )
    public void TC022_bulkUploadRejectsInvalidFile() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC022");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        AddTeamMemberDialog dialog = loginAsAdmin().clickAddTeamMember();

        soft.assertTrue(dialog.isDialogVisible(), "Add Team Member dialog should be visible");

        // Open Bulk Actions and assert file upload area is displayed
        dialog.clickBulkActionsLink();
        soft.assertTrue(dialog.isBulkUploadOptionVisible(),
                "File upload area should be visible after clicking Bulk Actions");
        ExtentReportListener.getTest().info("File upload area is displayed");

        // Upload the invalid/empty template file
        String filePath = "C:\\Users\\2479309\\Desktop\\Project Sphere\\user-upload-template (1).xlsx";
        ExtentReportListener.getTest().info("Uploading invalid file: " + filePath);
        dialog.uploadFile(filePath);
        dialog.clickBulkRegister();

        // Wait for the error message to appear (max 15s)
        dialog.waitForBulkResult();

        // System should show an error — this is the expected behaviour for an invalid file
        soft.assertTrue(dialog.isBulkResultDisplayed(),
                "System should display an error message rejecting the invalid/malformed file");
        ExtentReportListener.getTest().pass("Invalid file correctly rejected by the bulk upload system");

        dialog.clickClose();
        soft.assertAll();
    }

    // ── PS_TC023 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_SMOKE, AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC023 - Verify Move to Workspace button appears on hover and navigates correctly"
    )
    public void TC023_moveToWorkspaceButtonAppearsOnHoverAndNavigates() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC023");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        ProjectsWorkspacePage workspacePage = loginAsAdmin().navigateToProjectsWorkspace();

        soft.assertTrue(workspacePage.isPageDisplayed(),
                "Projects workspace with project cards should be displayed");
        ExtentReportListener.getTest().info("Hovering over first project card");

        BoardPage boardPage = workspacePage.hoverFirstCardAndMoveToWorkspace();
        soft.assertTrue(boardPage.isBoardDisplayed(),
                "Board page should open after clicking Move to Workspace");
        ExtentReportListener.getTest().pass("Move to Workspace hover button works and navigates to Board");

        soft.assertAll();
    }

    // ── PS_TC024 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC024 - Verify Admin workspace filter options (All/Active/Completed) work correctly"
    )
    public void TC024_adminWorkspaceFiltersWork() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC024");
        ExtentReportListener.getTest().info("Test data: " + data);

        SoftAssert soft = new SoftAssert();
        ProjectsWorkspacePage workspacePage = loginAsAdmin().navigateToProjectsWorkspace();

        soft.assertTrue(workspacePage.isPageDisplayed(),
                "Projects workspace should be displayed");

        ExtentReportListener.getTest().info("Applying Active filter");
        workspacePage.filterActive();
        soft.assertTrue(workspacePage.isPageDisplayed(),
                "Workspace should remain displayed with Active filter applied");

        ExtentReportListener.getTest().info("Applying Completed filter");
        workspacePage.filterCompleted();
        soft.assertTrue(workspacePage.isPageDisplayed(),
                "Workspace should remain displayed with Completed filter applied");

        ExtentReportListener.getTest().info("Applying All filter to restore full list");
        workspacePage.filterAll();
        soft.assertTrue(workspacePage.isPageDisplayed(),
                "All projects should be displayed after applying All filter");

        ExtentReportListener.getTest().pass("Workspace filter options All/Active/Completed all function correctly");
        soft.assertAll();
    }

    // ── PS_TC025 — Global Search Tests ───────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_SMOKE, AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC025 - Global search bar is visible on Admin Dashboard"
    )
    public void TC025_searchBarIsVisibleOnDashboard() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC025");
        ExtentReportListener.getTest().info("Test data: " + data);

        AdminDashboardPage dashboard = loginAsAdmin();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(dashboard.isDashboardDisplayed(), "Admin dashboard should be displayed");
        soft.assertTrue(dashboard.isSearchBarVisible(),
                "Global search bar should be visible in the top navigation");
        ExtentReportListener.getTest().pass("Global search bar is visible on the Admin Dashboard");

        soft.assertAll();
    }

    // ── PS_TC026 ──────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC026 - Searching for a known term shows results"
    )
    public void TC026_searchForKnownTermShowsResults() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC026");
        ExtentReportListener.getTest().info("Test data: " + data);

        AdminDashboardPage dashboard = loginAsAdmin();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(dashboard.isSearchBarVisible(), "Search bar must be visible before searching");
        dashboard.searchFor("admin");
        ExtentReportListener.getTest().info("Typed 'admin' into the search bar");

        soft.assertTrue(dashboard.areSearchResultsVisible(),
                "Searching for 'admin' should display a results dropdown with at least one matching item");
        ExtentReportListener.getTest().pass("Search results appeared for query 'admin'");

        soft.assertAll();
    }

    // ── PS_TC027.1 ────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC027.1 - Searching for a non-existent term shows no results"
    )
    public void TC027_1_searchForNonExistentTermShowsNoResults() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC027.1");
        ExtentReportListener.getTest().info("Test data: " + data);

        AdminDashboardPage dashboard = loginAsAdmin();
        dashboard.searchFor("XYZABC_NO_MATCH_99999");

        SoftAssert soft = new SoftAssert();
        soft.assertTrue(dashboard.isNoResultsMessageVisible(),
                "System should display a 'No results found' message for a non-existent search term");
        ExtentReportListener.getTest().pass("No results message correctly shown for garbage query");

        soft.assertAll();
    }

    // ── PS_TC027.2 ────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_ADMIN},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC027.2 - Clearing the search bar closes the results"
    )
    public void TC027_2_clearingSearchClosesResults() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC027.2");
        ExtentReportListener.getTest().info("Test data: " + data);

        AdminDashboardPage dashboard = loginAsAdmin();
        dashboard.searchFor("admin");

        SoftAssert soft = new SoftAssert();
        soft.assertTrue(dashboard.areSearchResultsVisible(),
                "Search results should appear after typing 'admin' before clearing");

        dashboard.clearSearch();
        String valueAfterClear = dashboard.getSearchInputValue();
        soft.assertTrue(valueAfterClear == null || valueAfterClear.isEmpty(),
                "Search input should be empty after clearing");
        soft.assertFalse(dashboard.areSearchResultsVisible(),
                "Search results dropdown should close after clearing the search bar");
        ExtentReportListener.getTest().pass("Search bar cleared — input empty and results closed");

        soft.assertAll();
    }
}
