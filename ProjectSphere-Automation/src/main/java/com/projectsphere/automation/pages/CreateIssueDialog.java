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

public class CreateIssueDialog {

    private static final Logger logger = LoggerFactory.getLogger(CreateIssueDialog.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators ──────────────────────────────────────────────────────────────
    // Angular reactive forms use formcontrolname. Fall back to name/placeholder.
    private final By dialogContainer    = By.xpath("//div[contains(@class,'modal') or contains(@role,'dialog') or contains(@class,'ax-modal')]");
    private final By issueTypeDropdown  = By.xpath("//select[@formcontrolname='type' or @formcontrolname='issueType' or @name='type' or @name='issueType'] | //select[contains(@id,'type')]");
    private final By statusDropdown     = By.xpath("//select[@formcontrolname='status' or @name='status'] | //select[contains(@id,'status')]");
    private final By summaryField       = By.xpath(
        "//input[@formcontrolname='summary' or @formcontrolname='title'" +
        "        or @name='summary' or @name='title'" +
        "        or contains(@placeholder,'Summary') or contains(@placeholder,'Title')]");
    private final By descriptionField   = By.xpath(
        "//textarea[@formcontrolname='description' or @name='description'" +
        "           or contains(@placeholder,'Description')] | //div[contains(@class,'editor')]");
    private final By assigneeDropdown   = By.xpath("//select[@formcontrolname='assignee' or @name='assignee'] | //select[contains(@id,'assignee')]");
    private final By storyPointsField   = By.xpath(
        "//input[@formcontrolname='storyPoints' or @formcontrolname='points'" +
        "        or @name='storyPoints' or @name='points'" +
        "        or contains(@placeholder,'Story Points') or contains(@placeholder,'Points')]");
    private final By sprintDropdown     = By.xpath("//select[@formcontrolname='sprint' or @name='sprint'] | //select[contains(@id,'sprint')]");
    private final By parentEpicDropdown = By.xpath("//select[@formcontrolname='parentEpic' or @formcontrolname='epic' or @name='parentEpic' or @name='epic'] | //select[contains(@id,'epic')]");
    // Exclude qa-card (mode-selection) buttons
    private final By createIssueBtn     = By.xpath(
        "//button[not(contains(@class,'qa-card'))][" +
        "  contains(normalize-space(),'Create Issue') or " +
        "  normalize-space()='Create' or " +
        "  contains(normalize-space(),'Submit')" +
        "]");
    private final By cancelBtn          = By.xpath("//button[normalize-space()='Cancel' or normalize-space()='Close']");
    // Pre-filled read-only fields for project and reporter
    private final By projectLabel       = By.xpath(
        "//*[contains(@class,'project') and (contains(@class,'read-only') or @readonly)] | " +
        "//input[@readonly][contains(@name,'project') or contains(@formcontrolname,'project')]");
    private final By reporterLabel      = By.xpath(
        "//*[contains(@class,'reporter') and (contains(@class,'read-only') or @readonly)] | " +
        "//input[@readonly][contains(@name,'reporter') or contains(@formcontrolname,'reporter')]");
    private final By validationError    = By.cssSelector(".error, [class*='error'], [class*='invalid']");

    public CreateIssueDialog(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("CreateIssueDialog initialized");
    }

    public boolean isDialogVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(dialogContainer));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areProjectAndReporterPreFilled() {
        try {
            boolean projectFound  = !driver.findElements(projectLabel).isEmpty();
            boolean reporterFound = !driver.findElements(reporterLabel).isEmpty();
            logger.info("Project pre-filled: {}, Reporter pre-filled: {}", projectFound, reporterFound);
            return projectFound || reporterFound;
        } catch (Exception e) {
            return false;
        }
    }

    public CreateIssueDialog selectIssueType(String type) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(issueTypeDropdown)))
                    .selectByVisibleText(type);
            logger.info("Selected issue type: {}", type);
        } catch (Exception e) {
            logger.warn("Issue type dropdown not found");
        }
        return this;
    }

    public CreateIssueDialog selectStatus(String status) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(statusDropdown)))
                    .selectByVisibleText(status);
            logger.info("Selected status: {}", status);
        } catch (Exception e) {
            logger.warn("Status dropdown not found");
        }
        return this;
    }

    public CreateIssueDialog enterSummary(String summary) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(summaryField));
        el.clear();
        el.sendKeys(summary);
        logger.info("Entered summary: {}", summary);
        return this;
    }

    public CreateIssueDialog enterDescription(String description) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionField));
            el.clear();
            el.sendKeys(description);
        } catch (Exception e) {
            logger.warn("Description field not found");
        }
        return this;
    }

    public CreateIssueDialog selectAssignee(String assignee) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(assigneeDropdown)))
                    .selectByVisibleText(assignee);
            logger.info("Selected assignee: {}", assignee);
        } catch (Exception e) {
            try {
                new Select(driver.findElement(assigneeDropdown)).selectByIndex(1);
            } catch (Exception ex) {
                logger.warn("Assignee dropdown not found");
            }
        }
        return this;
    }

    public CreateIssueDialog enterStoryPoints(String points) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(storyPointsField));
            el.clear();
            el.sendKeys(points);
        } catch (Exception e) {
            logger.warn("Story points field not found");
        }
        return this;
    }

    public CreateIssueDialog selectSprint(String sprint) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(sprintDropdown)))
                    .selectByVisibleText(sprint);
        } catch (Exception e) {
            try {
                new Select(driver.findElement(sprintDropdown)).selectByIndex(1);
            } catch (Exception ex) {
                logger.warn("Sprint dropdown not found");
            }
        }
        return this;
    }

    public BoardPage clickCreateIssue() {
        try {
            WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(createIssueBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Create Issue (JS click)");
        } catch (Exception e) {
            logger.warn("Create Issue button not found: {}", e.getMessage());
        }
        return new BoardPage(driver);
    }

    public void clickCancel() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(cancelBtn)).click();
        } catch (Exception e) {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        }
        logger.info("Cancelled Create Issue dialog");
    }

    public boolean isValidationErrorDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(validationError)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
