package com.zhangyuhuishou.zyhs.time;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.tlh.android.config.Constant;
import com.tlh.android.utils.ApkUtils;
import com.tlh.android.utils.Utils;
import com.zhangyuhuishou.zyhs.database.BucketStatusModel;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.TerminalModel;
import com.zhangyuhuishou.zyhs.serialport.constant.NormalConstant;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortManager;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortManagerNew;
import com.zhangyuhuishou.zyhs.serialport.util.ByteUtil;
import com.zhangyuhuishou.zyhs.serialport.util.Command;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


// 命令查询工具类
public class TimeQueryUtils {

    private String TAG = TimeQueryUtils.class.getSimpleName();

    private Handler handler;
    private int time = 2000;
    private int queryTime = 0;
    private int limitTemperature = 40;

    private List<TerminalModel> plasticBottleList;
    private List<TerminalModel> glassBottleList;
    private List<TerminalModel> paperList;
    private List<TerminalModel> spinList;
    private List<TerminalModel> metalList;
    private List<TerminalModel> plasticList;
    private List<TerminalModel> harmfulList;
    private DBManager dbManager;
    private Context context;

    private String channelInfo;// 渠道信息

    public TimeQueryUtils(Handler handler, Context context) {
        this.handler = handler;
        this.context = context;
        dbManager = DBManager.getInstance(context);
        channelInfo = ApkUtils.getAppMetaData(context, "UMENG_CHANNEL");
        plasticBottleList = dbManager.getDevListByType(Constant.DEV_PLASTIC_TYPE);
        glassBottleList = dbManager.getDevListByType(Constant.DEV_GLASS_TYPE);
        paperList = dbManager.getDevListByType(Constant.DEV_PAPER_TYPE);
        spinList = dbManager.getDevListByType(Constant.DEV_SPIN_TYPE);
        metalList = dbManager.getDevListByType(Constant.DEV_METAL_TYPE);
        plasticList = dbManager.getDevListByType(Constant.DEV_CEMENT_TYPE);
        harmfulList = dbManager.getDevListByType(Constant.DEV_POISON_TYPE);
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getQueryTime() {
        return queryTime;
    }

    public void initQueryTime() {
        this.queryTime = 0;
    }

    // 执行查询任务
    public void doTimerSchedule() {
        if (plasticBottleList != null) {
            for (int i = 0; i < plasticBottleList.size(); i++) {
                queryTime = queryTime + time;
                final TerminalModel model = plasticBottleList.get(i);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dbManager.updateDev(model.getId(), NormalConstant.TYPE_PLASTIC);
                        SerialPortManager.instance().sendCommand(Command.queryBucket(NormalConstant.TYPE_PLASTIC));
                    }
                }, queryTime);
            }
        }

        if (glassBottleList != null) {
            for (int i = 0; i < glassBottleList.size(); i++) {
                queryTime = queryTime + time;
                final TerminalModel model = glassBottleList.get(i);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dbManager.updateDev(model.getId(), NormalConstant.TYPE_GLASS);
                        SerialPortManager.instance().sendCommand(Command.queryBucket(NormalConstant.TYPE_GLASS));
                    }
                }, queryTime);
            }
        }

        if (paperList != null) {
            for (int i = 0; i < paperList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                final TerminalModel model = paperList.get(i);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dbManager.updateDev(model.getId(), ByteUtil.decimal2fitHex(Integer.valueOf("1" + k)));
                        SerialPortManager.instance().sendCommand(Command.queryBucket(ByteUtil.decimal2fitHex(Integer.valueOf("1" + k))));
                    }
                }, queryTime);
            }
        }

        if (spinList != null) {
            for (int i = 0; i < spinList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                final TerminalModel model = spinList.get(i);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dbManager.updateDev(model.getId(), ByteUtil.decimal2fitHex(Integer.valueOf("2" + k)));
                        SerialPortManager.instance().sendCommand(Command.queryBucket(ByteUtil.decimal2fitHex(Integer.valueOf("2" + k))));
                    }
                }, queryTime);
            }
        }

        if (metalList != null) {
            for (int i = 0; i < metalList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                final TerminalModel model = metalList.get(i);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dbManager.updateDev(model.getId(), ByteUtil.decimal2fitHex(Integer.valueOf("3" + k)));
                        SerialPortManager.instance().sendCommand(Command.queryBucket(ByteUtil.decimal2fitHex(Integer.valueOf("3" + k))));
                    }
                }, queryTime);
            }
        }

        if (plasticList != null) {
            for (int i = 0; i < plasticList.size(); i++) {
                queryTime = queryTime + time;
                final TerminalModel model = plasticList.get(i);
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dbManager.updateDev(model.getId(), ByteUtil.decimal2fitHex(Integer.valueOf("4" + k)));
                        SerialPortManager.instance().sendCommand(Command.queryBucket(ByteUtil.decimal2fitHex(Integer.valueOf("4" + k))));
                    }
                }, queryTime);
            }
        }
    }

    // 发送电表
    public void sendClock() {
        SerialPortManagerNew.instance().sendCommand(NormalConstant.GGT_METER_READING);
    }

    // 电表数据清零
    public void clearClock() {
        SerialPortManagerNew.instance().sendCommand(NormalConstant.CLEAR_METER_READING);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendClock();
            }
        }, time);
    }

    // 执行开门命令
    public void doOpenSchedule() {
        if (plasticBottleList != null) {
            for (int i = 0; i < plasticBottleList.size(); i++) {
                queryTime = queryTime + time;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("开门测试中（塑料瓶）");
                        }
                        SerialPortManager.instance().sendCommand(Command.recoveryPlasticCmd(NormalConstant.DOOR_DOING_OPEN));
                    }
                }, queryTime);
            }
        }

        if (glassBottleList != null) {
            for (int i = 0; i < glassBottleList.size(); i++) {
                queryTime = queryTime + time;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("开门测试中（玻璃瓶）");
                        }
                        SerialPortManager.instance().sendCommand(Command.recoveryGlassCmd(NormalConstant.DOOR_DOING_OPEN));
                    }
                }, queryTime);
            }
        }

        if (paperList != null) {
            for (int i = 0; i < paperList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("开门测试中（纸张）");
                        }
                        SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(ByteUtil.decimal2fitHex(Integer.valueOf("1" + k)), NormalConstant.DOOR_DOING_OPEN));
                    }
                }, queryTime);
            }
        }

        if (spinList != null) {
            for (int i = 0; i < spinList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("开门测试中（织物）");
                        }
                        SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(ByteUtil.decimal2fitHex(Integer.valueOf("2" + k)), NormalConstant.DOOR_DOING_OPEN));
                    }
                }, queryTime);
            }
        }

        if (metalList != null) {
            for (int i = 0; i < metalList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("开门测试中（金属）");
                        }
                        SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(ByteUtil.decimal2fitHex(Integer.valueOf("3" + k)), NormalConstant.DOOR_DOING_OPEN));
                    }
                }, queryTime);
            }
        }

        if (plasticList != null) {
            for (int i = 0; i < plasticList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("开门测试中（塑料制品）");
                        }
                        SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(ByteUtil.decimal2fitHex(Integer.valueOf("4" + k)), NormalConstant.DOOR_DOING_OPEN));
                    }
                }, queryTime);
            }
        }

    }


    // 执行关门命令
    public void doCloseScheduleByOpen() {
        if (plasticBottleList != null) {
            for (int i = 0; i < plasticBottleList.size(); i++) {
                TerminalModel model = plasticBottleList.get(i);
                BucketStatusModel bucketStatusModel = null;
                if (model != null) {
                    if (Utils.isEmpty(model.getID_D())) {
                        dbManager.updateDev(model.getId(), NormalConstant.TYPE_PLASTIC);
                        model.setID_D(NormalConstant.TYPE_PLASTIC);
                    }
                    bucketStatusModel = dbManager.getModel(model.getID_D());
                }

                if (bucketStatusModel != null) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SerialPortManager.instance().sendCommand(Command.recoveryPlasticCmd(NormalConstant.DOOR_DOING_CLOSE));
                        }
                    }, queryTime);
                }
            }
        }

        if (glassBottleList != null) {
            for (int i = 0; i < glassBottleList.size(); i++) {
                final TerminalModel model = glassBottleList.get(i);
                BucketStatusModel bucketStatusModel = null;
                if (model != null) {
                    if (Utils.isEmpty(model.getID_D())) {
                        dbManager.updateDev(model.getId(), NormalConstant.TYPE_GLASS);
                        model.setID_D(NormalConstant.TYPE_GLASS);
                    }
                    bucketStatusModel = dbManager.getModel(model.getID_D());
                }

                if (bucketStatusModel != null) {
                    queryTime = queryTime + time;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SerialPortManager.instance().sendCommand(Command.recoveryGlassCmd(NormalConstant.DOOR_DOING_CLOSE));
                        }
                    }, queryTime);
                }
            }
        }

        if (paperList != null) {
            for (int i = 0; i < paperList.size(); i++) {
                final int k = i;
                final TerminalModel model = paperList.get(i);
                BucketStatusModel bucketStatusModel = null;
                if (model != null) {
                    if (Utils.isEmpty(model.getID_D())) {
                        dbManager.updateDev(model.getId(), ByteUtil.decimal2fitHex(Integer.valueOf("1" + k)));
                        model.setID_D(ByteUtil.decimal2fitHex(Integer.valueOf("1" + k)));
                    }
                    bucketStatusModel = dbManager.getModel(model.getID_D());
                }

                if (bucketStatusModel != null) {
                    queryTime = queryTime + time;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(ByteUtil.decimal2fitHex(Integer.valueOf("1" + k)), NormalConstant.DOOR_DOING_CLOSE));
                        }
                    }, queryTime);
                }
            }
        }

        if (spinList != null) {
            for (int i = 0; i < spinList.size(); i++) {
                final int k = i;
                TerminalModel model = spinList.get(i);
                BucketStatusModel bucketStatusModel = null;
                if (model != null) {
                    if (Utils.isEmpty(model.getID_D())) {
                        dbManager.updateDev(model.getId(), ByteUtil.decimal2fitHex(Integer.valueOf("2" + k)));
                        model.setID_D(ByteUtil.decimal2fitHex(Integer.valueOf("2" + k)));
                    }
                    bucketStatusModel = dbManager.getModel(model.getID_D());
                }

                if (bucketStatusModel != null) {
                    queryTime = queryTime + time;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(ByteUtil.decimal2fitHex(Integer.valueOf("2" + k)), NormalConstant.DOOR_DOING_CLOSE));
                        }
                    }, queryTime);
                }
            }
        }

        if (metalList != null) {
            for (int i = 0; i < metalList.size(); i++) {
                final int k = i;
                TerminalModel model = metalList.get(i);
                BucketStatusModel bucketStatusModel = null;
                if (model != null) {
                    if (Utils.isEmpty(model.getID_D())) {
                        dbManager.updateDev(model.getId(), ByteUtil.decimal2fitHex(Integer.valueOf("3" + k)));
                        model.setID_D(ByteUtil.decimal2fitHex(Integer.valueOf("3" + k)));
                    }
                    bucketStatusModel = dbManager.getModel(model.getID_D());
                }

                if (bucketStatusModel != null) {
                    queryTime = queryTime + time;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(ByteUtil.decimal2fitHex(Integer.valueOf("3" + k)), NormalConstant.DOOR_DOING_CLOSE));
                        }
                    }, queryTime);
                }
            }
        }

        if (plasticList != null) {
            for (int i = 0; i < plasticList.size(); i++) {
                final int k = i;
                TerminalModel model = plasticList.get(i);
                BucketStatusModel bucketStatusModel = null;
                if (model != null) {
                    if (Utils.isEmpty(model.getID_D())) {
                        dbManager.updateDev(model.getId(), ByteUtil.decimal2fitHex(Integer.valueOf("4" + k)));
                        model.setID_D(ByteUtil.decimal2fitHex(Integer.valueOf("4" + k)));
                    }
                    bucketStatusModel = dbManager.getModel(model.getID_D());
                }
                if (bucketStatusModel != null) {
                    queryTime = queryTime + time;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(ByteUtil.decimal2fitHex(Integer.valueOf("4" + k)), NormalConstant.DOOR_DOING_CLOSE));
                        }
                    }, queryTime);
                }
            }
        }
    }

    // 执行关门命令
    public void doCloseSchedule() {
        if (plasticBottleList != null) {
            for (int i = 0; i < plasticBottleList.size(); i++) {
                queryTime = queryTime + time;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post("关门测试中（塑料瓶）");
                        SerialPortManager.instance().sendCommand(Command.recoveryPlasticCmd(NormalConstant.DOOR_DOING_CLOSE));
                    }
                }, queryTime);
            }
        }

        if (glassBottleList != null) {
            for (int i = 0; i < glassBottleList.size(); i++) {
                queryTime = queryTime + time;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post("关门测试中（玻璃瓶）");
                        SerialPortManager.instance().sendCommand(Command.recoveryGlassCmd(NormalConstant.DOOR_DOING_CLOSE));
                    }
                }, queryTime);
            }
        }

        if (paperList != null) {
            for (int i = 0; i < paperList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post("关门测试中（纸张）");
                        SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(ByteUtil.decimal2fitHex(Integer.valueOf("1" + k)), NormalConstant.DOOR_DOING_CLOSE));
                    }
                }, queryTime);
            }
        }

        if (spinList != null) {
            for (int i = 0; i < spinList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post("关门测试中（织物）");
                        SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(ByteUtil.decimal2fitHex(Integer.valueOf("2" + k)), NormalConstant.DOOR_DOING_CLOSE));
                    }
                }, queryTime);
            }
        }

        if (metalList != null) {
            for (int i = 0; i < metalList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post("关门测试中（金属）");
                        SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(ByteUtil.decimal2fitHex(Integer.valueOf("3" + k)), NormalConstant.DOOR_DOING_CLOSE));
                    }
                }, queryTime);
            }
        }

        if (plasticList != null) {
            for (int i = 0; i < plasticList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post("关门测试中（塑料制品）");
                        SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(ByteUtil.decimal2fitHex(Integer.valueOf("4" + k)), NormalConstant.DOOR_DOING_CLOSE));
                    }
                }, queryTime);
            }
        }

    }

    // 执行开启或者关闭风扇（根据当前温度来定,参数：limitTemperature）
    public void doIfOpenFan() {

        if (plasticBottleList != null) {
            for (int i = 0; i < plasticBottleList.size(); i++) {
                final int k = i;
                queryTime = queryTime + time;
                try {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TerminalModel model = plasticBottleList.get(k);
                            BucketStatusModel bucketStatusModel = null;
                            if (model != null) {
                                bucketStatusModel = dbManager.getModel(model.getID_D());
                            }
                            int temperature = 0, fanStatus = 0;
                            if (bucketStatusModel != null) {
                                temperature = bucketStatusModel.getCOLUMN_WEIGHT_STA();
                                fanStatus = bucketStatusModel.getCOLUMN_FAN_STA();

                            }
                            if (temperature > limitTemperature) {
                                if (fanStatus == 0) {
                                    // 开风扇
                                    SerialPortManager.instance().sendCommand(Command.openFun(NormalConstant.TYPE_PLASTIC));
                                }
                            } else {
                                if (fanStatus == 1) {
                                    // 关风扇
                                    SerialPortManager.instance().sendCommand(Command.closeFun(NormalConstant.TYPE_PLASTIC));
                                }
                            }
                        }
                    }, queryTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (paperList != null) {
            for (int i = 0; i < paperList.size(); i++) {
                final int k = i;
                queryTime = queryTime + time;
                try {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TerminalModel model = paperList.get(k);
                            BucketStatusModel bucketStatusModel = null;
                            if (model != null) {
                                bucketStatusModel = dbManager.getModel(model.getID_D());
                            }
                            int temperature = 0, fanStatus = 0;
                            if (bucketStatusModel != null) {
                                temperature = bucketStatusModel.getCOLUMN_WEIGHT_STA();
                                fanStatus = bucketStatusModel.getCOLUMN_FAN_STA();

                            }
                            if (temperature > limitTemperature) {
                                if (fanStatus == 0) {
                                    // 开风扇
                                    SerialPortManager.instance().sendCommand(Command.openFun(ByteUtil.decimal2fitHex(Integer.valueOf("1" + k))));
                                }
                            } else {
                                if (fanStatus == 1) {
                                    // 关风扇
                                    SerialPortManager.instance().sendCommand(Command.closeFun(ByteUtil.decimal2fitHex(Integer.valueOf("1" + k))));
                                }
                            }
                        }
                    }, queryTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (spinList != null) {
            for (int i = 0; i < spinList.size(); i++) {
                final int k = i;
                queryTime = queryTime + time;
                try {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TerminalModel model = spinList.get(k);
                            BucketStatusModel bucketStatusModel = null;
                            if (model != null) {
                                bucketStatusModel = dbManager.getModel(model.getID_D());
                            }
                            int temperature = 0, fanStatus = 0;
                            if (bucketStatusModel != null) {
                                temperature = bucketStatusModel.getCOLUMN_WEIGHT_STA();
                                fanStatus = bucketStatusModel.getCOLUMN_FAN_STA();

                            }
                            if (temperature > limitTemperature) {
                                if (fanStatus == 0) {
                                    // 开风扇
                                    SerialPortManager.instance().sendCommand(Command.openFun(ByteUtil.decimal2fitHex(Integer.valueOf("2" + k))));
                                }
                            } else {
                                if (fanStatus == 1) {
                                    // 关风扇
                                    SerialPortManager.instance().sendCommand(Command.closeFun(ByteUtil.decimal2fitHex(Integer.valueOf("2" + k))));
                                }
                            }
                        }
                    }, queryTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (metalList != null) {
            for (int i = 0; i < metalList.size(); i++) {
                final int k = i;
                queryTime = queryTime + time;
                try {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TerminalModel model = metalList.get(k);
                            BucketStatusModel bucketStatusModel = null;
                            if (model != null) {
                                bucketStatusModel = dbManager.getModel(model.getID_D());
                            }
                            int temperature = 0, fanStatus = 0;
                            if (bucketStatusModel != null) {
                                temperature = bucketStatusModel.getCOLUMN_WEIGHT_STA();
                                fanStatus = bucketStatusModel.getCOLUMN_FAN_STA();

                            }
                            if (temperature > limitTemperature) {
                                if (fanStatus == 0) {
                                    // 开风扇
                                    SerialPortManager.instance().sendCommand(Command.openFun(ByteUtil.decimal2fitHex(Integer.valueOf("3" + k))));
                                }
                            } else {
                                if (fanStatus == 1) {
                                    // 关风扇
                                    SerialPortManager.instance().sendCommand(Command.closeFun(ByteUtil.decimal2fitHex(Integer.valueOf("3" + k))));
                                }
                            }
                        }
                    }, queryTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (plasticList != null) {
            for (int i = 0; i < plasticList.size(); i++) {
                final int k = i;
                queryTime = queryTime + time;
                try {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TerminalModel model = plasticList.get(k);
                            BucketStatusModel bucketStatusModel = null;
                            if (model != null) {
                                bucketStatusModel = dbManager.getModel(model.getID_D());
                            }
                            int temperature = 0, fanStatus = 0;
                            if (bucketStatusModel != null) {
                                temperature = bucketStatusModel.getCOLUMN_WEIGHT_STA();
                                fanStatus = bucketStatusModel.getCOLUMN_FAN_STA();

                            }
                            if (temperature > limitTemperature) {
                                if (fanStatus == 0) {
                                    // 开风扇
                                    SerialPortManager.instance().sendCommand(Command.openFun(ByteUtil.decimal2fitHex(Integer.valueOf("4" + k))));
                                }
                            } else {
                                if (fanStatus == 1) {
                                    // 关风扇
                                    SerialPortManager.instance().sendCommand(Command.closeFun(ByteUtil.decimal2fitHex(Integer.valueOf("4" + k))));
                                }
                            }
                        }
                    }, queryTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 执行开启杀菌
    public void doOpenShaJun() {

        if (plasticBottleList != null) {
            for (int i = 0; i < plasticBottleList.size(); i++) {
                final int k = i;
                try {
                    TerminalModel model = plasticList.get(k);
                    BucketStatusModel bucketStatusModel = null;
                    if (model != null) {
                        bucketStatusModel = dbManager.getModel(model.getID_D());
                    }
                    int shajunStatus = 0;
                    if (bucketStatusModel != null) {
                        shajunStatus = bucketStatusModel.getCOLUMN_DEGERM_STA();
                    }
                    if (shajunStatus == 0) {
                        queryTime = queryTime + time;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if ("factory".equals(channelInfo)) {
                                    EventBus.getDefault().post("杀菌开启测试中");
                                }
                                SerialPortManager.instance().sendCommand(Command.openShajun(NormalConstant.TYPE_PLASTIC));
                            }
                        }, queryTime);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 执行关闭杀菌
    public void doCloseShaJun() {
        if (plasticBottleList != null) {
            for (int i = 0; i < plasticBottleList.size(); i++) {
                final int k = i;
                try {
                    TerminalModel model = plasticList.get(k);
                    BucketStatusModel bucketStatusModel = null;
                    if (model != null) {
                        bucketStatusModel = dbManager.getModel(model.getID_D());
                    }
                    int shajunStatus = 0;
                    if (bucketStatusModel != null) {
                        shajunStatus = bucketStatusModel.getCOLUMN_DEGERM_STA();
                    }
                    if (shajunStatus == 1) {
                        queryTime = queryTime + time;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if ("factory".equals(channelInfo)) {
                                    EventBus.getDefault().post("杀菌关闭测试中");
                                }
                                SerialPortManager.instance().sendCommand(Command.closeShajun(NormalConstant.TYPE_PLASTIC));
                            }
                        }, queryTime);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // 执行开启杀菌
    public void doOpenShaJunByTest() {
        if (plasticBottleList != null) {
            for (int i = 0; i < plasticBottleList.size(); i++) {
                queryTime = queryTime + time;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post("杀菌开启测试中");
                        SerialPortManager.instance().sendCommand(Command.openShajun(NormalConstant.TYPE_PLASTIC));
                    }
                }, queryTime);
            }
        }
    }

    // 执行关闭杀菌
    public void doCloseShaJunByTest() {
        if (plasticBottleList != null) {
            for (int i = 0; i < plasticBottleList.size(); i++) {
                queryTime = queryTime + time;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post("杀菌关闭测试中");
                        SerialPortManager.instance().sendCommand(Command.closeShajun(NormalConstant.TYPE_PLASTIC));
                    }
                }, queryTime);
            }
        }

    }


    // 执行开启灯箱
    public void doOpenLamp() {
        if (plasticBottleList != null) {
            for (int i = 0; i < plasticBottleList.size(); i++) {
                final int k = i;
                queryTime = queryTime + time;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("灯箱开启测试中（主机）");
                        }
                        SerialPortManager.instance().sendCommand(Command.openLampBox(NormalConstant.TYPE_PLASTIC));
                    }
                }, queryTime);
            }
        }

        if (paperList != null) {
            for (int i = 0; i < paperList.size(); i++) {
                final int k = i;
                queryTime = queryTime + time;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("灯箱开启测试中（纸张）");
                        }
                        SerialPortManager.instance().sendCommand(Command.openLampBox(ByteUtil.decimal2fitHex(Integer.valueOf("1" + k))));
                    }
                }, queryTime);
            }
        }

        if (spinList != null) {
            for (int i = 0; i < spinList.size(); i++) {
                final int k = i;
                queryTime = queryTime + time;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("灯箱开启测试中（织物）");
                        }
                        SerialPortManager.instance().sendCommand(Command.openLampBox(ByteUtil.decimal2fitHex(Integer.valueOf("2" + k))));
                    }
                }, queryTime);
            }
        }

        if (metalList != null) {
            for (int i = 0; i < metalList.size(); i++) {
                final int k = i;
                queryTime = queryTime + time;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("灯箱开启测试中（金属）");
                        }
                        SerialPortManager.instance().sendCommand(Command.openLampBox(ByteUtil.decimal2fitHex(Integer.valueOf("3" + k))));
                    }
                }, queryTime);
            }
        }

        if (plasticList != null) {
            for (int i = 0; i < plasticList.size(); i++) {
                final int k = i;
                queryTime = queryTime + time;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("灯箱开启测试中（塑料制品）");
                        }
                        SerialPortManager.instance().sendCommand(Command.openLampBox(ByteUtil.decimal2fitHex(Integer.valueOf("4" + k))));
                    }
                }, queryTime);
            }
        }

    }

    // 执行关闭灯箱
    public void doCloseLamp() {
        if (plasticBottleList != null) {
            for (int i = 0; i < plasticBottleList.size(); i++) {
                queryTime = queryTime + time;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("灯箱关闭测试中（主机）");
                        }
                        SerialPortManager.instance().sendCommand(Command.closeLampBox(NormalConstant.TYPE_PLASTIC));
                    }
                }, queryTime);
            }
        }

        if (paperList != null) {
            for (int i = 0; i < paperList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("灯箱关闭测试中（纸张）");
                        }
                        SerialPortManager.instance().sendCommand(Command.closeLampBox(ByteUtil.decimal2fitHex(Integer.valueOf("1" + k))));
                    }
                }, queryTime);
            }
        }

        if (spinList != null) {
            for (int i = 0; i < spinList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("灯箱关闭测试中（织物）");
                        }
                        SerialPortManager.instance().sendCommand(Command.closeLampBox(ByteUtil.decimal2fitHex(Integer.valueOf("2" + k))));
                    }
                }, queryTime);
            }
        }

        if (metalList != null) {
            for (int i = 0; i < metalList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("灯箱关闭测试中（金属）");
                        }
                        SerialPortManager.instance().sendCommand(Command.closeLampBox(ByteUtil.decimal2fitHex(Integer.valueOf("3" + k))));
                    }
                }, queryTime);
            }
        }

        if (plasticList != null) {
            for (int i = 0; i < plasticList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("灯箱关闭测试中（塑料制品）");
                        }
                        SerialPortManager.instance().sendCommand(Command.closeLampBox(ByteUtil.decimal2fitHex(Integer.valueOf("4" + k))));
                    }
                }, queryTime);
            }
        }
    }

    // 执行设备信息查询
    public void doQueryVersionInfo() {
        if (plasticBottleList != null) {
            for (int i = 0; i < plasticBottleList.size(); i++) {
                queryTime = queryTime + time;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SerialPortManager.instance().sendCommand(Command.queryVersionInfo(NormalConstant.TYPE_PLASTIC));
                    }
                }, queryTime);
            }
        }

        if (glassBottleList != null) {
            for (int i = 0; i < glassBottleList.size(); i++) {
                queryTime = queryTime + time;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SerialPortManager.instance().sendCommand(Command.queryVersionInfo(NormalConstant.TYPE_GLASS));
                    }
                }, queryTime);
            }
        }

        if (paperList != null) {
            for (int i = 0; i < paperList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SerialPortManager.instance().sendCommand(Command.queryVersionInfo(ByteUtil.decimal2fitHex(Integer.valueOf("1" + k))));
                    }
                }, queryTime);
            }
        }

        if (spinList != null) {
            for (int i = 0; i < spinList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SerialPortManager.instance().sendCommand(Command.queryVersionInfo(ByteUtil.decimal2fitHex(Integer.valueOf("2" + k))));
                    }
                }, queryTime);
            }
        }

        if (metalList != null) {
            for (int i = 0; i < metalList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SerialPortManager.instance().sendCommand(Command.queryVersionInfo(ByteUtil.decimal2fitHex(Integer.valueOf("3" + k))));
                    }
                }, queryTime);
            }
        }

        if (plasticList != null) {
            for (int i = 0; i < plasticList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SerialPortManager.instance().sendCommand(Command.queryVersionInfo(ByteUtil.decimal2fitHex(Integer.valueOf("4" + k))));
                    }
                }, queryTime);
            }
        }
    }

    // 执行开启风扇
    public void doOpenFan() {
        if (plasticBottleList != null) {
            for (int i = 0; i < plasticBottleList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("风扇开启测试中（主机）");
                        }
                        SerialPortManager.instance().sendCommand(Command.openFun(NormalConstant.TYPE_PLASTIC));
                    }
                }, queryTime);
            }
        }

        if (paperList != null) {
            for (int i = 0; i < paperList.size(); i++) {
                queryTime = queryTime + time * 3;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("风扇开启测试中（纸张）");
                        }
                        SerialPortManager.instance().sendCommand(Command.openFun(ByteUtil.decimal2fitHex(Integer.valueOf("1" + k))));
                    }
                }, queryTime);
            }
        }

        if (spinList != null) {
            for (int i = 0; i < spinList.size(); i++) {
                queryTime = queryTime + time * 3;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("风扇开启测试中（织物）");
                        }
                        SerialPortManager.instance().sendCommand(Command.openFun(ByteUtil.decimal2fitHex(Integer.valueOf("2" + k))));
                    }
                }, queryTime);
            }
        }

        if (metalList != null) {
            for (int i = 0; i < metalList.size(); i++) {
                queryTime = queryTime + time * 3;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("风扇开启测试中（金属）");
                        }
                        SerialPortManager.instance().sendCommand(Command.openFun(ByteUtil.decimal2fitHex(Integer.valueOf("3" + k))));
                    }
                }, queryTime);
            }
        }

        if (plasticList != null) {
            for (int i = 0; i < plasticList.size(); i++) {
                queryTime = queryTime + time * 3;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("风扇开启测试中（塑料制品）");
                        }
                        SerialPortManager.instance().sendCommand(Command.openFun(ByteUtil.decimal2fitHex(Integer.valueOf("4" + k))));
                    }
                }, queryTime);
            }
        }
    }

    // 执行关闭风扇
    public void doCloseFan() {
        if (plasticBottleList != null) {
            for (int i = 0; i < plasticBottleList.size(); i++) {
                queryTime = queryTime + time;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("风扇关闭测试中（主机）");
                        }
                        SerialPortManager.instance().sendCommand(Command.closeFun(NormalConstant.TYPE_PLASTIC));
                    }
                }, queryTime);
            }
        }

        if (paperList != null) {
            for (int i = 0; i < paperList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("风扇关闭测试中（纸张）");
                        }
                        SerialPortManager.instance().sendCommand(Command.closeFun(ByteUtil.decimal2fitHex(Integer.valueOf("1" + k))));
                    }
                }, queryTime);
            }
        }

        if (spinList != null) {
            for (int i = 0; i < spinList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("风扇关闭测试中（织物）");
                        }
                        SerialPortManager.instance().sendCommand(Command.closeFun(ByteUtil.decimal2fitHex(Integer.valueOf("2" + k))));
                    }
                }, queryTime);
            }
        }

        if (metalList != null) {
            for (int i = 0; i < metalList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("风扇关闭测试中（金属）");
                        }
                        SerialPortManager.instance().sendCommand(Command.closeFun(ByteUtil.decimal2fitHex(Integer.valueOf("3" + k))));
                    }
                }, queryTime);
            }
        }

        if (plasticList != null) {
            for (int i = 0; i < plasticList.size(); i++) {
                queryTime = queryTime + time;
                final int k = i;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ("factory".equals(channelInfo)) {
                            EventBus.getDefault().post("风扇关闭测试中（塑料制品）");
                        }
                        SerialPortManager.instance().sendCommand(Command.closeFun(ByteUtil.decimal2fitHex(Integer.valueOf("4" + k))));
                    }
                }, queryTime);
            }
        }
    }

}
