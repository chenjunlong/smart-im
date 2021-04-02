package com.smart.api.address.loadbalance;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

/**
 * @author chenjunlong
 */
@Component("random")
public class RandomLoadBalance extends AbstractLoadBalance {

    @Override
    public List<String> doSelect(List<String> address) {
        Collections.shuffle(address, ThreadLocalRandom.current());
        return address;
    }

}
