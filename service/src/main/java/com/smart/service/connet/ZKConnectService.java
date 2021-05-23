package com.smart.service.connet;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Service;

/**
 * @author chenjunlong
 */
@Service
public class ZKConnectService implements ConnectService {

    @Resource(name = "smartImZkClient")
    private ZkClient zkClient;

    private static final String rootPath = "/tcp_server_node_address";

    @Override
    public Optional<Boolean> register(String address) {
        if (!zkClient.exists(rootPath)) {
            zkClient.create(rootPath, System.currentTimeMillis(), CreateMode.PERSISTENT);
        }

        String path = rootPath + "/" + address;
        if (!zkClient.exists(path)) {
            String result = zkClient.create(path, System.currentTimeMillis(), CreateMode.PERSISTENT);
            return StringUtils.isBlank(result) ? Optional.empty() : Optional.of(true);
        }
        return Optional.of(true);
    }

    @Override
    public Optional<Boolean> unregister(String address) {
        String path = rootPath + "/" + address;
        boolean result = zkClient.delete(path);
        return Optional.of(result);
    }

    @Override
    public List<String> getConnectAddress() {
        return zkClient.getChildren(rootPath);
    }
}
