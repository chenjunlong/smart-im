package com.smart.api.address;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

/**
 * @author chenjunlong
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public List<String> select(List<String> address) {
        if (CollectionUtils.isEmpty(address)) {
            return Collections.emptyList();
        }
        return doSelect(address);
    }

    public abstract List<String> doSelect(List<String> address);
}
