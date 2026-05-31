const { Document, Packer, Paragraph, TextRun, HeadingLevel, AlignmentType,
        PageNumber, Header, Footer, TableOfContents, BorderStyle, WidthType,
        Table, TableRow, TableCell, ShadingType, LevelFormat, PageBreak } = require('docx');
const fs = require('fs');

const FONT = "Arial";
const CODE_FONT = "Courier New";
const HEADING_COLOR = "1F3864";
const CODE_BG = "F0F0F0";
const ACCENT = "2E75B6";

function h1(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_1,
    children: [new TextRun({ text, font: FONT, color: HEADING_COLOR, bold: true, size: 32 })],
    spacing: { before: 360, after: 120 }
  });
}
function h2(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_2,
    children: [new TextRun({ text, font: FONT, color: ACCENT, bold: true, size: 26 })],
    spacing: { before: 240, after: 80 }
  });
}
function h3(text) {
  return new Paragraph({
    children: [new TextRun({ text, font: FONT, bold: true, size: 24, color: "444444" })],
    spacing: { before: 180, after: 60 }
  });
}
function body(text) {
  return new Paragraph({
    children: [new TextRun({ text, font: FONT, size: 22 })],
    spacing: { before: 40, after: 40 }
  });
}
function bullet(text) {
  return new Paragraph({
    numbering: { reference: "bullets", level: 0 },
    children: [new TextRun({ text, font: FONT, size: 22 })],
    spacing: { before: 20, after: 20 }
  });
}
function code(text) {
  return new Paragraph({
    children: [new TextRun({ text, font: CODE_FONT, size: 18 })],
    shading: { fill: CODE_BG, type: ShadingType.CLEAR },
    spacing: { before: 20, after: 20 },
    indent: { left: 360 }
  });
}
function label(text, isQ) {
  return new Paragraph({
    children: [new TextRun({ text, font: FONT, bold: true, size: 22, color: isQ ? "1F3864" : "2E7D32" })],
    spacing: { before: 80, after: 20 }
  });
}
function spacer() {
  return new Paragraph({ children: [new TextRun("")], spacing: { before: 60, after: 60 } });
}
function pageBreak() {
  return new Paragraph({ children: [new PageBreak()] });
}
function qna(num, question, theory, codeLines, answer) {
  const items = [];
  items.push(label(`Q${num}. ${question}`, true));
  if (theory) items.push(body(theory));
  if (codeLines && codeLines.length > 0) {
    items.push(body("Example:"));
    codeLines.forEach(l => items.push(code(l)));
  }
  if (answer) {
    items.push(label("Answer:", false));
    if (typeof answer === 'string') items.push(body(answer));
    else answer.forEach(a => items.push(body(a)));
  }
  items.push(spacer());
  return items;
}

const allContent = [];

// Cover
allContent.push(
  new Paragraph({
    children: [new TextRun({ text: "Selenium WebDriver", font: FONT, bold: true, size: 56, color: HEADING_COLOR })],
    alignment: AlignmentType.CENTER,
    spacing: { before: 2880, after: 240 }
  }),
  new Paragraph({
    children: [new TextRun({ text: "100 Interview Questions & Answers", font: FONT, size: 36, color: ACCENT })],
    alignment: AlignmentType.CENTER,
    spacing: { before: 120, after: 120 }
  }),
  new Paragraph({
    children: [new TextRun({ text: "with Theory, Practical Code Examples & 25 Scenario-Based Questions", font: FONT, size: 26, color: "555555" })],
    alignment: AlignmentType.CENTER,
    spacing: { before: 120, after: 2880 }
  }),
  pageBreak(),
  new TableOfContents("Table of Contents", { hyperlink: true, headingStyleRange: "1-2" }),
  pageBreak()
);

// ─── SECTION 1: BASICS ───────────────────────────────────────────────────────
allContent.push(h1("Section 1: Selenium Basics (Q1–Q15)"), spacer());

allContent.push(...qna(1,
  "What is Selenium? What are its main components?",
  "Selenium is an open-source test automation framework for web applications. It supports multiple browsers, operating systems, and programming languages.",
  null,
  [
    "Selenium Suite has 4 components:",
    "1. Selenium WebDriver  — directly controls the browser via browser-native drivers",
    "2. Selenium IDE       — record-and-playback browser plugin",
    "3. Selenium Grid      — runs tests on multiple machines/browsers in parallel",
    "4. Selenium RC (deprecated) — old server-based architecture replaced by WebDriver"
  ]
));

allContent.push(...qna(2,
  "What is WebDriver? How is it different from Selenium RC?",
  "WebDriver communicates directly with the browser using its native support (ChromeDriver, GeckoDriver etc.). Selenium RC required a server proxy for every request, making it slower.",
  [
    "// WebDriver: direct browser communication",
    "WebDriver driver = new ChromeDriver();",
    "driver.get(\"https://example.com\");",
    "",
    "// No server needed — ChromeDriver acts as the bridge",
  ],
  "WebDriver is faster, more stable, supports JS natively, and handles pop-ups/alerts without workarounds."
));

allContent.push(...qna(3,
  "What browsers does Selenium WebDriver support?",
  "Selenium supports any browser that has a corresponding WebDriver implementation.",
  [
    "// Chrome",
    "WebDriver driver = new ChromeDriver();",
    "// Firefox",
    "WebDriver driver = new FirefoxDriver();",
    "// Edge",
    "WebDriver driver = new EdgeDriver();",
    "// Safari (macOS only)",
    "WebDriver driver = new SafariDriver();"
  ],
  "Chrome (ChromeDriver), Firefox (GeckoDriver), Edge (EdgeDriver), Safari (SafariDriver), IE (IEDriverServer — deprecated)."
));

allContent.push(...qna(4,
  "What is the difference between findElement() and findElements()?",
  "Both methods locate elements on the page but behave differently when no match is found.",
  [
    "// findElement() — returns first match, throws NoSuchElementException if none",
    "WebElement btn = driver.findElement(By.id(\"submit\"));",
    "",
    "// findElements() — returns List, empty list if none found (no exception)",
    "List<WebElement> links = driver.findElements(By.tagName(\"a\"));",
    "System.out.println(\"Links count: \" + links.size());"
  ],
  "findElement() returns a single WebElement and throws NoSuchElementException if not found. findElements() returns a List<WebElement> — empty if nothing matches."
));

allContent.push(...qna(5,
  "What is the difference between driver.close() and driver.quit()?",
  "These two methods end the browser session differently.",
  [
    "driver.close();  // Closes ONLY the current active browser window/tab",
    "driver.quit();   // Closes ALL windows and terminates the WebDriver session"
  ],
  "Always call driver.quit() in @AfterMethod/@AfterSuite to properly release WebDriver resources. driver.close() alone leaves the driver session open and causes resource leaks."
));

allContent.push(...qna(6,
  "What are driver.get() vs driver.navigate().to()?",
  "Both load a URL but navigate() has additional capabilities.",
  [
    "driver.get(\"https://example.com\");          // Loads URL, waits for page load",
    "",
    "driver.navigate().to(\"https://example.com\"); // Same as get()",
    "driver.navigate().back();                    // Browser back button",
    "driver.navigate().forward();                 // Browser forward button",
    "driver.navigate().refresh();                 // Refresh page"
  ],
  "driver.get() waits for full page load. driver.navigate().to() is equivalent but navigate() also provides back/forward/refresh which get() does not."
));

allContent.push(...qna(7,
  "How do you get the page title and current URL?",
  "WebDriver provides methods to retrieve browser metadata.",
  [
    "String title = driver.getTitle();",
    "System.out.println(\"Page Title: \" + title);",
    "",
    "String url = driver.getCurrentUrl();",
    "System.out.println(\"Current URL: \" + url);",
    "",
    "// Assert in TestNG",
    "Assert.assertEquals(driver.getTitle(), \"Login — ProjectSphere\");"
  ],
  null
));

allContent.push(...qna(8,
  "How do you maximize or set browser window size?",
  null,
  [
    "driver.manage().window().maximize();",
    "",
    "// Set specific size",
    "driver.manage().window().setSize(new Dimension(1920, 1080));",
    "",
    "// Fullscreen mode",
    "driver.manage().window().fullscreen();"
  ],
  "Always maximize in @BeforeMethod to ensure consistent element locations across machines."
));

allContent.push(...qna(9,
  "What is an implicit wait? What are its drawbacks?",
  "Implicit wait tells WebDriver to poll the DOM for a certain time when trying to find an element before throwing NoSuchElementException.",
  [
    "driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));",
    "// Now every findElement() polls for up to 10 seconds"
  ],
  "Drawbacks: (1) Applies globally to all findElement() calls — can slow down tests expecting fast failures. (2) Mixing implicit + explicit waits causes unpredictable behaviour. Prefer explicit waits."
));

allContent.push(...qna(10,
  "What is the difference between getText() and getAttribute()?",
  null,
  [
    "WebElement el = driver.findElement(By.id(\"username\"));",
    "",
    "// getText() — returns visible text content of element",
    "String text = el.getText();  // e.g. \"John Doe\"",
    "",
    "// getAttribute() — returns HTML attribute value",
    "String placeholder = el.getAttribute(\"placeholder\");  // e.g. \"Enter email\"",
    "String value = el.getAttribute(\"value\");              // typed text in input"
  ],
  "getText() returns the rendered visible text. getAttribute('value') returns typed input value. getAttribute('innerHTML') returns inner HTML content."
));

allContent.push(...qna(11,
  "How do you check if an element is displayed, enabled, or selected?",
  null,
  [
    "WebElement el = driver.findElement(By.id(\"submitBtn\"));",
    "",
    "boolean visible  = el.isDisplayed();  // Element is visible on page",
    "boolean enabled  = el.isEnabled();    // Element is not disabled",
    "boolean selected = el.isSelected();   // For checkboxes/radio/options",
    "",
    "if (el.isDisplayed() && el.isEnabled()) {",
    "    el.click();",
    "}"
  ],
  null
));

allContent.push(...qna(12,
  "What is getWindowHandle() and getWindowHandles()?",
  "Used to switch between multiple browser tabs or windows.",
  [
    "String mainWindow = driver.getWindowHandle(); // Current window ID",
    "",
    "Set<String> allWindows = driver.getWindowHandles(); // All open windows",
    "for (String handle : allWindows) {",
    "    if (!handle.equals(mainWindow)) {",
    "        driver.switchTo().window(handle); // Switch to new window",
    "        break;",
    "    }",
    "}",
    "// Do work in new window...",
    "driver.close();",
    "driver.switchTo().window(mainWindow); // Switch back"
  ],
  null
));

allContent.push(...qna(13,
  "How do you delete cookies in Selenium?",
  null,
  [
    "driver.manage().deleteAllCookies();          // Delete all cookies",
    "driver.manage().deleteCookieNamed(\"session\"); // Delete specific cookie",
    "",
    "// Add a cookie",
    "Cookie c = new Cookie(\"token\", \"abc123\");",
    "driver.manage().addCookie(c);",
    "",
    "// Get all cookies",
    "Set<Cookie> cookies = driver.manage().getCookies();"
  ],
  null
));

allContent.push(...qna(14,
  "How do you get the source code of a page?",
  null,
  [
    "String pageSource = driver.getPageSource();",
    "System.out.println(pageSource.contains(\"Welcome\"));"
  ],
  "getPageSource() returns the full HTML of the current page. Useful for quick checks but not recommended for element location — use locators instead."
));

allContent.push(...qna(15,
  "What is WebElement? List its important methods.",
  null,
  [
    "WebElement el = driver.findElement(By.id(\"email\"));",
    "",
    "el.click();                          // Click element",
    "el.sendKeys(\"test@example.com\");     // Type text",
    "el.clear();                          // Clear input field",
    "el.getText();                        // Get visible text",
    "el.getAttribute(\"placeholder\");      // Get attribute",
    "el.isDisplayed();                    // Visible?",
    "el.isEnabled();                      // Enabled?",
    "el.isSelected();                     // Selected?",
    "el.getTagName();                     // Returns tag e.g. \"input\"",
    "el.getCssValue(\"color\");             // CSS property value",
    "el.getRect();                        // Size and position"
  ],
  null
));

// ─── SECTION 2: LOCATORS ─────────────────────────────────────────────────────
allContent.push(pageBreak(), h1("Section 2: Locators (Q16–Q28)"), spacer());

allContent.push(...qna(16,
  "What are the 8 types of locators in Selenium?",
  null,
  [
    "By.id(\"username\")                          // Fastest — always prefer",
    "By.name(\"email\")                           // HTML name attribute",
    "By.className(\"btn-primary\")               // CSS class",
    "By.tagName(\"input\")                        // HTML tag",
    "By.linkText(\"Click here\")                  // Exact anchor text",
    "By.partialLinkText(\"Click\")               // Partial anchor text",
    "By.cssSelector(\"input#email.form-ctrl\")   // CSS selector",
    "By.xpath(\"//input[@id='email']\")          // XPath expression"
  ],
  "Priority order: id > name > cssSelector > xpath. id is fastest; xpath is most flexible but slowest."
));

allContent.push(...qna(17,
  "What is XPath? What is the difference between absolute and relative XPath?",
  "XPath is a language for navigating XML/HTML documents. In Selenium it is used to locate elements.",
  [
    "// Absolute XPath — starts from root, brittle",
    "//html/body/div[1]/form/input[2]",
    "",
    "// Relative XPath — starts anywhere, preferred",
    "//input[@id='username']",
    "//button[text()='Sign In']",
    "//div[@class='card']//span[contains(text(),'Error')]"
  ],
  "Always use relative XPath. Absolute XPath breaks when any parent element changes."
));

allContent.push(...qna(18,
  "What are XPath axes? Explain parent, child, following-sibling.",
  null,
  [
    "// following-sibling — next sibling element",
    "//label[text()='Email']/following-sibling::input",
    "",
    "// preceding-sibling — previous sibling",
    "//button[text()='Cancel']/preceding-sibling::button[1]",
    "",
    "// parent — go up one level",
    "//input[@id='email']/parent::div",
    "",
    "// ancestor — any ancestor",
    "//span[@class='error']/ancestor::form",
    "",
    "// descendant — any nested child",
    "//div[@class='modal']//descendant::button"
  ],
  null
));

allContent.push(...qna(19,
  "How do you use contains() and starts-with() in XPath?",
  "Useful for dynamic attributes that partially change.",
  [
    "// contains() — partial attribute match",
    "//input[contains(@id,'user')]",
    "//button[contains(@class,'btn-prim')]",
    "//span[contains(text(),'Welcome')]",
    "",
    "// starts-with() — attribute starts with value",
    "//input[starts-with(@name,'phone')]",
    "",
    "// and / or conditions",
    "//input[@type='text' and @name='email']",
    "//button[@id='save' or @id='submit']"
  ],
  null
));

allContent.push(...qna(20,
  "What is a CSS Selector? How is it different from XPath?",
  "CSS Selectors use CSS syntax to locate elements. They are generally faster than XPath.",
  [
    "driver.findElement(By.cssSelector(\"#email\"));          // id",
    "driver.findElement(By.cssSelector(\".btn-primary\"));    // class",
    "driver.findElement(By.cssSelector(\"input[name='q']\"));// attribute",
    "driver.findElement(By.cssSelector(\"div.card > p\"));   // child",
    "driver.findElement(By.cssSelector(\"div.card p\"));     // descendant",
    "",
    "// Contains (CSS ^= starts, $= ends, *= contains)",
    "driver.findElement(By.cssSelector(\"input[id*='user']\"));"
  ],
  "CSS Selectors cannot traverse to parent elements (no parent axis). XPath can. For upward navigation use XPath."
));

allContent.push(...qna(21,
  "How do you locate elements inside an iframe?",
  "Elements inside iframes are in a separate DOM context — you must switch into the iframe first.",
  [
    "// Switch by index",
    "driver.switchTo().frame(0);",
    "",
    "// Switch by name or id",
    "driver.switchTo().frame(\"iframeName\");",
    "",
    "// Switch by WebElement",
    "WebElement iframe = driver.findElement(By.tagName(\"iframe\"));",
    "driver.switchTo().frame(iframe);",
    "",
    "// Interact with element inside iframe",
    "driver.findElement(By.id(\"innerBtn\")).click();",
    "",
    "// Switch back to main content",
    "driver.switchTo().defaultContent();"
  ],
  null
));

allContent.push(...qna(22,
  "How do you find the nth element when multiple elements share the same locator?",
  null,
  [
    "// findElements returns a list — use index (0-based)",
    "List<WebElement> rows = driver.findElements(By.cssSelector(\"table tr\"));",
    "WebElement thirdRow = rows.get(2);",
    "",
    "// XPath with position predicate (1-based)",
    "driver.findElement(By.xpath(\"(//input[@type='text'])[3]\"));"
  ],
  null
));

allContent.push(...qna(23,
  "How do you locate elements by multiple classes?",
  null,
  [
    "// Element has both classes: class='btn primary large'",
    "// CSS — chain class selectors",
    "By.cssSelector(\".btn.primary.large\")",
    "",
    "// XPath — contains each class",
    "By.xpath(\"//button[contains(@class,'btn') and contains(@class,'primary')]\")"
  ],
  null
));

allContent.push(...qna(24,
  "How do you verify a broken link using Selenium?",
  null,
  [
    "List<WebElement> links = driver.findElements(By.tagName(\"a\"));",
    "for (WebElement link : links) {",
    "    String url = link.getAttribute(\"href\");",
    "    if (url == null || url.isEmpty()) continue;",
    "    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();",
    "    conn.setRequestMethod(\"HEAD\");",
    "    conn.connect();",
    "    int code = conn.getResponseCode();",
    "    if (code >= 400) System.out.println(\"Broken: \" + url + \" [\" + code + \"]\");",
    "}"
  ],
  null
));

allContent.push(...qna(25,
  "How do you handle shadow DOM elements?",
  "Shadow DOM encapsulates elements inside a shadow root — regular locators cannot pierce it.",
  [
    "// Get shadow host",
    "WebElement shadowHost = driver.findElement(By.cssSelector(\"my-component\"));",
    "",
    "// Execute JS to get shadow root, then find element",
    "WebElement shadowRoot = (WebElement)",
    "    ((JavascriptExecutor) driver).executeScript(",
    "        \"return arguments[0].shadowRoot\", shadowHost);",
    "",
    "WebElement btn = shadowRoot.findElement(By.cssSelector(\"button.submit\"));"
  ],
  null
));

allContent.push(...qna(26,
  "When should you prefer CSS Selector over XPath?",
  null,
  null,
  [
    "Prefer CSS when: element has id/class/attribute, no need to traverse upward, performance is critical.",
    "Prefer XPath when: need to traverse parent/ancestor, need text-based matching (text()), complex conditions with axes.",
    "Rule of thumb: id > CSS > XPath for performance."
  ]
));

allContent.push(...qna(27,
  "How do you handle a dynamically changing element ID?",
  "Many frameworks (Angular, React) generate dynamic IDs like 'btn-1647382'. Use stable attributes instead.",
  [
    "// BAD — dynamic ID",
    "By.id(\"btn-1647382\")",
    "",
    "// GOOD — stable attribute",
    "By.cssSelector(\"[data-testid='submit-btn']\")",
    "By.cssSelector(\"button[aria-label='Submit']\")  ",
    "By.xpath(\"//button[text()='Submit']\")",
    "By.xpath(\"//button[contains(@class,'submit')]\")"
  ],
  null
));

allContent.push(...qna(28,
  "What is the difference between By.linkText() and By.partialLinkText()?",
  null,
  [
    "// linkText — exact full text match (case-sensitive)",
    "driver.findElement(By.linkText(\"Click here to login\")).click();",
    "",
    "// partialLinkText — partial text match",
    "driver.findElement(By.partialLinkText(\"login\")).click();",
    "",
    "// Both only work on <a> (anchor) elements"
  ],
  null
));

// ─── SECTION 3: ACTIONS ───────────────────────────────────────────────────────
allContent.push(pageBreak(), h1("Section 3: WebDriver Actions (Q29–Q45)"), spacer());

allContent.push(...qna(29,
  "How do you handle a dropdown using the Select class?",
  "The Select class provides methods to interact with <select> HTML dropdowns.",
  [
    "import org.openqa.selenium.support.ui.Select;",
    "",
    "Select dropdown = new Select(driver.findElement(By.id(\"country\")));",
    "",
    "dropdown.selectByVisibleText(\"India\");   // By displayed text",
    "dropdown.selectByValue(\"IN\");             // By option value attribute",
    "dropdown.selectByIndex(2);                // By 0-based index",
    "",
    "// Get selected option",
    "String selected = dropdown.getFirstSelectedOption().getText();",
    "",
    "// Multi-select dropdown",
    "dropdown.selectByVisibleText(\"Option A\");",
    "dropdown.selectByVisibleText(\"Option B\");",
    "List<WebElement> all = dropdown.getAllSelectedOptions();"
  ],
  null
));

allContent.push(...qna(30,
  "How do you handle a custom (non-<select>) Angular dropdown?",
  "Angular dropdowns are usually div/ul/li based — Select class does not work.",
  [
    "// 1. Click to open",
    "driver.findElement(By.cssSelector(\".dropdown-trigger\")).click();",
    "",
    "// 2. Wait for options to appear",
    "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));",
    "wait.until(ExpectedConditions.visibilityOfElementLocated(",
    "    By.cssSelector(\".dropdown-option\")));",
    "",
    "// 3. Select the option by text",
    "List<WebElement> options = driver.findElements(By.cssSelector(\".dropdown-option\"));",
    "for (WebElement opt : options) {",
    "    if (opt.getText().equals(\"India\")) {",
    "        opt.click();",
    "        break;",
    "    }",
    "}"
  ],
  null
));

allContent.push(...qna(31,
  "How do you handle browser alerts, confirms, and prompts?",
  null,
  [
    "// Switch to alert",
    "Alert alert = driver.switchTo().alert();",
    "",
    "// Get alert message",
    "String msg = alert.getText();",
    "",
    "// Accept (OK button)",
    "alert.accept();",
    "",
    "// Dismiss (Cancel button)",
    "alert.dismiss();",
    "",
    "// Type in prompt and accept",
    "alert.sendKeys(\"My Input\");",
    "alert.accept();",
    "",
    "// Wait for alert to appear",
    "new WebDriverWait(driver, Duration.ofSeconds(5))",
    "    .until(ExpectedConditions.alertIsPresent());"
  ],
  null
));

allContent.push(...qna(32,
  "How do you handle checkboxes and radio buttons?",
  null,
  [
    "WebElement checkbox = driver.findElement(By.id(\"agree\"));",
    "",
    "// Check only if not already checked",
    "if (!checkbox.isSelected()) checkbox.click();",
    "",
    "// Uncheck",
    "if (checkbox.isSelected()) checkbox.click();",
    "",
    "// Radio button — same: click to select",
    "driver.findElement(By.cssSelector(\"input[value='male']\")).click();"
  ],
  null
));

allContent.push(...qna(33,
  "How do you upload a file in Selenium?",
  "File upload inputs (<input type='file'>) accept the file path via sendKeys — no need to interact with the OS dialog.",
  [
    "WebElement uploadInput = driver.findElement(By.cssSelector(\"input[type='file']\"));",
    "uploadInput.sendKeys(\"C:\\\\Users\\\\user\\\\Documents\\\\test.pdf\");"
  ],
  "sendKeys with the absolute file path directly types into the file input. Works for visible and hidden file inputs."
));

allContent.push(...qna(34,
  "How do you perform mouse hover using Actions class?",
  "The Actions class chains complex gestures — hover, drag/drop, right-click, keyboard combos.",
  [
    "import org.openqa.selenium.interactions.Actions;",
    "",
    "Actions actions = new Actions(driver);",
    "WebElement menu = driver.findElement(By.id(\"mainMenu\"));",
    "",
    "// Hover over element",
    "actions.moveToElement(menu).perform();",
    "",
    "// After hover, submenu appears — then click it",
    "WebElement subItem = driver.findElement(By.linkText(\"Profile\"));",
    "actions.moveToElement(menu).click(subItem).perform();"
  ],
  null
));

allContent.push(...qna(35,
  "How do you perform drag and drop?",
  null,
  [
    "Actions actions = new Actions(driver);",
    "WebElement source = driver.findElement(By.id(\"draggable\"));",
    "WebElement target = driver.findElement(By.id(\"droppable\"));",
    "",
    "// Method 1: dragAndDrop",
    "actions.dragAndDrop(source, target).perform();",
    "",
    "// Method 2: clickAndHold + move + release",
    "actions.clickAndHold(source)",
    "       .moveToElement(target)",
    "       .release()",
    "       .perform();"
  ],
  null
));

allContent.push(...qna(36,
  "How do you perform right-click and double-click?",
  null,
  [
    "Actions actions = new Actions(driver);",
    "WebElement el = driver.findElement(By.id(\"myElement\"));",
    "",
    "// Right-click (context menu)",
    "actions.contextClick(el).perform();",
    "",
    "// Double-click",
    "actions.doubleClick(el).perform();",
    "",
    "// Keyboard shortcut: Ctrl+A",
    "actions.keyDown(Keys.CONTROL).sendKeys(\"a\").keyUp(Keys.CONTROL).perform();"
  ],
  null
));

allContent.push(...qna(37,
  "How do you scroll the page in Selenium?",
  null,
  [
    "JavascriptExecutor js = (JavascriptExecutor) driver;",
    "",
    "// Scroll to bottom of page",
    "js.executeScript(\"window.scrollTo(0, document.body.scrollHeight)\");",
    "",
    "// Scroll to top",
    "js.executeScript(\"window.scrollTo(0, 0)\");",
    "",
    "// Scroll by pixels",
    "js.executeScript(\"window.scrollBy(0, 500)\");",
    "",
    "// Scroll element into view",
    "WebElement el = driver.findElement(By.id(\"footer\"));",
    "js.executeScript(\"arguments[0].scrollIntoView(true)\", el);"
  ],
  null
));

allContent.push(...qna(38,
  "How do you handle dynamic web tables?",
  null,
  [
    "// Get all rows in a table",
    "List<WebElement> rows = driver.findElements(By.cssSelector(\"table tbody tr\"));",
    "",
    "for (WebElement row : rows) {",
    "    List<WebElement> cols = row.findElements(By.tagName(\"td\"));",
    "    String name   = cols.get(0).getText();",
    "    String status = cols.get(1).getText();",
    "    System.out.println(name + \" | \" + status);",
    "}",
    "",
    "// Find row by cell value",
    "WebElement cell = driver.findElement(",
    "    By.xpath(\"//table//td[text()='John']/following-sibling::td[1]\"));"
  ],
  null
));

allContent.push(...qna(39,
  "How do you handle keyboard actions with sendKeys?",
  null,
  [
    "import org.openqa.selenium.Keys;",
    "",
    "WebElement search = driver.findElement(By.name(\"q\"));",
    "search.sendKeys(\"Selenium\");",
    "search.sendKeys(Keys.ENTER);       // Press Enter",
    "search.sendKeys(Keys.TAB);         // Press Tab",
    "search.sendKeys(Keys.BACK_SPACE);  // Backspace",
    "",
    "// Clear and type",
    "search.clear();",
    "search.sendKeys(\"New Text\");",
    "",
    "// Select all + delete",
    "search.sendKeys(Keys.chord(Keys.CONTROL, \"a\"));",
    "search.sendKeys(Keys.DELETE);"
  ],
  null
));

allContent.push(...qna(40,
  "How do you interact with calendar/date pickers?",
  null,
  [
    "// Option 1: Direct sendKeys if input is editable",
    "WebElement dateInput = driver.findElement(By.id(\"dob\"));",
    "dateInput.clear();",
    "dateInput.sendKeys(\"12/25/2024\");",
    "",
    "// Option 2: JS to set value for read-only inputs",
    "((JavascriptExecutor) driver).executeScript(",
    "    \"arguments[0].value = '2024-12-25'\", dateInput);",
    "",
    "// Option 3: Navigate calendar UI",
    "driver.findElement(By.cssSelector(\".calendar-next\")).click();",
    "driver.findElement(By.xpath(\"//td[text()='25']\")).click();"
  ],
  null
));

allContent.push(...qna(41,
  "How do you handle tooltips?",
  null,
  [
    "// Static tooltip — title attribute",
    "WebElement el = driver.findElement(By.id(\"info-icon\"));",
    "String tooltip = el.getAttribute(\"title\");",
    "",
    "// Dynamic tooltip — hover then read",
    "Actions actions = new Actions(driver);",
    "actions.moveToElement(el).perform();",
    "",
    "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));",
    "WebElement tip = wait.until(ExpectedConditions.visibilityOfElementLocated(",
    "    By.cssSelector(\".tooltip-text\")));",
    "System.out.println(tip.getText());"
  ],
  null
));

allContent.push(...qna(42,
  "How do you handle multiple browser tabs/windows?",
  null,
  [
    "// Open new tab with JS",
    "((JavascriptExecutor) driver).executeScript(\"window.open()\");",
    "",
    "// Get all handles",
    "List<String> tabs = new ArrayList<>(driver.getWindowHandles());",
    "",
    "// Switch to second tab",
    "driver.switchTo().window(tabs.get(1));",
    "driver.get(\"https://example.com\");",
    "",
    "// Close second tab and switch back",
    "driver.close();",
    "driver.switchTo().window(tabs.get(0));"
  ],
  null
));

allContent.push(...qna(43,
  "How do you read text from a disabled input field?",
  null,
  [
    "// isEnabled() returns false but getAttribute('value') still works",
    "WebElement disabledField = driver.findElement(By.id(\"readOnly\"));",
    "String value = disabledField.getAttribute(\"value\");",
    "",
    "// For readonly fields",
    "String attr = disabledField.getAttribute(\"readonly\"); // returns \"true\" or null"
  ],
  null
));

allContent.push(...qna(44,
  "How do you handle AJAX elements that load after user action?",
  null,
  [
    "// Click triggers AJAX request",
    "driver.findElement(By.id(\"loadBtn\")).click();",
    "",
    "// Wait for element that appears after AJAX completes",
    "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));",
    "WebElement result = wait.until(",
    "    ExpectedConditions.visibilityOfElementLocated(By.id(\"results\")));",
    "System.out.println(result.getText());"
  ],
  null
));

allContent.push(...qna(45,
  "How do you interact with a modal dialog?",
  null,
  [
    "// Click button to open modal",
    "driver.findElement(By.id(\"openModal\")).click();",
    "",
    "// Wait for modal overlay to appear",
    "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));",
    "wait.until(ExpectedConditions.visibilityOfElementLocated(",
    "    By.cssSelector(\".modal\")));",
    "",
    "// Interact with elements inside modal",
    "driver.findElement(By.cssSelector(\".modal input[name='name']\")).sendKeys(\"Test\");",
    "driver.findElement(By.cssSelector(\".modal button.confirm\")).click();",
    "",
    "// Wait for modal to close",
    "wait.until(ExpectedConditions.invisibilityOfElementLocated(",
    "    By.cssSelector(\".modal\")));"
  ],
  null
));

// ─── SECTION 4: WAITS ─────────────────────────────────────────────────────────
allContent.push(pageBreak(), h1("Section 4: Waits & Synchronization (Q46–Q55)"), spacer());

allContent.push(...qna(46,
  "What are the three types of waits in Selenium?",
  null,
  null,
  [
    "1. Implicit Wait — global timeout for findElement() calls. Set once, applies everywhere.",
    "2. Explicit Wait (WebDriverWait) — wait for a specific condition on a specific element.",
    "3. FluentWait — explicit wait with configurable polling interval and exception ignoring.",
    "Note: Thread.sleep() is NOT a Selenium wait — it freezes execution unconditionally."
  ]
));

allContent.push(...qna(47,
  "What is WebDriverWait (Explicit Wait)?",
  "WebDriverWait polls the DOM at regular intervals until the expected condition is met or the timeout expires.",
  [
    "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));",
    "",
    "// Wait until element is visible",
    "WebElement el = wait.until(",
    "    ExpectedConditions.visibilityOfElementLocated(By.id(\"result\")));",
    "",
    "// Wait until element is clickable",
    "WebElement btn = wait.until(",
    "    ExpectedConditions.elementToBeClickable(By.cssSelector(\".submit-btn\")));",
    "",
    "// Wait until text appears in element",
    "wait.until(ExpectedConditions.textToBePresentInElement(el, \"Success\"));",
    "",
    "// Wait until URL changes",
    "wait.until(ExpectedConditions.urlContains(\"/dashboard\"));"
  ],
  null
));

allContent.push(...qna(48,
  "What is FluentWait? How is it different from WebDriverWait?",
  "FluentWait allows custom polling frequency and specifying which exceptions to ignore during polling.",
  [
    "Wait<WebDriver> wait = new FluentWait<>(driver)",
    "    .withTimeout(Duration.ofSeconds(30))",
    "    .pollingEvery(Duration.ofSeconds(2))",
    "    .ignoring(NoSuchElementException.class)",
    "    .ignoring(StaleElementReferenceException.class);",
    "",
    "WebElement el = wait.until(driver ->",
    "    driver.findElement(By.id(\"dynamicElement\")));"
  ],
  "WebDriverWait extends FluentWait with a 500ms polling default. Use FluentWait when you need custom polling intervals or need to ignore specific exceptions."
));

allContent.push(...qna(49,
  "What are the most commonly used ExpectedConditions?",
  null,
  [
    "ExpectedConditions.visibilityOfElementLocated(By)   // Element visible",
    "ExpectedConditions.invisibilityOfElementLocated(By) // Element gone",
    "ExpectedConditions.elementToBeClickable(By)         // Visible + enabled",
    "ExpectedConditions.presenceOfElementLocated(By)     // In DOM (may be hidden)",
    "ExpectedConditions.textToBePresentInElement(el, s)  // Text match",
    "ExpectedConditions.titleContains(\"Login\")           // Title check",
    "ExpectedConditions.urlContains(\"/home\")             // URL check",
    "ExpectedConditions.alertIsPresent()                 // Alert ready",
    "ExpectedConditions.numberOfElementsToBe(By, n)      // Count match",
    "ExpectedConditions.stalenessOf(el)                  // Old element gone"
  ],
  null
));

allContent.push(...qna(50,
  "What causes StaleElementReferenceException and how do you fix it?",
  "A StaleElementReferenceException occurs when a WebElement reference becomes outdated — the DOM was refreshed or re-rendered after you found the element.",
  [
    "// BAD — element found before DOM refresh",
    "WebElement el = driver.findElement(By.id(\"btn\"));",
    "driver.navigate().refresh();  // DOM changes",
    "el.click();  // StaleElementReferenceException!",
    "",
    "// FIX 1 — re-find element after refresh",
    "driver.navigate().refresh();",
    "driver.findElement(By.id(\"btn\")).click();",
    "",
    "// FIX 2 — use FluentWait ignoring StaleElement",
    "Wait<WebDriver> wait = new FluentWait<>(driver)",
    "    .withTimeout(Duration.ofSeconds(10))",
    "    .pollingEvery(Duration.ofSeconds(1))",
    "    .ignoring(StaleElementReferenceException.class);",
    "wait.until(d -> d.findElement(By.id(\"btn\")).click() == null);"
  ],
  null
));

allContent.push(...qna(51,
  "Why should you never use Thread.sleep() in Selenium?",
  null,
  null,
  [
    "Thread.sleep() is a hard pause — it always waits the full duration regardless of whether the element is ready.",
    "Problems: (1) Wastes time on fast environments. (2) Fails on slow environments if sleep is too short.",
    "Correct approach: Always use WebDriverWait with ExpectedConditions. It exits as soon as the condition is met — fast on fast machines, patient on slow ones."
  ]
));

allContent.push(...qna(52,
  "What is the difference between visibilityOf() and visibilityOfElementLocated()?",
  null,
  [
    "// visibilityOfElementLocated — takes a By locator, finds AND waits",
    "wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(\"msg\")));",
    "",
    "// visibilityOf — takes an already-found WebElement",
    "WebElement el = driver.findElement(By.id(\"msg\"));",
    "wait.until(ExpectedConditions.visibilityOf(el));"
  ],
  "Use visibilityOfElementLocated when the element may not exist yet. Use visibilityOf when you already have the element reference."
));

allContent.push(...qna(53,
  "How do you wait for a page to fully load in Selenium?",
  null,
  [
    "// Wait for document.readyState to be 'complete'",
    "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));",
    "wait.until(driver ->",
    "    ((JavascriptExecutor) driver)",
    "        .executeScript(\"return document.readyState\")",
    "        .equals(\"complete\"));",
    "",
    "// For Angular SPAs — wait for Angular to finish rendering",
    "// Wait for a key element that only appears when page is fully rendered",
    "wait.until(ExpectedConditions.visibilityOfElementLocated(",
    "    By.cssSelector(\".dashboard-loaded\")));"
  ],
  null
));

allContent.push(...qna(54,
  "How do you handle ElementNotInteractableException?",
  "Thrown when an element is present in the DOM but cannot be interacted with (hidden, covered by overlay, etc.).",
  [
    "// Cause 1: Element hidden — scroll into view first",
    "js.executeScript(\"arguments[0].scrollIntoView(true)\", el);",
    "",
    "// Cause 2: Overlay covering element — wait for it to disappear",
    "wait.until(ExpectedConditions.invisibilityOfElementLocated(",
    "    By.cssSelector(\".loading-overlay\")));",
    "el.click();",
    "",
    "// Cause 3: JS click as workaround",
    "((JavascriptExecutor) driver).executeScript(\"arguments[0].click()\", el);"
  ],
  null
));

allContent.push(...qna(55,
  "How do you handle ElementClickInterceptedException?",
  "Thrown when another element (usually a modal, cookie banner, or loading spinner) receives the click instead.",
  [
    "// Wait for any overlay/spinner to disappear first",
    "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));",
    "wait.until(ExpectedConditions.invisibilityOfElementLocated(",
    "    By.cssSelector(\".spinner, .overlay, .modal-backdrop\")));",
    "",
    "// Then click target element",
    "wait.until(ExpectedConditions.elementToBeClickable(By.id(\"saveBtn\"))).click();",
    "",
    "// Or use JS click to bypass interception",
    "WebElement btn = driver.findElement(By.id(\"saveBtn\"));",
    "((JavascriptExecutor) driver).executeScript(\"arguments[0].click()\", btn);"
  ],
  null
));

// ─── SECTION 5: PAGE OBJECT MODEL ────────────────────────────────────────────
allContent.push(pageBreak(), h1("Section 5: Page Object Model (Q56–Q65)"), spacer());

allContent.push(...qna(56,
  "What is the Page Object Model (POM)? Why use it?",
  "POM is a design pattern where each web page has a corresponding Java class. Element locators and page actions are encapsulated in the page class; tests only call page methods.",
  [
    "// Page Object",
    "public class LoginPage {",
    "    private WebDriver driver;",
    "    private By emailField = By.id(\"email\");",
    "    private By passwordField = By.id(\"password\");",
    "    private By signInBtn = By.cssSelector(\".signin-btn\");",
    "",
    "    public LoginPage(WebDriver driver) { this.driver = driver; }",
    "",
    "    public void enterEmail(String email) {",
    "        driver.findElement(emailField).sendKeys(email);",
    "    }",
    "    public void clickSignIn() {",
    "        driver.findElement(signInBtn).click();",
    "    }",
    "}",
    "",
    "// Test",
    "LoginPage login = new LoginPage(driver);",
    "login.enterEmail(\"admin@test.com\");",
    "login.clickSignIn();"
  ],
  "Benefits: (1) Separation of concerns — test logic vs page logic. (2) Reusability. (3) Maintainability — locator change in one place."
));

allContent.push(...qna(57,
  "What is PageFactory? What is the @FindBy annotation?",
  "PageFactory initialises @FindBy-annotated WebElement fields using lazy initialisation.",
  [
    "import org.openqa.selenium.support.FindBy;",
    "import org.openqa.selenium.support.PageFactory;",
    "",
    "public class LoginPage {",
    "    @FindBy(id = \"email\")",
    "    private WebElement emailField;",
    "",
    "    @FindBy(css = \".signin-btn\")",
    "    private WebElement signInBtn;",
    "",
    "    public LoginPage(WebDriver driver) {",
    "        PageFactory.initElements(driver, this);",
    "    }",
    "",
    "    public void enterEmail(String email) {",
    "        emailField.sendKeys(email);",
    "    }",
    "}"
  ],
  "PageFactory elements are located lazily — each time the field is accessed, not when the page object is constructed. This avoids StaleElementReference on fresh pages."
));

allContent.push(...qna(58,
  "What is fluent interface / method chaining in POM?",
  "Methods return 'this' (or the next page object) so calls can be chained.",
  [
    "public class LoginPage {",
    "    public LoginPage enterEmail(String email) {",
    "        emailField.sendKeys(email);",
    "        return this;",
    "    }",
    "    public LoginPage enterPassword(String pw) {",
    "        passwordField.sendKeys(pw);",
    "        return this;",
    "    }",
    "    public DashboardPage clickSignIn() {",
    "        signInBtn.click();",
    "        return new DashboardPage(driver);",
    "    }",
    "}",
    "",
    "// Usage — clean chained call",
    "DashboardPage dashboard = new LoginPage(driver)",
    "    .enterEmail(\"admin@test.com\")",
    "    .enterPassword(\"pass\")",
    "    .clickSignIn();"
  ],
  null
));

allContent.push(...qna(59,
  "What is the difference between @FindBy, @FindAll, and @FindBys?",
  null,
  [
    "// @FindBy — single locator",
    "@FindBy(id = \"submit\") WebElement submitBtn;",
    "",
    "// @FindAll — OR logic: finds elements matching ANY of the locators",
    "@FindAll({@FindBy(css = \".btn-primary\"), @FindBy(css = \".btn-submit\")})",
    "private List<WebElement> submitButtons;",
    "",
    "// @FindBys — AND logic: elements matching ALL locators (narrowing)",
    "@FindBys({@FindBy(css = \".row\"), @FindBy(css = \".active\")})",
    "private List<WebElement> activeRows;"
  ],
  null
));

allContent.push(...qna(60,
  "How do you handle a base page object for common functionality?",
  null,
  [
    "public abstract class BasePage {",
    "    protected WebDriver driver;",
    "    protected WebDriverWait wait;",
    "",
    "    public BasePage(WebDriver driver) {",
    "        this.driver = driver;",
    "        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));",
    "        PageFactory.initElements(driver, this);",
    "    }",
    "",
    "    protected WebElement waitForVisible(By locator) {",
    "        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));",
    "    }",
    "    protected void jsClick(WebElement el) {",
    "        ((JavascriptExecutor) driver).executeScript(\"arguments[0].click()\", el);",
    "    }",
    "}",
    "",
    "public class LoginPage extends BasePage {",
    "    public LoginPage(WebDriver driver) { super(driver); }",
    "}"
  ],
  null
));

allContent.push(...qna(61,
  "What is BaseTest and why is it used in TestNG?",
  "BaseTest centralises WebDriver setup and teardown so every test class inherits it.",
  [
    "public class BaseTest {",
    "    private static ThreadLocal<WebDriver> driverHolder = new ThreadLocal<>();",
    "",
    "    @BeforeMethod",
    "    public void setUp() {",
    "        ChromeOptions options = new ChromeOptions();",
    "        options.addArguments(\"--disable-blink-features=AutomationControlled\");",
    "        WebDriver driver = new ChromeDriver(options);",
    "        driver.manage().window().maximize();",
    "        driverHolder.set(driver);",
    "    }",
    "",
    "    public WebDriver getDriver() { return driverHolder.get(); }",
    "",
    "    @AfterMethod",
    "    public void tearDown() {",
    "        WebDriver driver = driverHolder.get();",
    "        if (driver != null) { driver.quit(); driverHolder.remove(); }",
    "    }",
    "}",
    "",
    "public class LoginTest extends BaseTest {",
    "    @Test",
    "    public void testLogin() {",
    "        new LoginPage(getDriver()).enterEmail(\"x\").clickSignIn();",
    "    }",
    "}"
  ],
  null
));

allContent.push(...qna(62,
  "How do you implement data-driven testing with TestNG @DataProvider?",
  null,
  [
    "@DataProvider(name = \"loginData\")",
    "public Object[][] loginData() {",
    "    return new Object[][] {",
    "        { \"admin@test.com\", \"pass123\", \"Admin Dashboard\" },",
    "        { \"pm@test.com\",    \"pass456\", \"PM Dashboard\"    },",
    "        { \"wrong@test.com\", \"wrong\",   \"error\"           }",
    "    };",
    "}",
    "",
    "@Test(dataProvider = \"loginData\")",
    "public void testLogin(String email, String pw, String expected) {",
    "    LoginPage login = new LoginPage(getDriver());",
    "    login.enterEmail(email).enterPassword(pw).clickSignIn();",
    "    Assert.assertTrue(driver.getTitle().contains(expected));",
    "}"
  ],
  null
));

allContent.push(...qna(63,
  "How do you read test data from Excel using Apache POI?",
  null,
  [
    "public static String[][] readExcel(String path, String sheet) throws Exception {",
    "    FileInputStream fis = new FileInputStream(path);",
    "    Workbook wb = new XSSFWorkbook(fis);",
    "    Sheet sh = wb.getSheet(sheet);",
    "    int rows = sh.getLastRowNum() + 1;",
    "    int cols = sh.getRow(0).getLastCellNum();",
    "    String[][] data = new String[rows][cols];",
    "    for (int r = 0; r < rows; r++)",
    "        for (int c = 0; c < cols; c++)",
    "            data[r][c] = sh.getRow(r).getCell(c).toString();",
    "    wb.close();",
    "    return data;",
    "}"
  ],
  null
));

allContent.push(...qna(64,
  "How do you take a screenshot on test failure in TestNG?",
  null,
  [
    "public class ScreenshotListener implements ITestListener {",
    "    @Override",
    "    public void onTestFailure(ITestResult result) {",
    "        Object obj = result.getInstance();",
    "        WebDriver driver = ((BaseTest) obj).getDriver();",
    "        TakesScreenshot ts = (TakesScreenshot) driver;",
    "        File src = ts.getScreenshotAs(OutputType.FILE);",
    "        String path = \"screenshots/\" + result.getName() + \".png\";",
    "        try { FileUtils.copyFile(src, new File(path)); }",
    "        catch (IOException e) { e.printStackTrace(); }",
    "    }",
    "}"
  ],
  null
));

allContent.push(...qna(65,
  "What is SoftAssert in TestNG and when do you use it?",
  "SoftAssert collects all assertion failures and reports them at the end, instead of stopping the test on first failure.",
  [
    "// Hard Assert — stops on first failure",
    "Assert.assertEquals(actual, expected);",
    "",
    "// SoftAssert — continues even after failures",
    "@Test",
    "public void testDashboard() {",
    "    SoftAssert soft = new SoftAssert();",
    "    soft.assertTrue(dashboard.isTitleVisible(), \"Title missing\");",
    "    soft.assertEquals(dashboard.getProjectCount(), 5, \"Wrong count\");",
    "    soft.assertTrue(dashboard.isNavbarVisible(), \"Navbar missing\");",
    "    soft.assertAll();  // MUST call — reports all failures",
    "}"
  ],
  "Use SoftAssert when you want to verify multiple properties of a page in a single test and see ALL failures at once."
));

// ─── SECTION 6: ADVANCED ─────────────────────────────────────────────────────
allContent.push(pageBreak(), h1("Section 6: Advanced Selenium (Q66–Q80)"), spacer());

allContent.push(...qna(66,
  "What is JavascriptExecutor? When should you use it?",
  "JavascriptExecutor executes arbitrary JavaScript in the browser context from Selenium code.",
  [
    "JavascriptExecutor js = (JavascriptExecutor) driver;",
    "",
    "// Click element (bypass intercepting overlays)",
    "js.executeScript(\"arguments[0].click()\", element);",
    "",
    "// Set value on a read-only/Angular input",
    "js.executeScript(\"arguments[0].value = 'Test'\", inputEl);",
    "",
    "// Highlight element for debugging",
    "js.executeScript(\"arguments[0].style.border='3px solid red'\", el);",
    "",
    "// Return a value from JS",
    "String title = (String) js.executeScript(\"return document.title\");",
    "Long count = (Long) js.executeScript(\"return document.querySelectorAll('.row').length\");"
  ],
  "Use JS executor when: normal click is intercepted, Angular two-way binding doesn't reflect sendKeys, or you need to query/manipulate the DOM directly."
));

allContent.push(...qna(67,
  "How do you take a screenshot in Selenium?",
  null,
  [
    "// Method 1: File copy",
    "TakesScreenshot ts = (TakesScreenshot) driver;",
    "File src = ts.getScreenshotAs(OutputType.FILE);",
    "FileUtils.copyFile(src, new File(\"screenshot.png\"));",
    "",
    "// Method 2: Byte array (for Cucumber reports)",
    "byte[] bytes = ts.getScreenshotAs(OutputType.BYTES);",
    "scenario.attach(bytes, \"image/png\", \"Failure Screenshot\");",
    "",
    "// Method 3: Base64 string",
    "String base64 = ts.getScreenshotAs(OutputType.BASE64);",
    "",
    "// Screenshot of a specific element only",
    "WebElement el = driver.findElement(By.id(\"chart\"));",
    "File elShot = el.getScreenshotAs(OutputType.FILE);"
  ],
  null
));

allContent.push(...qna(68,
  "What is ChromeOptions? How do you configure it?",
  null,
  [
    "ChromeOptions options = new ChromeOptions();",
    "",
    "// Headless mode",
    "options.addArguments(\"--headless=new\");",
    "",
    "// Disable automation detection",
    "options.addArguments(\"--disable-blink-features=AutomationControlled\");",
    "options.setExperimentalOption(\"excludeSwitches\", List.of(\"enable-automation\"));",
    "",
    "// Incognito",
    "options.addArguments(\"--incognito\");",
    "",
    "// Ignore SSL certificate errors",
    "options.setAcceptInsecureCerts(true);",
    "",
    "// Set download directory",
    "Map<String, Object> prefs = new HashMap<>();",
    "prefs.put(\"download.default_directory\", \"C:\\\\Downloads\");",
    "options.setExperimentalOption(\"prefs\", prefs);",
    "",
    "WebDriver driver = new ChromeDriver(options);"
  ],
  null
));

allContent.push(...qna(69,
  "What is headless browser testing? How do you run tests headlessly?",
  "Headless mode runs Chrome without a visible UI — useful for CI/CD pipelines where no display is available.",
  [
    "ChromeOptions options = new ChromeOptions();",
    "options.addArguments(\"--headless=new\");   // New headless mode (Chrome 112+)",
    "options.addArguments(\"--no-sandbox\");",
    "options.addArguments(\"--disable-dev-shm-usage\");",
    "options.addArguments(\"--window-size=1920,1080\");",
    "",
    "WebDriver driver = new ChromeDriver(options);"
  ],
  "Always set --window-size in headless mode — no display means the default window can be very small, causing elements to be hidden."
));

allContent.push(...qna(70,
  "What is Selenium Grid? What are Hub and Node?",
  null,
  [
    "// Grid 4 — start Hub",
    "java -jar selenium-server.jar hub",
    "",
    "// Grid 4 — start Node",
    "java -jar selenium-server.jar node --hub http://localhost:4444",
    "",
    "// Run test on Grid using RemoteWebDriver",
    "ChromeOptions options = new ChromeOptions();",
    "WebDriver driver = new RemoteWebDriver(",
    "    new URL(\"http://localhost:4444/wd/hub\"), options);"
  ],
  "Hub receives test requests and routes them to available Nodes. Nodes execute tests on their local browser. Grid enables parallel cross-browser testing."
));

allContent.push(...qna(71,
  "How do you handle SSL certificate errors?",
  null,
  [
    "// ChromeOptions",
    "ChromeOptions options = new ChromeOptions();",
    "options.setAcceptInsecureCerts(true);",
    "",
    "// FirefoxOptions",
    "FirefoxOptions ffOptions = new FirefoxOptions();",
    "ffOptions.setAcceptInsecureCerts(true);"
  ],
  null
));

allContent.push(...qna(72,
  "How do you handle pagination in test automation?",
  null,
  [
    "boolean morePages = true;",
    "while (morePages) {",
    "    // Process current page",
    "    List<WebElement> rows = driver.findElements(By.cssSelector(\".data-row\"));",
    "    rows.forEach(r -> System.out.println(r.getText()));",
    "",
    "    // Check if Next button is enabled",
    "    WebElement nextBtn = driver.findElement(By.cssSelector(\".pagination-next\"));",
    "    if (nextBtn.isEnabled() && !nextBtn.getAttribute(\"class\").contains(\"disabled\")) {",
    "        nextBtn.click();",
    "        new WebDriverWait(driver, Duration.ofSeconds(10))",
    "            .until(ExpectedConditions.stalenessOf(rows.get(0)));",
    "    } else {",
    "        morePages = false;",
    "    }",
    "}"
  ],
  null
));

allContent.push(...qna(73,
  "How do you handle infinite scroll?",
  null,
  [
    "JavascriptExecutor js = (JavascriptExecutor) driver;",
    "long lastHeight = (Long) js.executeScript(",
    "    \"return document.body.scrollHeight\");",
    "",
    "while (true) {",
    "    js.executeScript(\"window.scrollTo(0, document.body.scrollHeight)\");",
    "    Thread.sleep(2000); // In real projects use WebDriverWait instead",
    "    long newHeight = (Long) js.executeScript(",
    "        \"return document.body.scrollHeight\");",
    "    if (newHeight == lastHeight) break;",
    "    lastHeight = newHeight;",
    "}"
  ],
  null
));

allContent.push(...qna(74,
  "How do you handle a file download and verify it?",
  null,
  [
    "// Configure download directory in ChromeOptions",
    "Map<String, Object> prefs = new HashMap<>();",
    "prefs.put(\"download.default_directory\", \"/tmp/downloads\");",
    "prefs.put(\"download.prompt_for_download\", false);",
    "ChromeOptions options = new ChromeOptions();",
    "options.setExperimentalOption(\"prefs\", prefs);",
    "",
    "// Click download button",
    "driver.findElement(By.id(\"downloadBtn\")).click();",
    "",
    "// Wait for file to appear",
    "File file = new File(\"/tmp/downloads/report.pdf\");",
    "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));",
    "wait.until(d -> file.exists() && file.length() > 0);"
  ],
  null
));

allContent.push(...qna(75,
  "How do you verify broken images on a page?",
  null,
  [
    "List<WebElement> images = driver.findElements(By.tagName(\"img\"));",
    "for (WebElement img : images) {",
    "    String src = img.getAttribute(\"src\");",
    "    // naturalWidth == 0 means image failed to load",
    "    Long width = (Long)((JavascriptExecutor) driver)",
    "        .executeScript(\"return arguments[0].naturalWidth\", img);",
    "    if (width == 0) System.out.println(\"Broken image: \" + src);",
    "}"
  ],
  null
));

allContent.push(...qna(76,
  "What is the difference between driver.findElement() and element.findElement()?",
  null,
  [
    "// driver.findElement — searches entire DOM from root",
    "WebElement table = driver.findElement(By.id(\"dataTable\"));",
    "",
    "// element.findElement — searches within the element's subtree only",
    "WebElement firstCell = table.findElement(By.cssSelector(\"tr:first-child td:first-child\"));",
    "",
    "// Chaining for clarity",
    "String text = driver.findElement(By.id(\"dataTable\"))",
    "    .findElement(By.cssSelector(\"tr:nth-child(3) td:nth-child(2)\"))",
    "    .getText();"
  ],
  "Scoped findElement() is faster (smaller DOM to search) and avoids accidental matches outside the container."
));

allContent.push(...qna(77,
  "What is ThreadLocal in Selenium parallel testing?",
  null,
  [
    "// ThreadLocal ensures each thread has its own WebDriver instance",
    "private static ThreadLocal<WebDriver> driverHolder = new ThreadLocal<>();",
    "",
    "@BeforeMethod",
    "public void setUp() {",
    "    driverHolder.set(new ChromeDriver());",
    "}",
    "",
    "public static WebDriver getDriver() {",
    "    return driverHolder.get();",
    "}",
    "",
    "@AfterMethod",
    "public void tearDown() {",
    "    driverHolder.get().quit();",
    "    driverHolder.remove();  // Prevent memory leaks",
    "}"
  ],
  "Without ThreadLocal, parallel tests share one WebDriver instance and interfere with each other. ThreadLocal gives each thread its own isolated driver."
));

allContent.push(...qna(78,
  "How do you run tests in parallel with TestNG?",
  null,
  [
    "<!-- testng.xml -->",
    "<suite name=\"ParallelSuite\" parallel=\"methods\" thread-count=\"4\">",
    "    <test name=\"LoginTests\">",
    "        <classes>",
    "            <class name=\"com.example.LoginTest\"/>",
    "        </classes>",
    "    </test>",
    "</suite>",
    "",
    "// In test class — use ThreadLocal driver from BaseTest",
    "@Test",
    "public void testOne() { /* uses getDriver() — thread-safe */ }",
    "",
    "@Test",
    "public void testTwo() { /* runs concurrently in separate thread */ }"
  ],
  "parallel=\"methods\" runs each @Test method in its own thread. parallel=\"tests\" runs each <test> block in parallel."
));

allContent.push(...qna(79,
  "How do you integrate Selenium with Maven?",
  null,
  [
    "<!-- pom.xml dependencies -->",
    "<dependency>",
    "    <groupId>org.seleniumhq.selenium</groupId>",
    "    <artifactId>selenium-java</artifactId>",
    "    <version>4.18.1</version>",
    "</dependency>",
    "<dependency>",
    "    <groupId>io.github.bonigarcia</groupId>",
    "    <artifactId>webdrivermanager</artifactId>",
    "    <version>5.7.0</version>",
    "</dependency>",
    "",
    "<!-- Run tests -->",
    "mvn test",
    "mvn test -Dsurefire.suiteXmlFiles=testng.xml",
    "mvn test -Dtest=LoginTest"
  ],
  null
));

allContent.push(...qna(80,
  "What is WebDriverManager? Why is it used?",
  "WebDriverManager automatically downloads and configures the correct browser driver binary — no manual ChromeDriver setup needed.",
  [
    "// Old way — manual path setup",
    "System.setProperty(\"webdriver.chrome.driver\", \"C:/drivers/chromedriver.exe\");",
    "",
    "// New way — WebDriverManager handles it automatically",
    "import io.github.bonigarcia.wdm.WebDriverManager;",
    "",
    "WebDriverManager.chromedriver().setup();",
    "WebDriver driver = new ChromeDriver();",
    "",
    "// For other browsers",
    "WebDriverManager.firefoxdriver().setup();",
    "WebDriverManager.edgedriver().setup();"
  ],
  null
));

// ─── SECTION 7: EXCEPTIONS ────────────────────────────────────────────────────
allContent.push(pageBreak(), h1("Section 7: Exception Handling (Q81–Q88)"), spacer());

allContent.push(...qna(81,
  "What are the most common Selenium exceptions?",
  null,
  null,
  [
    "NoSuchElementException — Element not found in DOM",
    "TimeoutException — WebDriverWait condition not met in time",
    "StaleElementReferenceException — Element was re-rendered after being found",
    "ElementNotInteractableException — Element in DOM but not interactable",
    "ElementClickInterceptedException — Another element received the click",
    "InvalidSelectorException — XPath/CSS syntax error",
    "NoSuchWindowException — Switched to a closed window",
    "NoSuchFrameException — Frame not found",
    "WebDriverException — General WebDriver error"
  ]
));

allContent.push(...qna(82,
  "How do you handle NoSuchElementException gracefully?",
  null,
  [
    "// Option 1: Use findElements and check size",
    "List<WebElement> els = driver.findElements(By.id(\"msg\"));",
    "if (!els.isEmpty()) els.get(0).click();",
    "",
    "// Option 2: Try-catch",
    "try {",
    "    driver.findElement(By.id(\"optionalBanner\")).click();",
    "} catch (NoSuchElementException e) {",
    "    System.out.println(\"Optional element not present — continuing\");",
    "}",
    "",
    "// Option 3: Custom helper",
    "public boolean isElementPresent(By by) {",
    "    return !driver.findElements(by).isEmpty();",
    "}"
  ],
  null
));

allContent.push(...qna(83,
  "How do you debug a flaky test in Selenium?",
  null,
  null,
  [
    "1. Check waits — are you using Thread.sleep()? Replace with WebDriverWait.",
    "2. Stale element — re-find the element after any DOM manipulation.",
    "3. Wrong locator — inspect element at runtime; Angular may change class names.",
    "4. Timing — add longer explicit waits for slow AJAX or server responses.",
    "5. Screenshots on failure — attach screenshot to see what the page looked like.",
    "6. Retry analyser — implement IRetryAnalyzer in TestNG to automatically re-run.",
    "7. Logging — add logger.info() before and after each action to narrow down failure point."
  ]
));

allContent.push(...qna(84,
  "What is IRetryAnalyzer in TestNG?",
  "IRetryAnalyzer allows automatic retry of failed tests — useful for transient failures.",
  [
    "public class RetryAnalyzer implements IRetryAnalyzer {",
    "    private int count = 0;",
    "    private static final int MAX = 2;",
    "",
    "    @Override",
    "    public boolean retry(ITestResult result) {",
    "        if (count < MAX) { count++; return true; }",
    "        return false;",
    "    }",
    "}",
    "",
    "@Test(retryAnalyzer = RetryAnalyzer.class)",
    "public void flakyTest() { /* will retry up to 2 times */ }"
  ],
  null
));

allContent.push(...qna(85,
  "How do you check if an element exists without throwing an exception?",
  null,
  [
    "// Recommended: findElements returns empty list, not exception",
    "public boolean isElementPresent(By locator) {",
    "    return driver.findElements(locator).size() > 0;",
    "}",
    "",
    "// With a quick wait",
    "public boolean isElementPresent(By locator, int seconds) {",
    "    try {",
    "        new WebDriverWait(driver, Duration.ofSeconds(seconds))",
    "            .until(ExpectedConditions.presenceOfElementLocated(locator));",
    "        return true;",
    "    } catch (TimeoutException e) {",
    "        return false;",
    "    }",
    "}"
  ],
  null
));

allContent.push(...qna(86,
  "What is the difference between hard assert and soft assert?",
  null,
  [
    "// Hard Assert — test stops immediately on first failure",
    "Assert.assertEquals(actual, expected, \"Values don't match\");  // Throws if fails",
    "System.out.println(\"This line never runs if above fails\");",
    "",
    "// SoftAssert — collects all failures, reports at assertAll()",
    "SoftAssert soft = new SoftAssert();",
    "soft.assertEquals(actual1, exp1, \"Check 1 failed\");",
    "soft.assertTrue(condition, \"Check 2 failed\");",
    "soft.assertEquals(actual3, exp3, \"Check 3 failed\");",
    "soft.assertAll();  // Reports ALL failures together"
  ],
  null
));

allContent.push(...qna(87,
  "How do you verify toast notifications / snackbar messages?",
  null,
  [
    "// Toasts appear and disappear quickly — use short explicit wait",
    "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));",
    "",
    "// Wait for toast to appear",
    "WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(",
    "    By.cssSelector(\".toast-message, .snackbar\")));",
    "",
    "String message = toast.getText();",
    "Assert.assertTrue(message.contains(\"saved successfully\"));",
    "",
    "// Optionally wait for it to disappear",
    "wait.until(ExpectedConditions.invisibilityOf(toast));"
  ],
  null
));

allContent.push(...qna(88,
  "How do you handle a situation where an element is visible but sendKeys() has no effect?",
  "Common in Angular/React inputs that use two-way data binding.",
  [
    "WebElement input = driver.findElement(By.cssSelector(\"input[formcontrolname='email']\"));",
    "",
    "// Option 1: Click first, then type",
    "input.click();",
    "input.sendKeys(\"test@example.com\");",
    "",
    "// Option 2: Clear, then sendKeys",
    "input.clear();",
    "input.sendKeys(\"test@example.com\");",
    "",
    "// Option 3: JS to set value (triggers Angular change detection)",
    "((JavascriptExecutor) driver).executeScript(",
    "    \"arguments[0].value = 'test@example.com'; \" +",
    "    \"arguments[0].dispatchEvent(new Event('input'))\", input);"
  ],
  null
));

// ─── SECTION 8: FRAMEWORK ────────────────────────────────────────────────────
allContent.push(pageBreak(), h1("Section 8: Framework & Integration (Q89–Q100)"), spacer());

allContent.push(...qna(89,
  "What is ExtentReports and how do you integrate it with TestNG?",
  null,
  [
    "public class ExtentReportListener implements ITestListener {",
    "    private static ExtentReports extent;",
    "    private static ExtentTest test;",
    "",
    "    @Override",
    "    public void onStart(ITestContext ctx) {",
    "        extent = new ExtentReports();",
    "        extent.attachReporter(new ExtentSparkReporter(\"report.html\"));",
    "    }",
    "    @Override",
    "    public void onTestStart(ITestResult r) {",
    "        test = extent.createTest(r.getName());",
    "    }",
    "    @Override",
    "    public void onTestSuccess(ITestResult r) { test.pass(\"PASSED\"); }",
    "    @Override",
    "    public void onTestFailure(ITestResult r) {",
    "        test.fail(r.getThrowable());",
    "    }",
    "    @Override",
    "    public void onFinish(ITestContext ctx) { extent.flush(); }",
    "}"
  ],
  null
));

allContent.push(...qna(90,
  "How do you use @Listeners in TestNG?",
  null,
  [
    "// Option 1: Annotation on test class",
    "@Listeners(ExtentReportListener.class)",
    "public class LoginTest extends BaseTest { ... }",
    "",
    "// Option 2: In testng.xml (applies to all tests)",
    "<suite>",
    "    <listeners>",
    "        <listener class-name=\"com.example.ExtentReportListener\"/>",
    "    </listeners>",
    "</suite>"
  ],
  null
));

allContent.push(...qna(91,
  "What is the difference between @BeforeMethod and @BeforeClass?",
  null,
  [
    "@BeforeClass   // Runs ONCE before all @Test methods in the class",
    "public void setUpClass() { /* DB setup, config load */ }",
    "",
    "@BeforeMethod  // Runs BEFORE EACH @Test method",
    "public void setUp() { /* launch browser, navigate to app */ }",
    "",
    "@AfterMethod   // Runs AFTER EACH @Test method",
    "public void tearDown() { /* quit browser */ }",
    "",
    "@AfterClass    // Runs ONCE after all @Test methods in class",
    "public void tearDownClass() { /* close DB connections */ }",
    "",
    "@BeforeSuite   // Runs ONCE before all tests in the suite",
    "@AfterSuite    // Runs ONCE after all tests in the suite"
  ],
  null
));

allContent.push(...qna(92,
  "How do you group tests in TestNG?",
  null,
  [
    "@Test(groups = {\"smoke\", \"login\"})",
    "public void testLogin() { ... }",
    "",
    "@Test(groups = {\"regression\", \"admin\"})",
    "public void testAdminDashboard() { ... }",
    "",
    "<!-- testng.xml — run only smoke group -->",
    "<test name=\"Smoke\">",
    "    <groups><run><include name=\"smoke\"/></run></groups>",
    "    <classes><class name=\"com.example.AllTests\"/></classes>",
    "</test>",
    "",
    "// Maven command",
    "mvn test -Dgroups=smoke"
  ],
  null
));

allContent.push(...qna(93,
  "What is @DependsOnMethods in TestNG?",
  null,
  [
    "@Test",
    "public void testLogin() { /* must pass first */ }",
    "",
    "@Test(dependsOnMethods = {\"testLogin\"})",
    "public void testDashboard() { /* only runs if testLogin passed */ }",
    "",
    "// If testLogin fails, testDashboard is SKIPPED (not failed)"
  ],
  "Use dependsOnMethods sparingly — test independence is a best practice. Only use when there is a genuine prerequisite."
));

allContent.push(...qna(94,
  "How do you configure test priorities in TestNG?",
  null,
  [
    "@Test(priority = 1)",
    "public void testOpenBrowser() { ... }",
    "",
    "@Test(priority = 2)",
    "public void testLogin() { ... }",
    "",
    "@Test(priority = 3)",
    "public void testDashboard() { ... }",
    "",
    "// Lower number = runs first",
    "// Tests with same priority run in alphabetical order",
    "// Tests without priority = default priority 0"
  ],
  null
));

allContent.push(...qna(95,
  "How do you configure Maven Surefire Plugin for TestNG?",
  null,
  [
    "<plugin>",
    "    <groupId>org.apache.maven.plugins</groupId>",
    "    <artifactId>maven-surefire-plugin</artifactId>",
    "    <version>3.2.5</version>",
    "    <configuration>",
    "        <suiteXmlFiles>",
    "            <suiteXmlFile>src/test/resources/testng.xml</suiteXmlFile>",
    "        </suiteXmlFiles>",
    "    </configuration>",
    "</plugin>",
    "",
    "// Run with custom suite",
    "mvn test -DsuiteFile=src/test/resources/smoke-testng.xml"
  ],
  null
));

allContent.push(...qna(96,
  "How do you read properties from a config file?",
  null,
  [
    "// config.properties",
    "base.url=https://app.projectsphere.com",
    "admin.email=admin@projectsphere.com",
    "timeout=60",
    "",
    "// ConfigReader singleton",
    "public class ConfigReader {",
    "    private static Properties props = new Properties();",
    "    static {",
    "        try (InputStream is = ConfigReader.class.getResourceAsStream(\"/config.properties\")) {",
    "            props.load(is);",
    "        } catch (IOException e) { throw new RuntimeException(e); }",
    "    }",
    "    public static String get(String key) { return props.getProperty(key); }",
    "}"
  ],
  null
));

allContent.push(...qna(97,
  "What is CI/CD integration for Selenium tests?",
  null,
  [
    "// Jenkinsfile (Pipeline)",
    "pipeline {",
    "    agent any",
    "    stages {",
    "        stage('Test') {",
    "            steps {",
    "                sh 'mvn test -DsuiteFile=src/test/resources/testng.xml'",
    "            }",
    "        }",
    "        stage('Report') {",
    "            steps {",
    "                publishHTML(target: [reportDir: 'test-output',",
    "                    reportFiles: 'index.html', reportName: 'Extent Report'])",
    "            }",
    "        }",
    "    }",
    "}",
    "",
    "// GitHub Actions .github/workflows/test.yml",
    "- run: mvn test --no-transfer-progress -Dheadless=true"
  ],
  null
));

allContent.push(...qna(98,
  "How do you implement a custom waiting utility?",
  null,
  [
    "public class WaitUtils {",
    "    public static WebElement waitForClickable(WebDriver d, By by, int sec) {",
    "        return new WebDriverWait(d, Duration.ofSeconds(sec))",
    "            .until(ExpectedConditions.elementToBeClickable(by));",
    "    }",
    "",
    "    public static void waitForUrl(WebDriver d, String fragment, int sec) {",
    "        new WebDriverWait(d, Duration.ofSeconds(sec))",
    "            .until(ExpectedConditions.urlContains(fragment));",
    "    }",
    "",
    "    public static boolean isPresent(WebDriver d, By by) {",
    "        return !d.findElements(by).isEmpty();",
    "    }",
    "}"
  ],
  null
));

allContent.push(...qna(99,
  "How do you handle configuration for multiple environments?",
  null,
  [
    "// Use Maven profiles",
    "<profiles>",
    "  <profile><id>dev</id><properties>",
    "    <env.url>https://dev.app.com</env.url>",
    "  </properties></profile>",
    "  <profile><id>staging</id><properties>",
    "    <env.url>https://staging.app.com</env.url>",
    "  </properties></profile>",
    "</profiles>",
    "",
    "// Run with profile",
    "mvn test -Pstaging",
    "",
    "// Read in code",
    "String baseUrl = System.getProperty(\"env.url\", \"https://localhost\");"
  ],
  null
));

allContent.push(...qna(100,
  "How do you implement a complete end-to-end test framework structure?",
  null,
  [
    "src/test/java/",
    "├── base/         BaseTest.java (driver lifecycle, ThreadLocal)",
    "├── config/       ConfigReader.java (properties singleton)",
    "├── listeners/    ExtentReportListener.java, ScreenshotListener.java",
    "├── pages/        LoginPage, DashboardPage, ... (POM)",
    "├── tests/        LoginTest, DashboardTest, ... (TestNG @Test)",
    "├── utils/        WaitUtils, ExcelReader, RandomData, ...",
    "└── cucumber/     runner/, stepdefs/, hooks/, context/",
    "",
    "src/test/resources/",
    "├── config.properties    (URLs, credentials, timeouts)",
    "├── testdata/            Excel test data files",
    "├── features/            Gherkin feature files",
    "└── testng.xml           TestNG suite configuration"
  ],
  "This structure separates concerns: tests don't know about locators, page objects don't know about reporting, configuration is centralised."
));

// ─── SECTION 9: SCENARIO-BASED ───────────────────────────────────────────────
allContent.push(pageBreak(), h1("Section 9: 25 Scenario-Based Practical Questions"), spacer());

const scenarios = [
  {
    n: "S1",
    q: "Scenario: Automate a login flow with valid and invalid credentials and verify the outcome.",
    code: [
      "public class LoginTest extends BaseTest {",
      "",
      "    @Test(dataProvider = \"creds\")",
      "    public void testLogin(String email, String pw, boolean valid) {",
      "        LoginPage lp = new LoginPage(getDriver());",
      "        lp.enterEmail(email).enterPassword(pw).clickSignIn();",
      "        if (valid) {",
      "            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(30));",
      "            wait.until(ExpectedConditions.urlContains(\"/dashboard\"));",
      "        } else {",
      "            Assert.assertTrue(lp.isErrorDisplayed());",
      "        }",
      "    }",
      "",
      "    @DataProvider(name = \"creds\")",
      "    public Object[][] creds() {",
      "        return new Object[][] {",
      "            { \"admin@test.com\", \"pass\", true  },",
      "            { \"wrong@x.com\",    \"bad\",  false }",
      "        };",
      "    }",
      "}"
    ]
  },
  {
    n: "S2",
    q: "Scenario: Search for a product, verify search results contain the keyword, then click the first result.",
    code: [
      "SearchPage sp = new SearchPage(driver);",
      "sp.enterSearch(\"Laptop\").clickSearch();",
      "",
      "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));",
      "wait.until(ExpectedConditions.visibilityOfElementLocated(",
      "    By.cssSelector(\".search-results\")));",
      "",
      "List<WebElement> results = driver.findElements(",
      "    By.cssSelector(\".result-item .title\"));",
      "Assert.assertFalse(results.isEmpty(), \"No results found\");",
      "",
      "boolean found = results.stream()",
      "    .anyMatch(r -> r.getText().contains(\"Laptop\"));",
      "Assert.assertTrue(found, \"Keyword not in any result\");",
      "results.get(0).click();"
    ]
  },
  {
    n: "S3",
    q: "Scenario: Verify all rows in a data table. Assert that status column shows 'Active' for all rows.",
    code: [
      "List<WebElement> rows = driver.findElements(",
      "    By.cssSelector(\"table tbody tr\"));",
      "Assert.assertFalse(rows.isEmpty(), \"Table is empty\");",
      "",
      "SoftAssert soft = new SoftAssert();",
      "for (int i = 0; i < rows.size(); i++) {",
      "    String status = rows.get(i)",
      "        .findElement(By.cssSelector(\"td:nth-child(3)\")).getText();",
      "    soft.assertEquals(status, \"Active\",",
      "        \"Row \" + (i+1) + \" has status: \" + status);",
      "}",
      "soft.assertAll();"
    ]
  },
  {
    n: "S4",
    q: "Scenario: Fill a multi-step registration form across 3 pages and verify final confirmation.",
    code: [
      "// Step 1",
      "driver.findElement(By.id(\"firstName\")).sendKeys(\"John\");",
      "driver.findElement(By.id(\"email\")).sendKeys(\"john@test.com\");",
      "driver.findElement(By.id(\"nextBtn\")).click();",
      "",
      "// Step 2 — wait for step 2 form",
      "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));",
      "wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(\"address\")));",
      "driver.findElement(By.id(\"address\")).sendKeys(\"123 Main St\");",
      "driver.findElement(By.id(\"nextBtn\")).click();",
      "",
      "// Step 3",
      "wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(\"submitBtn\")));",
      "driver.findElement(By.id(\"submitBtn\")).click();",
      "",
      "// Verify confirmation",
      "WebElement confirm = wait.until(ExpectedConditions.visibilityOfElementLocated(",
      "    By.cssSelector(\".confirmation-message\")));",
      "Assert.assertTrue(confirm.getText().contains(\"Registration successful\"));"
    ]
  },
  {
    n: "S5",
    q: "Scenario: Click a link that opens a new browser tab, perform an action in it, then close and return.",
    code: [
      "String parent = driver.getWindowHandle();",
      "",
      "// Click opens new tab",
      "driver.findElement(By.linkText(\"Open Report\")).click();",
      "",
      "// Wait for new tab",
      "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));",
      "wait.until(d -> d.getWindowHandles().size() > 1);",
      "",
      "// Switch to new tab",
      "String newTab = driver.getWindowHandles().stream()",
      "    .filter(h -> !h.equals(parent)).findFirst().get();",
      "driver.switchTo().window(newTab);",
      "",
      "Assert.assertTrue(driver.getTitle().contains(\"Report\"));",
      "",
      "// Close new tab and return",
      "driver.close();",
      "driver.switchTo().window(parent);"
    ]
  },
  {
    n: "S6",
    q: "Scenario: Automate a drag-and-drop Kanban board — move a card from 'To Do' to 'In Progress'.",
    code: [
      "WebElement card = driver.findElement(By.cssSelector(\".todo-column .card:first-child\"));",
      "WebElement target = driver.findElement(By.cssSelector(\".inprogress-column\"));",
      "",
      "Actions actions = new Actions(driver);",
      "actions.dragAndDrop(card, target).perform();",
      "",
      "// Verify card is now in In Progress column",
      "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));",
      "wait.until(ExpectedConditions.presenceOfElementLocated(",
      "    By.cssSelector(\".inprogress-column .card\")));",
      "Assert.assertTrue(",
      "    driver.findElements(By.cssSelector(\".inprogress-column .card\")).size() > 0);"
    ]
  },
  {
    n: "S7",
    q: "Scenario: Verify that form validation messages appear when submitting an empty form.",
    code: [
      "// Click submit without filling anything",
      "driver.findElement(By.cssSelector(\"button[type='submit']\")).click();",
      "",
      "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));",
      "",
      "// Collect all validation error messages",
      "List<WebElement> errors = wait.until(",
      "    ExpectedConditions.visibilityOfAllElementsLocatedBy(",
      "        By.cssSelector(\".field-error\")));",
      "",
      "Assert.assertEquals(errors.size(), 3, \"Expected 3 validation errors\");",
      "",
      "// Verify specific messages",
      "SoftAssert soft = new SoftAssert();",
      "soft.assertTrue(errors.get(0).getText().contains(\"required\"));",
      "soft.assertTrue(errors.get(1).getText().contains(\"required\"));",
      "soft.assertAll();"
    ]
  },
  {
    n: "S8",
    q: "Scenario: Automate an autocomplete search input — type 3 chars, pick a suggestion.",
    code: [
      "WebElement searchBox = driver.findElement(By.id(\"search-input\"));",
      "searchBox.sendKeys(\"Pro\");",
      "",
      "// Wait for dropdown suggestions",
      "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));",
      "List<WebElement> suggestions = wait.until(",
      "    ExpectedConditions.visibilityOfAllElementsLocatedBy(",
      "        By.cssSelector(\".autocomplete-suggestion\")));",
      "",
      "Assert.assertFalse(suggestions.isEmpty(), \"No autocomplete suggestions\");",
      "",
      "// Click suggestion containing 'ProjectSphere'",
      "suggestions.stream()",
      "    .filter(s -> s.getText().contains(\"ProjectSphere\"))",
      "    .findFirst()",
      "    .orElseThrow(() -> new AssertionError(\"Suggestion not found\"))",
      "    .click();"
    ]
  },
  {
    n: "S9",
    q: "Scenario: Handle a cookie consent popup — dismiss it and verify the main page loads.",
    code: [
      "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));",
      "",
      "// Check if cookie banner appears",
      "List<WebElement> banner = driver.findElements(",
      "    By.cssSelector(\".cookie-consent\"));",
      "",
      "if (!banner.isEmpty() && banner.get(0).isDisplayed()) {",
      "    // Click Reject / Decline",
      "    driver.findElement(By.cssSelector(\".cookie-reject-btn\")).click();",
      "    wait.until(ExpectedConditions.invisibilityOf(banner.get(0)));",
      "}",
      "",
      "// Verify main content is visible",
      "wait.until(ExpectedConditions.visibilityOfElementLocated(",
      "    By.cssSelector(\".main-content\")));"
    ]
  },
  {
    n: "S10",
    q: "Scenario: Test a date range picker — select start and end dates and verify the filter applies.",
    code: [
      "// Open date picker",
      "driver.findElement(By.id(\"date-range-btn\")).click();",
      "",
      "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));",
      "wait.until(ExpectedConditions.visibilityOfElementLocated(",
      "    By.cssSelector(\".calendar\")));",
      "",
      "// Select start date — click on day 1",
      "driver.findElement(By.xpath(\"//td[@data-day='1']\")).click();",
      "",
      "// Navigate to next month if needed",
      "// driver.findElement(By.cssSelector(\".cal-next\")).click();",
      "",
      "// Select end date — click on day 15",
      "driver.findElement(By.xpath(\"//td[@data-day='15']\")).click();",
      "",
      "// Apply filter",
      "driver.findElement(By.cssSelector(\".apply-dates\")).click();",
      "",
      "// Verify filter chip shows date range",
      "String chip = wait.until(ExpectedConditions.visibilityOfElementLocated(",
      "    By.cssSelector(\".active-filter\"))).getText();",
      "Assert.assertTrue(chip.contains(\"Jan 1\"));"
    ]
  },
  {
    n: "S11",
    q: "Scenario: Navigate through all pages of a paginated table and collect all row data.",
    code: [
      "List<String> allData = new ArrayList<>();",
      "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));",
      "",
      "while (true) {",
      "    List<WebElement> rows = driver.findElements(By.cssSelector(\"tbody tr\"));",
      "    rows.forEach(r -> allData.add(r.getText()));",
      "",
      "    // Check for 'Next' button",
      "    List<WebElement> next = driver.findElements(",
      "        By.cssSelector(\".pagination-next:not(.disabled)\"));",
      "    if (next.isEmpty()) break;",
      "",
      "    WebElement firstRow = rows.get(0);",
      "    next.get(0).click();",
      "",
      "    // Wait for table to refresh (stale = old rows gone)",
      "    wait.until(ExpectedConditions.stalenessOf(firstRow));",
      "}",
      "System.out.println(\"Total records: \" + allData.size());"
    ]
  },
  {
    n: "S12",
    q: "Scenario: Verify sorting on a table column — click header, assert rows are in ascending order.",
    code: [
      "// Click the 'Name' column header",
      "driver.findElement(By.cssSelector(\"th.name-col\")).click();",
      "",
      "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));",
      "wait.until(ExpectedConditions.attributeContains(",
      "    By.cssSelector(\"th.name-col\"), \"class\", \"sorted-asc\"));",
      "",
      "// Get all values in the Name column",
      "List<String> names = driver.findElements(",
      "    By.cssSelector(\"td.name-cell\"))",
      "    .stream().map(WebElement::getText).collect(Collectors.toList());",
      "",
      "// Verify sorted order",
      "List<String> sorted = new ArrayList<>(names);",
      "Collections.sort(sorted);",
      "Assert.assertEquals(names, sorted, \"Column not sorted ascending\");"
    ]
  },
  {
    n: "S13",
    q: "Scenario: Complete a shopping cart flow — add item, verify cart count, proceed to checkout.",
    code: [
      "// Add product to cart",
      "driver.findElement(By.cssSelector(\".product-card:first-child .add-to-cart\")).click();",
      "",
      "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));",
      "",
      "// Wait for cart count to update",
      "wait.until(ExpectedConditions.textToBePresentInElementLocated(",
      "    By.cssSelector(\".cart-count\"), \"1\"));",
      "",
      "// Navigate to cart",
      "driver.findElement(By.cssSelector(\".cart-icon\")).click();",
      "wait.until(ExpectedConditions.urlContains(\"/cart\"));",
      "",
      "// Verify item in cart",
      "Assert.assertTrue(driver.findElements(",
      "    By.cssSelector(\".cart-item\")).size() == 1);",
      "",
      "// Proceed to checkout",
      "driver.findElement(By.cssSelector(\".checkout-btn\")).click();",
      "wait.until(ExpectedConditions.urlContains(\"/checkout\"));"
    ]
  },
  {
    n: "S14",
    q: "Scenario: Verify that a file uploads successfully and appears in the file list.",
    code: [
      "// Find file input (may be hidden)",
      "WebElement input = driver.findElement(By.cssSelector(\"input[type='file']\"));",
      "",
      "// If hidden, make it visible via JS",
      "((JavascriptExecutor) driver).executeScript(",
      "    \"arguments[0].style.display = 'block'\", input);",
      "",
      "// Send file path",
      "input.sendKeys(\"C:\\\\TestData\\\\sample.pdf\");",
      "",
      "// Click upload button",
      "driver.findElement(By.id(\"uploadBtn\")).click();",
      "",
      "// Wait for success message",
      "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));",
      "wait.until(ExpectedConditions.textToBePresentInElementLocated(",
      "    By.cssSelector(\".upload-status\"), \"Upload complete\"));",
      "",
      "// Verify file in list",
      "List<WebElement> files = driver.findElements(By.cssSelector(\".file-list-item\"));",
      "Assert.assertTrue(files.stream()",
      "    .anyMatch(f -> f.getText().contains(\"sample.pdf\")));"
    ]
  },
  {
    n: "S15",
    q: "Scenario: Automate logout flow and verify user cannot access protected page after logout.",
    code: [
      "// Click user avatar / profile menu",
      "driver.findElement(By.cssSelector(\".user-avatar\")).click();",
      "",
      "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));",
      "wait.until(ExpectedConditions.visibilityOfElementLocated(",
      "    By.cssSelector(\".dropdown-menu\")));",
      "",
      "// Click Logout",
      "driver.findElement(By.linkText(\"Logout\")).click();",
      "",
      "// Verify redirected to login page",
      "wait.until(ExpectedConditions.urlContains(\"/login\"));",
      "Assert.assertTrue(driver.getCurrentUrl().contains(\"/login\"));",
      "",
      "// Attempt to navigate to protected dashboard",
      "driver.navigate().to(\"https://app.projectsphere.com/admin/dashboard\");",
      "",
      "// Should redirect back to login",
      "wait.until(ExpectedConditions.urlContains(\"/login\"));",
      "Assert.assertTrue(driver.getCurrentUrl().contains(\"/login\"));"
    ]
  },
  {
    n: "S16",
    q: "Scenario: Test a rich text editor (CKEditor/TinyMCE) — enter text and verify its content.",
    code: [
      "// Rich text editors use iframe internally",
      "WebElement editorFrame = driver.findElement(By.cssSelector(\".editor iframe\"));",
      "driver.switchTo().frame(editorFrame);",
      "",
      "// Find the editable body",
      "WebElement body = driver.findElement(By.tagName(\"body\"));",
      "body.clear();",
      "body.sendKeys(\"Hello from Selenium!\");",
      "",
      "// Switch back to main page",
      "driver.switchTo().defaultContent();",
      "",
      "// Submit form",
      "driver.findElement(By.id(\"submitPost\")).click();",
      "",
      "// Verify saved content",
      "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));",
      "wait.until(ExpectedConditions.textToBePresentInElementLocated(",
      "    By.cssSelector(\".post-content\"), \"Hello from Selenium!\"));"
    ]
  },
  {
    n: "S17",
    q: "Scenario: Perform a visual check — verify a specific element is in the viewport.",
    code: [
      "WebElement el = driver.findElement(By.id(\"importantSection\"));",
      "",
      "// Check if element is in viewport using JS",
      "Boolean inViewport = (Boolean) ((JavascriptExecutor) driver).executeScript(",
      "    \"var rect = arguments[0].getBoundingClientRect();\" +",
      "    \"return (rect.top >= 0 && rect.bottom <= window.innerHeight);\", el);",
      "",
      "Assert.assertTrue(inViewport, \"Element is not in viewport\");",
      "",
      "// Scroll it into view if needed",
      "if (!inViewport) {",
      "    ((JavascriptExecutor) driver)",
      "        .executeScript(\"arguments[0].scrollIntoView(true)\", el);",
      "}"
    ]
  },
  {
    n: "S18",
    q: "Scenario: Handle a session timeout — detect the re-login prompt and log back in.",
    code: [
      "// Simulate long wait or expire session cookie",
      "driver.manage().deleteCookieNamed(\"session_token\");",
      "driver.navigate().refresh();",
      "",
      "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));",
      "",
      "// Detect session expired modal or redirect to login",
      "boolean sessionModal = !driver.findElements(",
      "    By.cssSelector(\".session-expired-modal\")).isEmpty();",
      "",
      "if (sessionModal) {",
      "    driver.findElement(By.cssSelector(\".re-login-btn\")).click();",
      "}",
      "",
      "// Wait for login page",
      "wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(\"email\")));",
      "driver.findElement(By.id(\"email\")).sendKeys(\"admin@test.com\");",
      "driver.findElement(By.id(\"password\")).sendKeys(\"pass\");",
      "driver.findElement(By.cssSelector(\".signin-btn\")).click();",
      "wait.until(ExpectedConditions.urlContains(\"/dashboard\"));"
    ]
  },
  {
    n: "S19",
    q: "Scenario: Verify the API response and UI both show the same data (hybrid test).",
    code: [
      "// API call using Java HttpClient",
      "HttpClient client = HttpClient.newHttpClient();",
      "HttpRequest req = HttpRequest.newBuilder()",
      "    .uri(URI.create(\"https://api.app.com/projects\"))",
      "    .header(\"Authorization\", \"Bearer \" + token)",
      "    .GET().build();",
      "HttpResponse<String> resp = client.send(req,",
      "    HttpResponse.BodyHandlers.ofString());",
      "JSONArray apiData = new JSONArray(resp.body());",
      "String apiProjectName = apiData.getJSONObject(0).getString(\"name\");",
      "",
      "// UI check",
      "driver.get(\"https://app.com/projects\");",
      "String uiName = driver.findElement(",
      "    By.cssSelector(\".project-list .project-name:first-child\")).getText();",
      "",
      "Assert.assertEquals(uiName, apiProjectName, \"UI and API data mismatch\");"
    ]
  },
  {
    n: "S20",
    q: "Scenario: Automate testing of a responsive design — verify layout changes at mobile viewport.",
    code: [
      "// Desktop viewport",
      "driver.manage().window().setSize(new Dimension(1440, 900));",
      "WebElement navBar = driver.findElement(By.cssSelector(\".nav-desktop\"));",
      "Assert.assertTrue(navBar.isDisplayed(), \"Desktop nav should be visible\");",
      "",
      "// Mobile viewport",
      "driver.manage().window().setSize(new Dimension(375, 812));",
      "",
      "// Desktop nav should be hidden, hamburger should appear",
      "Assert.assertFalse(navBar.isDisplayed(), \"Desktop nav should hide on mobile\");",
      "Assert.assertTrue(driver.findElement(",
      "    By.cssSelector(\".hamburger-menu\")).isDisplayed());"
    ]
  },
  {
    n: "S21",
    q: "Scenario: Test role-based access — verify PM cannot see Admin-only menu items.",
    code: [
      "// Login as PM",
      "loginPage.enterEmail(\"pm@test.com\").enterPassword(\"pass\").clickSignIn();",
      "",
      "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));",
      "wait.until(ExpectedConditions.urlContains(\"/pm/dashboard\"));",
      "",
      "// Verify Admin menu is NOT present",
      "List<WebElement> adminMenu = driver.findElements(",
      "    By.cssSelector(\".admin-only-menu\"));",
      "Assert.assertTrue(adminMenu.isEmpty(), \"PM should not see Admin menu\");",
      "",
      "// Attempt to access Admin URL directly",
      "driver.navigate().to(\"https://app.com/admin/manage-users\");",
      "",
      "// Should get 403 or redirect",
      "wait.until(ExpectedConditions.or(",
      "    ExpectedConditions.urlContains(\"/403\"),",
      "    ExpectedConditions.urlContains(\"/dashboard\")));"
    ]
  },
  {
    n: "S22",
    q: "Scenario: Verify real-time notifications — trigger an event and check notification badge.",
    code: [
      "// Initial notification count",
      "WebElement badge = driver.findElement(By.cssSelector(\".notification-badge\"));",
      "int before = Integer.parseInt(badge.getText().trim());",
      "",
      "// Trigger event (e.g., another user assigns a task — simulate via API)",
      "// ... make API call to create assignment ...",
      "",
      "// Wait for badge to update",
      "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));",
      "wait.until(d -> {",
      "    String text = d.findElement(",
      "        By.cssSelector(\".notification-badge\")).getText().trim();",
      "    return !text.isEmpty() && Integer.parseInt(text) > before;",
      "});",
      "",
      "int after = Integer.parseInt(",
      "    driver.findElement(By.cssSelector(\".notification-badge\")).getText());",
      "Assert.assertEquals(after, before + 1);"
    ]
  },
  {
    n: "S23",
    q: "Scenario: Run the same test suite across Chrome and Firefox using TestNG parameters.",
    code: [
      "<!-- testng.xml -->",
      "<suite name=\"CrossBrowser\" parallel=\"tests\" thread-count=\"2\">",
      "    <test name=\"Chrome\"><parameter name=\"browser\" value=\"chrome\"/>",
      "        <classes><class name=\"com.example.LoginTest\"/></classes>",
      "    </test>",
      "    <test name=\"Firefox\"><parameter name=\"browser\" value=\"firefox\"/>",
      "        <classes><class name=\"com.example.LoginTest\"/></classes>",
      "    </test>",
      "</suite>",
      "",
      "// BaseTest",
      "@Parameters(\"browser\")",
      "@BeforeMethod",
      "public void setUp(@Optional(\"chrome\") String browser) {",
      "    WebDriver driver = browser.equals(\"firefox\")",
      "        ? new FirefoxDriver() : new ChromeDriver();",
      "    driver.manage().window().maximize();",
      "    driverHolder.set(driver);",
      "}"
    ]
  },
  {
    n: "S24",
    q: "Scenario: Verify that a chart/graph rendered correctly (element count check, not pixel comparison).",
    code: [
      "// Navigate to analytics page",
      "driver.findElement(By.linkText(\"Analytics\")).click();",
      "",
      "WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));",
      "wait.until(ExpectedConditions.visibilityOfElementLocated(",
      "    By.cssSelector(\".chart-container\")));",
      "",
      "// Verify chart bars/slices rendered (SVG elements)",
      "List<WebElement> bars = driver.findElements(",
      "    By.cssSelector(\".chart-container svg .bar\"));",
      "Assert.assertEquals(bars.size(), 12, \"Expected 12 bars (months)\");",
      "",
      "// Verify legend labels match expected data",
      "List<String> legends = driver.findElements(",
      "    By.cssSelector(\".chart-legend-item\"))",
      "    .stream().map(WebElement::getText).collect(Collectors.toList());",
      "Assert.assertTrue(legends.contains(\"Projects\"));",
      "Assert.assertTrue(legends.contains(\"Tasks\"));"
    ]
  },
  {
    n: "S25",
    q: "Scenario: Complete end-to-end test — Admin creates a project, PM sees it, Developer views task.",
    code: [
      "// Step 1: Admin logs in, creates project",
      "LoginPage login = new LoginPage(driver);",
      "AdminDashboardPage adminDash = login.loginAsAdmin(adminEmail, adminPass);",
      "ManageProjectsPage projects = adminDash.navigateToProjects();",
      "projects.clickCreateProject();",
      "new CreateProjectDialog(driver).enterProjectName(\"E2E Test Project\")",
      "    .clickCreate();",
      "",
      "// Step 2: Logout as Admin",
      "adminDash.logout();",
      "",
      "// Step 3: PM logs in — verifies project is listed",
      "PMDashboardPage pmDash = login.loginAsPM(pmEmail, pmPass);",
      "MyProjectsPMPage myProjects = pmDash.navigateToMyProjects();",
      "Assert.assertTrue(myProjects.isProjectInList(\"E2E Test Project\"),",
      "    \"PM should see the project created by Admin\");",
      "",
      "// Step 4: PM navigates to project, adds a task",
      "myProjects.clickProject(\"E2E Test Project\").addTask(\"Implement login\");",
      "",
      "System.out.println(\"E2E flow completed successfully\");"
    ]
  }
];

scenarios.forEach(s => {
  allContent.push(label(`${s.n}. ${s.q}`, true));
  allContent.push(body("Solution:"));
  s.code.forEach(l => allContent.push(code(l)));
  allContent.push(spacer());
});

// Final
allContent.push(pageBreak());
allContent.push(new Paragraph({
  children: [new TextRun({ text: "End of Document", font: FONT, bold: true, size: 28, color: HEADING_COLOR })],
  alignment: AlignmentType.CENTER,
  spacing: { before: 1440 }
}));

const doc = new Document({
  styles: {
    default: { document: { run: { font: FONT, size: 22 } } },
    paragraphStyles: [
      { id: "Heading1", name: "Heading 1", basedOn: "Normal", next: "Normal", quickFormat: true,
        run: { size: 32, bold: true, font: FONT, color: HEADING_COLOR },
        paragraph: { spacing: { before: 360, after: 120 }, outlineLevel: 0 } },
      { id: "Heading2", name: "Heading 2", basedOn: "Normal", next: "Normal", quickFormat: true,
        run: { size: 26, bold: true, font: FONT, color: ACCENT },
        paragraph: { spacing: { before: 240, after: 80 }, outlineLevel: 1 } },
    ]
  },
  numbering: {
    config: [{
      reference: "bullets",
      levels: [{ level: 0, format: LevelFormat.BULLET, text: "•", alignment: AlignmentType.LEFT,
        style: { paragraph: { indent: { left: 720, hanging: 360 } } } }]
    }]
  },
  sections: [{
    properties: {
      page: { size: { width: 12240, height: 15840 }, margin: { top: 1080, right: 1080, bottom: 1080, left: 1080 } }
    },
    headers: {
      default: new Header({
        children: [new Paragraph({
          children: [new TextRun({ text: "Selenium WebDriver — 100 Interview Q&A + 25 Scenarios", font: FONT, size: 18, color: "888888" })],
          alignment: AlignmentType.RIGHT,
          border: { bottom: { style: BorderStyle.SINGLE, size: 4, color: "CCCCCC", space: 4 } }
        })]
      })
    },
    footers: {
      default: new Footer({
        children: [new Paragraph({
          children: [
            new TextRun({ text: "Page ", font: FONT, size: 18, color: "888888" }),
            new TextRun({ children: [PageNumber.CURRENT], font: FONT, size: 18, color: "888888" }),
            new TextRun({ text: " of ", font: FONT, size: 18, color: "888888" }),
            new TextRun({ children: [PageNumber.TOTAL_PAGES], font: FONT, size: 18, color: "888888" }),
          ],
          alignment: AlignmentType.CENTER,
          border: { top: { style: BorderStyle.SINGLE, size: 4, color: "CCCCCC", space: 4 } }
        })]
      })
    },
    children: allContent
  }]
});

Packer.toBuffer(doc).then(buf => {
  fs.writeFileSync("C:\\Users\\2479309\\Desktop\\Project Sphere\\Selenium_Interview_QA.docx", buf);
  console.log("Done: Selenium_Interview_QA.docx");
});
