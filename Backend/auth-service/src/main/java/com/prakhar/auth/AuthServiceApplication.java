package com.prakhar.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication(scanBasePackages = "com.prakhar")
@EnableDiscoveryClient
public class AuthServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner verifyEnv(Environment env) {
        return args -> {
            log.info("[Startup] INTERNAL_SERVICE_API_KEY from Env: {}",
                    env.getProperty("INTERNAL_SERVICE_API_KEY") != null ? "YES" : "NO — CHECK .ENV LOADING");
            log.info("[Startup] internal.service.api.key property from Env: {}",
                    env.getProperty("internal.service.api.key") != null ? "YES" : "NO");
        };
    }
}
