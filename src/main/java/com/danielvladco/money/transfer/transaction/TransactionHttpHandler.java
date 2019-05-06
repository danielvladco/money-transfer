package com.danielvladco.money.transfer.transaction;

import com.danielvladco.money.transfer.transaction.exceptions.TransactionInvalidException;
import com.danielvladco.money.transfer.transaction.exceptions.TransactionNotFoundException;
import com.danielvladco.platform.common.HttpUtils;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;

public class TransactionHttpHandler {

	private TransactionRepository transactionRepository;

	public TransactionHttpHandler(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	public RoutingHandler makeHandler() {
		var router = new RoutingHandler();
		router.get("/transaction", exceptionHandlers(this::getAll));
		router.get("/transaction/{transactionId}", exceptionHandlers(this::get));
		return router;

	}

	private void get(HttpServerExchange exchange) throws Exception {
		var account = transactionRepository.get(HttpUtils.pathParam(exchange, "transactionId"));
		HttpUtils.sendJson(exchange, 200, account);
	}

	private void getAll(HttpServerExchange exchange) {
		var accounts = transactionRepository.getAll();
		HttpUtils.sendJson(exchange, 200, accounts);
	}

	private HttpHandler exceptionHandlers(HttpHandler next) {
		return Handlers.exceptionHandler(next)
				.addExceptionHandler(TransactionInvalidException.class, HttpUtils.sendException("transaction_invalid"))
				.addExceptionHandler(TransactionNotFoundException.class, HttpUtils.sendException("transaction_not_found"));
	}
}
