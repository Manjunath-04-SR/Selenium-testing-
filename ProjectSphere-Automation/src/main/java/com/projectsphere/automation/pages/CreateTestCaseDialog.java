package com.projectsphere.automation.pages;

import com.projectsphere.automation.constants.AppConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class CreateTestCaseDialog {

    private static final Logger logger = LoggerFactory.getLogger(CreateTestCaseDialog.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators ──────────────────────────────────────────────────────────────
    // Angular reactive forms use formcontrolname. Fall back to name/placeholder.
    private final By dialogContainer      = By.xpath("//div[contains(@class,'modal') or contains(@role,'dialog') or contains(@class,'ax-modal')]");
    private final By descriptionField     = By.xpath(
        "//textarea[@formcontrolname='description' or @formcontrolname='testDescription'" +
        "           or @name='description' or @name='testDescription'" +
        "           or contains(@placeholder,'Description')] | " +
        "//div[@contenteditable='true'][contains(@class,'desc')]");
    private final By testTypeDropdown     = By.xpath("//select[@formcontrolname='testType' or @formcontrolname='type' or @name='testType' or @name='type'] | //select[contains(@id,'testType') or contains(@id,'type')]");
    private final By complexityDropdown   = By.xpath("//select[@formcontrolname='complexity' or @name='complexity'] | //select[contains(@id,'complexity')]");
    // Fix: use separate predicates for @placeholder (not nested)
    private final By testDataField        = By.xpath(
        "//input[@formcontrolname='testData' or @name='testData'" +
        "        or contains(@placeholder,'Test Data') or contains(@placeholder,'test data')] | " +
        "//textarea[@formcontrolname='testData' or @name='testData']");
    private final By expectedResultField  = By.xpath(
        "//textarea[@formcontrolname='expectedResult' or @formcontrolname='expected'" +
        "           or @name='expectedResult' or @name='expected'" +
        "           or contains(@placeholder,'Expected')] | " +
        "//input[@formcontrolname='expectedResult' or @name='expectedResult'] | " +
        "//div[@contenteditable='true'][contains(@class,'expected')]");
    // Fix: use separate predicates for @placeholder (not nested)
    private final By userStorySearchInput = By.xpath(
        "//input[@formcontrolname='userStory' or @formcontrolname='storySearch'" +
        "        or @name='userStory' or @name='storySearch'" +
        "        or contains(@placeholder,'story') or contains(@placeholder,'Story') or contains(@placeholder,'user story')]");
    private final By userStoryDropdown    = By.xpath("//select[@formcontrolname='userStory' or @formcontrolname='userStoryId' or @name='userStory' or @name='userStoryId'] | //select[contains(@id,'userStory') or contains(@id,'story')]");
    private final By userStoryOptions     = By.xpath("//div[contains(@class,'story-option') or contains(@class,'user-story-item')] | " +
                                                     "//li[contains(@class,'story') or contains(@class,'user-story')]");
    private final By linkedStoriesSection = By.xpath("//div[contains(@class,'linked-stories') or contains(@class,'selected-stories')]");
    // Exclude qa-card (mode-selection) buttons
    private final By createTestCaseBtn    = By.xpath(
        "//button[not(contains(@class,'qa-card'))][" +
        "  contains(normalize-space(),'Create Test Case') or " +
        "  normalize-space()='Create' or " +
        "  contains(normalize-space(),'Save')" +
        "]");
    private final By cancelBtn            = By.xpath("//button[normalize-space()='Cancel' or normalize-space()='Close']");
    private final By validationError      = By.cssSelector(".error, [class*='error'], [class*='invalid'], [class*='required']");

    public CreateTestCaseDialog(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("CreateTestCaseDialog initialized");
    }

    public boolean isDialogVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(dialogContainer));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public CreateTestCaseDialog enterDescription(String description) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionField));
            el.clear();
            el.sendKeys(description);
            logger.info("Entered description: {}", description);
        } catch (Exception e) {
            logger.warn("Description field not found");
        }
        return this;
    }

    public CreateTestCaseDialog selectTestType(String testType) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(testTypeDropdown)))
                    .selectByVisibleText(testType);
            logger.info("Selected test type: {}", testType);
        } catch (Exception e) {
            logger.warn("Test type dropdown not found");
        }
        return this;
    }

    public CreateTestCaseDialog selectComplexity(String complexity) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(complexityDropdown)))
                    .selectByVisibleText(complexity);
            logger.info("Selected complexity: {}", complexity);
        } catch (Exception e) {
            logger.warn("Complexity dropdown not found");
        }
        return this;
    }

    public CreateTestCaseDialog enterTestData(String testData) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(testDataField));
            el.clear();
            el.sendKeys(testData);
            logger.info("Entered test data: {}", testData);
        } catch (Exception e) {
            logger.warn("Test data field not found");
        }
        return this;
    }

    public CreateTestCaseDialog enterExpectedResult(String expectedResult) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(expectedResultField));
            el.clear();
            el.sendKeys(expectedResult);
            logger.info("Entered expected result: {}", expectedResult);
        } catch (Exception e) {
            logger.warn("Expected result field not found");
        }
        return this;
    }

    public CreateTestCaseDialog linkUserStory(String storyName) {
        try {
            // Try dropdown first
            List<WebElement> dropdowns = driver.findElements(userStoryDropdown);
            if (!dropdowns.isEmpty()) {
                new Select(dropdowns.get(0)).selectByVisibleText(storyName);
                logger.info("Linked user story via dropdown: {}", storyName);
                return this;
            }
            // Fall back to search input + option click
            WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(userStorySearchInput));
            searchInput.clear();
            searchInput.sendKeys(storyName);
            List<WebElement> options = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(userStoryOptions));
            if (!options.isEmpty()) {
                options.get(0).click();
                logger.info("Linked user story via search: {}", storyName);
            }
        } catch (Exception e) {
            logger.warn("User story linking not available: {}", e.getMessage());
        }
        return this;
    }

    public boolean isUserStoryLinked() {
        try {
            return !driver.findElements(linkedStoriesSection).isEmpty() &&
                   driver.findElement(linkedStoriesSection).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public TestCasesPage clickCreateTestCase() {
        try {
            WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(createTestCaseBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Create Test Case (JS click)");
        } catch (Exception e) {
            logger.warn("Create Test Case button not found: {}", e.getMessage());
        }
        return new TestCasesPage(driver);
    }

    public TestCasesPage clickCancel() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(cancelBtn)).click();
        } catch (Exception e) {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        }
        logger.info("Cancelled Create Test Case dialog");
        return new TestCasesPage(driver);
    }

    public boolean isValidationErrorDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(validationError)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
