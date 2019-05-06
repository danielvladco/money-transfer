package com.danielvladco.money.transfer.transfer;

import com.danielvladco.money.transfer.account.exceptions.AccountNotFoundException;
import com.danielvladco.money.transfer.transaction.exceptions.TransactionInvalidException;
import com.danielvladco.money.transfer.transfer.exceptions.InsufficientFundsException;
import com.danielvladco.money.transfer.transfer.exceptions.MismatchingCurrenciesException;
import com.danielvladco.money.transfer.transfer.exceptions.TransferToOneselfException;
import com.danielvladco.money.transfer.transfer.models.TransferMoneyRequest;
import com.danielvladco.platform.common.HttpUtils;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.ExceptionHandler;

import java.util.Map;

public class TransferHttpHandler {

	private TransferService transferService;

	public TransferHttpHandler(TransferService transferService) {
		this.transferService = transferService;
	}

	public RoutingHandler makeHandler() {
		var router = new RoutingHandler();
		router.post("/transfer", exceptionHandlers(this::transferMoney));
		return router;
	}

	private void transferMoney(HttpServerExchange exchange) throws InsufficientFundsException, AccountNotFoundException,
			TransactionInvalidException, MismatchingCurrenciesException, TransferToOneselfException {
		var request = HttpUtils.parseJson(exchange, TransferMoneyRequest.class);
		transferService.transferMoney(request);
		HttpUtils.sendJson(exchange, 201, null);
	}

	private HttpHandler exceptionHandlers(HttpHandler next) {
		return Handlers.exceptionHandler(next)
				.addExceptionHandler(InsufficientFundsException.class, handleException("insufficient_funds"))
				.addExceptionHandler(MismatchingCurrenciesException.class, handleException("mismatching_currencies"))
				.addExceptionHandler(AccountNotFoundException.class, handleException("account_not_found"))
				.addExceptionHandler(TransactionInvalidException.class, handleException("transaction_invalid"))
				.addExceptionHandler(TransferToOneselfException.class, handleException("transfer_to_oneself"));
	}

	private HttpHandler handleException(String code) {
		return exchange -> HttpUtils.sendJson(exchange, 400, Map.of(
				"code", code,
				"message", exchange.getAttachment(ExceptionHandler.THROWABLE).getMessage()
		));
	}
}
