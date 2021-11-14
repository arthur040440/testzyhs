package com.zhangyuhuishou.zyhs.time;

import android.content.Context;
import android.util.Log;

import com.zhangyuhuishou.zyhs.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 作者:created by author:tlh
 * 日期:2018/10/12 0:07
 * 邮箱:tianlihui2234@live.com
 * 描述: 时间工具类
 */
public class TimeUtils {

    // 获取时间
    public static String getTime() {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
        return format.format(date);
    }

    // 获取星期
    public static String getWeek() {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("EEEE", Locale.CHINA);
        return format.format(date);
    }

    /**
     * 获取当前日期是星期几
     *
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate() {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static boolean isBelongAmmeter(Context context) {

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date now = null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(df.format(new Date()));
            beginTime = df.parse(context.getResources().getString(R.string.sys_ammeter_start_time));
            endTime = df.parse(context.getResources().getString(R.string.sys_ammeter_end_time));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return belongCalendar(now, beginTime, endTime);
    }

    public static boolean isBelongAmmeter_afternoon(Context context) {

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date now = null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(df.format(new Date()));
            beginTime = df.parse(context.getResources().getString(R.string.sys_ammeter_start_time_afternoon));
            endTime = df.parse(context.getResources().getString(R.string.sys_ammeter_end_time_afternoon));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return belongCalendar(now, beginTime, endTime);
    }

    public static boolean isBelongUpdate(Context context) {

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date now = null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(df.format(new Date()));
            beginTime = df.parse(context.getResources().getString(R.string.app_update_start_time));
            endTime = df.parse(context.getResources().getString(R.string.app_update_end_time));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return belongCalendar(now, beginTime, endTime);
    }

    // 是否更新广告
    public static boolean isUpdateBanner(Context context) {

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date now = null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(df.format(new Date()));
            beginTime = df.parse(context.getResources().getString(R.string.banner_update_start_time));
            endTime = df.parse(context.getResources().getString(R.string.banner_update_end_time));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return belongCalendar(now, beginTime, endTime);
    }

    // 是否静音
    public static boolean isVolumeMute(Context context) {

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date now = null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(df.format(new Date()));
            beginTime = df.parse(context.getResources().getString(R.string.sys_volume_is_mute_start_time));
            endTime = df.parse(context.getResources().getString(R.string.sys_volume_is_mute_end_time));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return belongCalendar(now, beginTime, endTime);
    }


    // 6月1号10：00-11：00清空电表数据
    public static boolean isBelong61_clear_electricmeter(Context context) {
        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");//设置日期格式
        Date now = null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(df.format(new Date()));
            beginTime = df.parse(context.getResources().getString(R.string.clear_electricmeter_start_time));
            endTime = df.parse(context.getResources().getString(R.string.clear_electricmeter_end_time));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return belongCalendar(now, beginTime, endTime);
    }


    // 6月5号当天播放河马图片
    public static boolean isBelong65_show_banner(Context context) {
        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");//设置日期格式
        Date now = null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(df.format(new Date()));
            beginTime = df.parse(context.getResources().getString(R.string.hema_showbanner_start_time));
            endTime = df.parse(context.getResources().getString(R.string.hema_showbanner_end_time));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return belongCalendar(now, beginTime, endTime);
    }


    // 关闭HDMI
    public static boolean isBelongCloseHdmi(Context context) {

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date now = null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(df.format(new Date()));
            beginTime = df.parse(context.getResources().getString(R.string.close_hdmi_start_time));
            endTime = df.parse(context.getResources().getString(R.string.close_hdmi_end_time));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return belongCalendar(now, beginTime, endTime);
    }

    // 开启HDMI
    public static boolean isBelongOpenHdmi(Context context) {

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date now = null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(df.format(new Date()));
            beginTime = df.parse(context.getResources().getString(R.string.open_hdmi_start_time));
            endTime = df.parse(context.getResources().getString(R.string.open_hdmi_end_time));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return belongCalendar(now, beginTime, endTime);
    }

    // 信息状态（满溢）
    public static boolean isSystemRestTime(Context context) {

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date now = null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(df.format(new Date()));
            beginTime = df.parse(context.getResources().getString(R.string.sys_not_send_full_start_time));
            endTime = df.parse(context.getResources().getString(R.string.sys_not_send_full_end_time));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return belongCalendar(now, beginTime, endTime);
    }

    // 设备重新启动
    public static boolean isRebootTime(Context context) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date now = null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(df.format(new Date()));
            beginTime = df.parse(context.getResources().getString(R.string.reboot_start_time));
            endTime = df.parse(context.getResources().getString(R.string.reboot_end_time));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return belongCalendar(now, beginTime, endTime);
    }

    // 是否更新PCB
    public static boolean isUpdatePCB(Context context) {

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date now = null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(df.format(new Date()));
            beginTime = df.parse(context.getResources().getString(R.string.pcb_update_start_time));
            endTime = df.parse(context.getResources().getString(R.string.pcb_update_end_time));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return belongCalendar(now, beginTime, endTime);
    }

    public static boolean isFirstRebootTime(Context context) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date now = null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(df.format(new Date()));
            beginTime = df.parse(context.getResources().getString(R.string.reboot_first_start_time));
            endTime = df.parse(context.getResources().getString(R.string.reboot_first_end_time));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return belongCalendar(now, beginTime, endTime);
    }

    // 更新点位
    public static boolean isUpdateLocation(Context context) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date now = null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(df.format(new Date()));
            beginTime = df.parse(context.getResources().getString(R.string.update_location_start_time));
            endTime = df.parse(context.getResources().getString(R.string.update_location_end_time));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return belongCalendar(now, beginTime, endTime);
    }

    /**
     * 判断时间是否在时间段内
     *
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取当前日期时间
     *
     * @return
     */

    public static String getNowDate() {
        String temp_str = "";
        Date dt = new Date();
        //最后的aa表示“上午”或“下午”  HH表示24小时制  如果换成hh表示12小时制
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
        temp_str = sdf.format(dt);
        return temp_str;
    }

    /**
     * 获取当前日期时间
     *
     * @return
     */

    public static String getDateByNow() {
        String temp_str = "";
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        temp_str = sdf.format(dt);
        return temp_str;
    }


}
