package com.example.demogwmdc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	@Bean
	public ReactiveRedisOperations<String, String> stringReactiveRedisTemplate(
			ReactiveRedisConnectionFactory connection) {
		// @formatter:off
		RedisSerializationContext<String, String> context = RedisSerializationContext
				.<String, String>newSerializationContext(new StringRedisSerializer())
				.hashKey(new StringRedisSerializer())
				.hashValue(new StringRedisSerializer())
				.build();
		// @formatter:on
		return new ReactiveRedisTemplate<>(connection, context);
	}

}
