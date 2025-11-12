package br.com.grape.accessmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AccessManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccessManagerApplication.class, args);
	}

}