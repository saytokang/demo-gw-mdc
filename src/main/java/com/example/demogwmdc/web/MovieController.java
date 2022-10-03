package com.example.demogwmdc.web;

import java.time.Duration;
import java.util.stream.Stream;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/movies")
public class MovieController {
    Stream<String> movies = Stream.of("범죄도시", "기생충", "비상상황", "우영우");

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<?> get() {
        log.info("----- call movie get");

        // @formatter:off
        return Flux.fromStream(movies)
        .doOnSubscribe(s -> log.info("streaming begin", s))
        .delayElements(Duration.ofSeconds(1))
        .doOnNext(m -> log.info("playing {}", m));
        // @formatter:on
    }
}
