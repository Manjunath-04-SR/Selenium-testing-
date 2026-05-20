package com.projectsphere.automation.utils;

import com.projectsphere.automation.constants.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    private static ConfigReader instance;
    private final Properties properties = new Properties();

    private ConfigReader() {
        String env = System.getProperty("env", "dev");
        String fileName = env.equals("dev") ? "config.properties" : "config-" + env + ".properties";
        logger.info("Loading configuration from: {}", fileName);
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) {
                logger.warn("Config file '{}' not found; falling back to config.properties", fileName);
                try (InputStream fallback = getClass().getClassLoader().getResourceAsStream("config.properties")) {
                    if (fallback != null) {
                        properties.load(fallback);
                    } else {
                        throw new RuntimeException("config.properties not found on classpath");
                    }
                }
            } else {
                properties.load(in);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration: " + e.getMessage(), e);
        }
        logger.info("Configuration loaded successfully for env: {}", env);
    }

    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    public String get(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isEmpty()) {
            return systemValue;
        }
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Config key '{}' not found; returning empty string", key);
            return "";
        }
        return value.trim();
    }

    public String getBaseUrl() {
        return get(AppConstants.KEY_BASE_URL);
    }

    public String getBrowser() {
        return get(AppConstants.KEY_BROWSER);
    }

    public int getImplicitWait() {
        try {
            return Integer.parseInt(get(AppConstants.KEY_IMPLICIT_WAIT));
        } catch (NumberFormatException e) {
            return AppConstants.IMPLICIT_WAIT;
        }
    }

    public int getExplicitWait() {
        try {
            return Integer.parseInt(get(AppConstants.KEY_EXPLICIT_WAIT));
        } catch (NumberFormatException e) {
            return AppConstants.EXPLICIT_WAIT;
        }
    }

    public int getRetryCount() {
        try {
            return Integer.parseInt(get(AppConstants.KEY_RETRY_COUNT));
        } catch (NumberFormatException e) {
            return 2;
        }
    }

    public String getReportPath() {
        return get(AppConstants.KEY_REPORT_PATH);
    }

    public String getScreenshotPath() {
        return get(AppConstants.KEY_SCREENSHOT_PATH);
    }

    public String getExcelPath() {
        return get(AppConstants.KEY_EXCEL_PATH);
    }

    public String getAdminEmail() {
        return get(AppConstants.KEY_ADMIN_EMAIL);
    }

    public String getAdminPassword() {
        return get(AppConstants.KEY_ADMIN_PASSWORD);
    }

    public String getPmEmail() {
        return get(AppConstants.KEY_PM_EMAIL);
    }

    public String getPmPassword() {
        return get(AppConstants.KEY_PM_PASSWORD);
    }

    public String getDeveloperEmail() {
        return get(AppConstants.KEY_DEV_EMAIL);
    }

    public String getDeveloperPassword() {
        return get(AppConstants.KEY_DEV_PASSWORD);
    }
}
