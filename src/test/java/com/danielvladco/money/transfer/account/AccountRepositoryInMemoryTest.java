package com.danielvladco.money.transfer.account;

import com.danielvladco.money.transfer.account.exceptions.AccountInvalidException;
import com.danielvladco.money.transfer.account.exceptions.AccountNotFoundException;
import com.danielvladco.money.transfer.account.models.Account;
import org.junit.Assert;
import org.junit.Test;

import java.util.Currency;

public class AccountRepositoryInMemoryTest {

	@Test
	public void accountRepository() {
		var accountRepository = new AccountRepositoryInMemory();
		try {
			accountRepository.create(new Account("fake-id1", Currency.getInstance("USD")));
		} catch (AccountInvalidException e) {
			Assert.fail();
		}
		// create the with same id
		try {
			accountRepository.create(new Account("fake-id1", Currency.getInstance("EUR")));
		} catch (AccountInvalidException e) {
			Assert.fail();
		}
		try {
			var account = accountRepository.get("fake-id1");
			// asset it's the same currency
			Assert.assertEquals(account.getCurrency(), Currency.getInstance("USD"));
		} catch (AccountNotFoundException e) {
			Assert.fail("account must exist");
		}

		try {
			accountRepository.create(new Account("", Currency.getInstance("USD")));
		} catch (AccountInvalidException e) {
			Assert.fail();
		}
		try {
			accountRepository.create(new Account("", null));

			Assert.fail("Must not be able to create account without currency");
		} catch (AccountInvalidException ignored) {
		}

		try {
			accountRepository.create(null);

			Assert.fail("Must not be able to create null account");
		} catch (AccountInvalidException ignored) {
		}

		try {
			accountRepository.get("nonexistent-account");

			Assert.fail("must return not found exception");
		} catch (AccountNotFoundException ignored) {
		}

		try {
			accountRepository.get(null);

			Assert.fail("must return not found exception");
		} catch (AccountNotFoundException ignored) {
		}

		var accounts = accountRepository.getAll();
		Assert.assertEquals(2, accounts.size());
	}
}