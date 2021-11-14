package com.tlh.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.zhangyuhuishou.zyhs.R;

/**
 * @author tlh
 * @time 2018/10/19 17:15
 * @desc 横向进度条（下载专用）
 */

public class DownHorizontalProgressBar extends View {

    private Paint paint;// 画笔
    private int progress;// 进度值
    private int width;// 宽度值
    private int height;// 高度值

    public DownHorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DownHorizontalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DownHorizontalProgressBar(Context context) {
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
        drawRect(canvas);
    }

    // 画矩形进度条
    private void drawRect(Canvas canvas){
        canvas.drawRect(0, 0, (progress * width) / 100, height, paint);// 画矩形
        canvas.drawLine(0, 0, width, 0, paint);// 画顶边
        canvas.drawLine(0, 0, 0, height, paint);// 画左边
        canvas.drawLine(width, 0, width, height, paint);// 画右边
        canvas.drawLine(0, height, width, height, paint);// 画底边
    }

    // 启动动画
    public void startAnim(int progress) {
        this.progress = progress;
        invalidate();
    }
}
