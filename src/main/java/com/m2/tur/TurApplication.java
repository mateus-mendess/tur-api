package com.m2.tur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TurApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurApplication.class, args);
	}

}
