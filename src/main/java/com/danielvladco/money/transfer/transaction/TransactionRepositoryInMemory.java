package com.danielvladco.money.transfer.transaction;

import com.danielvladco.money.transfer.transaction.exceptions.TransactionInvalidException;
import com.danielvladco.money.transfer.transaction.exceptions.TransactionNotFoundException;
import com.danielvladco.money.transfer.transaction.models.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TransactionRepositoryInMemory implements TransactionRepository {

	private Map<String, Transaction> transactions = new ConcurrentHashMap<>();

	@Override
	public List<Transaction> getAll() {
		return new ArrayList<>(transactions.values());
	}

	@Override
	public Transaction get(String transactionId) throws TransactionNotFoundException {
		if (transactionId == null) {
			throw new TransactionNotFoundException();
		}
		if (!transactions.containsKey(transactionId)) {
			throw new TransactionNotFoundException();
		}
		return transactions.get(transactionId);
	}

	@Override
	public void create(Transaction transaction) throws TransactionInvalidException {
		if (transaction == null) {
			throw new TransactionInvalidException("unable to create null transaction");
		}

		if (transaction.getId() == null || transaction.getId().equals("")) {
			transaction.setId(UUID.randomUUID().toString());
		}

		if (transaction.getAmount() == 0) {
			throw new TransactionInvalidException("transaction amount must not be 0");
		}
		if (transaction.getCurrency() == null) {
			throw new TransactionInvalidException("transaction currency must not be null");
		}
		if (transaction.getAccountId() == null || transaction.getAccountId().equals("")) {
			throw new TransactionInvalidException("account id must not be empty");
		}


		if (transactions.containsKey(transaction.getId())) {
			return;
		}

		transactions.put(transaction.getId(), transaction);
	}

	@Override
	public List<Transaction> getByAccountId(String accountId) {
		if (accountId == null) {
			accountId = "";
		}
		final String accountId2 = accountId;
		return transactions.values()
				.stream()
				.parallel()
				.filter(t -> t.getAccountId().equals(accountId2))
				.collect(Collectors.toList());
	}
}
