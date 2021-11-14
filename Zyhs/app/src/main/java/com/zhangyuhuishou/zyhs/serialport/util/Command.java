package com.zhangyuhuishou.zyhs.serialport.util;

import android.util.Log;

import com.tlh.android.config.Constant;
import com.tlh.android.utils.ApkUtils;
import com.zhangke.zlog.ZLog;
import com.zhangyuhuishou.zyhs.base.BaseApplication;
import com.zhangyuhuishou.zyhs.database.BucketStatusModel;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.DeliveryTipModel;
import com.zhangyuhuishou.zyhs.model.PcbExceptionModel;
import com.zhangyuhuishou.zyhs.model.TerminalModel;
import com.zhangyuhuishou.zyhs.serialport.constant.NormalConstant;
import com.zhangyuhuishou.zyhs.serialport.message.ClearThingModel;
import com.zhangyuhuishou.zyhs.serialport.message.CloseDoorModel;
import com.zhangyuhuishou.zyhs.serialport.message.ThingModel;

import org.greenrobot.eventbus.EventBus;

import java.math.BigInteger;
import java.util.List;

/**
 * 作者:created by author:tlh
 * 日期:2018/11/30 15:20
 * 邮箱:tianlihui2234@live.com
 * 描述:命令工具类
 * 规则：0x01 塑料回收桶地址
 * 0x02 玻璃瓶回收桶地址
 * 0x10 0x11 … 纸张回收桶地址（高位1代表纸柜类型 低位代表纸柜序号）
 * 0x20 0x21 … 织物回收桶地址（高位2代表织物类型 低位代表织物序号）
 * 0x30 0x31 … 金属回收桶地址（高位3代表金属类型 低位代表金属序号）
 * 0x40 0x41 … 塑料制品回收桶地址（高位4代表塑料制品类型 低位代表塑料制品序号）
 * 0x50 0x51 … 有害物质回收桶地址（高位5代表有害物质类型 低位代表有害物质序号）
 */

public class Command {
    public static StringBuilder combineCmd(String type, int cmdType, int doorDoingStatus) {
        StringBuilder builder = new StringBuilder();
        builder.append(NormalConstant.COMMAND_HEAD);
        builder.append(type);
        builder.append(NormalConstant.COMMAND_HEAD_REVERSE);
        builder.append(NormalConstant.ID_S);
        builder.append(NormalConstant.DO_PACKAGE_LENGTH);
        builder.append(singleCmd(cmdType));
        switch (doorDoingStatus) {
            case 0:
                builder.append(NormalConstant.CLOSE_DOOR);
                break;
            case 1:
            case 2:
                builder.append(NormalConstant.OPEN_DOOR);
                break;
            case 3:
                builder.append(NormalConstant.STOP_DOOR);
                break;
        }
        builder.append(ByteUtil.getCheckHex(builder.toString()));
        builder.append(NormalConstant.FINISH_CODE);
        return builder;
    }

    public static String singleCmd(int cmdType) {
        String singleCmd = "";
        switch (cmdType) {
            case NormalConstant.TYPE_QUERY_BUCKET:
                singleCmd = "30";
                break;
            case NormalConstant.TYPE_HS_PLASTIC:
                singleCmd = "01";
                break;
            case NormalConstant.TYPE_HS_OTHER:
                singleCmd = "03";
                break;
            case NormalConstant.TYPE_OPEN_DOOR:
                singleCmd = "21";
                break;
            case NormalConstant.TYPE_OPEN_FUN:
            case NormalConstant.TYPE_CLOSE_FUN:
                singleCmd = "23";
                break;
            case NormalConstant.TYPE_OPEN_SHAJUN:
            case NormalConstant.TYPE_CLOSE_SHAJUN:
                singleCmd = "27";
                break;
            case NormalConstant.TYPE_OPEN_LAMPBOX:
            case NormalConstant.TYPE_CLOSE_LAMPBOX:
                singleCmd = "29";
                break;
            case NormalConstant.TYPE_QUERY_VERSION:
                singleCmd = "3E";
                break;
            case NormalConstant.TYPE_HS_BELT:
                singleCmd = "61";
                break;
        }
        return singleCmd;
    }

    // 回收数据处理
    public static void dataCallBack(String callBackMessage) {
        if (callBackMessage.startsWith("FE")) {
            String cmd = callBackMessage.substring(10, 12);
            if ("02".equals(cmd)) {// 塑料及玻璃瓶桶上传指令
                recoveryPlasticCallBack(callBackMessage);
                return;
            }

            if ("04".equals(cmd)) {// 纸张/金属/有害物质/塑料制品/布料上传指令
                recoveryOtherCallBack(callBackMessage);
                return;
            }

            if ("31".equals(cmd)) {// 回收桶上传状态
                queryBucketCallBack(callBackMessage);
                return;
            }

            if ("22".equals(cmd)) {// 维护人员打开面门反馈处理
                doorCallBack(callBackMessage);
                return;
            }

            if ("3F".equals(cmd)) {// 主板版本信息
                queryVersionInfoCallback(callBackMessage);
                return;
            }
            // 回收桶告警信息 回收桶门告警 称重告警指令 面门告警指令 风扇告警指令 杀菌告警指令
            if ("40".equals(cmd) || "41".equals(cmd) || "42".equals(cmd) || "43".equals(cmd) || "44".equals(cmd) || "45".equals(cmd)) {
                alarmInfoCallBack(callBackMessage);
            }
        }
    }

    // 回收塑料瓶
    public static String recoveryPlasticCmd(int doorDoingStatus) {
        return Command.combineCmd(NormalConstant.TYPE_PLASTIC, NormalConstant.TYPE_HS_PLASTIC, doorDoingStatus).toString();
    }

    // 回收玻璃瓶
    public static String recoveryGlassCmd(int doorDoingStatus) {
        return Command.combineCmd(NormalConstant.TYPE_GLASS, NormalConstant.TYPE_HS_PLASTIC, doorDoingStatus).toString();
    }

    // 开启塑料瓶传送带
    public static String recoveryBeltPlasticCmd(int doorDoingStatus) {
        return Command.combineCmd(NormalConstant.TYPE_PLASTIC, NormalConstant.TYPE_HS_BELT, doorDoingStatus).toString();
    }

    // 开启玻璃瓶传送带
    public static String recoveryBeltGlassCmd(int doorDoingStatus) {
        return Command.combineCmd(NormalConstant.TYPE_GLASS, NormalConstant.TYPE_HS_BELT, doorDoingStatus).toString();
    }

    // 回收塑料瓶或者玻璃瓶反馈
    public static void recoveryPlasticCallBack(String callBackMessage) {

        System.out.println("回收塑料瓶或者玻璃瓶反馈:" + callBackMessage);

        if (callBackMessage.length() < 32) {
            return;
        }
        if (callBackMessage.startsWith("FE")) {
            String checkCode = callBackMessage.substring(28, 30);
            if (!ByteUtil.getCheckHex(checkCode).equals(ByteUtil.getCheckHex(callBackMessage.substring(0, 28)))) {
                // 校验码不正确
                return;
            }
            String ID_S = callBackMessage.substring(6, 8);// 桶的类别
            String terminalId = getTerminalId(callBackMessage.substring(6, 8));// 设备id
            String isOk = callBackMessage.substring(12, 14);// 本次动作响应状态
            String isBottle = callBackMessage.substring(14, 16);// 瓶子状态
            String isTimeOut = callBackMessage.substring(16, 18);// 超时状态
            String Doorsta = callBackMessage.substring(26, 28);// 门的状态

            CloseDoorModel model = null;
            switch (Doorsta) {
                //  回收门（关闭）
                case "00":
                    model = new CloseDoorModel(terminalId, 0, "回收柜门（关闭）");
                    break;
                // 回收门（打开）
                case "01":
                    model = new CloseDoorModel(terminalId, 1, "回收柜门（打开）");
                    break;
                // 回收门（夹住）
                case "02":
                    model = new CloseDoorModel(terminalId, 2, "回收柜门（夹手）");
                    break;
                //  回收门（开门超时）
                case "03":
                    model = new CloseDoorModel(terminalId, 3, "回收柜门（打开超时）");
                    break;
                //  回收门（关门超时）
                case "04":
                    model = new CloseDoorModel(terminalId, 4, "回收柜门（关闭超时）");
                    break;
                //  回收门（夹手开门）
                case "05":
                    model = new CloseDoorModel(terminalId, 5, "回收柜门（夹手开门）");
                    break;
            }

            if (model != null) {
                model.setSourceId(ID_S);
                EventBus.getDefault().post(model);
            }

//            // 更新回收桶状态（1代表正常，0代表满了）
//            BucketStatusModel bucketStatusModel = DBManager.getInstance(BaseApplication.getContext()).getModel(ID_S);
//            if (bucketStatusModel != null && bucketStatusModel.getCOLUMN_REC_BUCKET_STA() != ByteUtil.makeChecksum2(isTimeOut)) {
//                if (DBManager.getInstance(BaseApplication.getContext()).queryIsExist(ID_S)) {
//                    DBManager.getInstance(BaseApplication.getContext()).updateRecBucket(ID_S, ByteUtil.makeChecksum2(ID_S));
//                }
//            }

            // 回收门关闭不进行称重以及回收失败操作
            if ("00".equals(Doorsta)) {
                return;
            }

            if ("01".equals(isOk)) {
                // 回收成功计算数量
                String weightHex = callBackMessage.substring(18, 22);
                String numHex = callBackMessage.substring(22, 26);
                int weight = Integer.parseInt(weightHex, 16);
                int num = ByteUtil.makeChecksum(numHex);
                System.out.println("重量：" + weight + "数量：" + num);
                String channelInfo = ApkUtils.getAppMetaData(BaseApplication.getContext(), "UMENG_CHANNEL");
                if ("hippo".equals(channelInfo)) {
                    if (weight >= 0 && num >= 0) {
                        if (weight > 250 && weight < 295 && NormalConstant.TYPE_GLASS.equals(ID_S)) {
                            ThingModel thingModel = new ThingModel(ID_S, num);
                            thingModel.setShow(true);
                            EventBus.getDefault().post(thingModel);// 传递数据
                        } else {
                            EventBus.getDefault().post(new ThingModel(ID_S, num));// 传递数据
                        }
                    }
                } else {
                    if (num >= 0) {
                        EventBus.getDefault().post(new ThingModel(ID_S, num));// 传递数据
                    }
                }
                // 回收失败
            } else if ("00".equals(isOk)) {
                switch (isBottle) {
                    case "00":
                        // 没有东西
                        EventBus.getDefault().post(new DeliveryTipModel(NormalConstant.RECYCLE_TIP_NOTHING, terminalId));
                        break;
                    case "01":
                        // 重量合适
                        break;
                    case "02":
                        //  超重
                        EventBus.getDefault().post(new DeliveryTipModel(NormalConstant.RECYCLE_TIP_TOO_HEAVY, terminalId));
                        break;
                    case "03":
                        // 太轻
                        EventBus.getDefault().post(new DeliveryTipModel(NormalConstant.RECYCLE_TIP_TOO_LIGHT, terminalId));
                        break;
                    case "04":
                        // 计数异常
                        EventBus.getDefault().post(new DeliveryTipModel(NormalConstant.RECYCLE_TIP_COUNT_EXCEPTION, terminalId));
                        break;
                }
            }
        }
    }

    // 回收其它 type为分类
    public static String recoveryOtherCmd(String type, int doorDoingStatus) {
        return Command.combineCmd(type, NormalConstant.TYPE_HS_OTHER, doorDoingStatus).toString();
    }

    // 回收其它 type为分类（除塑料玻璃)反馈
    public static void recoveryOtherCallBack(String callBackMessage) {

        if (callBackMessage.length() < 32) {
            return;
        }

        if (callBackMessage.startsWith("FE")) {
            String checkCode = callBackMessage.substring(28, 30);
            if (!ByteUtil.getCheckHex(checkCode).equals(ByteUtil.getCheckHex(callBackMessage.substring(0, 28)))) {
                // 校验码不正确
                return;
            }
            String isOk = callBackMessage.substring(12, 14);// 本次动作响应状态
            String ID_S = callBackMessage.substring(6, 8);// 设置地址
            String terminalId = getTerminalId(callBackMessage.substring(6, 8));// 设备id
            String sourceId = callBackMessage.substring(6, 8);// 桶的类别（匹配后台接口分类）
            String isBottle = callBackMessage.substring(14, 16);// 瓶子状态
            String isTimeOut = callBackMessage.substring(16, 18);// 超时状态
            String Doorsta = callBackMessage.substring(26, 28);// 门的状态

            CloseDoorModel model = null;
            switch (Doorsta) {
                //  回收门（关闭）
                case "00":
                    model = new CloseDoorModel(terminalId, 0, "回收柜门（关闭）");
                    // 关门之后计算称重数据
//                    String total = callBackMessage.substring(18, 26);
//                    int num = ByteUtil.makeChecksum2(total);
                    int num = ByteUtil.hexChangeDec(callBackMessage);
                    System.out.println("十六进制称重数量：" + callBackMessage);
                    System.out.println("十进制称重数量：" + num);
                    EventBus.getDefault().post(new ThingModel(sourceId, num / 1000));// 传递数据
                    break;
                // 回收门（打开）
                case "01":
                    model = new CloseDoorModel(terminalId, 1, "回收柜门（打开）");
                    break;
                // 回收门（夹住）
                case "02":
                    model = new CloseDoorModel(terminalId, 2, "回收柜门（夹手）");
                    break;
                //  回收门（开门超时）
                case "03":
                    model = new CloseDoorModel(terminalId, 3, "回收柜门（打开超时）");
                    break;
                //  回收门（关闭超时）
                case "04":
                    model = new CloseDoorModel(terminalId, 4, "回收柜门（关闭超时）");
                    break;
                //  回收门（夹手开门）
                case "05":
                    model = new CloseDoorModel(terminalId, 5, "回收柜门（夹手开门）");
                    break;
            }

            if (model != null) {
                model.setSourceId(ID_S);
                EventBus.getDefault().post(model);
            }

//            // 更新回收桶状态（1代表正常，0代表满了）
//            BucketStatusModel bucketStatusModel = DBManager.getInstance(BaseApplication.getContext()).getModel(ID_S);
//            if (bucketStatusModel != null && bucketStatusModel.getCOLUMN_REC_BUCKET_STA() != ByteUtil.makeChecksum2(isTimeOut)) {
//                if (DBManager.getInstance(BaseApplication.getContext()).queryIsExist(ID_S)) {
//                    DBManager.getInstance(BaseApplication.getContext()).updateRecBucket(ID_S, ByteUtil.makeChecksum2(ID_S));
//                }
//            }

            if ("01".equals(isOk) && !("00".equals(Doorsta))) {
                // 实时重量数据
//                String total = callBackMessage.substring(18, 26);
                int num = ByteUtil.hexChangeDec(callBackMessage);
                EventBus.getDefault().post(new ThingModel(sourceId, num / 1000));// 传递数据
            } else if ("00".equals(isOk)) {
                // 回收失败
                if ("00".equals(isBottle)) {
                    // 没有称重
                    EventBus.getDefault().post(new DeliveryTipModel(NormalConstant.RECYCLE_TIP_NOTHING, terminalId));
                } else if ("01".equals(isBottle)) {
                    //  有称重
                    EventBus.getDefault().post(new DeliveryTipModel(NormalConstant.RECYCLE_TIP_HAS_WEIGHT, terminalId));
                }
            }

        }
    }

    // 查询回收桶状态
    public static String queryBucket(String type) {
        return Command.combineCmd(type, NormalConstant.TYPE_QUERY_BUCKET, NormalConstant.DOOR_DOING_CLOSE).toString();
    }

    // 查询回收桶状态反馈
    public static void queryBucketCallBack(String callBackMessage) {

        if (callBackMessage.length() < 34) {
            return;
        }
        if (callBackMessage.startsWith("FE")) {
            String checkCode = callBackMessage.substring(30, 32);
            if (!ByteUtil.getCheckHex(checkCode).equals(ByteUtil.getCheckHex(callBackMessage.substring(0, 30)))) {
                // 校验码不正确
                return;
            }
            String ID_S = callBackMessage.substring(6, 8);// 桶类别
            String Resp_sta = callBackMessage.substring(12, 14);// 本次动作响应状态
            String Weight_sta = callBackMessage.substring(14, 16);// 称重传感器状态(温度检测)
            String Rec_Bucket_sta = callBackMessage.substring(16, 18);// 回收桶状态
            String Rec_Gate_sta = callBackMessage.substring(18, 20);// 回收门状态
            String Main_Door_sta = callBackMessage.substring(20, 22);// 维护门状态
            String LED_Box_sta = callBackMessage.substring(22, 24);// 灯箱状态
            String FAN_sta = callBackMessage.substring(24, 26);// 风扇状态
            String DEGERM_sta = callBackMessage.substring(26, 28);// 杀菌状态
            String BUCKET_sta = callBackMessage.substring(28, 30);// 倒水桶状态
            String logTip = callBackMessage.substring(14, 30);// 日志信息（响应失败时有用）
            String terminalId = getTerminalId(ID_S);// 设备id
            if ("01".equals(Resp_sta)) {// 响应成功
                // 查询此桶存在
                int temperature = ByteUtil.makeChecksum2(Weight_sta);
                if (NormalConstant.TYPE_PLASTIC.equals(ID_S)) {
                    System.out.println("倒水桶重量:" + ByteUtil.makeChecksum2(BUCKET_sta));
                }

                if (DBManager.getInstance(BaseApplication.getContext()).queryIsExist(ID_S)) {
                    BucketStatusModel bucketStatusModel = DBManager.getInstance(BaseApplication.getContext()).getModel(ID_S);
                    String version = "1";

                    if (bucketStatusModel != null) {
                        version = bucketStatusModel.getVersion();
                    }
                    DBManager.getInstance(BaseApplication.getContext()).deleteById(ID_S);
                    DBManager.getInstance(BaseApplication.getContext()).insert(ID_S, ByteUtil.makeChecksum2(Resp_sta),
                            ByteUtil.makeChecksum2(Weight_sta), ByteUtil.makeChecksum2(Rec_Bucket_sta), ByteUtil.makeChecksum2(Rec_Gate_sta),
                            ByteUtil.makeChecksum2(Main_Door_sta), ByteUtil.makeChecksum2(LED_Box_sta), ByteUtil.makeChecksum2(FAN_sta),
                            ByteUtil.makeChecksum2(DEGERM_sta), ByteUtil.makeChecksum2(BUCKET_sta));
                    DBManager.getInstance(BaseApplication.getContext()).update_bucket_version(ID_S, version);

                } else {
                    DBManager.getInstance(BaseApplication.getContext()).insert(ID_S, ByteUtil.makeChecksum2(Resp_sta),
                            ByteUtil.makeChecksum2(Weight_sta), ByteUtil.makeChecksum2(Rec_Bucket_sta), ByteUtil.makeChecksum2(Rec_Gate_sta),
                            ByteUtil.makeChecksum2(Main_Door_sta), ByteUtil.makeChecksum2(LED_Box_sta), ByteUtil.makeChecksum2(FAN_sta),
                            ByteUtil.makeChecksum2(DEGERM_sta), ByteUtil.makeChecksum2(BUCKET_sta));
                }
                if ("00".equals(Rec_Gate_sta)) {
                    DBManager.getInstance(BaseApplication.getContext()).update_species_fault(ID_S, 0, ByteUtil.makeChecksum2(Rec_Gate_sta));
                } else {
                    DBManager.getInstance(BaseApplication.getContext()).update_species_fault(ID_S, 1, ByteUtil.makeChecksum2(Rec_Gate_sta));
                }

                System.out.println("设备：" + terminalId + "，温度：" + temperature);
            } else {
                // 响应失败
                System.out.println("错误的日志信息：" + logTip);
                ZLog.e("Command", "错误的日志信息：" + logTip);
                EventBus.getDefault().post(new PcbExceptionModel(terminalId, Constant.EXCEPTION_PCB_RESPONSE_FAILURE + ":" + logTip));
            }
        }
    }

    // 开启面门
    public static String openDoor(String type) {
        return Command.combineCmd(type, NormalConstant.TYPE_OPEN_DOOR, NormalConstant.DOOR_DOING_OTHER).toString();
    }

    // 面门反馈
    public static void doorCallBack(String callBackMessage) {

        if (callBackMessage.length() < 18) {
            return;
        }

        if (callBackMessage.startsWith("FE")) {
            String checkCode = callBackMessage.substring(14, 16);
            if (!ByteUtil.getCheckHex(checkCode).equals(ByteUtil.getCheckHex(callBackMessage.substring(0, 14)))) {
                // 校验码不正确
                return;
            }
            String type = callBackMessage.substring(6, 8);// 桶的类别
            String isOk = callBackMessage.substring(12, 14);// 相应状态
            if ("01".equals(isOk)) {
                // 面门开启
            } else if ("02".equals(isOk)) {

            } else if ("00".equals(isOk)) {
                // 面门关闭
                EventBus.getDefault().post(new ClearThingModel(type));
            }
        }
    }

    // 开启风扇
    public static String openFun(String type) {
        return Command.combineCmd(type, NormalConstant.TYPE_OPEN_FUN, NormalConstant.DOOR_DOING_OPEN).toString();
    }

    // 关闭风扇
    public static String closeFun(String type) {
        return Command.combineCmd(type, NormalConstant.TYPE_CLOSE_FUN, NormalConstant.DOOR_DOING_CLOSE).toString();
    }

    // 开启杀菌
    public static String openShajun(String type) {
        return Command.combineCmd(type, NormalConstant.TYPE_OPEN_SHAJUN, NormalConstant.DOOR_DOING_OTHER).toString();
    }

    // 关闭杀菌
    public static String closeShajun(String type) {
        return Command.combineCmd(type, NormalConstant.TYPE_CLOSE_SHAJUN, NormalConstant.DOOR_DOING_CLOSE).toString();
    }

    // 开启灯箱
    public static String openLampBox(String type) {
        return Command.combineCmd(type, NormalConstant.TYPE_OPEN_LAMPBOX, NormalConstant.DOOR_DOING_OTHER).toString();
    }

    // 关闭灯箱
    public static String closeLampBox(String type) {
        return Command.combineCmd(type, NormalConstant.TYPE_CLOSE_LAMPBOX, NormalConstant.DOOR_DOING_CLOSE).toString();
    }

    // 查询设备软硬件版本信息
    public static String queryVersionInfo(String type) {
        return Command.combineCmd(type, NormalConstant.TYPE_QUERY_VERSION, NormalConstant.DOOR_DOING_CLOSE).toString();
    }

    // 设备软硬件版本信息解析
    public static void queryVersionInfoCallback(String callBackMessage) {
        if (callBackMessage.length() < 20) {
            return;
        }

        if (callBackMessage.startsWith("FE")) {
            String checkCode = callBackMessage.substring(16, 18);
            if (!ByteUtil.getCheckHex(checkCode).equals(ByteUtil.getCheckHex(callBackMessage.substring(0, 16)))) {
                // 校验码不正确
                return;
            }
            String ID_S = callBackMessage.substring(6, 8);
            String type = getID_S(ID_S);// // 桶的类别
            int ver1 = ByteUtil.makeChecksum2(callBackMessage.substring(12, 14));// 设备软件版本
            int ver2 = ByteUtil.makeChecksum2(callBackMessage.substring(14, 16));// 设备硬件版本
            if (DBManager.getInstance(BaseApplication.getContext()).queryIsExist(ID_S)) {
                DBManager.getInstance(BaseApplication.getContext()).update_bucket_version(ID_S, ver1 + "");
            }

            System.out.println("回收桶信息：" + callBackMessage + "类型" + type + "：软件版本信息：" + ver1 + "硬件版本信息：" + ver2);
        }
    }

    // 告警信息反馈
    public static void alarmInfoCallBack(String callBackMessage) {

        if (callBackMessage.length() < 18) {
            return;
        }
        if (callBackMessage.startsWith("FE")) {
            String checkCode = callBackMessage.substring(14, 16);
            if (!ByteUtil.getCheckHex(checkCode).equals(ByteUtil.getCheckHex(callBackMessage.substring(0, 14)))) {
                // 校验码不正确
                return;
            }
            String ID_S = callBackMessage.substring(6, 8);
            String type = getID_S(ByteUtil.makeChecksum2(ID_S) + "");// // 桶的类别
            String cmd = callBackMessage.substring(10, 12);// 当前命令
            String ERR = callBackMessage.substring(12, 14);// 本次动作响应状态
            if ("40".equals(cmd)) {// 回收桶
                Log.i("分类类型", type);
                if ("01".equals(ERR)) {
                    Log.i("分类类型:", type + "桶满");
                } else if ("02".equals(ERR)) {
                    Log.i("分类类型:", type + "桶超重");
                }
                return;
            }

            if ("41".equals(cmd)) {// 回收桶门
                Log.i("分类类型", type);
                if ("01".equals(ERR)) {
                    Log.i("分类类型:", type + "桶门无法开启");
                } else if ("02".equals(ERR)) {
                    Log.i("分类类型:", type + "桶门无法关闭");
                }
                return;
            }

            if ("42".equals(cmd)) {// 称重
                Log.i("分类类型", type);
                if ("01".equals(ERR)) {
                    Log.i("分类类型:", type + "称重超量程");
                } else if ("02".equals(ERR)) {
                    Log.i("分类类型:", type + "称重不能工作");
                }
                return;
            }

            if ("43".equals(cmd)) {// 面门
                Log.i("分类类型", type);
                if ("01".equals(ERR)) {
                    Log.i("分类类型:", type + "面门无法打开");
                } else if ("02".equals(ERR)) {
                    Log.i("分类类型:", type + "面门无法关闭");
                }
                return;
            }

            if ("44".equals(cmd)) {// 风扇
                Log.i("分类类型", type);
                if ("01".equals(ERR)) {
                    Log.i("分类类型:", type + "风扇无法打开");
                } else if ("02".equals(ERR)) {
                    Log.i("分类类型:", type + "风扇无法关闭");
                }
                return;
            }

            if ("45".equals(cmd)) {// 杀菌
                Log.i("分类类型", type);
                if ("01".equals(ERR)) {
                    Log.i("分类类型:", type + "杀菌无法打开");
                } else if ("02".equals(ERR)) {
                    Log.i("分类类型:", type + "杀菌无法关闭");
                }
            }
        }
    }

    public static String getID_S(String type) {
        String ID_S = "";
        type = ByteUtil.makeChecksum2(type) + "";
        if (type.length() == 1) {
            type = "0" + type;
        }
        String devType = type.substring(0, 1);
        int devNum = Integer.valueOf(type.substring(1, 2));
        switch (Integer.valueOf(devType)) {
            case 0:
                if (devNum == 1) {
                    ID_S = "塑料桶";
                }
                if (devNum == 2) {
                    ID_S = "玻璃桶";
                }
                break;
            case 1:// 纸
                ID_S = (devNum + 1) + "号纸柜";
                break;
            case 2:// 织物
                ID_S = (devNum + 1) + "号织物柜";
                break;
            case 3:// 金属
                ID_S = (devNum + 1) + "号金属柜";
                break;
            case 4:// 塑料制品
                ID_S = (devNum + 1) + "号塑料制品柜";
                break;
            case 5:// 有害物质
                ID_S = (devNum + 1) + "号有害物质柜";
                break;
        }
        return ID_S;
    }


    public static String getTerminalId(String IDS) {
        IDS = ByteUtil.hexStr2decimal(IDS) + "";
        if (IDS.length() == 1) {
            IDS = "0" + IDS;
        }
        String terminalId = "";
        DBManager dbManager = DBManager.getInstance(BaseApplication.getContext());
        List<TerminalModel> plasticBottleList = dbManager.getDevListByType(Constant.DEV_PLASTIC_TYPE);
        List<TerminalModel> glassBottleList = dbManager.getDevListByType(Constant.DEV_GLASS_TYPE);
        List<TerminalModel> paperList = dbManager.getDevListByType(Constant.DEV_PAPER_TYPE);
        List<TerminalModel> spinList = dbManager.getDevListByType(Constant.DEV_SPIN_TYPE);
        List<TerminalModel> metalList = dbManager.getDevListByType(Constant.DEV_METAL_TYPE);
        List<TerminalModel> plasticList = dbManager.getDevListByType(Constant.DEV_CEMENT_TYPE);
        List<TerminalModel> harmfulList = dbManager.getDevListByType(Constant.DEV_POISON_TYPE);

        String devType = IDS.substring(0, 1);
        try {
            int devNum = Integer.valueOf(IDS.substring(1, 2));
            switch (Integer.valueOf(devType)) {
                case 0:// 塑料和玻璃
                    if (devNum == 1 && plasticBottleList.size() > 0) {
                        terminalId = plasticBottleList.get(0).getTerminalId();
                    }
                    if (devNum == 2 && glassBottleList.size() > 0) {
                        terminalId = glassBottleList.get(0).getTerminalId();
                    }
                    break;
                case 1:// 纸
                    if (paperList.size() > 0 && devNum < paperList.size()) {
                        terminalId = paperList.get(devNum).getTerminalId();
                    }
                    break;
                case 2:// 织物
                    if (spinList.size() > 0 && devNum < spinList.size()) {
                        terminalId = spinList.get(devNum).getTerminalId();
                    }
                    break;
                case 3:// 金属
                    if (metalList.size() > 0 && devNum < metalList.size()) {
                        terminalId = metalList.get(devNum).getTerminalId();
                    }
                    break;
                case 4:// 塑料制品
                    if (plasticList.size() > 0 && devNum < plasticList.size()) {
                        terminalId = plasticList.get(devNum).getTerminalId();
                    }
                    break;
                case 5:// 有害物质
                    if (harmfulList.size() > 0 && devNum < harmfulList.size()) {
                        terminalId = harmfulList.get(devNum).getTerminalId();
                    }
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return terminalId;
    }

}

