package tests;

import com.mmh.qa.auto.myqc.pages.myqcsetup.SetupMethods;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.mmh.qa.auto.myqc.pages.myqcsetup.MYQCBaseClasses.*;

public class MYQC3PointDonationTest extends SetupMethods {
    static int availablePoints;

    @Test(priority = 2)
    public static void pointDonationButtonTest() throws IOException, InterruptedException {
        try {
            homePage().clickOnRewardsPage();
            availablePoints = Integer.parseInt((rewardsPage().pointBalanceCheck()));
            //checking if the point balance is zero

            if (availablePoints == 0) {
                // the donation button should not be present if the balance is zero
                Assert.assertFalse(rewardsPage().presenseofDonationButton());
                rewardsPage().clickOnHome();
            } else {

                Assert.assertTrue(rewardsPage().presenseofDonationButton());
                //returning to the home page
                rewardsPage().clickOnHome();
            }
        } catch (Exception e) {
            // if for any reason the test case fails, user will be returned to the home page
            returnsToHome();
        }

    }

    @Test(priority = 3)
    public static void pointDonationTest() throws IOException, InterruptedException {
        //checking if the point balance is zero
        try {
            homePage().clickOnRewardsPage();
            availablePoints = Integer.parseInt((rewardsPage().pointBalanceCheck()));

            if (availablePoints == 0) {

                Assert.assertFalse(rewardsPage().presenseofDonationButton());
                rewardsPage().clickOnHome();


            }
            else{

                    rewardsPage().clickOnDonateButton();
                    Thread.sleep(1000);
                    pointDonationPage().clickOnDropDown();
                    Thread.sleep(1000);
                    pointDonationPage().charitySelection();
                    pointDonationPage().clickOnSubmission();
                    pointDonationPage().clickOnDonationConfirmation();
                    Thread.sleep(1000);
                    pointDonationPage().clickOnSuccessMsg();
                    //checking point balance after making a donation
                    int afterDonationPoints = Integer.parseInt((rewardsPage().pointBalanceCheck()));
                    Assert.assertEquals(afterDonationPoints, 0);
                    rewardsPage().clickOnHome();
                }



        } catch (Exception err) {
            returnsToHome();

        }
    }
}






