package com.tlh.android.utils;

import android.util.Log;

/**
 * 作者:created by author:tlh
 * 日期:2018/11/12 11:36
 * 邮箱:tianlihui2234@live.com
 * 描述:打印日志的工具类
 */
public class LogUtil {
    private static int LOG_MAXLENGTH = 4080;

    public static void e(String TAG, String msg) {
        int strLength = msg.length();
        int start = 0;
        int end = LOG_MAXLENGTH;
        for (int i = 0; i < 100; i++) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.e(TAG + i, msg.substring(start, end));
                start = end;
                end = end + LOG_MAXLENGTH;
            } else {
                Log.e(TAG, msg.substring(start, strLength));
                break;
            }
        }
    }
}
