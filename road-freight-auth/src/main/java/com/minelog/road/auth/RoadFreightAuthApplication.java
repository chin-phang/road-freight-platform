package com.minelog.road.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.minelog.road.auth",
    "com.minelog.shared.common",
})
public class RoadFreightAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoadFreightAuthApplication.class, args);
	}

}
