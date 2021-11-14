package com.zhangyuhuishou.zyhs.presenter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.tlh.android.utils.NetworkUtils;
import com.zhangyuhuishou.zyhs.base.BasePresenter;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;

public class YintaiCouponsPresenter extends BasePresenter<IYintaiCouponsView> {

    private Context context;

    public YintaiCouponsPresenter(Context context) {
        this.context = context;
    }

    // 获取银泰优惠券
    public void getYintaiCoupons(final String rangeId) {
        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            DealHttp.getYintaiCoupons(rangeId, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        JSONObject object = (JSONObject) o;
                        int code = object.getInteger("code");
                        if (code == 0) {
                            //操作成功
                            getBaseView().getYintaiCoupons(object.getJSONObject("data").getString("couponFileUrl"), true);

                        } else {
                            //操作失败
                            getBaseView().getYintaiCoupons("", false);
                        }

                        System.out.println("点位id：" + rangeId);
                        System.out.println("银泰优惠券：" + o.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    //请求失败
                    getBaseView().getYintaiCoupons("", false);

                }
            });
        }
    }
}
