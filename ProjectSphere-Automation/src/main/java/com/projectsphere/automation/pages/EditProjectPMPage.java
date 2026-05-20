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

public class EditProjectPMPage {

    private static final Logger logger = LoggerFactory.getLogger(EditProjectPMPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators ──────────────────────────────────────────────────────────────
    // Angular reactive forms use formcontrolname. Fall back to name/placeholder.
    private final By pageHeading        = By.xpath("//h1[contains(normalize-space(),'Edit Project')] | //h2[contains(normalize-space(),'Edit')]");
    private final By projectNameField   = By.xpath(
        "//input[@formcontrolname='projectName' or @formcontrolname='name'" +
        "        or @name='name' or @name='projectName'" +
        "        or contains(@placeholder,'Project Name')]");
    private final By descriptionField   = By.xpath(
        "//textarea[@formcontrolname='description' or @name='description'" +
        "           or contains(@placeholder,'Description')] " +
        "| //input[@formcontrolname='description' or @name='description']");
    private final By statusDropdown     = By.xpath("//select[@formcontrolname='status' or @name='status'] | //select[contains(@id,'status')]");
    private final By domainDropdown     = By.xpath("//select[@formcontrolname='domain' or @name='domain'] | //select[contains(@id,'domain')]");
    private final By teamInfo           = By.xpath("//*[contains(@class,'team') and (contains(@class,'chip') or contains(@class,'badge') or contains(@class,'card'))]");
    private final By editTeamBtn        = By.xpath("//button[contains(normalize-space(),'Edit Team')]");
    private final By saveChangesBtn     = By.xpath("//button[contains(normalize-space(),'Save Changes') or contains(normalize-space(),'Save') or contains(normalize-space(),'Update')]");
    private final By cancelBtn          = By.xpath("//button[normalize-space()='Cancel']");
    private final By deleteProjectBtn   = By.xpath("//button[contains(normalize-space(),'Delete Project') or contains(normalize-space(),'Delete')]");
    private final By confirmDeleteBtn   = By.xpath("//button[contains(normalize-space(),'Confirm') or contains(normalize-space(),'Yes')]" +
                                                   "[(ancestor::div[contains(@class,'confirm') or contains(@class,'modal')])]");

    public EditProjectPMPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        logger.info("EditProjectPMPage initialized");
    }

    public boolean isPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(projectNameField),
                    ExpectedConditions.visibilityOfElementLocated(pageHeading)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPrePopulated() {
        try {
            String val = wait.until(ExpectedConditions.visibilityOfElementLocated(projectNameField)).getAttribute("value");
            return val != null && !val.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public EditProjectPMPage updateProjectName(String name) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(projectNameField));
        el.clear();
        el.sendKeys(name);
        logger.info("Updated project name: {}", name);
        return this;
    }

    public EditProjectPMPage updateDescription(String desc) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionField));
            el.clear();
            el.sendKeys(desc);
        } catch (Exception e) {
            logger.warn("Description field not found");
        }
        return this;
    }

    public EditProjectPMPage selectStatus(String status) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(statusDropdown)))
                    .selectByVisibleText(status);
            logger.info("Selected status: {}", status);
        } catch (Exception e) {
            logger.warn("Status dropdown issue");
        }
        return this;
    }

    public EditProjectPMPage selectDomain(String domain) {
        try {
            new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(domainDropdown)))
                    .selectByVisibleText(domain);
            logger.info("Selected domain: {}", domain);
        } catch (Exception e) {
            logger.warn("Domain dropdown issue");
        }
        return this;
    }

    public boolean isTeamInfoDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(teamInfo)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public EditTeamPage clickEditTeam() {
        wait.until(ExpectedConditions.elementToBeClickable(editTeamBtn)).click();
        logger.info("Clicked Edit Team button from Edit Project PM page");
        return new EditTeamPage(driver);
    }

    public MyProjectsPMPage clickSaveChanges() {
        wait.until(ExpectedConditions.elementToBeClickable(saveChangesBtn)).click();
        logger.info("Clicked Save Changes on Edit Project (PM)");
        return new MyProjectsPMPage(driver);
    }

    public MyProjectsPMPage clickCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelBtn)).click();
        logger.info("Clicked Cancel on Edit Project (PM)");
        return new MyProjectsPMPage(driver);
    }

    public MyProjectsPMPage clickDeleteProject() {
        wait.until(ExpectedConditions.elementToBeClickable(deleteProjectBtn)).click();
        try {
            wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteBtn)).click();
            logger.info("Confirmed project deletion");
        } catch (Exception e) {
            logger.info("No confirmation dialog; deletion may be immediate");
        }
        return new MyProjectsPMPage(driver);
    }
}
