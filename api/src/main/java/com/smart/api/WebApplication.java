package com.smart.api;

import com.smart.api.intercepter.ratelimit.RateLimiterRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author chenjunlong
 */
@SpringBootApplication(scanBasePackages = {"com.smart.api", "com.smart.service"})
@Import(RateLimiterRegistrar.class)
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
