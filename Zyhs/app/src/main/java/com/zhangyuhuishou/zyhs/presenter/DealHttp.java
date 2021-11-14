package com.zhangyuhuishou.zyhs.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.tlh.android.config.Constant;
import com.tlh.android.utils.Utils;
import com.zhangyuhuishou.zyhs.model.ClearMissModel;
import com.zhangyuhuishou.zyhs.model.UserModel;

import org.json.JSONArray;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.RequestParams;
import okhttp3.CacheControl;

import static com.tlh.android.config.Constant.BASE_URL_NEW;
import static com.tlh.android.config.Constant.SERVER_IP_NEW;

public class DealHttp {

    // 获取用户详情
    public static void getUserInfo(BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addHeader("token", UserModel.getInstance().getToken());
        HttpRequest.post(BASE_URL_NEW + Constant.USER_INFO, params, callback);
    }

//    git@gitee.com:zhangyuhuishou/machine-android.git
//    git@gitee.com:qigecode/example.git

    // 点位搜索
    public static void pointSearchNew(String rangeName, String rangeId, BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        if (!TextUtils.isEmpty(rangeName)) {
            params.addFormDataPart("rangeName", rangeName);
        }
        if (!TextUtils.isEmpty(rangeId)) {
            params.addFormDataPart("rangeId", rangeId);
        }
        HttpRequest.post(BASE_URL_NEW + Constant.POINT_SEARCH_NEW, params, callback);
    }

    // 点位激活
    public static void pointActiveNew(String rangeId, BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addFormDataPart("rangeId", rangeId);
        HttpRequest.post(BASE_URL_NEW + Constant.TERMINAL_ACTIVE_NEW, params, callback);
    }

    // 创建订单
    public static void createOrderNew(String rangeId, JSONArray orderInfo, String token, BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addHeader("zyhs-token", token);
        params.addFormDataPart("rangeId", rangeId);
        params.addFormDataPart("orderInfo", orderInfo.toString());
        Log.e("orderInfo", orderInfo.toString());
        HttpRequest.post(BASE_URL_NEW + Constant.CREATE_ORDER_NEW, params, callback);
    }

    // 获取回收物价格
    public static void getList(String rangeId, String communityId, BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addFormDataPart("rangeId", rangeId);
        params.addFormDataPart("communityId", communityId);
        HttpRequest.post(BASE_URL_NEW + Constant.GET_RECYCLER_PRICE, params, callback);
    }


    // 创建（丢失）订单
    public static void createOrder(String rangeId, String token, JSONArray orderInfo, BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addHeader("zyhs-token", token);
        params.addFormDataPart("rangeId", rangeId);
        params.addFormDataPart("orderInfo", orderInfo.toString());
        HttpRequest.post(BASE_URL_NEW + Constant.CREATE_ORDER_NEW, params, callback);
    }

    // 发送心跳
    public static void sendPolling(String rangeId, JSONArray terminalInfo, String mobileVersion, String pcbVersion, BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addFormDataPart("rangeId", rangeId);
        params.addFormDataPart("version", mobileVersion);
        params.addFormDataPart("terminalInfo", terminalInfo.toString());
        params.addFormDataPart("pcbVersion", pcbVersion);
        HttpRequest.post(BASE_URL_NEW + Constant.SEND_POLLING, params, callback);

    }

    // 点位设备故障上报-资源清理-告警
    public static void rangException(String rangeId, String terminalId, String rangeLogType, String content, BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addFormDataPart("rangeId", rangeId);
        params.addFormDataPart("terminalId", terminalId);
        params.addFormDataPart("rangeLogType", rangeLogType);
        params.addFormDataPart("content", content);
        if (!Utils.isEmpty(UserModel.getInstance().getId())) {
            params.addFormDataPart("oprSysUserId", UserModel.getInstance().getId());
        }
        HttpRequest.post(BASE_URL_NEW + Constant.SYSTEM_EXCEPTION, params, callback);
    }

    // 点位设备故障上报-资源清理-告警
    public static void rangException(ClearMissModel model, BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addFormDataPart("rangeId", model.getRangeId());
        params.addFormDataPart("terminalId", model.getTerminalId());
        params.addFormDataPart("rangeLogType", model.getRangeLogType());
        params.addFormDataPart("content", model.getContent());
        params.addFormDataPart("oprSysUserId", model.getOprSysUserId());
        HttpRequest.post(BASE_URL_NEW + Constant.SYSTEM_EXCEPTION, params, callback);
    }

    //动态换肤
    public static void getSkinSource(String rangeId, String rangeSource, BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addFormDataPart("rangeId", rangeId);
        params.addFormDataPart("rangeSource", rangeSource);
        HttpRequest.post(BASE_URL_NEW + Constant.DYNAMIC_SKIN, params, callback);
    }

    //获取银泰优惠券
    public static void getYintaiCoupons(String rangeId, BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addFormDataPart("rangeId", rangeId);
        HttpRequest.post(BASE_URL_NEW + Constant.YINTAI_COUPONS, params, callback);
    }

    //饿了么活动礼品兑换
    public static void activityJoin(String rangeId, String token, BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addHeader("zyhs-token", token);
        params.addFormDataPart("productId", rangeId);
        HttpRequest.post(BASE_URL_NEW + Constant.ELEMENT_COUPONS, params, callback);
    }

    // 创建新用户
    public static void createUser(BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        HttpRequest.post(BASE_URL_NEW + Constant.CREATE_USER, params, callback);
    }

    // 检测升级
    public static void checkUpdate(String rangeId, String rangeSource, BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addFormDataPart("rangeId", rangeId);
        params.addFormDataPart("rangeSource", rangeSource);
        HttpRequest.post(BASE_URL_NEW + Constant.GET_VERSION, params, callback);
    }

    // 检测升级（PCB）
    public static void checkUpdateForPcb(String rangeId, String rangeSource, BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addFormDataPart("rangeId", rangeId);
        params.addFormDataPart("rangeSource", rangeSource);
        params.addFormDataPart("machineVersionType", "pcb");
        HttpRequest.post(BASE_URL_NEW + Constant.GET_VERSION, params, callback);
    }

    // 提交电量
    public static void uploadElectric(String rangeId, String ammeter, String meterCode, BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addFormDataPart("rangeId", rangeId);
        params.addFormDataPart("ammeter", ammeter);
        params.addFormDataPart("meterCode", meterCode);
        HttpRequest.post(BASE_URL_NEW + Constant.RANGE_POWER, params, callback);
    }

    // 内部员工信息
    public static void getInnerUserInfo(String rangId, BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addHeader("zyhs-token", UserModel.getInstance().getToken());
        params.addFormDataPart("rangeId", rangId);
        HttpRequest.post(BASE_URL_NEW + Constant.GET_INNER_USER, params, callback);
    }

    // 获取广告
    public static void getADs(String rangeId, String rangeSource, BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addFormDataPart("rangeId", rangeId);
        params.addFormDataPart("rangeSource", rangeSource);
        HttpRequest.post(BASE_URL_NEW + Constant.GET_AD, params, callback);
    }

    // 更新点位
    public static void updateRangeInfo(String rangeId, BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addFormDataPart("rangeId", rangeId);
        HttpRequest.post(BASE_URL_NEW + Constant.GET_RANGE_INFO, params, callback);
    }

    // 生成清运数据
    public static void produceClearOrder(String rangeId, String token, int estimatePlasticBottleCount, int actualPlasticBottleCount,
                                         int estimateGlassBottleCount, int actualGlassBottleCount, int estimateCansCount,
                                         int actualCansCount, int estimatePaperCount, int actualPaperCount,
                                         int estimateSpinCount, int actualSpinCount, int estimatePlasticCount,
                                         int actualPlasticCount, int estimateMetalCount, int actualMetalCount,
                                         BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addHeader("zyhs-token", token);
        params.addFormDataPart("rangeId", rangeId);
        params.addFormDataPart("estimatePlasticBottleCount", estimatePlasticBottleCount);
        params.addFormDataPart("actualPlasticBottleCount", actualPlasticBottleCount);
        params.addFormDataPart("estimateGlassBottleCount", estimateGlassBottleCount);
        params.addFormDataPart("actualGlassBottleCount", actualGlassBottleCount);
        params.addFormDataPart("estimateCansCount", estimateCansCount);
        params.addFormDataPart("actualCansCount", actualCansCount);
        params.addFormDataPart("estimatePaperCount", estimatePaperCount);
        params.addFormDataPart("actualPaperCount", actualPaperCount);
        params.addFormDataPart("estimateSpinCount", estimateSpinCount);
        params.addFormDataPart("actualSpinCount", actualSpinCount);
        params.addFormDataPart("estimatePlasticCount", estimatePlasticCount);
        params.addFormDataPart("actualPlasticCount", actualPlasticCount);
        params.addFormDataPart("estimateMetalCount", estimateMetalCount);
        params.addFormDataPart("actualMetalCount", actualMetalCount);
        params.addFormDataPart("estimateHarmfulCount", 0);
        params.addFormDataPart("actualHarmfulCount", 0);
        HttpRequest.post(BASE_URL_NEW + Constant.RANGE_CLEAR, params, callback);
    }


    // 提交巡检单
    public static void submitInspectionLog(String rangeId, String token, String report, String remark, BaseHttpRequestCallback callback) {
        RequestParams params = getRequestParams();
        params.addHeader("zyhs-token", token);
        params.addFormDataPart("rangeId", rangeId);
        params.addFormDataPart("remark", remark);
        params.addFormDataPart("report", report);
        HttpRequest.post(BASE_URL_NEW + Constant.INSPECTION_LOG, params, callback);
    }

    // 通用参数
    private static RequestParams getRequestParams() {
        RequestParams params = new RequestParams();
        params.setCacheControl(CacheControl.FORCE_NETWORK);
        params.applicationJson();
        return params;
    }

}
