package com.danielvladco.money.transfer.transfer.models;

public class TransferMoneyRequest {
	private String targetAccountId;
	private String sourceAccountId;
	private long amount;

	public TransferMoneyRequest(String targetAccountId, String sourceAccountId, long amount) {
		this.targetAccountId = targetAccountId;
		this.sourceAccountId = sourceAccountId;
		this.amount = amount;
	}

	public String getTargetAccountId() {
		return targetAccountId;
	}

	public String getSourceAccountId() {
		return sourceAccountId;
	}

	public long getAmount() {
		return amount;
	}

}
