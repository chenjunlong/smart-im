package com.smart.api.request.validator.range;

/**
 * @author chenjunlong
 */
public class DoubleRange implements ParamRange<Double> {

    double min = Double.MIN_VALUE;
    double max = Double.MAX_VALUE;

    public DoubleRange() {

    }

    public DoubleRange(String range) {
        String[] arr = range.split("~");
        this.min = Double.parseDouble(arr[0]);
        this.max = Double.parseDouble(arr[1]);
        if (this.max < this.min) {
            this.max = Double.MAX_VALUE;
        }
    }

    @Override
    public boolean isInRange(Double value) {
        double v = value.doubleValue();
        return (v >= min && v <= max);
    }

    @Override
    public String getDesc() {
        return min + "~" + max;
    }

    @Override
    public boolean isCompatible(Class<?> type) {
        return type == double.class || type == Double.class;
    }
}

