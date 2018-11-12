package com.simple.base.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatches {
    public static boolean isEmail(String address) {
        String pattern = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
        // 创建 Pattern 对象
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(address);
        return matcher.find();
    }
}
