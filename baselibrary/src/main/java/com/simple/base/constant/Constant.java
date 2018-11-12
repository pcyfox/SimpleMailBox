package com.simple.base.constant;


public class Constant {
    public static String separator1 = "☆";//将收件人名字和邮箱账号拼接起来，拼接符
    public static String separator2 = "★";//收件人与收件人之间拼接起来，拼接符
    //上传文件总共大小（50M）
    public static final int MAX_TOTAL_SIZE = 50 * 1048576;
    // 最大录音时长1000*60*10(分钟)
    public static final int MAX_RECORD_LENGTH = 1000 * 60 * 10;
    // 图片缓存最大容量
    public static final long MAX_PIC_CACHE = 1024 * 1024 * 500;//500M
}
