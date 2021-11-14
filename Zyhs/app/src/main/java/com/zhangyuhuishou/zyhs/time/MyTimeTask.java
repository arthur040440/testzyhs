package com.zhangyuhuishou.zyhs.time;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

public class MyTimeTask extends TimerTask {

    private Timer timer = null;
    private static boolean flag = false;
    private static MyTimeTask mybTimeTask = null;
    private TimeQueryUtils timeQueryUtils;

    private MyTimeTask() {
    }

    public static MyTimeTask getInstance() {
        if (mybTimeTask == null || flag) {
            mybTimeTask = new MyTimeTask();
            if (flag) {
                flag = false;
            }
        }
        return mybTimeTask;
    }

    public void start(TimeQueryUtils timeQueryUtils) {

        if (timer == null) {
            timer = new Timer();
        } else {
            //从此计时器的任务队列中移除所有已取消的任务。
            timer.purge();
        }
        this.timeQueryUtils = timeQueryUtils;
        timer.schedule(this, 0, 3600000);
        System.out.println("定时任务开始...............");
    }

    public void run() {
        System.out.println("定时任务执行：" + System.currentTimeMillis());
        timeQueryUtils.doCloseScheduleByOpen();
        timeQueryUtils.doTimerSchedule();
        timeQueryUtils.doOpenLamp();
        timeQueryUtils.doIfOpenFan();
//        timeQueryUtils.doOpenShaJun();
    }

    public void destroyed() {
        System.out.println("定时任务销毁............................");
        //终止此计时器，丢弃所有当前已安排的任务。(不但结束当前schedule，连整个Timer的线程(即当前的定时任务)都会结束掉)
        if (timer != null) {
            timer.cancel();
            timer.purge();
            flag = true;
        }
    }
}
