package com.smart.api.intercepter.request.validator.range;

/**
 * @author chenjunlong
 */
public class IntRange implements ParamRange<Integer> {

    int min = Integer.MIN_VALUE;
    int max = Integer.MAX_VALUE;

    public IntRange() {
    }

    public IntRange(String range) {
        String[] arr = range.split("~");
        this.min = Integer.parseInt(arr[0]);
        this.max = Integer.parseInt(arr[1]);
        if (this.max < this.min) {
            this.max = Integer.MAX_VALUE;
        }
    }

    public IntRange(int min, int max) {
        this.min = min;
        this.max = max;
        if (this.max < this.min) {
            this.max = Integer.MAX_VALUE;
        }
    }

    @Override
    public boolean isInRange(Integer value) {
        int v = value.intValue();
        return (v >= min && v <= max);
    }

    @Override
    public String getDesc() {
        return min + "~" + max;
    }

    @Override
    public boolean isCompatible(Class<?> type) {
        return type == int.class || type == Integer.class;
    }
}

