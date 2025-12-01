package com.minelog.road.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.minelog.road.job",
    "com.minelog.shared.common",
})
public class RoadFreightJobApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoadFreightJobApplication.class, args);
	}

}
