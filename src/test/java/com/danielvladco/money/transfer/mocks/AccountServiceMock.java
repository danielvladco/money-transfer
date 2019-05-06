package com.danielvladco.money.transfer.mocks;

import com.danielvladco.money.transfer.account.AccountService;

import java.util.function.Function;

public class AccountServiceMock implements AccountService {
	public Function<String, Long> getBalanceFn;
	public HasFundsFunction hasFundsFn;

	@Override
	public long getBalance(String accountId) {
		return getBalanceFn.apply(accountId);
	}

	@Override
	public boolean hasFunds(String accountId, long amount) {
		return hasFundsFn.apply(accountId, amount);
	}

	@FunctionalInterface
	public interface HasFundsFunction {
		boolean apply(String accountId, long amount);
	}
}
