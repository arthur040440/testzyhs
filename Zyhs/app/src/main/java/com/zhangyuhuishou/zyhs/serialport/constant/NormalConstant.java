package com.zhangyuhuishou.zyhs.serialport.constant;

public class NormalConstant {

    public static final String TYPE_PLASTIC = "01";// 塑料
    public static final String TYPE_GLASS = "02";// 玻璃


    public static final String COMMAND_HEAD = "FE";// 命令头
    public static final String COMMAND_HEAD_REVERSE = "01";// 命令头取反
    public static final String ID_S = "FF";//
    public static final String DO_PACKAGE_LENGTH = "09";// 包的长度（执行命令）
    public static final String CLOSE_DOOR = "00";// 关门
    public static final String OPEN_DOOR = "01";// 开门
    public static final String STOP_DOOR = "02";// 停止
    public static final String FINISH_CODE = "0D";// 结束字符

    public static final int DOOR_DOING_OPEN = 1;// 将要打开门
    public static final int DOOR_DOING_CLOSE = 0;// 将要关闭门
    public static final int DOOR_DOING_OTHER = 2;// 不处理
    public static final int DOOR_DOING_STOP = 3;// 停止

    public static final int TYPE_HS_PLASTIC = 1;// 回收塑料瓶或者玻璃瓶
    public static final int TYPE_HS_OTHER = 2;// 回收纸张，金属，塑料制品，布料，有害物质
    public static final int TYPE_OPEN_DOOR = 3;// 开启面门
    public static final int TYPE_OPEN_FUN = 4;// 开启风扇
    public static final int TYPE_CLOSE_FUN = 5;// 关闭风扇
    public static final int TYPE_OPEN_SHAJUN = 6;// 开启杀菌
    public static final int TYPE_CLOSE_SHAJUN = 7;// 关闭杀菌
    public static final int TYPE_QUERY_BUCKET = 8;// 查询回收桶状态
    public static final int TYPE_OPEN_LAMPBOX = 15;// 开启灯箱
    public static final int TYPE_CLOSE_LAMPBOX = 16;// 关闭灯箱
    public static final int TYPE_QUERY_VERSION = 17;// 查询设备信息
    public static final int TYPE_HS_BELT = 61;// 开启传送带


    public static final String RECYCLE_TIP_NOTHING = "0";// 回收提示（未放置物品）
    public static final String RECYCLE_TIP_TOO_HEAVY = "2";// 回收提示（太重）
    public static final String RECYCLE_TIP_TOO_LIGHT = "3";// 回收提示（太轻）
    public static final String RECYCLE_TIP_HAS_WEIGHT = "4";// 有重量
    public static final String RECYCLE_TIP_CLOSING = "5";// 回收提示（关门）
    public static final String RECYCLE_TIP_FULL = "6";// 回收提示（回收桶已满）
    public static final String INTERFACE_ACCESS_FAILURE = "7";// 接口访问失败
    public static final String INTERFACE_ACCESS_SUCCESS = "8";// 接口访问成功
    public static final String RECYCLE_TIP_COUNT_EXCEPTION = "9";// 回收提示（计数异常）
    public static final String ORDER_EXCEPTION = "10";// 订单异常
    public static final String STEAL_RECYCLE_EXCEPTION = "-1";// 盗取回收物异常处理


    public static final String GGT_METER_READING = "0104010000027037";// 电表命令
    public static final String MODIFY_METER_ = "0104010000027037";// 电表命令（修改电表地址）
    public static final String CLEAR_METER_READING = "00AA01CEA0";// 清除电表数据
}
