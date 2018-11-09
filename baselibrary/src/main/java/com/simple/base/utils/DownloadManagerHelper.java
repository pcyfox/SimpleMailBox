package com.simple.base.utils;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author 潘城尧
 */
public class DownloadManagerHelper {
    private static final String TAG = "DownloadManagerHelper";
    private String downloadFile = "";
    private String storePath = "";// 下载文件存放路径
    private DownloadManager downloadManager;
    private String fileName = "";

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    private Context context;
    private MyDownloadStatusListener downloadListener;
    private Object tag;

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    private boolean notificationVisibility = false;

    public boolean isNotificationVisibility() {
        return notificationVisibility;
    }

    public void setNotificationVisibility(boolean notificationVisibility) {
        this.notificationVisibility = notificationVisibility;
    }

    public DownloadManagerHelper(Context context, String storePath) {
        this.storePath = storePath;
        this.context = context;
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public DownloadManagerHelper(Context context) {
        this.context = context;
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public String getStorePath() {
        return storePath;
    }

    /**
     * @param storePath 下载文件存储路径
     */
    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

    public void startDownload(String url) {
        if (TextUtils.isEmpty(storePath)) {
            throw new IllegalArgumentException("storePath isEmpty ");
        }
        fileName = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
        try {
            fileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 文件完整路径
        String filePath = storePath +File.separator+ url.substring(url.lastIndexOf("/") + 1, url.length());
        // 发送请求
        DownloadAndSave(url, getFileType(url), fileName, storePath);
    }

    // 从文件url取得文件后缀名（即文件类型）
    private String getFileType(String url) {
        String[] strarray = url.split("\\.");
        return strarray[strarray.length - 1];

    }


    private void DownloadAndSave(String url, String fileType, String fileName, String fileSaveFolder) {
        try {
            Request request = new Request(Uri.parse(url));
            request.setAllowedNetworkTypes(
                    Request.NETWORK_MOBILE | Request.NETWORK_WIFI)
                    .setAllowedOverRoaming(true) // 缺省是true
                    .setTitle("犀牛云客") // 用于信息查看
                    .setDescription(fileName + "." + fileType) // 用于信息查看
                    .setDestinationInExternalPublicDir("/" + fileSaveFolder + "/", fileName + "." + fileType);
            if (!notificationVisibility) {
                request.setNotificationVisibility(Request.VISIBILITY_HIDDEN);
            }

            downloadManager.enqueue(request); // 加入下载队列
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 播接收器,接收并处理DownloadManager发出的广播
    public class DownLoadBroadReceiver extends BroadcastReceiver {
        public DownLoadBroadReceiver(MyDownloadStatusListener listener) {
            downloadListener = listener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                DownloadManager.Query query = new DownloadManager.Query();
                // 在广播中取出下载任务的id
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                Uri uri = downloadManager.getUriForDownloadedFile(id);
                if (uri == null) {
                    return;
                }
                query.setFilterById(id);
                Cursor c = downloadManager.query(query);
                if (c.moveToFirst()) {
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    switch (status) {

                        case DownloadManager.STATUS_FAILED:
                            downloadListener.downLoadFailure(tag);
                            downloadManager.remove(id);
                            break;

                        case DownloadManager.STATUS_SUCCESSFUL:

                            int fileUriIdx = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                            String fileUri = c.getString(fileUriIdx);
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                                if (fileUri != null) {
                                    downloadFile = Uri.parse(fileUri).getEncodedPath();
                                }
                            } else {
                                //Android 7.0以上的方式：请求获取写入权限，这一步报错
                                //过时的方式：DownloadManager.COLUMN_LOCAL_FILENAME
                                int fileNameIdx = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                                downloadFile = c.getString(fileNameIdx);
                            }

                            Log.d(TAG, "downloadFile  : "+downloadFile);

                            if (downloadFile != null && downloadFile.length() > 0) {
                                downloadListener.downLoadSuccess(downloadFile, tag);
                            }

                            break;

                    }

                }

            } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())) {

            }

        }

    }

    // 注册下载广播接收器
    public void registerBroadReceiver(DownLoadBroadReceiver receiver) {
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(receiver, filter);
    }

    // 反注册下载广播接收器
    public void unRegisterBroadReceiver(DownLoadBroadReceiver receiver) {
        if (receiver != null && context != null) {
            context.unregisterReceiver(receiver);
        }
    }

    public interface MyDownloadStatusListener {
        void downLoadSuccess(String downloadFile, Object tag);

        void downLoadFailure(Object tag);
    }

}
