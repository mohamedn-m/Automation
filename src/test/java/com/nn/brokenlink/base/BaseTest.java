package com.nn.brokenlink.base;

import com.nn.drivers.DriverManager;
import com.nn.utilities.DriverActions;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.*;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.nn.constants.Constants.HEADLESS;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class BaseTest extends SkipUrls {

    private Set<String> checked200UrlS = new HashSet<>();
    private Set<String> checkedURLs = new HashSet<>();
    private Set<String> checkedimageURLs = new HashSet<>();
    private AtomicInteger count = new AtomicInteger(0);


    private static final Lock lock = new ReentrantLock();
    public String linkchecksheetname ="BROKENLINK";
    public String imagechecksheetname ="BROKENIMAGE";
    public String metachecksheetname ="META-CHECK";
    public String H1tagchecksheetname ="H1TAG-CHECK";
    public String imagealtchecksheetname ="IMAGEALT-CHECK";
    public String canonicalTagssheetname ="canonical-CHECK_DE";

    private static File xl = new File(System.getProperty("user.dir") + "/src/test/resources/HomePage.xlsx");

    public static void quitRefresh(){
        System.out.println("Driver_Going_to_Refres");
        DriverManager.getDriver().quit();
        WebDriver driver = setupBrowser("chrome");
        DriverManager.setDriver(driver);
        driver.manage().deleteAllCookies();
        System.out.println("Driver_Refreshed");

    }
    @BeforeTest(alwaysRun = true)
    @Parameters({"BROWSER"})
    public void createDriver(@Optional("chrome") String browser) {
        WebDriver driver = setupBrowser(browser);
        DriverManager.setDriver(driver);
    }

    @AfterTest(alwaysRun = true)
    public void quitDriver() {
        if (DriverManager.getDriver() != null) {
            DriverManager.quit();
            System.out.println(count);
        }

    }


    public static WebDriver setupBrowser(String browserName) {
        WebDriver driver;
        switch (browserName.trim().toLowerCase()) {
            case "chrome":
                driver = chromeDriver();
                break;
            case "firefox":
                driver = firefoxDriver();
                break;
            case "edge":
                driver = edgeDriver();
                break;
            default:
                System.out.println("Browser " + browserName + " name is invalid. Launching chrome as default");
                driver = chromeDriver();
        }
        return driver;
    }

    private static WebDriver chromeDriver() {
        WebDriver driver;
        System.out.println("Launching Chrome Driver...");
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        ChromeOptions options = new ChromeOptions();
        if (HEADLESS)
            options.addArguments("--headless");     //options.addArguments("--headless=new");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        driver = new ChromeDriver(options);
        if (HEADLESS)
            driver.manage().window().setSize(new Dimension(1920, 1080));
        else
            driver.manage().window().maximize();
        return driver;
    }


    private static WebDriver firefoxDriver() {
        WebDriver driver;
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        System.out.println("Launching FireFox Driver...");
        FirefoxOptions options = new FirefoxOptions();
        if (HEADLESS)
            options.addArguments("--headless");
        driver = new FirefoxDriver(options);
        if (HEADLESS)
            driver.manage().window().setSize(new Dimension(1920, 1080));
        else
            driver.manage().window().maximize();
        return driver;
    }

    private static WebDriver edgeDriver() {
        WebDriver driver;
        System.out.println("Launching Edge Driver...");
        EdgeOptions options = new EdgeOptions();
        driver = new EdgeDriver(options);
        driver.manage().window().maximize();
        return driver;
    }


    public void checkAllLinks() throws IOException, GeneralSecurityException {
        DriverManager.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
        DriverActions.waitForAllElementLocated(By.tagName("a"));

        List<WebElement> allLinks = DriverActions.getElements(By.tagName("a"));
        for (WebElement link : allLinks) {
            String href = link.getAttribute("href");
            if (href != null && !href.isEmpty() && href.contains("novalnet")) {
                verifyLink( "N/A", href,linkchecksheetname ,"Broken_Link");
            }
        }

    }

    public void checkSublinks() throws IOException, GeneralSecurityException {
        List<String> novalnetLinks = getAllNovalnetLinks();

        for (String url : novalnetLinks) {
            DriverActions.openURL(url);
            DriverActions.waitForAllElementLocated(By.tagName("a"));
            List<WebElement> innerLinks = DriverActions.getElements(By.tagName("a"));
            DriverActions.waitForAllElementLocated(By.tagName("img"));
            List<WebElement> imgTags = DriverActions.getElements(By.tagName("img"));
            for (WebElement innerLink : innerLinks) {
                String subUrl = innerLink.getAttribute("href");
                if (subUrl != null && !subUrl.isEmpty()
                        && (subUrl.startsWith("https://") || subUrl.startsWith("http://"))) {
                    verifyLink(url, subUrl, linkchecksheetname,"Broken_Link");
                }

            }
            for (WebElement value : imgTags) {
                String imageurl = value.getAttribute("src");
                if (imageurl != null && (imageurl.contains("https") || imageurl.contains("http"))) {
                    verifyLink(url, imageurl, imagechecksheetname,"Broken_Image");

                }
            }

        }
    }


    private List<String> getAllNovalnetLinks() {
        DriverManager.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
        DriverActions.waitForAllElementLocated(By.tagName("a"));
        List<WebElement> allLinks = DriverActions.getElements(By.tagName("a"));
        List<String> novalnetLinks = new ArrayList<>();

        for (WebElement link : allLinks) {
            String href = link.getAttribute("href");
            if (href != null && href.contains("novalnet")) {
                novalnetLinks.add(href);
            }
        }

        return novalnetLinks;
    }

    private void verifyLink(String sourceUrl, String url ,String sheetname ,String purpose) throws IOException, GeneralSecurityException {

        boolean result =false;
        for(String successURs :checked200UrlS){
            result = successURs.equals(url);
            if (result==true)
                break;
        }
        if(result!=true){
            int statusCode = 0;
            String statusMessage = null;
            int currentCount = count.incrementAndGet();

            try {
                RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30 * 1000) // 30 seconds connect
                        // timeout
                        .setSocketTimeout(30 * 1000) // 30 seconds socket timeout
                        .build();

                HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
                HttpGet request = new HttpGet(url);
                HttpResponse response = httpClient.execute(request);
                statusCode = response.getStatusLine().getStatusCode();
                StatusLine statusLine = response.getStatusLine();
                statusMessage = statusLine.getReasonPhrase();

                if (statusCode == 200) {
                    System.out.println(currentCount + ": " + url + ": " + "Link is valid(HTTP response code: " + statusCode + ")");
                    if(checked200UrlS.add(url)){
                        writeDataToSheet(sheetname, new ArrayList<Object>(Arrays.asList(url, statusCode, statusMessage, sourceUrl,purpose)), xl);
                    }

                } else {
                    writeDataToSheet(sheetname, new ArrayList<Object>(Arrays.asList(url, statusCode, statusMessage, sourceUrl,purpose)), xl);
                    System.err.println(currentCount + ": " + url + ": " + "Link is broken (HTTP response code: "
                            + statusCode + ")");
                }
            } catch (Exception e) {
                if (statusCode == 0) {
                    writeDataToSheet(sheetname, new ArrayList<Object>(Arrays.asList(url, statusCode, "null", sourceUrl,purpose)), xl);
                    System.err.println(currentCount + ": " + url + ": " + "Exception occurred: " + statusMessage);
                } else {
                    writeDataToSheet(sheetname, new ArrayList<Object>(Arrays.asList(url, statusCode, statusMessage, sourceUrl,purpose)), xl);
                    System.err.println(currentCount + ": " + url + ": " + "Exception occurred: " + statusMessage);
                }
            }
        }

        }


    public void metaDataCheck() throws IOException, GeneralSecurityException {
        List<String> urls = getAllNovalnetLinks();
        for(String url:urls) {
            DriverActions.openURL(url);
            String value = null;
            List<WebElement> metaTags = DriverActions.getElements(By.tagName("meta"));
            WebElement lastIndexTag =metaTags.get(metaTags.size() - 1);
            String lastIndexTagValue=lastIndexTag.getAttribute("name").replaceAll("\\s", "");
            for (WebElement metTag : metaTags) {
                value = metTag.getAttribute("name").replaceAll("\\s", "");
                if (value.equalsIgnoreCase("description")) {
                    String xpath = value.equals("description") ? "//meta[@name='description']" : "//meta[@name='Description']";
                    WebElement descriptionMetaTag = DriverActions.getElement(By.xpath(xpath));
                    String description = descriptionMetaTag.getAttribute("content");
                    String lengthCondition = (description.length()<=155) ? "Yes" : "No";
                    writeDataToSheet(metachecksheetname, new ArrayList<Object>(Arrays.asList(url, "YES", description, value, description.length(), lengthCondition)), xl);
                    break;

                } else if (value.equals(lastIndexTagValue)) {
                    writeDataToSheet(metachecksheetname, new ArrayList<Object>(Arrays.asList(url, "NO", "N/A", "N/A", "N/A")), xl);
                }
            }
        }
    }

    public void verifyH1Tags() throws IOException, GeneralSecurityException {
        List<String> novalnetLinks = getAllNovalnetLinks();
        List<List<Object>> dataToWrite = new ArrayList<>();

        for (String url : novalnetLinks) {
            DriverActions.openURL(url);
            if (url != null && !url.isEmpty() && url.contains("novalnet") && checkedURLs.add(url)) {
               DriverActions.waitForPageLoad();
               List<WebElement> h1Tags = DriverActions.getElements(By.xpath("//h1"));
                String lengthCondition = (h1Tags.size()<=1) ? "Yes" : "No";
                writeDataToSheet(H1tagchecksheetname, new ArrayList<Object>(Arrays.asList(url, h1Tags.size(), lengthCondition)), xl);
            }
        }

    }

    public void verifyImageAltAttributes() throws GeneralSecurityException, IOException {
        String altValue=null;
      //  getSpreadsheetInstance();
        List<String> novalnetLinks = getAllNovalnetLinks();
        List<List<Object>> dataToWrite = new ArrayList<>();
        for(String novalnetURL:novalnetLinks) {
            DriverActions.openURL(novalnetURL);
            if (checkedURLs.add(novalnetURL)) {
                DriverActions.waitForAllElementLocated(By.xpath("//img"));
                List<WebElement> images = DriverActions.getElements(By.xpath("//img"));
                for (WebElement image : images) {
                    String imageURL = image.getAttribute("src");
                     altValue = image.getAttribute("alt");

                    if (urlsToSkip.contains(imageURL)) {
                        continue;
                    }
                     if(altValue.isEmpty()){
                         altValue="NIL";
                     }

                    writeDataToSheet(imagealtchecksheetname, new ArrayList<Object>(Arrays.asList(novalnetURL, "YES", imageURL, altValue)), xl);

                }
            }
        }
    }

    public void canonicalTags() throws IOException {
        List<String> novalLinks = getAllNovalnetLinks();
        for (String novalneturl : novalLinks) {
            int currentCount = count.incrementAndGet();
            DriverActions.openURL(novalneturl);
            DriverActions.waitForElementToBePresent(By.xpath("//link[@rel='canonical']"));
            List<WebElement> canonicalTags = DriverActions.getElements(By.xpath("//link[@rel='canonical']"));
            int a  =canonicalTags.size() ;
            if (a==1){
                String value = canonicalTags.get(0).getAttribute("href");
                String valuecondition = value.equals(novalneturl) ? "Self referrer tag = Yes" : "Self referrer tag = No";
                writeDataToSheet(canonicalTagssheetname, Arrays.asList(novalneturl, "Yes", valuecondition), xl);
                // System.out.println("updated");
            }
            else{
                String valuecondition = (a==0) ? "This page does not have a canonical tag " : "This page have more than 1 canonical tag";
                writeDataToSheet(canonicalTagssheetname, Arrays.asList(novalneturl, "abnormal", valuecondition), xl);
            }
            if (currentCount > 0 && (currentCount % 100 == 0)) {
                quitRefresh();
            }

        }


    }


    public void writeDataToSheet(String sheetName, List<Object> data, File filePath) throws IOException {
        lock.lock();  // Lock the block to prevent concurrent writes
        try {
            // Load the existing workbook
            FileInputStream fileInputStream = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(fileInputStream);

            // Get the sheet, or create it if it doesn't exist
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
            }

            // Get the next available row number
            int nextRowNum = getNextRow(sheet);
            Row row = sheet.createRow(nextRowNum);

            // Write the data into the cells
            int cellNum = 0;
            for (Object value : data) {
                Cell cell = row.createCell(cellNum++);
                // Check the type of value before setting the cell value
                if (value instanceof String) {
                    cell.setCellValue((String) value);
                } else if (value instanceof Integer) {
                    cell.setCellValue((Integer) value);
                } else if (value instanceof Double) {
                    cell.setCellValue((Double) value);
                } else {
                    cell.setCellValue(value.toString());  // Default to string conversion
                }
            }

            // Close the input stream
            fileInputStream.close();

            // Write the changes back to the file
            FileOutputStream outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);

            // Close the workbook and output stream
            workbook.close();
            outputStream.close();
        } finally {
            lock.unlock();  // Ensure the lock is released even if an exception occurs
        }
    }

    // Helper method to get the next available row number
    private int getNextRow(Sheet sheet) {
        // Iterate over each row to find the first completely empty row
        for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null || isRowEmpty(row)) {
                return rowNum;
            }
        }
        return sheet.getPhysicalNumberOfRows(); // If all rows are filled, return the next physical row number
    }

    // Helper method to check if a row is empty
    private boolean isRowEmpty(Row row) {
        for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

}




    /* public void metaDataCheck() throws IOException, GeneralSecurityException {
        getSpreadsheetInstance();
        List<String> urls = getAllNovalnetLinks();

        for(String url:urls) {
            DriverActions.openURL(url);
            boolean flag=false;
            String value = null;
            List<WebElement> metaTags = DriverActions.getElements(By.tagName("meta"));
            for (WebElement metTag : metaTags) {
                value = metTag.getAttribute("name").replaceAll("\\s", "");
                if (value.equals("description")) {
                    flag=true;
                }
                if (value.equals("Description") && flag==true) {
                    writeDataGoogleSheets("META_DATA_EN", new ArrayList<Object>(Arrays.asList(url)), existingSpreadSheetID);
                    break;
                }

            }

        }

    }*/


