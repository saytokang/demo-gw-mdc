package com.example.demogwmdc.web;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demogwmdc.service.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/time")
@RequiredArgsConstructor
public class TimeController {

	private final RedisService redisService;

	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<?> time() {
		log.info("time function all()");
		return Flux.interval(Duration.ofSeconds(1))
				// .take(5)
				.map(i -> {
					log.info("seq: {}", i);
					return LocalDateTime.now();
				}).doOnSubscribe(s -> log.info("timer begin....")).doOnNext(time -> log.info("{}", time));
	}

	@PostMapping("/set")
	public Mono<?> post() {
		String time = String.format("current time: %s", LocalDateTime.now().toString());
		return redisService.save(time);
	}

	@GetMapping("/get")
	public Mono<?> get() {
		String key = "time";
		return redisService.find(key);
	}

}
