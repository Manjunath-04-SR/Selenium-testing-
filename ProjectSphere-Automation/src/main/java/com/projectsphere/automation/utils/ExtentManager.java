package com.projectsphere.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.projectsphere.automation.constants.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ExtentManager {

    private static final Logger logger = LoggerFactory.getLogger(ExtentManager.class);
    private static ExtentReports extentReports;

    private ExtentManager() {}

    public static synchronized ExtentReports createInstance(String filePath) {
        if (extentReports == null) {
            File reportFile = new File(filePath);
            reportFile.getParentFile().mkdirs();

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(filePath);
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle(AppConstants.REPORT_TITLE);
            sparkReporter.config().setReportName(AppConstants.REPORT_NAME);
            sparkReporter.config().setEncoding("UTF-8");
            sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);

            extentReports.setSystemInfo("OS",           System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
            extentReports.setSystemInfo("Browser",      ConfigReader.getInstance().getBrowser());
            extentReports.setSystemInfo("Environment",  System.getProperty("env", "dev"));
            extentReports.setSystemInfo("Base URL",     ConfigReader.getInstance().getBaseUrl());
            extentReports.setSystemInfo("Tester",       System.getProperty("user.name"));

            logger.info("ExtentReports instance created at: {}", filePath);
        }
        return extentReports;
    }

    public static synchronized ExtentReports getInstance() {
        if (extentReports == null) {
            return createInstance(ConfigReader.getInstance().getReportPath());
        }
        return extentReports;
    }
}
