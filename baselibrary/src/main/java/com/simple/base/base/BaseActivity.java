package com.simple.base.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


import com.simple.base.utils.HandleBackUtil;
import com.simple.base.utils.ViewTool;

import static android.view.Window.ID_ANDROID_CONTENT;


public abstract class BaseActivity extends FragmentActivity {
    InputMethodManager imm;
    private ViewTool viewTool;
    protected boolean locked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //当Activity从异常销毁中恢复后savedInstanceState一定不为null，随后会执行恢复流程，然而当savedInstanceState设置为空后便会走正常启动流程
        if (null != savedInstanceState) {
            savedInstanceState=null;
        }
        super.onCreate(savedInstanceState);
        viewTool = new ViewTool(this.getWindow());
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    /**
     * 通过Class跳转界面
     **/
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 通过Action跳转界面
     **/
    protected void startActivity(String action) {
        startActivity(action, null);
    }

    /**
     * 含有Bundle通过Action跳转界面
     **/
    protected void startActivity(String action, Bundle bundle) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * Activity带返回值的跳转
     */
    public void startActivity(Bundle bundle, Class<?> cls, int requestCode) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setClass(this, cls);
        this.startActivityForResult(intent, requestCode);
    }

    /**
     * Activity带返回值的跳转
     */
    public void startActivity(Class<?> cls, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        this.startActivityForResult(intent, requestCode);
    }


    protected void hideView(int... resourcedIds) {
        viewTool.hideView(resourcedIds);
    }

    protected void hideView(View... view) {
        viewTool.hideView(view);
    }

    protected void showView(View... view) {
        viewTool.showView(view);
    }

    protected void showView(int... resourcedIds) {
        viewTool.showView(resourcedIds);
    }

    protected void setText(int textViewId, String text) {
        viewTool.setText(textViewId, text);
    }

    protected void setText(int textViewId, String text, String defaultString) {
        if (TextUtils.isEmpty(text)) {
            viewTool.setText(textViewId, defaultString);
        } else {
            viewTool.setText(textViewId, text);
        }
    }

    protected <T extends TextView> void setText(T view, String text) {
        viewTool.setText(view, text);
    }

    protected <T extends TextView> String getText(T view) {
        return viewTool.getText(view);
    }

    protected void setViewNonClick(int... resourceIds) {
        viewTool.setViewNonClick(resourceIds);
    }

    protected void setViewNonClick(View... view) {
        viewTool.setViewNonClick(view);
    }

    protected void setViewCanClick(View... view) {
        viewTool.setViewCanClick(view);
    }

    protected void setViewCanClick(int... resourceIds) {
        viewTool.setViewCanClick(resourceIds);
    }

    public void setClickState(boolean clickable, View view) {
        if (clickable) {
            view.setAlpha(1.0f);
            view.setClickable(true);
        } else {
            view.setAlpha(0.6f);
            view.setClickable(false);
        }
    }
    // -----------------------------------------------------------


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 解决透明Activity焦点游标跑到上一个Activity控件中的问题
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (this.getCurrentFocus() != null) {
                if (this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    View relative = (View) getCurrentFocus().getParent();
                    relative.setFocusable(true);
                    relative.setFocusableInTouchMode(true);
                    relative.requestFocus();
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public Resources getResources() {
        if (isNeedSystemResConfig()) {
            return super.getResources();
        } else {//解决字体大小变化影响
            Resources res = super.getResources();
            Configuration config = new Configuration();
            config.setToDefaults();
            res.updateConfiguration(config, res.getDisplayMetrics());
            return res;
        }
    }

    /**
     * 默认返回true，使用系统资源，如果个别界面不需要，在这些activity中Override this method ，then return false;
     * @return
     */
    protected boolean isNeedSystemResConfig() {
        return true;
    }


    @Override
    public void onBackPressed() {
        if (!HandleBackUtil.handleBackPress(this)) {
            super.onBackPressed();
        }
    }

    protected void setFullScreen() {
        // 设置全屏
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);
    }

    public View getContentView(){
        return findViewById(ID_ANDROID_CONTENT);
    }

}
