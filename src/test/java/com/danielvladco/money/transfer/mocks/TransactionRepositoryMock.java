package com.danielvladco.money.transfer.mocks;

import com.danielvladco.money.transfer.transaction.TransactionRepository;
import com.danielvladco.money.transfer.transaction.exceptions.TransactionInvalidException;
import com.danielvladco.money.transfer.transaction.exceptions.TransactionNotFoundException;
import com.danielvladco.money.transfer.transaction.models.Transaction;

import java.util.List;
import java.util.function.Function;

public class TransactionRepositoryMock implements TransactionRepository {
	public Function<Object, List<Transaction>> getAllFn;
	public GetFunction getFn;
	public CreateFunction createFn;
	public Function<String, List<Transaction>> getByAccountIdFn;

	@Override
	public List<Transaction> getAll() {
		return getAllFn.apply(null);
	}

	@Override
	public Transaction get(String transactionId) throws TransactionNotFoundException {
		return getFn.apply(transactionId);
	}

	@Override
	public void create(Transaction... transactions) throws TransactionInvalidException {
		createFn.apply(transactions);
	}

	@Override
	public List<Transaction> getByAccountId(String accountId) {
		return getByAccountIdFn.apply(accountId);
	}

	@FunctionalInterface
	public interface GetFunction {
		Transaction apply(String id) throws TransactionNotFoundException;
	}

	@FunctionalInterface
	public interface CreateFunction {
		void apply(Transaction... transactions) throws TransactionInvalidException;
	}
}
