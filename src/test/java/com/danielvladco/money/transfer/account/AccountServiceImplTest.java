package com.danielvladco.money.transfer.account;

import com.danielvladco.money.transfer.mocks.TransactionRepositoryMock;
import com.danielvladco.money.transfer.transaction.models.Transaction;
import org.junit.Assert;
import org.junit.Test;

import java.util.Currency;
import java.util.List;

public class AccountServiceImplTest {

	@Test
	public void getBalance() {
		var transactionRepository = new TransactionRepositoryMock();
		transactionRepository.getByAccountIdFn = accountId -> List.of(
				new Transaction("", "", 100, Currency.getInstance("USD")),
				new Transaction("", "", 120, Currency.getInstance("USD")),
				new Transaction("", "", 130, Currency.getInstance("USD")),
				new Transaction("", "", 400, Currency.getInstance("USD")),
				new Transaction("", "", -300, Currency.getInstance("USD"))
		);
		var accountService = new AccountServiceImpl(transactionRepository);
		var balance = accountService.getBalance("account-id1");

		Assert.assertEquals(450L, balance);
	}

	@Test
	public void hasFunds() {
		var transactionRepository = new TransactionRepositoryMock();
		transactionRepository.getByAccountIdFn = accountId -> List.of(
				new Transaction("", "", 100, Currency.getInstance("USD")),
				new Transaction("", "", 120, Currency.getInstance("USD")),
				new Transaction("", "", 130, Currency.getInstance("USD")),
				new Transaction("", "", 400, Currency.getInstance("USD")),
				new Transaction("", "", -300, Currency.getInstance("USD"))
		);
		var accountService = new AccountServiceImpl(transactionRepository);

		var hasFunds = accountService.hasFunds("account-id1", 500);

		Assert.assertFalse(hasFunds);

		hasFunds = accountService.hasFunds("account-id1", 450);
		Assert.assertTrue(hasFunds);

		hasFunds = accountService.hasFunds("account-id1", 439);
		Assert.assertTrue(hasFunds);

	}
}