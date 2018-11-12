package com.simple.mail.utils;

import android.os.Handler;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.simple.base.utils.ImageUtils;
import com.simple.mail.entity.AddressInfo;
import com.simple.mail.entity.Attach;
import com.simple.mail.entity.Image;
import com.simple.mail.entity.Mail;
import com.simple.mail.entity.Person;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;


public class MailSender {
    private static final String TAG = "MailSender";

    private ArrayList<Image> images;//一个有规则的ArrayList，用作嵌入图片
    private AddressInfo addresser;
    private Mail mail;

    public MailSender(AddressInfo addresser, Mail mail, ArrayList<Image> images) {
        this.images = images;
        this.addresser = addresser;
        this.mail = mail;
    }


    private int sendMail() {
        try {
            Session session = LoginUtils.getSessionSMTP(addresser.sendProtocol, addresser.email_account, addresser.email_password);
            MimeMessage msg = createMessage(session);
            Transport transport = session.getTransport(addresser.sendProtocol.protocl);//设置通讯协议
            transport.connect(addresser.email_account, addresser.email_password);//连接
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
            return Mail.MAIL_TYPE_OUTBOX;//发送失败
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Mail.MAIL_TYPE_OUTBOX;//发送失败
        } catch (IOException e) {
            e.printStackTrace();
            return Mail.MAIL_TYPE_OUTBOX;//发送失败
        }
        return Mail.MAIL_TYPE_SEND;//发送成功
    }

    /**
     * 发送邮件
     *
     * @param handler
     */
    public boolean send(Handler handler) {
        mail.folderType = Mail.MAIL_TYPE_OUTBOX;//发送之前先保存到发件箱
        //TODO
       // mail.id = DBMail.getInstance().addOrUpdateMail(mail);
        List<Person> receiverList = new ArrayList<>();//所有收件人
        receiverList.addAll(mail.tosList);
        receiverList.addAll(mail.ccsList);
        receiverList.addAll(mail.bccsList);
        mail.folderType = sendMail();//发送邮件并更新为已发送

        boolean isSendSuccess = mail.folderType == Mail.MAIL_TYPE_SEND;
        if (isSendSuccess) {//发送成功
           //TODO
           // DBMail.getInstance().updateMailType(mail.id, Mail.MAIL_TYPE_SEND);
            //保存最近联系人
            //TODO
          //  Jna.getInstance().saveNearestContacts(mail.tosList);
            //添加成功之后，将不在联系人列表里的收件人，密送人，抄送人添加到联系人列表
            addContacts(receiverList, handler);
        }

        return  isSendSuccess;

    }


    public MimeMessage createMessage(Session session) throws IOException, MessagingException {
        MimeMessage message = new MimeMessage(session);
        //设置发件人
        message.setFrom(new InternetAddress(mail.senderEmail, mail.senderName));
        //设置收件人，抄送人，密送人
        setReceiver(message, Message.RecipientType.TO, mail.tosList);
        setReceiver(message, Message.RecipientType.CC, mail.ccsList);
        setReceiver(message, Message.RecipientType.BCC, mail.bccsList);
        message.setSubject(mail.subject);//设置主题
        message.setSentDate(new Date()); // 发送日期
        //创建代表邮件正文和附件的各个MimeBodyPart对象
        MimeMultipart allMultipart = new MimeMultipart();
        MimeBodyPart contentpart = createContent(mail.contentNoImage);
        allMultipart.addBodyPart(contentpart);

        for (int i = 0; i < mail.attchList.size(); i++) {//创建用于组合邮件正文和附件的MimeMultipart对象
            allMultipart.addBodyPart(createAttachment(mail.attchList.get(i)));
        }
        message.setContent(allMultipart);//设置整个邮件内容为最终组合出的MimeMultipart对象
        message.saveChanges();
        return message;
    }

    /**
     * 设置收件人，抄送人，密送人
     */
    private void setReceiver(MimeMessage message, Message.RecipientType type, ArrayList<Person> list) throws UnsupportedEncodingException, MessagingException {
        if (list.size() > 0) {
            InternetAddress[] tos = new InternetAddress[list.size()];
            for (int i = 0; i < list.size(); i++) {
                tos[i] = new InternetAddress(list.get(i).email, list.get(i).name);
            }
            message.setRecipients(type, tos);
        }
    }

    public MimeBodyPart createContent(String body) throws MessagingException, IOException {
        //创建代表组合Mime消息的MimeMultipart对象，将该MimeMultipart对象保存到MimeBodyPart对象
        MimeBodyPart contentPart = new MimeBodyPart();
        MimeMultipart contentMultipart = new MimeMultipart("related");
        //创建用于保存HTML正文的MimeBodyPart对象，并将它保存到MimeMultipart中
        MimeBodyPart htmlbodypart = new MimeBodyPart();
        htmlbodypart.setContent(body, "text/html;charset=UTF-8");
        contentMultipart.addBodyPart(htmlbodypart);

        for (int i = 0; i < images.size(); i++) {
            //创建用于保存图片的MimeBodyPart对象，并将它保存到MimeMultipart中
            Image image = images.get(i);
            MimeBodyPart gifBodyPart = new MimeBodyPart();
            gifBodyPart.isMimeType("image/*");
            //压缩图片
            String path = ImageUtils.getSendImage(image.path);

            FileDataSource fileDataSource = new FileDataSource(path);
            gifBodyPart.setDataHandler(new DataHandler(fileDataSource));//图片所在的目录的绝对路径
            gifBodyPart.setContentID(image.cid);   //cid的值
            gifBodyPart.setFileName(MimeUtility.encodeText(TextUtils.isEmpty(image.name) ? fileDataSource.getName() : image.name));
            gifBodyPart.setDisposition(Part.INLINE);
            contentMultipart.addBodyPart(gifBodyPart);
        }
        //将MimeMultipart对象保存到MimeBodyPart对象
        contentPart.setContent(contentMultipart);
        return contentPart;
    }

    public MimeBodyPart createAttachment(Attach attach) throws MessagingException, UnsupportedEncodingException {
        if (attach.mpart != null) {//attach.mpart!=null 此时是转发邮件，直接取服务器的mpart,如果attach.mpart==null，则证明是自己写邮件的时候添加的附件。
            return (MimeBodyPart) attach.mpart;
        }
        //创建保存附件的MimeBodyPart对象，并加入附件内容和相应的信息
        String filePath = attach.file_path;
        MimeBodyPart attachPart = new MimeBodyPart();
        FileDataSource fsd = new FileDataSource(filePath);
        attachPart.setDataHandler(new DataHandler(fsd));
        attachPart.setDisposition(Part.ATTACHMENT);
        attachPart.setFileName(MimeUtility.encodeText(fsd.getName()));
        return attachPart;
    }

    /**
     * 将收件人，密送人，抄送人添加到联系人数据库
     *
     * @param list
     */
    public void addContacts(List<Person> list, Handler handler) {
        if (list == null || list.size() == 0) {
            return;
        }
        JsonArray jsonArray = new JsonArray();
        for (Person p : list) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", p.name);
            jsonObject.addProperty("email", p.email);
            jsonObject.addProperty("mobile", "");
            jsonArray.add(jsonObject);
        }
        //TODO
       // Jna.getInstance().batchAddContactsForEmail(jsonArray.toString(), handler);
    }


}



