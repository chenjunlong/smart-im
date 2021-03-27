package com.smart.api.request.validator;

import com.smart.api.request.validator.range.ParamRange;
import com.smart.api.request.validator.range.RangeFactory;
import com.smart.common.util.ClassUtil;

/**
 * @author chenjunlong
 */
public class TypeValidatorFactory {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static TypeValidator getInstance(Class type, String rangeStr) {

        Class newType = type.isPrimitive() ? ClassUtil.primitiveToWrapper(type) : (type.isArray() ? type.getComponentType() : type);
        ParamRange range = RangeFactory.getRangeInstance(rangeStr);

        if (newType == Long.class) {
            return new LongValidator(range);
        } else if (newType == Integer.class) {
            return new IntegerValidator(range);
        } else if (newType == Double.class) {
            return new DoubleValidator(range);
        } else if (newType == Float.class) {
            return new FloatValidator(range);
        } else if (newType == String.class) {
            return new StringValidator(range);
        } else if (newType == Boolean.class) {
            return new BooleanValidator();
        }
        throw new IllegalArgumentException("unsupport parameter type:" + type.getName());
    }
}
