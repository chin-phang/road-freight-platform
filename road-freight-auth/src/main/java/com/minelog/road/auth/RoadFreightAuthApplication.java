package com.minelog.road.auth;

import com.minelog.road.auth.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {
    "com.minelog.shared.common",
    "com.minelog.road.auth"
})
@EnableConfigurationProperties(JwtProperties.class)
public class RoadFreightAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoadFreightAuthApplication.class, args);
	}

}
