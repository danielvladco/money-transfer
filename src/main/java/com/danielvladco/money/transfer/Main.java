package com.danielvladco.money.transfer;

import com.danielvladco.money.transfer.account.AccountHttpHandler;
import com.danielvladco.money.transfer.account.AccountRepositoryInMemory;
import com.danielvladco.money.transfer.account.AccountServiceImpl;
import com.danielvladco.money.transfer.transaction.TransactionHttpHandler;
import com.danielvladco.money.transfer.transaction.TransactionRepositoryInMemory;
import com.danielvladco.money.transfer.transfer.TransferHttpHandler;
import com.danielvladco.money.transfer.transfer.TransferServiceImpl;
import com.danielvladco.platform.common.HttpUtils;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ExceptionHandler;

public class Main {
	public static void main(String[] args) {

		// service and repository definitions
		final var transactionRepository = new TransactionRepositoryInMemory();

		final var accountRepository = new AccountRepositoryInMemory();

		final var accountService = new AccountServiceImpl(transactionRepository);

		final var transferService = new TransferServiceImpl(accountRepository, accountService, transactionRepository);

		// http handlers
		final var transactionHttpHandler = new TransactionHttpHandler(transactionRepository);
		final var accountHttpHandler = new AccountHttpHandler(accountRepository, accountService, transactionRepository);
		final var transferHttpHandler = new TransferHttpHandler(transferService);

		final var pathHandler = Handlers.path(Main::notFoundHandler);
		pathHandler.addPrefixPath("/v1", accountHttpHandler.makeHandler());
		pathHandler.addPrefixPath("/v1", transactionHttpHandler.makeHandler());
		pathHandler.addPrefixPath("/v1", transferHttpHandler.makeHandler());

		// creates http handler and dispatches work in separate threads
		var handler = HttpUtils.threadDispatcherHandler(pathHandler);

		// wrap http handlers with generic exception handler
		handler = Handlers.exceptionHandler(handler)
				.addExceptionHandler(Throwable.class, Main::exceptionHandler)
				.addExceptionHandler(IllegalArgumentException.class, exc -> HttpUtils.sendJson(exc, 400, exc.getAttachment(ExceptionHandler.THROWABLE)));

		// wrap http handlers with logger (ex. GET /account)
		handler = HttpUtils.loggerHandler(handler);

		// wrap http handlers with CORS
		handler = HttpUtils.corsHandler(handler, "*", "GET, POST, OPTIONS", "Content-Type");

		// serve on any ip on port 8080
		Undertow.builder()
				.addHttpListener(8080, "0.0.0.0", handler)
				.build()
				.start();
	}

	private static void notFoundHandler(HttpServerExchange exc) {
		HttpUtils.sendJson(exc, 404, "resource not found");
	}

	private static void exceptionHandler(HttpServerExchange exc) {
		var ex = exc.getAttachment(ExceptionHandler.THROWABLE);
		ex.printStackTrace();
		HttpUtils.sendJson(exc, 500, "Internal server error");
	}
}
