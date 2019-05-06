package com.danielvladco.money.transfer.account.models;

import java.util.Currency;

public class Account {
	private String id;

	private Currency currency;

	public Account(String id, Currency currency) {
		this.id = id;
		this.currency = currency;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Currency getCurrency() {
		return currency;
	}
}
