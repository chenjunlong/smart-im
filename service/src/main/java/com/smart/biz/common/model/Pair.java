package com.smart.biz.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author chenjunlong
 */
@AllArgsConstructor
@Getter
public class Pair<K, V> {

    private K k;
    private V v;

}
