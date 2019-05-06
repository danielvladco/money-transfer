package com.danielvladco.platform.common;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ExceptionHandler;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;

import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

public abstract class HttpUtils {

	private static Logger log = Logger.getLogger(HttpUtils.class.getName());

	private static Gson gson = new Gson();

	/**
	 * Dispatch handler to worker threads
	 *
	 * @param next execute the next http handler
	 * @return http handler wrapping Logger functionality
	 */
	public static HttpHandler threadDispatcherHandler(HttpHandler next) {
		return new HttpHandler() {
			@Override
			public void handleRequest(HttpServerExchange exchange) throws Exception {
				exchange.startBlocking();
				if (exchange.isInIoThread()) {
					exchange.dispatch(this);
					return;
				}

				next.handleRequest(exchange);
			}
		};
	}

	/**
	 * Log generic http info
	 *
	 * @param next execute the next http handler
	 * @return http handler wrapping Logger functionality
	 */
	public static HttpHandler loggerHandler(HttpHandler next) {
		return (HttpServerExchange exchange) -> {
			log.info(String.format("%s %s",
					exchange.getRequestMethod(),
					exchange.getRequestPath()));

			next.handleRequest(exchange);
		};
	}

	/**
	 * Enable CORS on the server
	 *
	 * @param next    execute the next http handler
	 * @param origins comma separated list of allowed origins
	 * @param methods comma separated list of allowed methods
	 * @param headers comma separated list of allowed headers
	 * @return http handler wrapping CORS functionality
	 */
	public static HttpHandler corsHandler(HttpHandler next, String origins, String methods, String headers) {
		return (httpExchange) -> {
			httpExchange.getResponseHeaders().add(new HttpString("Access-Control-Allow-Origin"), origins);
			if (httpExchange.getRequestMethod().equals(Methods.OPTIONS)) {
				httpExchange.getResponseHeaders()
						.add(new HttpString("Access-Control-Allow-Methods"), methods);
				httpExchange.getResponseHeaders()
						.add(new HttpString("Access-Control-Allow-Headers"), headers);
				httpExchange.setStatusCode(204);
				return;
			}

			next.handleRequest(httpExchange);
		};
	}

	/**
	 * Sends a response with the payload encoded as json
	 *
	 * @param exchange   write the bytes to output stream as json and write the headers
	 * @param statusCode response code
	 * @param payload    the data that needs to be serialized as json
	 */
	public static void sendJson(HttpServerExchange exchange, int statusCode, Object payload) {
		exchange.setStatusCode(statusCode);
		var response = new LinkedHashMap<>();
		response.put("data", payload);
		if (statusCode > 308) {
			response.put("success", false);
		} else {
			response.put("success", true);
		}
		exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
		exchange.getResponseSender().send(ByteBuffer.wrap(gson.toJson(response).getBytes()));
	}

	/**
	 * Parse json from the request input stream
	 *
	 * @param exchange read the bytes from input stream
	 * @param classOfT the class of T
	 * @param <T>      the type of the desired object
	 * @return an object of type T from the string. Returns {@code null} if {@code json} is at EOF.
	 */
	public static <T> T parseJson(HttpServerExchange exchange, Class<T> classOfT) {
		return gson.fromJson(new InputStreamReader(exchange.getInputStream(), StandardCharsets.UTF_8), classOfT);
	}

	/**
	 * Returns a query query parameter.
	 *
	 * @param exchange get query parameters
	 * @return The query parameter
	 */
	public static String pathParam(HttpServerExchange exchange, String name) {
		if (exchange.getQueryParameters().get(name) != null) {
			return exchange.getQueryParameters().get(name).getFirst();
		}

		return "";
	}

	/**
	 * Sends a exception response as json with a code parameter
	 *
	 * @param code used do be able to identify the error for the api users
	 * @return http handler
	 */
	public static HttpHandler sendException(String code) {
		return exchange -> {
			var response = new LinkedHashMap<>();
			response.put("code", code);
			response.put("message", exchange.getAttachment(ExceptionHandler.THROWABLE).getMessage());
			HttpUtils.sendJson(exchange, 400, response);
		};
	}
}
