package com.tlh.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RadioButton;

public class IconRadioButton extends android.support.v7.widget.AppCompatRadioButton {
	private Drawable buttonDrawable;
	
	public IconRadioButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public IconRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IconRadioButton(Context context) {
		super(context);
	}
	
	@Override
	public void setButtonDrawable(Drawable d) {
		buttonDrawable = d;
		
		Drawable trans = getResources().getDrawable(android.R.color.transparent);
		super.setButtonDrawable(trans);
	}

	@Override
	public void setButtonDrawable(int resid) {
		if(resid == 0)
			return;
		
		Drawable d = getResources().getDrawable(resid);
		
		buttonDrawable = d;
		
		Drawable trans = getResources().getDrawable(android.R.color.transparent);
		super.setButtonDrawable(trans);
	}

	public void setButtonDrawable(String str){
		if(str == "")
			return;
		
	}
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);  
        if (buttonDrawable != null) {  
            buttonDrawable.setState(getDrawableState());  
            final int verticalGravity = getGravity()  
                    & Gravity.VERTICAL_GRAVITY_MASK;
            final int height = buttonDrawable.getIntrinsicHeight();  
            int y = 0;  
            switch (verticalGravity) {  
            case Gravity.BOTTOM:
                y = getHeight() - height;  
                break;  
            case Gravity.CENTER_VERTICAL:
                y = (getHeight() - height) / 2;  
                break;  
            }  
            int buttonWidth = buttonDrawable.getIntrinsicWidth();  
            int buttonLeft = (getWidth() - buttonWidth) / 2;  
            buttonDrawable.setBounds(buttonLeft, y, buttonLeft + buttonWidth, y + height);
            buttonDrawable.draw(canvas);  
        }
    }  
}
