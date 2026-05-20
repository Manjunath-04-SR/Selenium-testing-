package com.projectsphere.automation.pages;

import com.projectsphere.automation.constants.AppConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class PMDashboardPage {

    private static final Logger logger = LoggerFactory.getLogger(PMDashboardPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators — based on real DOM ──────────────────────────────────────────
    // Component  : <app-dashboard-pm>
    // KPI tiles  : <button class="ax-kpi"> — My Projects, Ongoing, Planned, Completed, Teams
    // Quick acts : <button class="qa-card"> — New Project, New Team, My Projects
    // Charts     : <div class="charts-grid"> with <div class="chart-card">

    private final By dashboardComponent  = By.cssSelector("app-dashboard-pm");
    private final By pageHeading         = By.cssSelector("h1.ax-page-title");
    private final By kpiTiles            = By.cssSelector("app-dashboard-pm button.ax-kpi");
    private final By kpiValues           = By.cssSelector("app-dashboard-pm .ax-kpi-value");
    private final By chartsGrid          = By.cssSelector("div.charts-grid");
    private final By quickActionCards    = By.cssSelector("button.qa-card");
    // KPI tile navigation buttons
    private final By myProjectsKpi       = By.xpath("//button[contains(@class,'ax-kpi')][.//span[contains(@class,'ax-kpi-label') and normalize-space()='My Projects']]");
    private final By teamsKpi            = By.xpath("//button[contains(@class,'ax-kpi')][.//span[contains(@class,'ax-kpi-label') and normalize-space()='Teams']]");
    // Quick action cards
    private final By addProjectBtn       = By.xpath("//button[contains(@class,'qa-card')][.//span[contains(@class,'qa-title') and normalize-space()='New Project']]");
    private final By addTeamBtn          = By.xpath("//button[contains(@class,'qa-card')][.//span[contains(@class,'qa-title') and normalize-space()='New Team']]");
    private final By myProjectsQABtn     = By.xpath("//button[contains(@class,'qa-card')][.//span[contains(@class,'qa-title') and normalize-space()='My Projects']]");
    // Logout — top-right user menu button
    private final By logoutBtn           = By.xpath("//button[contains(normalize-space(),'Logout') or contains(normalize-space(),'Sign Out') or contains(normalize-space(),'Log Out')]");

    public PMDashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("PMDashboardPage initialized");
    }

    public boolean isDashboardDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(dashboardComponent),
                    ExpectedConditions.visibilityOfElementLocated(pageHeading)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areProjectTilesVisible() {
        try {
            return !wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(kpiTiles)).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areHealthIndicatorsVisible() {
        try {
            // Check KPI values are present (Ongoing, Planned, Teams, etc.)
            return !driver.findElements(kpiValues).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public MyProjectsPMPage navigateToMyProjects() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(myProjectsKpi));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Navigated to My Projects via KPI tile");
        } catch (Exception e) {
            // fallback: quick-action card
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(myProjectsQABtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Navigated to My Projects via quick-action card");
        }
        return new MyProjectsPMPage(driver);
    }

    public MyTeamsPMPage navigateToMyTeams() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(teamsKpi));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Navigated to My Teams via KPI tile");
        } catch (Exception e) {
            logger.warn("Teams KPI tile not found: {}", e.getMessage());
        }
        return new MyTeamsPMPage(driver);
    }

    public CreateProjectDialog clickAddProject() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(addProjectBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked New Project qa-card on PM Dashboard");
        } catch (Exception e) {
            logger.warn("Add Project button not found: {}", e.getMessage());
        }
        return new CreateProjectDialog(driver);
    }

    public CreateTeamDialog clickAddTeam() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(addTeamBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked New Team qa-card on PM Dashboard");
        } catch (Exception e) {
            logger.warn("Add Team button not found: {}", e.getMessage());
        }
        return new CreateTeamDialog(driver);
    }

    public LoginPage logout() {
        wait.until(ExpectedConditions.elementToBeClickable(logoutBtn)).click();
        logger.info("Logged out from PM Dashboard");
        return new LoginPage(driver);
    }

    public MyProjectsPMPage clickProjectFromDashboard(String projectName) {
        By locator = By.xpath("//*[contains(normalize-space(),'" + projectName + "')]" +
                              "[(ancestor::div[contains(@class,'card') or contains(@class,'tile')])]");
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        logger.info("Clicked project '{}' from dashboard", projectName);
        return new MyProjectsPMPage(driver);
    }
}
