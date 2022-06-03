package tests;

import com.mmh.qa.auto.myqc.pages.myqcsetup.SetupMethods;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.mmh.qa.auto.myqc.pages.myqcsetup.MYQCBaseClasses.loginPage;
import static com.mmh.qa.auto.myqc.pages.myqcsetup.MYQCBaseClasses.preLoginPage;


public class MYQC1PreLoginTest extends SetupMethods {


    @Test()
    public static void preLoginTest() throws IOException, InterruptedException {
        driver.get("https://mmhcustfour.mmhcloud.com/myqc/#main");
        preLoginPage().clickOnMYQCLink();
        String actual = loginPage().getPageTitle();
        String expected = "Login";
        Assert.assertEquals(actual, expected);
        Assert.assertTrue(loginPage().presenseOfForgotPassLink());

    }


}



