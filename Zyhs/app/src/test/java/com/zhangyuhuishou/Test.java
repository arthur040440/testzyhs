package com.zhangyuhuishou;

public class Test {

    public static void main(String[] args) {
        String str = "FE0101FF090101160D";
        String checkHex = getCheckHex(str.substring(0, 14));
        System.out.println(checkHex);
    }


    // 取余并且转化为16进制
    public static String getCheckHex(String hexdata) {
        int sum = makeChecksum(hexdata);
        int remNum = Integer.valueOf(sum) % 100;
        return decimal2fitHex(remNum);
    }

    public static int makeChecksum(String hexdata) {
        if (hexdata == null || hexdata.equals("") || "nu".equals(hexdata)) {
            return 0;
        }
        hexdata = hexdata.replaceAll(" ", "");
        int total = 0;
        int len = hexdata.length();
        if (len % 2 != 0) {
            return 0;
        }
        int num = 0;
        while (num < len) {
            String s = hexdata.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        return total;
    }

    /**
     * 把十进制数字转换成足位的十六进制字符串,并补全空位
     *
     * @param num
     * @return
     */
    public static String decimal2fitHex(long num) {
        String hex = Long.toHexString(num).toUpperCase();
        if (hex.length() % 2 != 0) {
            return "0" + hex;
        }
        return hex.toUpperCase();
    }

}
