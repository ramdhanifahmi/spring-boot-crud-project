package com.dev.fahmi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FahmiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FahmiApplication.class, args);
	}

}
