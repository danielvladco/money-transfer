package com.danielvladco.money.transfer.transfer.exceptions;

import java.util.Currency;

public class MismatchingCurrenciesException extends Exception {
	private Currency wanted;
	private Currency received;

	public MismatchingCurrenciesException(Currency wanted, Currency received) {
		this.wanted = wanted;
		this.received = received;
	}

	public String getMessage() {
		return "mismatching currencies, wanted: " + wanted.getCurrencyCode() + " received: " + received.getCurrencyCode();
	}
}
