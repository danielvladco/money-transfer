package com.danielvladco.money.transfer.transfer;

import com.danielvladco.money.transfer.account.exceptions.AccountNotFoundException;
import com.danielvladco.money.transfer.transaction.exceptions.TransactionInvalidException;
import com.danielvladco.money.transfer.transfer.exceptions.InsufficientFundsException;
import com.danielvladco.money.transfer.transfer.exceptions.MismatchingCurrenciesException;
import com.danielvladco.money.transfer.transfer.exceptions.TransferToOneselfException;
import com.danielvladco.money.transfer.transfer.models.TransferMoneyRequest;

public interface TransferService {

	/**
	 * Transfers money between two accounts.
	 *
	 * @param request receive source account, target account, amount of money and explicit currency
	 * @throws AccountNotFoundException if one of the source or target account is not found
	 * @throws InsufficientFundsException user does not have sufficient funds
	 * @throws TransactionInvalidException for some reason unable to create transaction
	 * @throws MismatchingCurrenciesException currently does not support currency exchange
	 */
	void transferMoney(TransferMoneyRequest request) throws AccountNotFoundException,
			InsufficientFundsException,
			TransactionInvalidException,
			MismatchingCurrenciesException, TransferToOneselfException;
}
