package com.EdumentumBackend.EdumentumBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EdumentumBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdumentumBackendApplication.class, args);
	}

}
