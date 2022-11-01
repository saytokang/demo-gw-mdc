package com.example.demogwmdc.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.example.demogwmdc.config.MDCHandler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Slf4j
@Component
public class LastFilter implements GlobalFilter, Ordered {

	@Override
	public int getOrder() {
		return 100;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.info("LastFilter Try: ");
		String txId = (String) exchange.getAttributes().get(MDCHandler.TX_ID);

		return chain.filter(exchange)
				// .then(Mono.just(txId).contextWrite(ctx -> Context.of(MDCHandler.TX_ID,
				// txId)));
				.contextWrite(ctx -> Context.of(MDCHandler.TX_ID, txId));
	}

}
