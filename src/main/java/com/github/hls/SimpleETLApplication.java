package com.github.hls;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SimpleETLApplication {
	public static void main(String[] args) {
		SpringApplication.run(SimpleETLApplication.class, args);
	}
}
