package com.example.demogwmdc.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.example.demogwmdc.service.SampleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class BizFilter implements GlobalFilter, Ordered {

	private final SampleService sampleService;

	@Override
	public int getOrder() {
		return 10;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.info("biz Try: ");
		String url = exchange.getRequest().getPath().toString();
		return sampleService.hello(url).then(chain.filter(exchange));
	}

}
