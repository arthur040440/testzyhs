package com.tlh.android.utils;

import java.io.DataOutputStream;
import java.io.OutputStream;

/**
 * 作者: create by tlh on 2018/7/19 14:00
 * 邮箱: tianlihui2234@live.com
 * 描述: 系统命令（需要Root权限）
 */
public class SystemCmdUtils {

    // 关机
    public static boolean shutdown(){
        return execCmd("reboot -p");
    }

    // 重启
    public static boolean reboot(){
        return execCmd("reboot");
    }

    // 打开HDMI
    public static boolean openHdmi(){
        return execCmd("setprop sys.hdmi_status.aux on");
    }

    // 关闭HDMI
    public static boolean closeHdmi(){
        return execCmd("setprop sys.hdmi_status.aux off");
    }

    // 模拟触摸屏幕
    public static void touchScreen(){
        execShellCmd("input tap 100 100");
    }

    // 模拟触摸屏幕（系统弹框消失）
    public static void disSysTk(){
        execShellCmd("input tap 840 960");
    }

    /*
     * 执行命令
     * @param command
     * 1、获取root权限 "chmod 777 "+getPackageCodePath()
     * 2、关机 reboot -p
     * 3、重启 reboot
     */
    public static boolean execCmd(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command+"\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if(process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 执行shell命令
     *
     * @param cmd
     */
    public static void execShellCmd(String cmd) {
        OutputStream outputStream = null;
        DataOutputStream dataOutputStream = null;
        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            outputStream = process.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
