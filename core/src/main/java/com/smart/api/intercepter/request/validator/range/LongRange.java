package com.smart.api.intercepter.request.validator.range;

/**
 * @author chenjunlong
 */
public class LongRange implements ParamRange<Long> {

    long min = Long.MIN_VALUE;
    long max = Long.MAX_VALUE;

    public LongRange() {

    }

    public LongRange(String range) {
        String[] arr = range.split("~");
        this.min = Long.parseLong(arr[0]);
        this.max = Long.parseLong(arr[1]);
        if (this.max < this.min) {
            this.max = Long.MAX_VALUE;
        }
    }

    public LongRange(long min, long max) {
        this.min = min;
        this.max = max;
        if (this.max < this.min) {
            this.max = Integer.MAX_VALUE;
        }
    }

    @Override
    public boolean isInRange(Long value) {
        long v = value.longValue();
        return (v >= min && v <= max);
    }

    @Override
    public String getDesc() {
        return min + "~" + max;
    }

    @Override
    public boolean isCompatible(Class<?> type) {
        return type == long.class || type == Long.class;
    }
}

