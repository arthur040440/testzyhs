package com.tlh.android.utils;

/**
 * 作者:created by author:tlh
 * 日期:2018/12/19 15:54
 * 邮箱:tianlihui2234@live.com
 * 描述: 数字工具类
 */
public class NumberUtils {

    public static String toChineseNumber(String arabNumber) {
        String[] bit = {"零","一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] ten = {"十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千"};
        String result = "";
        int n = arabNumber.length();
        for (int i = 0; i < arabNumber.length(); i++) {
            int num = arabNumber.charAt(i) - '0';
            if (i != n - 1 && num != 0) {
                result += bit[num] + ten[n - 2 - i];
            } else {
                result += bit[num];
            }
        }
        return result;
    }
}
