package com.nn.brokenlink.base;

import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.nn.drivers.DriverManager;
import com.nn.reports.ExtentTestManager;
import com.nn.testcase.SheetsQuickstart;
import com.nn.utilities.DriverActions;
import io.qameta.allure.Allure;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
import com.nn.drivers.DriverManager;
import com.nn.reports.ExtentTestManager;
import com.nn.utilities.DriverActions;
import io.qameta.allure.Allure;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.*;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.nn.constants.Constants.HEADLESS;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
public class BaseTest {

    private Set<String> checked200UrlS = new HashSet<>();
    private Set<String> checkedURLs = new HashSet<>();
    private Set<String> checkedimageURLs = new HashSet<>();
    private AtomicInteger count = new AtomicInteger(0);
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens/path";
    static Sheets.Spreadsheets spreadsheets;
    public static final String existingSpreadSheetID = "1AF3YdABMEvVXzE7T6v8sVV4wGFpHee_tp3sufpENJLg";
    private static final List<String> SCOPES =
            Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";


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
                verifyLink( "N/A", href);
            }
        }

    }

    public void checkSublinks() throws IOException, GeneralSecurityException {
        getSpreadsheetInstance();
        List<String> novalnetLinks = getAllNovalnetLinks();

        for (String url : novalnetLinks) {
            DriverActions.openURL(url);
            DriverActions.waitForAllElementLocated(By.tagName("a"));
            List<WebElement> innerLinks = DriverActions.getElements(By.tagName("a"));
            for (WebElement innerLink : innerLinks) {
                String subUrl = innerLink.getAttribute("href");
                if (subUrl != null && !subUrl.isEmpty()
                        && (subUrl.startsWith("https://") || subUrl.startsWith("http://"))) {
                    verifyLink(url, subUrl);
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

    private void verifyLink(String sourceUrl, String url) throws IOException, GeneralSecurityException {
        boolean result =false;
        for(String successURs :checked200UrlS){
            result = successURs.equals(url);
            if (result==true)
                break;
        }
        if(result!=true){
            getSpreadsheetInstance();
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
                        writeDataGoogleSheets("BROKENLINKS_DE", new ArrayList<Object>(Arrays.asList(url, statusCode, statusMessage, sourceUrl)), existingSpreadSheetID);
                    }

                } else {
                    writeDataGoogleSheets("BROKENLINKS_DE", new ArrayList<Object>(Arrays.asList(url, statusCode, statusMessage, sourceUrl)), existingSpreadSheetID);
                    System.err.println(currentCount + ": " + url + ": " + "Link is broken (HTTP response code: "
                            + statusCode + ")");
                }
            } catch (Exception e) {
                if (statusCode == 0) {
                    writeDataGoogleSheets("BROKENLINKS_DE", new ArrayList<Object>(Arrays.asList(url, statusCode, "null", sourceUrl)), existingSpreadSheetID);
                    System.err.println(currentCount + ": " + url + ": " + "Exception occurred: " + statusMessage);
                } else {
                    writeDataGoogleSheets("BROKENLINKS_DE", new ArrayList<Object>(Arrays.asList(url, statusCode, statusMessage, sourceUrl)), existingSpreadSheetID);
                    System.err.println(currentCount + ": " + url + ": " + "Exception occurred: " + statusMessage);
                }
            }
        }

        }


    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public void addDataInGooGleSheet(String URL, int statusCode, String statusMessage, String soureURL) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "1wrkelAEm4KGDMHais-d_xHaoKssYIH9KpKq_Xe76Ouc";
        final String range = "Class Data!A2:E";

        Sheets service =
                new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        getSpreadsheetInstance();
        //  createNewSpreadSheet();
         createNewSheet(existingSpreadSheetID, "BROKENLINKS");
        writeDataGoogleSheets("BROKENLINKS_DE", new ArrayList<Object>(Arrays.asList(URL, statusCode, statusMessage, soureURL)), existingSpreadSheetID);
    }

    public static void createNewSheet(String existingSpreadSheetID, String newsheetTitle) throws GeneralSecurityException, IOException {

        //Create a new AddSheetRequest
        AddSheetRequest addShettRequest = new AddSheetRequest();
        SheetProperties sheetProperties = new SheetProperties();
        sheetProperties.setIndex(0);

        //add the sheetName to the sheetProperties
        addShettRequest.setProperties(sheetProperties);
        addShettRequest.setProperties(sheetProperties.setTitle(newsheetTitle));

        //create btachUpdateSpreadsheetRequest
        BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest();

        //create requestList and set it on the batchUpdateSpreadsheetRequest
        List<Request> requestsList = new ArrayList<Request>();
        batchUpdateSpreadsheetRequest.setRequests(requestsList);

        //create a new request with containing the addSheetRequest and add it to the requestList
        Request request = new Request();
        request.setAddSheet(addShettRequest);
        requestsList.add(request);

        //add the requestList to the batchUpdateSpreadSheetRequest
        batchUpdateSpreadsheetRequest.setRequests(requestsList);

        //call the sheets API to execute the batchUpdate
        spreadsheets.batchUpdate(existingSpreadSheetID, batchUpdateSpreadsheetRequest).execute();

    }

    public static void getSpreadsheetInstance() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        spreadsheets = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(), getCredentials(HTTP_TRANSPORT))
                .setApplicationName("Google Sheet Java Integrate").build().spreadsheets();
    }

    public static void writeDataGoogleSheets(String sheetName, List<Object> data, String existingSpreadSheetId) throws IOException {
        int nextROW = getRows(sheetName, existingSpreadSheetId) + 1;
        writeSheet(data, "!A" + nextROW, existingSpreadSheetId);

    }

    public static int getRows(String sheetName, String existingSpreadSheetID) throws IOException {
        List<List<Object>> values = spreadsheets.values().get(existingSpreadSheetID, sheetName)
                .execute().getValues();
        int numRows = values != null ? values.size() : 0;
        System.out.printf("%d rows retrived. in " + sheetName + "\n", numRows);
        return numRows;
    }

    public void addDataInGooGleSheet() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "1wrkelAEm4KGDMHais-d_xHaoKssYIH9KpKq_Xe76Ouc";
        final String range = "Class Data!A2:E";

        Sheets service =
                new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        getSpreadsheetInstance();
        // createNewSpreadSheet();
         // createNewSheet(existingSpreadSheetID, "BROKENLINKS_DE");
        writeDataGoogleSheets("BROKENLINKS_DE", new ArrayList<Object>(Arrays.asList("")), existingSpreadSheetID);
    }

    public static void writeSheet(List<Object> inputData, String sheetAndRange, String existingSpreadSheetID) throws IOException {
        //USER_ENTERED
        List<List<Object>> values = Arrays.asList(inputData);
        ValueRange body = new ValueRange().setValues(values);
        UpdateValuesResponse result = spreadsheets.values().update(existingSpreadSheetID, sheetAndRange, body)
                .setValueInputOption("RAW").execute();
        System.out.printf("%d cells updated.\n", result.getUpdatedCells());
    }

    public void metaDataCheck() throws IOException, GeneralSecurityException {
        getSpreadsheetInstance();
        List<String> urls = getAllNovalnetLinks();
        for(String url:urls) {
            DriverActions.openURL(url);
            String value = null;
            List<WebElement> metaTags = DriverActions.getElements(By.tagName("meta"));
            for (WebElement metTag : metaTags) {
                value = metTag.getAttribute("name").replaceAll("\\s", "");
                if (value.equals("Description")) {
                    WebElement descriptionMetaTag = DriverActions.getElement(By.xpath("//meta[@name='Description']"));
                    String description = descriptionMetaTag.getAttribute("content");
                    writeDataGoogleSheets("META_DESCRIPTIION_DE", new ArrayList<Object>(Arrays.asList(url, "YES", description, description.length(), value)), existingSpreadSheetID);
                    break;
                }
            }



        }

        }

    public void verifyH1Tags() throws IOException, GeneralSecurityException {
        getSpreadsheetInstance();
        List<String> novalnetLinks = getAllNovalnetLinks();
        List<List<Object>> dataToWrite = new ArrayList<>();

        for (String url : novalnetLinks) {
            DriverActions.openURL(url);
            if (url != null && !url.isEmpty() && url.contains("novalnet") && checkedURLs.add(url)) {
               DriverActions.waitForPageLoad();
               List<WebElement> h1Tags = DriverActions.getElements(By.xpath("//h1"));
                     dataToWrite.add(Arrays.asList(url, h1Tags.size()));
            }
        }

        writeDataGoogleSheetsBatch("BROKENLINKS_DE", dataToWrite, existingSpreadSheetID);

    }

    public void verifyImageAltAttributes() throws GeneralSecurityException, IOException {
        String altValue=null;
        getSpreadsheetInstance();
        List<String> novalnetLinks = getAllNovalnetLinks();
        List<List<Object>> dataToWrite = new ArrayList<>();

        Set<String> urlsToSkip = new HashSet<>(Arrays.asList(
                "https://www.novalnet.com/wp-content/uploads/2024/03/kreditkarte-pcidss.webp",
                "https://www.novalnet.com/wp-content/uploads/2024/03/Add-a-heading-1.svg",
                "https://www.novalnet.com/wp-content/uploads/2024/03/software_hosted-1.webp",
                "https://www.novalnet.com/wp-content/uploads/2022/08/alliance-for-cyber-security.svg",
                "https://www.novalnet.com/wp-content/uploads/2024/06/cross-border-payments-150x150.jpg",
                "https://www.novalnet.com/wp-content/uploads/2024/06/influencer-marketing-150x150.jpg",
                "https://www.novalnet.com/wp-content/uploads/2024/06/ai-fraud-prevention-150x150.jpg",
                "https://www.novalnet.com/wp-content/themes/wp-bootstrap-starter/images/linkedin.svg",
                "https://www.novalnet.com/wp-content/themes/wp-bootstrap-starter/images/twitter_icon.png",
                "https://www.novalnet.com/wp-content/themes/wp-bootstrap-starter/images/fb_icon.png",
                "https://www.novalnet.com/wp-content/themes/wp-bootstrap-starter/images/instagram-icon.svg",
                "https://www.novalnet.com/wp-content/themes/wp-bootstrap-starter/images/payment-plugin-form.webp",
                "https://www.novalnet.com/wp-content/themes/wp-bootstrap-starter/images/form-bg.webp",
                "https://www.novalnet.com/wp-content/themes/wp-bootstrap-starter/images/x_icon.png"
        ));
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


                    dataToWrite.add(Arrays.asList(novalnetURL, imageURL, altValue));
                }
            }
        }
        writeDataGoogleSheetsBatch("BROKENLINKS_DE", dataToWrite, existingSpreadSheetID);
    }

    /*public void verifyImageAltAttributes() throws GeneralSecurityException, IOException {
        getSpreadsheetInstance();
        Set<String> urlsToSkip = new HashSet<>(Arrays.asList(
                "https://www.novalnet.com/wp-content/uploads/2024/03/kreditkarte-pcidss.webp",
                "https://www.novalnet.com/wp-content/uploads/2024/03/Add-a-heading-1.svg",
                "https://www.novalnet.com/wp-content/uploads/2024/03/software_hosted-1.webp",
                "https://www.novalnet.com/wp-content/uploads/2022/08/alliance-for-cyber-security.svg",
                "https://www.novalnet.com/wp-content/uploads/2024/06/cross-border-payments-150x150.jpg",
                "https://www.novalnet.com/wp-content/uploads/2024/06/influencer-marketing-150x150.jpg",
                "https://www.novalnet.com/wp-content/uploads/2024/06/ai-fraud-prevention-150x150.jpg",
                "https://www.novalnet.com/wp-content/themes/wp-bootstrap-starter/images/linkedin.svg",
                "https://www.novalnet.com/wp-content/themes/wp-bootstrap-starter/images/twitter_icon.png",
                "https://www.novalnet.com/wp-content/themes/wp-bootstrap-starter/images/fb_icon.png",
                "https://www.novalnet.com/wp-content/themes/wp-bootstrap-starter/images/instagram-icon.svg",
                "https://www.novalnet.com/wp-content/themes/wp-bootstrap-starter/images/x_icon.png"
        ));
        List<List<Object>> dataToWrite = new ArrayList<>();
            DriverActions.openURL("https://www.novalnet.com/contact/");
                DriverActions.waitForAllElementLocated(By.xpath("//img"));
                List<WebElement> images = DriverActions.getElements(By.xpath("//img"));
                for (WebElement image : images) {
                    String imageURL = image.getAttribute("src");
                    String alt = image.getAttribute("alt");
                    if (urlsToSkip.contains(imageURL)) {
                        continue;
                    }
                    dataToWrite.add(Arrays.asList("www.novalnet.com", imageURL, alt));

            }
        writeDataGoogleSheetsBatch("BROKENLINKS_DE", dataToWrite, existingSpreadSheetID);
        }*/


    public static void writeDataGoogleSheetsBatch(String sheetName, List<List<Object>> data, String existingSpreadSheetID) throws IOException {
        List<ValueRange> dataRange = new ArrayList<>();
        int nextROW = getRows(sheetName, existingSpreadSheetID) + 1;

        for (List<Object> row : data) {
            ValueRange valueRange = new ValueRange();
            valueRange.setRange(sheetName + "!A" + nextROW);
            valueRange.setValues(Collections.singletonList(row));
            dataRange.add(valueRange);
            nextROW++;  // Move to the next row
        }

        BatchUpdateValuesRequest body = new BatchUpdateValuesRequest()
                .setValueInputOption("RAW")
                .setData(dataRange);

        BatchUpdateValuesResponse response = spreadsheets.values().batchUpdate(existingSpreadSheetID, body).execute();
        System.out.printf("%d cells updated.\n", response.getTotalUpdatedCells());
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

