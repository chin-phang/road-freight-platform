package com.minelog.road.gateway.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = {"com.minelog.shared.common.domain"})
public class JpaConfig {
}
