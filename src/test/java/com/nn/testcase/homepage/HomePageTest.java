package com.nn.testcase.homepage;

import com.nn.brokenlink.base.BaseTest;
import com.nn.utilities.DriverActions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class HomePageTest extends BaseTest {

    @Test(priority = 1,dataProvider = "siteMap_Url",description = "sdsdsdssd")
    public void brokenLink(String input) throws GeneralSecurityException, IOException {
        DriverActions.openURL(input);
        DriverActions.waitForTitleContains(("XML Sitemap"));
        checkAllLinks();
        checkSublinks();
    }

    @Test(priority = 2,dataProvider = "siteMap_Url",description = "verify the more than one H1 tag in the URL")
    public void H1TagChecker(String input) throws IOException, GeneralSecurityException {
        DriverActions.openURL(input);
        DriverActions.waitForTitleContains(("XML Sitemap"));
        verifyH1Tags();
    }

    @Test(priority = 3,dataProvider = "siteMap_Url",description = "Verify images are mentioned in alt tag")
    public void imageAltTagChecker(String input) throws GeneralSecurityException, IOException {
        DriverActions.openURL(input);
        DriverActions.waitForTitleContains(("XML Sitemap"));
        verifyImageAltAttributes();
    }
    @Test(priority = 4,dataProvider = "siteMap_Url",description = "")
    public void verifyMetaData(String URL) throws IOException, GeneralSecurityException {
        DriverActions.openURL(URL);
        metaDataCheck();
    }

    @DataProvider(parallel = true)
    public Object[][] siteMap_Url(){
        return new Object[][] {{"https://www.novalnet.com/post-sitemap.xml"},
                {"https://www.novalnet.com/page-sitemap.xml"},
                {"https://www.novalnet.com/integration-sitemap.xml"},
                {"https://www.novalnet.com/news-sitemap.xml"},
                {"https://www.novalnet.com/paymentsolution-sitemap.xml"},
                {"https://www.novalnet.com/glossary-sitemap.xml"},
                {"https://www.novalnet.com/news_categories-sitemap.xml"},
                {"https://www.novalnet.com/glossary_categories-sitemap.xml"},
                 {"https://www.novalnet.com/careers-sitemap.xml"}};
    }

}