package com.victor.rinhabackend2025;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class Rinhabackend2025Application {

	public static void main(String[] args) {
		SpringApplication.run(Rinhabackend2025Application.class, args);
	}

}
