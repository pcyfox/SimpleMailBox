package com.simple.base.utils;

import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.TextView;

/**
 * @author 潘城尧
 * @category 简化findViewById及隐藏与显示View等方法。
 */

public class ViewTool {
    private Window window;

    public ViewTool(Window window) {
        this.window = window;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getViewById(int id) {
        return (T) window.findViewById(id);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getViewById(View rootView, int id) {
        return (T) rootView.findViewById(id);
    }

    public void hideView(int... resourdIds) {

        for (int id : resourdIds) {
            View view = window.findViewById(id);
            if (view != null) {
                view.setVisibility(View.GONE);
            }

        }

    }


    public void hideView(View... view) {
        for (View v : view) {
            if (v != null && v.getVisibility() == View.VISIBLE) {
                v.setVisibility(View.GONE);
            }
        }

    }

    public void showView(View... view) {
        for (View v : view) {
            if (v != null && v.getVisibility() != View.VISIBLE) {
                v.setVisibility(View.VISIBLE);
            }
        }

    }

    public void showView(int... resourceIds) {

        for (int id : resourceIds) {
            View view = window.findViewById(id);
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }

        }

    }

    public void setText(int textViewId, String text) {
        TextView textView;
        try {
            textView = getViewById(textViewId);
            setText(textView, text);
            showView(textView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public <T extends TextView> void setText(T view, String text) {
        if (view != null) {
            view.setText(text);
        }
    }


    public <T extends TextView> String getText(T view) {
        return view == null ? null : view.getText().toString().trim();
    }

    public void setViewNonClick(View... view) {
        for (View v : view) {
            if (v != null && v.isClickable()) {
                v.setClickable(false);
            }
        }

    }

    public void setViewNonClick(int... resourceIds) {

        for (int id : resourceIds) {
            View view = window.findViewById(id);
            if (view != null && view.isClickable()) {
                view.setClickable(false);
            }
        }

    }

    public void setViewCanClick(View... view) {
        for (View v : view) {
            if (v != null && !v.isClickable()) {
                v.setClickable(true);
            }
        }

    }


    public void setViewCanClick(int... resourceIds) {

        for (int id : resourceIds) {
            View view = window.findViewById(id);
            if (view != null && !view.isClickable()) {
                view.setClickable(true);

            }
        }

    }

    /**
     * 此方法用于处理软键盘遮挡布局控件的问题，当软键盘弹出时，将布局往上滚动
     *
     * @param parentView 需要滚动的外层布局
     * @param childView  当软键盘弹出时，parentView中最靠近（垂直方向）软键盘的一个子控件
     */
    public static void addSoftInputToggleListenerForScrollView(final View parentView, final View childView) {
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public boolean isHasScroll = false;//布局是否已经滚动过

            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                parentView.getWindowVisibleDisplayFrame(rect);
                if (rect.bottom < parentView.getRootView().getHeight() * 2 / 3) {//键盘已弹出
                    int[] location = new int[2];
                    childView.getLocationInWindow(location);
                    int scrollHeight = (location[1] + childView.getHeight()) - rect.bottom;
                    if (scrollHeight > 0) {//控件被遮挡
                        parentView.scrollTo(0, scrollHeight + ScreenUtils.dip2px(parentView.getContext(), 10));//往上滚动，与键盘间距10dp
                        isHasScroll = true;
                    }
                } else {//键盘已隐藏
                    if (isHasScroll) {//键盘弹出时布局有滚动过
                        parentView.scrollTo(0, 0);//滚回原位
                        isHasScroll = false;
                    }
                }
            }
        });
    }

    /**
     * 根据TextSize获取字体高度
     *
     * @param textSize 单位:px
     * @return
     */
    public static float getFontHeight(float textSize) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

}
