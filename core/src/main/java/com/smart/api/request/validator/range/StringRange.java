package com.smart.api.request.validator.range;

import java.util.regex.Pattern;

/**
 * @author chenjunlong
 */
public class StringRange implements ParamRange<String> {

    private String regExp;
    int min = -1;
    int max = -1;

    public StringRange(int minlength, int maxlength) {
        this.min = minlength;
        this.max = maxlength;
    }

    public StringRange(String regExp) {
        this.regExp = regExp;
    }

    @Override
    public boolean isInRange(String value) {
        boolean rst = true;
        int len = value.length();
        if (this.min >= 0) {
            rst = (len >= min);
            if (this.max >= 0) {
                rst = ((len <= max) && rst);
            }
        } else if (this.regExp != null) {
            rst = Pattern.matches(regExp, value);
        }
        return rst;
    }

    @Override
    public String getDesc() {
        if (this.regExp != null) {
            return "regexp:" + this.regExp;
        }
        return "str length：" + min + "~" + max;
    }

    @Override
    public boolean isCompatible(Class<?> type) {
        return type == String.class;
    }
}
