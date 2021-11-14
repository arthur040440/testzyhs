package com.face.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tlh
 * @time 2018/10/11 13:38
 * @desc 两个弧度（内弧完整外弧分段-最外围四个角）
 */
public class RotateCircles extends View {

    private Paint mPaint;
    private RectF mRectOutSideArc;
    private boolean isDrawArcLine = false;
    private int ringColor = Color.parseColor("#ffcf45");
    private int mFirstX,mFirstY,mX;
    private int consValue = 80;
    //动画是否开始
    private boolean isStart = false;
    //存储所有动画的ValueAnimator方便管理
    private ConcurrentHashMap<String, ValueAnimator> animMap = new ConcurrentHashMap<>();
    private float outSideArcStartAngle;

    public RotateCircles(Context context) {
        super(context);
        init();
    }

    public RotateCircles(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RotateCircles(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // 初始化动作
    private void init() {
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mRectOutSideArc = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mFirstX = getMeasuredWidth() / 2;
        mX =  getMeasuredWidth() / 4;
        mFirstY = getMeasuredHeight() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isDrawArcLine)
            drawArcLine(canvas);
    }

    // 绘制圆弧
    private void drawArcLine(Canvas canvas) {
        mPaint.setColor(ringColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
        mPaint.setStrokeWidth(getMeasuredWidth() / 50);
        // 画短边
        // 上左边
        canvas.drawLine(mFirstX - mX, mFirstY - mX, mFirstX - mX + consValue, mFirstY - mX, mPaint);
        canvas.drawLine(mFirstX - mX, mFirstY - mX, mFirstX - mX, mFirstY - mX + consValue, mPaint);
        // 上右边
        canvas.drawLine(mFirstX + mX - consValue, mFirstY - mX, mFirstX + mX, mFirstY - mX, mPaint);
        canvas.drawLine(mFirstX + mX, mFirstY - mX, mFirstX + mX, mFirstY - mX + consValue, mPaint);
        // 下左边
        canvas.drawLine(mFirstX - mX, mFirstY + mX - consValue, mFirstX - mX,mFirstY + mX, mPaint);
        canvas.drawLine(mFirstX - mX,mFirstY + mX, mFirstX - mX + consValue, mFirstY + mX, mPaint);
        // 下右边
        canvas.drawLine(mFirstX + mX, mFirstY + mX - consValue,mFirstX + mX, mFirstY + mX, mPaint);
        canvas.drawLine(mFirstX + mX - consValue, mFirstY + mX, mFirstX + mX, mFirstY + mX, mPaint);
        mPaint.setStrokeWidth(getMeasuredWidth() / 80);
        canvas.drawArc(mRectOutSideArc, 0 + outSideArcStartAngle, 60, false, mPaint);
        canvas.drawArc(mRectOutSideArc, 90 + outSideArcStartAngle, 60, false, mPaint);
        canvas.drawArc(mRectOutSideArc, 90 * 2 + outSideArcStartAngle, 60, false, mPaint);
        canvas.drawArc(mRectOutSideArc, 90 * 3 + outSideArcStartAngle, 60, false, mPaint);
        mPaint.setStrokeWidth(getMeasuredWidth() / 65);
        canvas.drawCircle(mFirstX, mFirstY, mFirstX - mX - 30, mPaint);
    }

    // 启动动画
    public void startAnim() {
        resetAnim();
        startInvalidateAnim();
    }

    private static final String ANIM_CONTROL_INVALIDATE = "anim_control_invalidate";

    private void startInvalidateAnim() {
        isStart = true;
        ValueAnimator valueAnimator = animMap.get(ANIM_CONTROL_INVALIDATE);
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.setDuration(2500);
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    invalidate();
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    startArcLine();
                }

            });
            animMap.put(ANIM_CONTROL_INVALIDATE, valueAnimator);
        }
        startValueAnimator(valueAnimator);
    }

    public static final String ANIM_ARC_LINE_CENTER_MOVE = "anim_arc_line_center_move";
    public static final String ANIM_ARC_LINE_OUTSIZE_MOVE = "anim_arc_line_outsize_move";

    // 开始画弧
    private void startArcLine() {
        isDrawArcLine = true;
        //外部圆弧移动控制
        ValueAnimator outSizeArcLineMoveAnim = animMap.get(ANIM_ARC_LINE_OUTSIZE_MOVE);
        if (outSizeArcLineMoveAnim == null) {
            outSizeArcLineMoveAnim = ValueAnimator.ofFloat().setDuration(8000);
            outSizeArcLineMoveAnim.setRepeatCount(ValueAnimator.INFINITE);
            outSizeArcLineMoveAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    outSideArcStartAngle = (float) animation.getAnimatedValue();
                }
            });
            animMap.put(ANIM_ARC_LINE_OUTSIZE_MOVE, outSizeArcLineMoveAnim);
        }
        outSizeArcLineMoveAnim.setFloatValues(0, 360);
        startValueAnimator(outSizeArcLineMoveAnim);
    }

    // 开始动画效果
    private void startValueAnimator(ValueAnimator valueAnimator) {
        if (isStart) {
            valueAnimator.start();
        }
    }

    // 重置动画
    private void resetAnim() {
        stopAnimAndRemoveCallbacks();
        //是否画相应的内容
        isDrawArcLine = false;
        //圆弧部分
        outSideArcStartAngle = 0;
        mRectOutSideArc.set(mFirstX - mX, mFirstY - mX, mFirstX + mX, mFirstY + mX);
    }

    // 停止动画效果
    private void stopAnimAndRemoveCallbacks() {
        isStart = false;
        for (Map.Entry<String, ValueAnimator> entry : animMap.entrySet()) {
            entry.getValue().end();
        }
        Handler handler = this.getHandler();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

}
