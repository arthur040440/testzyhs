package com.zhangyuhuishou.zyhs.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.tlh.android.utils.NetworkUtils;
import com.tlh.android.utils.ToastUitls;
import com.tlh.android.utils.Utils;
import com.zhangyuhuishou.zyhs.base.BasePresenter;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.ClearMissModel;
import com.zhangyuhuishou.zyhs.model.ElecUploadModel;
import com.zhangyuhuishou.zyhs.model.PointModel;
import com.zhangyuhuishou.zyhs.model.RangeInfoModel;
import com.zhangyuhuishou.zyhs.model.TerminalModel;
import com.zhangyuhuishou.zyhs.model.UserModel;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;

// 点位相关

public class PointPresenter extends BasePresenter<IPointView> {

    private Context context;

    public PointPresenter(Context context) {
        this.context = context;
    }

    // 根据搜索获取点位信息
    public void getPoints(String rangeName, String rangeId) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            final long startTime = System.currentTimeMillis();

            DealHttp.pointSearchNew(rangeName, rangeId, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long costTime = System.currentTimeMillis() - startTime;
                        System.out.println("点位搜索时间：" + costTime + "\n" + o.toString());
                        Gson gson = new Gson();
                        PointModel model = gson.fromJson(o.toString(), PointModel.class);
                        if (model.getCode() == 0) {
                            getBaseView().getPoints(model.getData());
                        } else {
                            System.out.println(model.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    ToastUitls.toastMessage("接口访问失败信息：" + errorCode + ":" + msg);
                }
            });
        }
    }

    // 激活点位
    public void activePoint(final PointModel.DataBean model) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            final long startTime = System.currentTimeMillis();

            DealHttp.pointActiveNew(model.getRangeId(), new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long costTime = System.currentTimeMillis() - startTime;
                        System.out.println("检测时间：" + costTime + "\n" + o.toString());
                        JSONObject jsonObject = (JSONObject) o;
                        int code = jsonObject.getInteger("code");
                        String message = jsonObject.getString("message");
                        if (code == 0) {
                            getBaseView().activePoint(model);
                        } else {
                            System.out.println(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    ToastUitls.toastMessage("接口访问失败信息：" + errorCode + ":" + msg);
                }
            });
        }
    }

    // 故障上报
    public void logUpload(String rangeId, String terminalId, String rangeLogType, String content) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            DealHttp.rangException(rangeId, terminalId, rangeLogType, content, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        System.out.println("日志：" + o.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                }
            });
        }
    }


    // 故障上报
    public void logUpload(final String rangeId, final TerminalModel terminalModel, final String rangeLogType, final String content) {
        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            DealHttp.rangException(rangeId, terminalModel.getTerminalId(), rangeLogType, content, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        System.out.println("日志：" + o.toString());
                        getBaseView().clearSuccess(terminalModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    getBaseView().clearError("网络很不稳定");
                    DBManager dbManager = DBManager.getInstance(context);
                    dbManager.insertClearData(rangeId, terminalModel.getTerminalId(), rangeLogType, content, Utils.isEmpty(UserModel.getInstance().getId()) ? "" : UserModel.getInstance().getId());
                }
            });
        } else {
            getBaseView().clearError("网络不稳定");
            DBManager dbManager = DBManager.getInstance(context);
            dbManager.insertClearData(rangeId, terminalModel.getTerminalId(), rangeLogType, content, Utils.isEmpty(UserModel.getInstance().getId()) ? "" : UserModel.getInstance().getId());
        }
    }

    // 故障上报（丢失的清理信息）
    public void logUpload(final ClearMissModel model) {
        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            DealHttp.rangException(model, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {

                        JSONObject object = (JSONObject) o;
                        int code = object.getInteger("code");
                        if (code == 0) {
                            DBManager dbManager = DBManager.getInstance(context);
                            dbManager.deleteClearInfoById(model.getId());
                        }
                        System.out.println("丢失的日志清理信息：" + o.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                }
            });
        }
    }

    // 发送心跳
    public void sendPolling(final String rangeId, final JSONArray terminalInfo, String mobileVersion, String pcbVersion) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }
            final long startTime = System.currentTimeMillis();

            DealHttp.sendPolling(rangeId, terminalInfo, mobileVersion, pcbVersion, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long costTime = System.currentTimeMillis() - startTime;
                        System.out.println("发送心跳时间：" + costTime + "\n" + o.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                }
            });
        }
    }

    // 上传电量
    public void uploadElec(final String rangeId, final String ammeter, String meterCode) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }
            final long startTime = System.currentTimeMillis();

            DealHttp.uploadElectric(rangeId, ammeter, meterCode, new BaseHttpRequestCallback() {

                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long costTime = System.currentTimeMillis() - startTime;
                        System.out.println("上传电量时间：" + costTime + "\n" + o.toString());
                        EventBus.getDefault().post(new ElecUploadModel(true));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    EventBus.getDefault().post(new ElecUploadModel(false));
                }
            });
        }
    }

    // 更新点位
    public void updateLocationInfo(final String rangeId) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached() || TextUtils.isEmpty(rangeId)) {
                return;
            }
            final long startTime = System.currentTimeMillis();

            DealHttp.updateRangeInfo(rangeId, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    long costTime = System.currentTimeMillis() - startTime;
                    System.out.println("获取更新点位时间：" + costTime + "\n" + o.toString());
                    Gson gson = new Gson();
                    RangeInfoModel model = gson.fromJson(o.toString(), RangeInfoModel.class);
                    if (model.getCode() == 0) {
                        getBaseView().updatePoint(model);
                    } else {
                        System.out.println(model.getMessage());
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    getBaseView().updatePoint(null);
                }
            });
        }
    }
}
