package com.naum.system.moneyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class MoneyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneyServiceApplication.class, args);
	}

}
