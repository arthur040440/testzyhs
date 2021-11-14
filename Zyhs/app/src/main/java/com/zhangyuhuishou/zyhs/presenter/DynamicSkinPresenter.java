package com.zhangyuhuishou.zyhs.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.tlh.android.utils.NetworkUtils;
import com.zhangyuhuishou.zyhs.base.BasePresenter;
import com.zhangyuhuishou.zyhs.model.SkinModel;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;

public class DynamicSkinPresenter extends BasePresenter<IDynamicSkinView> {

    private Context context;

    public DynamicSkinPresenter(Context context) {
        this.context = context;
    }

    public void getSkinSource(String rangeId, String rangeSource) {
        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }
            DealHttp.getSkinSource(rangeId, rangeSource, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        JSONObject object = (JSONObject) o;
                        System.out.println("动态换肤" + o.toString());
                        int code = object.getInteger("code");
                        if (code == 0) {
                            //操作成功
                            Gson gson = new Gson();
                            SkinModel skinModel = gson.fromJson(o.toString(), SkinModel.class);
                            getBaseView().getSkinSource(skinModel.getData(), true);
                        } else {
                            //操作失败
                            getBaseView().getSkinSource(null, false);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    //请求失败
                    getBaseView().getSkinSource(null, false);
                }
            });
        } else {
            getBaseView().getSkinSource(null, false);
        }

    }
}
