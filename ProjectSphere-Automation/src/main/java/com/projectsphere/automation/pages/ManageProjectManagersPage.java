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

public class ManageProjectManagersPage {

    private static final Logger logger = LoggerFactory.getLogger(ManageProjectManagersPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators — based on real DOM ──────────────────────────────────────────
    // Page component  : <app-pm-list> or similar
    // "New Manager"   : <button class="ax-btn primary"> New Manager </button>
    // PM cards        : same ax-card-wrap / ax-person pattern as projects

    private final By pageHeading  = By.xpath(
        "//h1[contains(@class,'ax-page-title') and contains(normalize-space(),'Manager')]");
    private final By pageComponent = By.cssSelector("app-pm-list, app-project-manager-list");

    // "New Manager" primary button
    private final By addPMBtn     = By.cssSelector("button.ax-btn.primary");

    // PM cards — inside div.ax-grid (NOT div.ax-card-wrap like projects)
    // <div class="ax-grid"><button class="ax-person">...</button></div>
    private final By pmCards      = By.cssSelector("div.ax-grid button.ax-person");
    private final By firstPMCard  = By.cssSelector("div.ax-grid button.ax-person:first-of-type");

    public ManageProjectManagersPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("ManageProjectManagersPage initialized");
    }

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(pageComponent),
                    ExpectedConditions.visibilityOfElementLocated(pageHeading),
                    ExpectedConditions.visibilityOfElementLocated(addPMBtn)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getPMRows() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(pmCards));
        } catch (Exception e) {
            return List.of();
        }
    }

    /** Clicks the "New Manager" button and returns the Add PM dialog. */
    public AddProjectManagerDialog clickAddProjectManager() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(addPMBtn));
        btn.click();
        logger.info("Clicked New Manager button");
        return new AddProjectManagerDialog(driver);
    }

    public EditProjectManagerPage clickFirstPM() {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(firstPMCard));
        el.click();
        logger.info("Clicked first PM card");
        return new EditProjectManagerPage(driver);
    }

    public EditProjectManagerPage clickPM(String pmName) {
        By locator = By.xpath(
            "//div[contains(@class,'ax-person-name') and contains(normalize-space(),'" + pmName + "')]");
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        el.click();
        logger.info("Clicked PM: {}", pmName);
        return new EditProjectManagerPage(driver);
    }

    public boolean isPMInList(String pmName) {
        try {
            By locator = By.xpath(
                "//div[contains(@class,'ax-person-name') and contains(normalize-space(),'" + pmName + "')]");
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
