package com.zhangyuhuishou.zyhs.time;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

/**
 * Copyright: Copyright (c) 2017-2025
 * Class:  实时更新时间的线程
 *
 * @author: tlh
 * @date: 2018/10/18
 * describe:
 */
public class TimeThread extends Thread {

    public TextView tvDate;
    private int msgKey1 = 22;
    private MyHandler handler = new MyHandler();
    private final String TAG = TimeThread.class.getSimpleName();
    private boolean threadIsLive = true;
    private String source = "";

    public TimeThread(TextView tvDate,String source) {
        this.tvDate = tvDate;
        this.source = source;
    }



    @Override
    public void run() {
        do {
            if (!threadIsLive) {
                return;
            }
            try {
                Thread.sleep(1000);
                handler.sendEmptyMessage(msgKey1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (true);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, source + ":" + TimeUtils.getTime() + "  " + TimeUtils.getWeekOfDate());
            tvDate.setText(TimeUtils.getTime() + "  " + TimeUtils.getWeekOfDate());
        }
    }

    // 页面结束时一出队列任务
    public void removeTask() {
        threadIsLive = false;
        if (handler != null) {
            handler.removeCallbacks(this);
        }
    }

}

