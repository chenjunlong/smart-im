package com.smart.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TcpServer启动入口
 *
 * @author chenjunlong
 */
@SpringBootApplication(scanBasePackages = {"com.smart.server", "com.smart.biz", "com.smart.api"})
public class TcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(TcpApplication.class);
    }

}
