package com.projectsphere.automation.utils;

import com.projectsphere.automation.constants.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(RetryAnalyzer.class);

    private int retryCount = 0;
    private final int maxRetryCount = ConfigReader.getInstance().getRetryCount();

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            logger.warn("Retrying test '{}' – attempt {}/{}", result.getName(), retryCount, maxRetryCount);
            return true;
        }
        logger.error("Test '{}' exhausted {} retries and is marked FAILED", result.getName(), maxRetryCount);
        return false;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }
}
