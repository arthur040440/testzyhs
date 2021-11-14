package com.tlh.android.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * @author tlh
 * @time 2018/1/5 17:16
 * @desc 跑马灯效果
 */

public class MarqueeTextView extends AppCompatTextView {


    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isFocused(){
        return true;
    }

}
