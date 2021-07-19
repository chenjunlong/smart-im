package com.smart.service.registry.impl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
@Service("zkRegistry")
public class ZKRegistry extends AbstractRegistry {

    private static final Gson gson = new Gson();

    // tcp_server 注册到zk_server 的根结点
    private static final String rootPath = "/tcp_server_node_address";

    // key => 服务节点IP, value => 心跳上报时间
    private static Map<String, Long> localRegistryMap = new ConcurrentHashMap<>();

    // 记录创建过的监听器
    private static Set<String> watchRunnableRegistry = new HashSet<>();


    @Resource(name = "smartImZkClient")
    private ZkClient zkClient;


    @PostConstruct
    private void init() {
        this.initZkData();
        this.watchZkData();
    }

    @Override
    public Optional<Boolean> register(String subPath) {
        if (!zkClient.exists(rootPath)) {
            long data = System.currentTimeMillis();
            zkClient.create(rootPath, data, CreateMode.PERSISTENT);
        }

        String path = this.serverPath(subPath);
        if (!zkClient.exists(path)) {
            long lastUpdateTime = System.currentTimeMillis();
            String result = zkClient.create(path, lastUpdateTime, CreateMode.PERSISTENT);
            return StringUtils.isBlank(result) ? Optional.empty() : Optional.of(true);
        }
        return Optional.of(true);
    }

    @Override
    public Optional<Boolean> unregister(String subPath) {
        String path = this.serverPath(subPath);
        boolean result = zkClient.delete(path);
        return Optional.of(result);
    }

    @Override
    public List<String> getConnectAddress() {
        return localRegistryMap.keySet().stream().collect(Collectors.toList());
    }

    @Override
    public void beatHeart(String subPath) {
        String path = this.serverPath(subPath);
        long lastUpdateTime = System.currentTimeMillis();
        zkClient.writeData(path, lastUpdateTime);
    }

    /**
     * 服务注册目录
     * 
     * @param server
     * @return
     */
    private String serverPath(String server) {
        return StringUtils.joinWith("/", rootPath, server);
    }

    /**
     * 服务初始化，拉取zk服务注册表到本地
     */
    private void initZkData() {
        List<String> subPathList = zkClient.getChildren(rootPath);
        for (String subPath : subPathList) {
            String path = rootPath + "/" + subPath;
            long lastUpdateTime = zkClient.readData(path);
            localRegistryMap.putIfAbsent(subPath, lastUpdateTime);
            addRemoveListener(path);
        }
    }

    /**
     * 服务初始化，创建服务注册表变更监听
     */
    private void watchZkData() {
        zkClient.subscribeChildChanges(rootPath, new IZkChildListener() {

            @Override
            public void handleChildChange(String parentPath, List<String> subPathList) {
                log.info("ZKRegistry => handleChildChange, parentPath:{}, subPathList:{}", parentPath, gson.toJson(subPathList));

                for (String subPath : subPathList) {

                    String path = parentPath + "/" + subPath;
                    long lastUpdateTime = System.currentTimeMillis();

                    if (!localRegistryMap.containsKey(subPath)) {
                        localRegistryMap.put(subPath, lastUpdateTime);
                    } else {
                        long localLastUpdateTime = localRegistryMap.get(subPath);
                        if (lastUpdateTime > localLastUpdateTime) {
                            localRegistryMap.put(subPath, lastUpdateTime);
                        }
                    }

                    // 给子节点添加节点移除事件
                    addRemoveListener(path);
                }

            }
        });

    }

    /**
     * 为服务创建变更事件
     * 
     * @param path
     */
    private void addRemoveListener(String path) {
        if (watchRunnableRegistry.add(path)) {
            zkClient.subscribeDataChanges(path, new IZkDataListener() {
                @Override
                public void handleDataChange(String path, Object data) {}

                @Override
                public void handleDataDeleted(String path) {
                    log.info("ZKRegistry => handleDataDeleted, path:{} ", path);
                    String host = path.substring(path.lastIndexOf("/") + 1);
                    localRegistryMap.remove(host);
                }
            });
        }
    }
}
