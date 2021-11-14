package com.tlh.android.widget.customText;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class TextParser {

	private List<TextBean> textList;

	public TextParser() {
		textList = new LinkedList<TextBean>();
	}

	// 添加文字
	public TextParser append(String text, int size, int color) {
		if (text == null) {
			return this;
		}
		textList.add(new TextBean(text,size,color));
		return this;
	}

	// 添加带链接(可点击)的文字
	public TextParser append(String text, int size, int color, OnClickListener onClickListener) {
		if (text == null) {
			return this;
		}
		TextBean bean = new TextBean(text,size,color);
		bean.setOnClickListener(onClickListener);
		textList.add(bean);
		return this;
	}

	// 组装文字然后修饰
	public void parse(TextView textView) {

		StringBuilder sBuilder = new StringBuilder();
		for (TextBean bean : textList) {
			sBuilder.append(bean.getText());
		}

		// SpannableStringBuilder用于创建Spannable,它是Spannable的一个实现类,所有的文字和效果都要写在Spannable中
		SpannableStringBuilder style = new SpannableStringBuilder(sBuilder);
		int position = 0;
		for (TextBean bean : textList) {
			if (bean.getOnClickListener() != null) {
				// 如果点击,则在上面添加点击处理的Span,Span接口用于实现对文本的修饰的具体内容
				style.setSpan(new MyClickableSpan(bean.getOnClickListener()),
						position,// 修饰的起始位置
						position + bean.getText().length(),// 修饰的结束位置
						Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			}

			// 字体上色，字体的背景颜色也可以单独改变
			style.setSpan(new ForegroundColorSpan(bean.getColor()), position,
					position + bean.getText().length(),
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

			// 改变字体大小
			style.setSpan(new AbsoluteSizeSpan(bean.getSize()),
					position,
					position + bean.getText().length(),
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

			position += bean.getText().length();

		}
		// 设置TextView让文字可以被点击
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		textView.setText(style);
	}
}
