package com.tlh.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.zhangyuhuishou.zyhs.R;

public class UnderLineEditText extends android.support.v7.widget.AppCompatEditText {

    private Paint paint;

    public UnderLineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        //设置画笔的属性
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        //设置画笔颜色默认颜色
        paint.setColor(context.getResources().getColor(R.color.phone_num_underline, null));
        paint.setStrokeWidth(2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**canvas画直线，从左下角到右下角，this.getHeight()-2是获得父edittext的高度，但是必须要-2这样才能保证
         * 和原来的下划线的重合
         */
        canvas.drawLine(0, this.getHeight() - 10, this.getWidth() - 2, this.getHeight() - 10, paint);
    }

    public void setEditTextColor(int color) {
        paint.setColor(color);
        invalidate();
    }
}
