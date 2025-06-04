package com.finsight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FinSightApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinSightApplication.class, args);
    }
} 