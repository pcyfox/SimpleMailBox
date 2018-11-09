package com.simple.base.config;

import android.os.Environment;

import com.simple.base.base.BaseApplication;


/**
 * @Purpose
 * @Author Naruto Yang
 * @CreateDate ${2018-06-01}
 * @Note
 */
public final class AppConfig {
    private AppConfig() {
    }

    //私有文件外置存储根目录(随着卸载自动删除)

    public static final String PRIVATE_FOLDER_ROOT = BaseApplication.getApplication().getExternalFilesDir(null).getPath();
    //外置存储根目录
    public static final String EXTERNAL_STORAGE_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();

    //私有文件存储目录
    public static final String PRIVATE_FOLDER_PATH = PRIVATE_FOLDER_ROOT + "/private";

    //公开文件存储目录
    public static final String PUBLIC_FOLDER_PATH = EXTERNAL_STORAGE_ROOT + "/public";

    //邮件接收附件目录
    public static final String ATTACH_PATH = PRIVATE_FOLDER_PATH + "/attach/";
    //邮件接收正文图片目录
    public static final String IMAGE_PATH = PRIVATE_FOLDER_PATH + "/image";
    //邮件待发送正文图片目录
    public static final String SEND_PATH = PRIVATE_FOLDER_PATH + "/send";
    //邮件拍照存放目录
    public static final String EMAIL_PHOTO_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/xiniuyun";

    //语音文件存放目录
    public static final String AUDIO_PATH = PRIVATE_FOLDER_PATH + "/record/";
    //拍照存放目录
    public static final String PHOTO_PATH = PUBLIC_FOLDER_PATH + "/photo/";
    //录像存放目录
    public static final String VIDEO_PATH = PUBLIC_FOLDER_PATH + "/vidio";
    //录像缩略图存放目录
    public static final String VIDEO_THUMBNAIL_PATH = PUBLIC_FOLDER_PATH + "/vidioThumbnail/";
    //文件下载保存路径
    public static final String DOWNLOAD_PATH = PUBLIC_FOLDER_PATH + "/download";
    //图片缓存（LRU）
    public static final String PIC_CACHE = PRIVATE_FOLDER_ROOT + "/picCache";
    //Glide图片缓存
    public static final String GLIDE_CACHE_NAME = "GlideCache";
    //语音缓存（LRU）
    public static final String VOICE_CACHE = PRIVATE_FOLDER_ROOT + "/voiceCache";

    //用于存放各种临时文件
    public static final String TEMP = PRIVATE_FOLDER_ROOT + "/temp";

    //数据库的路径
    public static final String SQL_PATH = PRIVATE_FOLDER_ROOT;
    //配置文件的路径
    public static final String CONFIG_PATH = PRIVATE_FOLDER_PATH + "/config";




}
