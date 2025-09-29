package com.EdumentumBackend.EdumentumBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableCaching
@EnableScheduling
@SpringBootApplication
public class EdumentumBackendApplication {

	public static void main(String[] args) {
		// Set timezone to UTC before Spring Boot starts to avoid PostgreSQL timezone issues
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		System.setProperty("user.timezone", "UTC");

		SpringApplication.run(EdumentumBackendApplication.class, args);
	}

}
