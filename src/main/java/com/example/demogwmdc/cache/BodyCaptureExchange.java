package com.example.demogwmdc.cache;

import java.nio.charset.StandardCharsets;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BodyCaptureExchange extends ServerWebExchangeDecorator {

	private CaptureRequest req;

	private CaptureResponse res;

	public BodyCaptureExchange(ServerWebExchange exchange) {
		super(exchange);
		this.req = new CaptureRequest(exchange.getRequest());
		this.res = new CaptureResponse(exchange.getResponse());
	}

	@Override
	public CaptureRequest getRequest() {
		return this.req;
	}

	@Override
	public CaptureResponse getResponse() {
		return this.res;
	}

	public static class CaptureRequest extends ServerHttpRequestDecorator {

		private final StringBuilder body = new StringBuilder();

		CaptureRequest(ServerHttpRequest request) {
			super(request);
		}

		@Override
		public Flux<DataBuffer> getBody() {
			return super.getBody().doOnNext(this::capture);
		}

		private void capture(DataBuffer buffer) {
			this.body.append(StandardCharsets.UTF_8.decode(buffer.asByteBuffer()).toString());
		}

		public String getFullBody() {
			return this.body.toString();
		}

	}

	public static class CaptureResponse extends ServerHttpResponseDecorator {

		private final StringBuilder body = new StringBuilder();

		CaptureResponse(ServerHttpResponse response) {
			super(response);
		}

		@Override
		public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
			Flux<DataBuffer> buffer = Flux.from(body);
			return super.writeWith(buffer.doOnNext(this::capture));
		}

		private void capture(DataBuffer buffer) {
			this.body.append(StandardCharsets.UTF_8.decode(buffer.asByteBuffer()).toString());
		}

		public String getFullBody() {
			return this.body.toString();
		}

	}

}
