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

public class EditIssueDialog {

    private static final Logger logger = LoggerFactory.getLogger(EditIssueDialog.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators ──────────────────────────────────────────────────────────────
    // Angular reactive forms use formcontrolname. Fall back to name/placeholder.
    private final By dialogContainer    = By.xpath("//div[contains(@class,'modal') or contains(@role,'dialog') or contains(@class,'ax-modal')]");
    private final By titleField         = By.xpath(
        "//input[@formcontrolname='title' or @formcontrolname='summary' or @name='title' or @name='summary'] | " +
        "//h2[@contenteditable='true'] | " +
        "//div[@contenteditable='true'][contains(@class,'title')]");
    private final By statusDropdown     = By.xpath("//select[@formcontrolname='status' or @name='status'] | //select[contains(@id,'status')]");
    private final By typeDropdown       = By.xpath("//select[@formcontrolname='type' or @name='type'] | //select[contains(@id,'type')]");
    private final By assigneeDropdown   = By.xpath("//select[@formcontrolname='assignee' or @name='assignee'] | //select[contains(@id,'assignee')]");
    private final By storyPointsField   = By.xpath("//input[@formcontrolname='storyPoints' or @formcontrolname='points' or @name='storyPoints' or @name='points']");
    private final By sprintDropdown     = By.xpath("//select[@formcontrolname='sprint' or @name='sprint'] | //select[contains(@id,'sprint')]");
    private final By descriptionField   = By.xpath(
        "//textarea[@formcontrolname='description' or @name='description'] | " +
        "//div[@contenteditable='true'][contains(@class,'desc')]");
    // Tab buttons — contains() for Material Icons resilience
    private final By commentsTab        = By.xpath("//button[contains(normalize-space(),'Comments')] | //a[contains(normalize-space(),'Comments')] | //li[contains(normalize-space(),'Comments')]");
    private final By historyTab         = By.xpath("//button[contains(normalize-space(),'History')] | //a[contains(normalize-space(),'History')] | //li[contains(normalize-space(),'History')]");
    // Fix: use separate predicates for @placeholder (not nested)
    private final By commentInput       = By.xpath(
        "//textarea[@formcontrolname='comment' or @name='comment'" +
        "           or contains(@placeholder,'comment') or contains(@placeholder,'Comment')] | " +
        "//input[@formcontrolname='comment' or @name='comment']");
    private final By addCommentBtn      = By.xpath("//button[contains(normalize-space(),'Add Comment') or contains(normalize-space(),'Post') or contains(normalize-space(),'Submit')]");
    private final By saveBtn            = By.xpath(
        "//button[not(contains(@class,'qa-card'))][" +
        "  contains(normalize-space(),'Save') or contains(normalize-space(),'Update')" +
        "][(ancestor::div[contains(@class,'modal') or contains(@class,'ax-modal')])]");
    private final By cancelBtn          = By.xpath("//button[normalize-space()='Cancel' or normalize-space()='Close']");
    private final By historyEntries     = By.xpath("//div[contains(@class,'history-item') or contains(@class,'activity-item')]");
    private final By commentsSection    = By.xpath("//div[contains(@class,'comments') or contains(@class,'comment-list')]");

    public EditIssueDialog(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("EditIssueDialog initialized");
    }

    public boolean isDialogVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(dialogContainer));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPrePopulated() {
        try {
            String title = wait.until(ExpectedConditions.visibilityOfElementLocated(titleField)).getAttribute("value");
            if (title == null) title = driver.findElement(titleField).getText();
            return title != null && !title.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public EditIssueDialog editTitle(String title) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(titleField));
            el.clear();
            el.sendKeys(title);
            logger.info("Edited title to: {}", title);
        } catch (Exception e) {
            logger.warn("Title field not found or not editable");
        }
        return this;
    }

    public EditIssueDialog updateStatus(String status) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(statusDropdown)))
                    .selectByVisibleText(status);
            logger.info("Updated status to: {}", status);
        } catch (Exception e) {
            logger.warn("Status dropdown not found");
        }
        return this;
    }

    public EditIssueDialog updateAssignee(String assignee) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(assigneeDropdown)))
                    .selectByVisibleText(assignee);
        } catch (Exception e) {
            try {
                new Select(driver.findElement(assigneeDropdown)).selectByIndex(1);
            } catch (Exception ex) {
                logger.warn("Assignee dropdown not found");
            }
        }
        return this;
    }

    public EditIssueDialog updateStoryPoints(String points) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(storyPointsField));
            el.clear();
            el.sendKeys(points);
        } catch (Exception e) {
            logger.warn("Story points field not found");
        }
        return this;
    }

    public EditIssueDialog clickCommentsTab() {
        wait.until(ExpectedConditions.elementToBeClickable(commentsTab)).click();
        logger.info("Clicked Comments tab");
        return this;
    }

    public EditIssueDialog clickHistoryTab() {
        wait.until(ExpectedConditions.elementToBeClickable(historyTab)).click();
        logger.info("Clicked History tab");
        return this;
    }

    public EditIssueDialog addComment(String comment) {
        try {
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(commentInput));
            input.clear();
            input.sendKeys(comment);
            wait.until(ExpectedConditions.elementToBeClickable(addCommentBtn)).click();
            logger.info("Added comment: {}", comment);
        } catch (Exception e) {
            logger.warn("Comment input not found: {}", e.getMessage());
        }
        return this;
    }

    public boolean isCommentVisible(String comment) {
        By locator = By.xpath("//*[contains(normalize-space(),'" + comment + "')]");
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areHistoryEntriesVisible() {
        try {
            return !driver.findElements(historyEntries).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public BoardPage saveAndClose() {
        try {
            WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(saveBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Saved issue changes (JS click)");
        } catch (Exception e) {
            logger.warn("Save button not found; pressing Escape to close");
            try { driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE); } catch (Exception ignored) {}
        }
        return new BoardPage(driver);
    }

    public BoardPage cancel() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(cancelBtn)).click();
        } catch (Exception e) {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        }
        logger.info("Cancelled issue edit");
        return new BoardPage(driver);
    }
}
