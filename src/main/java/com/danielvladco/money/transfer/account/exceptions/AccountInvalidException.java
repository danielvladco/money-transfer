package com.danielvladco.money.transfer.account.exceptions;

public class AccountInvalidException extends Exception {
	private String message;

	public AccountInvalidException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
