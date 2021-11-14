package com.tlh.android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

// 带有文字的ImageView
public class ImageViewWithTxt extends android.support.v7.widget.AppCompatImageView {

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Rect rect;
    private int doorStatus = 0;// 状态1 暂不可用 2 暂未装配 3 正常 4 已满  5 设备故障
    private String mText;

    public ImageViewWithTxt(Context context) {
        super(context);
        init();
        doorStatus = 1;
    }

    public ImageViewWithTxt(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        doorStatus = 1;
    }

    public ImageViewWithTxt(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        doorStatus = 1;
    }

    private void init() {
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(38);
        rect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (doorStatus == 1) {
            setImageTintMode(PorterDuff.Mode.MULTIPLY);
            setImageTintList(ColorStateList.valueOf(Color.parseColor("#999999")));
            float textWidth = mPaint.measureText("暂不可用");// 左右存在间距
            mPaint.getTextBounds("暂不可用", 0, "暂不可用".length(), rect);
            int height = rect.height();//文本的高度
            canvas.drawText("暂不可用", (getWidth() - textWidth) / 2, getHeight() / 2 + height / 2, mPaint);
        } else if (doorStatus == 2) {
            setImageTintMode(PorterDuff.Mode.MULTIPLY);
            setImageTintList(ColorStateList.valueOf(Color.parseColor("#999999")));
            float textWidth = mPaint.measureText("暂未装配");// 左右存在间距
            mPaint.getTextBounds("暂未装配", 0, "暂未装配".length(), rect);
            int height = rect.height();//文本的高度
            canvas.drawText("暂未装配", (getWidth() - textWidth) / 2, getHeight() / 2 + height / 2, mPaint);
        } else if (doorStatus == 4) {
            setImageTintMode(PorterDuff.Mode.MULTIPLY);
            setImageTintList(ColorStateList.valueOf(Color.parseColor("#999999")));
            float textWidth = mPaint.measureText("暂满");// 左右存在间距
            mPaint.getTextBounds("暂满", 0, "暂满".length(), rect);
            int height = rect.height();//文本的高度
            canvas.drawText("暂满", (getWidth() - textWidth) / 2, getHeight() / 2 + height / 2, mPaint);
        } else if (doorStatus == 5) {
            setImageTintMode(PorterDuff.Mode.MULTIPLY);
            setImageTintList(ColorStateList.valueOf(Color.parseColor("#999999")));
            float textWidth = mPaint.measureText("设备故障");// 左右存在间距
            mPaint.getTextBounds("设备故障", 0, "设备故障".length(), rect);
            int height = rect.height();//文本的高度
            canvas.drawText("设备故障", (getWidth() - textWidth) / 2, getHeight() / 2 + height / 2, mPaint);
        }
    }

    public void setDoorStatus(int status) {
        doorStatus = status;
        invalidate();
    }

    public void setText(String text) {
        this.mText = text;
    }
}
