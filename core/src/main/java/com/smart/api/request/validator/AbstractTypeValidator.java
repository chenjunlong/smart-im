package com.smart.api.request.validator;

import com.smart.api.request.validator.range.ParamRange;

/**
 * @author chenjunlong
 */
public abstract class AbstractTypeValidator<T> implements TypeValidator {

    private ParamRange<T> range;

    public AbstractTypeValidator(Class<T> supportType, ParamRange<T> range) {
        this.range = range;
        if (this.range != null && !this.range.isCompatible(supportType)) {
            throw new IllegalArgumentException(
                    supportType.getSimpleName() + " uncompatible with range " + range.getClass().getName() + "(" + range.getDesc() + ")");
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean isValid(String[] values) {
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            try {
                T t = this.convert(value);
                // 兼容boolean类型
                if (t instanceof Boolean) {
                    values[i] = t.toString();
                }
                if (this.range != null) {
                    if (!this.range.isInRange(t)) {
                        return false;
                    }
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public boolean isValid(String value) {
        return this.isValid(new String[] {value});
    }

    protected abstract T convert(String value);

    @Override
    public String getDesc() {
        return this.getTypeName() + (this.range == null ? "" : "[" + this.range.getDesc() + "]");
    }

    protected abstract String getTypeName();

}

