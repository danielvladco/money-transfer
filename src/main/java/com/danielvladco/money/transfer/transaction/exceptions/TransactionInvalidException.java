package com.danielvladco.money.transfer.transaction.exceptions;

public class TransactionInvalidException extends Exception {
	private String message;

	public TransactionInvalidException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
