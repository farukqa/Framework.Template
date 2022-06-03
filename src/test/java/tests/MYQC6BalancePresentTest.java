package tests;


import com.mmh.qa.auto.myqc.pages.myqcsetup.SetupMethods;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.mmh.qa.auto.myqc.pages.myqcsetup.MYQCBaseClasses.currentBalancePage;
import static com.mmh.qa.auto.myqc.pages.myqcsetup.MYQCBaseClasses.homePage;

public class MYQC6BalancePresentTest extends SetupMethods {

    @Test(priority = 4)
    public static void balancePresentTest() throws IOException, InterruptedException {

            try {
                homePage().clickOnCurrentBalancePage();
                Assert.assertTrue(currentBalancePage().isAccountBalancePresent());
                currentBalancePage().clickOnHome();
            }
            catch(Exception err) {
            returnsToHome();
        }


    }
    @Test(priority = 5)
    public static void donationButtonPresentTest() throws IOException, InterruptedException {

        try {
            homePage().clickOnCurrentBalancePage();
            Assert.assertTrue(currentBalancePage().isDonationButtonPresent());
            currentBalancePage().clickOnHome();
        }
        catch(Exception err) {
            returnsToHome();
        }


    }




}
