package com.tlh.android.utils;

import android.content.Context;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static final String TAG = Utils.class.getName();
    public static final boolean DEBUG = true;

    public static boolean isEmpty(String value) {
        return value == null || value.length() < 1 || "null".equals(value);
    }

    public static String formatDate(long dateline, String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(new Date(dateline));
    }

    public static String leftTrim(String string) {
        if (string == null)
            return null;

        return string.replaceAll("^\\s*", "");
    }

    public static String rightTrim(String string) {
        if (string == null)
            return null;

        return string.replaceAll("\\s$", "");
    }

    public static String trim(String string) {
        if (string == null)
            return null;

        return string.replaceAll("(^\\s*)|(\\s$)", "");
    }


    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    public static boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    public static boolean hideKeyboard(Context context, IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            return im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return false;
    }

    //判断用户输入的颜色值是否为十六进制
    private static final String HEX_PATTERN = "^#([0-9a-fA-F]{6}|[0-9a-fA-F]{3})$";

    public static boolean isHexColor(String color) {
        return color != null && color.matches(HEX_PATTERN);
    }
}
