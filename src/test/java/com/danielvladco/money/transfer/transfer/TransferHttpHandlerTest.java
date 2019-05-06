package com.danielvladco.money.transfer.transfer;

import com.danielvladco.money.transfer.mocks.TransferServiceMock;
import com.danielvladco.money.transfer.transfer.exceptions.MismatchingCurrenciesException;
import com.danielvladco.money.transfer.transfer.models.TransferMoneyRequest;
import com.danielvladco.platform.common.HttpUtils;
import com.google.gson.Gson;
import io.undertow.Undertow;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Currency;

public class TransferHttpHandlerTest {

	private static Undertow server;
	private static int serverPort;
	private static OkHttpClient httpClient = new OkHttpClient();
	private static Gson gson = new Gson();
	private static TransferServiceMock transferServiceMock = new TransferServiceMock();

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
										new TransferHttpHandler(
												transferServiceMock
										).makeHandler())))
				.build();
		server.start();
	}

	@Test
	public void transferMoney() throws IOException {
		var expectRequest = new TransferMoneyRequest(
				"target-account-id",
				"source-account-id",
				100
		);

		transferServiceMock.transferMoneyFn = req -> {
			Assert.assertEquals(expectRequest.getAmount(), req.getAmount());
			Assert.assertEquals(expectRequest.getSourceAccountId(), req.getSourceAccountId());
			Assert.assertEquals(expectRequest.getTargetAccountId(), req.getTargetAccountId());
		};
		var res = httpClient.newCall(
				new Request.Builder()
						.url(String.format("http://localhost:%d/transfer", serverPort))
						.post(RequestBody.create(MediaType.parse("application/json"), gson.toJson(expectRequest)))
						.build()).execute();

		Assert.assertTrue(res.isSuccessful());
		assert res.body() != null;
		Assert.assertEquals("{\"success\":true}", res.body().string());
	}

	@Test
	public void handleException() throws IOException {
		transferServiceMock.transferMoneyFn = req -> {
			throw new MismatchingCurrenciesException(Currency.getInstance("USD"), Currency.getInstance("EUR"));
		};
		var res = httpClient.newCall(
				new Request.Builder()
						.url(String.format("http://localhost:%d/transfer", serverPort))
						.post(RequestBody.create(MediaType.parse("application/json"), "{}"))
						.build()).execute();

		Assert.assertEquals(400, res.code());
		assert res.body() != null;
		Assert.assertEquals("{\"data\":{\"code\":\"mismatching_currencies\",\"message\":\"mismatching currencies, wanted: USD received: EUR\"},\"success\":false}", res.body().string());
	}

	@AfterClass
	public static void afterClass() {
		server.stop();
	}
}