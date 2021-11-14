/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.face.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.tlh.android.utils.DensityUtils;
import com.zhangyuhuishou.zyhs.R;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 人脸检测区域View
 */
public class FaceDetectRoundView extends View {

    private static final String TAG = FaceDetectRoundView.class.getSimpleName();

    public static final float SURFACE_HEIGHT = 1000f;
    public static final float SURFACE_RATIO = 0.75f;
    public static final float WIDTH_SPACE_RATIO = 0.33f;
    public static final float HEIGHT_RATIO = 0.1f;
    public static final float HEIGHT_EXT_RATIO = 0.2f;
    public static final int CIRCLE_SPACE = 5;
    public static final int PATH_SPACE = 16;
    public static final int PATH_SMALL_SPACE = 12;
    public static final int PATH_WIDTH = 4;

    private RectF mRectOutSideArc;

    private PathEffect mFaceRoundPathEffect = null;
    // new DashPathEffect(new float[]{PATH_SPACE, PATH_SPACE}, 1);
    private Paint mBGPaint;
    private Paint mPathPaint;
    private Paint mFaceRectPaint;
    private Paint mFaceRoundPaint;
    private Paint mPaint;
    private Rect mFaceRect;
    private Rect mFaceDetectRect;
    private float mBaseSweepAngle = 180;

    private float mX;
    private float mY;
    private float mR;
    private boolean mIsDrawDash = true;

    private Bitmap mScreenBg, mTopLogo;
    private Rect mScreenBgSrcRect, mScreenBgDesRect, mTopLogDesRect;
    private int mScreenBgBitWidth, mScreenBgBitHeight;
    private int mLogoBgBitWidth, mLogoBgBitHeight;

    private float outSideArcStartAngle;
    private boolean isDrawArcLine = false;
    //存储所有动画的ValueAnimator方便管理
    private ConcurrentHashMap<String, ValueAnimator> animMap = new ConcurrentHashMap<>();
    //动画是否开始
    private boolean isStart = false;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public FaceDetectRoundView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float pathSpace = DensityUtils.dip2px(context, PATH_SPACE);
        float pathSmallSpace = DensityUtils.dip2px(context, PATH_SMALL_SPACE);
        float pathWidth = DensityUtils.dip2px(context, PATH_WIDTH);
        mFaceRoundPathEffect = new DashPathEffect(
                new float[]{pathSpace, dm.heightPixels < SURFACE_HEIGHT
                        ? pathSmallSpace : pathSpace}, 1);

        mBGPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBGPaint.setColor(context.getResources().getColor(R.color.face_color_bg,null));
        mBGPaint.setStyle(Paint.Style.FILL);
        mBGPaint.setAntiAlias(true);
        mBGPaint.setDither(true);

        mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathPaint.setColor(context.getResources().getColor(R.color.face_color_round,null));
        mPathPaint.setStrokeWidth(pathWidth);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setAntiAlias(true);
        mPathPaint.setDither(true);

        mFaceRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFaceRectPaint.setColor(context.getResources().getColor(R.color.white,null));
        mFaceRectPaint.setStrokeWidth(pathWidth);
        mFaceRectPaint.setStyle(Paint.Style.STROKE);
        mFaceRectPaint.setAntiAlias(true);
        mFaceRectPaint.setDither(true);

        mFaceRoundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathPaint.setColor(context.getResources().getColor(R.color.face_color_round,null));
        mFaceRoundPaint.setStyle(Paint.Style.FILL);
        mFaceRoundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mFaceRoundPaint.setAntiAlias(true);
        mFaceRoundPaint.setDither(true);

        mScreenBg = ((BitmapDrawable) getContext().getResources().getDrawable(R.mipmap.ic_screen_bg)).getBitmap();
        mTopLogo = ((BitmapDrawable) getContext().getResources().getDrawable(R.mipmap.ic_logo_top)).getBitmap();
        mScreenBgBitWidth = mScreenBg.getWidth();
        mScreenBgBitHeight = mScreenBg.getHeight();
        mLogoBgBitWidth = mTopLogo.getWidth();
        mLogoBgBitHeight = mTopLogo.getHeight();
//        mLogoBgBitWidth = 315;
//        mLogoBgBitHeight = 55;


        mRectOutSideArc = new RectF();
        mRectOutSideArc.set(220, 306, 860, 940);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(context.getResources().getColor(R.color.face_color_ring,null));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mScreenBgSrcRect = new Rect(0, 0, mScreenBgBitWidth, mScreenBgBitHeight);
        mScreenBgDesRect = new Rect(0, 0, mScreenBgBitWidth, mScreenBgBitHeight);
//        mTopLogDesRect = new Rect(383, 86, 698, 178);
        mTopLogDesRect = new Rect((mScreenBgBitWidth - mLogoBgBitWidth) / 2, 86, mLogoBgBitWidth + (mScreenBgBitWidth - mLogoBgBitWidth) / 2, mLogoBgBitHeight + 86);
    }

    public void processDrawState(boolean isDrawDash) {
        mIsDrawDash = isDrawDash;
        postInvalidate();
    }

    public float getRound() {
        return mR;
    }

    public Rect getFaceRoundRect() {
        if (mFaceRect != null) {
            Log.e(TAG, mFaceRect.toString());
        }
        return mFaceRect;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        float canvasWidth = right - left;
        float canvasHeight = bottom - top;

        float x = canvasWidth / 2;
        float y = (canvasHeight / 2) - ((canvasHeight / 2) * HEIGHT_RATIO);
        float r = (canvasWidth / 2) - ((canvasWidth / 2) * WIDTH_SPACE_RATIO);

        if (mFaceRect == null) {
            mFaceRect = new Rect((int) (x - r), (int) (y - r), (int) (x + r), (int) (y + r));
//            mFaceRect = new Rect(240, 326,840, 926);
        }
        if (mFaceDetectRect == null) {
            float hr = r + (r * HEIGHT_EXT_RATIO);
            mFaceDetectRect = new Rect((int) (x - r), (int) (y - hr), (int) (x + r), (int) (y + hr));
        }
        mX = x;
        mY = y;
        mR = r;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawBitmap(mScreenBg, null, mScreenBgDesRect, null);
        canvas.drawBitmap(mTopLogo, null, mTopLogDesRect, null);

        if (mIsDrawDash) {
            mPathPaint.setPathEffect(mFaceRoundPathEffect);
        } else {
            mPathPaint.setPathEffect(null);
        }
        canvas.drawCircle(mX, 626, 300, mFaceRoundPaint);

        if (isDrawArcLine)
            drawArcLine(canvas);

        if (isDrawPeopleRect) {
            float left = getMeasuredWidth() - rect.left  * 27 / 16;
            float right = getMeasuredWidth() - rect.right  * 27 / 16;
            // 绘制长方形开始(二选一)
//            float top = (rect.top * 608 / 480) + 326;
//            float bottom = rect.bottom * 608 / 480 + 326;
            // 绘制长方形结束

            // 绘制正方形开始(二选一)
            float top = (rect.top * 608 / 480) + 280;
            float bottom = rect.bottom * 608 / 480 + 280;
            float x = Math.abs(right - left);
            float y = Math.abs(bottom - top);
            if(x > y){
                bottom = bottom + x - y;
            }else {
                right = right + y - x;
            }
            // 绘制正方形结束
            mPaint.setStrokeWidth(2);
            mPaint.setColor(Color.parseColor("#50FFFFFF"));
//            // 画长边
//            // 左上角
//            canvas.drawLine(left - 70,top,left,top,mPaint);
//            canvas.drawLine(left,top,left,top + 70,mPaint);
//            // 右上角
//            canvas.drawLine(right,top,right + 70,top,mPaint);
//            canvas.drawLine(right,top,right,top + 70,mPaint);
//            // 左下角
//            canvas.drawLine(left - 70,bottom,left ,bottom ,mPaint);
//            canvas.drawLine(left,bottom - 70,left,bottom,mPaint);
//            // 右下角
//            canvas.drawLine(right,bottom,right + 70,bottom,mPaint);
//            canvas.drawLine(right,bottom - 70,right,bottom,mPaint);

//            canvas.drawRect(left, top, right , bottom, mPaint);
            mPaint.setStrokeWidth(5);
            mPaint.setColor(Color.parseColor("#FFFFFF"));
            // 画短边
            // 左上角
            canvas.drawLine(left - 30,top,left,top,mPaint);
            canvas.drawLine(left,top,left,top + 30,mPaint);
            // 右上角
            canvas.drawLine(right,top,right + 30,top,mPaint);
            canvas.drawLine(right,top,right,top + 30,mPaint);
            // 左下角
            canvas.drawLine(left - 30,bottom,left ,bottom ,mPaint);
            canvas.drawLine(left,bottom - 30,left,bottom,mPaint);
            // 右下角
            canvas.drawLine(right,bottom,right + 30,bottom,mPaint);
            canvas.drawLine(right,bottom - 30,right,bottom,mPaint);
        }
    }

    // 绘制圆弧
    private void drawArcLine(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
        mPaint.setStrokeWidth(getMeasuredWidth() / 80);
        mPaint.setColor(getResources().getColor(R.color.face_color_ring,null));
        canvas.drawArc(mRectOutSideArc, 120, mBaseSweepAngle + outSideArcStartAngle, false, mPaint);
        mPaint.setColor(Color.parseColor("#FFFFFF"));
        canvas.drawArc(mRectOutSideArc, 120, 180, false, mPaint);
    }

    public static Rect getPreviewDetectRect(int w, int pw, int ph) {
        float round = (w / 2) - ((w / 2) * WIDTH_SPACE_RATIO);
        float x = pw / 2;
        float y = (ph / 2) - ((ph / 2) * HEIGHT_RATIO);
        float r = (pw / 2) > round ? round : (pw / 2);
        float hr = r + (r * HEIGHT_EXT_RATIO);
        Rect rect = new Rect((int) (x - r), (int) (y - hr), (int) (x + r), (int) (y + hr));
        Log.e(TAG, "FaceRoundView getPreviewDetectRect " + pw + "-" + ph + "-" + rect.toString());
        return rect;
    }

    // 设置扫描弧度
    public void setmBaseSweepAngle(float angle) {
        mBaseSweepAngle = angle;
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

    public static final String ANIM_ARC_LINE_OUTSIZE_MOVE = "anim_arc_line_outsize_move";

    // 开始画弧
    private void startArcLine() {
        isDrawArcLine = true;
        //外部圆弧移动控制
        ValueAnimator outSizeArcLineMoveAnim = animMap.get(ANIM_ARC_LINE_OUTSIZE_MOVE);
        if (outSizeArcLineMoveAnim == null) {
            outSizeArcLineMoveAnim = ValueAnimator.ofFloat().setDuration(600);
            outSizeArcLineMoveAnim.setRepeatCount(ValueAnimator.INFINITE);
            outSizeArcLineMoveAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    outSideArcStartAngle = (float) animation.getAnimatedValue();
                }
            });
            animMap.put(ANIM_ARC_LINE_OUTSIZE_MOVE, outSizeArcLineMoveAnim);
        }
        outSizeArcLineMoveAnim.setFloatValues(0, 60);
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
        mBaseSweepAngle = 180;
        mRectOutSideArc.set(220, 306, 860, 940);
    }

    // 停止播放动画
    public void stopAnimaion() {
        stopAnimAndRemoveCallbacks();
        //是否画相应的内容
        isDrawArcLine = false;
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

    private Rect rect = null;
    private boolean isDrawPeopleRect = false;

    // 画人脸框（矩形）
    public void drawPeopleRect(Rect rect) {
        isDrawPeopleRect = true;
        this.rect = rect;
    }

    // 停止绘画人脸框
    public void stopDrawPeopleRect() {
        isDrawPeopleRect = false;
        rect = null;
    }

}