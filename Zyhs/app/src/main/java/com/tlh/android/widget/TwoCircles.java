package com.tlh.android.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.zhangyuhuishou.zyhs.R;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liumenglong
 * @time 2019/4/18 16:01
 * @desc 交替变换的圆
 */

public class TwoCircles extends View {

    private Paint outPaint;// 外圆画笔
    private Paint inPaint;// 内圆画笔
    private float outCircleRadius;
    private float inCircleRadius;
    private int width;// 宽度值
    private int height;// 高度值

    //动画是否开始
    private boolean isStart = false;
    private boolean isDrawCircle = false;
    //存储所有动画的ValueAnimator方便管理
    private ConcurrentHashMap<String, ValueAnimator> animMap = new ConcurrentHashMap<>();
    private static final String ANIM_CONTROL_INVALIDATE = "anim_control_invalidate";
    public static final String ANIM_OUT_PROGRESS_MOVE = "ANIM_OUT_PROGRESS_MOVE";
    public static final String ANIM_IN_PROGRESS_MOVE = "ANIM_IN_PROGRESS_MOVE";


    public TwoCircles(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TwoCircles(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TwoCircles(Context context) {
        super(context);
        init();
    }

    private void init() {
        outPaint = new Paint();
        outPaint.setColor(getResources().getColor(R.color.face_color_round,null));// 设置画笔颜色
        outPaint.setStyle(Paint.Style.FILL);
        outPaint.setStrokeWidth(10);
        outPaint.setAntiAlias(true);

        inPaint = new Paint();
        inPaint.setColor(getResources().getColor(R.color.theme_color,null));// 设置画笔颜色
        inPaint.setStyle(Paint.Style.FILL);
        inPaint.setStrokeWidth(10);
        inPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth() - 1;// 宽度值
        height = getMeasuredHeight() - 1;// 高度值

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isDrawCircle)
            drawCricles(canvas);
    }

    // 画圆（开始重合，慢慢一个变大，一个变小循环重复此过程）
    private void drawCricles(Canvas canvas) {
        canvas.drawCircle(width / 2, width / 2, outCircleRadius, outPaint);
        canvas.drawCircle(width / 2,width / 2,inCircleRadius,inPaint);
    }

    // 启动动画
    public void startAnim() {
        resetAnim();
        startInvalidateAnim();
    }

    // 重置动画
    private void resetAnim() {
        stopAnimAndRemoveCallbacks();
        isDrawCircle = false;
    }

    private void startInvalidateAnim() {
        isStart = true;
        ValueAnimator valueAnimator = animMap.get(ANIM_CONTROL_INVALIDATE);
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator.setInterpolator(new LinearInterpolator());
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
                    startCircles();
                }

            });
            animMap.put(ANIM_CONTROL_INVALIDATE, valueAnimator);
        }
        startValueAnimator(valueAnimator);
    }

    // 开始画外圆
    private void startCircles() {
        isDrawCircle = true;
        ValueAnimator outValueAnimator = animMap.get(ANIM_OUT_PROGRESS_MOVE);
        if (outValueAnimator == null) {
            outValueAnimator = ValueAnimator.ofFloat().setDuration(1500);
            outValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            outValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    outCircleRadius = (float) animation.getAnimatedValue();
                }
            });

            outValueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    ValueAnimator valueAnimator = animMap.get(ANIM_OUT_PROGRESS_MOVE);
                    if (valueAnimator != null) {
                        System.out.println("外圆取消动画");
                        valueAnimator.cancel();
                    }
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    System.out.println("外圆开始动画");
                }
            });
            animMap.put(ANIM_OUT_PROGRESS_MOVE, outValueAnimator);
        }
        outValueAnimator.setFloatValues(50, 100, 50);
        startValueAnimator(outValueAnimator);

        ValueAnimator inValueAnimator = animMap.get(ANIM_IN_PROGRESS_MOVE);
        if (inValueAnimator == null) {
            inValueAnimator = ValueAnimator.ofFloat().setDuration(1500);
            inValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            inValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    inCircleRadius = (float) animation.getAnimatedValue();
                }
            });

            inValueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    ValueAnimator valueAnimator = animMap.get(ANIM_IN_PROGRESS_MOVE);
                    if (valueAnimator != null) {
                        System.out.println("内圆取消动画");
                        valueAnimator.cancel();
                    }
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    System.out.println("内圆开始动画");
                }
            });
            animMap.put(ANIM_IN_PROGRESS_MOVE, inValueAnimator);
        }
        inValueAnimator.setFloatValues(50, 0, 50);
        startValueAnimator(inValueAnimator);
    }

    // 开始动画效果
    private void startValueAnimator(ValueAnimator valueAnimator) {
        if (isStart) {
            valueAnimator.start();
        }
    }

    // 停止动画效果
    public void stopAnimAndRemoveCallbacks() {
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
