package com.example.demogwmdc.service;

import java.time.Duration;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisService {

	private final ReactiveStringRedisTemplate redisTemplate;

	public Mono<?> save(String time) {
		String key = "time";
		return redisTemplate.opsForValue().set(key, time, Duration.ofMinutes(10))
				.doOnNext(rs -> log.info("redis execute rs: {}", rs))
				.contextWrite(ctx -> Context.of("CC", "123456789"));
	}

	public Mono<?> find(String key) {
		return redisTemplate.opsForValue().get(key).doOnNext(val -> log.info("find result: {}", val))
				.contextWrite(ctx -> Context.of("CC", "123456789"));
	}

}
