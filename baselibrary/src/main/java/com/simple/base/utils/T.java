package com.simple.base.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.simple.base.base.BaseApplication;


public class T {
    private static final String TAG = "T";

    public static void show(Object text) {
        show(BaseApplication.getApplication(), text, 0);
    }

    public static void longShow(Object text) {
        show(BaseApplication.getApplication(), text, 1);
    }

    public static void show(Context context, Object text, int duration) {
        if (text instanceof String) {
            String content = (String) text;
            if (!TextUtils.isEmpty(content)) {
                Toast.makeText(context, content, duration).show();
            }
            return;
        }
        if (text instanceof Integer) {
            Toast.makeText(context, (Integer) text, duration).show();
            return;
        }

    }

    /**
     * 弹出错误信息，如果查找到错误码，则为错误码所在的返回信息，如果没有，则为 description
     */
    public static void showErr(String description, short code, String... strings) {
        switch (code) {
            case -1:
                description = "无效令牌";
                break;

            case -2:
                description = "已受限制";
                break;

//             case -3:
//                description="密码错误";
//                break;

            case -4:
                description = "没有找到用户";
                break;

            case -5:
                description = "邮箱地址被占用";
                break;


            case -6:
                description = "手机号码被占用";
                break;

            case -7:
                description = "手机号码不存在";
                break;

            case -8:
                description = "验证码错误";
                break;

            case -9:
                description = "终端设备已受限制";
                break;

            case -10://其他业务逻辑错误标识符  -10 .... -999,显示返回的description
                break;

            case -11:
                description = "已超过重试次数";
                break;

            case -12:
                description = "没有数据";
                break;

            case -13:
                description = "数据不匹配";
                break;

            case -14:
                description = "域名已存在";
                break;

            case -15:
                description = "部门已存在";
                break;

            case -16:
                description = "应用已安装";
                break;

            case -17:
                description = "用户已授权";
                break;

            case -18:
                description = "手机号码格式错误";
                break;

            case -19:
                description = "邮箱地址格式错误";
                break;

            case -98:
                description = "权限不足";
                break;

            case -201:
                description = "账号已经存在";
                break;

            case -1001:
                description = "通讯连接错误";
                break;

            case -1002:
                description = "通讯发送错误";
                break;

            case -1003:
                description = "通讯接收失败";
                break;

            case -1004:
                description = "通讯接收数据异常";
                break;

            case -10001:
                description = "系统错误-未知异常";
                break;


        }

        if (!TextUtils.isEmpty(description)) {
            show(description);
        } else {
            if (!TextUtils.isEmpty(strings[0])) {
                show(strings[0]);
            }
        }
    }
}
