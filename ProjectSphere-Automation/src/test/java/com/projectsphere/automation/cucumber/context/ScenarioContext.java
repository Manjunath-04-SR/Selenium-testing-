package com.projectsphere.automation.cucumber.context;

import com.projectsphere.automation.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Shared state container injected into every step definition class via PicoContainer.
 * One instance is created per Cucumber scenario and destroyed after it finishes.
 */
public class ScenarioContext {

    private static final Logger logger = LoggerFactory.getLogger(ScenarioContext.class);

    private WebDriver driver;
    private final ConfigReader config = ConfigReader.getInstance();

    // ── Driver lifecycle ──────────────────────────────────────────────────────

    public void initDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments(
                "--start-maximized",
                "--disable-notifications",
                "--disable-infobars",
                "--disable-blink-features=AutomationControlled"
        );
        opts.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        opts.setExperimentalOption("useAutomationExtension", false);
        driver = new ChromeDriver(opts);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(90));
        logger.info("Cucumber WebDriver initialized (Chrome)");
    }

    /**
     * Navigate to the app URL and wait for the Angular login form to be ready.
     * Replicates the same landing-page logic from BaseTest.setUp().
     */
    public void navigateToApp() {
        String url = config.getBaseUrl();
        try {
            driver.get(url);
        } catch (org.openqa.selenium.TimeoutException te) {
            logger.warn("Page load timed out — continuing: {}", te.getMessage());
        }
        logger.info("Navigated to: {}", url);

        By loginForm    = By.cssSelector("input[formcontrolname='email']");
        By signInNavBtn = By.xpath(
                "//button[normalize-space()='Sign In'] | " +
                "//a[normalize-space()='Sign In'] | " +
                "//*[contains(@class,'sign-in') and normalize-space()='Sign In']");

        boolean formReady = false;
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.presenceOfElementLocated(loginForm));
            formReady = true;
            logger.info("Login form visible directly.");
        } catch (Exception ignored) {
            logger.info("Login form not visible — checking for landing page Sign In button...");
        }

        if (!formReady) {
            try {
                WebElement signInBtn = new WebDriverWait(driver, Duration.ofSeconds(20))
                        .until(ExpectedConditions.elementToBeClickable(signInNavBtn));
                signInBtn.click();
                logger.info("Clicked landing page Sign In button.");
                new WebDriverWait(driver, Duration.ofSeconds(90))
                        .until(ExpectedConditions.presenceOfElementLocated(loginForm));
                logger.info("Login form ready after Sign In click.");
            } catch (Exception e) {
                logger.warn("Could not reach login form: {}", e.getMessage());
            }
        }
    }

    public void quitDriver() {
        if (driver != null) {
            try {
                driver.quit();
                logger.info("Cucumber WebDriver quit.");
            } catch (Exception e) {
                logger.warn("Error quitting WebDriver: {}", e.getMessage());
            } finally {
                driver = null;
            }
        }
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public WebDriver getDriver() {
        return driver;
    }

    public ConfigReader getConfig() {
        return config;
    }
}
