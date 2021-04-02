package com.smart.api.intercepter.request.validator;

import com.smart.api.intercepter.request.validator.range.ParamRange;

public class IntegerValidator extends AbstractTypeValidator<Integer> {

    public IntegerValidator(ParamRange<Integer> range) {
        super(Integer.class, range);
    }

    @Override
    protected Integer convert(String value) {
        return Integer.parseInt(value);
    }

    @Override
    protected String getTypeName() {
        return "int";
    }

}
