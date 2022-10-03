package com.example.demogwmdc.service;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SampleService {

	public Mono<?> hello(String s) {
		log.info("---Try hello. input: {}", s);
		// @formatter:off
        return Mono.justOrEmpty(s)
        .flatMap(a -> Mono.just((a.toUpperCase() + " add from the Service.")))
        .flatMap(b -> doit().then(Mono.just(b)));
        // @formatter:on
	}

	private Mono<?> doit() {
		log.info("Try doit");
		// @formatter:off
        return Flux.fromIterable(Arrays.asList("A", "B", "C"))
            .flatMap(s -> Mono.just(s.toLowerCase()))
            .collectList()
            .doOnNext(rs -> log.info("doit rs: {}", rs));
        // @formatter:on
	}

}
