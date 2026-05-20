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
import java.util.List;

public class MyTeamsPMPage {

    private static final Logger logger = LoggerFactory.getLogger(MyTeamsPMPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators — based on real DOM ──────────────────────────────────────────
    // Component  : <app-pm-teams-list> (or similar PM teams component)
    // Team cards : <button class="ax-person pmt-team-card"> — same pattern as admin teams
    // Fallback   : any button.ax-person inside the teams section

    private final By pageComponent   = By.cssSelector("app-pm-teams-list, app-teams-list");
    private final By teamCards       = By.cssSelector("button.ax-person.pmt-team-card, button.ax-person");
    private final By firstTeamCard   = By.cssSelector("button.ax-person.pmt-team-card:first-of-type, div.ax-card-wrap:first-child button.ax-person");

    public MyTeamsPMPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("MyTeamsPMPage initialized");
    }

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(pageComponent),
                    ExpectedConditions.visibilityOfElementLocated(teamCards)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getTeamItems() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(teamCards));
        } catch (Exception e) {
            return List.of();
        }
    }

    public boolean areTeamsListed() {
        return !getTeamItems().isEmpty();
    }

    public EditTeamPage clickFirstTeam() {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(firstTeamCard));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        logger.info("Clicked first team in My Teams");
        return new EditTeamPage(driver);
    }

    public EditTeamPage clickTeam(String teamName) {
        By locator = By.xpath("//button[contains(@class,'ax-person')][contains(normalize-space(),'"
                + teamName + "')]");
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        } catch (Exception e) {
            By broad = By.xpath("//*[contains(normalize-space(),'" + teamName + "')]");
            wait.until(ExpectedConditions.elementToBeClickable(broad)).click();
        }
        logger.info("Clicked team: {}", teamName);
        return new EditTeamPage(driver);
    }
}
