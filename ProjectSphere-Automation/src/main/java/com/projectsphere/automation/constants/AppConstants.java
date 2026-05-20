package com.projectsphere.automation.constants;

public final class AppConstants {

    private AppConstants() {}

    // ── Timeouts ──────────────────────────────────────────────────────────────
    public static final int IMPLICIT_WAIT        = 10;
    public static final int EXPLICIT_WAIT        = 60;   // matches config explicit.wait=60 (Render free-tier)
    public static final int PAGE_LOAD_WAIT       = 90;   // matches config page.load.wait=90
    public static final int BACKEND_ERROR_WAIT   = 120;  // Render free-tier auth API can take up to 90-120s

    // ── Application URLs ──────────────────────────────────────────────────────
    public static final String BASE_URL     = "https://projectsphere-8xvr.onrender.com/home";
    public static final String LOGIN_PATH   = "/home";

    // ── Page Titles ───────────────────────────────────────────────────────────
    public static final String LOGIN_PAGE_TITLE         = "ProjectSphere";
    public static final String ADMIN_DASHBOARD_TITLE    = "Admin Dashboard";
    public static final String PM_DASHBOARD_TITLE       = "PM Dashboard";
    public static final String DEV_DASHBOARD_TITLE      = "Developer Dashboard";

    // ── Navigation Menu Labels ────────────────────────────────────────────────
    public static final String NAV_PROJECTS             = "Projects";
    public static final String NAV_PROJECT_MANAGERS     = "Project Managers";
    public static final String NAV_TEAMS                = "Teams";
    public static final String NAV_MY_PROJECTS          = "My Projects";
    public static final String NAV_MY_TEAMS             = "My Teams";
    public static final String NAV_BOARD                = "Board";
    public static final String NAV_BACKLOG              = "Backlog";
    public static final String NAV_TIMELINE             = "Timeline";
    public static final String NAV_DEFECTS              = "Defects";
    public static final String NAV_TEST_CASES           = "Test Cases";
    public static final String NAV_HISTORY              = "History";
    public static final String NAV_ANALYTICS            = "Analytics";
    public static final String NAV_DOCUMENTS            = "Documents";
    public static final String NAV_TEAM_INFO            = "Team Info";

    // ── Button Labels ─────────────────────────────────────────────────────────
    public static final String BTN_SIGN_IN              = "Sign In";
    public static final String BTN_FORGOT_PASSWORD      = "Forgot Password";
    public static final String BTN_CREATE_PROJECT       = "Create Project";
    public static final String BTN_CREATE_TEAM          = "Create Team";
    public static final String BTN_CREATE_ISSUE         = "Create Issue";
    public static final String BTN_REPORT_DEFECT        = "Report Defect";
    public static final String BTN_CREATE_TEST_CASE     = "Create Test Case";
    public static final String BTN_SAVE_CHANGES         = "Save Changes";
    public static final String BTN_CANCEL               = "Cancel";
    public static final String BTN_CLOSE                = "Close";
    public static final String BTN_DELETE_PROJECT       = "Delete Project";
    public static final String BTN_MOVE_TO_WORKSPACE    = "Move to Workspace";
    public static final String BTN_EDIT_TEAM            = "Edit Team";
    public static final String BTN_ADD_STEP             = "Add Step";
    public static final String BTN_MARK_FIXED           = "Mark Fixed";
    public static final String BTN_RETEST               = "Retest";
    public static final String BTN_START_PROGRESS       = "Start Progress";
    public static final String BTN_LOGOUT               = "Logout";

    // ── Tab Labels ────────────────────────────────────────────────────────────
    public static final String TAB_COMMENTS             = "Comments";
    public static final String TAB_HISTORY              = "History";
    public static final String TAB_LINKED_TEST_CASES    = "Linked Test Cases";
    public static final String TAB_ACTIVITY             = "Activity";

    // ── Status Values ─────────────────────────────────────────────────────────
    public static final String STATUS_IN_PROGRESS       = "IN_PROGRESS";
    public static final String STATUS_ACTIVE            = "Active";
    public static final String STATUS_INACTIVE          = "Inactive";
    public static final String STATUS_OPEN              = "Open";
    public static final String STATUS_DONE              = "Done";
    public static final String STATUS_TO_DO             = "To Do";
    public static final String STATUS_PASSED            = "Passed";
    public static final String STATUS_FAILED            = "Failed";
    public static final String STATUS_BLOCKED           = "Blocked";

    // ── Board Column Names ────────────────────────────────────────────────────
    public static final String BOARD_COL_TODO           = "To Do";
    public static final String BOARD_COL_IN_PROGRESS    = "In Progress";
    public static final String BOARD_COL_DONE           = "Done";

    // ── User Roles ────────────────────────────────────────────────────────────
    public static final String ROLE_ADMIN               = "ADMIN";
    public static final String ROLE_PM                  = "PM";
    public static final String ROLE_DEVELOPER           = "DEVELOPER";

    // ── Config Property Keys ──────────────────────────────────────────────────
    public static final String KEY_BASE_URL             = "base.url";
    public static final String KEY_BROWSER              = "browser";
    public static final String KEY_IMPLICIT_WAIT        = "implicit.wait";
    public static final String KEY_EXPLICIT_WAIT        = "explicit.wait";
    public static final String KEY_PAGE_LOAD_WAIT       = "page.load.wait";
    public static final String KEY_RETRY_COUNT          = "retry.count";
    public static final String KEY_REPORT_PATH          = "report.path";
    public static final String KEY_SCREENSHOT_PATH      = "screenshot.path";
    public static final String KEY_EXCEL_PATH           = "excel.path";
    public static final String KEY_ADMIN_EMAIL          = "admin.email";
    public static final String KEY_ADMIN_PASSWORD       = "admin.password";
    public static final String KEY_PM_EMAIL             = "pm.email";
    public static final String KEY_PM_PASSWORD          = "pm.password";
    public static final String KEY_DEV_EMAIL            = "developer.email";
    public static final String KEY_DEV_PASSWORD         = "developer.password";

    // ── Excel Sheet Names ─────────────────────────────────────────────────────
    public static final String SHEET_TEST_SCENARIOS     = "TestScenarios";
    public static final String SHEET_TEST_CASES         = "TestCases";
    public static final String SHEET_RTM                = "RTM";
    public static final String SHEET_DEFECT             = "Defect";

    // Aliases used in test classes
    public static final String EXCEL_SHEET_TEST_CASES   = SHEET_TEST_CASES;

    // ── Excel Column Header Keys ──────────────────────────────────────────────
    public static final String COL_GROUP                = "GROUP";
    public static final String COL_TEST_CASE_ID         = "TEST CASE ID";
    public static final String COL_TEST_DESCRIPTION     = "TEST DESCRIPTION";
    public static final String COL_DESIGNER             = "DESIGNER";
    public static final String COL_TEST_TYPE            = "TEST TYPE";
    public static final String COL_FRD_PARAGRAPH        = "FRD PARAGRAPH NUMBER";
    public static final String COL_TEST_DATA            = "TEST DATA";
    public static final String COL_COMPLEXITY           = "COMPLEXITY";
    public static final String COL_STEP_NUM             = "STEP #";
    public static final String COL_STEP_DESC            = "STEP DESCRIPTION";
    public static final String COL_STEP_EXPECTED        = "STEP EXPECTED RESULTS";

    // ── Report / Screenshot Paths ─────────────────────────────────────────────
    public static final String REPORT_PATH              = "test-output/reports/ExtentReport.html";
    public static final String SCREENSHOT_PATH          = "test-output/screenshots/";
    public static final String REPORT_NAME              = "ProjectSphere Automation Report";
    public static final String REPORT_TITLE             = "ProjectSphere Test Execution Report";

    // ── Error Messages ────────────────────────────────────────────────────────
    public static final String ERR_INVALID_CREDENTIALS  = "Invalid credentials";
    public static final String ERR_MANDATORY_FIELD      = "This field is required";
    public static final String ERR_BLANK_FORM           = "Please fill in the required fields";

    // ── Domain Values ─────────────────────────────────────────────────────────
    public static final String DOMAIN_TECHNOLOGY        = "TECHNOLOGY";
    public static final String DOMAIN_WEB               = "WEB";
    public static final String DOMAIN_MOBILE            = "MOBILE";
    public static final String DOMAIN_FINANCE           = "FINANCE";

    // ── Issue Types ───────────────────────────────────────────────────────────
    public static final String ISSUE_TYPE_USER_STORY    = "User Story";
    public static final String ISSUE_TYPE_STORY         = "User Story";
    public static final String ISSUE_TYPE_TASK          = "Task";
    public static final String ISSUE_TYPE_BUG           = "Bug";

    // ── Severity Values ───────────────────────────────────────────────────────
    public static final String SEVERITY_CRITICAL        = "CRITICAL";
    public static final String SEVERITY_MAJOR           = "MAJOR";
    public static final String SEVERITY_HIGH            = "HIGH";
    public static final String SEVERITY_MEDIUM          = "MEDIUM";
    public static final String SEVERITY_LOW             = "LOW";
    public static final String SEVERITY_MINOR           = "MINOR";

    // ── Test Groups ───────────────────────────────────────────────────────────
    public static final String GROUP_SMOKE              = "smoke";
    public static final String GROUP_REGRESSION         = "regression";
    public static final String GROUP_LOGIN              = "login";
    public static final String GROUP_ADMIN              = "admin";
    public static final String GROUP_PM                 = "pm";
    public static final String GROUP_DEVELOPER          = "developer";
    public static final String GROUP_DEV                = "developer";
}
