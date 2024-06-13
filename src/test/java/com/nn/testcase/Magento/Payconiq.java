package com.nn.testcase.Magento;
import com.nn.apis.MagentoAPI_Helper;
import com.nn.apis.MagentoAPIs;
import com.nn.apis.NovalnetAPIs;
import com.nn.apis.TID_Helper;
import com.nn.callback.PayconiqCallbackEvents;
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
import static com.nn.pages.Magento.NovalnetAdminPortalPaymentConfiguration.*;

public class Payconiq extends BaseTest{

    MagentoPage magentoPage = MagentoPage.builder()
            .novalnetAdminPortal(new NovalnetAdminPortal())
            .shopUserLoginPage(new ShopUserLoginPage())
            .shopBackEndLoginPage(new ShopBackEndLoginPage())
            .checkoutPage(new CheckoutPage())
            .successPage(new SuccessPage())
            .orderPage(new OrderPage())
            .myAccountPage(new MyAccountPage())
            .callback(new PayconiqCallbackEvents())
            .txnInfo(new HashMap<>())
            .build();

    @BeforeClass(alwaysRun = true)
    public void setUpGlobalConfiguration() {
        ExtentTestManager.saveToReport("Setup","Setting up the Global configuration and creating new customer.");
        createCustomer(PAYCONIQ);
        updateProductStock(PRODUCT_PAYCONIQ);
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        magentoPage.getNovalnetAdminPortal().loadAutomationProject();
        paymentActivation(PAYCONIQ,true);
    }
    @AfterClass
    public void tear(){
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        magentoPage.getNovalnetAdminPortal().loadAutomationProject();
        paymentActivation(PAYCONIQ,true);
    }

    @AfterMethod(alwaysRun = true)
    public void clearCartProducts(){
        clearCart();
    }

    @Test(priority = 1, description = "Check whether the Payconiq payment order placed successully and Callback partial refund events executed successfully",retryAnalyzer = RetryListener.class)
    public void firstOrder() {
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        magentoPage.getNovalnetAdminPortal().loadAutomationProject();
        setPaymentConfiguration(PAYCONIQ, Map.of(
                TESTMODE, false
        ));
        addProductToCart(PRODUCT_PAYCONIQ, 1);
        navigateCheckout(MagentoAPIs.getCustomerEmail());
        magentoPage.getCheckoutPage().isPaymentMethodDisplayed(PAYCONIQ);
        magentoPage.getCheckoutPage().placeOrderWithPayconiq();
        magentoPage.getSuccessPage().verifyNovalnetCommentsDisplayed();
        magentoPage.getTxnInfo().putAll(magentoPage.getSuccessPage().getSuccessPageTransactionDetails());
        String tid = magentoPage.getTxnInfo().get("TID").toString(),
                orderNumber = magentoPage.getTxnInfo().get("OrderNumber").toString(),
                orderAmount = magentoPage.getTxnInfo().get("TotalAmount").toString(),
                paymentName = magentoPage.getSuccessPage().getPaymentFromSuccessPage(PAYCONIQ),
                paymentComments = magentoPage.getTxnInfo().get("NovalnetComments").toString();
        TID_Helper.verifyTIDInformation(tid, orderAmount, TID_STATUS_CONFIRMED, PAYCONIQ);
        statusCommentsVerification(orderNumber,COMPLETION_ORDER_STATUS,true,paymentComments,paymentName);
        magentoPage.getCallback().transactionRefund(tid,String.valueOf(Integer.parseInt(orderAmount)/2));
        verifyNovalnetComments(orderNumber,REFUND_COMMENT_,String.valueOf(Integer.parseInt(orderAmount)/2));
        verifyCreditMemoCreated(orderNumber,true);
        verifyOrderStatus(orderNumber,PROCESSING_ORDER_STATUS);
    }
    @Test(priority = 2, description = "Check whether the Payconiq payment order placed successully and Callback Full refund events executed successfully",retryAnalyzer = RetryListener.class)
    public void secondOrder() {
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        magentoPage.getNovalnetAdminPortal().loadAutomationProject();
        setPaymentConfiguration(PAYCONIQ, Map.of(
                TESTMODE, true
        ));
        addProductToCart(PRODUCT_PAYCONIQ, 1);
        navigateCheckout(MagentoAPIs.getCustomerEmail());
        magentoPage.getCheckoutPage().isPaymentMethodDisplayed(PAYCONIQ);
        magentoPage.getCheckoutPage().placeOrderWithPayconiq();
        magentoPage.getSuccessPage().verifyNovalnetCommentsDisplayed();
        magentoPage.getTxnInfo().putAll(magentoPage.getSuccessPage().getSuccessPageTransactionDetails());
        String tid = magentoPage.getTxnInfo().get("TID").toString(),
                orderNumber = magentoPage.getTxnInfo().get("OrderNumber").toString(),
                orderAmount = magentoPage.getTxnInfo().get("TotalAmount").toString(),
                paymentName = magentoPage.getSuccessPage().getPaymentFromSuccessPage(PAYCONIQ),
                paymentComments = magentoPage.getTxnInfo().get("NovalnetComments").toString();
        TID_Helper.verifyTIDInformation(tid, orderAmount, TID_STATUS_CONFIRMED, PAYCONIQ);
        statusCommentsVerification(orderNumber,COMPLETION_ORDER_STATUS,true,paymentComments,paymentName);
        magentoPage.getCallback().transactionRefund(tid,orderAmount);
        verifyNovalnetComments(orderNumber,REFUND_COMMENT_,orderAmount);
        verifyCreditMemoCreated(orderNumber,true);
        verifyOrderStatus(orderNumber,REFUND_ORDER_STATUS);
    }
    @Test(priority = 3, description = "Check whether the Payconiq payment order placed successully and Callback credit event executed successfully",retryAnalyzer = RetryListener.class)
    public void thirdOrder() {
        addProductToCart(PRODUCT_PAYCONIQ, 1);
        navigateCheckout(MagentoAPIs.getCustomerEmail());
        magentoPage.getCheckoutPage().isPaymentMethodDisplayed(PAYCONIQ);
        magentoPage.getCheckoutPage().placeOrderWithPayconiq();
        magentoPage.getSuccessPage().verifyNovalnetCommentsDisplayed();
        magentoPage.getTxnInfo().putAll(magentoPage.getSuccessPage().getSuccessPageTransactionDetails());
        String tid = magentoPage.getTxnInfo().get("TID").toString(),
                orderNumber = magentoPage.getTxnInfo().get("OrderNumber").toString(),
                orderAmount = magentoPage.getTxnInfo().get("TotalAmount").toString(),
                paymentName = magentoPage.getSuccessPage().getPaymentFromSuccessPage(PAYCONIQ),
                paymentComments = magentoPage.getTxnInfo().get("NovalnetComments").toString();
        TID_Helper.verifyTIDInformation(tid, orderAmount, TID_STATUS_CONFIRMED, PAYCONIQ);
        statusCommentsVerification(orderNumber,COMPLETION_ORDER_STATUS,true,paymentComments,paymentName);
        magentoPage.getCallback().credit(tid,orderAmount);
        verifyNovalnetComments(orderNumber,CREDIT_COMMENT_,orderAmount);
        verifyOrderStatus(orderNumber,COMPLETION_ORDER_STATUS);
    }
    @Test(priority = 4, description = "Check whether the Payconiq payment order placed successully and Callback partial creidt events executed successfully",retryAnalyzer = RetryListener.class)
    public void fourthOrder() {
        addProductToCart(PRODUCT_PAYCONIQ, 1);
        navigateCheckout(MagentoAPIs.getCustomerEmail());
        magentoPage.getCheckoutPage().isPaymentMethodDisplayed(PAYCONIQ);
        magentoPage.getCheckoutPage().placeOrderWithPayconiq();
        magentoPage.getSuccessPage().verifyNovalnetCommentsDisplayed();
        magentoPage.getTxnInfo().putAll(magentoPage.getSuccessPage().getSuccessPageTransactionDetails());
        String tid = magentoPage.getTxnInfo().get("TID").toString(),
                orderNumber = magentoPage.getTxnInfo().get("OrderNumber").toString(),
                orderAmount = magentoPage.getTxnInfo().get("TotalAmount").toString(),
                paymentName = magentoPage.getSuccessPage().getPaymentFromSuccessPage(PAYCONIQ),
                paymentComments = magentoPage.getTxnInfo().get("NovalnetComments").toString();
        TID_Helper.verifyTIDInformation(tid, orderAmount, TID_STATUS_CONFIRMED, PAYCONIQ);
        statusCommentsVerification(orderNumber,COMPLETION_ORDER_STATUS,true,paymentComments,paymentName);
        magentoPage.getCallback().credit(tid,String.valueOf(Integer.parseInt(orderAmount)/2));
        verifyNovalnetComments(orderNumber,CREDIT_COMMENT_,String.valueOf(Integer.parseInt(orderAmount)/2));
        verifyOrderStatus(orderNumber,COMPLETION_ORDER_STATUS);
    }
    @Test(priority = 5, description = "Check whether the Payconiq payment order placed successfully through communication break",retryAnalyzer = RetryListener.class)
    public void fifthOrder(){
        magentoPage.getShopUserLoginPage().logout();
        createCustomer(PAYCONIQ);
        addProductToCart(PRODUCT_PAYCONIQ, 1);
        navigateCheckout(MagentoAPIs.getCustomerEmail());
        magentoPage.getCheckoutPage()
                .isPaymentMethodDisplayed(PAYCONIQ)
                .waitForPayconiqRedirection();
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        String tid = magentoPage.getNovalnetAdminPortal().getTID(MagentoAPIs.getCustomerEmail());
        String orderNumber = TID_Helper.getOrderNumber(tid);
        magentoPage.getCallback().communicationBreakSuccess(tid,"");
        statusCommentsVerificationAfterCommunicationBreak(tid,orderNumber,COMPLETION_ORDER_STATUS,true);
    }
    @Test(priority = 6, description = "Check whether the Payconiq payment order placed & failed by communication break",retryAnalyzer = RetryListener.class)
    public void sixthOrder(){
        magentoPage.getShopUserLoginPage().logout();
        createCustomer(PAYCONIQ);
        addProductToCart(PRODUCT_PAYCONIQ, 1);
        navigateCheckout(MagentoAPIs.getCustomerEmail());
        magentoPage.getCheckoutPage()
                .isPaymentMethodDisplayed(PAYCONIQ)
                .waitForPayconiqRedirection();
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        String tid = magentoPage.getNovalnetAdminPortal().getTID(MagentoAPIs.getCustomerEmail());
        String orderNumber = TID_Helper.getOrderNumber(tid);
        magentoPage.getCallback().communicationBreakFailure(tid,"");
        statusCommentsVerificationAfterCommunicationBreak(tid,orderNumber,CANCELLATION_ORDER_STATUS,false);
    }
    @Test(priority = 7, description = "Verify the display of error text on the checkout page in case a transaction is aborted, leading to a change in the order status to failed",retryAnalyzer = RetryListener.class)
    public void seventhOrder(){
        magentoPage.getShopUserLoginPage().logout();
        createCustomer(PAYCONIQ);
        addProductToCart(PRODUCT_PAYCONIQ, 1);
        navigateCheckout(MagentoAPIs.getCustomerEmail());
        magentoPage.getCheckoutPage().isPaymentMethodDisplayed(PAYCONIQ);
        magentoPage.getCheckoutPage().cancelAtPayconiqRedirectionPage();
        magentoPage.getCheckoutPage().verifyCheckoutErrorMessage(REDIRECT_END_USER_CANCEL_ERROR);
        magentoPage.getNovalnetAdminPortal().openNovalnetAdminPortal();
        String tid = magentoPage.getNovalnetAdminPortal().getTID(MagentoAPIs.getCustomerEmail());
        String orderNumber = TID_Helper.getOrderNumber(tid);
        TID_Helper.verifyTIDInformation(tid, "0", TID_STATUS_FAILURE, PAYCONIQ);
        statusCommentsVerificationAfterCommunicationBreak(tid,orderNumber,CANCELLATION_ORDER_STATUS,false);
    }

    @Test(priority = 8, description = "Check whether the test transaction is successful with guest user",retryAnalyzer = RetryListener.class)
    public void guestOrder(){
        magentoPage.getShopUserLoginPage().logout();
        navigateGuestCheckout(PRODUCT_GUEST,"BE");
        magentoPage.getCheckoutPage().isPaymentMethodDisplayed(PAYCONIQ);
        magentoPage.getCheckoutPage().placeOrderWithPayconiq();
        String orderNumber = magentoPage.getSuccessPage().getOrderNumberForGuestOrder();
        String tid = NovalnetAPIs.getRecentTransactionTID(orderNumber);
        DriverActions.verifyEquals(TID_Helper.getTIDStatus(tid),TID_STATUS_CONFIRMED,"Verify tid status for guest user");
        magentoPage.getOrderPage().load();
        magentoPage.getOrderPage().searchOrderByOrderNumber(orderNumber);
        DriverActions.verifyEquals(magentoPage.getOrderPage().getOrderNovalnetComments().contains(tid),true,"Verify TID value returned from server");
        magentoPage.getOrderPage().verifyOrderHistoryPageStatus(COMPLETION_ORDER_STATUS);
        magentoPage.getOrderPage().verifyInvoiceCreated(true);
    }

}
