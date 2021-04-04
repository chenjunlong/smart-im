package com.smart.api;

import com.smart.server.tcp.channel.ChannelRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenjunlong
 */
@Slf4j
@RestController
@RequestMapping("/manager")
public class ManagerController {

    @GetMapping(value = "/index", produces = MediaType.APPLICATION_JSON_VALUE)
    public String index() {
        return "running...";
    }

    @GetMapping(value = "/connections", produces = MediaType.APPLICATION_JSON_VALUE)
    public long getConnections() {
        return ChannelRegistry.getConnections();
    }
}
