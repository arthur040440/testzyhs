package com.zhangyuhuishou.zyhs.presenter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tlh.android.utils.NetworkUtils;
import com.zhangyuhuishou.zyhs.base.BasePresenter;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;

// 用户相关

public class UserPresenter extends BasePresenter<IUserView> {

    private Context context;

    public UserPresenter(Context context) {
        this.context = context;
    }

    // 内部员工信息
    public void getInnerUser(String rangId) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            final long startTime = System.currentTimeMillis();

            DealHttp.getInnerUserInfo(rangId,new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long costTime = System.currentTimeMillis() - startTime;
                        System.out.println("获取内部员工信息：" + costTime + "\n" + o.toString());
                        JSONObject jsonObject = (JSONObject) o;
                        int code = jsonObject.getInteger("code");
                        if (code == 0) {
                            getBaseView().roleDistribute(jsonObject.getJSONObject("data").getString("roleNames"));
                        } else if (code == -1) {
                            getBaseView().outOfCurrentArea(jsonObject.getString("message"));
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
