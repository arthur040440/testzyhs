package com.tlh.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import com.tlh.android.utils.UIUtils;
import com.zhangyuhuishou.zyhs.R;

public class ImageTextButton extends android.support.v7.widget.AppCompatTextView {

    private Drawable drawableLeft,drawableRight,drawableTop;
    private int scaleWidth;
    private int scaleHeight;
    private int topWidth;
    private int topHeight;
    private boolean isBad = false;// 状态

    public ImageTextButton(Context context) {
        super(context);

    }

    public ImageTextButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public ImageTextButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageTextButton);
        drawableLeft = typedArray.getDrawable(R.styleable.ImageTextButton_leftDrawable);
        drawableRight = typedArray.getDrawable(R.styleable.ImageTextButton_rightDrawable);
        drawableTop = typedArray.getDrawable(R.styleable.ImageTextButton_topDrawable);
        scaleWidth = typedArray.getDimensionPixelOffset(R.styleable.ImageTextButton_drawableWidth,48);
        scaleHeight = typedArray.getDimensionPixelOffset(R.styleable.ImageTextButton_drawableHeight, 31);
        topWidth = typedArray.getDimensionPixelOffset(R.styleable.ImageTextButton_topWidth,65);
        topHeight = typedArray.getDimensionPixelOffset(R.styleable.ImageTextButton_topHeight, 65);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (drawableLeft != null) {
            drawableLeft.setBounds(0, 0, scaleWidth, scaleHeight);
        }
        if (drawableRight != null) {
            drawableRight.setBounds(0, 0, scaleWidth, scaleHeight);
        }

        if (drawableTop != null) {
            drawableTop.setBounds(0, 0, topWidth, topHeight);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.setCompoundDrawables(drawableLeft, drawableTop, drawableRight, null);
    }

    /**
     * 设置左侧图片并重绘
     * @param drawableLeft
     */
    public void setDrawableLeft(Drawable drawableLeft) {
        this.drawableLeft = drawableLeft;
        invalidate();
    }

    /**
     * 设置左侧图片并重绘
     * @param drawableLeftRes
     */
    public void setDrawableLeft(int drawableLeftRes) {
        this.drawableLeft = UIUtils.getContext().getResources().getDrawable(drawableLeftRes);
        invalidate();
    }

    /**
     * 设置右侧图片并重绘
     * @param drawableTopRes
     */
    public void setDrawableTop(int drawableTopRes){
        this.drawableTop = UIUtils.getContext().getResources().getDrawable(drawableTopRes);
        invalidate();
    }

    public void setDrawableTop(Drawable drawableTop){
        this.drawableTop = drawableTop;
        invalidate();
    }

    // 是否坏了
    public void setDeivceBad(boolean isBad){
        this.isBad = isBad;
        if(isBad){
            setTextColor(Color.parseColor("#FF3838"));
            this.drawableTop = createTintedDrawable(this.drawableTop,getResources().getColor(R.color.red));
        }else {
            setTextColor(Color.parseColor("#000000"));
            this.drawableTop = createTintedDrawable(this.drawableTop,getResources().getColor(R.color.black));
        }
        invalidate();
    }

    public boolean isBad() {
        return isBad;
    }

    private Drawable createTintedDrawable(@Nullable Drawable drawable, @ColorInt int color) {
        if(drawable == null) {
            return null;
        } else {
            drawable = DrawableCompat.wrap(drawable.mutate());
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
            DrawableCompat.setTint(drawable, color);
            return drawable;
        }
    }
}
