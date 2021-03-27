package com.smart.api.request.validator.range;

/**
 * @author chenjunlong
 */
public class FloatRange implements ParamRange<Float> {

    float min = Float.MIN_VALUE;
    float max = Float.MAX_VALUE;

    public FloatRange() {

    }

    public FloatRange(String range) {
        String[] arr = range.split("~");
        this.min = Float.parseFloat(arr[0]);
        this.max = Float.parseFloat(arr[1]);
        if (this.max < this.min) {
            this.max = Float.MAX_VALUE;
        }
    }

    @Override
    public boolean isInRange(Float value) {
        float v = value.floatValue();
        return (v >= min && v <= max);
    }

    @Override
    public String getDesc() {
        return min + "~" + max;
    }

    @Override
    public boolean isCompatible(Class<?> type) {
        return type == float.class || type == Float.class;
    }
}

