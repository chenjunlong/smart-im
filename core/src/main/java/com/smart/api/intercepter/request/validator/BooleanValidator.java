package com.smart.api.intercepter.request.validator;

/**
 * @author chenjunlong
 */
public class BooleanValidator extends AbstractTypeValidator<Boolean> {

    public BooleanValidator() {
        super(Boolean.class, null);
    }

    @Override
    protected Boolean convert(String value) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("t") || value.equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected String getTypeName() {
        return "boolean";
    }

}
