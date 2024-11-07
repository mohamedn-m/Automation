package com.nn.testcase.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Test {

    @org.testng.annotations.Test
   public void test(){

        WebDriver driver = new ChromeDriver();
       System.out.println("Hello world");
   }
}
