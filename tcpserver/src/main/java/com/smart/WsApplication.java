package com.smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * WsServer启动入口
 *
 * @author chenjunlong
 */
@SpringBootApplication(scanBasePackages = {"com.smart.ws", "com.smart.api", "com.smart.server", "com.smart.biz"})
public class WsApplication {

    public static void main(String[] args) {
        SpringApplication.run(WsApplication.class);
    }

}
