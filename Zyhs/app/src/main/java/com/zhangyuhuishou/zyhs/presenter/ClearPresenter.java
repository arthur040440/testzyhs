package com.zhangyuhuishou.zyhs.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.NetworkUtils;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.utils.Utils;
import com.zhangyuhuishou.zyhs.base.BasePresenter;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.ClearDismissModel;
import com.zhangyuhuishou.zyhs.model.UserModel;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;

// 清运数据相关
public class ClearPresenter extends BasePresenter<IClearView> {

    private Context context;

    public ClearPresenter(Context context) {
        this.context = context;
    }

    private String token = "";

    // 创建清运数据
    public void createClearOrder(final String rangeId, final int estimatePlasticBottleCount, final int actualPlasticBottleCount,
                                 final int estimateGlassBottleCount, final int actualGlassBottleCount, final int estimateCansCount,
                                 final int actualCansCount, final int estimatePaperCount, final int actualPaperCount,
                                 final int estimateSpinCount, final int actualSpinCount, final int estimatePlasticCount,
                                 final int actualPlasticCount, final int estimateMetalCount, final int actualMetalCount) {

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
            DealHttp.produceClearOrder(rangeId, token, estimatePlasticBottleCount, actualPlasticBottleCount, estimateGlassBottleCount, actualGlassBottleCount, estimateCansCount,
                    actualCansCount, estimatePaperCount, actualPaperCount, estimateSpinCount, actualSpinCount, estimatePlasticCount,
                    actualPlasticCount, estimateMetalCount, actualMetalCount, new BaseHttpRequestCallback() {
                        @Override
                        protected void onSuccess(Object o) {
                            super.onSuccess(o);
                            try {
                                long costTime = System.currentTimeMillis() - startTime;
                                System.out.println("生成清运数据：" + costTime + "\n" + o.toString());
                                JSONObject object = (JSONObject) o;
                                int code = object.getInteger("code");
                                if (code == 0) {
                                    // 操作成功
                                    getBaseView().clearSuccessCallback();
                                } else {
                                    getBaseView().clearNetExceptionCallback(1);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                getBaseView().clearNetExceptionCallback(2);
                            }
                        }

                        @Override
                        public void onFailure(int errorCode, String msg) {
                            super.onFailure(errorCode, msg);
//                            DBManager.getInstance(context).insertClearInfo(oprSysUserId, rangeId, estimatePlasticBottleCount, actualPlasticBottleCount, estimateGlassBottleCount, actualGlassBottleCount, estimateCansCount,
//                                    actualCansCount, estimatePaperCount, actualPaperCount, estimateSpinCount, actualSpinCount, estimatePlasticCount,
//                                    actualPlasticCount, estimateMetalCount, actualMetalCount);
                            System.out.println("接口访问失败：" + errorCode + "--" + msg);
                            getBaseView().clearNetExceptionCallback(3);
                        }
                    });
        } else {
            System.out.println("没有网络");
//            DBManager.getInstance(context).insertClearInfo(oprSysUserId, rangeId, estimatePlasticBottleCount, actualPlasticBottleCount, estimateGlassBottleCount, actualGlassBottleCount, estimateCansCount,
//                    actualCansCount, estimatePaperCount, actualPaperCount, estimateSpinCount, actualSpinCount, estimatePlasticCount,
//                    actualPlasticCount, estimateMetalCount, actualMetalCount);
            getBaseView().clearNetExceptionCallback(4);
        }
    }

    // 创建清运数据（因为网络原因丢失的数据）
    public void uploadClearOrder(final ClearDismissModel model) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached() || model == null || Utils.isEmpty(model.getOprSysUserId())) {
                return;
            }

            final long startTime = System.currentTimeMillis();

            DealHttp.produceClearOrder(model.getRangeId(), model.getOprSysUserId(), model.getEstimatePlasticBottleCount(),
                    model.getActualPlasticBottleCount(), model.getEstimateGlassBottleCount(), model.getActualGlassBottleCount(),
                    model.getEstimateCansCount(), model.getActualCansCount(), model.getEstimatePaperCount(),
                    model.getActualPaperCount(), model.getEstimateSpinCount(), model.getActualSpinCount(),
                    model.getEstimatePlasticCount(), model.getActualPlasticCount(), model.getEstimateMetalCount(),
                    model.getActualMetalCount(), new BaseHttpRequestCallback() {
                        @Override
                        protected void onSuccess(Object o) {
                            super.onSuccess(o);
                            try {
                                long costTime = System.currentTimeMillis() - startTime;
                                System.out.println("生成丢失的清运数据：" + costTime + "\n" + o.toString());
                                JSONObject object = (JSONObject) o;
                                int code = object.getInteger("code");
                                if (code == 0) {
                                    // 访问成功，删除数据库数据
                                    DBManager.getInstance(context).deleteClearDataById(model.getId());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
//                                getBaseView().clearCallback();
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
