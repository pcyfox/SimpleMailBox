package com.simple.mail.utils;


import android.text.TextUtils;


import com.simple.mail.entity.Protocol;

import java.util.ArrayList;

public class ProtocolUtils {
    public static ArrayList<Protocol> list;

    public static ArrayList<Protocol> getAddresser() {
        list = new ArrayList<>();
        list.add(new Protocol(1, "smtp", "mail.eims.com.cn", "25", false));//eims sll=false
        list.add(new Protocol(1, "imap", "mail.eims.com.cn", "143", false));//eims sll=false

        list.add(new Protocol(2, "smtp", "mail.xiniu.com", "25", false));//xiniu sll=false
        list.add(new Protocol(2, "imap", "mail.xiniu.com", "143", false));//xiniu sll=false

//        list.add(new Protocol(3, "smtp", "smtp.qq.com", "25", false));//qq sll=false
//        list.add(new Protocol(3, "imap", "imap.qq.com", "143", false));//qq sll=false
        list.add(new Protocol(4, "smtp", "smtp.qq.com", "465", true));//qq sll=true
        list.add(new Protocol(4, "imap", "imap.qq.com", "993", true));//qq sll=true

        list.add(new Protocol(5, "smtp", "smtp.163.com", "25", false));//163 sll=false
        list.add(new Protocol(5, "pop3", "pop.163.com", "110", false));//163 sll=false

        list.add(new Protocol(6, "smtp", "smtp.126.com", "25", false));//126 sll=false
        list.add(new Protocol(6, "pop3", "pop.126.com", "110", false));//126 sll=false

        list.add(new Protocol(7, "smtp", "smtp.sina.com", "25", false));//sina sll=false
        list.add(new Protocol(7, "imap", "imap.sina.com", "143", false));//sina sll=false

        list.add(new Protocol(8, "smtp", "smtp.sina.com", "25", false));//sina sll=false
        list.add(new Protocol(8, "pop3", "pop.sina.com", "110", false));//sina sll=false

        list.add(new Protocol(9, "smtp", "smtp.sohu.com", "25", false));//sohu sll=false
        list.add(new Protocol(9, "pop3", "pop3.sohu.com", "110", false));//sohu sll=false

        list.add(new Protocol(9, "smtp", "imap.otot-it.com", "465", false));//sohu sll=false
        list.add(new Protocol(9, "smtp", "imap.otot-it.com", "465", false));//sohu sll=false

        return list;
    }

    /**
     * 通过邮箱地址查询POP3或者IMAP协议的相关资料
     */
    public static ArrayList<Protocol> selectPOP3orIMAP(String address) {
        String key = address.substring(address.lastIndexOf("@") + 1);
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        getAddresser();
        ArrayList<Protocol> selects = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Protocol protocol = list.get(i);

            if (!protocol.protocl.equals("smtp") && protocol.server.contains(key)) {
                selects.add(protocol);
            }
        }
        return selects;
    }

    /**
     * 通过邮箱地址查询POP3或者IMAP协议的相关资料
     */
    public static Protocol selectSMTP(int id) {
        for (int i = 0; i < list.size(); i++) {
            Protocol protocol = list.get(i);
            if (protocol.protocl.equals("smtp") && protocol.id == id) {
                return protocol;
            }
        }
        return null;
    }


}
