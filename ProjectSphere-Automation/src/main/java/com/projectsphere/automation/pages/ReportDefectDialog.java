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

public class ReportDefectDialog {

    private static final Logger logger = LoggerFactory.getLogger(ReportDefectDialog.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators ──────────────────────────────────────────────────────────────
    // Angular reactive forms use formcontrolname. Fall back to name/placeholder.
    private final By dialogContainer     = By.xpath("//div[contains(@class,'modal') or contains(@role,'dialog') or contains(@class,'ax-modal')]");
    private final By ticketDropdown      = By.xpath("//select[@formcontrolname='ticket' or @formcontrolname='ticketId' or @name='ticket' or @name='ticketId'] | //select[contains(@id,'ticket')]");
    private final By testCaseDropdown    = By.xpath("//select[@formcontrolname='testCase' or @formcontrolname='testCaseId' or @name='testCase' or @name='testCaseId'] | //select[contains(@id,'testCase') or contains(@id,'test_case')]");
    private final By severityDropdown    = By.xpath("//select[@formcontrolname='severity' or @name='severity'] | //select[contains(@id,'severity')]");
    private final By reproducibilityDd   = By.xpath("//select[@formcontrolname='reproducibility' or @name='reproducibility'] | //select[contains(@id,'repro')]");
    private final By addStepBtn          = By.xpath("//button[contains(normalize-space(),'Add Step') or normalize-space()='+']");
    // Fix: use separate predicates for @name and @placeholder (avoid nested predicate on attribute)
    private final By stepInputs          = By.xpath(
        "//input[contains(@name,'step') or contains(@formcontrolname,'step')] | " +
        "//textarea[contains(@name,'step') or contains(@formcontrolname,'step')] | " +
        "//input[contains(@placeholder,'step') or contains(@placeholder,'Step')]");
    private final By fileAttachInput     = By.cssSelector("input[type='file']");
    // Exclude qa-card buttons if any; use JS click to bypass modal-backdrop
    private final By reportDefectBtn     = By.xpath(
        "//button[not(contains(@class,'qa-card'))][" +
        "  contains(normalize-space(),'Report Defect') or " +
        "  normalize-space()='Report' or " +
        "  contains(normalize-space(),'Submit')" +
        "]");
    private final By cancelBtn           = By.xpath("//button[normalize-space()='Cancel' or normalize-space()='Close']");
    private final By attachmentArea      = By.xpath("//div[contains(@class,'attachment') or contains(@class,'upload')]");

    public ReportDefectDialog(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("ReportDefectDialog initialized");
    }

    public boolean isDialogVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(dialogContainer));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ReportDefectDialog selectTicket(String ticketName) {
        try {
            Select sel = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(ticketDropdown)));
            if (ticketName == null || ticketName.isEmpty()) {
                sel.selectByIndex(1);
            } else {
                sel.selectByVisibleText(ticketName);
            }
            logger.info("Selected ticket: {}", ticketName);
        } catch (Exception e) {
            logger.warn("Ticket dropdown not found");
        }
        return this;
    }

    public ReportDefectDialog selectTestCase(String testCaseName) {
        try {
            Select sel = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(testCaseDropdown)));
            if (testCaseName == null || testCaseName.isEmpty()) {
                sel.selectByIndex(1);
            } else {
                sel.selectByVisibleText(testCaseName);
            }
            logger.info("Selected test case: {}", testCaseName);
        } catch (Exception e) {
            logger.warn("Test case dropdown not found");
        }
        return this;
    }

    public ReportDefectDialog selectSeverity(String severity) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(severityDropdown)))
                    .selectByVisibleText(severity);
            logger.info("Selected severity: {}", severity);
        } catch (Exception e) {
            logger.warn("Severity dropdown not found");
        }
        return this;
    }

    public ReportDefectDialog selectReproducibility(String reproducibility) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(reproducibilityDd)))
                    .selectByVisibleText(reproducibility);
            logger.info("Selected reproducibility: {}", reproducibility);
        } catch (Exception e) {
            logger.warn("Reproducibility dropdown not found");
        }
        return this;
    }

    public ReportDefectDialog addStep(String stepDescription) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(addStepBtn)).click();
            List<WebElement> inputs = driver.findElements(stepInputs);
            if (!inputs.isEmpty()) {
                WebElement lastInput = inputs.get(inputs.size() - 1);
                lastInput.clear();
                lastInput.sendKeys(stepDescription);
            }
            logger.info("Added step: {}", stepDescription);
        } catch (Exception e) {
            logger.warn("Could not add step: {}", e.getMessage());
        }
        return this;
    }

    public ReportDefectDialog attachFile(String filePath) {
        try {
            WebElement fileEl = wait.until(ExpectedConditions.presenceOfElementLocated(fileAttachInput));
            fileEl.sendKeys(filePath);
            logger.info("Attached file: {}", filePath);
        } catch (Exception e) {
            logger.warn("File attachment input not found");
        }
        return this;
    }

    public DefectsPage clickReportDefect() {
        try {
            WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(reportDefectBtn));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            logger.info("Clicked Report Defect (JS click)");
        } catch (Exception e) {
            logger.warn("Report Defect button not found: {}", e.getMessage());
        }
        return new DefectsPage(driver);
    }

    public void clickCancel() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(cancelBtn)).click();
        } catch (Exception e) {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        }
        logger.info("Cancelled Report Defect dialog");
    }

    public boolean areTicketAndTestCasePopulated() {
        try {
            Select ticketSel = new Select(driver.findElement(ticketDropdown));
            Select tcSel     = new Select(driver.findElement(testCaseDropdown));
            boolean t = !ticketSel.getFirstSelectedOption().getText().isEmpty();
            boolean c = !tcSel.getFirstSelectedOption().getText().isEmpty();
            return t && c;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAttachmentVisible() {
        try {
            return driver.findElement(attachmentArea).isDisplayed() ||
                   driver.findElement(fileAttachInput).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
