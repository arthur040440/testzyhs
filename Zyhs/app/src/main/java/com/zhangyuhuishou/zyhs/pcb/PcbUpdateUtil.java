package com.zhangyuhuishou.zyhs.pcb;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 作者:created by author:tlh
 * 日期:2019/4/24 10:53
 * 邮箱:tianlihui2234@live.com
 * 描述:读取本地硬件程序
 */

public class PcbUpdateUtil {

    private static String TAG = PcbUpdateUtil.class.getSimpleName();
    public static String mTempPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "pcbSys";

    // 读取assets目录下的文件
    public static byte[] readAssetsFile(Context context) {

        AssetManager manager = context.getAssets();
        byte bt[] = null;
        try {
            InputStream is = manager.open("RTC.bin");
            int length = is.available();
            bt = new byte[length];
            is.read(bt);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bt;
    }

    // 读取sd卡中的文件
    @SuppressWarnings("all")
    public static byte[] readSdcardFile(Context context) {

        File filePath = new File(mTempPath);
        if(!filePath.exists()){
            filePath.mkdir();
        }

        byte bt[] = null;
        File file = new File(mTempPath + File.separator + "RTC.bin");
        try{
            if (file != null && file.isFile() && file.exists()){
                InputStream is = new FileInputStream(file);
                int length = is.available();
                bt = new byte[length];
                is.read(bt);
            }else {
                Log.i(TAG,"文件不存在");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return bt;
    }

    // 格式化数据（转换为16进制）
    public static String format(byte[] bt) {
        if(bt == null){
            return "文件不存在";
        }

        int line = 0;
        StringBuilder buf = new StringBuilder();
        for (byte d : bt) {
            if (line % 128 == 0)
            System.out.println(String.format("%05x:", line));
//          buf.append(String.format("%05x:", line));
            buf.append(String.format("%02x", d));
            line++;
            if (line % 128 == 0)
                buf.append("\n");
        }
        return buf.toString();
    }
}
