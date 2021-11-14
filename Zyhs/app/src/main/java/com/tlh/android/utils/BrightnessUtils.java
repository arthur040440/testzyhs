package com.tlh.android.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

/**
 * 作者:created by author:tlh
 * 日期:2018/11/4 22:21
 * 邮箱:tianlihui2234@live.com
 * 描述:屏幕亮度工具类
 * 如果只在自己的activity显示期间调节亮度 , 那就直接设置亮度(3)并且关闭自动亮度调节(4) , 当该Activity退出后应该恢复自动亮度调节(5) ;
 　如果想直接对手机进行设置 , 那设置亮度(3)后 , 应该保存亮度设置状态(6) ;
 　以上6个方法配合使用即可 .
 */
public class BrightnessUtils {

    // （1）判断是否开启了自动亮度调节
    public static boolean isAutoBrightness(ContentResolver aContentResolver) {
        boolean automicBrightness = false;
        try{
            automicBrightness = Settings.System.getInt(aContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE) ==Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch(Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }

    // （2）获取屏幕的亮度
    public static int getScreenBrightness(Activity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try{
            nowBrightnessValue = Settings.System.getInt(resolver,Settings.System.SCREEN_BRIGHTNESS);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    // （3）设置亮度
    public static void setBrightness(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = brightness * (1f / 255f);
        Log.d("lxy", "set  lp.screenBrightness == " + lp.screenBrightness);
        activity.getWindow().setAttributes(lp);
    }

    // （4）停止自动亮度调节
    public static void stopAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,  Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    // （5）开启亮度自动调节
    public static void startAutoBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    // （6）保存亮度设置状态
    public static void saveBrightness(ContentResolver resolver, int brightness) {
        Uri uri = Settings.System.getUriFor("screen_brightness");
        Settings.System.putInt(resolver, "screen_brightness", brightness);
        resolver.notifyChange(uri, null);
    }


}
