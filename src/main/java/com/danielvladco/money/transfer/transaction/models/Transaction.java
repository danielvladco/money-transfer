package com.danielvladco.money.transfer.transaction.models;

import java.util.Currency;

public class Transaction {
	private String id;
	private String accountId;
	private long amount;
	private Currency currency;

	public Transaction(String id, String accountId, long amount, Currency currency) {
		this.id = id;
		this.accountId = accountId;
		this.amount = amount;
		this.currency = currency;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccountId() {
		return accountId;
	}

	public long getAmount() {
		return amount;
	}

	public Currency getCurrency() {
		return currency;
	}
}
