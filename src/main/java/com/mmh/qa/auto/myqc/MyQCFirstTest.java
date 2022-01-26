package com.mmh.qa.auto.myqc;

import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

@Test()
public class MyQCFirstTest extends MyQC {
    public MyQCFirstTest() {
        super();
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("window-position=-1000,0");
        setBrowserDriverOptions(options);  // provide options for the browser before its instantiated
        webDriver = getBrowserDriver();

    }
    public static void main (String[] args) {
        MyQCFirstTest test = new MyQCFirstTest();
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
        MyQC myqc = new MyQC();
        myqc.loginUser("autotest400", "Auto Test 400", "Kronites1", "samqc95rtm.mmhayes.com", "myqc", true);
        myqc.navMainMenu(MyQC.MENU_ABOUT);
        myqc.navMainMenu(MyQC.MENU_ORDERING);
        myqc.navMainMenu(MyQC.MENU_HISTORY);
        myqc.navMainMenu(MyQC.MENU_ACCOUNT);
        myqc.navMainMenu(MyQC.MENU_FREEZE);
        myqc.navMainMenu(MyQC.MENU_FUNDING);

    }
}
