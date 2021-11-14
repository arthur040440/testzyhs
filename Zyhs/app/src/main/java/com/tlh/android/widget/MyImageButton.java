package com.tlh.android.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

/*
 * 按钮(添加觸摸按扭反應)
 *
 * @author
 *
*/

@SuppressLint("AppCompatCustomView")
public class MyImageButton extends ImageButton {
	private static String TAG = "MyImageButton";

	// 按下这个按钮进行的颜色过滤
	public final float[] BT_SELECTED = new float[] { 0.75f, 0, 0, 0, 0, 0,
			0.75f, 0, 0, 0, 0, 0, 0.75f, 0, 0, 0, 0, 0, 1, 0 };
	// 按钮恢复原状的颜色过滤
	public final float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0,
			1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

	// 针对应用按钮编辑列表
	public int position = 0;

	public MyImageButton(Context context) {
		super(context);
		this.setOnTouchListener(new myButtonTouch(this));
	}

	public MyImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(new myButtonTouch(this));
	}

	public MyImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setOnTouchListener(new myButtonTouch(this));
	}

	// 传入正在编辑的按钮位置
	public void setPosition(int position) {
		this.position = position;
	}

	/****************************************************************************************************************
	 * 添加觸摸按扭反應
	 ****************************************************************************************************************/
	private class myButtonTouch implements OnTouchListener {

		private MyImageButton button = null;

		public myButtonTouch(MyImageButton button) {
			this.button = button;
		}

		@SuppressWarnings("deprecation")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (MyImageButton.this.isFocusable()) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					button.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
					Log.e(TAG, "DOWN");
					button.setBackgroundDrawable(v.getBackground());
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					button.setBackgroundDrawable(v.getBackground());
					button.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));
				}else if(event.getAction() == MotionEvent.ACTION_CANCEL){
					button.setBackgroundDrawable(v.getBackground());
					button.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));
				}
			}
			return false;
		}
	}
}

