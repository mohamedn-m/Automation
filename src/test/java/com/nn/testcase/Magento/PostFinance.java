package com.nn.testcase.Magento;

import com.nn.apis.MagentoAPIs;
import com.nn.apis.NovalnetAPIs;
import com.nn.apis.TID_Helper;
import com.nn.callback.PostFinanceCardPaymentCallbackEvents;
import com.nn.listeners.RetryListener;
import com.nn.pages.Magento.*;
import com.nn.pages.Magento.basetest.BaseTest;
import com.nn.reports.ExtentTestManager;
import com.nn.utilities.DriverActions;
import com.nn.utilities.ThunderBirdEmailHelper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static com.nn.Magento.Constants.*;
import static com.nn.apis.MagentoAPI_Helper.*;
import static com.nn.callback.CallbackProperties.*;
import static com.nn.callback.CallbackProperties.IDEAL;
import static com.nn.pages.Magento.NovalnetAdminPortalPaymentConfiguration.*;

public class PostFinance extends BaseTest {

    MagentoPage magentoPage = MagentoPage.builder()
            .novalnetAdminPortal(new NovalnetAdminPortal())
            .shopUserLoginPage(new ShopUserLoginPage())
            .shopBackEndLoginPage(new ShopBackEndLoginPage())
            .checkoutPage(new CheckoutPage())
            .successPage(new SuccessPage())
            .orderPage(new OrderPage())
            .myAccountPage(new MyAccountPage())
            .callback(new PostFinanceCardPaymentCallbackEvents())
            .txnInfo(new HashMap<>())
            .build();

    @BeforeClass(alwaysRun = true)
    public void setUpGlobalConfiguration() {
        ExtentTestManager.saveToReport("Setup","Setting up the Global configuration and creating new customer.");
        createCustomer(POSTFINANCE_CARD);
        updateProductStock(PRODUCT_POST_FINANCE_PAY);
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        magentoPage.getNovalnetAdminPortal().loadAutomationProject();
        paymentActivation(POSTFINANCE_CARD,true);
    }

    @AfterClass
    public void tear(){
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        magentoPage.getNovalnetAdminPortal().loadAutomationProject();
        paymentActivation(POSTFINANCE_CARD,true);
    }

    @AfterMethod(alwaysRun = true)
    public void clearCartProducts(){
        clearCart();
    }

    @Test(priority = 1, description = "Check whether the PostFinance payment order placed successully and Callback Full refund events executed successfully",retryAnalyzer = RetryListener.class)
    public void firstOrder(){
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        magentoPage.getNovalnetAdminPortal().loadAutomationProject();
        setPaymentConfiguration(POSTFINANCE_CARD, Map.of(
                TESTMODE,false
        ));
        addProductToCart(PRODUCT_POST_FINANCE_PAY,1);
        navigateCheckout(MagentoAPIs.getCustomerEmail());
        magentoPage.getCheckoutPage().isPaymentMethodDisplayed(POSTFINANCE_CARD);
        magentoPage.getCheckoutPage().placeOrderWithPostFinance();
        magentoPage.getSuccessPage().verifyNovalnetCommentsDisplayed();
        magentoPage.getTxnInfo().putAll(magentoPage.getSuccessPage().getSuccessPageTransactionDetails());
        String tid = magentoPage.getTxnInfo().get("TID").toString(),
                orderNumber = magentoPage.getTxnInfo().get("OrderNumber").toString(),
                orderAmount = magentoPage.getTxnInfo().get("TotalAmount").toString(),
                paymentName = magentoPage.getSuccessPage().getPaymentFromSuccessPage(POSTFINANCE_CARD),
                paymentComments = magentoPage.getTxnInfo().get("NovalnetComments").toString();
        TID_Helper.verifyTIDInformation(tid, orderAmount, TID_STATUS_PENDING, POSTFINANCE_CARD);
        statusCommentsVerification(orderNumber,PENDING_ORDER_STATUS,false,paymentComments,paymentName);
        magentoPage.getCallback().transactionUpdateStatus(tid,TID_STATUS_CONFIRMED);
        verifyInvoiceCreated(orderNumber,true);
        magentoPage.getCallback().transactionRefund(tid,orderAmount);
        verifyNovalnetComments(orderNumber,REFUND_COMMENT_,orderAmount);
        verifyCreditMemoCreated(orderNumber,true);
        verifyOrderStatus(orderNumber,REFUND_ORDER_STATUS);
    }

    @Test(priority = 2, description = "Check whether the PostFinance payment order placed successully and Callback partial refund events executed successfully executed successfully",retryAnalyzer = RetryListener.class)
    public void secondOrder(){
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        magentoPage.getNovalnetAdminPortal().loadAutomationProject();
        setPaymentConfiguration(POSTFINANCE_CARD, Map.of(
                TESTMODE,true
        ));
        addProductToCart(PRODUCT_POST_FINANCE_PAY,1);
        navigateCheckout(MagentoAPIs.getCustomerEmail());
        magentoPage.getCheckoutPage().isPaymentMethodDisplayed(POSTFINANCE_CARD);
        magentoPage.getCheckoutPage().placeOrderWithPostFinance();
        magentoPage.getSuccessPage().verifyNovalnetCommentsDisplayed();
        magentoPage.getTxnInfo().putAll(magentoPage.getSuccessPage().getSuccessPageTransactionDetails());
        String tid = magentoPage.getTxnInfo().get("TID").toString(),
                orderNumber = magentoPage.getTxnInfo().get("OrderNumber").toString(),
                orderAmount = magentoPage.getTxnInfo().get("TotalAmount").toString(),
                paymentName = magentoPage.getSuccessPage().getPaymentFromSuccessPage(POSTFINANCE_CARD),
                paymentComments = magentoPage.getTxnInfo().get("NovalnetComments").toString();
        TID_Helper.verifyTIDInformation(tid, orderAmount, TID_STATUS_PENDING, POSTFINANCE_CARD);
        statusCommentsVerification(orderNumber,PENDING_ORDER_STATUS,false,paymentComments,paymentName);
        magentoPage.getCallback().transactionRefund(tid,String.valueOf(Integer.parseInt(orderAmount)/2));
        verifyNovalnetComments(orderNumber,REFUND_COMMENT_,String.valueOf(Integer.parseInt(orderAmount)/2));
        verifyCreditMemoCreated(orderNumber,true);
        verifyOrderStatus(orderNumber,PROCESSING_ORDER_STATUS);
    }


    @Test(priority = 3, description = "Check whether the PostFinance payment order placed successfully by communication break",retryAnalyzer = RetryListener.class)
    public void thirdOrder(){
        magentoPage.getShopUserLoginPage().logout();
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        magentoPage.getNovalnetAdminPortal().loadAutomationProject();
        setPaymentConfiguration(POSTFINANCE_CARD, Map.of(
                TESTMODE,true
        ));
        createCustomer(POSTFINANCE_CARD);
        addProductToCart(PRODUCT_POST_FINANCE_PAY,1);
        navigateCheckout(MagentoAPIs.getCustomerEmail());
        magentoPage.getCheckoutPage().isPaymentMethodDisplayed(POSTFINANCE_CARD);
        magentoPage.getCheckoutPage().waitForPostFinanceRedirection();
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        String tid = magentoPage.getNovalnetAdminPortal().getTID(MagentoAPIs.getCustomerEmail());
        String orderNumber = TID_Helper.getOrderNumber(tid);
        magentoPage.getCallback().communicationBreakSuccess(tid,"");
        statusCommentsVerificationAfterCommunicationBreak(tid,orderNumber,COMPLETION_ORDER_STATUS,true);
    }

    @Test(priority = 4, description = "Check whether the PostFinance payment order placed & failed by communication break",retryAnalyzer = RetryListener.class)
    public void fourthOrder(){
        magentoPage.getShopUserLoginPage().logout();
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        magentoPage.getNovalnetAdminPortal().loadAutomationProject();
        setPaymentConfiguration(POSTFINANCE_CARD, Map.of(
                TESTMODE,true
        ));
        createCustomer(POSTFINANCE_CARD);
        addProductToCart(PRODUCT_POST_FINANCE_PAY,1);
        navigateCheckout(MagentoAPIs.getCustomerEmail());
        magentoPage.getCheckoutPage().isPaymentMethodDisplayed(POSTFINANCE_CARD);
        magentoPage.getCheckoutPage().waitForPostFinanceRedirection();
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        String tid = magentoPage.getNovalnetAdminPortal().getTID(MagentoAPIs.getCustomerEmail());
        String orderNumber = TID_Helper.getOrderNumber(tid);
        magentoPage.getCallback().communicationBreakSuccess(tid,"");
        statusCommentsVerificationAfterCommunicationBreak(tid,orderNumber,CANCELLATION_ORDER_STATUS,false);
    }

    @Test(priority = 5, description = "Verify the display of error text on the checkout page in case a transaction is aborted, leading to a change in the order status to failed",retryAnalyzer = RetryListener.class)
    public void fifthOrder(){
        magentoPage.getShopUserLoginPage().logout();
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        magentoPage.getNovalnetAdminPortal().loadAutomationProject();
        setPaymentConfiguration(POSTFINANCE_CARD, Map.of(
                TESTMODE,true
        ));
        createCustomer(POSTFINANCE_CARD);
        addProductToCart(PRODUCT_POST_FINANCE_PAY,1);
        navigateCheckout(MagentoAPIs.getCustomerEmail());
        magentoPage.getCheckoutPage().isPaymentMethodDisplayed(POSTFINANCE_CARD);
        magentoPage.getCheckoutPage().cancelAtPostFinanceRedirection();
        magentoPage.getCheckoutPage().verifyCheckoutErrorMessage(REDIRECT_END_USER_CANCEL_ERROR);
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        String tid = magentoPage.getNovalnetAdminPortal().getTID(MagentoAPIs.getCustomerEmail());
        String orderNumber = TID_Helper.getOrderNumber(tid);
        TID_Helper.verifyTIDInformation(tid, "0", TID_STATUS_FAILURE, POSTFINANCE_CARD);
        statusCommentsVerificationAfterCommunicationBreak(tid,orderNumber,CANCELLATION_ORDER_STATUS,false);
    }

    //@Test(priority = 6, description = "Verify novalnet payment comments displayed in the mail after successful order",retryAnalyzer = RetryListener.class)
    public void mailVerifyOrder(){
        magentoPage.getShopUserLoginPage().logout();
        createCustomer(POSTFINANCE_CARD,"gopinath_m@novalnetsolutions.com");
        addProductToCart(PRODUCT_POST_FINANCE_PAY,1);
        magentoPage.getShopUserLoginPage().SigninToShop(MagentoAPIs.getCustomerEmail(),SHOP_FRONTEND_PASSWORD);
        magentoPage.getMyAccountPage().load().changeCountry("CH");
        magentoPage.getCheckoutPage().load().openCheckoutPage();
        magentoPage.getCheckoutPage().isPaymentMethodDisplayed(POSTFINANCE_CARD);
        magentoPage.getCheckoutPage().placeOrderWithPostFinance();
        magentoPage.getSuccessPage().verifyNovalnetCommentsDisplayed();
        magentoPage.getTxnInfo().putAll(magentoPage.getSuccessPage().getSuccessPageTransactionDetails());
        String tid = magentoPage.getTxnInfo().get("TID").toString(),
                orderNumber = magentoPage.getSuccessPage().getOrderNumber(),
                orderAmount = magentoPage.getTxnInfo().get("TotalAmount").toString(),
                paymentName = magentoPage.getSuccessPage().getPaymentFromSuccessPage(POSTFINANCE_CARD),
                paymentComments = magentoPage.getSuccessPage().getPaymentComment();
        TID_Helper.verifyTIDInformation(tid, orderAmount, TID_STATUS_PENDING, POSTFINANCE_CARD);
        String mailContent = ThunderBirdEmailHelper.getTodaysEmailByOrderNoWithRetry(ThunderBirdEmailHelper.connectToThunderBird(),mailOrderSubjectText,orderNumber);
        DriverActions.verifyEquals(mailContent.contains(paymentName),true,"Verify order mail has payment name");
        DriverActions.verifyEquals(mailContent.contains(paymentComments),true,"Verify order mail has novalnet payment comments");
        magentoPage.getCallback().transactionUpdateStatus(tid,TID_STATUS_CONFIRMED);
        DriverActions.reloadPage();
        String invoiceMailContent = ThunderBirdEmailHelper.getTodaysEmailByOrderNoWithRetry(ThunderBirdEmailHelper.connectToThunderBird(),mailInvoiceSubjectText,orderNumber);
        DriverActions.verifyEquals(invoiceMailContent.contains(paymentName),true,"Verify invoice mail has payment name");
        DriverActions.verifyEquals(invoiceMailContent.contains(paymentComments),true,"Verify invoice mail has novalnet payment comments");
    }

    @Test(priority = 6, description = "Check whether the test transaction is successful with guest user",retryAnalyzer = RetryListener.class)
    public void guestOrder(){
        magentoPage.getShopUserLoginPage().logout();
        navigateGuestCheckout(PRODUCT_GUEST,"CH");
        magentoPage.getCheckoutPage().isPaymentMethodDisplayed(POSTFINANCE_CARD);
        magentoPage.getCheckoutPage().placeOrderWithPostFinance();
        String orderNumber = magentoPage.getSuccessPage().getOrderNumberForGuestOrder();
        String tid = NovalnetAPIs.getRecentTransactionTID(orderNumber);
        DriverActions.verifyEquals(TID_Helper.getTIDStatus(tid),TID_STATUS_CONFIRMED,"Verify tid status for guest user");
        magentoPage.getOrderPage().load();
        magentoPage.getOrderPage().searchOrderByOrderNumber(orderNumber);
        DriverActions.verifyEquals(magentoPage.getOrderPage().getOrderNovalnetComments().contains(tid),true,"Verify TID value returned from server");
        magentoPage.getOrderPage().verifyOrderHistoryPageStatus(COMPLETION_ORDER_STATUS);
        magentoPage.getOrderPage().verifyInvoiceCreated(true);
    }
    //@Test(priority = 8,description = "Check whether the payment logo displayed as per admin portal configurations ",retryAnalyzer = RetryListener.class)
    public void verifyPaymentLogo(){
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        magentoPage.getNovalnetAdminPortal().loadAutomationProject();
        setLogoConfiguration(false);
        createCustomer(PRODUCT_POST_FINANCE_PAY);
        addProductToCart(POSTFINANCE_CARD,1);
        navigateCheckout(MagentoAPIs.getCustomerEmail());
        magentoPage.getCheckoutPage().isLogoDisplayed("PostFinance Card",false);
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        magentoPage.getNovalnetAdminPortal().loadAutomationProject();
        setLogoConfiguration(true);
        magentoPage.getCheckoutPage().load();
        magentoPage.getCheckoutPage().openCheckoutPage();
        magentoPage.getCheckoutPage().isLogoDisplayed("PostFinance Card",true);
    }
    //@Test(priority = 7, description = "Verify the payment is hidden on the checkout page when payment is disabled in admin portal")
    public void deactivatePaymentVerifyCheckout(){
        magentoPage.getShopUserLoginPage().logout();
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        magentoPage.getNovalnetAdminPortal().loadAutomationProject();
        paymentActivation(POSTFINANCE_CARD,false);
        createCustomer(POSTFINANCE_CARD);
        addProductToCart(PRODUCT_POST_FINANCE_PAY,1);
        navigateCheckout(MagentoAPIs.getCustomerEmail());
        magentoPage.getCheckoutPage()
                .isPaymentDisplayed(POSTFINANCE_CARD,false);
    }
}
