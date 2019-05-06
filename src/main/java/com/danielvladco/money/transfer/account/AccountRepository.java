package com.danielvladco.money.transfer.account;

import com.danielvladco.money.transfer.account.exceptions.AccountInvalidException;
import com.danielvladco.money.transfer.account.exceptions.AccountNotFoundException;
import com.danielvladco.money.transfer.account.models.Account;

import java.util.List;

public interface AccountRepository {

	void create(Account account) throws AccountInvalidException;

	Account get(String accountId) throws AccountNotFoundException;

	List<Account> getAll();
}
