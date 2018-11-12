package com.simple.mail.utils;


import android.text.TextUtils;

import com.eims.basemodule.utils.FileUtils;
import com.eims.yunke.constant.UserConfig;
import com.eims.yunke.db.DBMail;
import com.eims.yunke.entity.Addresser;
import com.eims.yunke.entity.Attach;
import com.eims.yunke.entity.Image;
import com.eims.yunke.entity.Mail;
import com.eims.yunke.entity.MailContact;
import com.eims.yunke.entity.Person;
import com.eims.yunke.manager.StringManager;
import com.simple.mail.entity.Mail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

/**
 * 删除与移动邮件
 */
public class MailUtils {
    private static final String TAG = "MailUtils";

    /**
     * @param mail
     */
    public static void initMail(Mail mail) {
        if (mail == null) return;
        mail.fromContacts = new MailContact(TextUtils.isEmpty(mail.nativeFromName) ? mail.senderName : mail.nativeFromName, mail.senderEmail);
//        m.attachments_url = mailDetails.attachments_url;
//        m.attchsList = StringManager.conversionToAttachs(m.attachments_url);
        mail.attchsList = new ArrayList<>();
        mail.tosList = StringManager.conversionToContactses(mail.toAddressee);
        mail.ccsList = StringManager.conversionToContactses(mail.cc);
        mail.bccsList = StringManager.conversionToContactses(mail.bcc);
        mail.imagesList = new ArrayList<>();
        List<String> imgSrc = StringManager.getImgSrc(mail.content);
        for (int i = 0; i < imgSrc.size(); i++) {
            String path = imgSrc.get(i);
            File file = new File(path);
            mail.imagesList.add(new Image(path, file.getName() + ".jpg", FileUtils.getMd5(file)));
        }
    }

    /**
     * 删除邮件（单个删除）
     */
    public static boolean deleteMail(int position) {
        Mail removeMail = UserConfig.inbox.remove(position);
        return DBMail.getInstance().deleteMail(removeMail.id, removeMail.folderType);
    }

    /**
     * 删除邮件（批量删除）
     */
    public static void deleteMailAll() {
        StringBuffer sb = new StringBuffer();
        int srcDir = -1;
        for (int i = UserConfig.inbox.size() - 1; i > -1; i--) {
            Mail mail = UserConfig.inbox.get(i);
            if (mail.isSelected) {
                UserConfig.inbox.remove(i);

                sb.append(mail.id + ",");
                srcDir = mail.folderType;
            }
        }
        if (!TextUtils.isEmpty(sb) && srcDir != -1) {
            DBMail.getInstance().deleteMail(sb.toString(), srcDir);
        }
    }

    /**
     * 将邮件批量恢复至收件箱
     */
    public static void renewEmail() {

        StringBuffer sb = new StringBuffer();
        for (int i = UserConfig.inbox.size() - 1; i > -1; i--) {
            Mail mail = UserConfig.inbox.get(i);
            if (mail.isSelected) {
                mail.folderType = Mail.MAIL_TYPE_INBOX;
                UserConfig.inbox.remove(i);
                sb.append(mail.id + ",");
            }
        }
        DBMail.getInstance().updateMailType(sb.toString(), Mail.MAIL_TYPE_INBOX);
    }

    /**
     * 构建回复邮件的收件人
     *
     * @param mail
     * @param isReplyAll
     * @return
     */
    public static Map<String, ArrayList<Person>> getReceiverForReply(Mail mail, boolean isReplyAll) {
        Map<String, ArrayList<Person>> map = new HashMap<>();
        ArrayList<Person> replyToList = new ArrayList<>();
        ArrayList<Person> replyCcList = new ArrayList<>();
        if (mail.folderType == Mail.MAIL_TYPE_SEND) {//已发送的邮件
            replyToList.addAll(mail.tosList);
            replyCcList.addAll(mail.ccsList);
        } else {//收件箱里的邮件
            Person currentPerson = new Person("", UserConfig.addresser.email_account);
            if (isReplyAll) {//回复全部
                //一般情况下bcc内收件人比较少，故出于性能考虑，先从bcc开始检查
                if (mail.bccsList.size() > 0 && isPersonInList(currentPerson, mail.bccsList)
                        && !(isPersonInList(currentPerson, mail.tosList) || isPersonInList(currentPerson, mail.ccsList))) {
                    //当前用户在bccList，且不是该邮件的to或cc对象，那么就是bcc对象
                    replyToList.add(mail.fromContacts);
                } else {//当前用户为该邮件的to或cc对象
                    replyToList.add(mail.fromContacts);
                    replyToList.addAll(mail.tosList);
                    replyCcList.addAll(mail.ccsList);
                    removePersonByAddress(UserConfig.addresser.email_account, replyToList);
                    removePersonByAddress(UserConfig.addresser.email_account, replyCcList);
                }
            } else {//单独回复
                replyToList.add(mail.fromContacts);
            }
        }
        map.put("to", replyToList);
        map.put("cc", replyCcList);
        return map;
    }

    /**
     * 根据邮箱删除list中的person
     *
     * @param address
     * @param list
     */
    public static void removePersonByAddress(String address, List<Person> list) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).email.equals(address)) {
                    list.remove(i--);
                }
            }
        }
    }

    /**
     * 判断list中是否存在与person相同的邮箱
     *
     * @param person
     * @param list
     * @return
     */
    public static boolean isPersonInList(Person person, List<Person> list) {
        if (list == null || list.size() == 0) {
            return false;
        }
        boolean isIn = true;
        for (Person p : list) {
            if (p.email.equals(person.email)) {
                isIn = true;
                break;
            }
        }
        return isIn;
    }

    /**
     * 重新获取该邮件对应的附件
     */
    public static List<Attach> getAttach(String mailUid, String sendTime, Addresser addresser) {
        Mail mail = new Mail("-1");
        mail.uid = mailUid;
        mail.sendTime=sendTime;
        try {
            MailReceiver.getInBoxMailMessage(mail, addresser, null);
            mail.getAttchs(mail.message); //得到附件的列表
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mail.attchsList;
    }

}
