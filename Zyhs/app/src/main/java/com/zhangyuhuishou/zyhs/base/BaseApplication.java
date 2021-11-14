package com.zhangyuhuishou.zyhs.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.baidu.tts.util.TTSUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.ApkUtils;
import com.zhangke.zlog.ZLog;
import com.zhangyuhuishou.zyhs.actys.SplashActivity;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;

/**
 * @author tlh
 * @time 2017/12/19 10:00
 * @desc
 */

public class BaseApplication extends Application {

    private static Context sContext;
    private static Handler sHandler;
    private static Typeface typeFace;

    public static Handler getHandler() {
        return sHandler;
    }

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        sHandler = new Handler(Looper.getMainLooper());

        // 设置字体样式（思源黑体）
//        setTypeface();

        // OkHttpFinal初始化
        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        builder.setTimeout(5000);
        OkHttpFinal.getInstance().init(builder.build());


        // bugly初始化
        CrashReport.initCrashReport(getApplicationContext(), "aa925f15f9", true);
        // 程序崩溃时触发线程  以下用来捕获程序崩溃异常
        Thread.setDefaultUncaughtExceptionHandler(handler);

        // 语音初始化
//        TTSUtils.init();

        // 卡顿检测
//        BlockCanary.install(this, new AppContext()).start();
//        Looper.getMainLooper().setMessageLogging(new Printer() {
//            private static final String START = ">>>>Dispatching";
//            private static final String END = "<<<<Finished";
//            @Override
//            public void println(String x) {
//
//                if(x.startsWith(START)){
//                    LogMonitor.getInstance().startMonitor();
//                }
//
//                if(x.startsWith(END)){
//                    LogMonitor.getInstance().removeMonitor();
//                }
//            }
//        });


        // 日志工具初始化
        try {
            ZLog.Init(String.format("%s/log/", getExternalFilesDir(null).getPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            try {
                String channelInfo = ApkUtils.getAppMetaData(sContext, "UMENG_CHANNEL");
                if (!"factory".equals(channelInfo) && !Constant.SERVER_IP_NEW.contains("test")) {
                    CrashReport.postCatchedException(e);
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
            Log.e("AndroidRuntime", "--->CockroachException:" + t + "<---", e);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("App应用需要重启");
                    restartApp(); //发生崩溃异常时,重启应用
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 1000);
        }
    };

    private void restartApp() {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 通过反射方法设置app全局字体
     */
    public void setTypeface() {
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/syht.ttf");
        try {
            Field field = Typeface.class.getDeclaredField("SERIF");
            field.setAccessible(true);
            field.set(null, typeFace);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
