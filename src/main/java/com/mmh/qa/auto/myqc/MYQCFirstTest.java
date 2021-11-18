package com.mmh.qa.auto.myqc;

import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

@Test()
public class MYQCFirstTest extends MYQCUtils {
    public MYQCFirstTest() {
        super();
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("window-position=-1000,0");
        setBrowserDriverOptions(options);  // provide options for the browser before its instantiated
        webDriver = getBrowserDriver();

    }
    public static void main (String[] args) {
        MYQCFirstTest test = new MYQCFirstTest();
        test.testInherited();
    }

    public void testInherited() {
        loginUser("autotest400", "Auto Test 400", "Kronites1", "samqc95rtm.mmhayes.com", "myqc", true);
        navMainMenu(MENU_ABOUT);
        navMainMenu(MENU_ORDERING);
        navMainMenu(MENU_HISTORY);
        navMainMenu(MENU_ACCOUNT);
        navMainMenu(MENU_FREEZE);
        navMainMenu(MENU_FUNDING);
        String[] u =getCurrentUser();
        System.out.println("Current user:" + u[0] +", " + u[1]);
        System.out.println("Client version:" + getVersionClient());
    }

    public void test_standalone() {
        MYQCUtils myqc = new MYQCUtils();
        myqc.loginUser("autotest400", "Auto Test 400", "Kronites1", "samqc95rtm.mmhayes.com", "myqc", true);
        myqc.navMainMenu(MYQCUtils.MENU_ABOUT);
        myqc.navMainMenu(MYQCUtils.MENU_ORDERING);
        myqc.navMainMenu(MYQCUtils.MENU_HISTORY);
        myqc.navMainMenu(MYQCUtils.MENU_ACCOUNT);
        myqc.navMainMenu(MYQCUtils.MENU_FREEZE);
        myqc.navMainMenu(MYQCUtils.MENU_FUNDING);

    }
}
