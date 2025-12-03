package com.minelog.road.gateway;

import com.minelog.road.gateway.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {
    "com.minelog.shared.common",
    "com.minelog.road.gateway"
})
@EnableConfigurationProperties(JwtProperties.class)
public class RoadFreightGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoadFreightGatewayApplication.class, args);
	}

}
