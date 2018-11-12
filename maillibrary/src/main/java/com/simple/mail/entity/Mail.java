package com.simple.mail.entity;


import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * 邮件的实体类
 */
public class Mail {
    private static final String TAG = "Mail";
    public final static int PAGE_COUNT = 20;

    /**
     * 收件箱
     */
    public final static int MAIL_TYPE_INBOX = 0;
    /**
     * 草稿箱
     */
    public final static int MAIL_TYPE_DRAFT = 1;
    /**
     * 发件箱
     */
    public final static int MAIL_TYPE_OUTBOX = 2;
    /**
     * 已发送邮件
     */
    public final static int MAIL_TYPE_SEND = 3;
    /**
     * 已删除邮件
     */
    public final static int MAIL_TYPE_DELETED = 4;
    /**
     * .eml附件
     */
    public final static int MAIL_TYPE_ATTACHMENT = 5;

    @Expose
    public String id;//邮件id
    @Expose
    public String mailId;//邮箱账号id
    @Expose
    public String uid;
    public String detailsStr;
    @Expose
    public String senderEmail;//发件人 邮箱账号
    @Expose
    public String senderName;//发件人 邮箱昵称
    @Expose
    public String nativeFromName;
    @Expose
    public String subject;
    @Expose
    public String contentSynopsis; // 邮件详情的简介，用于列表界面
    @Expose
    public int readStatus; // 邮件是否已读    0是已读   1是未读
    @Expose
    public String sendTime;//发送邮件的时间
    @Expose
    public int folderType = -1;//发件箱   收件箱  草稿箱
    @Expose
    public String toAddressee;//收件人连接成字符串
    @Expose
    public String cc;// 抄送人连接成字符串
    @Expose
    public String bcc;//密送人连接成字符串
    @Expose
    public String content; // 邮件详情(要图片的内容，<img>对应的是本地路径)
    @Expose
    public int attach;//是否有附件  attach   0代表没有附件
    @Expose
    public String attachments_url;//附件本地路径
    public String name;//列表上最终显示的名字。
    public int textColor;//mail展示的时候最前面的变色的圆圈颜色。1-8的随机数，随机生成
    public String contentNoImage;  //邮件详情(不要图片的内容，<img>对应的是uid,没有换成本地路径)
    public Contact fromContacts;

    public ArrayList<Person> tosList;//收件人集合
    public ArrayList<Person> ccsList;// 抄送人集合
    public ArrayList<Person> bccsList;//密送人集合

    public List<Attach> attchList;
    public ArrayList<Image> imagesList;
    public boolean isSelected = false;
    public boolean needFilter = false;//如果在邮件获取信息的时候，出现了错误，则需要过滤这封邮件。

    public Mail(String mail_id) {
        this.mailId = mail_id;
        this.textColor = (int) (Math.random() * 5);
        attchList = new ArrayList<>();
    }

    /**
     * 实例化收件箱未读邮件
     */
    public Mail(String mailId, String uid) {
        this(mailId);
        this.uid = uid;
    }



    @Override
    public String toString() {
        return "Mail{" +
                "id='" + id + '\'' +
                ", mailId='" + mailId + '\'' +
                ", uid='" + uid + '\'' +
                ", subject='" + subject + '\'' +
                ", readStatus=" + readStatus +
                ", sendTime='" + sendTime + '\'' +
                ", folderType=" + folderType +
                ", detailsStr=" + detailsStr +
                ", imagesList=" + imagesList +
                ", senderEmail=" + senderEmail +
                ", senderName=" + senderName +
                ", bcc=" + bcc +
                ", cc=" + cc +
                ", cc=" + cc +
                ", toAddressee=" + toAddressee +
                '}';
    }
}
