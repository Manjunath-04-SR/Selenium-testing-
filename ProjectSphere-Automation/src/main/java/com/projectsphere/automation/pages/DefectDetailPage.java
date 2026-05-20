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

public class DefectDetailPage {

    private static final Logger logger = LoggerFactory.getLogger(DefectDetailPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By pageContainer        = By.xpath("//div[contains(@class,'defect-detail') or contains(@class,'bug-detail')]");
    private final By assigneeField        = By.xpath("//*[contains(@class,'assignee') or contains(normalize-space(),'Assignee')]");
    private final By severityField        = By.xpath("//*[contains(@class,'severity') or contains(normalize-space(),'Severity')]");
    private final By reproducibilityField = By.xpath("//*[contains(@class,'repro') or contains(normalize-space(),'Reproducibility')]");
    private final By statusField          = By.xpath("//*[contains(@class,'status') or contains(normalize-space(),'Status')]");
    private final By expectedResultField  = By.xpath("//*[contains(@class,'expected') or contains(normalize-space(),'Expected')]");
    private final By actualResultField    = By.xpath("//*[contains(@class,'actual') or contains(normalize-space(),'Actual')]");
    private final By stepsToReproduce     = By.xpath("//ol//li | //div[contains(@class,'steps')]//div | //*[contains(@class,'step')]");
    private final By linkedTestCasesTab   = By.xpath("//button[normalize-space()='Linked Test Cases'] | //a[normalize-space()='Linked Test Cases'] | //li[normalize-space()='Linked Test Cases']");
    private final By activityTab          = By.xpath("//button[normalize-space()='Activity'] | //a[normalize-space()='Activity'] | //li[normalize-space()='Activity']");
    private final By retestBtn            = By.xpath("//button[contains(normalize-space(),'Retest')]");
    private final By markFixedBtn         = By.xpath("//button[contains(normalize-space(),'Mark Fixed') or contains(normalize-space(),'Fixed')]");
    private final By startProgressBtn     = By.xpath("//button[contains(normalize-space(),'Start Progress') or contains(normalize-space(),'In Progress')]");
    private final By linkedTestCasesList  = By.xpath("//div[contains(@class,'linked') or contains(@class,'test-case-link')] | //tbody/tr");

    public DefectDetailPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("DefectDetailPage initialized");
    }

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(pageContainer),
                    ExpectedConditions.visibilityOfElementLocated(severityField)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areAllFieldsDisplayed() {
        try {
            boolean assignee      = !driver.findElements(assigneeField).isEmpty();
            boolean severity      = !driver.findElements(severityField).isEmpty();
            boolean reproducibility = !driver.findElements(reproducibilityField).isEmpty();
            boolean status        = !driver.findElements(statusField).isEmpty();
            logger.info("Defect detail fields — assignee:{} severity:{} repro:{} status:{}",
                    assignee, severity, reproducibility, status);
            return assignee && severity;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areExpectedAndActualDisplayed() {
        try {
            boolean exp = !driver.findElements(expectedResultField).isEmpty();
            boolean act = !driver.findElements(actualResultField).isEmpty();
            logger.info("Expected visible:{}, Actual visible:{}", exp, act);
            return exp && act;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean areStepsToReproduceDisplayed() {
        try {
            List<WebElement> steps = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(stepsToReproduce));
            logger.info("Steps to reproduce count: {}", steps.size());
            return !steps.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public DefectDetailPage clickLinkedTestCasesTab() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(linkedTestCasesTab)).click();
            logger.info("Clicked Linked Test Cases tab");
        } catch (Exception e) {
            logger.warn("Linked Test Cases tab not found");
        }
        return this;
    }

    public boolean areLinkedTestCasesDisplayed() {
        try {
            return !wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(linkedTestCasesList)).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public DefectDetailPage clickStartProgress() {
        wait.until(ExpectedConditions.elementToBeClickable(startProgressBtn)).click();
        logger.info("Clicked Start Progress");
        return this;
    }

    public DefectDetailPage clickMarkFixed() {
        wait.until(ExpectedConditions.elementToBeClickable(markFixedBtn)).click();
        logger.info("Clicked Mark Fixed");
        return this;
    }

    public DefectDetailPage clickRetest() {
        wait.until(ExpectedConditions.elementToBeClickable(retestBtn)).click();
        logger.info("Clicked Retest");
        return this;
    }

    public String getCurrentStatus() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(statusField)).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }
}
