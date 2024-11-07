package com.nn.testcase.homepage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Test {

    @org.testng.annotations.Test
   public void test(){
        ChromeOptions options = new ChromeOptions();
System.out.println("Hello world");
        options.addArguments("--headless");  // Run Chrome in headless mode
        options.addArguments("--no-sandbox");  // Required in CI environments
        options.addArguments("--disable-dev-shm-usage"); 
       WebDriver driver = new ChromeDriver(options);
       
   }
}
