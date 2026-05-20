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

public class TeamInfoPage {

    private static final Logger logger = LoggerFactory.getLogger(TeamInfoPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By pageContainer  = By.xpath("//div[contains(@class,'team-info') or contains(@class,'team-detail')]");
    private final By memberCards    = By.xpath("//div[contains(@class,'member-card') or contains(@class,'member-item')] | //tbody/tr");
    private final By memberRoles    = By.xpath("//*[contains(@class,'role') or contains(@class,'Role')]");
    private final By memberContacts = By.xpath("//*[contains(@class,'contact') or contains(@class,'email') or contains(@type,'email')]");
    private final By memberNames    = By.xpath("//*[contains(@class,'member-name') or contains(@class,'name')]");

    public TeamInfoPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("TeamInfoPage initialized");
    }

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(pageContainer),
                    ExpectedConditions.presenceOfAllElementsLocatedBy(memberCards)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getMemberCards() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(memberCards));
        } catch (Exception e) {
            return List.of();
        }
    }

    public boolean areMembersListed() {
        return !getMemberCards().isEmpty();
    }

    public boolean doMembersHaveRoles() {
        try {
            List<WebElement> roles = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(memberRoles));
            boolean visible = roles.stream().anyMatch(WebElement::isDisplayed);
            logger.info("Member roles visible: {}", visible);
            return visible;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean doMembersHaveContactInfo() {
        try {
            List<WebElement> contacts = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(memberContacts));
            boolean visible = contacts.stream().anyMatch(WebElement::isDisplayed);
            logger.info("Member contacts visible: {}", visible);
            return visible;
        } catch (Exception e) {
            return false;
        }
    }
}
