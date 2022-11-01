package com.example.demogwmdc.config;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.HttpHandlerDecoratorFactory;
import org.springframework.stereotype.Component;

import reactor.util.context.Context;

// @Configuration
@Component
public class MDCHandler implements HttpHandlerDecoratorFactory {

	public static final String TX_ID = "TX-ID";

	@Override
	public HttpHandler apply(HttpHandler handler) {
		// @formatter:off
        return (req, res) ->
                handler.handle(req, res)
                .contextWrite(ctx -> {
                    String txId = UUID.randomUUID().toString();
                    MDC.put(TX_ID, txId);                    
                    return Context.of(TX_ID, txId);
                });
        // @formatter:on
	}

}
