package com.simple.mail.entity;

import android.text.TextUtils;

import com.simple.base.config.AppConfig;
import com.simple.base.constant.Constant;
import com.simple.base.utils.FileUtils;
import com.simple.mail.UserConfig;
import com.simple.mail.utils.StringManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;

import static com.simple.mail.entity.Mail.MAIL_TYPE_INBOX;

public class MailParser {

    private OnPartFileParserListener onPartFileParserListener;

    public OnPartFileParserListener getOnPartFileParser() {
        return onPartFileParserListener;
    }

    public void setPartFileParserListener(OnPartFileParserListener onPartFileParser) {
        this.onPartFileParserListener = onPartFileParser;
    }

    public Mail parseMessageToMail(Message message, String mailId) throws MessagingException, UnsupportedEncodingException {
        Mail mail = new Mail(mailId);
        InternetAddress[] address = (InternetAddress[]) message.getFrom();
        if (address != null) {
            initfroms(address,mail);
            mail.subject = initSubject(message);
            mail.contentNoImage = parseTMLContentNoImage(message, new StringBuffer());
            mail.contentSynopsis = StringManager.removeHtmlTag(mail.contentNoImage);
            mail.readStatus = 1;
            if (message.getSentDate() == null) {
                Date receivedDate = message.getReceivedDate();
                mail.sendTime = receivedDate == null ? "" : Long.toString(receivedDate.getTime());
            } else {
                mail.sendTime = Long.toString(message.getSentDate().getTime());
            }
            mail.folderType = MAIL_TYPE_INBOX;
            mail.tosList = StringManager.conversionToContactses(mail.toAddressee, UserConfig.addresser.email_account);
            mail.attach = getAttachCount(message);
        }
        return mail;
    }

    /**
     * 初始化化发件人
     */
    public void initfroms(InternetAddress[] address, Mail mail) throws MessagingException {
        mail.senderEmail = address[0].getAddress();

        if (TextUtils.isEmpty(mail.senderEmail)) {
            throw new NullPointerException();
        }
        //检查是否为自己发出的
        if (mail.senderEmail.equals(UserConfig.addresser)) {
            mail.nativeFromName = "我";
        }

        String personal = address[0].getPersonal();
        if (TextUtils.isEmpty(personal)) {
            mail.senderName = mail.senderEmail;
        } else {
            mail.senderName = personal;
        }
    }





    public int getAttachCount(Part part) {
        int attachCount = -1;
        try {
            attachCount = parseAttachment(part, false).size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attachCount;
    }

    /**
     * 判断是否有附件 0 代表没有附近   非0代表有附件
     */
    public int isContainAttch(Part part) {
        return getAttachCount(part) == 0 ? 0 : 1;
    }

    /**
     * 解析附件
     *
     * @param part
     * @param isNeedGetAttachmentData 是否需要解析附件数据（获取附件数量不需要解析附件数据）
     * @return
     * @throws Exception
     */
    public List<Attach> parseAttachment(Part part, boolean isNeedGetAttachmentData) throws Exception {
        String fileName = "";
        List<Attach> attachList = new ArrayList<>();
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mpart = mp.getBodyPart(i);
                String disposition = mpart.getDisposition();
                String description = mpart.getDescription();

                if (disposition != null && disposition.equals(Part.ATTACHMENT)) {

                    if (isNeedGetAttachmentData) {
                        fileName = MimeUtility.decodeText(mpart.getFileName());
                        attachList.add(buildAttach(fileName, mpart));
                    }
                } else if (description != null && description.equals("Undelivered Message")) {//退信
                    if (isNeedGetAttachmentData) {
                        MimeBodyPart mbp = new MimeBodyPart(mpart.getInputStream());
                        fileName = MimeUtility.decodeText(mbp.getHeader("Subject", null)) + ".eml"; //仿照outlook,用退件的标题做文件名
                        attachList.add(buildAttach(fileName, mpart));

                    }
                } else if (mpart.isMimeType("multipart/*")) {
                    attachList.addAll(parseAttachment(mpart, isNeedGetAttachmentData));
                }
            }
        } else if (part.isMimeType("message/rfc822")) {//RFC822邮件格式
            attachList.addAll(parseAttachment((Part) part.getContent(), isNeedGetAttachmentData));
        }
        return attachList;
    }

    private Attach buildAttach(String fileName, BodyPart part) throws IOException, MessagingException {
        String path = AppConfig.ATTACH_PATH + fileName;
        return new Attach(fileName, path, part.getInputStream(), part, StringManager.formatFileSize(part.getSize()));

    }

    /**
     * 将附件地址转换成string，并且将附件简介保存到数据库中
     */
    public String getAttchs(Part part) throws Exception {
        return StringManager.conversionToAttachsString(parseAttachment(part, true));
    }

    /**
     * 网上获得附件之后，将附件简介保存到数据库
     */
    public void saveAttachToDb(List<Attach> attachList, String id/*邮件id*/) {
        if (attachList != null) {
            for (int i = 0; i < attachList.size(); i++) {
                Attach attach = attachList.get(i);
                attach.email_id = id;
                attach.attach_cid = String.valueOf(i);//因为附件没有cid这里用附件的索引做cid

                //TODO：将附件简介保存到数据库中
                // Jna.getInstance().saveMailAttachIntroduction(attach.email_id, attach.attach_cid, attach.attach_size, attach.attach_name, true, attach.is_download);
            }

        }
    }

    public void getTosCcsBccs(Mail mail, Message message) {
        try {
            mail.toAddressee = getMailAddress(message, Message.RecipientType.TO);
            mail.cc = getMailAddress(message, Message.RecipientType.CC);
            mail.bcc = getMailAddress(message, Message.RecipientType.BCC);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得标题
     */
    public String initSubject(Message message) throws MessagingException, UnsupportedEncodingException {
        String subject = "";
        String[] strs = message.getHeader("Subject");
        if (strs != null) {
            for (int i = 0; i < strs.length; i++) {
                subject = new String(strs[i].getBytes("ISO8859_1"), "GBK");//一般主题不会有多个吧?其它属性自己处理啦
            }
            return MimeUtility.decodeText(subject);//base64解码
        }
        return subject;
    }


    /**
     * 解犀邮件html内容，处理其中嵌套的图片（下载、保存、显示）
     */
    public ImageMailParserResult parseHTMLContentWithImage(String mailID, Part message, StringBuffer bodyText, String storePath) {
        List<Image> imagesList = new ArrayList<>();
        try {
            if (message.isMimeType("message/rfc822")) {
                parseHTMLContentWithImage(mailID, message, bodyText, storePath);
            } else if (message.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.isMimeType("multipart/*")) {
                        parseHTMLContentWithImage(mailID, bodyPart, bodyText, storePath);
                    }
                    if (bodyPart.isMimeType("text/html")) {
                        bodyText.append((String) bodyPart.getContent());
                    }
                    if (bodyPart.isMimeType("image/*")) {
                        // 得到HTML文件中所有图片的Content-ID,里面包含图片的cid名称
                        String[] contentIDs = bodyPart.getHeader("Content-ID");
                        if (contentIDs != null && contentIDs.length != 0) {

                            for (int j = 0; j < contentIDs.length; j++) {
                                String cid = "";
                                // cid名称规范化
                                if (contentIDs[0].startsWith("<") && contentIDs[0].endsWith(">")) {
                                    cid = contentIDs[0].substring(1, contentIDs[0].length() - 1);
                                } else {
                                    cid = contentIDs[0] + "\"";
                                }
                                contentIDs[0] = "\"cid:" + cid;
                                //创建本地文件并写入bodyPart中文件
                                File file = FileUtils.makeFilePath(storePath, MimeUtility.decodeText(bodyPart.getFileName()));
                                BufferedInputStream bis = new BufferedInputStream(bodyPart.getInputStream());
                                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file), 4 * 1024 * 1024);
                                int len = -1;
                                while ((len = bis.read()) != -1) {
                                    bos.write(len);
                                    bos.flush();
                                }
                                bos.close();
                                bis.close();

                                //将文件保存至磁盘并返回其绝对路径
                                String path = "";
                                if (onPartFileParserListener != null) {
                                    path = onPartFileParserListener.onResolvedFile(file);
                                }

                                if (TextUtils.isEmpty(path)) {
                                    path = file.getAbsolutePath();
                                } else {
                                    file.delete();//删除原文件
                                }

                                if (contentIDs[0].startsWith("\"")) {
                                    contentIDs[0] = contentIDs[0].substring(1, contentIDs[0].length());
                                }

                                if (contentIDs[0].endsWith("\"")) {
                                    contentIDs[0] = contentIDs[0].substring(0, contentIDs[0].length() - 1);
                                }

                                int index = bodyText.indexOf(contentIDs[0]);
                                // 交换图片内嵌地址
                                while (index != -1) {
                                    bodyText.replace(index, index + contentIDs[0].length(), "file://" + path);
                                    imagesList.add(new Image(path, file.getName(), cid));
                                    index = bodyText.indexOf(contentIDs[0]);
                                }
                            }
                        }
                    }
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ImageMailParserResult(bodyText.toString(), imagesList);
    }


    /**
     * 获得邮件text内容
     */
    public String parseTMLContentNoImage(Part message, StringBuffer bodyText) {
        try {
            if ((message.isMimeType("text/html") || message.isMimeType("text/plain")) && message.getContentType().indexOf("name") == -1) {
                String content = (String) message.getContent();
                bodyText.append(content);
            } else if (message.isMimeType("message/rfc822")) {
                parseTMLContentNoImage((Part) message.getContent(), bodyText);
            } else if (message.isMimeType("multipart/*")) {
                String disposition = message.getDisposition();

                if (disposition != null) {
                    if (disposition.equals(Part.ATTACHMENT) || disposition.equals(Part.INLINE)) {
                        return "";
                    }
                }

                Multipart multipart = (Multipart) message.getContent();
                int count = multipart.getCount();
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        Part p = multipart.getBodyPart(i);
                        String description = p.getDescription();
                        if (description != null && description.equals("Undelivered Message")) {//退信(退信内容会在解析附件时保存为.eml，所以此处不解析)
                            continue;
                        }
                        parseTMLContentNoImage(p, bodyText);
                    }
                }

            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bodyText.toString();
    }

    /**
     * 获得抄送，发送，密送人
     */
    public String getMailAddress(Message msg, Message.RecipientType type) throws
            MessagingException, UnsupportedEncodingException {
        StringBuilder mailAddr = new StringBuilder();
        InternetAddress[] address = (InternetAddress[]) msg.getRecipients(type);
        if (address != null && address.length > 0) {
            for (int i = 0; i < address.length; i++) {
                String mail = address[i].getAddress();
                if (mail == null) {
                    continue;
                } else {
                    mail = MimeUtility.decodeText(mail);
                }
                String personal = address[i].getPersonal();
                if (personal == null) {
                    personal = mail;
                } else {
                    personal = MimeUtility.decodeText(personal);
                }
                String compositeTo = personal + Constant.separator1 + mail + Constant.separator2;
                mailAddr.append(compositeTo);
            }
            return mailAddr.substring(0, mailAddr.length() - 1);
        }
        return "";
    }

    /**
     * 邮件是否读过
     */
    public boolean isSeen(Message message) {
        boolean isSeen = false;
        try {
            Flags flags = message.getFlags();
            Flags.Flag[] flag = flags.getSystemFlags();
            for (int i = 0; i < flag.length; i++) {
                if (flag[i] == Flags.Flag.SEEN) {
                    isSeen = true;
                    break;
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return isSeen;
    }

    interface OnPartFileParserListener {
        /**
         * 当Part中文件解析完成后会调用该方法，在这里你可以实现将文件存储到其它地方或实现其它操作
         *
         * @return 文件路径
         */
        String onResolvedFile(File file);
    }

}
