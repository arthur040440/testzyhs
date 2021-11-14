package com.tlh.android.widget.other;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

// 带有文字的ImageView
public class BannerImageViewWithTxt extends android.support.v7.widget.AppCompatImageView{

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Rect rect;
    private String bannerTxt = "";
    private int time;

    public BannerImageViewWithTxt(Context context) {
        super(context);
        init();
    }

    public BannerImageViewWithTxt(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BannerImageViewWithTxt(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint.setColor(Color.parseColor("#222222"));
        mPaint.setTextSize(70);
        rect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!TextUtils.isEmpty(bannerTxt)){
            float textWidth = mPaint.measureText(bannerTxt);// 左右存在间距
            mPaint.getTextBounds(bannerTxt, 0, bannerTxt.length(), rect);
            int height = rect.height();//文本的高度
            canvas.drawText(bannerTxt,(getWidth() - textWidth) / 2,getHeight() - height/ 2,mPaint);
        }
    }

    public void setBannerTxt(String text){
        this.bannerTxt = text;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
