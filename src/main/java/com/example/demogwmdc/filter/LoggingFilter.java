package com.example.demogwmdc.filter;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.example.demogwmdc.config.MDCHandler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

	@Override
	public int getOrder() {
		return -1;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.info(" pre request");

		ServerHttpResponse response = exchange.getResponse();
		response.beforeCommit(() -> {
			response.getHeaders().add(MDCHandler.TX_ID, MDC.get(MDCHandler.TX_ID));
			return Mono.empty();
		});

		return chain.filter(exchange.mutate().response(response).build())
				.then(Mono.fromRunnable(() -> log.info("after respoonse: mdc tx-id: {}", MDC.get(MDCHandler.TX_ID))));
	}

}
