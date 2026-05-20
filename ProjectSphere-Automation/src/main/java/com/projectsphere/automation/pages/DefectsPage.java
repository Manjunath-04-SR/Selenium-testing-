package com.projectsphere.automation.pages;

import com.projectsphere.automation.constants.AppConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class DefectsPage {

    private static final Logger logger = LoggerFactory.getLogger(DefectsPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By pageContainer      = By.xpath("//div[contains(@class,'defect') or contains(@class,'bug')]");
    private final By defectRows         = By.xpath("//tbody/tr | //div[contains(@class,'defect-row') or contains(@class,'defect-item')]");
    private final By newDefectBtn       = By.xpath("//button[contains(normalize-space(),'New Defect') or contains(normalize-space(),'Raise') or contains(normalize-space(),'Add Defect')]");
    private final By severityFilter     = By.xpath("//select[@formcontrolname='severity' or @name='severity'] | //select[contains(@id,'severity')]");
    private final By statusFilter       = By.xpath("//select[@formcontrolname='status' or @name='status'] | //select[contains(@id,'status')]");
    private final By searchInput        = By.cssSelector("input[placeholder*='search' i], input[type='search']");
    private final By firstDefectRow     = By.xpath("(//tbody/tr | //div[contains(@class,'defect-item')])[1]");
    private final By defectTitleField   = By.xpath(
        "//input[@formcontrolname='title' or @formcontrolname='description'" +
        "        or @name='title' or @name='description'" +
        "        or contains(@placeholder,'title') or contains(@placeholder,'description')" +
        "        or contains(@placeholder,'Title') or contains(@placeholder,'Description')]");
    private final By severitySelect     = By.xpath("//select[@name='severity'] | //select[contains(@id,'severity')]");
    private final By prioritySelect     = By.xpath("//select[@name='priority'] | //select[contains(@id,'priority')]");
    private final By assigneeSelect     = By.xpath("//select[@name='assignee'] | //select[contains(@id,'assignee')]");
    private final By saveDefectBtn      = By.xpath("//button[contains(normalize-space(),'Save') or contains(normalize-space(),'Submit') or contains(normalize-space(),'Report')]");

    public DefectsPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("DefectsPage initialized");
    }

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(pageContainer),
                    ExpectedConditions.presenceOfAllElementsLocatedBy(defectRows)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getDefectRows() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(defectRows));
        } catch (Exception e) {
            return List.of();
        }
    }

    public DefectsPage clickNewDefect() {
        wait.until(ExpectedConditions.elementToBeClickable(newDefectBtn)).click();
        logger.info("Clicked New/Raise Defect");
        return this;
    }

    public DefectsPage fillDefect(String title, String severity, String priority) {
        try {
            WebElement titleEl = wait.until(ExpectedConditions.visibilityOfElementLocated(defectTitleField));
            titleEl.clear();
            titleEl.sendKeys(title);
        } catch (Exception e) {
            logger.warn("Title field not found");
        }
        try {
            new Select(driver.findElement(severitySelect)).selectByVisibleText(severity);
        } catch (Exception e) {
            logger.warn("Severity dropdown not found");
        }
        try {
            new Select(driver.findElement(prioritySelect)).selectByVisibleText(priority);
        } catch (Exception e) {
            logger.warn("Priority dropdown not found");
        }
        return this;
    }

    public DefectsPage assignDefectTo(String assignee) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(assigneeSelect)))
                    .selectByVisibleText(assignee);
            logger.info("Assigned defect to: {}", assignee);
        } catch (Exception e) {
            try {
                new Select(driver.findElement(assigneeSelect)).selectByIndex(1);
            } catch (Exception ex) {
                logger.warn("Assignee dropdown not found");
            }
        }
        return this;
    }

    public DefectsPage saveDefect() {
        wait.until(ExpectedConditions.elementToBeClickable(saveDefectBtn)).click();
        logger.info("Saved new defect");
        return this;
    }

    public DefectDetailPage openFirstDefect() {
        WebElement row = wait.until(ExpectedConditions.elementToBeClickable(firstDefectRow));
        row.click();
        logger.info("Opened first defect");
        return new DefectDetailPage(driver);
    }

    public DefectsPage filterBySeverity(String severity) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(severityFilter)))
                    .selectByVisibleText(severity);
            logger.info("Filtered by severity: {}", severity);
        } catch (Exception e) {
            logger.warn("Severity filter not found");
        }
        return this;
    }

    public DefectsPage filterByStatus(String status) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(statusFilter)))
                    .selectByVisibleText(status);
            logger.info("Filtered by status: {}", status);
        } catch (Exception e) {
            logger.warn("Status filter not found");
        }
        return this;
    }

    public DefectsPage searchDefect(String keyword) {
        try {
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
            input.clear();
            input.sendKeys(keyword);
            logger.info("Searched for defect: {}", keyword);
        } catch (Exception e) {
            logger.warn("Search input not found");
        }
        return this;
    }

    public boolean isDefectInList(String keyword) {
        By locator = By.xpath("//*[contains(normalize-space(),'" + keyword + "')]");
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
