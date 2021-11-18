package com.mmh.qa.auto.myqc;

import com.mmh.qa.auto.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

public class MYQCUtils extends TestUtils {
    public static final long NAVIGATE_TIMEOUT = 30000;
    public static final int MENU_ORDERING = 0;
    public static final int MENU_REWARDS = 1;
    public static final int MENU_BALANCE = 2;
    public static final int MENU_HISTORY = 3;
    public static final int MENU_DEDUCTION = 4;
    public static final int MENU_FUNDING = 5;
    public static final int MENU_QUICKPAY = 6;
    public static final int MENU_FREEZE = 7;
    public static final int MENU_ACCOUNT = 8;
    public static final int MENU_ABOUT = 9;
    public static final int MENU_LOGOUT = 10;
    public final String[][] mainMenuItems = {
            // index, display name, id of link, id of main div
            {"0","Online Ordering", "nav-order"},
            {"1","Rewards", "nav-rewards"},
            {"2","Current Balance", "nav-balances"},
            {"3","Purchase History", "nav-purchase"},
            {"4","Deduction History", "nav-deductions"},
            {"5","Account Funding", "nav-accountFunding"},
            {"6","Quick Pay", "nav-quickPay"},
            {"7","Freeze Account", "nav-freeze"},
            {"8","Account Settings", "nav-spendinglimits"},
            {"9","About Quickcharge", "nav-about"},
            {"10","Log Out", "nav-logout"}
    };

    private String versionClient;  // cached values
    private String versionServer;
    private String userDisplayedName;
    private String userAccountNum;

    MYQCUtils() {
        super();
//        webDriver = getBrowserDriver();
    }

    MYQCUtils(String driver) {
        super(driver);
    }

    /**
     * Returns the user info as displayed on the home page
     * Author: LAW
     * Created: 11/18/21
     * @return Current user array: [0]displayed name, [1]account #
     */
    public String[] getCurrentUser() {
        long tBegin = System.currentTimeMillis();
        String[] retVal = null;

        if (userDisplayedName != null && userAccountNum != null) {
            return new String[] {userDisplayedName, userAccountNum};
        }
        if (navToHome()) {
            WebElement weUser = findElementBy(By.xpath("//div[@id='mainview']/*/div[@id='main']/div[@id='user-information']"));
            String user = weUser.findElement(By.xpath("./h3")).getText().trim();
            String acct = weUser.findElement(By.xpath("./h5")).getText().trim();
            acct = acct.substring(acct.indexOf(":") +1).trim();
            retVal = new String[] {user, acct};
        }
        return retVal;
    }
    /**
     * Returns the client version of the current MYQC site
     * Author: LAW
     * Created: 11/18/21
     * @return client version
     */
    public String getVersionClient() {
        if (versionClient == null) {
            String vers = getVersionString();
            versionClient = vers.substring(vers.indexOf("/") + 1).trim();
        }
        return versionClient;
    }

    /**
     * Returns the server version of the current MYQC site
     * Author: LAW
     * Created: 11/18/21
     * @return server version
     */
    public String getVersionServer() {
        if (versionServer == null) {
            String vers = getVersionString();
            versionServer = vers.substring(0, vers.indexOf("/")).trim();
        }
        return versionServer;
    }
    /**
     * Returns the version text as displayed on the MYQC About page
     * Author: LAW
     * Created: 11/18/21
     * @return version string
     */
    private String getVersionString() {
        long tBegin = System.currentTimeMillis();
        String retVal = "";

        if (navMainMenu(MENU_ABOUT)) {
            WebElement weVers = findElementBy(By.xpath("//div[@id='tour-page-0']/div[@class='tour-subtext app-version']"));
            if (weVers == null) {
                System.err.println("Cannot find version element");
                return retVal;
            } else {
                retVal = weVers.getText().trim();
            }
        }
        return retVal;
    }

    /**
     *
     * @return true upon success
     */
    public boolean navBack() {
        long tBegin = System.currentTimeMillis();
        boolean retVal = false;

        waitForApp();
        String urlBefore = webDriver.getCurrentUrl();
        WebElement weBackLink = findElementBy(By.xpath("//div[contains(@class,'active')]//a[contains(@class,'back-icon')]"));
        WebElement weXClose = findElementBy(By.xpath("//div[contains(@class,'active')]//div[contains(@class,'_close')]"));

        if (urlBefore.toLowerCase().endsWith("/#main")) {  // check if we're on the home page, can't go any further
            retVal = true;
        } else if (weXClose != null) {  // check for product floater window
            // must close floater windows first before being able to click back-arrows
            weXClose.click();
            waitForApp();
        } else if (weBackLink != null) {  // check for the back-arrow
            weBackLink.click();
            waitForApp();
        } else {
            System.out.println("The back-navigation link not found.");
        }

        // verify navigation
        if (!retVal && webDriver.getCurrentUrl().equals(urlBefore)) {
            System.out.println("Failed to complete the back-navigation action. Unchanged url:" + urlBefore);
            return retVal;
        }
        return true;
    }

    /**
     * Navigates to the specified main menu item.
     * Author: LAW
     * Created: 11/18/21
     * @param menuItem see MENU_ constants
     * @return true upon success
     */
    public boolean navMainMenu(int menuItem) {
        long tBegin = System.currentTimeMillis();
        final long TIMEOUT_TIME = System.currentTimeMillis() + NAVIGATE_TIMEOUT;
        boolean retVal = false;

        navToHome();  // goes home for you
        WebElement weItem = findElementBy(By.id(mainMenuItems[menuItem][2]));  // mainMenuItems[] is a static array containing info on items of the main menu
        if (weItem == null) {
            // nav item not found
            System.err.println("Navigate failed. Check perms. Menu item not found:" + mainMenuItems[menuItem][1]);
            return retVal;
        } else {
            if (!weItem.isDisplayed() || !weItem.isEnabled()) {
                System.err.println("Navigate failed. Menu item not visible or enabled.");
                return retVal;
            } else {
                weItem.click();  // click to navigate here
                waitForApp();
            }
        }
        // verify landing page. Use this header to match with expected page
        WebElement wePage = findElementBy(By.xpath("//div[contains(@class, 'view') and contains(@class, 'active')]//h1"));
        if (wePage == null) {
            System.err.println("Navigate failed. Cannot identify the page H1 header");
        } else {
            String pageH1 = wePage.getText();
            // exceptions(b/c the html is inconsistent)
            if (pageH1.equals("Ordering for Today")) pageH1 = "Online Ordering";
            if (pageH1.equals("About")) pageH1 = "About Quickcharge";
            if (pageH1.trim().compareToIgnoreCase(mainMenuItems[menuItem][1].trim()) == 0) {
                retVal = true;
            } else {
                System.err.println(String.format("Navigate failed. Page not verified. %s != %s", pageH1, mainMenuItems[menuItem][1]));
                screenshot();
            }
        }
        return retVal;
    }

    /**
     *
     * @return true upon success
     */
    public boolean navToHome() {
        long tBegin = System.currentTimeMillis();
        final long TIMEOUT_TIME = System.currentTimeMillis() + NAVIGATE_TIMEOUT;
        boolean retVal = false;

        String homeLinkXPath = "//div[contains(@class, 'view') and contains(@class, 'active')]//a[contains(@class, 'home-link')]";
        WebElement weHomeLink = findElementBy(By.xpath(homeLinkXPath));
        WebElement weMainList = findElementBy(By.id("item-list"));
        if (weHomeLink == null && weMainList == null) {
            System.err.println("Cannot find main menu component(s) (home link,list)");
            return retVal;
        }
        while (!weMainList.isDisplayed() && System.currentTimeMillis() < TIMEOUT_TIME) {
            if (!navBack()) {
                weHomeLink = findElementBy(By.xpath(homeLinkXPath));
                if (weHomeLink != null && weHomeLink.isDisplayed()) {
                    weHomeLink.click();
                }
            }
        }
/*        while (navBack() && System.currentTimeMillis() < TIMEOUT_TIME) {
            //
        }
        weHomeLink = findElementBy(By.xpath(homeLinkXPath));
        if (weHomeLink != null && weHomeLink.isDisplayed()) {
            weHomeLink.click();
        }
*/
        // verify we're at home
        if (weMainList.isDisplayed()) {
            retVal = true;
        } else if (System.currentTimeMillis() > TIMEOUT_TIME) {
            System.err.println("Timed out navigating to home");
        }
        return retVal;
    }

    /**
     * Logs into MYQC using the given URL and credentials. If a different user is already logged in, that user will be logged out.
     * Author: LAW
     * Created: 11/16/21
     * @param userName     User to login as
     * @param userDispName User as shown on-screen (optional)
     * @param userPW       User password
     * @param domain       The base site domain (no 'http' prefixes)
     * @param qcwar        Name of the WAR to use
     * @param stayLogged   Select the "Keep me logged in" option
     * @return true on success
     */
    public boolean loginUser(String userName, String userDispName, String userPW, String domain, String qcwar, boolean stayLogged) {
        return loginUser(userName, userDispName, userPW, domain, qcwar, stayLogged, false);
    }

     /**
     * Logs into MYQC using the given URL and credentials. If a different user is already logged in, that user will be logged out.
     * Author: LAW
     * Created: 11/16/21
     * @param userName     User to login as
     * @param userDispName User as shown on-screen (optional)
     * @param userPW       User password
     * @param domain       The base site domain (no 'http' prefixes)
     * @param qcwar        Name of the WAR to use
     * @param stayLogged   Select the "Keep me logged in" option
     * @param skipLoginCheck Skips verification after login action (for negative testing)
     * @return true on success
     */
    public boolean loginUser(String userName, String userDispName, String userPW, String domain, String qcwar, boolean stayLogged, boolean skipLoginCheck) {
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
        if (waitFor("//div[@id='logoutFormContainer']")) {
            System.out.println("TIMING: Found initial logout page:" + (System.currentTimeMillis()-t1));
        } else {
            System.err.println("Timed out waiting for initial logout-form ");
            screenshot();
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
                    screenshot();
                } else {
                    weStayLogged.click();
                }
            }
            weLoginBtn.click();
            waitForApp();
        } else {
            System.err.println("Login panel not found");
            screenshot();
            return retVal;
        }

        // override login checks (for negative test cases)
        // this allows the caller to treat this method itself as a positive/negative test case
        if (skipLoginCheck) {
            return true;
        }

        // check for failed login
        if (webDriver.findElementByXPath("//div[@id='loginErrorContainer']").isDisplayed()) {
            WebElement weErrMsg = webDriver.findElementByXPath("//div[@id='loginErrorContainer']//div[@class='general-error-msg-content']");
            System.err.println("Login error detected:" + weErrMsg.getText());
            screenshot();
            return retVal;
        }

        // verify main menu displayed
        WebElement weLogo = webDriver.findElementByXPath("//div[@class='logo-container']");
        WebElement weNavList = webDriver.findElementByXPath("//ul[@id='item-list']");
        WebElement weLogout = weNavList.findElement(By.xpath("//a[@id='nav-logout']"));  // get correct syntax for relative xpath
        if (weLogo == null || weNavList == null || weLogout == null) {
            System.err.println("Not all login-page elements verified(logo, navlist, logout)");
            screenshot();
            return retVal;
        } else {
            System.out.println("Login verified:");
        }

        // verify current user displayed
        if (userDispName != null) {
            WebElement weDispName = findElementBy(By.xpath("//h3[@class='user-information-title']"));
            if (weDispName == null) {
                screenshot();
                System.err.println("Cannot find logged in user displayed-name");
                return retVal;
            } else {
                if (!weDispName.getText().equals(userDispName)) {
                    System.err.println(String.format("Unexpected displayed user found after login. Expected user: '%s'. Displayed user: '%s'", userDispName, weDispName.getText()));
                    return retVal;
                }
            }
        }
        userDisplayedName = userDispName;  // populate cached version

        System.out.println("--------------- exited loginUser(), elapsed: " + (System.currentTimeMillis()-tBegin));
        return true;
    }

    public static void main(String[] args) {

    }

}
