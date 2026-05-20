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

public class TestCasesPage {

    private static final Logger logger = LoggerFactory.getLogger(TestCasesPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By pageContainer      = By.xpath("//div[contains(@class,'test-case') or contains(@class,'testcase')]");
    private final By testCaseRows       = By.xpath("//tbody/tr | //div[contains(@class,'test-case-row') or contains(@class,'tc-item')]");
    private final By userStoryTiles     = By.xpath("//div[contains(@class,'user-story') or contains(@class,'story-tile')]");
    private final By createTCBtn        = By.xpath("//button[contains(normalize-space(),'Create Test Case') or contains(normalize-space(),'New Test Case') or contains(normalize-space(),'+ Test')]");
    private final By moduleFilter       = By.xpath("//select[@name='module'] | //select[contains(@id,'module')]");
    private final By statusFilter       = By.xpath("//select[@name='status'] | //select[contains(@id,'status')]");
    private final By searchInput        = By.cssSelector("input[placeholder*='search' i], input[type='search']");
    private final By firstTCRow         = By.xpath("(//tbody/tr | //div[contains(@class,'tc-item')])[1]");
    private final By firstUserStoryTile = By.xpath("(//div[contains(@class,'user-story') or contains(@class,'story-tile')])[1]");
    private final By markPassedBtn      = By.xpath("//button[contains(normalize-space(),'Pass') or contains(normalize-space(),'Passed')]");
    private final By markFailedBtn      = By.xpath("//button[contains(normalize-space(),'Fail') or contains(normalize-space(),'Failed')]");
    private final By markBlockedBtn     = By.xpath("//button[contains(normalize-space(),'Block') or contains(normalize-space(),'Blocked')]");

    public TestCasesPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("TestCasesPage initialized");
    }

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(pageContainer),
                    ExpectedConditions.visibilityOfElementLocated(createTCBtn)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getTestCaseRows() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(testCaseRows));
        } catch (Exception e) {
            return List.of();
        }
    }

    public CreateTestCaseDialog clickCreateTestCase() {
        wait.until(ExpectedConditions.elementToBeClickable(createTCBtn)).click();
        logger.info("Clicked Create Test Case");
        return new CreateTestCaseDialog(driver);
    }

    public UserStoryDetailDialog clickFirstUserStoryTile() {
        WebElement tile = wait.until(ExpectedConditions.elementToBeClickable(firstUserStoryTile));
        tile.click();
        logger.info("Clicked first user story tile");
        return new UserStoryDetailDialog(driver);
    }

    public TestCasesPage clickEditFirstTestCase() {
        By editBtn = By.xpath("(//button[contains(@class,'edit') or contains(normalize-space(),'Edit')])[1]");
        try {
            wait.until(ExpectedConditions.elementToBeClickable(editBtn)).click();
        } catch (Exception e) {
            wait.until(ExpectedConditions.elementToBeClickable(firstTCRow)).click();
        }
        logger.info("Clicked edit on first test case");
        return this;
    }

    public TestCasesPage markFirstTestCasePassed() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(markPassedBtn)).click();
            logger.info("Marked test case as Passed");
        } catch (Exception e) {
            logger.warn("Mark Passed button not found");
        }
        return this;
    }

    public TestCasesPage markFirstTestCaseFailed() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(markFailedBtn)).click();
            logger.info("Marked test case as Failed");
        } catch (Exception e) {
            logger.warn("Mark Failed button not found");
        }
        return this;
    }

    public TestCasesPage filterByModule(String module) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(moduleFilter)))
                    .selectByVisibleText(module);
            logger.info("Filtered by module: {}", module);
        } catch (Exception e) {
            logger.warn("Module filter not found");
        }
        return this;
    }

    public boolean isTestCaseInList(String keyword) {
        By locator = By.xpath("//*[contains(normalize-space(),'" + keyword + "')]");
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
