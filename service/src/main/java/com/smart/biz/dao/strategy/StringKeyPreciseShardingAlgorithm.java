package com.smart.biz.dao.strategy;

import java.util.Collection;

import org.apache.kafka.common.utils.Crc32;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import lombok.NoArgsConstructor;

/**
 * @author chenjunlong
 */
@NoArgsConstructor
public class StringKeyPreciseShardingAlgorithm implements PreciseShardingAlgorithm<String> {

    @Override
    public String doSharding(Collection<String> tableNames, PreciseShardingValue<String> preciseShardingValue) {
        long segment = Crc32.crc32(preciseShardingValue.getValue().getBytes()) % 2;
        for (String tableName : tableNames) {
            if (tableName.endsWith(String.valueOf(segment))) {
                return tableName;
            }
        }
        return null;
    }
}
