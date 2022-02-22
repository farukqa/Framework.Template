    package com.mmh.qa.auto;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TestRoot {
    public static final String ENV_BROWSER_DRIVER = "BROWSER_DRIVER";
    public static final String DRIVER_PATH_CURRENT = "\\bin\\webdrivers";
    public static final String DRIVER_CHROME = "chromedriver.exe";
    public static final String DRIVER_EDGE = "msedgedriver.exe";
    public static final String DRIVER_FIREFOX = "geckodriver.exe";
    public static final String DRIVER_DEFAULT = DRIVER_CHROME;
    public static final String LOG_PATH = "\\";
    public static final int WAITFOR_DEFAULT_TIMEOUT = 30;

    protected RemoteWebDriver webDriver;

    private String webDriverName;
    private Capabilities webDriverOptions;
    private WebDriverWait webWaiterDefault;  // predefined with 30s timeout

    public TestRoot() {
        setBrowserDriver(DRIVER_DEFAULT);
    }

    public TestRoot(String driver) {
        setBrowserDriver(driver);
    }

    public TestRoot(String driver, Capabilities theOptions) {
        setBrowserDriver(driver);
        setBrowserDriverOptions(theOptions);
    }

    /**
     * Wrapper around WebDriver.findElement() that returns a value instead of throwing exceptions
     * @param by
     * @return WebElement found or null
     */
    public WebElement findElementBy(By by) {
        WebElement we = null;
        try {
            we = webDriver.findElement(by);
        } catch (Exception e) {
            System.err.println(String.format("Cannot find element:(%s) - Encountered error:%s ", by.toString(), e.getMessage() ));
        }
        return we;
    }

    /**
     * Wrapper around WebDriver.findElements() that returns a value instead of throwing exceptions
     * @param by
     * @return WebElement found or null
     */
    public List findElementsBy(By by) {
        List<WebElement> weList = null;
        try {
            weList = webDriver.findElements(by);
        } catch (Exception e) {
            System.err.println(String.format("Cannot find element:(%s) - Encountered error:%s ", by.toString(), e.getMessage() ));
        }
        return weList;
    }

    /**
     * Wrapper around getBrowserDriver(). This is just a shorter method name.
     * @return
     */
    public RemoteWebDriver getDriver() {
        return getBrowserDriver();
    }

    /**
     *
     * @return Browser driver for this object
     */
    public RemoteWebDriver getBrowserDriver() {
        if (webDriver == null) {
            if (!webDriverName.equals(DRIVER_FIREFOX) && webDriverName == null) {
                System.err.println("Browser driver property is not set");
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
                            break;
                        case DRIVER_FIREFOX:
                            webDriver = new FirefoxDriver();
                            break;
                        default:
                            System.err.println("Unknown browser driver:" + webDriverName);
                    }
                } catch (Exception e) {
                    System.err.println("Cannot get browser driver: " + e.getMessage());
                }
            }
        }
        return webDriver;
    }

    public Capabilities getBrowserDriverOptions() {
        return webDriverOptions;
    }

    public void screenshot() {
        //TODO: verify location of images files is ok
        TakesScreenshot shotTaker = (TakesScreenshot) webDriver;
        File shot = shotTaker.getScreenshotAs(OutputType.FILE);
        String nameSerial = String.valueOf(System.currentTimeMillis());
        try {
            FileUtils.copyFile(shot, new File(System.getProperty("user.dir") + LOG_PATH + "screenshot_" + nameSerial + ".png"));
        } catch (IOException ioe) {
            System.err.println("Error saving screenshot:" + nameSerial + ". " + ioe.getMessage());
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

    public void setBrowserDriver(RemoteWebDriver theDriver) {
        webDriver = theDriver;
    }

    /**
     * Verifies and sets the property for the web driver used with this instance of TestUtils.
     * @param driverName
     */
    public void setBrowserDriver(String driverName) {
        String foundBDPath = null;
        // look for windows env var
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
            String browseDriverPath = DRIVER_PATH_CURRENT + "\\" + driverName;
            File f = new File(System.getProperty("user.dir") + browseDriverPath);
            if (f != null && f.exists()) {
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
                    System.setProperty("webdriver.?????.driver", foundBDPath);
                    break;
                case DRIVER_FIREFOX:
                    //System.setProperty("webdriver.chrome.driver", foundBDPath);
                    break;
                default:
                    System.err.println("Unknown browser driver:" + driverName);
            }
            webDriver = null;
            webDriverName = driverName;
        }
    }

    public void setBrowserDriverOptions(Capabilities theOptions) {
        webDriverOptions = theOptions;
    }

    public boolean waitFor(String xPath) {
        return waitFor(xPath, WAITFOR_DEFAULT_TIMEOUT);
    }

    public boolean waitFor(String xPath, long timeOutSec) {
        return waitFor(xPath, timeOutSec, false);
    }

    public boolean waitFor(String xPath, long timeOutSec, boolean silent) {
        WebDriverWait waiter;
        if (timeOutSec == WAITFOR_DEFAULT_TIMEOUT) {
            if (webWaiterDefault == null) {
                webWaiterDefault = new WebDriverWait(webDriver, WAITFOR_DEFAULT_TIMEOUT);
            }
            waiter = webWaiterDefault;
        } else {
            waiter = new WebDriverWait(webDriver, timeOutSec);
        }
        long t1 = System.currentTimeMillis();
        try {
            waiter.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xPath)));
            if (!silent) System.out.println("Found element "+xPath+" after " + (System.currentTimeMillis()-t1) + "ms");
            return true;
        } catch (TimeoutException toe) {
            if (!silent) System.err.println("Timed out waiting for element "+xPath+" after " + (System.currentTimeMillis()-t1) + "ms");
            return false;
        }
    }

    public void waitForApp() {
        waitForApp(WAITFOR_DEFAULT_TIMEOUT);
    }

    public void waitForApp(long timeout) {
        System.out.println("================================================\n-------------- entered waitForApp()");
        long tBegin=System.currentTimeMillis();
        WebDriverWait webWaiter;
        String spinnerXPath = "//div[contains(@class, 'block-mask')]";
        if (waitFor(spinnerXPath, 1, true)) {
            System.out.println("found spinner");
            if (timeout == WAITFOR_DEFAULT_TIMEOUT) {
                webWaiter = webWaiterDefault;
            } else {
                webWaiter = new WebDriverWait(webDriver, timeout);
            }
            webWaiter.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(spinnerXPath)));
            System.out.println("Spinner wait time:"+ (System.currentTimeMillis()-tBegin));
        }
        System.out.println("--------------- exited waitForApp(), elapsed: " + (System.currentTimeMillis()-tBegin));
    }

    public static void main(String[] args) {

    }
}