package com.danielvladco.money.transfer.transfer;

import com.danielvladco.money.transfer.account.AccountRepository;
import com.danielvladco.money.transfer.account.AccountService;
import com.danielvladco.money.transfer.account.exceptions.AccountNotFoundException;
import com.danielvladco.money.transfer.account.models.Account;
import com.danielvladco.money.transfer.transaction.TransactionRepository;
import com.danielvladco.money.transfer.transaction.exceptions.TransactionInvalidException;
import com.danielvladco.money.transfer.transaction.models.Transaction;
import com.danielvladco.money.transfer.transfer.exceptions.InsufficientFundsException;
import com.danielvladco.money.transfer.transfer.exceptions.MismatchingCurrenciesException;
import com.danielvladco.money.transfer.transfer.exceptions.TransferToOneselfException;
import com.danielvladco.money.transfer.transfer.models.TransferMoneyRequest;

import java.util.UUID;

public class TransferServiceImpl implements TransferService {
	private AccountRepository accountRepository;
	private AccountService accountService;
	private TransactionRepository transactionRepository;

	public TransferServiceImpl(AccountRepository accountRepository, AccountService accountService, TransactionRepository transactionRepository) {
		this.accountRepository = accountRepository;
		this.accountService = accountService;
		this.transactionRepository = transactionRepository;
	}

	@Override
	public void transferMoney(TransferMoneyRequest request) throws AccountNotFoundException, MismatchingCurrenciesException, TransactionInvalidException, InsufficientFundsException, TransferToOneselfException {
		Account sourceAccount;
		Account targetAccount;
		try {
			sourceAccount = accountRepository.get(request.getSourceAccountId());
		} catch (AccountNotFoundException e) {
			throw new AccountNotFoundException("source account not found, id: " + request.getSourceAccountId());
		}

		try {
			targetAccount = accountRepository.get(request.getTargetAccountId());
		} catch (AccountNotFoundException e) {
			throw new AccountNotFoundException("target account not found, id: " + request.getTargetAccountId());
		}

		if (sourceAccount.getId().equals(targetAccount.getId())) {
			throw new TransferToOneselfException();
		}

		// check if source account has the same currency as target account
		if (!targetAccount.getCurrency().equals(sourceAccount.getCurrency())) {
			throw new MismatchingCurrenciesException(sourceAccount.getCurrency(), targetAccount.getCurrency());
		}

		// check if account has sufficient funds
		if (!accountService.hasFunds(sourceAccount.getId(), request.getAmount())) {
			throw new InsufficientFundsException();
		}

		transactionRepository.create(
				// debit transaction for source account
				new Transaction(
						UUID.randomUUID().toString(),
						request.getSourceAccountId(),
						-request.getAmount(),
						sourceAccount.getCurrency()
				),
				// credit transaction for target account
				new Transaction(
						UUID.randomUUID().toString(),
						request.getTargetAccountId(),
						request.getAmount(),
						targetAccount.getCurrency()
				)
		);
	}
}
