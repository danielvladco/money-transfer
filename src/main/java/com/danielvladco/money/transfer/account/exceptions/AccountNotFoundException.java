package com.danielvladco.money.transfer.account.exceptions;

public class AccountNotFoundException extends Exception {
	private String message = "account not found";

	public AccountNotFoundException() {
	}

	public AccountNotFoundException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
