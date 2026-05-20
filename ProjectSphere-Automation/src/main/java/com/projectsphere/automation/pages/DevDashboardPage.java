package com.projectsphere.automation.pages;

import com.projectsphere.automation.constants.AppConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class DevDashboardPage {

    private static final Logger logger = LoggerFactory.getLogger(DevDashboardPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By dashboardContainer  = By.xpath("//div[contains(@class,'dashboard') or contains(@class,'developer')]");
    private final By workItemCards       = By.xpath("//div[contains(@class,'work-item') or contains(@class,'task-card') or contains(@class,'issue-card')]");
    private final By progressIndicator   = By.xpath("//*[contains(@class,'progress') or contains(@class,'progress-bar')]");
    private final By pendingActionsCount = By.xpath("//*[contains(normalize-space(),'Pending') and (contains(@class,'count') or contains(@class,'badge'))]");
    // Use contains() to tolerate Material Icons text prefix (e.g. "view_kanban Board")
    private final By boardShortcut       = By.xpath("//a[contains(normalize-space(),'Board')] | //button[contains(normalize-space(),'Board')]");
    private final By defectsShortcut     = By.xpath("//a[contains(normalize-space(),'Defects')] | //button[contains(normalize-space(),'Defects')]");
    private final By testCasesShortcut   = By.xpath("//a[contains(normalize-space(),'Test Cases')] | //button[contains(normalize-space(),'Test Cases')]");
    private final By documentsShortcut   = By.xpath("//a[contains(normalize-space(),'Documents')] | //button[contains(normalize-space(),'Documents')]");
    private final By welcomeHeading      = By.xpath("//h1 | //h2[contains(normalize-space(),'Dashboard') or contains(normalize-space(),'Welcome')]");

    public DevDashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("DevDashboardPage initialized");
    }

    public boolean isDashboardDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(dashboardContainer),
                    ExpectedConditions.visibilityOfElementLocated(welcomeHeading)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areWorkItemsVisible() {
        try {
            return !driver.findElements(workItemCards).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isProgressIndicatorVisible() {
        try {
            return driver.findElement(progressIndicator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public BoardPage clickBoardShortcut() {
        wait.until(ExpectedConditions.elementToBeClickable(boardShortcut)).click();
        logger.info("Clicked Board shortcut from Dev Dashboard");
        return new BoardPage(driver);
    }

    public DefectsPage clickDefectsShortcut() {
        wait.until(ExpectedConditions.elementToBeClickable(defectsShortcut)).click();
        logger.info("Clicked Defects shortcut");
        return new DefectsPage(driver);
    }

    public TestCasesPage clickTestCasesShortcut() {
        wait.until(ExpectedConditions.elementToBeClickable(testCasesShortcut)).click();
        logger.info("Clicked Test Cases shortcut");
        return new TestCasesPage(driver);
    }

    public DocumentsPage clickDocumentsShortcut() {
        wait.until(ExpectedConditions.elementToBeClickable(documentsShortcut)).click();
        logger.info("Clicked Documents shortcut");
        return new DocumentsPage(driver);
    }

    // ── Navigation methods ────────────────────────────────────────────────────

    public BacklogPage navigateToBacklog() {
        By backlogLink = By.xpath("//a[contains(normalize-space(),'Backlog')] | //button[contains(normalize-space(),'Backlog')]");
        wait.until(ExpectedConditions.elementToBeClickable(backlogLink)).click();
        logger.info("Navigated to Backlog");
        return new BacklogPage(driver);
    }

    public TimelinePage navigateToTimeline() {
        By timelineLink = By.xpath("//a[contains(normalize-space(),'Timeline')] | //button[contains(normalize-space(),'Timeline')]");
        wait.until(ExpectedConditions.elementToBeClickable(timelineLink)).click();
        logger.info("Navigated to Timeline");
        return new TimelinePage(driver);
    }

    public HistoryPage navigateToHistory() {
        By historyLink = By.xpath("//a[contains(normalize-space(),'History')] | //button[contains(normalize-space(),'History')]");
        wait.until(ExpectedConditions.elementToBeClickable(historyLink)).click();
        logger.info("Navigated to History");
        return new HistoryPage(driver);
    }

    public AnalyticsPage navigateToAnalytics() {
        By analyticsLink = By.xpath("//a[contains(normalize-space(),'Analytics')] | //button[contains(normalize-space(),'Analytics')]");
        wait.until(ExpectedConditions.elementToBeClickable(analyticsLink)).click();
        logger.info("Navigated to Analytics");
        return new AnalyticsPage(driver);
    }

    public DocumentsPage navigateToDocuments() {
        By documentsLink = By.xpath("//a[contains(normalize-space(),'Documents')] | //button[contains(normalize-space(),'Documents')]");
        wait.until(ExpectedConditions.elementToBeClickable(documentsLink)).click();
        logger.info("Navigated to Documents");
        return new DocumentsPage(driver);
    }

    public TeamInfoPage navigateToTeamInfo() {
        By teamInfoLink = By.xpath("//a[contains(normalize-space(),'Team Info')] | //button[contains(normalize-space(),'Team Info')]");
        wait.until(ExpectedConditions.elementToBeClickable(teamInfoLink)).click();
        logger.info("Navigated to Team Info");
        return new TeamInfoPage(driver);
    }

    public LoginPage logout() {
        By logoutBtn = By.xpath("//button[contains(normalize-space(),'Logout')] | //a[contains(normalize-space(),'Logout')]");
        wait.until(ExpectedConditions.elementToBeClickable(logoutBtn)).click();
        logger.info("Logged out from Dev Dashboard");
        return new LoginPage(driver);
    }
}
