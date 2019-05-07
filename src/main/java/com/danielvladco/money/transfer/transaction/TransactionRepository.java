package com.danielvladco.money.transfer.transaction;

import com.danielvladco.money.transfer.transaction.exceptions.TransactionInvalidException;
import com.danielvladco.money.transfer.transaction.exceptions.TransactionNotFoundException;
import com.danielvladco.money.transfer.transaction.models.Transaction;

import java.util.List;

public interface TransactionRepository {

	List<Transaction> getAll();

	/**
	 * Get a transaction based on transaction id
	 *
	 * @param transactionId transaction id
	 * @return transaction
	 * @throws TransactionNotFoundException transaction is not found
	 */
	Transaction get(String transactionId) throws TransactionNotFoundException;

	/**
	 * Create a list of transactions in one atomic operation.
	 *
	 * @param transactions list of transactions
	 * @throws TransactionInvalidException if at least one transaction is is invalid
	 */
	void create(Transaction... transactions) throws TransactionInvalidException;

	/**
	 * Get a list of transactions based on account id
	 *
	 * @param accountId account id
	 * @return list of transaction
	 */
	List<Transaction> getByAccountId(String accountId);
}
