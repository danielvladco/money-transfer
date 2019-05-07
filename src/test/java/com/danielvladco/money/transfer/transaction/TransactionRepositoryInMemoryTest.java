package com.danielvladco.money.transfer.transaction;

import com.danielvladco.money.transfer.transaction.exceptions.TransactionInvalidException;
import com.danielvladco.money.transfer.transaction.exceptions.TransactionNotFoundException;
import com.danielvladco.money.transfer.transaction.models.Transaction;
import org.junit.Assert;
import org.junit.Test;

import java.util.Currency;

public class TransactionRepositoryInMemoryTest {
	@Test
	public void transactionRepository() {
		var transactionRepository = new TransactionRepositoryInMemory();
		try {
			transactionRepository.create(new Transaction("fake-id1", "fake-account-id1", 100, Currency.getInstance("USD")));
		} catch (TransactionInvalidException e) {
			Assert.fail();
		}
		// create the with same id
		try {
			transactionRepository.create(new Transaction("fake-id1", "fake-account-id1", -100, Currency.getInstance("EUR")));
		} catch (TransactionInvalidException e) {
			Assert.fail();
		}
		try {
			var Transaction = transactionRepository.get("fake-id1");
			// asset it's the same currency
			Assert.assertEquals(Transaction.getCurrency(), Currency.getInstance("USD"));
		} catch (TransactionNotFoundException e) {
			Assert.fail("Transaction must exist");
		}

		try {
			transactionRepository.create(new Transaction("", "fake-account-id1", 100, Currency.getInstance("USD")));
		} catch (TransactionInvalidException e) {
			Assert.fail();
		}
		try {
			transactionRepository.create(new Transaction("", "fake-account-id1", 100, null));

			Assert.fail("Must not be able to create Transaction without currency");
		} catch (TransactionInvalidException ignored) {
		}
		try {
			transactionRepository.create(new Transaction("", "fake-account-id1", 0, Currency.getInstance("USD")));

			Assert.fail("Must not be able to create Transaction with 0 amount");
		} catch (TransactionInvalidException ignored) {
		}
		try {
			transactionRepository.create(new Transaction("", "", 1000, Currency.getInstance("USD")));

			Assert.fail("Must not be able to create Transaction without account id ");
		} catch (TransactionInvalidException ignored) {
		}

		try {
			transactionRepository.create(null, null);

			Assert.fail("Must not be able to create null Transaction");
		} catch (TransactionInvalidException ignored) {
		}

		try {
			transactionRepository.get("nonexistent-Transaction");

			Assert.fail("must return not found exception");
		} catch (TransactionNotFoundException ignored) {
		}
		try {
			transactionRepository.get(null);

			Assert.fail("must return not found exception");
		} catch (TransactionNotFoundException ignored) {
		}

		var transactions = transactionRepository.getAll();
		Assert.assertEquals(2, transactions.size());

		transactions = transactionRepository.getByAccountId("fake-account-id1");
		Assert.assertEquals(2, transactions.size());

		transactions = transactionRepository.getByAccountId(null);
		Assert.assertEquals(0, transactions.size());
	}
}