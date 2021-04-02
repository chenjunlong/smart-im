package com.smart.api.intercepter.request.validator;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.smart.api.exception.ApiInvalidParameterException;
import com.smart.api.exception.ExcepFactor;

/**
 * @author chenjunlong
 */
@SuppressWarnings("deprecation")
public class ParameterValidator {

    private String parameterName;
    private TypeValidator typeValidator;

    public ParameterValidator(String parameterName, Class<?> parameterType, String range) {
        this.parameterName = parameterName;
        this.typeValidator = TypeValidatorFactory.getInstance(parameterType, range);
    }

    public void validate(Map<String, String[]> params) {
        String[] values = params.get(this.parameterName);
        if (values == null || ArrayUtils.isEmpty(values)) {
            return;
        }
        if (!this.typeValidator.isValid(values)) {
            throw new ApiInvalidParameterException(ExcepFactor.E_PARAMS_ERROR, this.parameterName, this.typeValidator.getDesc(), StringUtils.join(values, ","));
        }
    }

}
