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

public class ManageTeamsAdminPage {

    private static final Logger logger = LoggerFactory.getLogger(ManageTeamsAdminPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators — based on real DOM ──────────────────────────────────────────
    // Component  : <app-teams-list>
    // Page title : <h1 class="ax-page-title">Teams</h1>
    // Team cards : <button class="ax-person pmt-team-card"> inside div.ax-grid.pmt-teams-grid
    // New Team   : <button class="ax-btn primary"> New Team </button>

    private final By pageComponent = By.cssSelector("app-teams-list");
    private final By pageHeading   = By.cssSelector("h1.ax-page-title");
    private final By teamRows      = By.cssSelector("button.ax-person.pmt-team-card");
    private final By firstTeamLink = By.cssSelector("button.ax-person.pmt-team-card:first-of-type");
    private final By addTeamBtn    = By.cssSelector("button.ax-btn.primary");

    public ManageTeamsAdminPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("ManageTeamsAdminPage initialized");
    }

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(pageComponent),
                    ExpectedConditions.visibilityOfElementLocated(pageHeading)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getTeamRows() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(teamRows));
        } catch (Exception e) {
            return List.of();
        }
    }

    public int getTeamCount() {
        return getTeamRows().size();
    }

    public EditTeamPage clickFirstTeam() {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(firstTeamLink));
        el.click();
        logger.info("Clicked first team");
        return new EditTeamPage(driver);
    }

    public EditTeamPage clickTeam(String teamName) {
        By locator = By.xpath("//tr[contains(.,'" + teamName + "')] | //div[contains(.,'" + teamName + "')]");
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        el.click();
        logger.info("Clicked team: {}", teamName);
        return new EditTeamPage(driver);
    }

    public boolean isTeamInList(String teamName) {
        try {
            By locator = By.xpath("//*[contains(normalize-space(),'" + teamName + "')]");
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTeamAbsent(String teamName) {
        try {
            By locator = By.xpath("//*[normalize-space()='" + teamName + "']");
            List<WebElement> els = driver.findElements(locator);
            return els.isEmpty() || els.stream().noneMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            return true;
        }
    }
}
