package com.mmh.qa.auto.myqc;

import com.mmh.qa.auto.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

public class MYQCUtils extends TestUtils {
    private RemoteWebDriver webDriver;

    MYQCUtils() {
        super();
        webDriver = super.getBrowserDriver();
    }

    MYQCUtils(String driver) {
        super(driver);
    }

    public boolean loginUser(String userName, String userDispName, String userPW, String domain, String qcwar, boolean stayLogged) {
        System.out.println("================================================\n-------------- entered loginUser()");
        long tBegin = System.currentTimeMillis();
        boolean retVal = false;

        // ensure prefix "https://"
        if (domain.startsWith("http://")) {
            domain.replace("http://" ,"https://");
        }
        if (!domain.startsWith("http")) {
            domain = "https://" + domain;
        }
        String targetURL = domain + "/" + qcwar;
        // open new browser window here
        webDriver.get(targetURL);  // never starts from previous session?

        long t1=System.currentTimeMillis();
        if (super.waitFor("//div[@id='logoutFormContainer']")) {
            System.out.println("TIMING: Found initial logout page:" + (System.currentTimeMillis()-t1));
        } else {
            System.err.println("Timed out waiting for initial logout-form ");
            //TODO screenshot
            return retVal;
        }

        // check for QC login or SSO
        WebElement weQCLogin = webDriver.findElementByXPath("//a[@id='logoutMyQCLink']");
        if (weQCLogin == null) {
            // SSO login here
        } else {
            weQCLogin.click();
        }

        // do login here
        if (webDriver.findElementByXPath("//div[@id='loginFormContainer']") != null) {
            WebElement weUName =  webDriver.findElementByXPath("//input[@id = 'loginName']");
            WebElement weUPW =  webDriver.findElementByXPath("//input[@id = 'loginPassword']");
            WebElement weLoginBtn = webDriver.findElementByXPath("//button[@id = 'loginButton']");
            if (weUName == null || weUPW == null || weLoginBtn == null) {
                System.err.println(String.format("Login component found: name:%b. pw:%b. button:%b.", weUName, weUPW, weLoginBtn));
                return false;
            }
            weUName.sendKeys(userName);
            weUPW.sendKeys(userPW);
            if (stayLogged) {
                WebElement weStayLogged = webDriver.findElementByXPath("//img[@class='keepLogged-info-icon']");
                if (weStayLogged == null) {
                    System.err.println("Control not found:\"stay logged in\"");
                    //TODO screenshot
                } else {
                    weStayLogged.click();
                }
            }
            weLoginBtn.click();
            super.waitForApp();

                    // div id=popup_container
        } else {
            System.err.println("Login panel not found");
            //TODO screenshot
            return retVal;
        }

        // override login checks (for negative test cases)
        //TODO

        // check for failed login
        if (webDriver.findElementByXPath("//div[@id='loginErrorContainer']").isDisplayed()) {
            WebElement weErrMsg = webDriver.findElementByXPath("//div[@id='loginErrorContainer']//div[@class='general-error-msg-content']");
            System.err.println("Login error detected:" + weErrMsg.getText());
            //TODO screenshot
            return retVal;
        }

        // verify main menu displayed
        WebElement weLogo = webDriver.findElementByXPath("//div[@class='logo-container']");
        WebElement weNavList = webDriver.findElementByXPath("//ul[@id='item-list']");
        WebElement weLogout = weNavList.findElement(By.xpath("//a[@id='nav-logout']"));  // get correct syntax for relative xpath
        if (weLogo == null || weNavList == null || weLogout == null) {
            //TODO screenshot
            return retVal;
        } else {
            System.out.println("Login verified:");
        }

        // verify current user displayed
        WebElement weDispName = webDriver.findElementByXPath("//h3[@class='user-information-title']");
        if (weDispName == null) {
            //TODO screenshot
            System.err.println("Cannot find logged in user displayed-name");
            return retVal;
        } else {
            if (!weDispName.getText().equals(userDispName)) {
                System.err.println(String.format("Unexpected displayed user found after login. Expected user: '%s'. Displayed user: '%s'", userDispName, weDispName.getText()));
                return retVal;
            }
        }

        System.out.println("--------------- exited loginUser(), elapsed: " + (System.currentTimeMillis()-tBegin));
        return true;
    }

    public static void main(String[] args) {

    }

}
