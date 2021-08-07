package com.smart.api;

import com.smart.server.tcp.channel.ChannelRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping(value = "/monitor/{op}", produces = MediaType.APPLICATION_JSON_VALUE)
    public long getConnections(@PathVariable String op) {
        if ("connections".equals(op)) {
            return ChannelRegistry.Connection.get();
        }
        if ("onlines".equals(op)) {
            return ChannelRegistry.getAllChannel().size();
        }
        return 0;
    }
}
