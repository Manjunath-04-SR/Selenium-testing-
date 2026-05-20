package com.projectsphere.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.projectsphere.automation.base.BaseTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExtentReportListener implements ITestListener {

    private static final Logger logger = LoggerFactory.getLogger(ExtentReportListener.class);
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();
    private static ExtentReports extentReports;

    @Override
    public void onStart(ITestContext context) {
        logger.info("Test Suite starting: {}", context.getName());
        extentReports = ExtentManager.getInstance();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        String[] groups = result.getMethod().getGroups();

        ExtentTest test = extentReports.createTest(testName, description);
        for (String group : groups) {
            test.assignCategory(group);
        }
        extentTestThreadLocal.set(test);
        logger.info("Test started: {}", testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        getTest().log(Status.PASS,
                MarkupHelper.createLabel("TEST PASSED: " + testName, ExtentColor.GREEN));
        logger.info("Test passed: {}", testName);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();

        getTest().log(Status.FAIL,
                MarkupHelper.createLabel("TEST FAILED: " + testName, ExtentColor.RED));
        if (throwable != null) {
            getTest().fail(throwable);
        }

        // Capture and embed screenshot
        try {
            String screenshotPath = captureScreenshot(testName);
            if (screenshotPath != null) {
                getTest().addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
                logger.info("Screenshot captured: {}", screenshotPath);
            }
        } catch (IOException e) {
            logger.error("Failed to capture screenshot for test '{}': {}", testName, e.getMessage());
        }
        logger.error("Test failed: {} — {}", testName, throwable != null ? throwable.getMessage() : "no message");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();
        getTest().log(Status.SKIP,
                MarkupHelper.createLabel("TEST SKIPPED: " + testName, ExtentColor.ORANGE));
        if (throwable != null) {
            getTest().skip(throwable);
        }
        logger.warn("Test skipped: {}", testName);
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extentReports != null) {
            extentReports.flush();
            logger.info("ExtentReports flushed. Suite finished: {}", context.getName());
        }
        extentTestThreadLocal.remove();
    }

    /** Returns the ExtentTest bound to the current thread. */
    public static ExtentTest getTest() {
        ExtentTest test = extentTestThreadLocal.get();
        if (test == null) {
            // Defensive fallback – create an orphan test rather than NPE
            test = ExtentManager.getInstance().createTest("UnknownTest");
            extentTestThreadLocal.set(test);
        }
        return test;
    }

    private String captureScreenshot(String testName) throws IOException {
        String screenshotDir = ConfigReader.getInstance().getScreenshotPath();
        Files.createDirectories(Paths.get(screenshotDir));

        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileName = testName + "_" + timestamp + ".png";
        String filePath = screenshotDir + File.separator + fileName;

        try {
            byte[] screenshotBytes = BaseTest.captureScreenshotBytes();
            if (screenshotBytes != null && screenshotBytes.length > 0) {
                Files.write(Paths.get(filePath), screenshotBytes);
                return filePath;
            }
        } catch (Exception e) {
            logger.warn("Could not capture screenshot: {}", e.getMessage());
        }
        return null;
    }
}
