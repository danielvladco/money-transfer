package com.danielvladco.money.transfer.transfer.exceptions;

public class TransferToOneselfException extends Exception {
	@Override
	public String getMessage() {
		return "unable to transfer money no oneself";
	}
}
