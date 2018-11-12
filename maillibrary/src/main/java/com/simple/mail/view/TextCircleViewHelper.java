package com.simple.mail.view;

import android.graphics.Color;

import com.simple.base.base.BaseApplication;
import com.simple.mail.maillibrary.R;

public class TextCircleViewHelper {

    /**
     * 给TextCircleView设置颜色
     */
    public static void setTextCircleViewColor(TextCircleView ctv, int textColor) {
        String[] colors = BaseApplication.getApplication().getResources().getStringArray(R.array.color);
        if (colors.length > 0)
            ctv.setTextColor(Color.parseColor(colors[textColor]));
    }
}
