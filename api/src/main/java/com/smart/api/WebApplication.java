package com.smart.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chenjunlong
 */
@SpringBootApplication(scanBasePackages = {"com.smart.api", "com.smart.biz"})
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
