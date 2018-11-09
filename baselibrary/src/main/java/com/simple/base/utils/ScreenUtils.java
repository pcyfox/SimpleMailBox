package com.simple.base.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 1、获取屏幕宽高 2、dp、px、sp之间转换 Created by yxl on 2016/5/3.
 */
public class ScreenUtils {
    private static final String TAG = "ScreenUtils";

    /**
     * 获得屏幕宽度
     *
     * @param context context
     * @return 屏幕宽
     */
    public static int getScreenWidth(Context context) {
        if (null == context)
            return 0;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     *
     * @param context context
     * @return 屏幕高
     */
    public static int getScreenHeight(Context context) {
        if (null == context)
            return 0;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 有些界面在版本大于19的时候，需要将状态栏设为沉浸式
     */
    public static void setTranslucentStatus(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window window = activity.getWindow();
//            //取消设置悬浮透明状态栏,ContentView便不会进入状态栏的下方了
//            //  	window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//
//        }


        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        );

    }

    public static void setStatusClolor(@NonNull Activity activity, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            //取消设置悬浮透明状态栏,ContentView便不会进入状态栏的下方了
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View decorView = activity.getWindow().getDecorView();
            //设置全屏和状态栏透明
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            activity.getWindow().setStatusBarColor(color);
        }
    }

    /**
     * 有些界面在版本大于19的时候，将状态栏设为沉浸式,将界面的父布局设置10dp的padding
     */
    public static void setViewPadding(Context context, View view, int paddingTop) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setPadding(0, ScreenUtils.dip2px(context, paddingTop), 0, 0);
        }
    }

    /**
     * 有些界面在版本大于19的时候，将状态栏设为沉浸式,将界面的父布局设置10dp的padding
     */
    public static void setViewVisible(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 写邮件界面，收件人，抄送人，密送人栏目，当该处失去焦点，收起flow的时候，显示出的textview的高度
     */
    public static int getFlowWidth(Context context, TextView view) {
        TextView textView = (TextView) ((LinearLayout) view.getParent().getParent().getParent()).getChildAt(0);
        textView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        //60 = 父控件两边的margin ，textView右边的margin 和 flowLayout右边的margin
        return getScreenWidth(context) - dip2px(context, 80) - textView.getMeasuredWidth();
    }


}
