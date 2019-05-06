package com.danielvladco.money.transfer.transaction.exceptions;

public class TransactionNotFoundException extends Exception {

	public TransactionNotFoundException() {
	}

	@Override
	public String getMessage() {
		return "transaction not found";
	}

}
