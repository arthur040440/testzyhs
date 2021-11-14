package com.tlh.android.widget.customText;

import android.view.View.OnClickListener;

/**
 * @author tlh
 * @time 2018/1/30 13:42
 * @desc 用于记录文本的内容，字体，大小和监听器
*/

public class TextBean {
	
	private OnClickListener onClickListener;
	private String text;
	private int size;
	private int color;

	public TextBean() {}

	public TextBean(String text, int size, int color){
		this.text = text;
		this.size = size;
		this.color = color;
	}

	public void setOnClickListener(OnClickListener onClickListener){
		this.onClickListener = onClickListener;
	}

	public String getText() {
		return text;
	}

	public int getSize() {
		return size;
	}

	public int getColor() {
		return color;
	}

	public OnClickListener getOnClickListener() {
		return onClickListener;
	}
}
