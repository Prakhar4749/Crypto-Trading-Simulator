package com.prakhar.coretrading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.prakhar")
@EnableDiscoveryClient
@EnableFeignClients
public class CoreTradingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoreTradingServiceApplication.class, args);
    }
}
