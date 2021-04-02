package com.smart.api.intercepter.request.validator;

import com.smart.api.intercepter.request.validator.range.ParamRange;

/**
 * @author chenjunlong
 */
public class FloatValidator extends AbstractTypeValidator<Float> {

    public FloatValidator(ParamRange<Float> range) {
        super(Float.class, range);
    }

    @Override
    protected Float convert(String value) {
        return Float.parseFloat(value);
    }

    @Override
    protected String getTypeName() {
        return "float";
    }

}
