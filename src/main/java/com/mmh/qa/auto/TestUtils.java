package com.mmh.qa.auto;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;

public class TestUtils {
    public static final String ENV_BROWSER_DRIVER = "BROWSER_DRIVER";
    public static final String DRIVER_PATH_CURRENT = "\\bin\\webdrivers";
    public static final String DRIVER_CHROME = "chromedriver.exe";
    public static final String DRIVER_EDGE = "msedgedriver.exe";
    public static final String DRIVER_FIREFOX = "geckodriver.exe";
    public static final String DRIVER_DEFAULT = DRIVER_CHROME;
    public static final String LOG_PATH = "\\";
    public static final int WAITFOR_DEFAULT_TIMEOUT = 30;

    private String webDriverName;
    private RemoteWebDriver webDriver;
    private WebDriverWait webWaiterDefault;  // predefined with 30s timeout
    private WebDriverWait webWaiter;

    public TestUtils() {
        setBrowserDriver(DRIVER_DEFAULT);
    }

    public TestUtils(String driver) {
        setBrowserDriver(driver);
    }

    /**
     *
     * @return Browser driver of type RemoteWebDriver
     */
    public RemoteWebDriver getBrowserDriver() {
        if (webDriver == null) {
            if (!webDriverName.equals(DRIVER_FIREFOX) && webDriverName == null) {
                System.err.println("Browser driver property is not set");
            } else {
                try {
                    switch (webDriverName) {
                        case DRIVER_CHROME:
                            webDriver = new ChromeDriver();
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
        File f = new File(System.getenv(ENV_BROWSER_DRIVER));
        if (f.exists()) {
            foundBDPath = System.getenv(ENV_BROWSER_DRIVER);
            System.out.println("found driver from Win env:" + ENV_BROWSER_DRIVER);
            System.out.println("found driver file in dir:" + f.getPath());
        }

        if (foundBDPath == null) {
            // look in the current dir
            String browseDriverPath = DRIVER_PATH_CURRENT + "\\" + driverName;
            f = new File(System.getProperty("user.dir") + browseDriverPath);
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

    public boolean waitFor(String xPath) {

        return waitFor(xPath, WAITFOR_DEFAULT_TIMEOUT);
    }

    public boolean waitFor(String xPath, long timeOut) {

        WebDriverWait waiter;
        if (timeOut == WAITFOR_DEFAULT_TIMEOUT) {
            if (webWaiterDefault == null) {
                webWaiterDefault = new WebDriverWait(webDriver, WAITFOR_DEFAULT_TIMEOUT);
            }
            waiter = webWaiterDefault;
        } else {
            waiter = new WebDriverWait(webDriver, timeOut);
        }
        long t1 = System.currentTimeMillis();
        try {
            waiter.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xPath)));
            System.out.println("Found element "+xPath+" after " + (System.currentTimeMillis()-t1) + "ms");
            return true;
        } catch (TimeoutException toe) {
            System.err.println("Timed out waiting for element "+xPath+" after " + (System.currentTimeMillis()-t1) + "ms");
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
        if (waitFor(spinnerXPath, 3)) {
            System.out.println("found spinner");
            if (timeout == WAITFOR_DEFAULT_TIMEOUT) {
                webWaiter = webWaiterDefault;
            } else {
                webWaiter = new WebDriverWait(webDriver, timeout);
            }
            webWaiter.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(spinnerXPath)));
            System.out.println("Spinner wait time:"+ (System.currentTimeMillis()-tBegin));
        } else {
            //TODO screenshot
            System.err.println(String.format("Timed out(%ssec) waiting for spinner(%s)", Long.valueOf(System.currentTimeMillis()-tBegin), spinnerXPath));
        }
        System.out.println("--------------- exited waitForApp(), elapsed: " + (System.currentTimeMillis()-tBegin));
    }

    public static void main(String[] args) {

    }
}