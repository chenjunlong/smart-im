package com.smart.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chenjunlong
 */
@SpringBootApplication(scanBasePackages = {"com.smart.server", "com.smart.service"})
public class BootStrapApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootStrapApplication.class);
    }
}
