package tests;


import com.mmh.qa.auto.myqc.pages.myqcsetup.SetupMethods;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.mmh.qa.auto.myqc.pages.myqcsetup.MYQCBaseClasses.homePage;
import static com.mmh.qa.auto.myqc.pages.myqcsetup.MYQCBaseClasses.loginPage;


public class MYQC2LoginTest extends SetupMethods {


    @Test(priority = 1)
    // TC002- Testing Logging in to MYQC
    public static void loginTest() throws IOException, InterruptedException {

        //Create an object of the File class to open xlsx file
        File file = new File("C:\\Users\\fhasan\\Desktop\\idPass.xlsx");
        // Create an object of FileInputStream class to read Excel file
        FileInputStream inputStream = new FileInputStream(file);
        //Create a workbook instance that refers to .xlsx file
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = wb.getSheet("Sheet1");
        for (int i = 1; i < 2; i++) {
            //Create a row object to retrieve row at index 1
            XSSFRow row1 = sheet.getRow(i);
            //Create a cell object to retreive cell at index 1
            XSSFCell cell = row1.getCell(0);
            XSSFCell cell1 = row1.getCell(1);
            //Get the id and pass in variables
            String username = cell.getStringCellValue();
            String password = cell1.getStringCellValue();
            loginPage().clickOnUsername();
            loginPage().sendUsername(username);
            loginPage().clickOnPassword();
            loginPage().sendPassword(password);
            loginPage().clickOnLogin();
            String expected = "Online Ordering";
            String actual = homePage().getTextOnlineOrderingButton();
            Assert.assertEquals(actual, expected);
            prln("Homepage load is complete to start testing\n=======");
            Thread.sleep(2000);

            //https://www.toolsqa.com/selenium-webdriver/excel-in-selenium/
        }
    }
}





