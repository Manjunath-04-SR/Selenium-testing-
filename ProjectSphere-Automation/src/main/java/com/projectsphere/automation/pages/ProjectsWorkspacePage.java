package com.projectsphere.automation.pages;

import com.projectsphere.automation.constants.AppConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class ProjectsWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(ProjectsWorkspacePage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Actions actions;

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By pageContainer      = By.xpath("//div[contains(@class,'workspace') or contains(@class,'projects')]");
    private final By projectCards       = By.xpath("//div[contains(@class,'card') or contains(@class,'project-card')]");
    private final By moveToWorkspaceBtn = By.xpath("//button[contains(normalize-space(),'Move to Workspace') or contains(normalize-space(),'Workspace')]");
    private final By searchInput        = By.cssSelector("input[placeholder*='Search' i], input[type='search']");
    private final By filterAllBtn       = By.xpath("//button[normalize-space()='All'] | //span[normalize-space()='All']/parent::button");
    private final By filterActiveBtn    = By.xpath("//button[normalize-space()='Active'] | //span[normalize-space()='Active']/parent::button");
    private final By filterCompletedBtn = By.xpath("//button[normalize-space()='Completed'] | //span[normalize-space()='Completed']/parent::button");
    private final By totalCountDisplay  = By.xpath("//*[contains(normalize-space(),'Total') or contains(normalize-space(),'total')]" +
                                                   "[not(contains(@class,'hidden'))]");
    private final By boardTab           = By.xpath("//a[normalize-space()='Board'] | //button[normalize-space()='Board'] | //li[normalize-space()='Board']");
    private final By backlogTab         = By.xpath("//a[normalize-space()='Backlog'] | //button[normalize-space()='Backlog']");
    private final By timelineTab        = By.xpath("//a[normalize-space()='Timeline'] | //button[normalize-space()='Timeline']");
    private final By defectsTab         = By.xpath("//a[normalize-space()='Defects'] | //button[normalize-space()='Defects']");
    private final By testCasesTab       = By.xpath("//a[normalize-space()='Test Cases'] | //button[normalize-space()='Test Cases']");
    private final By historyTab         = By.xpath("//a[normalize-space()='History'] | //button[normalize-space()='History']");
    private final By analyticsTab       = By.xpath("//a[normalize-space()='Analytics'] | //button[normalize-space()='Analytics']");
    private final By documentsTab       = By.xpath("//a[normalize-space()='Documents'] | //button[normalize-space()='Documents']");
    private final By teamInfoTab        = By.xpath("//a[normalize-space()='Team Info'] | //button[normalize-space()='Team Info']");

    public ProjectsWorkspacePage(WebDriver driver) {
        this.driver  = driver;
        this.wait    = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        this.actions = new Actions(driver);
        logger.info("ProjectsWorkspacePage initialized");
    }

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(projectCards),
                    ExpectedConditions.visibilityOfElementLocated(pageContainer)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getProjectCards() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(projectCards));
        } catch (Exception e) {
            return List.of();
        }
    }

    public BoardPage hoverAndClickMoveToWorkspace(WebElement card) {
        actions.moveToElement(card).perform();
        logger.info("Hovered over project card");
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(moveToWorkspaceBtn));
        btn.click();
        logger.info("Clicked Move to Workspace");
        return new BoardPage(driver);
    }

    public BoardPage hoverFirstCardAndMoveToWorkspace() {
        List<WebElement> cards = getProjectCards();
        if (!cards.isEmpty()) {
            return hoverAndClickMoveToWorkspace(cards.get(0));
        }
        throw new RuntimeException("No project cards found on workspace");
    }

    public ProjectsWorkspacePage searchProject(String query) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
        input.clear();
        input.sendKeys(query);
        logger.info("Searched for: {}", query);
        return this;
    }

    public ProjectsWorkspacePage filterAll() {
        clickSafe(filterAllBtn, "All filter");
        return this;
    }

    public ProjectsWorkspacePage filterActive() {
        clickSafe(filterActiveBtn, "Active filter");
        return this;
    }

    public ProjectsWorkspacePage filterCompleted() {
        clickSafe(filterCompletedBtn, "Completed filter");
        return this;
    }

    public boolean isBoardTabPresent() { return isTabPresent(boardTab, "Board"); }
    public boolean isBacklogTabPresent() { return isTabPresent(backlogTab, "Backlog"); }
    public boolean isTimelineTabPresent() { return isTabPresent(timelineTab, "Timeline"); }
    public boolean isDefectsTabPresent() { return isTabPresent(defectsTab, "Defects"); }
    public boolean isTestCasesTabPresent() { return isTabPresent(testCasesTab, "Test Cases"); }
    public boolean isHistoryTabPresent() { return isTabPresent(historyTab, "History"); }
    public boolean isAnalyticsTabPresent() { return isTabPresent(analyticsTab, "Analytics"); }
    public boolean isDocumentsTabPresent() { return isTabPresent(documentsTab, "Documents"); }
    public boolean isTeamInfoTabPresent() { return isTabPresent(teamInfoTab, "Team Info"); }

    public boolean areAllWorkspaceTabsPresent() {
        return isBoardTabPresent() && isBacklogTabPresent() && isTimelineTabPresent()
                && isDefectsTabPresent() && isTestCasesTabPresent() && isHistoryTabPresent()
                && isAnalyticsTabPresent() && isDocumentsTabPresent() && isTeamInfoTabPresent();
    }

    private boolean isTabPresent(By locator, String tabName) {
        try {
            boolean found = !driver.findElements(locator).isEmpty();
            logger.info("Tab '{}' present: {}", tabName, found);
            return found;
        } catch (Exception e) {
            return false;
        }
    }

    private void clickSafe(By locator, String name) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
            logger.info("Clicked: {}", name);
        } catch (Exception e) {
            logger.warn("Could not click '{}': {}", name, e.getMessage());
        }
    }
}
