package com.tlh.android.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.zhangyuhuishou.zyhs.R;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tlh
 * @time 2018/10/19 17:15
 * @desc 横向进度条
 */

public class HorizontalProgressBar extends View {

    private Paint paint;// 画笔
    private float progress;// 进度值
    private int width;// 宽度值
    private int height;// 高度值
    private long mDuration;
    //动画是否开始
    private boolean isStart = false;
    private boolean isDrawRect = false;
    //存储所有动画的ValueAnimator方便管理
    private ConcurrentHashMap<String, ValueAnimator> animMap = new ConcurrentHashMap<>();

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HorizontalProgressBar(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.theme_color,null));// 设置画笔颜色
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
        if(isDrawRect)
            drawRect(canvas);
    }

    // 画矩形进度条
    private void drawRect(Canvas canvas){
        canvas.drawRect(0, 0, progress, height, paint);// 画矩形
        canvas.drawLine(0, 0, width, 0, paint);// 画顶边
        canvas.drawLine(0, 0, 0, height, paint);// 画左边
        canvas.drawLine(width, 0, width, height, paint);// 画右边
        canvas.drawLine(0, height, width, height, paint);// 画底边
    }

    // 启动动画
    public void startAnim(long duration) {
        mDuration = duration;
        resetAnim();
        startInvalidateAnim();
    }

    // 重置动画
    private void resetAnim() {
        stopAnimAndRemoveCallbacks();
        isDrawRect = false;
    }

    // 停止动画
    public void stopAnim(){
        stopAnimAndRemoveCallbacks();
        isDrawRect = false;
    }

    private static final String ANIM_CONTROL_INVALIDATE = "anim_control_invalidate";

    private void startInvalidateAnim() {
        isStart = true;
        ValueAnimator valueAnimator = animMap.get(ANIM_CONTROL_INVALIDATE);
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(2500);
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
                    startRect();
                }

            });
            animMap.put(ANIM_CONTROL_INVALIDATE, valueAnimator);
        }
        startValueAnimator(valueAnimator);
    }

    public static final String ANIM_RECT_PROGRESS_MOVE = "ANIM_RECT_PROGRESS_MOVE";

    // 开始画矩形
    private void startRect() {
        isDrawRect = true;
        ValueAnimator progressValueAnimator = animMap.get(ANIM_RECT_PROGRESS_MOVE);
        if (progressValueAnimator == null) {
            progressValueAnimator = ValueAnimator.ofFloat().setDuration(mDuration);
            progressValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    progress = (float) animation.getAnimatedValue();
                }
            });

            progressValueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isDrawRect = false;
                    ValueAnimator valueAnimator = animMap.get(ANIM_CONTROL_INVALIDATE);
                    if(valueAnimator != null){
                        valueAnimator.cancel();
                    }
                }
            });
            animMap.put(ANIM_RECT_PROGRESS_MOVE, progressValueAnimator);
        }
        progressValueAnimator.setFloatValues(0, 798);
        startValueAnimator(progressValueAnimator);
    }

    // 开始动画效果
    private void startValueAnimator(ValueAnimator valueAnimator) {
        if (isStart) {
            valueAnimator.start();
        }
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
