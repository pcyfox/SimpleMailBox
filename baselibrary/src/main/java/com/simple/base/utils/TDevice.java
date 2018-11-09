package com.simple.base.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import com.simple.base.base.BaseApplication;
import com.simple.base.manager.PreferenceManager;

import java.io.File;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.List;


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
/**
 * 主要是是与设备硬件及系统相关的方法
 */
public class TDevice {

    // 手机网络类型
    private static final int NETTYPE_WIFI = 0x01;
    private static final int NETTYPE_CMWAP = 0x02;
    private static final int NETTYPE_CMNET = 0x03;

    private static boolean GTE_HC;
    private static boolean GTE_ICS;
    private static boolean PRE_HC;
    private static Boolean _hasBigScreen = null;
    private static Boolean _hasCamera = null;
    private static Boolean _isTablet = null;
    private static Integer _loadFactor = null;
    private static final String TAG = "TDevice";

    public static float displayDensity = 0.0F;

    static {
        GTE_ICS = Build.VERSION.SDK_INT >= 14;
        GTE_HC = Build.VERSION.SDK_INT >= 11;
        PRE_HC = Build.VERSION.SDK_INT >= 11 ? false : true;
    }

    public TDevice() {
    }

    //包括虚拟键盘高度
    public static int[] getRealScreenSize(Activity activity) {
        int[] size = new int[2];
        int screenWidth = 0, screenHeight = 0;
        WindowManager w = activity.getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                screenWidth = (Integer) Display.class.getMethod("getRawWidth")
                        .invoke(d);
                screenHeight = (Integer) Display.class
                        .getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d,
                        realSize);
                screenWidth = realSize.x;
                screenHeight = realSize.y;
            } catch (Exception ignored) {
            }
        size[0] = screenWidth;
        size[1] = screenHeight;
        return size;
    }

    public static int getStatusBarHeight(Context ct) {
        if (ct == null) return -1;
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            return ct.getResources()
                    .getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获得屏幕高度(不包括底部虚拟按键)
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获取 虚拟按键的高度
     *
     * @param context
     * @return
     */
    public static int getBottomStatusHeight(Activity context) {
        int[] size = getRealScreenSize(context);
        int contentHeight = getScreenHeight(context);
        return size[1] - contentHeight;
    }

    /**
     * 获取 虚拟按键的高度
     * 与getBottomStatusHeight(Activity context)方法一样
     *
     * @param activity
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }


    public static int getSupportSoftKeyboardHeight(Activity activity) {
        Rect r = new Rect();
        int bottomStatusHeight = getBottomStatusHeight(activity);
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
        int softInputHeight = screenHeight - r.bottom;
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - bottomStatusHeight;
            //限制键盘高度不能超过屏幕尺寸的一半
        }
        if (softInputHeight < 0) {//发生这种情况说明屏幕底部有虚拟按键
            softInputHeight += bottomStatusHeight;
        }
        if (screenHeight > 100) PreferenceManager.setSoftKeyBoardHeight(softInputHeight);
        return softInputHeight;
    }

    public boolean hasBottomStatus(Activity context) {
        return getBottomStatusHeight(context) > 0;
    }

    /**
     * 检测是否有摄像头
     */
    public static final boolean hasCamera(Context ct) {
        if (ct == null) return false;
        if (_hasCamera == null) {
            PackageManager pckMgr = ct
                    .getPackageManager();
            boolean flag = pckMgr
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);

            boolean flag1 = pckMgr.hasSystemFeature(PackageManager.FEATURE_CAMERA);
            boolean flag2;
            if (flag || flag1)
                flag2 = true;
            else
                flag2 = false;
            _hasCamera = Boolean.valueOf(flag2);
        }
        return _hasCamera.booleanValue();
    }


    public static boolean hasHardwareMenuKey(Context context) {
        boolean flag = false;
        if (PRE_HC)
            flag = true;
        else if (GTE_ICS) {
            flag = ViewConfiguration.get(context).hasPermanentMenuKey();
        } else
            flag = false;
        return flag;
    }


    public static boolean gotoGoogleMarket(Activity activity, String pck) {
        try {
            Intent intent = new Intent();
            intent.setPackage("com.android.vending");
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + pck));
            activity.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isPackageExist(Context ct, String pckName) {
        if (ct == null || pckName == null) return false;
        try {
            PackageInfo pckInfo = ct.getPackageManager()
                    .getPackageInfo(pckName, 0);
            if (pckInfo != null)
                return true;
        } catch (NameNotFoundException e) {

        }
        return false;
    }


    public static void hideSoftKeyboard(View view) {
        if (view == null)
            return;
        ((InputMethodManager) view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                view.getWindowToken(), 0);
    }

    /**
     * 判断是否为横屏
     */
    public static boolean isLandscape(Context ct) {
        if (ct == null) return false;
        boolean flag;
        if (ct.getResources().getConfiguration().orientation == 2)
            flag = true;
        else
            flag = false;
        return flag;
    }

    /**
     * 判断是否为竖屏
     */
    public static boolean isPortrait(Context ct) {
        if (ct == null) return false;
        boolean flag = true;
        if (ct.getResources().getConfiguration().orientation != 1)
            flag = false;
        return flag;
    }

    /**
     * 判断是否为平板电脑
     */
    public static boolean isTablet(Context ct) {
        if (ct == null) return false;
        if (_isTablet == null) {
            boolean flag;
            if ((0xf & ct.getResources()
                    .getConfiguration().screenLayout) >= 3)
                flag = true;
            else
                flag = false;
            _isTablet = Boolean.valueOf(flag);
        }
        return _isTablet.booleanValue();
    }


    public static void showSoftKeyboard(Dialog dialog) {
        dialog.getWindow().setSoftInputMode(4);
    }

    public static void showSoftKeyboard(View view) {
        if (view == null) return;
        ((InputMethodManager) view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE)).showSoftInput(view,
                InputMethodManager.SHOW_FORCED);
    }

    public static void toogleSoftKeyboard(View view) {
        if (view == null) return;
        ;
        ((InputMethodManager) view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public static boolean isSdcardReady() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }
//
//    public static String getCurCountryLan() {
//        return BaseApplication.context().getResources().getConfiguration().locale
//                .getLanguage()
//                + "-"
//                + BaseApplication.context().getResources().getConfiguration().locale
//                .getCountry();
//    }

//    public static boolean isZhCN() {
//        String lang = BaseApplication.context().getResources()
//                .getConfiguration().locale.getCountry();
//        if (lang.equalsIgnoreCase("CN")) {
//            return true;
//        }
//        return false;
//    }

    public static String percent(double p1, double p2) {
        String str;
        double p3 = p1 / p2;
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        str = nf.format(p3);
        return str;
    }

    public static String percent2(double p1, double p2) {
        String str;
        double p3 = p1 / p2;
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(0);
        str = nf.format(p3);
        return str;
    }

    public static void gotoMarket(Context context, String pck) {
        if (!isHaveMarket(context)) {
//    AppContext.showToast("你手机中没有安装应用市场！");
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + pck));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static boolean isHaveMarket(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_MARKET");
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        return infos.size() > 0;
    }

    public static void openAppInMarket(Context context) {
        if (context != null) {
            String pckName = context.getPackageName();
            try {
                gotoMarket(context, pckName);
            } catch (Exception ex) {
                try {
                    String otherMarketUri = "http://market.android.com/details?id="
                            + pckName;
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(otherMarketUri));
                    context.startActivity(intent);
                } catch (Exception e) {

                }
            }
        }
    }

    public static void setFullScreen(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow()
                .getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(params);
        activity.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static void cancelFullScreen(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow()
                .getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(params);
        activity.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

//    public static PackageInfo getPackageInfo(String pckName) {
//        try {
//            return BaseApplication.context().getPackageManager()
//                    .getPackageInfo(pckName, 0);
//        } catch (NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static int getVersionCode() {
//        int versionCode = 0;
//        try {
//            versionCode = BaseApplication
//                    .context()
//                    .getPackageManager()
//                    .getPackageInfo(BaseApplication.context().getPackageName(),
//                            0).versionCode;
//        } catch (NameNotFoundException ex) {
//            versionCode = 0;
//        }
//        return versionCode;
//    }

    /**
     * 获取apk版本号
     */
    public static int getVersionCode(String packageName) {
        int versionCode = 0;
        try {
            versionCode = BaseApplication.getApplication().getPackageManager()
                    .getPackageInfo(packageName, 0).versionCode;
        } catch (NameNotFoundException ex) {
            versionCode = 0;
        }
        return versionCode;
    }

    /**
     * 当前系统SDK的版本号
     */
    public static int getAndroidSDKVersion() {
        int version;
        try {
            version = Integer.valueOf(Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            Log.d(TAG, e.toString());
            return -1;
        }
        return version;
    }
//
//    /**
//     * 获取apk版本名
//     */
//    public static String getVersionName() {
//        String name = "";
//        try {
//            name = BaseApplication
//                    .context()
//                    .getPackageManager()
//                    .getPackageInfo(BaseApplication.context().getPackageName(),
//                            0).versionName;
//        } catch (NameNotFoundException ex) {
//            name = "";
//        }
//        return name;
//    }

//    /**
//     * 检车屏幕是否开启
//     */
//    @SuppressLint("NewApi")
//    public static boolean isScreenOn() {
//        PowerManager pm = (PowerManager) BaseApplication.context()
//                .getSystemService(Context.POWER_SERVICE);
//        return pm.isInteractive();
//    }

    /**
     * 安装apk
     */
    public static void installAPK(Context context, File file) {
        if (file == null || !file.exists())
            return;
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static Intent getInstallApkIntent(File file) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        return intent;
    }

    public static void openDial(Context context, String number) {
        Uri uri = Uri.parse("tel:" + number);
        Intent it = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivity(it);
    }

    public static void openSMS(Context context, String smsBody, String tel) {
        Uri uri = Uri.parse("smsto:" + tel);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", smsBody);
        context.startActivity(it);
    }

    public static void openDail(Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void openSendMsg(Context context) {
        Uri uri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 调用照相机
     */
    public static void openCamera(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
        //    intent.setFlags(0x34c40000);
        context.startActivity(intent);
    }

//    public static String getIMEI() {
//        TelephonyManager tel = (TelephonyManager) BaseApplication.context()
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        return tel.getDeviceId();
//    }

    public static String getPhoneType() {
        return Build.MODEL;
    }

//    public static void openApp(Context context, String packageName) {
//        Intent mainIntent = BaseApplication.context().getPackageManager()
//                .getLaunchIntentForPackage(packageName);
//        if (mainIntent == null) {
//            mainIntent = new Intent(packageName);
//        } else {
////	    TLog.log("Action:" + mainIntent.getAction());
//        }
//        context.startActivity(mainIntent);
//    }
//
//    public static boolean openAppActivity(Context context, String packageName,
//                                          String activityName) {
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        ComponentName cn = new ComponentName(packageName, activityName);
//        intent.setComponent(cn);
//        try {
//            context.startActivity(intent);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public static boolean isWifiOpen() {
//        boolean isWifiConnect = false;
//        ConnectivityManager cm = (ConnectivityManager) BaseApplication
//                .context().getSystemService(Context.CONNECTIVITY_SERVICE);
//        // check the networkInfos numbers
//        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
//        for (int i = 0; i < networkInfos.length; i++) {
//            if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
//                if (networkInfos[i].getType() == ConnectivityManager.TYPE_MOBILE) {
//                    isWifiConnect = false;
//                }
//                if (networkInfos[i].getType() == ConnectivityManager.TYPE_WIFI) {
//                    isWifiConnect = true;
//                }
//            }
//        }
//        return isWifiConnect;
//    }
//
//    public static void uninstallApk(Context context, String packageName) {
//        if (isPackageExist(packageName)) {
//            Uri packageURI = Uri.parse("package:" + packageName);
//            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
//                    packageURI);
//            context.startActivity(uninstallIntent);
//        }
//    }
//
//    @SuppressWarnings("deprecation")
//    public static void copyTextToBoard(String string) {
//        if (TextUtils.isEmpty(string))
//            return;
//        ClipboardManager clip = (ClipboardManager) BaseApplication.context()
//                .getSystemService(Context.CLIPBOARD_SERVICE);
//        clip.setText(string);
////	AppContext.showToast(R.string.copy_success);
//    }
//
//    /**
//     * 发送邮件
//     *
//     * @param context
//     * @param subject 主题
//     * @param content 内容
//     * @param emails  邮件地址
//     */
//    public static void sendEmail(Context context, String subject,
//                                 String content, String... emails) {
//        try {
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            // 模拟器
//            // intent.setType("text/plain");
//            intent.setType("message/rfc822"); // 真机
//            intent.putExtra(Intent.EXTRA_EMAIL, emails);
//            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
//            intent.putExtra(Intent.EXTRA_TEXT, content);
//            context.startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static int getStatuBarHeight() {
//        Class<?> c = null;
//        Object obj = null;
//        Field field = null;
//        int x = 0, sbar = 38;// 默认为38，貌似大部分是这样的
//        try {
//            c = Class.forName("com.android.internal.R$dimen");
//            obj = c.newInstance();
//            field = c.getField("status_bar_height");
//            x = Integer.parseInt(field.get(obj).toString());
//            sbar = BaseApplication.context().getResources()
//                    .getDimensionPixelSize(x);
//
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//        return sbar;
//    }
//
//    public static int getActionBarHeight(Context context) {
//        int actionBarHeight = 0;
//        TypedValue tv = new TypedValue();
//        if (context.getTheme().resolveAttribute(R.attr.actionBarSize,
//                tv, true))
//            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
//                    context.getResources().getDisplayMetrics());
//
//        if (actionBarHeight == 0
//                && context.getTheme().resolveAttribute(R.attr.actionBarSize,
//                tv, true)) {
//            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
//                    context.getResources().getDisplayMetrics());
//        }
//
//        return actionBarHeight;
//    }
//
//    public static boolean hasStatusBar(Activity activity) {
//        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
//        if ((attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    /**
//     * 调用系统安装了的应用分享
//     *
//     * @param context
//     * @param title
//     * @param url
//     */
//    public static void showSystemShareOption(Activity context,
//                                             final String title, final String url) {
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
//        intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
//        context.startActivity(Intent.createChooser(intent, "选择分享"));
//    }
//

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
     */
    public static int getNetworkType() {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) BaseApplication.getApplication()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!TextUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }


}
