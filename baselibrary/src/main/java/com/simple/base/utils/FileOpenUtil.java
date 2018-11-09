package com.simple.base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;


import com.simple.base.base.BaseApplication;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


/**
 * 文件工具类
 * 提供文件打开、删除功能
 */

public class FileOpenUtil {

    private static String authorities = "com.eims.yunke.fileprovider";

    public static String getAuthorities() {
        return authorities;
    }

    public static void setAuthorities(String authorities) {
        FileOpenUtil.authorities = authorities;
    }

    // 文件后缀
    private static final String[] VIDEO_EXTENSIONS = {"264", "3g2", "3gp", "3gp2", "3gpp", "3gpp2", "3mm", "3p2", "60d",
            "aep", "ajp", "amv", "amx", "arf", "asf", "asx", "avb", "avd", "avi", "avs", "avs", "axm", "bdm", "bdmv",
            "bik", "bin", "bix", "bmk", "box", "bs4", "bsf", "byu", "camre", "clpi", "cpi", "cvc", "d2v", "d3v", "dat",
            "dav", "dce", "dck", "ddat", "dif", "dir", "divx", "dlx", "dmb", "dmsm", "dmss", "dnc", "dpg", "dream",
            "dsy", "dv", "dv-avi", "dv4", "dvdmedia", "dvr-ms", "dvx", "dxr", "dzm", "dzp", "dzt", "evo", "eye", "f4p",
            "f4v", "fbr", "fbr", "fbz", "fcp", "flc", "flh", "fli", "flv", "flx", "gl", "grasp", "gts", "gvi", "gvp",
            "hdmov", "hkm", "ifo", "imovi", "imovi", "iva", "ivf", "ivr", "ivs", "izz", "izzy", "jts", "lsf", "lsx",
            "m15", "m1pg", "m1v", "m21", "m21", "m2a", "m2p", "m2t", "m2ts", "m2v", "m4e", "m4u", "m4v", "m75", "meta",
            "mgv", "mj2", "mjp", "mjpg", "mkv", "mmv", "mnv", "mod", "modd", "moff", "moi", "moov", "mov", "movie",
            "mp21", "mp21", "mp2v", "mp4", "mp4v", "mpe", "mpeg", "mpeg4", "mpf", "mpg", "mpg2", "mpgin", "mpl", "mpls",
            "mpv", "mpv2", "mqv", "msdvd", "msh", "mswmm", "mts", "mtv", "mvb", "mvc", "mvd", "mve", "mvp", "mxf",
            "mys", "ncor", "nsv", "nvc", "ogm", "ogv", "ogx", "osp", "par", "pds", "pgi", "piv", "playlist", "pmf",
            "prel", "pro", "prproj", "psh", "pva", "pvr", "pxv", "qt", "qtch", "qtl", "qtm", "qtz", "rcproject", "rdb",
            "rec", "rm", "rmd", "rmp", "rms", "rmvb", "roq", "rp", "rts", "rts", "rum", "rv", "sbk", "sbt", "scm",
            "scm", "scn", "sec", "seq", "sfvidcap", "smil", "smk", "sml", "smv", "spl", "ssm", "str", "stx", "svi",
            "swf", "swi", "swt", "tda3mt", "tivo", "tix", "tod", "tp", "tp0", "tpd", "tpr", "trp", "ts", "tvs", "vc1",
            "vcr", "vcv", "vdo", "vdr", "veg", "vem", "vf", "vfw", "vfz", "vgz", "vid", "viewlet", "viv", "vivo",
            "vlab", "vob", "vp3", "vp6", "vp7", "vpj", "vro", "vsp", "w32", "wcp", "webm", "wm", "wmd", "wmmp", "wmv",
            "wmx", "wp3", "wpl", "wtv", "wvx", "xfl", "xvid", "yuv", "zm1", "zm2", "zm3", "zmv"};

    private static final HashSet<String> vHashSet = new HashSet<String>(Arrays.asList(VIDEO_EXTENSIONS));

    // 是否是video
    public static boolean isVideo(String path) {
        path = getFileExtension(path);
        return vHashSet.contains(path);
    }

    // 获取文件后缀
    public static String getFileExtension(String path) {
        if (null != path) {
            int dex = path.lastIndexOf(".");
            return path.substring(dex + 1);
        }
        return null;
    }

    /**
     * 打开文件
     *
     * @param filePath
     * @param fileName 打开文件需要根据文件扩展名确定文件类型，文件路径不一定包含文件扩展名，故需要传入文件名
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static Intent openFileIntent(String filePath, String fileName) {

        File file = new File(filePath);
        if (!file.exists())
            return null;

        String end = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();

        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg")
                || end.equals("wav")) {
            return getAudioFileIntent(filePath);
        } else if (end.equals("3gp") || end.equals("mp4")) {
            return getVideoFileIntent(filePath);
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg")
                || end.equals("bmp")) {
            return getImageFileIntent(filePath);
        } else if (end.equals("apk")) {
            return getApkFileIntent(filePath);
        } else if (end.equals("ppt")) {
            return getPptFileIntent(filePath);
        } else if (end.equals("xls") || end.equals("xlsx")) {
            return getExcelFileIntent(filePath);
        } else if (end.equals("doc") || end.equals("docx")) {
            return getWordFileIntent(filePath);
        } else if (end.equals("pdf")) {
            return getPdfFileIntent(filePath);
        } else if (end.equals("chm")) {
            return getChmFileIntent(filePath);
        } else if (end.equals("txt")) {
            return getTextFileIntent(filePath);
        } else if (end.equals("zip")) {
            Intent intent = getZIPFileIntent(filePath);
            if (!isIntentAvaileble(BaseApplication.getApplication(), intent)) {
                intent = getAllIntent(filePath);
            }
            ;
            return intent;
        } else if (end.equals("eml")) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            return build(intent, new File(filePath), "message/rfc822");
        } else {
            return getAllIntent(filePath);
        }
    }


    private static boolean isIntentAvaileble(Context context, Intent intent) {
        List<ResolveInfo> resolves = context.getPackageManager().queryIntentActivities(intent, 0);
        return resolves.size() > 0;
    }


    // 获取打开全部类型文件intent
    private static Intent getAllIntent(String param) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        return build(intent, new File(param), "*/*");
    }

    // 获取打开apk类型文件intent
    private static Intent getApkFileIntent(String param) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        return build(intent, new File(param), "application/vnd.android.package-archive");
    }

    // 获取打开video类型文件intent
    private static Intent getVideoFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        return build(intent, new File(param), "video/*");
    }

    // 获取打开audio类型文件intent
    private static Intent getAudioFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        return build(intent, new File(param), "audio/*");
    }

    // 获取打开html类型文件intent
    public static Intent getHtmlFileIntent(String param) {
        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content")
                .encodedPath(param).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    // 获取打开image类型文件intent
    private static Intent getImageFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        return build(intent, new File(param), "image/*");
    }

    // 获取打开ppt类型文件intent
    private static Intent getPptFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        return build(intent, new File(param), "application/vnd.ms-powerpoint");
    }

    // 获取打开excel类型文件intent
    private static Intent getExcelFileIntent(String param) {
        Log.d("dhc", "filePath:" + param);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        return build(intent, new File(param), "application/vnd.ms-excel");
    }

    // 获取打开word类型文件intent
    private static Intent getWordFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        return build(intent, new File(param), "application/msword");
    }

    // 获取打开chm类型文件intent
    private static Intent getChmFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        return build(intent, new File(param), "application/x-chm");
    }

    // 获取打开text类型文件intent
    private static Intent getTextFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        return build(intent, new File(param), "text/plain");
    }

    // 获取打开pdf类型文件intent
    private static Intent getPdfFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        return build(intent, new File(param), "application/pdf");
    }


    private static Intent getZIPFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        return build(intent, new File(param), "zip/plain");
    }

    private static Intent build(Intent intent, File file, String type) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(BaseApplication.getApplication(), authorities, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, type);
        } else {
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, type);
        }
        return intent;
    }

    // 检测apk是否存在
    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 用指定应用打开指定视频
     */
    public static void openVideo(final String videoPlayer, Context context, String voideoPath) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(videoPlayer));
        intent.setData(uri);
        intent.setPackage(videoPlayer);
        context.startActivity(intent);
    }


}
