package com.smart.server.model;

/**
 * @author chenjunlong
 */
public enum BehaviorEnum {

    JOIN(1), EXIT(2);

    private final int behavior;

    BehaviorEnum(int behavior) {
        this.behavior = behavior;
    }

    public int getBehavior() {
        return behavior;
    }
}
