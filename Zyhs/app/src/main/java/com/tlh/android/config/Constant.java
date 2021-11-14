package com.tlh.android.config;

import android.os.Environment;

/**
 * @author tlh
 * @time 2017/12/19 11:22
 * @desc 基本配置文件
 */

public class Constant {

    public static final String PACKAGE_NAME_PREFERENCES = "PACKAGE_NAME_PREFERENCES";
    public static final String SERVER_IP_NEW = "https://www.zyhs.com/v1";
    //    public static final String SERVER_IP_NEW = "https://test.zyhs.com/v1";
    //    public static final String SERVER_IP_NEW = "http://192.168.1.239:7001";
    public static final String BASE_URL_NEW = SERVER_IP_NEW;// 域名基本地址
    public static final String BASE_URL_SOCKET = "https://www.zyhs.com";// Socket通讯地址
//    public static final String BASE_URL_SOCKET = "https://test.zyhs.com";// Socket通讯地址

    public static final String USER_INFO = "/outUser/outUser/getUserInfo";// 用户信息
    public static final String POINT_SEARCH_NEW = "/machine/range/init";// 点位搜索
    public static final String TERMINAL_ACTIVE_NEW = "/machine/range/active";// 设备激活
    public static final String CREATE_ORDER_NEW = "/outUser/normalOrder/add";// 创建订单
    public static final String GET_RECYCLER_PRICE = "/business/recoveryPrice/getList";// 获取回收物价格

    public static final String SEND_POLLING = "/machine/range/polling";// 发送心跳
    public static final String GET_VERSION = "/machine/machine/getVersion";// 检查版本信息
    public static final String SYSTEM_EXCEPTION = "/machine/rangeLog/add";// 故障上报
    public static final String YINTAI_COUPONS = "/business/yintaiCoupon/getCoupon";// 获取银泰优惠券
    public static final String ELEMENT_COUPONS = "/activity/activity/activityJoin";// 饿了么礼品兑换

    public static final String DYNAMIC_SKIN = "/business/skin/getSkin";// 动态换肤

    public static final String CREATE_USER = "/outUser/outUser/cerateUserByFace";// 通过旷视创建新用户
    public static final String LOGIN_BY_FACE = "/outUser/outUser/loginByFace";// 人脸登录
    public static final String GET_FACESET = "/outUser/outUser/getFaceSet";// 获取人脸库
    public static final String RANGE_POWER = "/machine/rangePower/add";// 提交电表数据
    public static final String GET_INNER_USER = "/innerUser/innerUser/getUserInfo";// 内部员工信息
    public static final String GET_AD = "/business/ad/getAd";// 获取广告信息
    public static final String GET_RANGE_INFO = "/machine/range/getRangeInfo";// 更新点位
    public static final String UPDATE_BANNER_NOTIFY = "UPDATE_BANNER_NOTIFY";// 更新广告通知
    public static final String NEED_UPDATE_BANNER_NOTIFY = "NEED_UPDATE_BANNER_NOTIFY";// 需要更新广告
    public static final String NEED_UPDATE_LOCATION_NOTIFY = "NEED_UPDATE_LOCATION_NOTIFY";// 需要更新点位
    public static final String NEED_UPDATE_NOTIFY = "NEED_UPDATE_NOTIFY";// 需要升级
    public static boolean IS_DOWNLOAD_APP = false;// 是否正在下载应用
    public static final String FACETELEPHONE = "/outUser/outUser/loginByFaceOfTelphone"; // 手机号码是否和人脸绑定/手机号码绑定的人脸是否和登录人脸匹配
    public static final String FACECOMMUNITY = "/outUser/outUser/loginByFaceOfCommunity"; //当前小区是否有此人脸
    public static final String RANGE_CLEAR = "/machine/rangeClean/add";// 生成清运数据
    public static final String INSPECTION_LOG = "/machine/rangeInspection/add";// 提交巡检单

    public static final String DEV_STATUS_MARK = "DEV_STATUS_MARK";// 设置标识
    public static final int DEV_STATUS_DOOR = 1;// 柜门
    public static final int DEV_STATUS_BOX = 2;// 灯箱
    public static final int DEV_STATUS_FUN = 3;// 风扇
    public static final int DEV_STATUS_DISINFECT = 4;// 消毒
    public static final int DEV_ENTRY_TEST = 5;// 测试入口
    public static final int DEV_STATUS_TEMPERATURE = 6;// 温度

    public static final String CURRENT_LOCATION_ID = "CURRENT_LOCATION_ID";// 点位ID
    public static final String CURRENT_LOCATION_NUM = "CURRENT_LOCATION_NUM";// 设备编码
    public static final String CURRENT_COMMUNITYNAME = "CURRENT_COMMUNITYNAME";// 小区名称
    public static final String CURRENT_RANGENAME = "CURRENT_RANGENAME";// 点位名称
    public static final String CURRENT_LOCATION_SOURCE = "CURRENT_LOCATION_SOURCE";// 点位来源
    public static final String CURRENT_COMMUNITY_ID = "CURRENT_COMMUNITY_ID";// 小区id
    public static final String CURRENT_COMMUNITY_ADCODE = "CURRENT_COMMUNITY_ADCODE";// 所在地区编码
    public static final String CURRENT_POINT_ADCODE = "CURRENT_POINT_ADCODE";// 点位编码
    public static final String RANGE_SOURCE_NAME_ALI = "aliInternal";// 阿里园区标识
    public static final String RANGE_SOURCE_NAME_HEMA = "freshhema";// 河马标识
    public static final String USER_TOKEN = "USER_TOKEN";// 用户token
    public static final String YINTAI_COUPONS_URL = "YINTAI_COUPONS_URL";// 银泰优惠券二维码url


    public static final String DEV_BUCKET_STATUS = "DEV_BUCKET_STATUS";// 倒水桶

    public static final String LAUNCH_COME_FROM = "LAUNCH_COME_FROM";// 点位设置页面启动来源
    public static final int LAUNCH_COME_FROM_SPLASH = 1;// 启动页
    public static final int LAUNCH_COME_FROM_AllVIEW = 2;// 管理页面
    public static final String FINISH_HOME_PAGE = "FINISH_HOME_PAGE";// 结束Home页面
    public static final String FACE_PHONE = "FACE_PHONE";// 人脸绑定的电话

    public static final String CURRENT_FACE_TOKEN = "CURRENT_FACE_TOKEN";// 当前人脸token

    public static final int BOTTOM_FULL_NUM = 120;// 主机柜满溢数目
    public static final int BUCKET_FULL_NUM = 30;// 水桶满溢重量（公斤）水桶重10公斤
    public static final int BUCKET_FULL_OTHER_NUM = 100;// 其它桶重100公斤
    public static final int MAX_BUCKET_PAPER_WEIGHT = 15000;// 纸张桶重15公斤（满溢最大数据）
    public static final int MAX_BUCKET_SPIN_WEIGHT = 25000;// 织物桶重25公斤（满溢最大数据）
    public static final int MAX_BUCKET_METAL_WEIGHT = 80000;// 金属桶重80公斤（满溢最大数据）
    public static final int MAX_BUCKET_PLASTIC_WEIGHT = 15000;// 塑料制品桶重15公斤（满溢最大数据）


    public static final String DEV_PLASTIC_TYPE = "plasticBottle";// 塑料瓶类型
    public static final String DEV_GLASS_TYPE = "glassBottle";// 玻璃瓶类型
    public static final String DEV_PAPER_TYPE = "paper";// 纸张类型
    public static final String DEV_SPIN_TYPE = "spin";// 纺织品类型
    public static final String DEV_METAL_TYPE = "metal";// 金属类型
    public static final String DEV_CEMENT_TYPE = "plastic";// 塑料制品类型
    public static final String DEV_POISON_TYPE = "harmful";// 有害物类型
    public static final String DEV_OTHER_TYPE = "other";// 其它类型

    public static final String LOG_ALARM = "alarm";// 日志-告警
    public static final String LOG_EXCEPTION = "exception";// 日志-异常
    public static final String LOG_CLEAR = "clear";// 日志-清理
    public static final String LOG_INSPECTION = "inspection";// 日志-巡检
    public static final String LOG_COUPON_COUNT = "coupon";// 日志-优惠券计数
    public static final String LOG_PCB_EXCEPTION = "pcbException";// 日志-Pcb异常

    public static final String EXCEPTION_WATER_BUCKET_FULL = "倒水桶已满";// 倒水桶已满
    public static final String EXCEPTION_RECYCLE_BUCKET_FULL = "回收桶已满";// 回收桶已满

    public static final String EXCEPTION_CABINET_FOLDER = "投递门夹手异常";// 投递门（夹手）
    public static final String EXCEPTION_OPEN_DOOR = "投递门开门异常（未收到PCB板反馈）";// 投递门（打开超时-控制板未上传）
    public static final String EXCEPTION_CABINET_OPEN = "投递门开门异常";// 投递门（打开超时-控制板上传）
    public static final String EXCEPTION_CLOSE_DOOR = "投递门关门异常（未收到PCB板反馈）";// 投递门（关闭超时-控制板未上传）
    public static final String EXCEPTION_CABINET_CLOSE = "投递门关门异常";// 投递门门（关闭超时-控制板上传）
    public static final String EXCEPTION_CABINET_FOLDER_OPEN = "投递门夹手开门异常";// 投递门（夹手开门）
    public static final String EXCEPTION_CABINET_OPENING = "投递门处于打开状态";// 投递门（打开状态）
    public static final String EXCEPTION_CABINET_FOLDERING = "投递门处于夹手状态";// 投递门（夹手状态）
    public static final String EXCEPTION_PCB_RESPONSE_FAILURE = "PCB响应失败";// PCB响应失败

    public static final String FACE_QR_INFO = "https://www.zhangyuhuishou.com/qr/face";// 二维码注册信息

    public static final String VIDEO_PIC_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/local";// 视频图片存储路径
    public static final String BIN_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pcbSys";// 硬件程序二进制文件存储路径
    public static final String BACKGROUND_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/background";//背景图片存储路径
    public static final String LOGO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/logo";//logo存储路径
    public static final String SKIN_BACKGROUND_URL = "SKIN_BACKGROUND_URL";//背景图片存储url
    public static final String SKIN_LOGO_URL = "SKIN_LOGO_URL";//logo图片存储url


    // 语音提示
    public static final String TTS_TIP_SCAN_QR = "欢迎使用章鱼智能回收柜，请使用微信或支付宝扫码投递";
    public static final String TTS_TIP_CHOOSE_THROW = "请选择您要投递的物品分类";
    public static final String TTS_TIP_PLASTIC_BOTTLE_DOOR_OPENING = "塑料瓶投递门正在开启";
    public static final String TTS_TIP_PLASTIC_BOTTLE_DOOR_CLOSING = "塑料瓶投递门正在关闭";
    public static final String TTS_TIP_GLASS_BOTTLE_DOOR_OPENING = "玻璃瓶投递门正在开启";
    public static final String TTS_TIP_GLASS_BOTTLE_DOOR_CLOSING = "玻璃瓶投递门正在关闭";
    public static final String TTS_TIP_PAPER_DOOR_OPENING = "纸张投递门正在开启";
    public static final String TTS_TIP_PAPER_DOOR_CLOSING = "纸张投递门正在关闭";
    public static final String TTS_TIP_SPIN_DOOR_OPENING = "织物投递门正在开启";
    public static final String TTS_TIP_SPIN_DOOR_CLOSING = "织物投递门正在关闭";
    public static final String TTS_TIP_METAL_DOOR_OPENING = "金属投递门正在开启";
    public static final String TTS_TIP_METAL_DOOR_CLOSING = "金属投递门正在关闭";
    public static final String TTS_TIP_PLASTIC_DOOR_OPENING = "塑料织品投递门正在开启";
    public static final String TTS_TIP_PLASTIC_DOOR_CLOSING = "塑料制品投递门正在关闭";
    public static final String TTS_TIP_TOO_LIGHT = "投递物品品类错误，请检查后重新投入";
    public static final String TTS_TIP_TOO_HEAVY = "请将瓶内液体倒入入水口后重新投递";

    public static final String TTS_TIP_INPUT_YOUR_PHONE = "请输入手机号码";
    public static final String TTS_TIP_SCAN_FACE_INFO = "请正对屏幕，开始人脸识别";
    public static final String TTS_TIP_THANK_YOU_FOR_THIS_DELIVERY = "感谢你本次投递";

    public static final String DEVICE_MODEL_RK3288 = "rk3288";// 设备型号（Android版本7.1.2）
    public static final String DEVICE_MODEL_DS83X = "Octopus A83 F1";// 设备型号（Android版本6.0.1）

    public static final String IS_AUTO_UPDATE = "IS_AUTO_UPDATE";// PCB是否自动升级

    public static final String CURRENT_DAY = "CURRENT_DAY";// 当前日期（点位激活时保存此值,跟新电表有关）

}
