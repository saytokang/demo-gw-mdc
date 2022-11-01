package com.example.demogwmdc.filter;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.example.demogwmdc.cache.BodyCaptureExchange;
import com.example.demogwmdc.config.MDCHandler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Slf4j
@Component
public class RouteLogGatewayFilter extends AbstractGatewayFilterFactory<RouteLogGatewayFilter.Config> {

	public RouteLogGatewayFilter() {
		super(Config.class);
	}

	public static class Config {

	}

	@Override
	public GatewayFilter apply(Config config) {
		return new OrderedGatewayFilter((exchange, chain) -> {
			String txId = MDC.get(MDCHandler.TX_ID);
			// MDC.put(MDCHandler.TX_ID, txId);
			exchange.getAttributes().put(MDCHandler.TX_ID, txId);

			BodyCaptureExchange captureExchange = new BodyCaptureExchange(exchange);
			// @formatter:off
			return chain.filter(captureExchange)
                        .doOnSuccess(s -> logging(captureExchange));
            // @formatter:on
		}, NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1);
	}

	private void logging(BodyCaptureExchange exchange) {
		// loggingRequestHeader(exchange.getRequest().getHeaders());
		loggingRequestBody(exchange.getRequest().getFullBody());
		loggingResponseHeader(exchange.getResponse().getHeaders());
		loggingResponseBody(exchange.getResponse().getFullBody());
	}

	private void loggingRequestBody(String content) {
		log.info("------- route request body -------");
		log.info("{}", content);
	}

	private void loggingRequestHeader(HttpHeaders headers) {
		log.info("------- route request headers -------");
		headers.forEach((k, v) -> log.debug("{}:{}", k, v));
	}

	private void loggingResponseBody(String content) {
		log.info("------- route response body -------");
		log.info("{}", content);

		// @formatter:off
		Mono.just(content)
			.map(String::toUpperCase)
			.doOnNext(s -> log.info("{}", s))
			.contextWrite(ctx -> ctx.put(MDCHandler.TX_ID, MDC.get(MDCHandler.TX_ID)))
			.contextWrite(ctx -> ctx.put("CC", "123456789"))
			.subscribe();
		// @formatter:on

	}

	private void loggingResponseHeader(HttpHeaders headers) {
		log.info("------- route response headers -------");
		headers.forEach((k, v) -> log.debug("{}:{}", k, v));
	}

	@Override
	public String name() {
		return "RouteLog";
	}

}
