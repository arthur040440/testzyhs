package com.zhangyuhuishou.zyhs.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.carmelo.library.KeepliveService;
import com.zhangyuhuishou.zyhs.actys.SplashActivity;

/**
 * 作者:created by author:tlh
 * 日期:2018/12/13 17:57
 * 邮箱:tianlihui2234@live.com
 * 描述:保活服务
 */
public class keepAppLiveService extends KeepliveService{


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int i = super.onStartCommand(intent, flags, startId);
        Intent keepIntent = new Intent(this, SplashActivity.class);
        keepIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(keepIntent);
        //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
        android.os.Process.killProcess(android.os.Process.myPid());
        return i;
    }
}
