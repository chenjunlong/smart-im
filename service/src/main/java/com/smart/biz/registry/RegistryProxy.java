package com.smart.biz.registry;

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
    private Registry registry;

    public Optional<Boolean> register(String address) {
        Optional<Boolean> result = registry.register(address);
        return result.isPresent() ? Optional.of(true) : Optional.empty();
    }

    public Optional<Boolean> unregister(String address) {
        Optional<Boolean> result = registry.unregister(address);
        return result.isPresent() ? Optional.of(true) : Optional.empty();
    }

    public List<String> getConnectAddress() {
        List<String> address = registry.getConnectAddress();
        return address;
    }

    public void beatHeart(String address) {
        registry.beatHeart(address);
    }
}
