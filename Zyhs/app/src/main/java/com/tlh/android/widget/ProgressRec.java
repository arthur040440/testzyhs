package com.tlh.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class ProgressRec extends View{

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mProgress = 0;

    public ProgressRec(Context context) {
        super(context);
        initParams();
    }

    public ProgressRec(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initParams();
    }

    public ProgressRec(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParams();
    }

    private void initParams(){
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#E5E5E5"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0,0,getWidth(),getHeight(),mPaint);
        if(mProgress < 267.5){
            mProgressPaint.setColor(Color.parseColor("#FFE123"));
            canvas.drawRect(0,0,mProgress,getHeight(),mProgressPaint);
        }else if(mProgress == 267.5){
            mProgressPaint.setColor(Color.parseColor("#FFC323"));
            canvas.drawRect(0,0,mProgress,getHeight(),mProgressPaint);
        }else {
            mProgressPaint.setColor(Color.parseColor("#FF9A23"));
            canvas.drawRect(0,0,mProgress,getHeight(),mProgressPaint);
        }
    }

    public void setmProgress(float progress){
        this.mProgress = progress;
        invalidate();
    }
}
