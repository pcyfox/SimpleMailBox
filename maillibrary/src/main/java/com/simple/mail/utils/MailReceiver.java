package com.simple.mail.utils;

import android.content.Context;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;


import com.simple.mail.entity.AddressInfo;
import com.simple.mail.entity.Mail;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;

import static android.content.Context.POWER_SERVICE;

/**
 * 接收邮件的类
 */
public class MailReceiver {
    private static Thread getPageMailBottomThread;
    private static Thread getPageMailTopThread;
    private static final String TAG = "MailReceiver";
    private boolean isNewToOld = false;//邮件收取顺序
    private Folder folder;
    private PowerManager.WakeLock wakeLock;
    private static final String WAKE_LOCK_TAG = "com.eims.yunke:MailReceiver";

    public MailReceiver(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG);
    }

    /**
     * 上滑加载更多
     * folderType  mail列表文件夹的类型   operatorType 收件箱的类型（全部或者未读）
     */
    public List<Mail> getPageMailBottom(final AddressInfo a, final Mail startMail, final int folderType, final int inboxType) {
        //先去数据库中取出total个mail
        String emailAccount = a.email_account;
        if (TextUtils.isEmpty(emailAccount)) return null;
        //TODO
       // List<Mail> pageMailFromDb = DBMail.getInstance().getPageMailFromDbIndex(a, startMail, folderType, inboxType);
        List<Mail> pageMailFromDb =new ArrayList<>();

        //如果数据库中取出的不够total个mail，且此时为收件箱，且为全部的时候，再去服务器请求。
        if (pageMailFromDb.size() < Mail.PAGE_COUNT && folderType == Mail.MAIL_TYPE_INBOX) {
            //再去网上请求。
            ArrayList<Mail> mailsFromServer = getMailFromServer(false, a, Mail.PAGE_COUNT - pageMailFromDb.size());
            pageMailFromDb.addAll(mailsFromServer);
        }

        return pageMailFromDb;
    }

    /**
     * 下拉刷新
     */
    public List<Mail> getPageMailTop(final AddressInfo addresser, final int folderType) {
        wakeLock.acquire(5000 * 60);
        //取出数据库中最近的一个mail,作为标志位去服务器请求
        List<Mail> pageMailFromServer = getMailFromServer(true, addresser, Mail.PAGE_COUNT);
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        return pageMailFromServer;
    }

    /**
     * 获取并打开邮箱服务器收件箱（如果打不开则返回null）
     *
     * @param addresser
     * @return
     */
    public static Folder getOpenedServerInboxFolder(AddressInfo addresser) {
        Folder folder = null;
        try {
            Session session = LoginUtils.getSessionPOP3orIMAP(addresser.receiveProtocol);
            Store store = session.getStore(addresser.receiveProtocol.protocl);//设置通讯协议
            store.connect(addresser.email_account, addresser.email_password);//连接
            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);// 设置仅读READ_ONLY   READ_WRITE
            if (!folder.isOpen()) {//如果没有开启，则再开启一遍。
                folder.open(Folder.READ_WRITE);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        if (folder != null && !folder.isOpen()) {//如果第二遍仍然没有开启，则返回空。
            folder = null;
        }
        return folder;
    }

    /**
     * 从服务器收件箱获取邮件
     *
     * @param isGetNewMail  true 获取新邮件 false 获取旧邮件
     * @param addresser
     * @param needMailCount
     * @return
     */
    public ArrayList<Mail> getMailFromServer(boolean isGetNewMail, AddressInfo addresser, int needMailCount) {

        if (addresser == null || TextUtils.isEmpty(addresser.email_account)) {
            return null;
        }
        folder = null;
        folder = getOpenedServerInboxFolder(addresser);
        if (folder == null) {//下载的时候如果切换到了别的界面，就不要继续下载了
            return null;
        }
        boolean isPOP3 = folder instanceof POP3Folder;
        if (!isPOP3 && !(folder instanceof IMAPFolder)) {
            return null;//只支持pop3和imap
        }
        ArrayList<Mail> list;

        GetUidInterface getUidInterface = isPOP3 ? new GetPOP3Uid() : new GetIMAPUid();
        if (isGetNewMail) {
            list = getNewMail(folder, addresser, getUidInterface, needMailCount);
        } else {
            list = getOldMail(folder, addresser, getUidInterface, needMailCount);
        }
        return list;
    }

    /**
     * 获取新邮件
     *
     * @param folder
     * @return
     */
    private List<Message> getMessage(Folder folder, String firstMailUid, Date data) {
        List<Message> messages = null;
        if (folder instanceof IMAPFolder) {
            try {
                long newestUID = ((IMAPFolder) folder).getUID(folder.getMessage(folder.getMessageCount()));
                messages = Arrays.asList(((IMAPFolder) folder).getMessagesByUID(Long.parseLong(firstMailUid), newestUID));
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (messages == null) {
                return null;
            }
        } else {
            messages = getMessagesByData(data, folder, true);
        }

        return messages;

    }


    private ArrayList<Mail> getNewMail(Folder folder, AddressInfo addresser, GetUidInterface getUidInterface, int needMailCount) {
   //TODO
      //  String firstMailUid = DBAddresser.getInstance().getFirstMailUid(addresser);
        String firstMailUid ="";
        if ((TextUtils.isEmpty(firstMailUid) || firstMailUid.equals("-1") || firstMailUid.equals("0"))) {//当前邮箱账号第一次取邮件,没有“新邮件”，直接取旧邮件
            return getOldMail(folder, addresser, getUidInterface, needMailCount);
        }

        //TODO  Date date = Jna.getInstance().getFirstOrLastMailSentDate(isGetNewMessage);
        Date date = null;
        List<Message> messages = getMessage(folder, firstMailUid, date);
        if (messages == null) {
            return null;
        }

        return getMail(folder, addresser, getUidInterface, needMailCount, false, firstMailUid, messages);
    }

    /**
     * 获取旧邮件
     *
     * @param folder
     * @param addresser
     * @param getUidInterface
     * @param needMailCount
     * @return
     */
    private ArrayList<Mail> getOldMail(Folder folder, AddressInfo addresser, GetUidInterface getUidInterface, int needMailCount) {

        boolean isAllowAdd = false;
        //TODO
       // String lastMailUid = DBAddresser.getInstance().getLastMailUid(addresser);
        String lastMailUid ="";
        if (TextUtils.isEmpty(lastMailUid) || lastMailUid.equals("-1") || lastMailUid.equals("0")) {//等于空或"-1'、"0"证明第一次登录该账号，直接下载。
            isAllowAdd = true;
        }
        isNewToOld = true;
        //TODO  Date date = Jna.getInstance().getFirstOrLastMailSentDate(isGetNewMessage);
        Date date = null;
        List<Message> messages = getMessage(folder, lastMailUid, date);
        Collections.reverse(messages);
        return getMail(folder, addresser, getUidInterface, needMailCount, isAllowAdd, lastMailUid, messages);
    }

    @NonNull
    private ArrayList<Mail> getMail(Folder folder, AddressInfo addresser, GetUidInterface getUidInterface, int needMailCount, boolean isAllowAdd, String lastMailUid, List<Message> messages) {
        ArrayList<Mail> list = new ArrayList<>();
        for (Message message : messages) {
            String uid = getUidInterface.getUid(folder, message);
            if (TextUtils.isEmpty(uid)) {
                continue;
            }

            if (isAllowAdd) {
                Mail mail = new Mail(addresser.id, uid);
                if (!mail.needFilter) {//mail.needFilter==false代表此邮件接收的时候没有出现问题，不需要过滤
                    list.add(mail);
                }
            } else {
                if (!(TextUtils.isEmpty(lastMailUid) || lastMailUid.equals("-1") || lastMailUid.equals("0"))) {
                    isAllowAdd = uid.equals(lastMailUid);
                } else {
                    return list;
                }
            }
            //下载的mail够一页的时候，停止下载
            if (list.size() == needMailCount) {
                return list;
            }

        }

        return list;
    }




    /**
     * 从收件箱服务器获取邮件内容
     *
     */
    public static void getInBoxMailMessage(Mail mail, AddressInfo addresser) throws MessagingException {
        if (addresser != null ) {
            Folder folder = getOpenedServerInboxFolder(addresser);

            if (folder instanceof POP3Folder) {
                if (!TextUtils.isEmpty(mail.sendTime)) {
                    Message[] messages = null;
                    try {
                        long l = Long.parseLong(mail.sendTime);
                        Date date = new Date(l);
                        SearchTerm searchTerm = new SentDateTerm(ComparisonTerm.EQ, date);
                        messages = folder.search(searchTerm);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        messages = folder.getMessages();
                    }
                    if (messages != null) {
                        POP3Folder inbox = (POP3Folder) folder;
                        for (int i = 0; i < messages.length; i++) {
                            String uid = inbox.getUID(messages[i]);
                            if (uid.equals(mail.uid)) {

                                //TODO 将messages解析为Mail

                                break;
                            }
                        }
                    }
                }
            } else if (folder instanceof IMAPFolder) {
                IMAPFolder imapFolder = (IMAPFolder) folder;
                Message messagee = imapFolder.getMessageByUID(Long.parseLong(mail.uid));
                //TODO 将messages解析为Mail
            }
        }
    }





    /**
     * 获取服务器信箱中的邮件
     *
     * @param folder
     * @param isGetNewMessage
     * @return
     */
    public static List<Message> getMessagesByData(Date date, Folder folder, boolean isGetNewMessage) {
        Map<String, Object> map = new HashMap<>();
        int startPosition = -1;
        Message[] messageArray;
        try {
            if (date == null) {
                messageArray = folder.getMessages();
            } else {
                SentDateTerm searchTerm = new SentDateTerm(isGetNewMessage ? ComparisonTerm.GE : ComparisonTerm.LE, date);
                messageArray = folder.search(searchTerm);
                if (isGetNewMessage) {//获取新消息
                    for (int i = 0; i < messageArray.length; i++) {
                        if (!messageArray[i].getSentDate().before(date)) {
                            startPosition = i;
                            break;
                        }
                    }
                } else {//获取历史消息
                    for (int i = messageArray.length - 1; i > -1; i--) {
                        if (!messageArray[i].getSentDate().after(date)) {
                            startPosition = i;
                            break;
                        }
                    }
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            return null;
        }

        if (startPosition == -1) {
            startPosition = isGetNewMessage ? 0 : messageArray.length - 1;
        }

        List<Message> messages = Arrays.asList(messageArray);
        messages.subList(startPosition, messageArray.length);
        return messages;
    }




    /**
     * @Purpose
     * @Author Naruto Yang
     * @CreateDate 2018/8/27 0027
     * @Note
     */
    public static class GetMailThread extends Thread {
        private AddressInfo a;
        private Mail mail;
        private boolean isNeedStop = false;
        private Object data;
        private SimpleInterface simpleInterface;



        @Override
        public void run() {
            if (mail.id.contains("eml")) {

            } else {

            }
        }
    }

    /**
     * @Purpose
     * @Author Naruto Yang
     * @CreateDate 2018/9/15 0015
     * @Note
     */
    public interface SimpleInterface {
        Object done(Object o);
    }

    /**
     * @Purpose 获取pop3 的uid
     * @Author Naruto Yang
     * @CreateDate 2018/8/18 0018
     * @Note
     */
    public static class GetPOP3Uid implements GetUidInterface {
        @Override
        public String getUid(Folder folder, Message message) {
            String uid = "";
            try {
                uid = ((POP3Folder) folder).getUID(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return uid;
        }
    }


    /**
     * @Purpose 获取IMAP 的 uid
     * @Author Naruto Yang
     * @CreateDate 2018/8/18 0018
     * @Note
     */
    public static class GetIMAPUid implements GetUidInterface {
        @Override
        public String getUid(Folder folder, Message message) {
            String uid = "";
            try {
                uid = Long.toString(((IMAPFolder) folder).getUID(message));
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return uid;
        }
    }


    /**
     * @Purpose 获取邮件UID的接口
     * @Author Naruto Yang
     * @CreateDate 2018/8/18 0018
     * @Note
     */
    public interface GetUidInterface {
        String getUid(Folder folder, Message message);
    }

}
