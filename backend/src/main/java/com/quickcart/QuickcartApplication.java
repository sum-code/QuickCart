package com.quickcart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class QuickcartApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuickcartApplication.class, args);
	}

}
