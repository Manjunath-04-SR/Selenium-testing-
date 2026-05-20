package com.projectsphere.automation.pages;

import com.projectsphere.automation.constants.AppConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class AdminDashboardPage {

    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Dashboard container ───────────────────────────────────────────────────
    // Real DOM: <h1 class="ax-page-title">Admin Dashboard</h1>
    private final By pageTitle       = By.cssSelector("h1.ax-page-title");
    private final By dashboardAdmin  = By.cssSelector("app-dashboard-admin");

    // ── Global search bar (top nav) ───────────────────────────────────────────
    // Real DOM: <div class="ax-search-top"><span class="mi">search</span>
    //           <input type="text" placeholder="Search users, teams, projects...">
    private final By globalSearchBar = By.cssSelector(".ax-search-top input[type='text']");

    // Results dropdown — appears after typing; class names follow the ax- prefix pattern
    private final By searchResultsDropdown = By.cssSelector(
        ".ax-search-results, .ax-search-dropdown, .ax-dropdown, " +
        "[class*='search-result'], [class*='search-drop']");

    // Individual result rows inside the dropdown
    private final By searchResultItems = By.cssSelector(
        ".ax-search-results li, .ax-search-results a, " +
        "[class*='search-result'] [class*='item'], " +
        "[class*='search-drop'] [class*='row']");

    // "No results" message rendered when nothing matches
    private final By noResultsMessage = By.xpath(
        "//*[contains(normalize-space(),'No results') or " +
        "    contains(normalize-space(),'no results') or " +
        "    contains(normalize-space(),'Nothing found')]" +
        "[not(self::script)]");

    // ── KPI summary tiles ─────────────────────────────────────────────────────
    // Real DOM: <button class="ax-kpi">  ← clickable navigation tiles
    //             <span class="ax-kpi-icon blue"><span class="mi">badge</span></span>
    //             <span>
    //               <span class="ax-kpi-label">Project Managers</span>
    //               <div  class="ax-kpi-value">1</div>
    //             </span>
    //           </button>
    //
    // "Total Projects" is a display-only tile (div, not button):
    //   <div class="ax-kpi">
    //     <span class="ax-kpi-icon blue"><span class="mi">folder_open</span></span>
    //     <span>
    //       <span class="ax-kpi-label">Total Projects</span>
    //       <div  class="ax-kpi-value">9</div>
    //     </span>
    //   </div>
    // Tiles (in DOM order): Project Managers, Scrum Masters, Scrum Engineers,
    //                        Teams, Active Projects, Planned Projects, Completed Projects
    private final By kpiTiles        = By.cssSelector("button.ax-kpi");
    private final By kpiValues       = By.cssSelector(".ax-kpi-value");

    // "Total Projects" display tile — uses div.ax-kpi (not a button)
    private final By kpiTotalProjectsValue = By.xpath(
        "//div[contains(@class,'ax-kpi')][.//span[contains(@class,'ax-kpi-label')" +
        " and normalize-space()='Total Projects']]//div[contains(@class,'ax-kpi-value')]");

    // Individual KPI tiles by label — use ax-kpi-label class for precision
    private final By kpiPMsTile          = By.xpath("//button[contains(@class,'ax-kpi')][.//span[contains(@class,'ax-kpi-label') and normalize-space()='Project Managers']]//div[contains(@class,'ax-kpi-value')]");
    private final By kpiScrumMastersTile = By.xpath("//button[contains(@class,'ax-kpi')][.//span[contains(@class,'ax-kpi-label') and normalize-space()='Scrum Masters']]//div[contains(@class,'ax-kpi-value')]");
    private final By kpiScrumEngTile     = By.xpath("//button[contains(@class,'ax-kpi')][.//span[contains(@class,'ax-kpi-label') and normalize-space()='Scrum Engineers']]//div[contains(@class,'ax-kpi-value')]");
    private final By kpiTeamsTile        = By.xpath("//button[contains(@class,'ax-kpi')][.//span[contains(@class,'ax-kpi-label') and normalize-space()='Teams']]//div[contains(@class,'ax-kpi-value')]");
    private final By kpiActiveProjTile   = By.xpath("//button[contains(@class,'ax-kpi')][.//span[contains(@class,'ax-kpi-label') and normalize-space()='Active Projects']]//div[contains(@class,'ax-kpi-value')]");
    private final By kpiPlannedProjTile  = By.xpath("//button[contains(@class,'ax-kpi')][.//span[contains(@class,'ax-kpi-label') and normalize-space()='Planned Projects']]//div[contains(@class,'ax-kpi-value')]");
    private final By kpiCompletedProjTile= By.xpath("//button[contains(@class,'ax-kpi')][.//span[contains(@class,'ax-kpi-label') and normalize-space()='Completed Projects']]//div[contains(@class,'ax-kpi-value')]");

    // ── KPI tiles used for NAVIGATION (buttons navigate when clicked) ─────────
    // Pin to [1] so "Active Projects" (first in DOM) wins over Planned/Completed
    private final By navKpiProjects  = By.xpath(
        "(//button[contains(@class,'ax-kpi')]" +
        "[.//span[contains(@class,'ax-kpi-label') and contains(normalize-space(),'Projects')]])[1]");
    private final By navKpiPMs       = By.xpath(
        "//button[contains(@class,'ax-kpi')]" +
        "[.//span[contains(@class,'ax-kpi-label') and normalize-space()='Project Managers']]");
    private final By navKpiTeams     = By.xpath(
        "//button[contains(@class,'ax-kpi')]" +
        "[.//span[contains(@class,'ax-kpi-label') and normalize-space()='Teams']]");

    // ── Quick-action buttons (button.qa-card on the dashboard) ───────────────
    // Real DOM: <button class="qa-card">
    //             <span class="qa-text">
    //               <span class="qa-title">Add Team Member</span>
    //             </span>
    //           </button>
    // Use qa-title span for exact matching — avoids "Add Project" matching "Add Project Manager"
    private final By addTeamMemberBtn  = By.xpath(
        "//button[contains(@class,'qa-card')][.//span[contains(@class,'qa-title') and normalize-space()='Add Team Member']]");
    private final By addPMBtn          = By.xpath(
        "//button[contains(@class,'qa-card')][.//span[contains(@class,'qa-title') and normalize-space()='Add Project Manager']]");
    private final By addScrumMasterBtn = By.xpath(
        "//button[contains(@class,'qa-card')][.//span[contains(@class,'qa-title') and normalize-space()='Add Scrum Master']]");
    private final By addProjectBtn     = By.xpath(
        "//button[contains(@class,'qa-card')][.//span[contains(@class,'qa-title') and normalize-space()='Add Project']]");
    private final By bulkActionsBtn    = By.xpath(
        "//button[contains(@class,'qa-card')][.//span[contains(@class,'qa-title') and normalize-space()='Bulk Actions']]");

    // ── Avatar + Logout ───────────────────────────────────────────────────────
    // Real DOM: <button class="ax-avatar-btn"><span class="ax-avatar">AU</span></button>
    // Logout is hidden inside a dropdown that opens on avatar click
    private final By avatarBtn = By.cssSelector("button.ax-avatar-btn");
    private final By logoutBtn = By.xpath(
        "//button[contains(normalize-space(),'Logout') or contains(normalize-space(),'Sign Out')" +
        "       or contains(normalize-space(),'Log out')]" +
        " | //a[contains(normalize-space(),'Logout') or contains(normalize-space(),'Sign Out')]");

    public AdminDashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("AdminDashboardPage initialized");
    }

    // ── Dashboard state ───────────────────────────────────────────────────────

    public boolean isDashboardDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(pageTitle),
                    ExpectedConditions.visibilityOfElementLocated(dashboardAdmin)
            ));
            logger.info("Admin Dashboard is displayed");
            return true;
        } catch (Exception e) {
            logger.warn("Admin Dashboard not found: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Returns true when at least one ax-kpi tile is visible.
     * Real DOM has 7 tiles: Project Managers, Scrum Masters, Scrum Engineers,
     * Teams, Active Projects, Planned Projects, Completed Projects.
     */
    public boolean areSummaryTilesVisible() {
        try {
            List<WebElement> tiles = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(kpiTiles));
            logger.info("Found {} KPI tiles", tiles.size());
            return !tiles.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getKpiTiles() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(kpiTiles));
        } catch (Exception e) {
            return List.of();
        }
    }

    // ── KPI counts ────────────────────────────────────────────────────────────

    public String getProjectManagersCount()  { return getKpiValue(kpiPMsTile,           "Project Managers"); }
    public String getScrumMastersCount()     { return getKpiValue(kpiScrumMastersTile,  "Scrum Masters"); }
    public String getScrumEngineersCount()   { return getKpiValue(kpiScrumEngTile,      "Scrum Engineers"); }
    public String getTeamsCount()            { return getKpiValue(kpiTeamsTile,          "Teams"); }
    public String getActiveProjectsCount()   { return getKpiValue(kpiActiveProjTile,    "Active Projects"); }
    public String getPlannedProjectsCount()  { return getKpiValue(kpiPlannedProjTile,   "Planned Projects"); }
    public String getCompletedProjectsCount(){ return getKpiValue(kpiCompletedProjTile, "Completed Projects"); }

    /**
     * Reads the "Total Projects" display KPI tile (div.ax-kpi) and returns
     * the count as an int.  Returns -1 if the tile is not found or cannot be parsed.
     */
    public int getTotalProjectsCount() {
        try {
            WebElement el = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(kpiTotalProjectsValue));
            int count = Integer.parseInt(el.getText().trim());
            logger.info("Total Projects KPI: {}", count);
            return count;
        } catch (NumberFormatException nfe) {
            logger.warn("Could not parse Total Projects KPI value");
            return -1;
        } catch (Exception e) {
            logger.warn("Total Projects KPI not found: {}", e.getMessage());
            return -1;
        }
    }

    // Legacy aliases kept for backward compatibility with existing tests
    public String getTotalPMsCount()       { return getProjectManagersCount(); }
    // "Members" = Scrum Masters + Scrum Engineers combined count (no dedicated tile)
    public String getTotalMembersCount()   { return getScrumEngineersCount(); }
    public String getTotalTeamsCount()     { return getTeamsCount(); }

    // ── Navigation via KPI tiles ──────────────────────────────────────────────
    // The dashboard has no sidebar nav links — navigation is through KPI button clicks.

    public ManageProjectsPage navigateToProjects() {
        clickKpiTile(navKpiProjects, "Projects");
        return new ManageProjectsPage(driver);
    }

    public ManageProjectManagersPage navigateToProjectManagers() {
        clickKpiTile(navKpiPMs, "Project Managers");
        return new ManageProjectManagersPage(driver);
    }

    public ManageTeamsAdminPage navigateToTeams() {
        clickKpiTile(navKpiTeams, "Teams");
        return new ManageTeamsAdminPage(driver);
    }

    /**
     * Navigate to the Projects workspace view.
     * First attempts to click a "Projects" KPI tile; if that lands on a list page,
     * the caller should use {@link ManageProjectsPage} to open the workspace view.
     */
    public ProjectsWorkspacePage navigateToProjectsWorkspace() {
        clickKpiTile(navKpiProjects, "Projects (for workspace)");
        logger.info("Navigated toward Projects Workspace");
        return new ProjectsWorkspacePage(driver);
    }

    // ── Quick-action dialogs ──────────────────────────────────────────────────

    public AddProjectDialog clickAddProject() {
        clickSafe(addProjectBtn, "Add Project");
        return new AddProjectDialog(driver);
    }

    public AddTeamMemberDialog clickAddTeamMember() {
        clickSafe(addTeamMemberBtn, "Add Team Member");
        return new AddTeamMemberDialog(driver);
    }

    public AddProjectManagerDialog clickAddProjectManager() {
        clickSafe(addPMBtn, "Add Project Manager");
        return new AddProjectManagerDialog(driver);
    }

    public AddProjectManagerDialog clickAddScrumMaster() {
        clickSafe(addScrumMasterBtn, "Add Scrum Master");
        return new AddProjectManagerDialog(driver);
    }

    // ── Global search bar ─────────────────────────────────────────────────────

    /** Returns true when the search input in the top nav is visible. */
    public boolean isSearchBarVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(globalSearchBar))
                       .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Types {@code query} into the global search bar and waits briefly
     * for Angular to render results.
     */
    public AdminDashboardPage searchFor(String query) {
        try {
            WebElement input = wait.until(
                    ExpectedConditions.elementToBeClickable(globalSearchBar));
            input.clear();
            input.sendKeys(query);
            logger.info("Typed search query: '{}'", query);
            // Brief pause for Angular reactive search to render results
            Thread.sleep(600);
        } catch (Exception e) {
            logger.warn("Could not type in global search bar: {}", e.getMessage());
        }
        return this;
    }

    /** Clears the search bar using Ctrl+A → Delete, then Escape to close results. */
    public AdminDashboardPage clearSearch() {
        try {
            WebElement input = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(globalSearchBar));
            input.sendKeys(Keys.CONTROL + "a");
            input.sendKeys(Keys.DELETE);
            input.sendKeys(Keys.ESCAPE);
            logger.info("Cleared global search bar");
            Thread.sleep(300);
        } catch (Exception e) {
            logger.warn("Could not clear search bar: {}", e.getMessage());
        }
        return this;
    }

    /** Returns true when the search results dropdown is visible (any results). Uses 5s wait. */
    public boolean areSearchResultsVisible() {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(5))
                       .until(ExpectedConditions.visibilityOfElementLocated(searchResultsDropdown))
                       .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns the list of result items currently shown in the dropdown. */
    public List<WebElement> getSearchResultItems() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(searchResultItems));
        } catch (Exception e) {
            return List.of();
        }
    }

    /** Returns true when the result dropdown contains at least one item matching {@code text}. */
    public boolean isResultContaining(String text) {
        try {
            By locator = By.xpath(
                "//*[contains(@class,'search-result') or contains(@class,'ax-search') or " +
                "    contains(@class,'ax-dropdown')]//*[contains(normalize-space(),'" + text + "')]");
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns true when the "No results" message is shown after a failed search. Uses 5s wait. */
    public boolean isNoResultsMessageVisible() {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(5))
                       .until(ExpectedConditions.visibilityOfElementLocated(noResultsMessage))
                       .isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns the current value of the search input field. */
    public String getSearchInputValue() {
        try {
            return driver.findElement(globalSearchBar).getAttribute("value");
        } catch (Exception e) {
            return "";
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    public LoginPage logout() {
        // Step 1: click avatar to open profile/logout dropdown
        try {
            wait.until(ExpectedConditions.elementToBeClickable(avatarBtn)).click();
            logger.info("Clicked avatar button to open dropdown");
            Thread.sleep(400);
        } catch (Exception e) {
            logger.warn("Could not click avatar button: {}", e.getMessage());
        }
        // Step 2: click Logout inside the dropdown
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(logoutBtn));
        btn.click();
        logger.info("Logged out from Admin Dashboard");
        return new LoginPage(driver);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private String getKpiValue(By locator, String label) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            String text = el.getText().trim();
            logger.info("{} count: {}", label, text);
            return text;
        } catch (Exception e) {
            logger.warn("Could not read KPI value for '{}': {}", label, e.getMessage());
            return "";
        }
    }

    /**
     * Clicks a KPI tile (used for navigation).
     * The dashboard has no sidebar — KPI buttons are the navigation mechanism.
     */
    private void clickKpiTile(By locator, String label) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            el.click();
            logger.info("Clicked KPI tile: {}", label);
        } catch (Exception e) {
            logger.warn("KPI tile '{}' not clickable: {}", label, e.getMessage());
        }
    }

    private void clickSafe(By locator, String label) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            el.click();
            logger.info("Clicked: {}", label);
        } catch (Exception e) {
            logger.warn("Could not click '{}': {}", label, e.getMessage());
        }
    }
}
