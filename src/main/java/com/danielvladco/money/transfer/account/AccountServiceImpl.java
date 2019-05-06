package com.danielvladco.money.transfer.account;

import com.danielvladco.money.transfer.transaction.TransactionRepository;

public class AccountServiceImpl implements AccountService {

	private TransactionRepository transactionRepository;

	public AccountServiceImpl(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	@Override
	public long getBalance(String accountId) {
		var transactions = transactionRepository.getByAccountId(accountId);
		var amount = 0;
		for (var t : transactions) {
			amount += t.getAmount();
		}
		return amount;
	}

	@Override
	public boolean hasFunds(String accountId, long amount) {
		return getBalance(accountId) >= amount;
	}
}
