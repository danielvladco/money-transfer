package com.danielvladco.money.transfer.mocks;

import com.danielvladco.money.transfer.account.AccountRepository;
import com.danielvladco.money.transfer.account.exceptions.AccountNotFoundException;
import com.danielvladco.money.transfer.account.models.Account;

import java.util.List;
import java.util.function.Function;

public class AccountRepositoryMock implements AccountRepository {

	public Function<Account, Object> createFn;
	public GetFunction getFn;
	public Function<Object, List<Account>> getAllFn;

	@Override
	public void create(Account account) {
		createFn.apply(account);
	}

	@Override
	public Account get(String accountId) throws AccountNotFoundException {
		return getFn.apply(accountId);
	}

	@Override
	public List<Account> getAll() {
		return getAllFn.apply(null);
	}

	@FunctionalInterface
	public interface GetFunction {
		Account apply(String accountId) throws AccountNotFoundException;
	}
}