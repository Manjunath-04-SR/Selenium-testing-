package com.projectsphere.automation.pages;

import com.projectsphere.automation.constants.AppConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class BoardPage {

    private static final Logger logger = LoggerFactory.getLogger(BoardPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Actions actions;

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By boardContainer      = By.xpath("//div[contains(@class,'board') or contains(@class,'kanban')]");
    private final By toDoColumn          = By.xpath("//div[contains(@class,'column') or contains(@class,'col')][contains(.,'To Do') or contains(.,'TODO') or contains(.,'todo')]");
    private final By inProgressColumn    = By.xpath("//div[contains(@class,'column') or contains(@class,'col')][contains(.,'In Progress') or contains(.,'InProgress')]");
    private final By doneColumn          = By.xpath("//div[contains(@class,'column') or contains(@class,'col')][contains(.,'Done') or contains(.,'DONE')]");
    private final By allCards            = By.xpath("//div[contains(@class,'card') or contains(@class,'issue-card') or contains(@class,'task-card') or contains(@class,'story-card')]");
    private final By createIssueBtn      = By.xpath("//button[contains(normalize-space(),'Create Issue') or contains(normalize-space(),'New Issue') or contains(normalize-space(),'+ Issue')]");
    private final By reportDefectBtn     = By.xpath("//button[contains(normalize-space(),'Report Defect') or contains(normalize-space(),'New Defect')]");
    private final By filterByMemberInput = By.xpath("//select[@name='assignee'] | //input[@placeholder[contains(.,'filter') or contains(.,'Filter') or contains(.,'member')]]");
    private final By filterDropdown      = By.xpath("//select[contains(@id,'filter') or contains(@name,'filter')]");
    private final By clearFilterBtn      = By.xpath("//button[contains(normalize-space(),'Clear') or contains(normalize-space(),'Reset')]");
    private final By firstCard           = By.xpath("(//div[contains(@class,'card') or contains(@class,'issue-card')])[1]");

    public BoardPage(WebDriver driver) {
        this.driver  = driver;
        this.wait    = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        this.actions = new Actions(driver);
        logger.info("BoardPage initialized");
    }

    public boolean isBoardDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(boardContainer),
                    ExpectedConditions.visibilityOfElementLocated(toDoColumn)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areCardsInStatusColumns() {
        try {
            List<WebElement> cards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(allCards));
            logger.info("Found {} cards on board", cards.size());
            return !cards.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getAllCards() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(allCards));
        } catch (Exception e) {
            return List.of();
        }
    }

    public EditIssueDialog openFirstCard() {
        WebElement card = wait.until(ExpectedConditions.elementToBeClickable(firstCard));
        card.click();
        logger.info("Opened first card on Board");
        return new EditIssueDialog(driver);
    }

    public EditIssueDialog openCard(String cardTitle) {
        By locator = By.xpath("//div[contains(@class,'card')][contains(.,'" + cardTitle + "')]");
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        logger.info("Opened card: {}", cardTitle);
        return new EditIssueDialog(driver);
    }

    public BoardPage dragCardToColumn(WebElement card, WebElement targetColumn) {
        actions.dragAndDrop(card, targetColumn).perform();
        logger.info("Dragged card to column");
        return this;
    }

    public BoardPage moveFirstTodoCardToInProgress() {
        try {
            By todoCard = By.xpath("(//div[contains(@class,'card')][ancestor::div[contains(.,'To Do')]])[1]");
            WebElement card = wait.until(ExpectedConditions.visibilityOfElementLocated(todoCard));
            WebElement inProgCol = wait.until(ExpectedConditions.visibilityOfElementLocated(inProgressColumn));
            actions.dragAndDrop(card, inProgCol).perform();
            logger.info("Moved card from To Do to In Progress");
        } catch (Exception e) {
            logger.warn("Could not drag card: {}. Trying click-based status change.", e.getMessage());
        }
        return this;
    }

    public CreateIssueDialog clickCreateIssue() {
        wait.until(ExpectedConditions.elementToBeClickable(createIssueBtn)).click();
        logger.info("Clicked Create Issue");
        return new CreateIssueDialog(driver);
    }

    public ReportDefectDialog clickReportDefect() {
        wait.until(ExpectedConditions.elementToBeClickable(reportDefectBtn)).click();
        logger.info("Clicked Report Defect");
        return new ReportDefectDialog(driver);
    }

    public BoardPage filterByMember(String memberName) {
        try {
            WebElement filter = wait.until(ExpectedConditions.visibilityOfElementLocated(filterByMemberInput));
            filter.sendKeys(memberName);
            logger.info("Filtered by member: {}", memberName);
        } catch (Exception e) {
            logger.warn("Filter input not found: {}", e.getMessage());
        }
        return this;
    }

    public BoardPage clearFilters() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(clearFilterBtn)).click();
            logger.info("Cleared all filters");
        } catch (Exception e) {
            logger.warn("Clear filter button not found");
        }
        return this;
    }

    public boolean isCardStatusInColumn(String cardTitle, String columnName) {
        By locator = By.xpath("//div[contains(@class,'column') or contains(@class,'col')][contains(.,'" + columnName + "')]" +
                              "//div[contains(@class,'card')][contains(.,'" + cardTitle + "')]");
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
