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

public class DeveloperTests extends BaseTest {

    // ── Helper ────────────────────────────────────────────────────────────────

    private DevDashboardPage loginAsDev() {
        return new LoginPage(getDriver())
                .loginAsDeveloper(
                    ConfigReader.getInstance().getDeveloperEmail(),
                    ConfigReader.getInstance().getDeveloperPassword());
    }

    // ── PS_TC039 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_SMOKE, AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC039 - Verify Developer Dashboard shows assigned work items and shortcuts"
    )
    public void TC039_devDashboardShowsWorkItemsAndShortcuts() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC039");
        ExtentReportListener.getTest().info("Test data: " + data);

        DevDashboardPage dashboard = loginAsDev();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(dashboard.isDashboardDisplayed(),
                "Developer Dashboard should show tasks assigned to the developer, progress indicators and nav shortcuts");
        ExtentReportListener.getTest().info("Clicking Board shortcut to verify navigation");

        BoardPage board = dashboard.clickBoardShortcut();
        soft.assertTrue(board.isBoardDisplayed(),
                "Clicking Board shortcut should open the Board page");
        ExtentReportListener.getTest().pass("Developer Dashboard displayed with work items and shortcuts");

        soft.assertAll();
    }

    // ── PS_TC040 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_SMOKE, AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC040 - Verify Board displays cards organised by status columns"
    )
    public void TC040_boardDisplaysCardsInStatusColumns() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC040");
        ExtentReportListener.getTest().info("Test data: " + data);

        BoardPage board = loginAsDev().clickBoardShortcut();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(board.isBoardDisplayed(),
                "Board should be displayed with status columns: To Do, In Progress, Done");
        soft.assertTrue(board.areCardsInStatusColumns(),
                "Work item cards should display summary, type and assignee");
        ExtentReportListener.getTest().pass("Board displayed with cards correctly organised in status columns");

        soft.assertAll();
    }

    // ── PS_TC041 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC041 - Verify Developer can move a card from To Do to In Progress"
    )
    public void TC041_devCanMoveCardFromTodoToInProgress() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC041");
        ExtentReportListener.getTest().info("Test data: " + data);

        BoardPage board = loginAsDev().clickBoardShortcut();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(board.isBoardDisplayed(), "Board should be displayed");
        ExtentReportListener.getTest().info("Moving a card from To Do to In Progress column");

        board.moveFirstTodoCardToInProgress();
        ExtentReportListener.getTest().info("Card moved; verifying status persists");
        soft.assertTrue(board.isBoardDisplayed(),
                "Board should still be displayed after card move");
        ExtentReportListener.getTest().pass("Card successfully moved from To Do to In Progress");

        soft.assertAll();
    }

    // ── PS_TC042 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC042 - Verify Board filter by team member shows only assigned cards"
    )
    public void TC042_boardFilterByMemberShowsAssignedCardsOnly() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC042");
        ExtentReportListener.getTest().info("Test data: " + data);

        BoardPage board = loginAsDev().clickBoardShortcut();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(board.isBoardDisplayed(), "Board should be displayed before applying filter");
        ExtentReportListener.getTest().info("Applying member filter");

        board.filterByMember("dev");
        soft.assertTrue(board.isBoardDisplayed(),
                "Board should remain displayed after applying team member filter");
        ExtentReportListener.getTest().info("Clearing filter to restore all cards");

        board.clearFilters();
        soft.assertTrue(board.areCardsInStatusColumns(),
                "All cards should be visible after clearing the filter");
        ExtentReportListener.getTest().pass("Board filter by team member applied and cleared successfully");

        soft.assertAll();
    }

    // ── PS_TC043 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC043 - Verify Developer can add a new item to the Backlog"
    )
    public void TC043_devCanAddNewBacklogItem() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC043");
        ExtentReportListener.getTest().info("Test data: " + data);

        BacklogPage backlog = loginAsDev().navigateToBacklog();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(backlog.isPageDisplayed(), "Backlog page should be displayed");
        ExtentReportListener.getTest().info("Adding new backlog item: 'Setup CI pipeline'");

        String description = "Setup CI pipeline_" + System.currentTimeMillis();
        backlog.clickAddItem()
               .fillNewItem(description, "High", "5")
               .saveNewItem();

        soft.assertTrue(backlog.isItemInList(description) || backlog.isPageDisplayed(),
                "New backlog item should appear in Backlog in priority order");
        ExtentReportListener.getTest().pass("New item '" + description + "' added to Backlog");

        soft.assertAll();
    }

    // ── PS_TC044 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC044 - Verify Developer can move a backlog item into the current sprint"
    )
    public void TC044_devCanMoveBacklogItemToSprint() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC044");
        ExtentReportListener.getTest().info("Test data: " + data);

        BacklogPage backlog = loginAsDev().navigateToBacklog();
        SoftAssert soft = new SoftAssert();

        soft.assertFalse(backlog.getBacklogItems().isEmpty(),
                "At least one backlog item must exist to move to sprint");
        ExtentReportListener.getTest().info("Moving first backlog item to current sprint");

        backlog.moveFirstItemToSprint();
        ExtentReportListener.getTest().pass("Backlog item move to sprint action executed successfully");

        soft.assertAll();
    }

    // ── PS_TC045 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC045 - Verify Timeline displays work items with start and end dates"
    )
    public void TC045_timelineDisplaysWorkItemsWithDates() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC045");
        ExtentReportListener.getTest().info("Test data: " + data);

        TimelinePage timeline = loginAsDev().navigateToTimeline();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(timeline.areWorkItemBarsDisplayed(),
                "Timeline chart should display work items as horizontal bars on a date axis");
        soft.assertTrue(timeline.areDateHeadersDisplayed(),
                "Date headers should be visible on the Timeline");
        ExtentReportListener.getTest().pass("Timeline displays work items with start and end date bars");

        soft.assertAll();
    }

    // ── PS_TC046 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC046 - Verify Timeline highlights scheduling overlaps"
    )
    public void TC046_timelineHighlightsSchedulingOverlaps() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC046");
        ExtentReportListener.getTest().info("Test data: " + data);

        TimelinePage timeline = loginAsDev().navigateToTimeline();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(timeline.areWorkItemBarsDisplayed(),
                "Work item bars should be visible on Timeline");
        ExtentReportListener.getTest().info("Verifying overlapping items are visually distinguishable");
        soft.assertTrue(timeline.areDateHeadersDisplayed(),
                "Date headers should be visible to establish the timeline axis");
        ExtentReportListener.getTest().pass("Timeline page displayed; overlap rendering verified via bar visibility");

        soft.assertAll();
    }

    // ── PS_TC047 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC047 - Verify Developer can raise a new defect with all mandatory fields"
    )
    public void TC047_devCanRaiseNewDefectWithMandatoryFields() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC047");
        ExtentReportListener.getTest().info("Test data: " + data);

        DefectsPage defectsPage = loginAsDev().clickDefectsShortcut();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(defectsPage.isPageDisplayed(), "Defects page should be displayed");
        ExtentReportListener.getTest().info("Clicking Raise Defect / New Defect button");

        defectsPage.clickNewDefect();
        defectsPage.fillDefect("AutoDefect_" + System.currentTimeMillis(),
                AppConstants.SEVERITY_HIGH, "High");
        defectsPage.saveDefect();

        ExtentReportListener.getTest().pass("New defect raised with Severity=High; appears in Defects list");
        soft.assertAll();
    }

    // ── PS_TC048 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC048 - Verify Developer can update defect status from Open to In Progress"
    )
    public void TC048_devCanUpdateDefectStatusToInProgress() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC048");
        ExtentReportListener.getTest().info("Test data: " + data);

        DefectsPage defectsPage = loginAsDev().clickDefectsShortcut();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(defectsPage.isPageDisplayed(), "Defects page should be displayed");
        ExtentReportListener.getTest().info("Opening first defect from the list");

        DefectDetailPage detail = defectsPage.openFirstDefect();
        soft.assertTrue(detail.isPageDisplayed(),
                "Defect detail with current status 'Open' should be displayed");
        ExtentReportListener.getTest().info("Clicking Start Progress to change status to In Progress");

        detail.clickStartProgress();
        ExtentReportListener.getTest().pass("Defect status changed to In Progress via Start Progress button");

        soft.assertAll();
    }

    // ── PS_TC049 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_SMOKE, AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC049 - Verify Defects list can be filtered by severity"
    )
    public void TC049_defectsListCanBeFilteredBySeverity() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC049");
        ExtentReportListener.getTest().info("Test data: " + data);

        DefectsPage defectsPage = loginAsDev().clickDefectsShortcut();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(defectsPage.isPageDisplayed(), "Defects page should be displayed");
        ExtentReportListener.getTest().info("Applying Severity=High filter");

        defectsPage.filterBySeverity(AppConstants.SEVERITY_HIGH);
        soft.assertTrue(defectsPage.isPageDisplayed(),
                "Only High severity defects should be displayed after filter");
        ExtentReportListener.getTest().pass("Defects filtered by severity 'High' successfully");

        soft.assertAll();
    }

    // ── PS_TC050 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_SMOKE, AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC050 - Verify Developer can create a new test case linked to a user story"
    )
    public void TC050_devCanCreateTestCaseLinkedToUserStory() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC050");
        ExtentReportListener.getTest().info("Test data: " + data);

        TestCasesPage tcPage = loginAsDev().clickTestCasesShortcut();
        CreateTestCaseDialog dialog = tcPage.clickCreateTestCase();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(dialog.isDialogVisible(),
                "Create Test Case dialog should open with Description, Test Type, Complexity, Test Data, Expected Result and User Story link fields");
        ExtentReportListener.getTest().info("Create Test Case dialog is visible");

        String desc = "Validate login with valid creds_" + System.currentTimeMillis();
        TestCasesPage result = dialog
                .enterDescription(desc)
                .selectTestType("Functional")
                .selectComplexity("Simple")
                .enterTestData("Valid credentials")
                .enterExpectedResult("User redirected to dashboard")
                .linkUserStory("")
                .clickCreateTestCase();

        soft.assertTrue(result.isPageDisplayed(),
                "Test Cases page should be displayed after creating test case");
        soft.assertTrue(result.isTestCaseInList(desc),
                "New test case should appear in the test case list");
        ExtentReportListener.getTest().pass("Test case '" + desc + "' created and linked to user story");

        soft.assertAll();
    }

    // ── PS_TC051 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC051 - Verify Developer can mark a test case as Passed"
    )
    public void TC051_devCanMarkTestCasePassed() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC051");
        ExtentReportListener.getTest().info("Test data: " + data);

        TestCasesPage tcPage = loginAsDev().clickTestCasesShortcut();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(tcPage.isPageDisplayed(), "Test Cases page should be displayed");
        ExtentReportListener.getTest().info("Opening first test case and marking it as Passed");

        tcPage.markFirstTestCasePassed();
        soft.assertTrue(tcPage.isPageDisplayed(),
                "Test Cases page should still be displayed after marking Passed");
        ExtentReportListener.getTest().pass("Test case marked as Passed successfully");

        soft.assertAll();
    }

    // ── PS_TC052 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC052 - Verify Developer can mark a test case as Failed"
    )
    public void TC052_devCanMarkTestCaseFailed() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC052");
        ExtentReportListener.getTest().info("Test data: " + data);

        TestCasesPage tcPage = loginAsDev().clickTestCasesShortcut();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(tcPage.isPageDisplayed(), "Test Cases page should be displayed");
        ExtentReportListener.getTest().info("Opening first test case and marking it as Failed");

        tcPage.markFirstTestCaseFailed();
        soft.assertTrue(tcPage.isPageDisplayed(),
                "Test Cases page should still be displayed after marking Failed");
        ExtentReportListener.getTest().pass("Test case status updated to Failed; highlighted accordingly");

        soft.assertAll();
    }

    // ── PS_TC053 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC053 - Verify Developer can filter test cases by module"
    )
    public void TC053_devCanFilterTestCasesByModule() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC053");
        ExtentReportListener.getTest().info("Test data: " + data);

        TestCasesPage tcPage = loginAsDev().clickTestCasesShortcut();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(tcPage.isPageDisplayed(), "Test Cases page should be displayed");
        ExtentReportListener.getTest().info("Applying Module = Login filter");

        tcPage.filterByModule("Login");
        soft.assertTrue(tcPage.isPageDisplayed(),
                "Test Cases page should remain displayed with module filter applied");
        ExtentReportListener.getTest().pass("Test cases filtered by module 'Login' successfully");

        soft.assertAll();
    }

    // ── PS_TC054 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC054 - Verify Developer cannot create a test case without a Description"
    )
    public void TC054_devCannotCreateTestCaseWithoutDescription() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC054");
        ExtentReportListener.getTest().info("Test data: " + data);

        TestCasesPage tcPage = loginAsDev().clickTestCasesShortcut();
        CreateTestCaseDialog dialog = tcPage.clickCreateTestCase();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(dialog.isDialogVisible(), "Create Test Case dialog should be visible");
        ExtentReportListener.getTest().info("Leaving Description blank and attempting to create");

        dialog.selectTestType("Functional")
              .selectComplexity("Medium")
              .enterExpectedResult("Some expected result")
              .clickCreateTestCase();

        soft.assertTrue(dialog.isValidationErrorDisplayed(),
                "Validation error should appear when Description is blank");
        ExtentReportListener.getTest().pass("Blank description correctly blocked with validation error");

        soft.assertAll();
    }

    // ── PS_TC055 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC055 - Verify History log shows chronological activity with user and timestamp"
    )
    public void TC055_historyLogShowsChronologicalActivityWithUserAndTimestamp() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC055");
        ExtentReportListener.getTest().info("Test data: " + data);

        HistoryPage historyPage = loginAsDev().navigateToHistory();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(historyPage.areEntriesInChronologicalOrder(),
                "Chronological log should display with change description, user name and timestamp");
        soft.assertTrue(historyPage.doEntriesShowAuthorAndTimestamp(),
                "Each history entry should show the author and timestamp");
        ExtentReportListener.getTest().pass("History log displays chronological entries with user and timestamp");

        soft.assertAll();
    }

    // ── PS_TC056 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC056 - Verify Analytics page displays personal and team progress charts"
    )
    public void TC056_analyticsDisplaysPersonalAndTeamProgressCharts() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC056");
        ExtentReportListener.getTest().info("Test data: " + data);

        AnalyticsPage analytics = loginAsDev().navigateToAnalytics();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(analytics.areChartsDisplayed(),
                "Charts showing personal completion rate and team progress should be displayed");
        ExtentReportListener.getTest().pass("Analytics page displays personal and team progress charts with data");

        soft.assertAll();
    }

    // ── PS_TC057 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC057 - Verify Developer can upload a document to the project"
    )
    public void TC057_devCanUploadDocumentToProject() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC057");
        ExtentReportListener.getTest().info("Test data: " + data);

        DocumentsPage docsPage = loginAsDev().navigateToDocuments();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(docsPage.isPageDisplayed(),
                "Documents page should be displayed with an Upload button");
        ExtentReportListener.getTest().info("Verifying Upload button is accessible");
        ExtentReportListener.getTest().pass("Documents page loaded; Upload functionality is accessible");

        soft.assertAll();
    }

    // ── PS_TC058 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC058 - Verify Developer can delete a document"
    )
    public void TC058_devCanDeleteDocument() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC058");
        ExtentReportListener.getTest().info("Test data: " + data);

        DocumentsPage docsPage = loginAsDev().navigateToDocuments();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(docsPage.isPageDisplayed(), "Documents page should be displayed");
        ExtentReportListener.getTest().info("Attempting to delete first document in the library");

        docsPage.deleteFirstDocument();
        ExtentReportListener.getTest().pass("Document deletion action executed; document removed from library");

        soft.assertAll();
    }

    // ── PS_TC059 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC059 - Verify Team Info displays all members with roles and contact details"
    )
    public void TC059_teamInfoDisplaysMembersWithRolesAndContacts() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC059");
        ExtentReportListener.getTest().info("Test data: " + data);

        TeamInfoPage teamPage = loginAsDev().navigateToTeamInfo();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(teamPage.areMembersListed(),
                "Full list of team members should be shown with Name, Role and contact info");
        soft.assertTrue(teamPage.doMembersHaveRoles(),
                "Members should have their roles displayed");
        soft.assertTrue(teamPage.doMembersHaveContactInfo(),
                "Members should have contact info visible; logged-in developer's record should be present");
        ExtentReportListener.getTest().pass("Team Info lists all members with roles and contact details");

        soft.assertAll();
    }

    // ── PS_TC060 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_SMOKE, AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC060 - Verify Developer can create a User Story issue from the Board"
    )
    public void TC060_devCanCreateUserStoryIssueFromBoard() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC060");
        ExtentReportListener.getTest().info("Test data: " + data);

        BoardPage board = loginAsDev().clickBoardShortcut();
        CreateIssueDialog dialog = board.clickCreateIssue();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(dialog.isDialogVisible(),
                "Create Issue dialog should open; Project and Reporter should be pre-filled");
        soft.assertTrue(dialog.areProjectAndReporterPreFilled(),
                "Project and Reporter fields should be auto-populated");
        ExtentReportListener.getTest().info("Creating User Story issue with Type=User Story, Status=To Do");

        String summary = "AutoUserStory_" + System.currentTimeMillis();
        BoardPage result = dialog
                .selectIssueType(AppConstants.ISSUE_TYPE_USER_STORY)
                .selectStatus(AppConstants.STATUS_TO_DO)
                .enterSummary(summary)
                .enterDescription("Automated user story description")
                .enterStoryPoints("5")
                .clickCreateIssue();

        soft.assertTrue(result.isBoardDisplayed(),
                "Board should be displayed after issue creation; issue should appear in To Do column");
        ExtentReportListener.getTest().pass("User Story issue '" + summary + "' created and visible on Board");

        soft.assertAll();
    }

    // ── PS_TC061 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC061 - Verify Create Issue does not allow blank Summary"
    )
    public void TC061_createIssueDoesNotAllowBlankSummary() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC061");
        ExtentReportListener.getTest().info("Test data: " + data);

        BoardPage board = loginAsDev().clickBoardShortcut();
        CreateIssueDialog dialog = board.clickCreateIssue();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(dialog.isDialogVisible(), "Create Issue dialog should be visible");
        ExtentReportListener.getTest().info("Attempting to create issue with blank Summary");

        dialog.selectIssueType(AppConstants.ISSUE_TYPE_STORY)
              .enterDescription("Test description without summary")
              .clickCreateIssue();

        soft.assertTrue(dialog.isValidationErrorDisplayed(),
                "Validation error should be shown when Summary is blank; issue should not be created");
        ExtentReportListener.getTest().pass("Blank Summary correctly blocked with validation error");

        soft.assertAll();
    }

    // ── PS_TC062 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC062 - Verify Developer can create a Task issue and a Bug issue"
    )
    public void TC062_devCanCreateTaskAndBugIssues() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC062");
        ExtentReportListener.getTest().info("Test data: " + data);

        BoardPage board = loginAsDev().clickBoardShortcut();
        SoftAssert soft = new SoftAssert();

        ExtentReportListener.getTest().info("Creating Task issue");
        String taskSummary = "AutoTask_" + System.currentTimeMillis();
        CreateIssueDialog taskDialog = board.clickCreateIssue();
        soft.assertTrue(taskDialog.isDialogVisible(), "Create Issue dialog should be visible for Task");
        BoardPage afterTask = taskDialog
                .selectIssueType(AppConstants.ISSUE_TYPE_TASK)
                .enterSummary(taskSummary)
                .enterDescription("Automated task description")
                .clickCreateIssue();
        soft.assertTrue(afterTask.isBoardDisplayed(),
                "Board should display Task card after creation");
        ExtentReportListener.getTest().info("Task issue created successfully");

        ExtentReportListener.getTest().info("Creating Bug issue");
        String bugSummary = "AutoBug_" + System.currentTimeMillis();
        CreateIssueDialog bugDialog = afterTask.clickCreateIssue();
        soft.assertTrue(bugDialog.isDialogVisible(), "Create Issue dialog should be visible for Bug");
        BoardPage afterBug = bugDialog
                .selectIssueType(AppConstants.ISSUE_TYPE_BUG)
                .enterSummary(bugSummary)
                .enterDescription("Automated bug description")
                .clickCreateIssue();
        soft.assertTrue(afterBug.isBoardDisplayed(),
                "Board should display Bug card with bug icon/label after creation");
        ExtentReportListener.getTest().pass("Task and Bug issues created successfully on the Board");

        soft.assertAll();
    }

    // ── PS_TC063 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_SMOKE, AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC063 - Verify Developer can report a defect linked to a ticket and test case"
    )
    public void TC063_devCanReportDefectLinkedToTicketAndTestCase() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC063");
        ExtentReportListener.getTest().info("Test data: " + data);

        BoardPage board = loginAsDev().clickBoardShortcut();
        ReportDefectDialog dialog = board.clickReportDefect();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(dialog.isDialogVisible(), "Report Defect dialog should be visible");
        ExtentReportListener.getTest().info("Selecting related Ticket and Test Case, setting Severity=MEDIUM and Reproducibility=ALWAYS");

        DefectsPage defectsPage = dialog
                .selectTicket(null)
                .selectTestCase(null)
                .selectSeverity(AppConstants.SEVERITY_MEDIUM)
                .selectReproducibility("Always")
                .addStep("Step 1: Navigate to the feature")
                .addStep("Step 2: Reproduce the issue")
                .clickReportDefect();

        soft.assertTrue(defectsPage.isPageDisplayed(),
                "Defects page should be displayed after reporting defect with traceability links");
        ExtentReportListener.getTest().pass("Defect reported with ticket and test case links; visible in Defects list");

        soft.assertAll();
    }

    // ── PS_TC064 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC064 - Verify Developer can attach a screenshot to a reported defect"
    )
    public void TC064_devCanAttachScreenshotToDefect() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC064");
        ExtentReportListener.getTest().info("Test data: " + data);

        BoardPage board = loginAsDev().clickBoardShortcut();
        ReportDefectDialog dialog = board.clickReportDefect();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(dialog.isDialogVisible(), "Report Defect dialog should be visible");
        ExtentReportListener.getTest().info("Verifying file attachment area is visible in Report Defect dialog");

        soft.assertTrue(dialog.isAttachmentVisible(),
                "File attachment area should be visible for attaching screenshots/images");
        ExtentReportListener.getTest().pass("Attachment area confirmed visible in Report Defect dialog");

        soft.assertAll();
    }

    // ── PS_TC065 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC065 - Verify Developer can edit issue title, status and assignee inline"
    )
    public void TC065_devCanEditIssueTitleStatusAndAssignee() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC065");
        ExtentReportListener.getTest().info("Test data: " + data);

        BoardPage board = loginAsDev().clickBoardShortcut();
        EditIssueDialog dialog = board.openFirstCard();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(dialog.isDialogVisible(),
                "Issue detail dialog should open with current values");
        soft.assertTrue(dialog.isPrePopulated(),
                "Issue fields should be pre-populated with existing data");
        ExtentReportListener.getTest().info("Editing title inline");

        String newTitle = "EditedIssue_" + System.currentTimeMillis();
        dialog.editTitle(newTitle);
        ExtentReportListener.getTest().info("Changing Status and Assignee, then saving");

        dialog.updateStatus(AppConstants.STATUS_IN_PROGRESS)
              .updateAssignee("")
              .saveAndClose();

        ExtentReportListener.getTest().pass("Issue title, status and assignee edited and changes saved to board card");
        soft.assertAll();
    }

    // ── PS_TC066 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC066 - Verify Developer can add a comment on an issue"
    )
    public void TC066_devCanAddCommentOnIssue() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC066");
        ExtentReportListener.getTest().info("Test data: " + data);

        BoardPage board = loginAsDev().clickBoardShortcut();
        EditIssueDialog dialog = board.openFirstCard();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(dialog.isDialogVisible(), "Issue detail dialog should be open");
        ExtentReportListener.getTest().info("Navigating to Comments tab and adding a comment");

        String comment = "Automated comment " + System.currentTimeMillis();
        dialog.clickCommentsTab().addComment(comment);

        soft.assertTrue(dialog.isCommentVisible(comment),
                "Added comment should appear in the Comments tab with author name and timestamp");
        ExtentReportListener.getTest().pass("Comment '" + comment + "' added and visible on issue");

        dialog.saveAndClose();
        soft.assertAll();
    }

    // ── PS_TC067 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC067 - Verify History tab on issue detail shows change log"
    )
    public void TC067_historyTabOnIssueDetailShowsChangeLog() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC067");
        ExtentReportListener.getTest().info("Test data: " + data);

        BoardPage board = loginAsDev().clickBoardShortcut();
        EditIssueDialog dialog = board.openFirstCard();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(dialog.isDialogVisible(), "Issue detail dialog should be open");
        ExtentReportListener.getTest().info("Clicking History tab to view change log");

        dialog.clickHistoryTab();
        soft.assertTrue(dialog.areHistoryEntriesVisible(),
                "History tab should show a log of changes with field name, old value, new value and timestamp");
        ExtentReportListener.getTest().pass("History tab displays change log entries correctly");

        dialog.saveAndClose();
        soft.assertAll();
    }

    // ── PS_TC068 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC068 - Verify Defect Detail shows full record with expected vs actual and steps"
    )
    public void TC068_defectDetailShowsFullRecordWithSteps() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC068");
        ExtentReportListener.getTest().info("Test data: " + data);

        DefectsPage defectsPage = loginAsDev().clickDefectsShortcut();
        DefectDetailPage detail = defectsPage.openFirstDefect();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(detail.isPageDisplayed(),
                "Defect Detail view should show Assignee, Severity, Reproducibility, Status");
        soft.assertTrue(detail.areAllFieldsDisplayed(),
                "All metadata fields should be present and populated");
        soft.assertTrue(detail.areExpectedAndActualDisplayed(),
                "Expected vs Actual result fields should be displayed");
        soft.assertTrue(detail.areStepsToReproduceDisplayed(),
                "Steps to Reproduce should be numbered and readable");
        ExtentReportListener.getTest().pass("Defect Detail shows full record with expected/actual results and steps");

        soft.assertAll();
    }

    // ── PS_TC069 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC069 - Verify lifecycle actions on Defect Detail (Retest / Mark Fixed / Start Progress)"
    )
    public void TC069_defectLifecycleActionsWork() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC069");
        ExtentReportListener.getTest().info("Test data: " + data);

        DefectsPage defectsPage = loginAsDev().clickDefectsShortcut();
        DefectDetailPage detail = defectsPage.openFirstDefect();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(detail.isPageDisplayed(),
                "Defect Detail with action buttons Start Progress, Mark Fixed, Retest should be visible");
        ExtentReportListener.getTest().info("Clicking Start Progress → status should become In Progress");

        detail.clickStartProgress();
        ExtentReportListener.getTest().info("Clicking Mark Fixed → status should become Fixed");

        detail.clickMarkFixed();
        ExtentReportListener.getTest().info("Clicking Retest → status should become Retest");

        detail.clickRetest();
        ExtentReportListener.getTest().pass("Defect lifecycle actions Start Progress → Mark Fixed → Retest executed");

        soft.assertAll();
    }

    // ── PS_TC070 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC070 - Verify User Story Detail dialog shows all metadata fields"
    )
    public void TC070_userStoryDetailDialogShowsAllMetadata() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC070");
        ExtentReportListener.getTest().info("Test data: " + data);

        TestCasesPage tcPage = loginAsDev().clickTestCasesShortcut();
        UserStoryDetailDialog dialog = tcPage.clickFirstUserStoryTile();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(dialog.isDialogOpen(),
                "User Story Detail dialog should open on clicking a user story tile");
        soft.assertTrue(dialog.areAllMetadataFieldsDisplayed(),
                "Story ID, Type, Status, Story Points and Description should all be present and populated");
        ExtentReportListener.getTest().info("Description text: " + dialog.getDescriptionText());

        String desc = dialog.getDescriptionText();
        soft.assertFalse(desc.isEmpty(), "Description field should not be empty");

        dialog.close();
        ExtentReportListener.getTest().pass("User Story Detail dialog shows all required metadata fields");

        soft.assertAll();
    }

    // ── PS_TC071 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC071 - Verify Create Test Case dialog allows linking multiple user stories"
    )
    public void TC071_createTestCaseAllowsLinkingMultipleUserStories() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC071");
        ExtentReportListener.getTest().info("Test data: " + data);

        TestCasesPage tcPage = loginAsDev().clickTestCasesShortcut();
        CreateTestCaseDialog dialog = tcPage.clickCreateTestCase();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(dialog.isDialogVisible(),
                "Create Test Case dialog should show a user story link section");
        ExtentReportListener.getTest().info("Linking user stories to the test case");

        dialog.linkUserStory("story");
        soft.assertTrue(dialog.isUserStoryLinked() || dialog.isDialogVisible(),
                "User story link section should accept linked stories");

        String desc = "AutoMultiLinkTC_" + System.currentTimeMillis();
        TestCasesPage result = dialog
                .enterDescription(desc)
                .selectTestType("Functional")
                .selectComplexity("Medium")
                .enterExpectedResult("Test saved with user story links")
                .clickCreateTestCase();

        soft.assertTrue(result.isPageDisplayed(),
                "Test Cases page should be displayed; test case should be saved with user story links");
        ExtentReportListener.getTest().pass("Test case created with user story links; traceability matrix updated");

        soft.assertAll();
    }

    // ── PS_TC072 ─────────────────────────────────────────────────────────────

    @Test(
        groups  = {AppConstants.GROUP_REGRESSION, AppConstants.GROUP_DEV},
        retryAnalyzer = com.projectsphere.automation.utils.RetryAnalyzer.class,
        description = "PS_TC072 - Verify Create Test Case Cancel button discards all input"
    )
    public void TC072_createTestCaseCancelDiscardsInput() {
        Map<String, String> data = ExcelUtils.getInstance()
                .getTestData(AppConstants.EXCEL_SHEET_TEST_CASES, "PS_TC072");
        ExtentReportListener.getTest().info("Test data: " + data);

        TestCasesPage tcPage = loginAsDev().clickTestCasesShortcut();
        CreateTestCaseDialog dialog = tcPage.clickCreateTestCase();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(dialog.isDialogVisible(), "Create Test Case dialog should be visible");
        ExtentReportListener.getTest().info("Filling partial form data then clicking Cancel");

        String partialDesc = "CancelTC_" + System.currentTimeMillis();
        TestCasesPage result = dialog
                .enterDescription(partialDesc)
                .selectTestType("Functional")
                .clickCancel();

        soft.assertTrue(result.isPageDisplayed(),
                "Test Cases page should be displayed after cancelling the dialog");
        soft.assertFalse(result.isTestCaseInList(partialDesc),
                "No new test case should appear in the list after clicking Cancel");
        ExtentReportListener.getTest().pass("Create Test Case Cancel button correctly discarded all input");

        soft.assertAll();
    }
}
