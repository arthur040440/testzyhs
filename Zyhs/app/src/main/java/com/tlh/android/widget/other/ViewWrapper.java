package com.tlh.android.widget.other;

import android.view.View;

/**
 * @作者 tlh
 * @创建日期 2018/5/18  23:09
 * @描述 包装类属性动画使用
 */

public class ViewWrapper {

    private View mTarget;

    public ViewWrapper(View target){
        this.mTarget = target;
    }

    public int getWidth(){
        return mTarget.getLayoutParams().width;
    }

    public void setWidth(int width){
        mTarget.getLayoutParams().width = width;
        mTarget.requestLayout();
    }
}
