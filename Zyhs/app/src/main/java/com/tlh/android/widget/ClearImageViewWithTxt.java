package com.tlh.android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

// 带有文字的ImageView
public class ClearImageViewWithTxt extends android.support.v7.widget.AppCompatImageView{

    private int cabinetStatus = 0;// 状态 1正常(空) 2正常（有）3满 4暂不可用

    public ClearImageViewWithTxt(Context context) {
        super(context);
        cabinetStatus = 1;
    }

    public ClearImageViewWithTxt(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        cabinetStatus = 1;
    }

    public ClearImageViewWithTxt(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        cabinetStatus = 1;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(cabinetStatus == 1){
            setImageTintMode(PorterDuff.Mode.MULTIPLY);
            setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFFFF")));
        }else if(cabinetStatus == 2){
            setImageTintMode(PorterDuff.Mode.MULTIPLY);
            setImageTintList(ColorStateList.valueOf(Color.parseColor("#68A1A6")));
        }else if(cabinetStatus == 3){
            setImageTintMode(PorterDuff.Mode.MULTIPLY);
            setImageTintList(ColorStateList.valueOf(Color.parseColor("#E66D48")));
        }else if(cabinetStatus == 4){
            setImageTintMode(PorterDuff.Mode.MULTIPLY);
            setImageTintList(ColorStateList.valueOf(Color.parseColor("#999999")));
        }
    }

    public void setCabinetStatus(int status){
        cabinetStatus = status;
        invalidate();
    }

}
