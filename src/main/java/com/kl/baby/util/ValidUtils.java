package com.kl.baby.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qiang on 2018/2/1.
 */
public class ValidUtils {
    private static final Pattern MOBILE = Pattern.compile("^[1][3,4,5,6,7,8,9][0-9]{9}$");
    private static final Pattern SHORT_PHONE = Pattern.compile("^[1-9][0-9]{5,8}$");
    private static final Pattern LONG_PHONE = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");

    /**
     * 手机号验证
     */
    public static boolean isMobile(String str) {
        return MOBILE.matcher(str).matches();
    }

    /**
     * 电话号码验证
     */
    public static boolean isPhone(String str) {
        if (str.length() > 9) {
            return LONG_PHONE.matcher(str).matches();
        } else {
            return SHORT_PHONE.matcher(str).matches();
        }

    }
}
