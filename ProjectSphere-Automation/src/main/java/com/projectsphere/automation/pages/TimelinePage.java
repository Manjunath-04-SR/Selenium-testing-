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

public class TimelinePage {

    private static final Logger logger = LoggerFactory.getLogger(TimelinePage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By timelineContainer  = By.xpath("//div[contains(@class,'timeline') or contains(@class,'gantt')]");
    private final By timelineBars       = By.xpath("//div[contains(@class,'bar') or contains(@class,'task-bar') or contains(@class,'gantt-bar')]");
    private final By dateHeaders        = By.xpath("//div[contains(@class,'date-header') or contains(@class,'timeline-header')]//span | //thead//th");
    private final By workItemRows       = By.xpath("//div[contains(@class,'timeline-row') or contains(@class,'gantt-row')] | //tbody/tr");

    public TimelinePage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("TimelinePage initialized");
    }

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(timelineContainer),
                    ExpectedConditions.visibilityOfElementLocated(timelineBars)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areWorkItemBarsDisplayed() {
        try {
            List<WebElement> bars = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(timelineBars));
            logger.info("Found {} timeline bars", bars.size());
            return !bars.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areDateHeadersDisplayed() {
        try {
            return !driver.findElements(dateHeaders).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areOverlappingItemsVisible() {
        try {
            List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(workItemRows));
            return rows.size() > 1;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getWorkItemRows() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(workItemRows));
        } catch (Exception e) {
            return List.of();
        }
    }
}
