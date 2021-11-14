package com.zhangyuhuishou.zyhs.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.NetworkUtils;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.utils.Utils;
import com.zhangyuhuishou.zyhs.base.BasePresenter;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.InspectionModel;
import com.zhangyuhuishou.zyhs.model.UserModel;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;

public class MaintainPresenter extends BasePresenter<IMaintainView> {

    private Context context;

    public MaintainPresenter(Context context) {
        this.context = context;
    }

    private String token = "";

    // 提交运维巡检单
    public void submitInspectionLog(final String rangeId, final String report, final String remark) {

        if (UserModel.getInstance().getToken() != null) {
            token = UserModel.getInstance().getToken();
        } else {
            token = SPUtils.getString(context, Constant.USER_TOKEN, "");
        }

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            final long startTime = System.currentTimeMillis();


            DealHttp.submitInspectionLog(rangeId, token, report, remark, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long costTime = System.currentTimeMillis() - startTime;
                        System.out.println("巡检单提交成功：" + costTime + "\n" + o.toString());
                        JSONObject object = (JSONObject) o;
                        int code = object.getInteger("code");
                        if (code == 0) {
                            // 操作成功
                            getBaseView().inspectionCallback();
                        } else {
                            // 操作失败
                            getBaseView().inspectionCallback();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        getBaseView().inspectionCallback();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    System.out.println("接口访问失败：" + errorCode + "--" + msg);
                    DBManager.getInstance(context).insertInspectionInfo(rangeId, token, remark, report);
                    getBaseView().inspectionCallback();
                }
            });
        } else {
            System.out.println("没有网络");
            DBManager.getInstance(context).insertInspectionInfo(rangeId, token, remark, report);
            getBaseView().inspectionCallback();
        }
    }


    // 上传巡检单（因为网络原因丢失的数据）
    public void uploadInspectionInfo(final InspectionModel inspectionModel) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached() || inspectionModel == null || Utils.isEmpty(inspectionModel.getToken())) {
                return;
            }

            final long startTime = System.currentTimeMillis();

            DealHttp.submitInspectionLog(inspectionModel.getRangeId(), inspectionModel.getToken(), inspectionModel.getReport(), inspectionModel.getRemark(),
                    new BaseHttpRequestCallback() {
                        @Override
                        protected void onSuccess(Object o) {
                            super.onSuccess(o);
                            try {
                                long costTime = System.currentTimeMillis() - startTime;
                                System.out.println("上传丢失的巡检数据：" + costTime + "\n" + o.toString());
                                JSONObject object = (JSONObject) o;
                                int code = object.getInteger("code");
                                if (code == 0) {
                                    // 访问成功，删除数据库数据
                                    DBManager.getInstance(context).deleteInspectionById(inspectionModel.getId());
                                }
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
}
