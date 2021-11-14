package com.tlh.android.widget.customText;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author tlh
 * @time 2018/1/30 13:42
 * @desc 用于更改文字点击的事件和效果
*/
public class MyClickableSpan extends ClickableSpan {


	private OnClickListener mOnClickListener;

	public MyClickableSpan(OnClickListener onClickListener) {
		mOnClickListener = onClickListener;
	}

	@Override
	public void onClick(View v) {
		if (mOnClickListener != null) {
			mOnClickListener.onClick(v);
		}
	}

	@Override
	public void updateDrawState(TextPaint ds) {

	}

}