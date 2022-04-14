package com.mmh.qa.auto;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Date;
import java.util.List;

public abstract class RootTest {
    public static final String ENV_BROWSER_DRIVER = "BROWSER_DRIVER";
    public static final String DRIVER_PATH = "\\bin\\webdrivers";
    public static final String DRIVER_CHROME = "chromedriver.exe";
    public static final String DRIVER_EDGE = "msedgedriver.exe";
    public static final String DRIVER_FIREFOX = "geckodriver.exe";
    public static final String DRIVER_DEFAULT = DRIVER_CHROME;
    public static final String LOG4J_PROPERTIES_FILE = "log4j2.properties";
    public static final String LOG_PATH = "\\target\\logs";
    public static final String SCREENSHOTS_PATH = "\\target\\screenshots";
    public static final int WAITFOR_DEFAULT_TIMEOUT = 30;

    private WebDriver webDriver;
    private WebDriverWait webWaiterDefault;  // predefined with 30s timeout
    protected Logger logger;

    private String webDriverName;
    private Capabilities webDriverOptions;
    private final String logHeaderSeparator = "=============================================================";

    public int findAnyElementBy(By[] by) {
        return findAnyElementBy(by, false);
    }

    public int findAnyElementBy(By[] by, boolean silent) {
        return findAnyElementBy(by, false, WAITFOR_DEFAULT_TIMEOUT);
    }

    /**
     * A "fast" search for multiple "By" criteria. This performs low-timeout "findBy" calls in rapid succession.
     * This results in an overall quicker detection time as compared to sequentially searching for each "By" criteria.
     * This is useful when you're waiting for the next UI to appear which may be one of multiple possible UIs.
     * @param by - array of By objects to poll
     * @param silent - if true, no notifications are logged
     * @param timeoutMillis - overall timeout(ms)
     * @return index of element that was detected, otherwise -1
     */
    public int findAnyElementBy(By[] by, boolean silent, long timeoutMillis) {
        long tBegin = methodHeaderEnter();
        long timeoutTime = System.currentTimeMillis() + timeoutMillis;
        int byIndex = 0;
        WebElement we = null;
        Duration savedDur = getDriver().manage().timeouts().getImplicitWaitTimeout();  // to be restored before exiting
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(1));

        do {
            for (byIndex=0; byIndex<by.length; byIndex++) {
                try {
                    we = webDriver.findElement(by[byIndex]);
                    if (!silent) logDebug("Found element: " + by[byIndex].toString());
                    break;
                } catch (Exception e) {
                    if (!silent) logError(String.format("Cannot find element:(%s) - Encountered error:%s ", by[byIndex], e.getMessage()));
                }

            }
            // keep looping until one of the webelements is found or until timeout
        } while (we == null && System.currentTimeMillis() < timeoutTime);

        getDriver().manage().timeouts().implicitlyWait(savedDur);  // restore timeout
        methodFooterExit(tBegin);
        return we == null ? -1 : byIndex;
    }

    /**
     * Wrapper around WebDriver.findElement() that returns a value instead of throwing an exception
     * @param by search criteria
     * @return WebElement found or null
     */
    public WebElement findElementBy(By by) {
        return findElementBy(by, false);
    }

    /**
     * Wrapper around WebDriver.findElement() that returns a value instead of throwing an exception
     * @param by search criteria
     * @param silent if true do not log messages
     * @return WebElement found or null
     */
    public WebElement findElementBy(By by, boolean silent) {
        long tBegin = methodHeaderEnter();
        WebElement we = null;
        try {
            we = webDriver.findElement(by);
        } catch (Exception e) {
            if (!silent) {
                logError(String.format("Cannot find element:(%s) - Encountered error:%s ", by.toString(), e.getMessage()));
            }
        }
        methodFooterExit(tBegin);
        return we;
    }

    /**
     * Wrapper around WebDriver.findElements() that returns a value instead of throwing an exception
     * @param by search criteria
     * @return WebElements found or null
     */
    public List findElementsBy(By by) {
        return findElementsBy(by, false);
    }

    /**
     * Wrapper around WebDriver.findElements() that returns a value instead of throwing an exception
     * @param by search criteria
     * @param silent if true do not log messages
     * @return WebElements found or null
     */
    public List findElementsBy(By by, boolean silent) {
        List<WebElement> weList = null;
        try {
            weList = webDriver.findElements(by);
        } catch (Exception e) {
            if (!silent) {
                System.err.println(String.format("Cannot find element:(%s) - Encountered error:%s ", by.toString(), e.getMessage()));
            }
        }
        return weList;
    }

    public String fixHttpsPrefix(String domain) {

        // ensure prefix "https://"
        if (domain.startsWith("http://")) {
            domain.replace("http://" ,"https://");
        }
        if (!domain.startsWith("http")) {
            domain = "https://" + domain;
        }
        return domain;
    }

    /**
     * Returns the browser driver for this object. The browser driver object is initialized if needed.
     * @return Browser driver for this object
     */
    public WebDriver getDriver() {
        if (webDriver == null) {
            if (webDriverName == null && !webDriverName.equals(DRIVER_FIREFOX)) {
                logError("Browser driver property is not set");
            } else {
                try {
                    switch (webDriverName) {
                        case DRIVER_CHROME:
                            if (webDriverOptions == null) {
                                webDriver = new ChromeDriver();
                            } else {
                                webDriver = new ChromeDriver((ChromeOptions)webDriverOptions);
                            }
                            break;
                        case DRIVER_EDGE:
                            webDriver = new EdgeDriver();
                            //TODO support for edge
                            break;
                        case DRIVER_FIREFOX:
                            webDriver = new FirefoxDriver();
                            //TODO support for FF
                            break;
                        default:
                            throw new Exception("Unknown browser driver: " + webDriverName);
                    }
                } catch (Exception e) {
                    logError("Cannot get browser driver. Error: " + e.getMessage());
                    webDriver = null;
                }
            }
        }
        return webDriver;
    }

    public Capabilities getBrowserDriverOptions() {
        return webDriverOptions;
    }

    public WebDriverWait getWebWaiterDefault() {
        return webWaiterDefault;
    }

    //TODO: more enhancement for wrappers?
    public void logDebug(String msg) {
        logger.debug(msg);
    }
    public void logInfo(String msg) {
        logger.info(msg);
    }
    public void logWarn(String msg) {
        logger.warn(msg);
    }
    public void logError(String msg) {
        logger.error(msg);
    }
    public void logError(String msg, boolean screenshot) {
        logger.error(msg);
    }
    public void logFatal(String msg) {
        logger.fatal(msg);
    }
    public void logFatal(String msg, boolean screenshot) {
        logger.fatal(msg);
    }

    /**
     * For producing a consistent log entries when entering or exiting a method. This is Useful for debugging.
     * @return output of System.currentTimeMillis(). (useful for time benchmarking)
     */
    public long methodHeaderEnter() {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        logDebug(String.format("\n%s entered %s(), %s", logHeaderSeparator, methodName, new Date()));
        //TODO show only the time (not entire date)
        return System.currentTimeMillis();
    }

    /**
     * For producing a consistent log entries when entering or exiting a method. This is Useful for debugging.
     * @param startTime will calculate an elapsed time value from this.
     */
    public void methodFooterExit(long startTime) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        String elapsedTime = "elapsed:" + Long.toString(System.currentTimeMillis() - startTime) + "ms";
        logDebug(String.format("\n%s exited %s(), %s", logHeaderSeparator, methodName, elapsedTime));
        //TODO show only the time (not entire date)
    }

    /**
     * Wrapper around screenshot()
     */
    public void screenshot() {
        screenshot(null);
    }

    /**
     * Wrapper around taking a screenshot
      * @param message Message to include with screenshot. Message text will be integrated into file name and also written to the info-log
     */
    //TODO incorporate adding a message-type param so that can post an error, warning etc simultaneously
    public void screenshot(String message) {
        String showMessage = message==null ? "" : message;
        // build the file name = <message>_<serial#>.png
        final Character[] INVALID_CHARS = {' ', '"', '*', ':', '<', '>', '?', '\\', '|', 0x7F};
        String nameMessage = showMessage;  // remove invalid chars
        for (char c : INVALID_CHARS) {
            nameMessage = nameMessage.replace(c, '_');
        }
        String nameSerial = String.valueOf(System.currentTimeMillis());
        nameSerial = nameSerial.substring(nameSerial.length() - 6) + ".png";
        String nameFinal = "";
        try {
            Paths.get(nameMessage);
            if (nameMessage.length() > 25) {  // use the first 25 chars
                nameMessage = nameMessage.substring(0, 24);
            }
            nameFinal = nameMessage + "_" + nameSerial;  // only use the last 6 chars of serial
        } catch (RuntimeException re) {
            nameFinal = nameSerial;
            logWarn("Invalid char found. Cannot integrate message into file name. File name=" + nameSerial);
        }

        TakesScreenshot shotTaker = (TakesScreenshot)getDriver();
        File shot = shotTaker.getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(shot, new File(System.getProperty("user.dir") + SCREENSHOTS_PATH + "\\" + nameFinal));
            logInfo(String.format("screenshot '%s' saved: %s", nameFinal, showMessage));
        } catch (IOException ioe) {
            logError("Error saving screenshot:" + nameFinal + ". " + ioe.getMessage());
            ioe.printStackTrace();
        }
    }

    /**
     * Wrapper around Thread.sleep() that doesn't throw an exception.
     * @param timeMillis
     */
    public void sleep(long timeMillis) {
        try {
            Thread.sleep(timeMillis);
        } catch (InterruptedException ie) {

        }
    }

    public void setBrowserDriver(WebDriver theDriver) {
        webDriver = theDriver;
    }

    /**
     * Validates and sets the System property for the webdriver for this object
     * This only sets the property, it does not initialize the webdriver.
     * @param driverName
     */
    public void setBrowserDriver(String driverName) {
        String foundBDPath = null;
        // look for windows env var. This overrides getting the WebDriver from the default location
        try {
            File f = new File(System.getenv(ENV_BROWSER_DRIVER));
            if (f != null && f.exists()) {
                foundBDPath = System.getenv(ENV_BROWSER_DRIVER);
                System.out.println("found driver from Win env:" + ENV_BROWSER_DRIVER);
                System.out.println("found driver file in dir:" + f.getPath());
            }
        } catch (Exception e) {
            // dont need to do anything here
        }

        if (foundBDPath == null) {
            // look in the current dir
            String browseDriverPath = DRIVER_PATH + "\\" + driverName;
            File f = new File(System.getProperty("user.dir") + browseDriverPath);
            if (f.exists()) {
                foundBDPath = f.getPath();
                System.out.println("found driver file in user.dir:" + foundBDPath);
            }
        }
        if (foundBDPath == null) {
            throw new RuntimeException("Browser driver not found for:" + driverName);
        } else {
            switch (driverName) {
                case DRIVER_CHROME:
                    System.setProperty("webdriver.chrome.driver", foundBDPath);
                    break;
                case DRIVER_EDGE:
                    System.setProperty("webdriver.?????.driver?", foundBDPath);
                    break;
                case DRIVER_FIREFOX:
                    //System.setProperty("webdriver.firefox.driver?", foundBDPath);
                    break;
                default:
                    System.err.println("Unknown browser driver:" + driverName);
            }
            webDriver = null;
            webDriverName = driverName;
        }
    }

    public void setBrowserDriverOptions(Capabilities theOptions) {
        webDriverOptions = theOptions == null ? webDriverOptions : theOptions;
    }

    /**
     * Initial setup of logging
     * Log file location: [user.dir]\target\logs
     * Log file name: [name of class].log
     * TODO: allow options for logging
     */
    private void setupLogging() {
        // create a new configuration
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        //builder.setStatusLevel(Level.INFO);
        builder.setConfigurationName("config1");
        String layoutPattern = "%d{HH:mm:ss} %-5p %c{1}:%L %m%n";  // log entry line format
        LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
                .addAttribute("pattern", layoutPattern); //"%d [%t] %-5level: %msg%n%throwable");
        // create a console appender
        AppenderComponentBuilder appenderBuilderCons = builder.newAppender("outconsole", "CONSOLE")
                .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
        appenderBuilderCons.add(layoutBuilder);
        builder.add(appenderBuilderCons);
        // create a file appender
        AppenderComponentBuilder appenderBuilderFile = builder.newAppender("outfile", "File")
                .addAttribute("fileName", System.getProperty("user.dir")+LOG_PATH+"\\log_"+getClass().getSimpleName()+".log");
        appenderBuilderFile.add(layoutBuilder);
        builder.add(appenderBuilderFile);
        // create logger
        builder.add(builder.newRootLogger(Level.INFO)
                .add(builder.newAppenderRef("outconsole"))
                .add(builder.newAppenderRef("outfile")));
        Configurator.initialize(builder.build());
        logger = LogManager.getLogger(getClass());
        logger.info(String.format("\n\n%s\nOpening log file for:%s (%s)", logHeaderSeparator, getClass(), new Date()));
    }

    /**
     * Wrapper around waitFor()
     * @param xPath element to wait for
     * @return true if element was found
     */
    public boolean waitFor(String xPath) {
        return waitFor(xPath, WAITFOR_DEFAULT_TIMEOUT);
    }

    /**
     * Wrapper around waitFor()
     * @param xPath element to wait for
     * @param timeOutSec
     * @return true if element was found
     */
    public boolean waitFor(String xPath, long timeOutSec) {
        return waitFor(xPath, timeOutSec, false);
    }

    /**
     * Wraps call to WebDriverWait.
     * @param xPath element to wait for
     * @param timeOutSec timeout time
     * @param silent if true, no notifications are logged
     * @return true if element was found
     */
    //TODO expand to accept more waitfor conditions
    public boolean waitFor(String xPath, long timeOutSec, boolean silent) {
        WebDriverWait waiter;
        if (timeOutSec == WAITFOR_DEFAULT_TIMEOUT) {
            if (webWaiterDefault == null) {
                webWaiterDefault = new WebDriverWait(webDriver, Duration.ofSeconds(WAITFOR_DEFAULT_TIMEOUT));
            }
            waiter = webWaiterDefault;
        } else {
            waiter = new WebDriverWait(webDriver, Duration.ofSeconds(timeOutSec));
        }
        long t1 = System.currentTimeMillis();
        try {
            waiter.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xPath)));
            if (!silent) logError("Found element "+xPath+" after " + (System.currentTimeMillis()-t1) + "ms");
            return true;
        } catch (TimeoutException toe) {
            if (!silent) logError("Timed out waiting for element "+xPath+" after " + (System.currentTimeMillis()-t1) + "ms");
            return false;
        }
    }

    public RootTest() {
        this(DRIVER_DEFAULT);
    }

    public RootTest(RootTest activeTest) {
        webDriver = activeTest.webDriver;
        webDriverName = activeTest.webDriverName;
        webDriverOptions = activeTest.webDriverOptions;
        logger = activeTest.logger;
    }

    public RootTest(WebDriver driver) {
        webDriver = driver;
    }

    public RootTest(String driver) {
        this(driver, null);
    }

    public RootTest(String driver, Capabilities theOptions) {
        setBrowserDriver(driver);
        setBrowserDriverOptions(theOptions);
        setupLogging();
    }
}