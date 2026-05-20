package com.projectsphere.automation.pages;

import com.projectsphere.automation.constants.AppConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class MyProjectsPMPage {

    private static final Logger logger = LoggerFactory.getLogger(MyProjectsPMPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Actions actions;

    // ── Locators — based on real DOM ──────────────────────────────────────────
    // Component  : <app-pm-projects-list>
    // Grid       : <div class="ax-grid">
    // Card wrap  : <div class="ax-card-wrap">
    // Card button: <button class="ax-person"> (clickable card)
    // Hover btn  : <button class="pmt-workspace-btn"> — "Move to Workspace"

    private final By pageComponent      = By.cssSelector("app-pm-projects-list");
    private final By projectCards       = By.cssSelector("div.ax-card-wrap button.ax-person");
    private final By firstProjectCard   = By.cssSelector("div.ax-grid div.ax-card-wrap:first-child button.ax-person");
    // Move to Workspace button — appears on hover over card wrap
    private final By moveToWorkspaceBtn = By.cssSelector("button.pmt-workspace-btn");
    // Fallback XPath if CSS class differs
    private final By moveToWorkspaceFbk = By.xpath("//button[contains(normalize-space(),'Move to Workspace') or contains(normalize-space(),'Workspace')]");

    public MyProjectsPMPage(WebDriver driver) {
        this.driver  = driver;
        this.wait    = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        this.actions = new Actions(driver);
        logger.info("MyProjectsPMPage initialized");
    }

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(pageComponent),
                    ExpectedConditions.visibilityOfElementLocated(projectCards)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getProjectItems() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(projectCards));
        } catch (Exception e) {
            return List.of();
        }
    }

    public boolean areProjectsListedWithStatus() {
        return !getProjectItems().isEmpty();
    }

    /**
     * Hover over the given project card and check whether the
     * "Move to Workspace" button becomes visible.
     */
    public boolean isMoveToWorkspaceButtonVisible(WebElement projectCard) {
        try {
            // Hover over the card's parent wrap so the hover button is revealed
            WebElement wrap = projectCard.findElement(By.xpath("./ancestor::div[contains(@class,'ax-card-wrap')][1]"));
            actions.moveToElement(wrap).perform();
        } catch (Exception e) {
            actions.moveToElement(projectCard).perform();
        }
        logger.info("Hovered over project card");
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(moveToWorkspaceBtn),
                    ExpectedConditions.visibilityOfElementLocated(moveToWorkspaceFbk)
            ));
            logger.info("Move to Workspace button is visible");
            return true;
        } catch (Exception e) {
            logger.warn("Move to Workspace button not visible on hover: {}", e.getMessage());
            return false;
        }
    }

    public boolean isMoveToWorkspaceVisibleOnHoverFirst() {
        List<WebElement> items = getProjectItems();
        if (!items.isEmpty()) {
            return isMoveToWorkspaceButtonVisible(items.get(0));
        }
        logger.warn("No project cards found on My Projects page");
        return false;
    }

    public BoardPage clickMoveToWorkspaceOnFirstProject() {
        List<WebElement> items = getProjectItems();
        if (!items.isEmpty()) {
            try {
                WebElement wrap = items.get(0).findElement(
                        By.xpath("./ancestor::div[contains(@class,'ax-card-wrap')][1]"));
                actions.moveToElement(wrap).perform();
            } catch (Exception e) {
                actions.moveToElement(items.get(0)).perform();
            }
        }
        logger.info("Hovering to reveal Move to Workspace button");
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(moveToWorkspaceBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Move to Workspace (CSS selector)");
        } catch (Exception e) {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(moveToWorkspaceFbk));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Move to Workspace (XPath fallback)");
        }
        return new BoardPage(driver);
    }

    public EditProjectPMPage clickFirstProject() {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(firstProjectCard));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        logger.info("Clicked first project card in My Projects");
        return new EditProjectPMPage(driver);
    }

    public EditProjectPMPage clickProject(String projectName) {
        By locator = By.xpath("//button[contains(@class,'ax-person')][contains(normalize-space(),'"
                + projectName + "')]");
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        } catch (Exception e) {
            // broad fallback
            By broad = By.xpath("//*[contains(normalize-space(),'" + projectName + "')]");
            wait.until(ExpectedConditions.elementToBeClickable(broad)).click();
        }
        logger.info("Clicked project: {}", projectName);
        return new EditProjectPMPage(driver);
    }

    public boolean isProjectInList(String projectName) {
        try {
            By locator = By.xpath("//*[contains(normalize-space(),'" + projectName + "')]");
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            logger.warn("Project '{}' not found in list", projectName);
            return false;
        }
    }
}
