package com.danielvladco.money.transfer.transaction;

import com.danielvladco.money.transfer.mocks.TransactionRepositoryMock;
import com.danielvladco.money.transfer.transaction.exceptions.TransactionNotFoundException;
import com.danielvladco.money.transfer.transaction.models.Transaction;
import com.danielvladco.platform.common.HttpUtils;
import com.google.gson.Gson;
import io.undertow.Undertow;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Currency;
import java.util.List;

public class TransactionHttpHandlerTest {

	private static Undertow server;
	private static int serverPort;
	private static OkHttpClient httpClient = new OkHttpClient();
	private static Gson gson = new Gson();
	private static TransactionRepositoryMock transactionRepositoryMock = new TransactionRepositoryMock();

	@BeforeClass
	public static void beforeClass() throws IOException {
		// get an unused port
		ServerSocket s = new ServerSocket(0);
		s.close();
		serverPort = s.getLocalPort();
		server = Undertow.builder()
				.addHttpListener(s.getLocalPort(), "0.0.0.0",
						HttpUtils.threadDispatcherHandler(
								HttpUtils.loggerHandler(
										new TransactionHttpHandler(
												transactionRepositoryMock
										).makeHandler())))
				.build();
		server.start();
	}

	@Test
	public void get() throws IOException {
		var expectTransactionId = "transaction-id1";
		var transaction = new Transaction(expectTransactionId, "account-id", 100, Currency.getInstance("USD"));

		transactionRepositoryMock.getFn = transactionId -> {
			Assert.assertEquals(expectTransactionId, transactionId);
			return transaction;
		};
		var res = httpClient.newCall(
				new Request.Builder()
						.url(String.format("http://localhost:%d/transaction/%s", serverPort, expectTransactionId))
						.get()
						.build()).execute();

		Assert.assertTrue(res.isSuccessful());
		assert res.body() != null;
		Assert.assertEquals(String.format("{\"data\":%s,\"success\":true}", gson.toJson(transaction)), res.body().string());
	}

	@Test
	public void getAll() throws IOException {
		var transactions = List.of(
				new Transaction("transaction-id1", "account-id1", 100, Currency.getInstance("USD")),
				new Transaction("transaction-id2", "account-id1", 100, Currency.getInstance("USD")),
				new Transaction("transaction-id3", "account-id2", 100, Currency.getInstance("USD"))
		);

		transactionRepositoryMock.getAllFn = none -> transactions;
		var res = httpClient.newCall(
				new Request.Builder()
						.url(String.format("http://localhost:%d/transaction", serverPort))
						.get()
						.build()).execute();

		Assert.assertTrue(res.isSuccessful());
		assert res.body() != null;
		Assert.assertEquals(String.format("{\"data\":%s,\"success\":true}", gson.toJson(transactions)), res.body().string());
	}

	@Test
	public void handleException() throws IOException {
		transactionRepositoryMock.getFn = id -> {
			throw new TransactionNotFoundException();
		};
		var res = httpClient.newCall(
				new Request.Builder()
						.url(String.format("http://localhost:%d/transaction/transaction-id", serverPort))
						.get()
						.build()).execute();

		Assert.assertEquals(400, res.code());
		assert res.body() != null;
		Assert.assertEquals("{\"data\":{\"code\":\"transaction_not_found\",\"message\":\"transaction not found\"},\"success\":false}", res.body().string());
	}

	@AfterClass
	public static void afterClass() {
		server.stop();
	}
}