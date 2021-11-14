package com.zhangyuhuishou.zyhs.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.zhangyuhuishou.zyhs.actys.SplashActivity;

/**
 * 作者: create by tlh on 2018/7/4 13:38
 * 邮箱: tianlihui2234@live.com
 * 描述: 开机自启动接收器
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent intentToHome = new Intent(context, SplashActivity.class);
            intentToHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentToHome);
        }
    }
}
