package com.simple.mail;

import android.text.TextUtils;


import com.simple.mail.entity.AddressInfo;
import com.simple.mail.entity.Mail;

import java.util.ArrayList;
import java.util.List;

public class UserConfig {
    private UserConfig() {
    }

    public static AddressInfo addresser;
    public static List<Mail> inbox = new ArrayList<>();
    public static String id = "";
    private static String name;

    public static int allUnreadMessageNum = 0;//所有未读消息
    public static boolean isLoadingMail;//是否正在加载邮件

    public static void clear() {
        addresser = null;
        inbox = null;
        id = "";
        allUnreadMessageNum = 0;
        setName("");
    }

    /**
     * 获取昵称
     *
     * @return
     */
    public static String getName() {
        return TextUtils.isEmpty(name) ? "犀牛会员" : name;
    }

    /**
     * 设置昵称
     *
     * @param
     */
    public static void setName(String name) {
        UserConfig.name = TextUtils.isEmpty(name) ? "犀牛会员" : name;
    }


}
