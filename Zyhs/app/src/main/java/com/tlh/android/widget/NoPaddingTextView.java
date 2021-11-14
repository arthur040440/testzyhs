package com.tlh.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author tlh
 * @time 2017/12/19 10:40
 * @desc 清除TextView字体周边空白(左右两侧默认没有留白)
*/

public class NoPaddingTextView extends android.support.v7.widget.AppCompatTextView {

    public NoPaddingTextView(Context context) {
        this(context, null);
    }

    public NoPaddingTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoPaddingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setIncludeFontPadding(false);
    }
}
