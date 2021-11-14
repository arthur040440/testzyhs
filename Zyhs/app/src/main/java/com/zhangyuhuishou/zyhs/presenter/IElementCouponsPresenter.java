package com.zhangyuhuishou.zyhs.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.NetworkUtils;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.utils.Utils;
import com.zhangyuhuishou.zyhs.base.BasePresenter;
import com.zhangyuhuishou.zyhs.model.UserModel;
import com.zhangyuhuishou.zyhs.serialport.message.ThingModel;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;

public class IElementCouponsPresenter extends BasePresenter<IElementCouponsView> {

    private Context context;

    public IElementCouponsPresenter(Context context) {
        this.context = context;
    }

    private String token = "";

    //礼品兑换
    public void activityJoin(String productId, final ThingModel model) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            if (UserModel.getInstance().getToken() != null) {
                token = UserModel.getInstance().getToken();
            } else {
                token = SPUtils.getString(context, Constant.USER_TOKEN, "");
            }


            DealHttp.activityJoin(productId, token, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {

                        JSONObject object = (JSONObject) o;
                        int code = object.getInteger("code");
                        if (code == 0) {
                            //操作成功
                            getBaseView().activityJoin(true, model);

                        } else {
                            //操作失败
                            getBaseView().activityJoin(false, model);

                        }

                        System.out.println("礼品兑换：" + o.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    //请求失败
                    getBaseView().activityJoin(false, model);

                }
            });
        } else {
            System.out.println("没有网络");
            getBaseView().activityJoin(false, model);
        }
    }
}
