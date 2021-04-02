package com.smart.api.intercepter.request.validator.range;

import org.apache.commons.lang3.StringUtils;

/**
 * @author chenjunlong
 */
public class RangeFactory {

    @SuppressWarnings("rawtypes")
    public static ParamRange getRangeInstance(String range) {
        if (StringUtils.isBlank(range)) {
            return null;
        }

        String[] arr = range.split(":");
        if (arr.length != 2) {
            throw new IllegalArgumentException("invalid range define:" + range);
        }

        ParamRange result = null;
        if (arr[0].equals("int")) {
            result = new IntRange(arr[1].trim());
        } else if (arr[0].equals("long")) {
            result = new LongRange(arr[1].trim());
        } else if (arr[0].equals("float")) {
            result = new FloatRange(arr[1].trim());
        } else if (arr[0].equals("double")) {
            result = new DoubleRange(arr[1].trim());
        } else if (arr[0].equals("str")) {
            String[] rg = arr[1].trim().split("~");
            result = new StringRange(Integer.parseInt(rg[0]), Integer.parseInt(rg[1]));
        } else if (arr[0].equals("strexp")) {
            result = new StringRange(arr[1].trim());
        }

        return result;
    }
}
