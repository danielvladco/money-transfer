package com.danielvladco.money.transfer.transfer;

import com.danielvladco.money.transfer.account.exceptions.AccountNotFoundException;
import com.danielvladco.money.transfer.account.models.Account;
import com.danielvladco.money.transfer.mocks.AccountRepositoryMock;
import com.danielvladco.money.transfer.mocks.AccountServiceMock;
import com.danielvladco.money.transfer.mocks.TransactionRepositoryMock;
import com.danielvladco.money.transfer.transaction.exceptions.TransactionInvalidException;
import com.danielvladco.money.transfer.transaction.models.Transaction;
import com.danielvladco.money.transfer.transfer.exceptions.InsufficientFundsException;
import com.danielvladco.money.transfer.transfer.exceptions.MismatchingCurrenciesException;
import com.danielvladco.money.transfer.transfer.models.TransferMoneyRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.Currency;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class TransferServiceImplTest {
	private TransactionRepositoryMock transactionRepository = new TransactionRepositoryMock();
	private AccountRepositoryMock accountRepository = new AccountRepositoryMock();
	private AccountServiceMock accountService = new AccountServiceMock();
	private TransferServiceImpl transferService = new TransferServiceImpl(accountRepository, accountService, transactionRepository);

	@Test
	public void transferMoney() {
		try {
			doMoneyTransfer(true,
					accountId -> new Account(accountId, Currency.getInstance("USD")),
					accountId -> new Account(accountId, Currency.getInstance("USD")),
					new TransferMoneyRequest(
							"target-account-id1",
							"source-account-id1",
							1000
					));
		} catch (Exception e) {
			Assert.fail("transfer must be successful");
		}

		try {
			doMoneyTransfer(false,
					accountId -> new Account(accountId, Currency.getInstance("USD")),
					accountId -> new Account(accountId, Currency.getInstance("USD")),
					new TransferMoneyRequest(
							"target-account-id1",
							"source-account-id1",
							1000
					));

			Assert.fail("transaction must fail with InsufficientFundsException");
		} catch (InsufficientFundsException e) {
			e.printStackTrace();
		} catch (Exception e) {
			Assert.fail("transaction must fail with InsufficientFundsException");
		}

		try {
			doMoneyTransfer(true,
					accountId -> new Account(accountId, Currency.getInstance("EUR")),
					accountId -> new Account(accountId, Currency.getInstance("USD")),
					new TransferMoneyRequest(
							"target-account-id1",
							"source-account-id1",
							1000
					));

			Assert.fail("transaction must fail with MismatchingCurrenciesException");
		} catch (MismatchingCurrenciesException e) {
			e.printStackTrace();
		} catch (Exception e) {
			Assert.fail("transaction must fail with MismatchingCurrenciesException");
		}

		try {
			doMoneyTransfer(true,
					accountId -> new Account(accountId, Currency.getInstance("EUR")),
					accountId -> {
						throw new AccountNotFoundException();
					},
					new TransferMoneyRequest(
							"target-account-id1",
							"source-account-id1",
							1000
					));

			Assert.fail("transaction must fail with AccountNotFoundException");
		} catch (AccountNotFoundException e) {
			Assert.assertEquals("source account not found, id: source-account-id1", e.getMessage());
		} catch (Exception e) {
			Assert.fail("transaction must fail with AccountNotFoundException");
		}

		try {
			doMoneyTransfer(true,
					accountId -> {
						throw new AccountNotFoundException();
					},
					accountId -> new Account(accountId, Currency.getInstance("EUR")),
					new TransferMoneyRequest(
							"target-account-id1",
							"source-account-id1",
							1000
					));

			Assert.fail("transaction must fail with AccountNotFoundException");
		} catch (AccountNotFoundException e) {
			Assert.assertEquals("target account not found, id: target-account-id1", e.getMessage());
		} catch (Exception e) {
			Assert.fail("transaction must fail with AccountNotFoundException");
		}
	}

	void doMoneyTransfer(boolean hasFunds, AccountFactory targetFactory, AccountFactory sourceFactory, TransferMoneyRequest request) throws AccountNotFoundException, MismatchingCurrenciesException, TransactionInvalidException, InsufficientFundsException {
		AtomicReference<Account> sourceAccount = new AtomicReference<>();
		AtomicReference<Account> targetAccount = new AtomicReference<>();
		accountService.hasFundsFn = (accountId, amount) -> {
			Assert.assertEquals(request.getSourceAccountId(), accountId);
			Assert.assertEquals(request.getAmount(), amount);
			return hasFunds;
		};

		final AccountRepositoryMock.GetFunction getTargetAccount = (String accountId) -> {
			Assert.assertEquals(request.getTargetAccountId(), accountId);
			targetAccount.set(targetFactory.create(accountId));
			return targetAccount.get();
		};

		accountRepository.getFn = accountId -> {
			Assert.assertEquals(request.getSourceAccountId(), accountId);
			accountRepository.getFn = getTargetAccount;
			sourceAccount.set(sourceFactory.create(accountId));
			return sourceAccount.get();
		};

		final Function<Transaction, Object> targetTransaction = transaction -> {
			Assert.assertNotEquals(null, transaction.getId());
			Assert.assertEquals(request.getTargetAccountId(), transaction.getAccountId());
			Assert.assertEquals(request.getAmount(), transaction.getAmount());
			Assert.assertEquals(targetAccount.get().getCurrency(), transaction.getCurrency());
			return null;
		};
		transactionRepository.createFn = transaction -> {
			Assert.assertNotEquals(null, transaction.getId());
			Assert.assertEquals(request.getSourceAccountId(), transaction.getAccountId());
			Assert.assertEquals(request.getAmount() * -1, transaction.getAmount());
			Assert.assertEquals(sourceAccount.get().getCurrency(), transaction.getCurrency());
			transactionRepository.createFn = targetTransaction;
			return null;
		};
		transferService.transferMoney(request);
	}

	interface AccountFactory {
		Account create(String accountId) throws AccountNotFoundException;
	}
}