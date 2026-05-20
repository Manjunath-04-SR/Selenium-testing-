package com.projectsphere.automation.pages;

import com.projectsphere.automation.constants.AppConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class ManageProjectsPage {

    private static final Logger logger = LoggerFactory.getLogger(ManageProjectsPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators — based on real DOM ──────────────────────────────────────────
    // Component : <app-projects-list>
    // Page title: <h1 class="ax-page-title">Projects Workspace</h1>
    // Cards     : <div class="ax-card-wrap"><button class="ax-person">...</button></div>
    // Add btn   : <button class="ax-btn primary"><span class="mi">add</span> New Project </button>
    // Search    : <div class="ax-search"><input type="text" placeholder="Search projects by name or ID...">
    // Filters   : <button class="ax-seg-btn active">All</button>
    //             <button class="ax-seg-btn">Active</button>
    //             <button class="ax-seg-btn">Completed</button>

    private final By pageComponent   = By.cssSelector("app-projects-list");
    private final By pageTitle       = By.xpath("//h1[contains(@class,'ax-page-title') and contains(normalize-space(),'Projects')]");

    // Project cards — each card is a button.ax-person inside div.ax-card-wrap
    private final By projectCards    = By.cssSelector("div.ax-card-wrap button.ax-person");
    private final By projectNames    = By.cssSelector("div.ax-person-name");
    private final By firstCard       = By.cssSelector("div.ax-card-wrap:first-of-type button.ax-person");

    // Add / New Project button
    private final By addProjectBtn   = By.cssSelector("button.ax-btn.primary");

    // Search inside ax-toolbar
    private final By searchInput     = By.cssSelector("div.ax-search input[type='text']");

    // Filter segment buttons (All / Active / Completed)
    private final By filterAllBtn       = By.xpath("//button[contains(@class,'ax-seg-btn') and normalize-space()='All']");
    private final By filterActiveBtn    = By.xpath("//button[contains(@class,'ax-seg-btn') and normalize-space()='Active']");
    private final By filterCompletedBtn = By.xpath("//button[contains(@class,'ax-seg-btn') and normalize-space()='Completed']");

    // Status dot on card footer: <span class="ax-status-dot ongoing"> Ongoing </span>
    private final By statusDots      = By.cssSelector("span.ax-status-dot");

    // Pagination
    private final By pagerWrap       = By.cssSelector("div.ax-pager-wrap");

    // "Total Projects" summary KPI shown on this page:
    // <div class="ax-kpi">
    //   <span class="ax-kpi-label">Total Projects</span>
    //   <div class="ax-kpi-value">9</div>
    // </div>
    private final By totalProjectsKpi = By.xpath(
        "//div[contains(@class,'ax-kpi')][.//span[contains(@class,'ax-kpi-label')" +
        " and normalize-space()='Total Projects']]//div[contains(@class,'ax-kpi-value')]");

    public ManageProjectsPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("ManageProjectsPage initialized");
    }

    // ── State ─────────────────────────────────────────────────────────────────

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(pageComponent),
                    ExpectedConditions.visibilityOfElementLocated(pageTitle)
            ));
            logger.info("Projects Workspace page is displayed");
            return true;
        } catch (Exception e) {
            logger.warn("Projects page not found: {}", e.getMessage());
            return false;
        }
    }

    // ── Project cards ─────────────────────────────────────────────────────────

    /** Returns all project card buttons visible on the current page. */
    public List<WebElement> getProjectRows() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(projectCards));
        } catch (Exception e) {
            logger.warn("No project cards found");
            return List.of();
        }
    }

    public int getProjectCount() {
        return getProjectRows().size();
    }

    /**
     * Waits for the Add Project dialog to close, then waits until the project
     * card count is greater than {@code countBefore}, then returns the new count.
     */
    public int waitForCountIncrease(int countBefore) {
        // 1. Wait for the dialog backdrop to disappear (dialog fully closed)
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("div.ax-modal-backdrop")));
            logger.info("Dialog closed — waiting for project count to increase");
        } catch (Exception e) {
            logger.warn("Dialog close wait issue: {}", e.getMessage());
        }
        // 2. Wait until card count exceeds countBefore
        try {
            wait.until(d -> d.findElements(projectCards).size() > countBefore);
            logger.info("Project count increased above {}", countBefore);
        } catch (Exception e) {
            logger.warn("Project count did not increase above {} within timeout", countBefore);
        }
        return driver.findElements(projectCards).size();
    }

    /**
     * Reads the "Total Projects" KPI value displayed on the page.
     * Returns -1 if the KPI element is not found or cannot be parsed.
     */
    public int getTotalProjectsCount() {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(totalProjectsKpi));
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

    /**
     * Waits until the "Total Projects" KPI value is greater than {@code countBefore},
     * then returns the new count.  Use this immediately after creating a project.
     */
    public int waitForTotalProjectsCountIncrease(int countBefore) {
        try {
            wait.until(d -> {
                try {
                    WebElement el = d.findElement(totalProjectsKpi);
                    return Integer.parseInt(el.getText().trim()) > countBefore;
                } catch (Exception e) {
                    return false;
                }
            });
            logger.info("Total Projects KPI increased above {}", countBefore);
        } catch (Exception e) {
            logger.warn("Total Projects KPI did not increase above {} within timeout", countBefore);
        }
        return getTotalProjectsCount();
    }

    public boolean isProjectInList(String projectName) {
        try {
            By locator = By.xpath("//div[contains(@class,'ax-person-name') and contains(normalize-space(),'" + projectName + "')]");
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    /** Clicks the "New Project" button and returns the Add Project dialog. */
    public AddProjectDialog clickAddProject() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(addProjectBtn));
        btn.click();
        logger.info("Clicked New Project button");
        return new AddProjectDialog(driver);
    }

    /** Opens a project by name. */
    public EditProjectAdminPage clickProject(String projectName) {
        By locator = By.xpath("//div[contains(@class,'ax-person-name') and contains(normalize-space(),'" + projectName + "')]");
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        el.click();
        logger.info("Clicked project: {}", projectName);
        return new EditProjectAdminPage(driver);
    }

    /** Opens the first project card. */
    public EditProjectAdminPage clickFirstProject() {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(firstCard));
        el.click();
        logger.info("Clicked first project card");
        return new EditProjectAdminPage(driver);
    }

    // ── Search ────────────────────────────────────────────────────────────────

    public ManageProjectsPage searchProject(String query) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
        input.clear();
        input.sendKeys(query);
        logger.info("Searched for project: {}", query);
        return this;
    }

    // ── Filters ───────────────────────────────────────────────────────────────

    public ManageProjectsPage filterAll() {
        click(filterAllBtn, "All");
        return this;
    }

    public ManageProjectsPage filterActive() {
        click(filterActiveBtn, "Active");
        return this;
    }

    public ManageProjectsPage filterCompleted() {
        click(filterCompletedBtn, "Completed");
        return this;
    }

    // ── Workspace view (kept for compatibility) ───────────────────────────────

    public ProjectsWorkspacePage openWorkspaceView() {
        return new ProjectsWorkspacePage(driver);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void click(By locator, String name) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            el.click();
            logger.info("Clicked filter: {}", name);
        } catch (Exception e) {
            logger.warn("Could not click '{}': {}", name, e.getMessage());
        }
    }
}
