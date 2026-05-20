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

public class DocumentsPage {

    private static final Logger logger = LoggerFactory.getLogger(DocumentsPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By pageContainer      = By.xpath("//div[contains(@class,'documents') or contains(@class,'library')]");
    private final By documentList       = By.xpath("//div[contains(@class,'document-item') or contains(@class,'file-item')] | //tbody/tr");
    private final By uploadBtn          = By.xpath("//button[contains(normalize-space(),'Upload') or contains(normalize-space(),'Add Document')]");
    private final By fileInput          = By.cssSelector("input[type='file']");
    private final By firstDocumentRow   = By.xpath("(//div[contains(@class,'document-item') or contains(@class,'file-item')] | //tbody/tr)[1]");
    private final By downloadBtn        = By.xpath("//button[contains(normalize-space(),'Download') or @title='Download'] | //a[contains(@href,'download')]");
    private final By deleteBtn          = By.xpath("//button[contains(normalize-space(),'Delete') or @aria-label='Delete']");
    private final By replaceBtn         = By.xpath("//button[contains(normalize-space(),'Replace') or contains(normalize-space(),'Update')]");
    private final By confirmDeleteBtn   = By.xpath("//button[contains(normalize-space(),'Confirm') or contains(normalize-space(),'Yes')]" +
                                                   "[(ancestor::div[contains(@class,'confirm') or contains(@class,'modal')])]");

    public DocumentsPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("DocumentsPage initialized");
    }

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(pageContainer),
                    ExpectedConditions.visibilityOfElementLocated(uploadBtn)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getDocumentList() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(documentList));
        } catch (Exception e) {
            return List.of();
        }
    }

    public DocumentsPage uploadDocument(String filePath) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(uploadBtn)).click();
            WebElement fileEl = wait.until(ExpectedConditions.presenceOfElementLocated(fileInput));
            fileEl.sendKeys(filePath);
            logger.info("Uploaded document: {}", filePath);
        } catch (Exception e) {
            logger.warn("Could not upload document: {}", e.getMessage());
        }
        return this;
    }

    public DocumentsPage clickFirstDocument() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(firstDocumentRow)).click();
            logger.info("Clicked first document");
        } catch (Exception e) {
            logger.warn("No documents found");
        }
        return this;
    }

    public DocumentsPage downloadFirstDocument() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(downloadBtn)).click();
            logger.info("Clicked Download button");
        } catch (Exception e) {
            logger.warn("Download button not found");
        }
        return this;
    }

    public DocumentsPage deleteFirstDocument() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(firstDocumentRow)).click();
            wait.until(ExpectedConditions.elementToBeClickable(deleteBtn)).click();
            try {
                wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteBtn)).click();
            } catch (Exception e) {
                // No confirmation required
            }
            logger.info("Deleted first document");
        } catch (Exception e) {
            logger.warn("Could not delete document: {}", e.getMessage());
        }
        return this;
    }

    public boolean isDocumentInList(String fileName) {
        By locator = By.xpath("//*[contains(normalize-space(),'" + fileName + "')]");
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDocumentAbsent(String fileName) {
        By locator = By.xpath("//*[normalize-space()='" + fileName + "']");
        try {
            List<WebElement> els = driver.findElements(locator);
            return els.isEmpty() || els.stream().noneMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            return true;
        }
    }
}
