package com.smart.service.registry;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

/**
 * @author chenjunlong
 */
@Service
public class RegistryProxy {

    @Resource(name = "zkRegistry")
    private Registry zkRegistry;

    public Optional<Boolean> register(String address) {
        Optional<Boolean> zkResult = zkRegistry.register(address);
        return zkResult.isPresent() ? Optional.of(true) : Optional.empty();
    }

    public Optional<Boolean> unregister(String address) {
        Optional<Boolean> zkResult = zkRegistry.unregister(address);
        return zkResult.isPresent() ? Optional.of(true) : Optional.empty();
    }

    public List<String> getConnectAddress() {
        List<String> address = zkRegistry.getConnectAddress();
        return address;
    }

    public void beatHeart(String address){
        zkRegistry.beatHeart(address);
    }
}
