package com.mmh.qa.auto.myqc;

import org.testng.annotations.Test;

@Test()
public class FirstMYQCTest {
    public static void main (String[] args) {

        MYQCUtils myqc = new MYQCUtils();
        myqc.loginUser("autotest400", "Auto Test 400", "Kronites1", "samqc95rtm.mmhayes.com", "myqc", true);

    }

}
