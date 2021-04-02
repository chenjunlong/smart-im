package com.smart.api.intercepter.request.validator;

import com.smart.api.intercepter.request.validator.range.ParamRange;

/**
 * @author chenjunlong
 */
public class LongValidator extends AbstractTypeValidator<Long> {

    public LongValidator(ParamRange<Long> range) {
        super(Long.class, range);
    }

    @Override
    protected Long convert(String value) {
        return Long.parseLong(value);
    }

    @Override
    protected String getTypeName() {
        return "long";
    }

}
