package com.danielvladco.money.transfer.account;

import com.danielvladco.money.transfer.account.exceptions.AccountInvalidException;
import com.danielvladco.money.transfer.account.exceptions.AccountNotFoundException;
import com.danielvladco.money.transfer.account.models.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AccountRepositoryInMemory implements AccountRepository {
	private Map<String, Account> accounts = new ConcurrentHashMap<>();

	@Override
	public void create(Account account) throws AccountInvalidException {
		if (account == null) {
			throw new AccountInvalidException("unable to create null account");
		}
		if (account.getId() == null || account.getId().equals("")) {
			account.setId(UUID.randomUUID().toString());
		}
		if (account.getCurrency() == null) {
			throw new AccountInvalidException("unable to create account without currency");
		}

		if (accounts.containsKey(account.getId())) {
			return;
		}
		accounts.put(account.getId(), account);
	}

	@Override
	public Account get(String accountId) throws AccountNotFoundException {
		if (!accounts.containsKey(accountId)) {
			throw new AccountNotFoundException();
		}
		return accounts.get(accountId);
	}

	@Override
	public List<Account> getAll() {
		return new ArrayList<>(accounts.values());
	}

}
