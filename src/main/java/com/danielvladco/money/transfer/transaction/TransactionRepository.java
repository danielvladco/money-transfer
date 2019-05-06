package com.danielvladco.money.transfer.transaction;

import com.danielvladco.money.transfer.transaction.exceptions.TransactionInvalidException;
import com.danielvladco.money.transfer.transaction.exceptions.TransactionNotFoundException;
import com.danielvladco.money.transfer.transaction.models.Transaction;

import java.util.List;

public interface TransactionRepository {

	List<Transaction> getAll();

	Transaction get(String transactionId) throws TransactionNotFoundException;

	void create(Transaction transaction) throws TransactionInvalidException;

	List<Transaction> getByAccountId(String accountId) ;
}
