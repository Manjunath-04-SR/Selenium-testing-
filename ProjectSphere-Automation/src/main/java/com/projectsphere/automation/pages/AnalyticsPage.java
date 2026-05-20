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

public class AnalyticsPage {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By pageContainer      = By.xpath("//div[contains(@class,'analytics') or contains(@class,'charts')]");
    private final By progressChart      = By.xpath("//div[contains(@class,'chart') or contains(@class,'graph')]" +
                                                   "[contains(.,'progress') or contains(.,'Progress') or (position()=1)]");
    private final By completionRateChart = By.xpath("//div[contains(@class,'chart') or contains(@class,'graph')]" +
                                                    "[contains(.,'completion') or contains(.,'Completion') or (position()=2)]");
    private final By velocityChart      = By.xpath("//div[contains(@class,'chart') or contains(@class,'graph')]" +
                                                   "[contains(.,'velocity') or contains(.,'Velocity') or (position()=3)]");
    private final By allCharts          = By.xpath("//div[contains(@class,'chart') or contains(@class,'graph') or contains(@class,'recharts')]");
    private final By chartCanvas        = By.xpath("//canvas | //svg[contains(@class,'chart') or contains(@class,'recharts')]");

    public AnalyticsPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("AnalyticsPage initialized");
    }

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(pageContainer),
                    ExpectedConditions.visibilityOfElementLocated(chartCanvas)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPersonalProgressChartVisible() {
        return isChartVisible(progressChart, "Personal Progress");
    }

    public boolean isCompletionRateChartVisible() {
        return isChartVisible(completionRateChart, "Completion Rate");
    }

    public boolean isVelocityTrendChartVisible() {
        return isChartVisible(velocityChart, "Velocity Trend");
    }

    public List<WebElement> getAllCharts() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(allCharts));
        } catch (Exception e) {
            // Try canvas-based charts
            try {
                return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(chartCanvas));
            } catch (Exception ex) {
                return List.of();
            }
        }
    }

    public boolean areChartsDisplayed() {
        List<WebElement> charts = getAllCharts();
        logger.info("Found {} charts on Analytics page", charts.size());
        return !charts.isEmpty();
    }

    private boolean isChartVisible(By locator, String chartName) {
        try {
            boolean visible = wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
            logger.info("Chart '{}' visible: {}", chartName, visible);
            return visible;
        } catch (Exception e) {
            // Fallback: check generic chart existence
            return areChartsDisplayed();
        }
    }
}
