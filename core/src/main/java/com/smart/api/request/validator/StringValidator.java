package com.smart.api.request.validator;

import com.smart.api.request.validator.range.ParamRange;

/**
 * @author chenjunlong
 */
public class StringValidator extends AbstractTypeValidator<String> {

    public StringValidator(ParamRange<String> range) {
        super(String.class, range);
    }

    @Override
    protected String convert(String value) {
        return value;
    }

    @Override
    protected String getTypeName() {
        return "str";
    }

}

