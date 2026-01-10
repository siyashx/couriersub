package com.codesupreme.couriersub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CouriersubApplication {
	public static void main(String[] args) {
		SpringApplication.run(CouriersubApplication.class, args);
	}
}
