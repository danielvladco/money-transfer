package com.danielvladco.money.transfer.transfer.exceptions;

public class InsufficientFundsException extends Exception {

	public InsufficientFundsException() {
	}

	@Override
	public String getMessage() {
		return "account has insufficient funds";
	}
}
