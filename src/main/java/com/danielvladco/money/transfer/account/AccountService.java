package com.danielvladco.money.transfer.account;

public interface AccountService {

	/**
	 * Gets user balance.
	 *
	 * @param accountId resolve balance for the account with accountId
	 * @return account balance
	 */
	long getBalance(String accountId);

	/**
	 * Checks if user has sufficient funds. Use before a transaction commit
	 *
	 * @param accountId account id
	 * @param amount    amount to be checked against
	 * @return boolean
	 */
	boolean hasFunds(String accountId, long amount);
}
