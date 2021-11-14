package com.tlh.android.utils;

public class ColorUtil {

    /**
     * 颜色设置alpha
     * @param colorString 颜色值 如: "#ffffff"
     * @param alphaString 透明度值 如: "#aa"
     * @return
     */
    public static int parseColor(String colorString, String alphaString) {
        if (colorString.charAt(0) == '#') {
            // Use a long to avoid rollovers on #ffXXXXXX
            long color = Long.parseLong(colorString.substring(1), 16);
            if (colorString.length() == 7) {
                // Set the alpha value
//                alpha |= 0x0000000000000000;
                color = parseColor(color, alphaString);
            } else if (colorString.length() != 9) {
                throw new IllegalArgumentException("Unknown color");
            }
            return (int)color;
        }
        throw new IllegalArgumentException("Unknown color");
    }

    /**
     * 颜色设置alpha
     * @param color 颜色值
     * @param alphaString 透明度值 如: "#aa"
     * @return
     */
    public static int parseColor(long color, String alphaString) {
        if (alphaString.charAt(0) == '#' && alphaString.length() == 3) {
            int alpha = Integer.parseInt(alphaString.substring(1), 16);
            alpha = alpha << 24;
            alpha &= 0x00000000ff000000;
            color |= alpha;
        } else {
            throw new IllegalArgumentException("Unknown alpha");
        }
        return (int)color;
    }
}
