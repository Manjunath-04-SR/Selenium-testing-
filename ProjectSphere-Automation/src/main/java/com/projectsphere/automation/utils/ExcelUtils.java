package com.projectsphere.automation.utils;

import com.projectsphere.automation.constants.AppConstants;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);
    private static ExcelUtils instance;
    private Workbook workbook;
    private String excelFilePath;

    private ExcelUtils() {
        String configuredPath = ConfigReader.getInstance().getExcelPath();

        // 1. Try exact file path from config (absolute or relative to working directory)
        File directFile = new File(configuredPath);
        if (directFile.exists() && directFile.isFile()) {
            excelFilePath = directFile.getAbsolutePath();
            logger.info("Found Excel file at configured path: {}", excelFilePath);
        }

        // 2. Try relative to project root (for IntelliJ runs)
        if (excelFilePath == null) {
            File projectRelative = new File(System.getProperty("user.dir"), configuredPath);
            if (projectRelative.exists() && projectRelative.isFile()) {
                excelFilePath = projectRelative.getAbsolutePath();
                logger.info("Found Excel file relative to project root: {}", excelFilePath);
            }
        }

        // 3. Scan configured path as directory
        if (excelFilePath == null) {
            File dir = new File(configuredPath);
            if (dir.exists() && dir.isDirectory()) {
                File[] xlsx = dir.listFiles((d, name) -> name.endsWith(".xlsx"));
                if (xlsx != null && xlsx.length > 0) {
                    excelFilePath = xlsx[0].getAbsolutePath();
                    logger.info("Found Excel file by directory scan: {}", excelFilePath);
                }
            }
        }

        // 4. Fallback: classpath lookup
        if (excelFilePath == null) {
            String[] classpathNames = {
                "testdata/ProjectSphere_TestDesign.xlsx",
                "testdata/ProjectSphere_TestDesign 1.xlsx",
                "ProjectSphere_TestDesign.xlsx"
            };
            for (String name : classpathNames) {
                try {
                    java.net.URL url = getClass().getClassLoader().getResource(name);
                    if (url != null) {
                        excelFilePath = url.getPath();
                        logger.info("Found Excel file on classpath: {}", excelFilePath);
                        break;
                    }
                } catch (Exception ignored) {}
            }
        }

        if (excelFilePath == null || excelFilePath.isEmpty()) {
            logger.warn("Excel test data file not found at path '{}'. ExcelUtils will return empty maps.", configuredPath);
            return;
        }
        logger.info("Loading Excel test data from: {}", excelFilePath);
        try (FileInputStream fis = new FileInputStream(excelFilePath)) {
            workbook = new XSSFWorkbook(fis);
        } catch (IOException e) {
            logger.error("Failed to open Excel file: {}", e.getMessage(), e);
        }
    }

    public static synchronized ExcelUtils getInstance() {
        if (instance == null) {
            instance = new ExcelUtils();
        }
        return instance;
    }

    /**
     * Returns metadata for a single test case from the TestCases sheet.
     * Keys: GROUP, TEST_CASE_ID, TEST_DESCRIPTION, DESIGNER, TEST_TYPE,
     *       FRD_PARAGRAPH, TEST_DATA, COMPLEXITY, STEP_1_DESC, STEP_1_EXPECTED, etc.
     */
    public Map<String, String> getTestData(String sheetName, String testCaseId) {
        Map<String, String> result = new LinkedHashMap<>();
        if (workbook == null) {
            logger.warn("Workbook not loaded; returning empty test data for {}", testCaseId);
            return result;
        }
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            logger.warn("Sheet '{}' not found in workbook", sheetName);
            return result;
        }

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return result;

        // Build column index → header name map
        Map<Integer, String> colHeaders = new LinkedHashMap<>();
        for (Cell cell : headerRow) {
            colHeaders.put(cell.getColumnIndex(), getCellValue(cell));
        }

        // Find the starting row for the requested test case ID
        int tcIdColIdx = getColumnIndex(colHeaders, AppConstants.COL_TEST_CASE_ID);
        if (tcIdColIdx < 0) tcIdColIdx = 1; // default to column B

        boolean capturing = false;
        int stepNumber = 0;

        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;

            String cellId = getCellValue(row, tcIdColIdx);

            if (testCaseId.equalsIgnoreCase(cellId)) {
                capturing = true;
                stepNumber = 0;
                // Capture header-level fields from this row
                for (Map.Entry<Integer, String> entry : colHeaders.entrySet()) {
                    int colIdx = entry.getKey();
                    String header = entry.getValue();
                    if (!header.isEmpty()) {
                        String key = normalizeKey(header);
                        result.put(key, getCellValue(row, colIdx));
                    }
                }
            }

            if (capturing) {
                // Capture step data
                int stepDescIdx  = getColumnIndex(colHeaders, AppConstants.COL_STEP_DESC);
                int stepExpIdx   = getColumnIndex(colHeaders, AppConstants.COL_STEP_EXPECTED);
                int stepNumIdx   = getColumnIndex(colHeaders, AppConstants.COL_STEP_NUM);

                if (stepDescIdx >= 0) {
                    String stepDesc = getCellValue(row, stepDescIdx);
                    String stepExp  = stepExpIdx >= 0 ? getCellValue(row, stepExpIdx) : "";
                    String stepNum  = stepNumIdx >= 0 ? getCellValue(row, stepNumIdx) : "";

                    if (!stepDesc.isEmpty()) {
                        stepNumber++;
                        int sn = stepNum.isEmpty() ? stepNumber : (int) Double.parseDouble(stepNum);
                        result.put("STEP_" + sn + "_DESC",     stepDesc);
                        result.put("STEP_" + sn + "_EXPECTED", stepExp);
                    }
                }

                // Stop when a new test case ID row is encountered (after capturing has started)
                if (!cellId.isEmpty() && !cellId.equalsIgnoreCase(testCaseId) && stepNumber > 0) {
                    break;
                }
            }
        }

        logger.debug("Loaded {} data entries for test case '{}'", result.size(), testCaseId);
        return result;
    }

    /**
     * Returns all data rows for the given sheet as a list of maps.
     * Each map key is the column header (normalized).
     */
    public List<Map<String, String>> getAllRowsForSheet(String sheetName) {
        List<Map<String, String>> rows = new ArrayList<>();
        if (workbook == null) return rows;

        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            logger.warn("Sheet '{}' not found", sheetName);
            return rows;
        }

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return rows;

        Map<Integer, String> colHeaders = new LinkedHashMap<>();
        for (Cell cell : headerRow) {
            colHeaders.put(cell.getColumnIndex(), getCellValue(cell));
        }

        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;
            Map<String, String> rowData = new LinkedHashMap<>();
            for (Map.Entry<Integer, String> entry : colHeaders.entrySet()) {
                rowData.put(normalizeKey(entry.getValue()), getCellValue(row, entry.getKey()));
            }
            rows.add(rowData);
        }
        return rows;
    }

    /** Returns the cell value as a String regardless of the underlying cell type. */
    public String getCellValue(Row row, int colIndex) {
        if (row == null) return "";
        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return getCellValue(cell);
    }

    public String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:   return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                double d = cell.getNumericCellValue();
                return (d == Math.floor(d) && !Double.isInfinite(d))
                        ? String.valueOf((long) d)
                        : String.valueOf(d);
            case BOOLEAN:  return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try { return cell.getStringCellValue().trim(); }
                catch (Exception e) {
                    try { return String.valueOf((long) cell.getNumericCellValue()); }
                    catch (Exception ex) { return ""; }
                }
            case BLANK:
            default:       return "";
        }
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private int getColumnIndex(Map<Integer, String> headers, String headerName) {
        for (Map.Entry<Integer, String> e : headers.entrySet()) {
            if (headerName.equalsIgnoreCase(e.getValue())) return e.getKey();
        }
        return -1;
    }

    private String normalizeKey(String header) {
        return header.trim().toUpperCase().replace(" ", "_").replace("#", "NUM");
    }
}
