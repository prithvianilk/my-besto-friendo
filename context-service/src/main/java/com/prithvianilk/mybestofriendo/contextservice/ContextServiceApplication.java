package com.prithvianilk.mybestofriendo.contextservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@SpringBootApplication
public class ContextServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContextServiceApplication.class, args);
	}

	@Bean
	public Clock clock() {
		return Clock.systemDefaultZone();
	}
}
