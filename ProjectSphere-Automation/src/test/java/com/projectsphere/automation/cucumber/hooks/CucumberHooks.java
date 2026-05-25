package com.projectsphere.automation.cucumber.hooks;

import com.projectsphere.automation.cucumber.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cucumber lifecycle hooks.
 * PicoContainer injects the same ScenarioContext instance into this class
 * and into every step definition class within the same scenario.
 */
public class CucumberHooks {

    private static final Logger logger = LoggerFactory.getLogger(CucumberHooks.class);

    private final ScenarioContext context;

    public CucumberHooks(ScenarioContext context) {
        this.context = context;
    }

    // ── Before each scenario ──────────────────────────────────────────────────

    @Before(order = 0)
    public void setUp(Scenario scenario) {
        logger.info("=== Starting Scenario: [{}] ===", scenario.getName());
        context.initDriver();
        context.navigateToApp();
    }

    // ── After each scenario ───────────────────────────────────────────────────

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        WebDriver driver = context.getDriver();

        // Attach screenshot to report on failure
        if (scenario.isFailed() && driver instanceof TakesScreenshot) {
            try {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Screenshot on Failure");
                logger.warn("Screenshot attached for failed scenario: {}", scenario.getName());
            } catch (Exception e) {
                logger.warn("Could not capture screenshot: {}", e.getMessage());
            }
        }

        logger.info("=== Finished Scenario: [{}] — Status: {} ===",
                scenario.getName(), scenario.getStatus());
        context.quitDriver();
    }
}
