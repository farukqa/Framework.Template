package com.mmh.qa.auto.myqc;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;
import java.util.concurrent.TimeUnit;

@Test()
public class FirstMYQCTest {
    public static void main (String[] args) {

        ChromeDriver chromeDrive = new ChromeDriver();
        ChromeOptions co = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        chromeDrive.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        System.out.println("TEST MESSAGE");

        chromeDrive.get("https://samqc95rtm.mmhayes.com/myqc");

        WebElement button = chromeDrive.findElement(By.xpath("//a[@id='logoutMyQCLink']"));
        button.click();

        WebElement userName = chromeDrive.findElementById("loginName");
        userName.sendKeys("autotest400");
        WebElement userPW = chromeDrive.findElementById("loginPassword");
        userPW.sendKeys("Kronites1");

        WebElement logButton = chromeDrive.findElementById("loginButton");
        logButton.click();


    }

}
