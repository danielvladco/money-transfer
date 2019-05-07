package com.danielvladco.money.transfer.account;

import com.danielvladco.money.transfer.account.exceptions.AccountInvalidException;
import com.danielvladco.money.transfer.account.exceptions.AccountNotFoundException;
import com.danielvladco.money.transfer.account.models.Account;

import java.util.List;

public interface AccountRepository {

	/**
	 * Create an account
	 *
	 * @param account account
	 * @throws AccountInvalidException account is invalid or has invalid properties
	 */
	void create(Account account) throws AccountInvalidException;

	/**
	 * Get account based of account id
	 *
	 * @param accountId account id
	 * @return account
	 * @throws AccountNotFoundException account is not found
	 */
	Account get(String accountId) throws AccountNotFoundException;

	/**
	 * Get all accounts
	 *
	 * @return list of accounts
	 */
	List<Account> getAll();
}
