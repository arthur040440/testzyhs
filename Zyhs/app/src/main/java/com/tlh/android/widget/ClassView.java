package com.tlh.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.zhangyuhuishou.zyhs.R;

/**
 * 作者:created by author:tlh
 * 日期:2018/11/2 9:54
 * 邮箱:tianlihui2234@live.com
 * 描述:分类图标
 */
public class ClassView extends View{

    private int mArcColor = 0;// 弧颜色
    private float mArcThick = 0;// 弧粗细
    private RectF mArcRect;

    private int mCircleColor = 0;// 圆颜色
    private float mCircleThick = 0;// 圆粗细

    private Drawable mClassDrawable = null;// 分类图标
    private Bitmap mClassBitmap;
    private Rect mClassRecct;// 分类图标绘制范围
    private int mClassDrawableWidth = 0;// 分类图标宽度
    private int mClassDrawableHeight = 0;// 分类图标高度

    private String mClassText = "";// 分类文字
    private int mClassTextColor = 0;// 分类文字颜色
    private float mClassTextSize = 0;// 分类文字大小

    private Paint mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 弧画笔
    private Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 圆画笔
    private Paint mClassTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 文字画笔

    public ClassView(Context context) {
        super(context);
        initParams();
    }

    public ClassView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initParams();
    }

    public ClassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClassView);
        mArcColor = typedArray.getColor(R.styleable.ClassView_arc_color,getResources().getColor(R.color.black,null));
        mArcThick = typedArray.getFloat(R.styleable.ClassView_arc_thickness,20);
        mCircleColor = typedArray.getColor(R.styleable.ClassView_circle_color,getResources().getColor(R.color.theme_color,null));
        mCircleThick = typedArray.getFloat(R.styleable.ClassView_circle_thickness,10);
        mClassDrawable = typedArray.getDrawable(R.styleable.ClassView_class_drawable);
        mClassDrawableWidth = typedArray.getDimensionPixelOffset(R.styleable.ClassView_class_drawable_width,212);
        mClassDrawableHeight = typedArray.getDimensionPixelOffset(R.styleable.ClassView_class_drawable_height,178);
        mClassText = typedArray.getString(R.styleable.ClassView_class_text);
        mClassTextColor = typedArray.getColor(R.styleable.ClassView_class_text_color,getResources().getColor(R.color.black,null));
        mClassTextSize = typedArray.getFloat(R.styleable.ClassView_class_text_size,15);
        typedArray.recycle();
        initParams();
    }

    // 初始化参数
    private void initParams(){

        mArcPaint.setColor(mArcColor);
        mArcPaint.setStrokeWidth(mArcThick);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setDither(true);

        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStrokeWidth(mCircleThick);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setDither(true);

        mClassTextPaint.setColor(mClassTextColor);
        mClassTextPaint.setStrokeWidth(mClassTextSize);
        mClassTextPaint.setDither(true);

        BitmapDrawable bd = (BitmapDrawable) mClassDrawable;
        mClassBitmap = bd.getBitmap();

        mArcRect = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mClassRecct = new Rect((w - mClassDrawableWidth) / 2, 0,  (w + mClassDrawableWidth) / 2,  mClassDrawableHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mArcRect.set(0,0,getWidth(),getHeight());
        canvas.drawArc(mArcRect,30,10,false,mArcPaint);
        canvas.drawArc(mArcRect,50,180,false,mArcPaint);
        canvas.drawArc(mArcRect,240,10,false,mArcPaint);
        canvas.drawArc(mArcRect,260,180,false,mArcPaint);

        canvas.drawCircle(getWidth() / 2,getHeight() / 2,getWidth() / 2 - 10,mCirclePaint);

        canvas.drawBitmap(mClassBitmap,null,mClassRecct,null);

    }
}
