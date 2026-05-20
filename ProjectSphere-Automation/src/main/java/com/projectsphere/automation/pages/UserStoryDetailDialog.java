package com.projectsphere.automation.pages;

import com.projectsphere.automation.constants.AppConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class UserStoryDetailDialog {

    private static final Logger logger = LoggerFactory.getLogger(UserStoryDetailDialog.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By dialogContainer  = By.xpath("//div[contains(@class,'modal') or contains(@role,'dialog')]");
    private final By storyIdField     = By.xpath("//*[contains(@class,'story-id') or contains(normalize-space(),'Story ID')]" +
                                                 "| //*[@label='Story ID' or contains(@data-field,'storyId')]");
    private final By typeField        = By.xpath("//*[contains(@class,'type') or contains(normalize-space(),'Type')]");
    private final By statusField      = By.xpath("//*[contains(@class,'status') or contains(normalize-space(),'Status')]");
    private final By storyPointsField = By.xpath("//*[contains(@class,'points') or contains(normalize-space(),'Story Points')]");
    private final By descriptionField = By.xpath("//*[contains(@class,'description') or contains(normalize-space(),'Description')]" +
                                                 "[not(contains(@class,'input') or contains(@class,'field'))]");
    private final By closeBtn         = By.xpath("//button[normalize-space()='Close' or @aria-label='Close'] | //button[normalize-space()='×']");
    private final By metaSection      = By.xpath("//div[contains(@class,'meta') or contains(@class,'detail')]");

    public UserStoryDetailDialog(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("UserStoryDetailDialog initialized");
    }

    public boolean isDialogOpen() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(dialogContainer));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isStoryIdDisplayed() {
        return isElementPresent(storyIdField, "Story ID");
    }

    public boolean isTypeDisplayed() {
        return isElementPresent(typeField, "Type");
    }

    public boolean isStatusDisplayed() {
        return isElementPresent(statusField, "Status");
    }

    public boolean isStoryPointsDisplayed() {
        return isElementPresent(storyPointsField, "Story Points");
    }

    public boolean isDescriptionDisplayed() {
        return isElementPresent(descriptionField, "Description");
    }

    public boolean areAllMetadataFieldsDisplayed() {
        boolean id      = isStoryIdDisplayed();
        boolean type    = isTypeDisplayed();
        boolean status  = isStatusDisplayed();
        boolean points  = isStoryPointsDisplayed();
        logger.info("Story metadata — id:{} type:{} status:{} points:{}", id, type, status, points);
        return id && type && status && points;
    }

    public String getDescriptionText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionField)).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public TestCasesPage close() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(closeBtn)).click();
        } catch (Exception e) {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        }
        logger.info("Closed User Story Detail dialog");
        return new TestCasesPage(driver);
    }

    private boolean isElementPresent(By locator, String name) {
        try {
            boolean found = !driver.findElements(locator).isEmpty() &&
                            driver.findElements(locator).stream().anyMatch(e -> e.isDisplayed() && !e.getText().isEmpty());
            logger.info("Field '{}' present: {}", name, found);
            return found;
        } catch (Exception e) {
            return false;
        }
    }
}
