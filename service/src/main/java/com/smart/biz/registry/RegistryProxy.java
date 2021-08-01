package com.smart.biz.registry;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author chenjunlong
 */
public class RegistryProxy implements Registry {

    private Registry registry;

    public RegistryProxy(Registry registry) {
        this.registry = registry;
    }

    @Override
    public Optional<Boolean> register(String address) {
        Optional<Boolean> result = registry.register(address);
        return result.isPresent() ? Optional.of(true) : Optional.empty();
    }

    @Override
    public Optional<Boolean> unregister(String address) {
        Optional<Boolean> result = registry.unregister(address);
        return result.isPresent() ? Optional.of(true) : Optional.empty();
    }

    @Override
    public List<String> getConnectAddress() {
        List<String> address = registry.getConnectAddress();
        if (CollectionUtils.isEmpty(address)) {
            return Collections.emptyList();
        }
        return address;
    }

    @Override
    public void beatHeart(String address) {
        registry.beatHeart(address);
    }
}
