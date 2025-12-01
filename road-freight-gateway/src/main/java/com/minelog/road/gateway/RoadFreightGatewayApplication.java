package com.minelog.road.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.minelog.road.gateway",
    "com.minelog.shared.common",
})
public class RoadFreightGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoadFreightGatewayApplication.class, args);
	}

}
