package com.danielvladco.money.transfer.account;

import com.danielvladco.money.transfer.account.exceptions.AccountNotFoundException;
import com.danielvladco.money.transfer.account.models.Account;
import com.danielvladco.money.transfer.mocks.AccountRepositoryMock;
import com.danielvladco.money.transfer.mocks.AccountServiceMock;
import com.danielvladco.money.transfer.mocks.TransactionRepositoryMock;
import com.danielvladco.money.transfer.transaction.models.Transaction;
import com.danielvladco.platform.common.HttpUtils;
import com.google.gson.Gson;
import io.undertow.Undertow;
import okhttp3.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Currency;
import java.util.List;

public class AccountHttpHandlerTest {

	private static Undertow server;
	private static int serverPort;
	private static OkHttpClient httpClient = new OkHttpClient();
	private static Gson gson = new Gson();
	private static AccountRepositoryMock accountRepositoryMock = new AccountRepositoryMock();
	private static AccountServiceMock accountServiceMock = new AccountServiceMock();
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
										new AccountHttpHandler(
												accountRepositoryMock,
												accountServiceMock,
												transactionRepositoryMock
										).makeHandler())))
				.build();
		server.start();
	}

	@Test
	public void create() throws IOException {
		var accountId = "fake-id";
		var currency = "USD";
		accountRepositoryMock.createFn = account -> {
			Assert.assertEquals(accountId, account.getId());
			Assert.assertEquals(Currency.getInstance(currency), account.getCurrency());
			return null;
		};

		var res = httpPost("/account",
				String.format("{\"id\": \"%s\",\"currency\": \"%s\"}", accountId, currency));

		Assert.assertTrue(res.isSuccessful());
		assert res.body() != null;
		Assert.assertEquals("{\"success\":true}", res.body().string());
	}

	@Test
	public void get() throws IOException {
		var expectAccountId = "fake-id";
		var account = new Account(expectAccountId, Currency.getInstance("USD"));
		accountRepositoryMock.getFn = accountId -> {
			Assert.assertEquals(expectAccountId, accountId);
			return account;
		};

		var res = httpGet("/account/" + expectAccountId);

		Assert.assertTrue(res.isSuccessful());
		assert res.body() != null;
		Assert.assertEquals(String.format("{\"data\":%s,\"success\":true}", gson.toJson(account)), res.body().string());
	}

	@Test
	public void getAll() throws IOException {
		var accounts = List.of(
				new Account("fake-id1", Currency.getInstance("USD")),
				new Account("fake-id2", Currency.getInstance("USD"))
		);
		accountRepositoryMock.getAllFn = none -> accounts;

		var res = httpGet("/account");

		Assert.assertTrue(res.isSuccessful());
		assert res.body() != null;
		Assert.assertEquals(String.format("{\"data\":%s,\"success\":true}", gson.toJson(accounts)), res.body().string());
	}

	@Test
	public void getBalance() throws IOException {
		var balance = 100;
		var expectedAccountId = "fake-id";
		accountServiceMock.getBalanceFn = accountId -> {
			Assert.assertEquals(expectedAccountId, accountId);
			return (long) balance;
		};
		var res = httpGet(String.format("/account/%s/balance", expectedAccountId));
		Assert.assertTrue(res.isSuccessful());
		assert res.body() != null;
		Assert.assertEquals(String.format("{\"data\":%d,\"success\":true}", balance), res.body().string());
	}

	@Test
	public void userTransactions() throws IOException {
		var expectedAccountId = "fake-account-id1";
		var transactions = List.of(
				new Transaction("fake-id1", expectedAccountId, 100, Currency.getInstance("USD")),
				new Transaction("fake-id2", expectedAccountId, -100, Currency.getInstance("USD"))
		);
		transactionRepositoryMock.getByAccountIdFn = accountId -> {
			Assert.assertEquals(expectedAccountId, accountId);
			return transactions;
		};

		var res = httpGet(String.format("/account/%s/transactions", expectedAccountId));

		Assert.assertTrue(res.isSuccessful());
		assert res.body() != null;
		Assert.assertEquals(String.format("{\"data\":%s,\"success\":true}", gson.toJson(transactions)), res.body().string());
	}

	@Test
	public void handleException() throws IOException {
		accountRepositoryMock.getFn = id -> {
			throw new AccountNotFoundException();
		};
		var res = httpGet("/account/account-id");

		Assert.assertEquals(res.code(), 400);
		assert res.body() != null;
		Assert.assertEquals("{\"data\":{\"message\":\"account not found\",\"code\":\"account_not_found\"},\"success\":false}", res.body().string());
	}

	private Response httpPost(String urlPath, String jsonBody) throws IOException {
		var request = new Request.Builder()
				.url(String.format("http://localhost:%d%s", serverPort, urlPath))
				.method("POST", RequestBody.create(MediaType.parse("application/json"), jsonBody))
				.addHeader("Content-Type", "application/json")
				.build();

		return httpClient.newCall(request).execute();
	}

	private Response httpGet(String urlPath) throws IOException {
		var request = new Request.Builder()
				.url(String.format("http://localhost:%d%s", serverPort, urlPath))
				.method("GET", null)
				.build();

		return httpClient.newCall(request).execute();
	}

	@AfterClass
	public static void afterClass() {
		server.stop();
	}
}