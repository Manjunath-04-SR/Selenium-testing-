package com.projectsphere.automation.cucumber.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestNG-based Cucumber runner.
 *
 * Run via IntelliJ:  Right-click → Run 'CucumberRunner'
 * Run via Maven:     mvn test -DsuiteFile=src/test/resources/cucumber-testng.xml
 *
 * Run only @smoke tags:
 *   mvn test -DsuiteFile=src/test/resources/cucumber-testng.xml -Dcucumber.filter.tags="@smoke"
 *
 * Run only @admin tags:
 *   mvn test -DsuiteFile=src/test/resources/cucumber-testng.xml -Dcucumber.filter.tags="@admin"
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue     = {
            "com.projectsphere.automation.cucumber.hooks",
            "com.projectsphere.automation.cucumber.stepdefs"
        },
        plugin   = {
            "pretty",
            "html:target/cucumber-reports/cucumber-report.html",
            "json:target/cucumber-reports/cucumber.json"
        },
        monochrome = true,
        tags       = "not @wip"
)
public class CucumberRunner extends AbstractTestNGCucumberTests {

    /**
     * Override to enable parallel scenario execution.
     * parallel = false (default) runs scenarios sequentially — safe for shared Render instance.
     */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
