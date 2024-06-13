package com.nn.callback;

import io.qameta.allure.Step;

import static com.nn.callback.CallbackProperties.*;

public class DirectDebitSEPACallbackEvents implements CallbackEventInterface{
	
	CallbackProperties callback;
	
	public DirectDebitSEPACallbackEvents() {
		callback = new CallbackProperties();
	}


	@Step("Perform TRANSACTION_CAPTURE event")
	@Override
	public String transactionCapture(String tid) {
		callback.setTID(tid);
		callback.setEvent(TRANSACTION_CAPTURE);
		callback.setPaymentType(DIRECT_DEBIT_SEPA);
		return callback.sendCallbackRequest();
	}

	@Step("Perform TRANSACTION_CANCEL event")
	@Override
	public String transactionCancel(String tid) {
		callback.setTID(tid);
		callback.setEvent(TRANSACTION_CANCEL);
		callback.setPaymentType(DIRECT_DEBIT_SEPA);
		return callback.sendCallbackRequest();
	}

	@Step("Perform TRANSACTION_REFUND event")
	public String transactionRefund(String tid,String amount) {
		callback.setCallbackValueOrAmount(amount);
		callback.setTID(tid);
		callback.setEvent(TRANSACTION_REFUND);
		callback.setPaymentType(REFUND_BY_BANK_TRANSFER_EU);
		return callback.sendCallbackRequest();
	}

	@Step("Perform TRANSACTION_UPDATE event with status")
	@Override
	public String transactionUpdateStatus(String tid, String status) {
		callback.setTID(tid);
		callback.setUpdateType("STATUS");
		callback.setCallbackValueOrAmount(status);
		callback.setEvent(TRANSACTION_UPDATE);
		callback.setPaymentType(DIRECT_DEBIT_SEPA);
		return callback.sendCallbackRequest();
	}

	@Step("Perform TRANSACTION_UPDATE event with amount")
	@Override
	public String transactionUpdateAmount(String tid, String amount) {
		callback.setTID(tid);
		callback.setUpdateType("AMOUNT");
		callback.setCallbackValueOrAmount(amount);
		callback.setEvent(TRANSACTION_UPDATE);
		callback.setPaymentType(DIRECT_DEBIT_SEPA);
		return callback.sendCallbackRequest();
	}

	@Step("Perform RETURN_DEBIT_SEPA event")
	@Override
	public String chargeback(String tid,String amount){
		callback.setCallbackValueOrAmount(amount);
		callback.setTID(tid);
		callback.setEvent(CHARGEBACK);
		callback.setPaymentType(RETURN_DEBIT_SEPA);
		return callback.sendCallbackRequest();
	}

	@Step("Perform CREDIT_ENTRY_SEPA event")
	@Override
	public String creditEntrySepa(String tid,String amount) {
		callback.setCallbackValueOrAmount(amount);
		callback.setTID(tid);
		callback.setEvent(CREDIT);
		callback.setPaymentType(CREDIT_ENTRY_SEPA);
		return callback.sendCallbackRequest();
	}

	@Step("Perform DEBT_COLLECTION_SEPA event")
	@Override
	public String debtCollectionSepa(String tid,String amount) {
		callback.setCallbackValueOrAmount(amount);
		callback.setTID(tid);
		callback.setEvent(CREDIT);
		callback.setPaymentType(DEBT_COLLECTION_SEPA);
		return callback.sendCallbackRequest();
	}

	@Step("Perform BANK_TRANSFER_BY_END_CUSTOMER event")
	@Override
	public String bankTransferByEndCustomer(String tid,String amount) {
		callback.setCallbackValueOrAmount(amount);
		callback.setTID(tid);
		callback.setEvent(CREDIT);
		callback.setPaymentType(BANK_TRANSFER_BY_END_CUSTOMER);
		return callback.sendCallbackRequest();
	}

	@Step("Perform PAYMENT_REMINDER_1 event")
	@Override
	public String paymentReminderOne(String tid) {
		callback.setTID(tid);
		callback.setEvent(PAYMENT_REMINDER_1);
		callback.setPaymentType(DIRECT_DEBIT_SEPA);
		return callback.sendCallbackRequest();
	}

	@Step("Perform PAYMENT_REMINDER_2 event")
	@Override
	public String paymentReminderTwo(String tid) {
		callback.setTID(tid);
		callback.setEvent(PAYMENT_REMINDER_2);
		callback.setPaymentType(DIRECT_DEBIT_SEPA);
		return callback.sendCallbackRequest();
	}

	@Step("Perform SUBMISSION_TO_COLLECTION_AGENCY event")
	@Override
	public String submissionToCollection(String tid) {
		callback.setTID(tid);
		callback.setEvent(SUBMISSION_TO_COLLECTION_AGENCY);
		callback.setPaymentType(DIRECT_DEBIT_SEPA);
		return callback.sendCallbackRequest();
	}
	
}
