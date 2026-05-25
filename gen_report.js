const {
  Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell,
  Header, Footer, AlignmentType, HeadingLevel, LevelFormat,
  PageNumber, TableOfContents, BorderStyle, WidthType, ShadingType
} = require('docx');
const fs = require('fs');

// ── Helpers ───────────────────────────────────────────────────────────────────

const H1_COLOR  = "1F5C99";
const H2_COLOR  = "2E75B6";
const BODY_COLOR = "1A1A1A";
const CODE_BG   = "F2F2F2";
const CODE_COLOR = "C0392B";

function h1(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_1,
    pageBreakBefore: true,
    spacing: { before: 240, after: 280 },
    children: [new TextRun({ text, font: "Arial", size: 36, bold: true, color: H1_COLOR })]
  });
}

function h2(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_2,
    spacing: { before: 280, after: 120 },
    children: [new TextRun({ text, font: "Arial", size: 28, bold: true, color: H2_COLOR })]
  });
}

function body(text, opts = {}) {
  return new Paragraph({
    spacing: { before: 60, after: 100 },
    children: [new TextRun({
      text,
      font: "Arial",
      size: 22,
      color: BODY_COLOR,
      bold: opts.bold || false,
      italics: opts.italic || false
    })]
  });
}

function bodyMixed(runs) {
  return new Paragraph({
    spacing: { before: 60, after: 100 },
    children: runs.map(r => {
      if (r.code) {
        return new TextRun({ text: r.text, font: "Courier New", size: 20, color: CODE_COLOR, bold: false });
      }
      return new TextRun({ text: r.text, font: "Arial", size: 22, color: BODY_COLOR, bold: r.bold || false });
    })
  });
}

function code(text) {
  return new Paragraph({
    spacing: { before: 40, after: 40 },
    indent: { left: 720 },
    shading: { fill: CODE_BG, type: ShadingType.CLEAR },
    children: [new TextRun({ text, font: "Courier New", size: 20, color: CODE_COLOR })]
  });
}

function bullet(text, opts = {}) {
  return new Paragraph({
    numbering: { reference: "bullets", level: 0 },
    spacing: { before: 40, after: 60 },
    children: [
      ...(opts.label ? [new TextRun({ text: opts.label, font: "Arial", size: 22, bold: true, color: H2_COLOR })] : []),
      new TextRun({ text: opts.label ? " — " + text : text, font: "Arial", size: 22, color: BODY_COLOR })
    ]
  });
}

function numbered(text, n) {
  return new Paragraph({
    numbering: { reference: "numbers" + n, level: 0 },
    spacing: { before: 40, after: 60 },
    children: [new TextRun({ text, font: "Arial", size: 22, color: BODY_COLOR })]
  });
}

function spacer() {
  return new Paragraph({ spacing: { before: 80, after: 80 }, children: [new TextRun("  ")] });
}

function divider() {
  return new Paragraph({
    spacing: { before: 160, after: 160 },
    border: { bottom: { style: BorderStyle.SINGLE, size: 4, color: "DDDDDD", space: 1 } },
    children: [new TextRun("")]
  });
}

// ── Comparison Table ──────────────────────────────────────────────────────────

function makeComparisonTable() {
  const rows_data = [
    ["Aspect", "TestNG", "Cucumber BDD"],
    ["Test file type", "Java (.java)", "Gherkin (.feature) + Java step defs"],
    ["Test style", "Code annotations (@Test)", "Plain English (Given-When-Then)"],
    ["Readable by business?", "No", "Yes"],
    ["Suite configuration", "testng.xml", "cucumber-testng.xml + @CucumberOptions"],
    ["Browser setup", "BaseTest @BeforeMethod", "CucumberHooks @Before"],
    ["Browser teardown", "BaseTest @AfterMethod", "CucumberHooks @After"],
    ["Shared WebDriver state", "ThreadLocal<WebDriver>", "PicoContainer ScenarioContext"],
    ["Page objects used", "Yes (directly)", "Yes (via step definitions)"],
    ["HTML Reports", "ExtentReports HTML", "Cucumber HTML + JSON"],
    ["Screenshot on failure", "ExtentReportListener", "scenario.attach() in @After hook"],
    ["Test data source", "Excel (Apache POI)", "Feature file parameters + config.properties"],
    ["Test case IDs", "PS_TC001 to PS_TC041", "Scenario names with @tags"],
    ["Run command", "mvn test", "mvn test -DsuiteFile=cucumber-testng.xml"],
  ];

  const colWidths = [2400, 3480, 3480];
  const borderDef = { style: BorderStyle.SINGLE, size: 1, color: "BBBBBB" };
  const borders = { top: borderDef, bottom: borderDef, left: borderDef, right: borderDef };

  return new Table({
    width: { size: 9360, type: WidthType.DXA },
    columnWidths: colWidths,
    rows: rows_data.map((row, ri) => {
      const isHeader = ri === 0;
      const isEven   = ri % 2 === 0 && !isHeader;
      return new TableRow({
        tableHeader: isHeader,
        children: row.map((cell, ci) => {
          return new TableCell({
            borders,
            width: { size: colWidths[ci], type: WidthType.DXA },
            margins: { top: 80, bottom: 80, left: 120, right: 120 },
            shading: isHeader
              ? { fill: "1F5C99", type: ShadingType.CLEAR }
              : isEven
                ? { fill: "EBF2FA", type: ShadingType.CLEAR }
                : { fill: "FFFFFF", type: ShadingType.CLEAR },
            children: [new Paragraph({
              children: [new TextRun({
                text: cell,
                font: "Arial",
                size: 20,
                bold: isHeader,
                color: isHeader ? "FFFFFF" : BODY_COLOR
              })]
            })]
          });
        })
      });
    })
  });
}

// ── Document children ─────────────────────────────────────────────────────────

const children = [];

// ── Cover ─────────────────────────────────────────────────────────────────────
children.push(
  new Paragraph({
    alignment: AlignmentType.CENTER,
    spacing: { before: 2880, after: 200 },
    children: [new TextRun({ text: "ProjectSphere Automation Framework", bold: true, font: "Arial", size: 56, color: H1_COLOR })]
  }),
  new Paragraph({
    alignment: AlignmentType.CENTER,
    spacing: { before: 0, after: 200 },
    children: [new TextRun({ text: "TestNG & Cucumber BDD", bold: true, font: "Arial", size: 44, color: H2_COLOR })]
  }),
  new Paragraph({
    alignment: AlignmentType.CENTER,
    spacing: { before: 0, after: 480 },
    border: { bottom: { style: BorderStyle.SINGLE, size: 6, color: H2_COLOR, space: 1 } },
    children: [new TextRun({ text: "How It Works", font: "Arial", size: 32, color: "555555", italics: true })]
  }),
  new Paragraph({
    alignment: AlignmentType.CENTER,
    spacing: { before: 480, after: 120 },
    children: [new TextRun({ text: "A complete explanation of the testing architecture, browser lifecycle,", font: "Arial", size: 22, color: "777777" })]
  }),
  new Paragraph({
    alignment: AlignmentType.CENTER,
    spacing: { before: 0, after: 120 },
    children: [new TextRun({ text: "page objects, reports, and how both frameworks co-exist.", font: "Arial", size: 22, color: "777777" })]
  }),
  new Paragraph({
    alignment: AlignmentType.CENTER,
    spacing: { before: 240, after: 120 },
    children: [new TextRun({ text: "Prepared: May 2026", font: "Arial", size: 20, color: "999999" })]
  })
);

// ── TOC ───────────────────────────────────────────────────────────────────────
children.push(new Paragraph({ pageBreakBefore: true, children: [new TextRun("")] }));
children.push(new TableOfContents("Table of Contents", { hyperlink: true, headingStyleRange: "1-2" }));

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 1 — OVERVIEW
// ═══════════════════════════════════════════════════════════════════════════════
children.push(h1("Section 1: Overview"));
children.push(body("This report explains how TestNG and Cucumber BDD are used inside the ProjectSphere Selenium Automation Framework. The framework automates testing of the ProjectSphere Angular 17 Single Page Application (SPA)."));
children.push(spacer());
children.push(body("The application has three user roles:", { bold: true }));
children.push(bullet("Admin — manages projects, teams, and project managers"));
children.push(bullet("Project Manager (PM) — manages their own projects and teams"));
children.push(bullet("Developer — works on assigned tasks, issues, and defects"));
children.push(spacer());
children.push(body("The framework uses TWO testing approaches running side by side:", { bold: true }));
children.push(bullet("TestNG — traditional Java annotation-driven test cases (TC001 to TC041), tied to an Excel test design sheet and formal test case IDs."));
children.push(bullet("Cucumber BDD — behavior-driven testing using plain English Gherkin feature files, readable by business analysts and product owners."));
children.push(spacer());
children.push(body("Both approaches share the same Selenium page objects. Neither interferes with the other. They can be run independently with separate Maven commands."));

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 2 — PROJECT STRUCTURE
// ═══════════════════════════════════════════════════════════════════════════════
children.push(h1("Section 2: Project Structure"));
children.push(body("The framework follows the Page Object Model (POM) design pattern. Every page or dialog in the application is represented by a dedicated Java class. Test logic is kept separate from Selenium interaction logic."));
children.push(spacer());
children.push(h2("2.1 Main Source (src/main/java)"));
children.push(body("These files are shared by BOTH TestNG and Cucumber:"));
children.push(spacer());
children.push(code("base/BaseTest.java             — WebDriver setup and teardown for TestNG tests"));
children.push(code("pages/LoginPage.java           — Login page locators and actions"));
children.push(code("pages/AdminDashboardPage.java  — Admin dashboard interactions"));
children.push(code("pages/PMDashboardPage.java     — PM dashboard interactions"));
children.push(code("pages/CreateProjectDialog.java — Create Project modal dialog"));
children.push(code("pages/CreateTeamDialog.java    — Create Team modal dialog"));
children.push(code("pages/EditTeamPage.java        — Edit Team inline form"));
children.push(code("pages/ManageTeamsAdminPage.java — Admin Manage Teams page"));
children.push(code("pages/ManageProjectsPage.java  — Admin Manage Projects page"));
children.push(code("pages/MyProjectsPMPage.java    — PM My Projects page"));
children.push(code("pages/MyTeamsPMPage.java       — PM My Teams page"));
children.push(code("constants/AppConstants.java    — All constants: URLs, timeouts, labels"));
children.push(code("utils/ConfigReader.java        — Reads config.properties"));
children.push(code("utils/ExcelUtils.java          — Reads test data from Excel (Apache POI)"));
children.push(code("utils/ExtentReportListener.java — TestNG listener, screenshots on failure"));
children.push(spacer());
children.push(h2("2.2 Test Source (src/test/java)"));
children.push(body("TestNG test classes:"));
children.push(code("tests/LoginTests.java          — TC001 to TC010 (login scenarios)"));
children.push(code("tests/AdminTests.java          — TC011 to TC027.2 (admin scenarios)"));
children.push(code("tests/PMTests.java             — TC028 to TC041 (PM scenarios)"));
children.push(code("tests/DeveloperTests.java      — Developer test cases"));
children.push(spacer());
children.push(body("Cucumber BDD classes (new, independent layer):"));
children.push(code("cucumber/context/ScenarioContext.java  — Shared WebDriver + ConfigReader"));
children.push(code("cucumber/hooks/CucumberHooks.java      — @Before and @After per scenario"));
children.push(code("cucumber/stepdefs/LoginSteps.java      — Step defs for login feature"));
children.push(code("cucumber/stepdefs/AdminSteps.java      — Step defs for admin feature"));
children.push(code("cucumber/stepdefs/PMSteps.java         — Step defs for PM feature"));
children.push(code("cucumber/runner/CucumberRunner.java    — TestNG-based Cucumber runner"));
children.push(spacer());
children.push(h2("2.3 Test Resources (src/test/resources)"));
children.push(code("features/login.feature         — 4 login BDD scenarios in Gherkin"));
children.push(code("features/admin.feature         — 5 admin BDD scenarios in Gherkin"));
children.push(code("features/pm.feature            — 5 PM BDD scenarios in Gherkin"));
children.push(code("testng.xml                     — TestNG suite config (unchanged)"));
children.push(code("cucumber-testng.xml            — Separate Cucumber suite config"));
children.push(code("config.properties              — Base URL, credentials, wait times"));

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 3 — TESTNG
// ═══════════════════════════════════════════════════════════════════════════════
children.push(h1("Section 3: TestNG — How It Works in This Project"));
children.push(h2("3.1 What is TestNG?"));
children.push(body("TestNG (Test Next Generation) is the core testing framework for all formal test cases (TC001 to TC041). It provides Java annotations for setup/teardown, test ordering, grouping, parallel execution, and HTML report generation. Every test class extends BaseTest to inherit the WebDriver lifecycle."));

children.push(h2("3.2 BaseTest — The Foundation of All TestNG Tests"));
children.push(body("BaseTest.java is the most important class in the TestNG layer. Every test class (LoginTests, AdminTests, PMTests, DeveloperTests) extends it. It manages the browser from the moment a test starts to the moment it finishes."));
children.push(spacer());
children.push(body("What BaseTest does:", { bold: true }));
children.push(bullet("@BeforeSuite — runs ONCE before all tests. Initializes the ExtentReports HTML report at test-output/reports/ExtentReport.html."));
children.push(bullet("@BeforeMethod — runs before EVERY individual test method:"));
children.push(new Paragraph({ indent: { left: 1080 }, spacing: { before: 40, after: 40 }, numbering: { reference: "sub1", level: 0 }, children: [new TextRun({ text: "Reads browser type and environment from config", font: "Arial", size: 22, color: BODY_COLOR })] }));
children.push(new Paragraph({ indent: { left: 1080 }, spacing: { before: 40, after: 40 }, numbering: { reference: "sub2", level: 0 }, children: [new TextRun({ text: "Creates a new ChromeDriver with automation detection disabled", font: "Arial", size: 22, color: BODY_COLOR })] }));
children.push(new Paragraph({ indent: { left: 1080 }, spacing: { before: 40, after: 40 }, numbering: { reference: "sub3", level: 0 }, children: [new TextRun({ text: "Sets implicit wait (10s) and page load timeout (90s)", font: "Arial", size: 22, color: BODY_COLOR })] }));
children.push(new Paragraph({ indent: { left: 1080 }, spacing: { before: 40, after: 40 }, numbering: { reference: "sub4", level: 0 }, children: [new TextRun({ text: "Navigates to the app URL on Render's free tier", font: "Arial", size: 22, color: BODY_COLOR })] }));
children.push(new Paragraph({ indent: { left: 1080 }, spacing: { before: 40, after: 40 }, numbering: { reference: "sub5", level: 0 }, children: [new TextRun({ text: "Handles the Angular landing page: if login form is not directly visible, clicks the Sign In nav button and waits up to 90 seconds for the Angular login form to load", font: "Arial", size: 22, color: BODY_COLOR })] }));
children.push(bullet("@AfterMethod — runs after every test. Quits the browser with driver.quit()."));
children.push(bullet("@AfterSuite — runs ONCE after all tests. Flushes ExtentReports to save the HTML file."));
children.push(spacer());
children.push(bodyMixed([{ text: "Why 90 seconds? " , bold: true }, { text: "The app is hosted on Render's free tier which cold-starts after inactivity. The backend API can take 90-120 seconds to respond on the first request after a sleep period." }]));

children.push(h2("3.3 WebDriver Thread Safety"));
children.push(body("BaseTest stores the WebDriver in a ThreadLocal to support parallel execution:"));
children.push(code("private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();"));
children.push(body("Each test thread gets its own completely isolated WebDriver instance. Multiple tests can run simultaneously without sharing a browser or interfering with each other."));

children.push(h2("3.4 How a TestNG Test is Written"));
children.push(body("Every test method follows this consistent pattern:"));
children.push(bullet("Get the driver — BaseTest.getDriver() returns the ThreadLocal WebDriver"));
children.push(bullet("Create a page object — new LoginPage(driver), new AdminDashboardPage(driver)"));
children.push(bullet("Call page object methods — enterEmail(), clickSignIn(), isDashboardDisplayed()"));
children.push(bullet("Assert with SoftAssert — collects all failures before reporting"));
children.push(bullet("Call softAssert.assertAll() — throws once at the end if any assertion failed"));
children.push(spacer());
children.push(body("Example — TC028 (PM can see dashboard):", { bold: true }));
children.push(code("PMDashboardPage dashboard = loginPage.loginAsPM(email, password);"));
children.push(code("soft.assertTrue(dashboard.isDashboardDisplayed(), \"PM dashboard visible\");"));
children.push(code("soft.assertAll();"));

children.push(h2("3.5 TestNG Annotations Used in This Project"));
children.push(bullet("@BeforeSuite(alwaysRun=true)", { label: "@BeforeSuite" }));
children.push(body("    Runs once before all tests. Initializes ExtentReports."));
children.push(bullet("@AfterSuite(alwaysRun=true)", { label: "@AfterSuite" }));
children.push(body("    Runs once after all tests. Flushes/saves the HTML report."));
children.push(bullet("@BeforeMethod(alwaysRun=true)", { label: "@BeforeMethod" }));
children.push(body("    Runs before every @Test method. Creates browser, navigates to app."));
children.push(bullet("@AfterMethod(alwaysRun=true)", { label: "@AfterMethod" }));
children.push(body("    Runs after every @Test method. Quits the browser."));
children.push(bullet("@Test(priority=N)", { label: "@Test(priority)" }));
children.push(body("    Controls test execution order. Priority 1 runs before priority 2. All test cases have sequential priorities to run in the correct order."));
children.push(bullet("@Test(groups={\"smoke\",\"regression\"})", { label: "@Test(groups)" }));
children.push(body("    Tags a test. testng.xml can include/exclude groups to run only smoke or only regression tests."));
children.push(bullet("@Listeners({ExtentReportListener.class})", { label: "@Listeners" }));
children.push(body("    Hooks the report listener. It fires onTestSuccess, onTestFailure, onTestSkipped to update the HTML report in real-time."));

children.push(h2("3.6 SoftAssert vs Hard Assert"));
children.push(body("SoftAssert is used throughout this project to verify multiple conditions in one test without stopping on the first failure:"));
children.push(code("SoftAssert soft = new SoftAssert();"));
children.push(code("soft.assertTrue(dashboard.isDashboardDisplayed(), \"Dashboard visible\");"));
children.push(code("soft.assertEquals(title, \"PM Dashboard\", \"Correct title\");"));
children.push(code("soft.assertAll();  // throws here if ANY of the above failed"));
children.push(body("Hard assertions (Assert.assertTrue) are only used when a failure at that point makes it impossible to continue the test (e.g., if the login itself fails, there is no point checking the dashboard)."));

children.push(h2("3.7 Test Data from Excel"));
children.push(body("Test data is loaded from ProjectSphere_TestDesign.xlsx using Apache POI via ExcelUtils. Each row has a TEST CASE ID (e.g., PS_TC028). The test method reads the row and extracts inputs from columns like TEST DATA, STEP DESCRIPTION, and STEP EXPECTED RESULTS. This cleanly separates test data from test code."));

children.push(h2("3.8 testng.xml — Suite Configuration"));
children.push(body("The testng.xml file controls which tests run:"));
children.push(bullet("Smoke Tests — runs only @Test(groups=\"smoke\") methods"));
children.push(bullet("Regression Tests — runs all @Test(groups=\"regression\") methods"));
children.push(bullet("browser parameter — Chrome/Firefox/Edge, passed to @BeforeMethod"));
children.push(bullet("env parameter — chooses which config.properties file (dev/staging/prod)"));
children.push(bullet("thread-count=\"1\" — sequential execution (safe for shared Render backend)"));
children.push(spacer());
children.push(body("This file is NEVER modified by the Cucumber integration."));

children.push(h2("3.9 ExtentReports — HTML Report"));
children.push(body("The ExtentReportListener is a TestNG ITestListener that automatically:"));
children.push(bullet("Records PASS / FAIL / SKIP for every test method"));
children.push(bullet("Captures a screenshot on failure and embeds it in the report"));
children.push(bullet("Shows test duration, log messages, and error stack traces"));
children.push(bullet("Saves the complete report to: test-output/reports/ExtentReport.html"));

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 4 — CUCUMBER BDD
// ═══════════════════════════════════════════════════════════════════════════════
children.push(h1("Section 4: Cucumber BDD — How It Works in This Project"));
children.push(h2("4.1 What is Cucumber BDD?"));
children.push(body("Cucumber is a Behavior-Driven Development (BDD) framework that bridges the gap between technical teams and business stakeholders. Tests are written in plain English using the Gherkin language (Given-When-Then syntax). Under the hood, each plain English step maps to a Java method called a Step Definition."));
children.push(spacer());
children.push(body("In this project, Cucumber is an independent, additive test layer. It does NOT replace TestNG — both co-exist. The Cucumber layer reuses all the same page objects, so there is zero Selenium code duplication."));

children.push(h2("4.2 Feature Files — Plain English Test Cases"));
children.push(body("Feature files are stored in src/test/resources/features/. Each file describes one area of the application using Gherkin keywords:"));
children.push(spacer());
children.push(bullet("Feature — Title and description of what is being tested"));
children.push(bullet("Background — Steps that run before EVERY scenario in the file (equivalent to @BeforeMethod)"));
children.push(bullet("Scenario — One specific test case written in plain English"));
children.push(bullet("Given — Sets up the initial state (e.g., user is on the login page)"));
children.push(bullet("When — The action performed (e.g., user clicks Sign In)"));
children.push(bullet("Then — The expected outcome (e.g., dashboard is displayed)"));
children.push(bullet("And / But — Adds more steps of the same type without repeating the keyword"));
children.push(bullet("@tagname — Labels scenarios for selective filtering"));
children.push(spacer());
children.push(body("Example from login.feature:", { bold: true }));
children.push(code("@admin"));
children.push(code("Scenario: Admin logs in with valid credentials"));
children.push(code("  Given the user is on the login page"));
children.push(code("  When the admin enters valid credentials and clicks Sign In"));
children.push(code("  Then the admin dashboard should be displayed"));
children.push(spacer());
children.push(body("Example from admin.feature showing Background:", { bold: true }));
children.push(code("Background:"));
children.push(code("  Given the admin is logged in"));
children.push(code(""));
children.push(code("Scenario: Admin can navigate to Manage Teams"));
children.push(code("  When the admin navigates to Manage Teams"));
children.push(code("  Then the Manage Teams page should be displayed"));
children.push(spacer());
children.push(body("The Background Given step runs automatically before each scenario in admin.feature, logging in the admin user before every test — exactly like @BeforeMethod in TestNG."));

children.push(h2("4.3 Tags — Selective Execution"));
children.push(body("Tags filter which scenarios run without changing any code:"));
children.push(bullet("@smoke — Quick critical path scenarios"));
children.push(bullet("@regression — Full regression scenarios"));
children.push(bullet("@login — Login-specific scenarios only"));
children.push(bullet("@admin — Admin feature scenarios only"));
children.push(bullet("@pm — PM feature scenarios only"));
children.push(bullet("@negative — Negative/error path scenarios"));
children.push(bullet("@wip — Work in progress (excluded from run by default in CucumberRunner)"));

children.push(h2("4.4 Step Definitions — The Java Behind the Gherkin"));
children.push(body("Step definitions connect the plain English feature file steps to actual Java/Selenium code. They are annotated with @Given, @When, or @Then and match the step text exactly:"));
children.push(spacer());
children.push(body("Feature file step:", { bold: true }));
children.push(code("When the admin enters valid credentials and clicks Sign In"));
children.push(spacer());
children.push(body("Matching Java step definition in LoginSteps.java:", { bold: true }));
children.push(code("@When(\"the admin enters valid credentials and clicks Sign In\")"));
children.push(code("public void theAdminEntersValidCredentialsAndClicksSignIn() {"));
children.push(code("    loginPage = new LoginPage(context.getDriver());"));
children.push(code("    loginPage.enterEmail(context.getConfig().getAdminEmail());"));
children.push(code("    loginPage.enterPassword(context.getConfig().getAdminPassword());"));
children.push(code("    loginPage.clickSignIn();"));
children.push(code("}"));
children.push(spacer());
children.push(body("For parameterized steps, {string} captures the value from the feature file:"));
children.push(code("Feature:  When the admin creates a team with name \"BDD Test Team\""));
children.push(code("Java:     @When(\"the admin creates a team with name {string}\")"));
children.push(code("          public void theAdminCreatesATeam(String teamName) { ... }"));
children.push(spacer());
children.push(body("All step definition methods call the exact same page objects (LoginPage, AdminDashboardPage, PMDashboardPage, etc.) that are used by the TestNG tests. There is zero Selenium code duplication."));

children.push(h2("4.5 ScenarioContext — Sharing State with PicoContainer"));
children.push(body("Multiple step definition classes (LoginSteps, AdminSteps, PMSteps) need to share the same WebDriver within one scenario. This is solved using PicoContainer dependency injection."));
children.push(spacer());
children.push(body("ScenarioContext holds:", { bold: true }));
children.push(bullet("The WebDriver instance for the running scenario"));
children.push(bullet("The ConfigReader instance for credentials and URLs"));
children.push(bullet("initDriver() — creates ChromeDriver with the same options as BaseTest"));
children.push(bullet("navigateToApp() — handles the Render landing page, same logic as BaseTest"));
children.push(bullet("quitDriver() — closes the browser after the scenario"));
children.push(spacer());
children.push(body("PicoContainer creates ONE ScenarioContext per scenario and injects the same object into every step class via constructor:", { bold: true }));
children.push(code("public LoginSteps(ScenarioContext context) { this.context = context; }"));
children.push(code("public AdminSteps(ScenarioContext context) { this.context = context; }"));
children.push(code("public PMSteps(ScenarioContext context)   { this.context = context; }"));
children.push(spacer());
children.push(body("All three classes share the same WebDriver instance. State flows naturally between Given, When, and Then steps even across different step definition classes — no static variables or global state needed."));

children.push(h2("4.6 CucumberHooks — Browser Lifecycle per Scenario"));
children.push(body("CucumberHooks.java manages the browser for each Cucumber scenario. It mirrors exactly what BaseTest does for TestNG but uses Cucumber's @Before and @After annotations."));
children.push(spacer());
children.push(body("@Before (runs before each scenario):", { bold: true }));
children.push(bullet("Calls context.initDriver() — launches Chrome with automation detection disabled"));
children.push(bullet("Calls context.navigateToApp() — navigates to the app URL, handles landing page, waits up to 90 seconds for Angular login form"));
children.push(spacer());
children.push(body("@After (runs after each scenario):", { bold: true }));
children.push(bullet("Checks scenario.isFailed() — if true, takes a screenshot"));
children.push(bullet("Attaches screenshot bytes to the Cucumber report with scenario.attach()"));
children.push(bullet("Always calls context.quitDriver() to close the browser, pass or fail"));

children.push(h2("4.7 CucumberRunner — The Entry Point"));
children.push(body("CucumberRunner.java connects Cucumber to TestNG. It extends AbstractTestNGCucumberTests and uses @CucumberOptions to configure everything:"));
children.push(spacer());
children.push(code("@CucumberOptions("));
children.push(code("  features = \"src/test/resources/features\","));
children.push(code("  glue = { \"...cucumber.hooks\", \"...cucumber.stepdefs\" },"));
children.push(code("  plugin = { \"pretty\","));
children.push(code("            \"html:target/cucumber-reports/cucumber-report.html\","));
children.push(code("            \"json:target/cucumber-reports/cucumber.json\" },"));
children.push(code("  monochrome = true,"));
children.push(code("  tags = \"not @wip\""));
children.push(code(")"));
children.push(spacer());
children.push(body("The @DataProvider(parallel=false) override runs scenarios sequentially — safe for the shared Render backend."));

children.push(h2("4.8 cucumber-testng.xml — Separate Suite File"));
children.push(body("A new dedicated TestNG XML was created for Cucumber tests:"));
children.push(code("src/test/resources/cucumber-testng.xml"));
children.push(body("This file points only to CucumberRunner. The original testng.xml is completely untouched — running mvn test still executes only the TestNG test classes as before."));

children.push(h2("4.9 Cucumber Reports"));
children.push(body("After a Cucumber run, two reports are generated:"));
children.push(bullet("target/cucumber-reports/cucumber-report.html — Visual step-by-step HTML report with scenario status, screenshots on failure embedded inline"));
children.push(bullet("target/cucumber-reports/cucumber.json — Machine-readable JSON for CI tools (Jenkins Cucumber plugin, Allure)"));

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 5 — HOW THEY WORK TOGETHER
// ═══════════════════════════════════════════════════════════════════════════════
children.push(h1("Section 5: How TestNG and Cucumber Work Together"));
children.push(h2("5.1 Shared Page Objects — Zero Duplication"));
children.push(body("Both layers use exactly the same page objects from src/main/java. The page objects are written once and reused everywhere. Neither TestNG tests nor Cucumber step definitions contain any direct Selenium findElement() or WebDriverWait calls — all interaction code lives only in the page objects."));

children.push(h2("5.2 Same Config and Credentials"));
children.push(body("Both layers call ConfigReader.getInstance() to read config.properties. The same BASE_URL, admin email/password, PM email/password, timeout values, and file paths are shared. There is one source of truth for all configuration."));

children.push(h2("5.3 Independent Execution — No Interference"));
children.push(code("mvn test                                            — runs ONLY TestNG tests"));
children.push(code("mvn test -DsuiteFile=...cucumber-testng.xml         — runs ONLY Cucumber tests"));
children.push(spacer());
children.push(body("Each framework manages its own WebDriver lifecycle completely independently:"));
children.push(bullet("TestNG: BaseTest @BeforeMethod creates driver, @AfterMethod quits driver"));
children.push(bullet("Cucumber: CucumberHooks @Before creates driver, @After quits driver"));
children.push(body("There is no shared driver, no shared browser instance, and no risk of one test affecting the other."));

children.push(h2("5.4 Different Purposes"));
children.push(body("TestNG tests (TC001-TC041):", { bold: true }));
children.push(bullet("Formal test cases with unique IDs (PS_TC001, PS_TC028, etc.)"));
children.push(bullet("Linked to Jira/Zephyr for defect tracking and test reporting"));
children.push(bullet("Excel-driven test data and step documentation"));
children.push(bullet("Primary tool for the QA team for systematic regression testing"));
children.push(spacer());
children.push(body("Cucumber BDD scenarios:", { bold: true }));
children.push(bullet("Plain English, readable by business analysts and product owners"));
children.push(bullet("Scenarios describe user behaviors, not test steps"));
children.push(bullet("Tags enable quick smoke/regression filtering without XML changes"));
children.push(bullet("Serves as living documentation of system behavior"));

children.push(h2("5.5 Flow Comparison — The Same Test, Two Styles"));
children.push(body("TestNG style (PMTests.java — TC028):", { bold: true }));
children.push(code("@Test(priority=1, groups={\"smoke\",\"regression\"})"));
children.push(code("public void TC028_pmCanSeeProjectsOnDashboard() {"));
children.push(code("    WebDriver driver = BaseTest.getDriver();"));
children.push(code("    PMDashboardPage dash = new LoginPage(driver).loginAsPM(email, pass);"));
children.push(code("    SoftAssert soft = new SoftAssert();"));
children.push(code("    soft.assertTrue(dash.isDashboardDisplayed(), \"PM dashboard visible\");"));
children.push(code("    soft.assertAll();"));
children.push(code("}"));
children.push(spacer());
children.push(body("Cucumber BDD style (pm.feature):", { bold: true }));
children.push(code("Scenario: PM dashboard is displayed with quick action cards"));
children.push(code("  Given the PM is logged in"));
children.push(code("  Then the PM dashboard should show quick action cards"));
children.push(spacer());
children.push(body("Both verify the exact same thing. TestNG is formal, code-centric, and traceable. Cucumber is readable, business-facing, and tagable."));

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 6 — HOW TO RUN
// ═══════════════════════════════════════════════════════════════════════════════
children.push(h1("Section 6: How to Run the Tests"));
children.push(h2("6.1 Run TestNG Tests (TC001 to TC041)"));
children.push(body("From the IntelliJ terminal (full suite):"));
children.push(code("mvn test"));
children.push(spacer());
children.push(body("Run a specific test class:"));
children.push(code("mvn test -Dtest=AdminTests"));
children.push(code("mvn test -Dtest=PMTests"));
children.push(code("mvn test -Dtest=LoginTests"));
children.push(spacer());
children.push(body("Run only smoke group:"));
children.push(code("mvn test -Dgroups=smoke"));

children.push(h2("6.2 Run Cucumber BDD Tests"));
children.push(body("Run all Cucumber scenarios:"));
children.push(code("mvn test -DsuiteFile=src/test/resources/cucumber-testng.xml"));
children.push(spacer());
children.push(body("Run with tag filter:"));
children.push(code("mvn test -DsuiteFile=...cucumber-testng.xml -Dcucumber.filter.tags=\"@smoke\""));
children.push(code("mvn test -DsuiteFile=...cucumber-testng.xml -Dcucumber.filter.tags=\"@admin\""));
children.push(code("mvn test -DsuiteFile=...cucumber-testng.xml -Dcucumber.filter.tags=\"@pm\""));
children.push(code("mvn test -DsuiteFile=...cucumber-testng.xml -Dcucumber.filter.tags=\"@regression\""));

children.push(h2("6.3 Run from IntelliJ IDE"));
children.push(bullet("TestNG tests: Right-click AdminTests.java or PMTests.java → Run"));
children.push(bullet("All Cucumber: Right-click CucumberRunner.java → Run"));
children.push(bullet("One scenario: Open .feature file → Right-click any Scenario line → Run"));

children.push(h2("6.4 Report Locations After Run"));
children.push(code("TestNG ExtentReport:    test-output/reports/ExtentReport.html"));
children.push(code("TestNG default:         test-output/index.html"));
children.push(code("Cucumber HTML:          target/cucumber-reports/cucumber-report.html"));
children.push(code("Cucumber JSON (CI):     target/cucumber-reports/cucumber.json"));
children.push(code("Screenshots (TestNG):   test-output/screenshots/"));
children.push(code("Screenshots (Cucumber): Embedded inline in cucumber-report.html"));

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 7 — SUMMARY TABLE
// ═══════════════════════════════════════════════════════════════════════════════
children.push(h1("Section 7: TestNG vs Cucumber — Summary Comparison"));
children.push(body("The table below summarises every key aspect of how TestNG and Cucumber BDD differ in this project:"));
children.push(spacer());
children.push(makeComparisonTable());
children.push(spacer());
children.push(divider());
children.push(new Paragraph({
  alignment: AlignmentType.CENTER,
  spacing: { before: 120, after: 120 },
  children: [new TextRun({ text: "Both frameworks share the same page objects, config, and application URL.", font: "Arial", size: 20, color: "555555", italics: true })]
}));
children.push(new Paragraph({
  alignment: AlignmentType.CENTER,
  spacing: { before: 0, after: 120 },
  children: [new TextRun({ text: "TestNG = formal QA traceability.  Cucumber = business-readable living documentation.", font: "Arial", size: 20, color: "555555", italics: true })]
}));

// ═══════════════════════════════════════════════════════════════════════════════
// DOCUMENT ASSEMBLY
// ═══════════════════════════════════════════════════════════════════════════════

const doc = new Document({
  numbering: {
    config: [
      { reference: "bullets",
        levels: [{ level: 0, format: LevelFormat.BULLET, text: "•", alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 600, hanging: 300 } } } }] },
      { reference: "sub1",
        levels: [{ level: 0, format: LevelFormat.DECIMAL, text: "%1.", alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 1080, hanging: 360 } } } }] },
      { reference: "sub2",
        levels: [{ level: 0, format: LevelFormat.DECIMAL, text: "%1.", alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 1080, hanging: 360 } } } }] },
      { reference: "sub3",
        levels: [{ level: 0, format: LevelFormat.DECIMAL, text: "%1.", alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 1080, hanging: 360 } } } }] },
      { reference: "sub4",
        levels: [{ level: 0, format: LevelFormat.DECIMAL, text: "%1.", alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 1080, hanging: 360 } } } }] },
      { reference: "sub5",
        levels: [{ level: 0, format: LevelFormat.DECIMAL, text: "%1.", alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 1080, hanging: 360 } } } }] },
    ]
  },
  styles: {
    default: { document: { run: { font: "Arial", size: 22 } } },
    paragraphStyles: [
      { id: "Heading1", name: "Heading 1", basedOn: "Normal", next: "Normal", quickFormat: true,
        run: { size: 36, bold: true, font: "Arial", color: H1_COLOR },
        paragraph: { spacing: { before: 240, after: 240 }, outlineLevel: 0 } },
      { id: "Heading2", name: "Heading 2", basedOn: "Normal", next: "Normal", quickFormat: true,
        run: { size: 26, bold: true, font: "Arial", color: H2_COLOR },
        paragraph: { spacing: { before: 200, after: 120 }, outlineLevel: 1 } },
    ]
  },
  sections: [{
    properties: {
      page: {
        size: { width: 12240, height: 15840 },
        margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 }
      }
    },
    headers: {
      default: new Header({
        children: [new Paragraph({
          border: { bottom: { style: BorderStyle.SINGLE, size: 4, color: H2_COLOR, space: 1 } },
          spacing: { after: 100 },
          children: [new TextRun({ text: "ProjectSphere Automation Framework — TestNG & Cucumber BDD", font: "Arial", size: 18, color: "666666", italics: true })]
        })]
      })
    },
    footers: {
      default: new Footer({
        children: [new Paragraph({
          border: { top: { style: BorderStyle.SINGLE, size: 4, color: H2_COLOR, space: 1 } },
          spacing: { before: 100 },
          alignment: AlignmentType.CENTER,
          children: [
            new TextRun({ text: "Page ", font: "Arial", size: 18, color: "777777" }),
            new TextRun({ children: [PageNumber.CURRENT], font: "Arial", size: 18, color: "777777" }),
            new TextRun({ text: " of ", font: "Arial", size: 18, color: "777777" }),
            new TextRun({ children: [PageNumber.TOTAL_PAGES], font: "Arial", size: 18, color: "777777" })
          ]
        })]
      })
    },
    children
  }]
});

const outPath = "C:\\Users\\2479309\\Desktop\\Project Sphere\\ProjectSphere_TestNG_Cucumber_Report.docx";
Packer.toBuffer(doc).then(buf => {
  fs.writeFileSync(outPath, buf);
  console.log("Done: " + outPath);
}).catch(err => { console.error(err); process.exit(1); });
