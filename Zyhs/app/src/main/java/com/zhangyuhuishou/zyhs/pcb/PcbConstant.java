package com.zhangyuhuishou.zyhs.pcb;

public class PcbConstant {

    public static final String ILLEGAL_FUNCTION = "01";// 非法功能
    public static final String ILLEGAL_DATA_ADDRESS = "02";// 非法数据地址
    public static final String ILLEGAL_DATA_VALUE = "03";// 非法数据值
    public static final String SLAVE_FAILURE = "04";// 从站设备故障
    public static final String CONFIRM = "05";// 确认
    public static final String SLAVE_DEVICE_BUSY = "07";// 从属设备忙
    public static final String STORAGE_PARITY_ERROR = "08";// 存储奇偶性差错
    public static final String UNAVAILABLE_GATEWAY_PATH = "0A";// 不可用网关路径
    public static final String GATEWAY_TARGET_DEVICE_RESPONSE_FAILED= "0B";// 网关目标设备响应失败

    public static String COMMON_REGISTER_ADDRESS = "0000";// 通用寄存器地址

    public static final String GGT_METER_READING = " F00340000002C4EA";// 电表命令
    public static final String CLEAR_METER_READING = " F010000200010200016FE6";// 清除电表数据

}
