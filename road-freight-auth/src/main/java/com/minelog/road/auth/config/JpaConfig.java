package com.minelog.road.auth.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {
    "com.minelog.shared.common.domain",
    "com.minelog.road.auth.domain"
})
@EnableJpaRepositories(basePackages = "com.minelog.road.auth.repository")
public class JpaConfig {
}
