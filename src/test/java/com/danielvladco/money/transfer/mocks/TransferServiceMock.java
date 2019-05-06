package com.danielvladco.money.transfer.mocks;

import com.danielvladco.money.transfer.account.exceptions.AccountNotFoundException;
import com.danielvladco.money.transfer.transaction.exceptions.TransactionInvalidException;
import com.danielvladco.money.transfer.transfer.TransferService;
import com.danielvladco.money.transfer.transfer.exceptions.InsufficientFundsException;
import com.danielvladco.money.transfer.transfer.exceptions.MismatchingCurrenciesException;
import com.danielvladco.money.transfer.transfer.models.TransferMoneyRequest;

public class TransferServiceMock implements TransferService {
	public TransferMoneyFunction transferMoneyFn;
	@Override
	public void transferMoney(TransferMoneyRequest request) throws AccountNotFoundException, InsufficientFundsException, TransactionInvalidException, MismatchingCurrenciesException {
		transferMoneyFn.apply(request);
	}

	@FunctionalInterface
	public interface TransferMoneyFunction {
		void apply(TransferMoneyRequest req) throws AccountNotFoundException, InsufficientFundsException, TransactionInvalidException, MismatchingCurrenciesException;
	}
}
