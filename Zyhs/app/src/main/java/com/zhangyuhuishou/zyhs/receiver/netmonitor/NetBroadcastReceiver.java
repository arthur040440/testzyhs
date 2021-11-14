package com.zhangyuhuishou.zyhs.receiver.netmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import com.zhangyuhuishou.zyhs.base.BaseActivity;

/**
 * 作者: create by tlh on 2018/7/17 14:10
 * 邮箱: tianlihui2234@live.com
 * 描述: 自定义检查手机网络状态是否切换的广播接受器
 */

public class NetBroadcastReceiver extends BroadcastReceiver {

    public NetEvent event = BaseActivity.event;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果相等的话就说明网络状态发生了变化
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int netWorkState = NetUtil.getNetWorkState(context);
            // 接口回调传过去状态的类型
            if(event != null){
                event.onNetChange(netWorkState);
            }
        }
    }

    // 自定义接口
    public interface NetEvent {
        void onNetChange(int netMobile);
    }
}
