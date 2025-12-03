package com.minelog.road.job.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {
    "com.minelog.shared.common.domain",
    "com.minelog.road.job.domain"
})
@EnableJpaRepositories(basePackages = "com.minelog.road.job.repository")
public class JpaConfig {
}
