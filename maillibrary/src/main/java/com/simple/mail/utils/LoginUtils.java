package com.simple.mail.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.simple.mail.entity.Addresser;
import com.simple.mail.entity.Protocol;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;

/**
 * 登录服务器相关工具类
 */
public class LoginUtils {
    private static final String TAG = "LoginUtils";
    //用于存储错误信息
    private static Bundle errorBundle = new Bundle();
    public static String ERROR = "error";
    private static String AUTHENTICATION_ERROR_MSG = "认证失败：密码或账户错误!";


    /**
     * 登录
     */
    public static boolean loginPOP3orIMAP(String account, String password, Protocol p, Addresser addresser) {
        try {
            Session session = getSessionPOP3orIMAP(p);//得到session
            session.setDebug(true);
            Store store = session.getStore(p.protocl);//设置通讯协议
            store.connect(account, password);//连接
            addresser.setReceiveProtocol(p);
        } catch (MessagingException e) {
            if (e instanceof AuthenticationFailedException) {
                Log.e(TAG, "loginPOP3orIMAP: 认证失败");
                errorBundle.putString(ERROR, AUTHENTICATION_ERROR_MSG);
            }
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * SMTP（Simple Mail Transfer Protocol）即简单邮件传输协议
     */
    public static boolean connectBySMTP(String account, String password, Protocol p, Addresser addresser) {
        try {
            Session session = getSessionSMTP(p, account, password);  //得到session
            Transport transport = session.getTransport(p.protocl);//设置通讯协议
            transport.connect(account, password);//连接
            addresser.setSendProtocol(p);
        } catch (MessagingException e) {
            if (e instanceof AuthenticationFailedException) {
                Log.e(TAG, "loginPOP3orIMAP: 认证失败");
                errorBundle.putString(ERROR, AUTHENTICATION_ERROR_MSG);
            }
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 登录
     */
    public static boolean loginPOP3orIMAP2(String account, String password, ArrayList<Protocol> list, Addresser addresser) {
        if (list.isEmpty()) {//如果list为空，那就使用公共的去配置
            Protocol receiveProtocol = new Protocol("imap", "mail." + account.substring(account.indexOf("@") + 1, account.length()), "143", false);

            if (loginPOP3orIMAP(account, password, receiveProtocol, addresser)) {//接受邮件的服务器设置好了之后，给他将默认的发送的服务器设置进去
                addresser.setSendProtocol(new Protocol("smtp", "mail." + account.substring(account.indexOf("@") + 1, account.length()), "25", false));
                return true;
            } else {//如果使用"imap"协议失败，那就再尝试pop3
                receiveProtocol = new Protocol("pop3", "mail." + account.substring(account.indexOf("@") + 1, account.length()), "110", false);
                if (loginPOP3orIMAP(account, password, receiveProtocol, addresser)) {//接受邮件的服务器设置好了之后，给他将默认的发送的服务器设置进去
                    addresser.setSendProtocol(new Protocol("smtp", "mail." + account.substring(account.indexOf("@") + 1, account.length()), "25", false));
                    return true;
                }
            }

        } else {
            for (int i = 0; i < list.size(); i++) {
                if (loginPOP3orIMAP(account, password, list.get(i), addresser)) {//接受邮件的服务器设置好了之后，给他将默认的发送的服务器设置进去
                    addresser.setSendProtocol(ProtocolUtils.selectSMTP(list.get(i).id));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 登录
     */
    public static boolean loginPOP3orIMAP(String account, String password, Addresser addresser) {
        ArrayList<Protocol> listPOP3orIMAP = ProtocolUtils.selectPOP3orIMAP(account); //得到所有条件的接收协议
        if (listPOP3orIMAP == null) {
            return false;
        }
        return loginPOP3orIMAP2(account, password, listPOP3orIMAP, addresser);
    }


    /**
     * 尝试连接服务器成功并通过POP3或IMAP协议登录成功则返回true
     */
    public static boolean loginPOP3AndIMAP(final Context context, final Addresser addresser) {
        Boolean isLoginReceive = LoginUtils.loginPOP3orIMAP(addresser.email_account, addresser.email_password, addresser.receiveProtocol, addresser);
        Boolean isLoginSend = LoginUtils.connectBySMTP(addresser.email_account, addresser.email_password, addresser.sendProtocol, addresser);
        return isLoginReceive && isLoginSend;

    }

    /**
     * 得到Session
     */
    public static Session getSessionSMTP(Protocol p, String account, String password) {
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.auth", "true");// 服务器需要认证
        props.setProperty("mail.transport.protocol", p.port);// 声明发送邮件使用的端口
        props.setProperty("mail.smtp.host", p.server); //设置服务器
        props.setProperty("mail.smtp.timeout", "20000"); //设置网络连接时间

        if (p.ssl) { //如果开启了ssl加密则需如下设置，未开启ssl加密则不需要。
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //使用JSSE的SSL socketfactory来取代默认的socketfactory
            props.setProperty("mail.smtp.socketFactory.fallback", "false");  // 只处理SSL的连接,对于非SSL的连接不做处理
            props.setProperty("mail.smtp.socketFactory.port", p.port);  //设置SSL连接的端口号
        }
        return Session.getInstance(props, new MyAuthenticator(account, password));
    }

    /**
     * 得到Session
     */
    public static Session getSessionPOP3orIMAP(Protocol p) {
        Properties props = System.getProperties();
        props.setProperty("mail." + p.protocl + ".host", p.server); //设置服务器
        props.setProperty("mail." + p.protocl + ".port", p.port); //设置端口号
        props.setProperty("mail." + p.protocl + ".timeout", "20000"); //设置网络连接时间

        if (p.ssl) { //如果开启了ssl加密则需如下设置，未开启ssl加密则不需要。
            props.setProperty("mail." + p.protocl + ".socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //使用JSSE的SSL socketfactory来取代默认的socketfactory
            props.setProperty("mail." + p.protocl + ".socketFactory.fallback", "false");  // 只处理SSL的连接,对于非SSL的连接不做处理
            props.setProperty("mail." + p.protocl + ".socketFactory.port", p.port);  //设置SSL连接的端口号
        }
        return Session.getDefaultInstance(props);
    }


}
