package com.smart.api.address;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

/**
 * @author chenjunlong
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    /**
     * 最多返回的TcpServer节点的地址数量，6个(1主5备) 第0个元素为主，其余为备，满足业务场景
     */
    private static final int MAX_RETURN_ADDRESS_NUM = 6;

    @Override
    public List<String> select(List<String> address) {
        if (CollectionUtils.isEmpty(address)) {
            return Collections.emptyList();
        }
        List<String> returnAddress = this.doSelect(address);
        if (CollectionUtils.size(returnAddress) > MAX_RETURN_ADDRESS_NUM) {
            returnAddress = returnAddress.subList(0, MAX_RETURN_ADDRESS_NUM - 1);
        }
        return returnAddress;
    }

    public abstract List<String> doSelect(List<String> address);
}
