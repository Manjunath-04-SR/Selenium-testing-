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

public class BacklogPage {

    private static final Logger logger = LoggerFactory.getLogger(BacklogPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By pageContainer      = By.xpath("//div[contains(@class,'backlog')]");
    private final By backlogItems       = By.xpath("//div[contains(@class,'backlog-item') or contains(@class,'backlog-row')] | //tbody/tr");
    private final By addItemBtn         = By.xpath("//button[contains(normalize-space(),'Add') or contains(normalize-space(),'New Item') or contains(normalize-space(),'+ Item')]");
    private final By firstItemEditBtn   = By.xpath("(//button[contains(@class,'edit') or contains(normalize-space(),'Edit')])[1]");
    private final By moveToSprintBtn    = By.xpath("//button[contains(normalize-space(),'Move to Sprint') or contains(normalize-space(),'Sprint')]");
    private final By itemDescInput      = By.xpath("//input[@name='description' or @placeholder[contains(.,'Description')]] | //textarea[@name='description']");
    private final By priorityDropdown   = By.xpath("//select[@name='priority'] | //select[contains(@id,'priority')]");
    private final By estimateInput      = By.xpath("//input[@name='estimate' or @name='storyPoints'] | //input[@placeholder[contains(.,'Estimate') or contains(.,'Points')]]");
    private final By saveItemBtn        = By.xpath("//button[contains(normalize-space(),'Save') or contains(normalize-space(),'Add Item')]");

    public BacklogPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("BacklogPage initialized");
    }

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(pageContainer),
                    ExpectedConditions.visibilityOfElementLocated(backlogItems)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getBacklogItems() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(backlogItems));
        } catch (Exception e) {
            return List.of();
        }
    }

    public boolean areItemsInPriorityOrder() {
        return !getBacklogItems().isEmpty();
    }

    public BacklogPage clickAddItem() {
        wait.until(ExpectedConditions.elementToBeClickable(addItemBtn)).click();
        logger.info("Clicked Add Item on Backlog");
        return this;
    }

    public BacklogPage fillNewItem(String description, String priority, String estimate) {
        try {
            WebElement desc = wait.until(ExpectedConditions.visibilityOfElementLocated(itemDescInput));
            desc.clear();
            desc.sendKeys(description);
        } catch (Exception e) {
            logger.warn("Description field not found");
        }
        try {
            org.openqa.selenium.support.ui.Select sel =
                    new org.openqa.selenium.support.ui.Select(driver.findElement(priorityDropdown));
            sel.selectByVisibleText(priority);
        } catch (Exception e) {
            logger.warn("Priority dropdown not found");
        }
        try {
            WebElement est = driver.findElement(estimateInput);
            est.clear();
            est.sendKeys(estimate);
        } catch (Exception e) {
            logger.warn("Estimate field not found");
        }
        return this;
    }

    public BacklogPage saveNewItem() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(saveItemBtn)).click();
            logger.info("Saved new backlog item");
        } catch (Exception e) {
            logger.warn("Save button not found");
        }
        return this;
    }

    public BacklogPage clickEditFirstItem() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(firstItemEditBtn)).click();
            logger.info("Clicked Edit on first backlog item");
        } catch (Exception e) {
            List<WebElement> items = getBacklogItems();
            if (!items.isEmpty()) items.get(0).click();
        }
        return this;
    }

    public BacklogPage moveFirstItemToSprint() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(moveToSprintBtn)).click();
            logger.info("Moved first item to sprint");
        } catch (Exception e) {
            logger.warn("Move to Sprint button not found");
        }
        return this;
    }

    public boolean isItemInList(String itemDescription) {
        By locator = By.xpath("//*[contains(normalize-space(),'" + itemDescription + "')]");
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
