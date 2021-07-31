package com.smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TcpServer启动入口
 *
 * @author chenjunlong
 */
@SpringBootApplication(scanBasePackages = {"com.smart.tcp", "com.smart.api", "com.smart.server", "com.smart.biz"})
public class TcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(TcpApplication.class);
    }

}
