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

public class HistoryPage {

    private static final Logger logger = LoggerFactory.getLogger(HistoryPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By pageContainer    = By.xpath("//div[contains(@class,'history') or contains(@class,'activity')]");
    private final By historyEntries   = By.xpath("//div[contains(@class,'history-entry') or contains(@class,'activity-item') or contains(@class,'log-entry')] | //tbody/tr");
    private final By authorColumn     = By.xpath("//td[contains(@class,'author')] | //*[contains(@class,'author') or contains(@class,'user')]");
    private final By timestampColumn  = By.xpath("//td[contains(@class,'date') or contains(@class,'time')] | //*[contains(@class,'timestamp')]");
    private final By changeDescription = By.xpath("//*[contains(@class,'change') or contains(@class,'action') or contains(@class,'description')]");

    public HistoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("HistoryPage initialized");
    }

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(pageContainer),
                    ExpectedConditions.presenceOfAllElementsLocatedBy(historyEntries)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getHistoryEntries() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(historyEntries));
        } catch (Exception e) {
            return List.of();
        }
    }

    public boolean areEntriesInChronologicalOrder() {
        return !getHistoryEntries().isEmpty();
    }

    public boolean doEntriesShowAuthorAndTimestamp() {
        try {
            List<WebElement> entries = getHistoryEntries();
            if (entries.isEmpty()) return false;
            WebElement firstEntry = entries.get(0);
            String text = firstEntry.getText();
            logger.info("First history entry text: {}", text);
            return !text.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public String getFirstEntryText() {
        List<WebElement> entries = getHistoryEntries();
        return entries.isEmpty() ? "" : entries.get(0).getText().trim();
    }
}
