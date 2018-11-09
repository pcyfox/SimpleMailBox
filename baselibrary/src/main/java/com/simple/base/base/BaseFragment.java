package com.simple.base.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simple.base.utils.HandleBackUtil;
import com.simple.base.utils.ViewTool;


/**
 * Fragment基类
 * 1. 初始化布局 initView
 * 2. 初始化数据 initData
 *
 * @author Ace
 */
public abstract class BaseFragment extends Fragment implements HandleBackUtil.HandleBackInterface {
    private static final String TAG = "BaseFragment";

    private ViewTool viewTool;
    protected boolean locked = false;
    protected Activity activity;

    // Fragment被创建
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();// 获取所在的activity对象
        viewTool = new ViewTool(this.getActivity().getWindow());
    }


    protected void hideView(int... resourdIds) {
        viewTool.hideView(resourdIds);
    }

    protected void hideView(View... view) {
        viewTool.hideView(view);
    }

    protected void showView(View... view) {
        viewTool.showView(view);
    }

    protected void showView(int... resourdIds) {
        viewTool.showView(resourdIds);
    }

    protected void setText(int textViewId, String text) {
        viewTool.setText(textViewId, text);
    }

    protected <T extends TextView> void setText(T view, String text) {
        viewTool.setText(view, text);
    }

    protected void setText(int textViewId, String text, String defaultString) {
        if (TextUtils.isEmpty(text)) {
            viewTool.setText(textViewId, defaultString);
        } else {
            viewTool.setText(textViewId, text);
        }
    }

    protected <T extends TextView> String getText(T view) {
        return viewTool.getText(view);
    }


    // 初始化Fragment布局
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = initView();
        return view;
    }

    // activity创建结束
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 初始化布局, 子类必须实现
     */
    public abstract View initView();

    /**
     * 初始化数据, 子类可以不实现
     */
    public void initData() {

    }

    @Override
    public boolean onBackPressed() {
        return HandleBackUtil.handleBackPress(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected boolean isVisible;

    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {


    }

    public void onVisible() {

    }

    protected void lazyLoad() {
    }

    ;//子类实现

    public void onInvisible() {
    }

}