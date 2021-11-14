package com.tlh.android.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 
 * @author tlh
 * 
 */
public class ToastUitls {
	
	private static Toast mToast = null;

	public static void toastMessage(String msg) {
		
		if (mToast == null) {
			mToast = Toast.makeText(UIUtils.getContext(), msg, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(msg);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		
		setToastAppearence(UIUtils.getContext());
		
	}

	public static void toastMessage(int textId) {
		if (mToast == null) {
			mToast = Toast.makeText(UIUtils.getContext(), textId, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(textId);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		
		setToastAppearence(UIUtils.getContext());
		
	}
	
	private static void setToastAppearence(Context context){
//		View v = mToast.getView();
//		v.setBackgroundResource(R.drawable.yjs_toast_border);
//		mToast.setView(v);
//		mToast.setGravity(Gravity.BOTTOM, 0,  (int)context.getResources().getDisplayMetrics().density * 80);
		mToast.show();
	}
}
