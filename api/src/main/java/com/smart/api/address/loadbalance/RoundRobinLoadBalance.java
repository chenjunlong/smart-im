package com.smart.api.address.loadbalance;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import com.smart.common.util.MathUtil;

/**
 * @author chenjunlong
 */
@Component("roundrobin")
public class RoundRobinLoadBalance extends AbstractLoadBalance {

    private AtomicInteger idx = new AtomicInteger(0);

    @Override
    public List<String> doSelect(List<String> address) {
        int index = MathUtil.getNonNegative(idx.incrementAndGet());
        Collections.swap(address, 0, index % address.size());
        return address;
    }

}
