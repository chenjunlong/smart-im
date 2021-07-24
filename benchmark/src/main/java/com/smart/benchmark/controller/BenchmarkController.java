package com.smart.benchmark.controller;

import java.util.*;

import com.google.gson.JsonParser;
import com.smart.benchmark.netty.NettyClient;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

/**
 * @author chenjunlong
 */
@Slf4j
@RestController
@RequestMapping("/benchmark")
public class BenchmarkController {


    private static Set<NettyClient> clientHolder = new HashSet<>();


    /**
     * curl 'http://localhost:8002/v1/smart-im/benchmark/virtual_connect?nums=100&roomId=room1001'
     */
    @GetMapping(value = "/virtual_connect", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> virtualConnect(@RequestParam(value = "nums") int nums, @RequestParam(value = "roomId") String roomId) throws Exception {

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        String response = restTemplate.getForObject("http://10.211.163.123:8000/v1/smart-im/dispatch/connect_address", String.class);


        String address = JsonParser.parseString(response).getAsJsonObject().getAsJsonArray("body").get(0).getAsString();
        String ip = address.split(":")[0];
        int port = Integer.parseInt(address.split(":")[1]);


        int suc = 0;
        for (int i = 0; i < nums; i++) {

            NettyClient nettyClient = new NettyClient(ip, port, roomId, RandomUtils.nextLong());
            nettyClient.start();

            clientHolder.add(nettyClient);
            suc++;
        }


        Map<String, Object> map = new HashMap<>(16);
        map.put("state", "ok");
        map.put("nums", suc);
        return map;
    }

    /**
     * curl 'http://localhost:8002/v1/smart-im/benchmark/virtual_disconnect'
     */
    @GetMapping(value = "/virtual_disconnect", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> virtualDisConnect() {

        int suc = 0;
        for (NettyClient nettyClient : clientHolder) {
            nettyClient.stop();
            suc++;
        }

        Map<String, Object> map = new HashMap<>(16);
        map.put("state", "ok");
        map.put("nums", suc);

        return map;
    }

}
