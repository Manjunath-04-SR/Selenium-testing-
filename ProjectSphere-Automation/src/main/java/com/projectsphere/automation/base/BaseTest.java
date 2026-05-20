package com.projectsphere.automation.base;

import com.projectsphere.automation.constants.AppConstants;
import com.projectsphere.automation.utils.ConfigReader;
import com.projectsphere.automation.utils.ExtentManager;
import com.projectsphere.automation.utils.ExtentReportListener;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

@Listeners({ExtentReportListener.class})
public class BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ConfigReader config = ConfigReader.getInstance();

    // ── Suite-level lifecycle ─────────────────────────────────────────────────

    @BeforeSuite(alwaysRun = true)
    public void initReports() {
        ExtentManager.createInstance(config.getReportPath());
        logger.info("ExtentReports initialized. Report path: {}", config.getReportPath());
    }

    @AfterSuite(alwaysRun = true)
    public void flushReports() {
        ExtentManager.getInstance().flush();
        logger.info("ExtentReports flushed.");
    }

    // ── Test-level lifecycle ──────────────────────────────────────────────────

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "env"})
    public void setUp(@Optional String browser, @Optional String env) {
        String browserToUse = (browser != null && !browser.isEmpty()) ? browser : config.getBrowser();
        String envToUse     = (env != null && !env.isEmpty()) ? env : System.getProperty("env", "dev");

        logger.info("Setting up WebDriver — browser={}, env={}", browserToUse, envToUse);
        WebDriver driver = createDriver(browserToUse);
        driverThreadLocal.set(driver);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(90));
        driver.manage().window().maximize();

        String baseUrl = config.getBaseUrl();
        logger.info("Navigating to: {}", baseUrl);
        try {
            driver.get(baseUrl);
        } catch (org.openqa.selenium.TimeoutException te) {
            logger.warn("Page load timed out; attempting to continue: {}", te.getMessage());
        }
        logger.info("Page loaded. Title='{}' URL='{}'", driver.getTitle(), driver.getCurrentUrl());

        // ── Step 1: Wait briefly for EITHER the login form OR the marketing landing page ──
        // The app shows a marketing/landing page at /home first.
        // The login form is reached by clicking the "Sign In" button on that page.
        By loginFormLocator   = By.cssSelector("input[formcontrolname='email']");
        By signInNavButton    = By.xpath(
            "//button[normalize-space()='Sign In'] | " +
            "//a[normalize-space()='Sign In'] | " +
            "//*[contains(@class,'sign-in') and normalize-space()='Sign In']");

        logger.info("Checking which page loaded (marketing page or login form)...");
        boolean formFound = false;
        try {
            // Short 15s wait — if the login form is directly visible, great
            new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(loginFormLocator));
            logger.info("Login form loaded directly — no Sign In click needed.");
            formFound = true;
        } catch (Exception ignored) {
            logger.info("Login form not visible yet — looking for 'Sign In' nav button on landing page...");
        }

        // ── Step 2: If on marketing page, click the Sign In button ──────────────
        if (!formFound) {
            try {
                WebElement signInBtn = new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(ExpectedConditions.elementToBeClickable(signInNavButton));
                logger.info("Found 'Sign In' nav button (text='{}') — clicking it.", signInBtn.getText());
                signInBtn.click();

                // ── Step 3: Wait for the login form to appear after click ────────
                long startMs = System.currentTimeMillis();
                logger.info("Waiting for login form after Sign In click (up to 90s)...");
                try {
                    new WebDriverWait(driver, Duration.ofSeconds(90))
                        .until(ExpectedConditions.presenceOfElementLocated(loginFormLocator));
                    long elapsed = (System.currentTimeMillis() - startMs) / 1000;
                    logger.info("Login form appeared after {}s.", elapsed);
                    formFound = true;
                } catch (Exception e) {
                    long elapsed = (System.currentTimeMillis() - startMs) / 1000;
                    logger.warn("Login form still not found {}s after Sign In click.", elapsed);
                }
            } catch (Exception e) {
                logger.warn("Could not find/click Sign In nav button: {}", e.getMessage());
            }
        }

        // ── Step 4: If still not found, dump diagnostics ─────────────────────────
        if (!formFound) {
            dumpPageDiagnostics(driver);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            logger.warn("Test FAILED: {}", result.getName());
            ExtentReportListener.getTest().fail("Test failed: " + result.getName());
        }
        WebDriver driver = getDriver();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("WebDriver quit successfully for test: {}", result.getName());
            } catch (Exception e) {
                logger.warn("Error quitting WebDriver: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    // ── Static Helpers ────────────────────────────────────────────────────────

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static WebDriverWait getWait() {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
    }

    /** Called by ExtentReportListener to grab the screenshot bytes for the current thread. */
    public static byte[] captureScreenshotBytes() {
        WebDriver driver = getDriver();
        if (driver instanceof TakesScreenshot) {
            try {
                return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            } catch (Exception e) {
                LoggerFactory.getLogger(BaseTest.class).warn("Screenshot capture failed: {}", e.getMessage());
            }
        }
        return new byte[0];
    }

    // ── Diagnostics ──────────────────────────────────────────────────────────

    /**
     * Called when the Angular login form is not found within the timeout.
     * Saves a screenshot + first 3000 chars of page source to target/screenshots/
     * and logs key info so we can see what Chrome actually rendered.
     */
    private void dumpPageDiagnostics(WebDriver driver) {
        try {
            logger.warn("=== PAGE DIAGNOSTICS ===");
            logger.warn("  Title   : {}", driver.getTitle());
            logger.warn("  URL     : {}", driver.getCurrentUrl());

            // Count all input elements (including hidden)
            java.util.List<WebElement> allInputs = driver.findElements(By.tagName("input"));
            logger.warn("  Inputs  : {} total", allInputs.size());
            for (int i = 0; i < Math.min(allInputs.size(), 5); i++) {
                WebElement inp = allInputs.get(i);
                logger.warn("    [{}] type='{}' formcontrolname='{}' placeholder='{}' visible={}",
                        i, inp.getAttribute("type"),
                        inp.getAttribute("formcontrolname"),
                        inp.getAttribute("placeholder"),
                        inp.isDisplayed());
            }

            // Log first 3000 chars of page source
            String src = driver.getPageSource();
            int end = Math.min(src.length(), 3000);
            logger.warn("  PageSource(0-3000):\n{}", src.substring(0, end));
            logger.warn("========================");

            // Save screenshot to target/screenshots/diagnostic_<timestamp>.png
            if (driver instanceof TakesScreenshot) {
                byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                String dir = "target/screenshots";
                Files.createDirectories(Paths.get(dir));
                String filename = dir + "/diagnostic_" + System.currentTimeMillis() + ".png";
                try (FileOutputStream fos = new FileOutputStream(new File(filename))) {
                    fos.write(png);
                }
                logger.warn("  Screenshot saved: {}", filename);
            }
        } catch (Exception ex) {
            logger.warn("Diagnostics dump failed: {}", ex.getMessage());
        }
    }

    // ── Driver Factory ────────────────────────────────────────────────────────

    private WebDriver createDriver(String browser) {
        switch (browser.toLowerCase().trim()) {
            case "firefox": {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions opts = new FirefoxOptions();
                opts.addArguments("--start-maximized");
                logger.info("Launching Firefox WebDriver");
                return new FirefoxDriver(opts);
            }
            case "edge": {
                WebDriverManager.edgedriver().setup();
                EdgeOptions opts = new EdgeOptions();
                opts.addArguments("--start-maximized", "--disable-notifications");
                logger.info("Launching Edge WebDriver");
                return new EdgeDriver(opts);
            }
            case "chrome":
            default: {
                WebDriverManager.chromedriver().setup();
                ChromeOptions opts = new ChromeOptions();
                // --disable-blink-features=AutomationControlled prevents sites from
                // detecting Chrome is being driven by Selenium and serving different content
                opts.addArguments(
                    "--start-maximized",
                    "--disable-notifications",
                    "--disable-infobars",
                    "--disable-blink-features=AutomationControlled"
                );
                // Remove navigator.webdriver flag that Angular/JS apps can detect
                opts.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                opts.setExperimentalOption("useAutomationExtension", false);
                logger.info("Launching Chrome WebDriver (automation detection disabled)");
                return new ChromeDriver(opts);
            }
        }
    }
}
