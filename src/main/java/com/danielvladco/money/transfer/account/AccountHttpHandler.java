package com.danielvladco.money.transfer.account;

import com.danielvladco.money.transfer.account.exceptions.AccountInvalidException;
import com.danielvladco.money.transfer.account.exceptions.AccountNotFoundException;
import com.danielvladco.money.transfer.account.models.Account;
import com.danielvladco.money.transfer.transaction.TransactionRepository;
import com.danielvladco.platform.common.HttpUtils;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;

public class AccountHttpHandler {

	private AccountRepository accountRepository;

	private AccountService accountService;

	private TransactionRepository transactionRepository;

	public AccountHttpHandler(AccountRepository accountRepository, AccountService accountService, TransactionRepository transactionRepository) {
		this.accountRepository = accountRepository;
		this.accountService = accountService;
		this.transactionRepository = transactionRepository;
	}

	public RoutingHandler makeHandler() {
		var router = new RoutingHandler();
		router.post("/account", exceptionHandlers(this::create));
		router.get("/account", exceptionHandlers(this::getAll));
		router.get("/account/{accountId}", exceptionHandlers(this::get));
		router.get("/account/{accountId}/balance", exceptionHandlers(this::getBalance));
		router.get("/account/{accountId}/transactions", exceptionHandlers(this::accountTransactions));
		return router;

	}

	private void create(HttpServerExchange exchange) throws AccountInvalidException {
		var account = HttpUtils.parseJson(exchange, Account.class);
		accountRepository.create(account);
		HttpUtils.sendJson(exchange, 201, null);
	}

	private void get(HttpServerExchange exchange) throws Exception {
		var account = accountRepository.get(HttpUtils.pathParam(exchange, "accountId"));
		HttpUtils.sendJson(exchange, 200, account);
	}

	private void getAll(HttpServerExchange exchange) {
		var accounts = accountRepository.getAll();
		HttpUtils.sendJson(exchange, 200, accounts);
	}

	private void getBalance(HttpServerExchange exchange) {
		var balance = accountService.getBalance(HttpUtils.pathParam(exchange, "accountId"));
		HttpUtils.sendJson(exchange, 200, balance);
	}

	private void accountTransactions(HttpServerExchange exchange) {
		var balance = transactionRepository.getByAccountId(HttpUtils.pathParam(exchange, "accountId"));
		HttpUtils.sendJson(exchange, 200, balance);
	}

	private HttpHandler exceptionHandlers(HttpHandler next) {
		return Handlers.exceptionHandler(next)
				.addExceptionHandler(AccountInvalidException.class, HttpUtils.sendException("account_invalid"))
				.addExceptionHandler(AccountNotFoundException.class, HttpUtils.sendException("account_not_found"));
	}
}

