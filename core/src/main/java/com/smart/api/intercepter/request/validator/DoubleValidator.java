package com.smart.api.intercepter.request.validator;

import com.smart.api.intercepter.request.validator.range.ParamRange;

/**
 * @author chenjunlong
 */
public class DoubleValidator extends AbstractTypeValidator<Double> {

    public DoubleValidator(ParamRange<Double> range) {
        super(Double.class,range);
    }

    @Override
    protected Double convert(String value) {
        return Double.parseDouble(value);
    }

    @Override
    protected String getTypeName() {
        return "double";
    }

}
