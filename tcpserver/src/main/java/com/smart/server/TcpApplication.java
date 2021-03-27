package com.smart.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * TcpServer启动入口
 *
 * @author chenjunlong
 */
@SpringBootApplication(scanBasePackages = {"com.smart.server", "com.smart.service"})
@RestController
public class TcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(TcpApplication.class);
    }

    @GetMapping(value = "/index", produces = MediaType.APPLICATION_JSON_VALUE)
    public String index() {
        return "running...";
    }
}
