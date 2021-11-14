package com.zhangyuhuishou.zyhs.presenter;

import android.content.Context;

import com.google.gson.Gson;
import com.tlh.android.utils.NetworkUtils;
import com.zhangyuhuishou.zyhs.banner.IBannerView;
import com.zhangyuhuishou.zyhs.base.BasePresenter;
import com.zhangyuhuishou.zyhs.model.AdModel;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;

// 广告相关

public class BannerPresenter extends BasePresenter<IBannerView> {


    private Context context;

    public BannerPresenter(Context context) {
        this.context = context;
    }

    // 获取广告信息
    public void getAds(String rangeId, String rangeSource) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            final long startTime = System.currentTimeMillis();

            DealHttp.getADs(rangeId, rangeSource, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long costTime = System.currentTimeMillis() - startTime;
                        System.out.println("获取广告：" + costTime + "\n" + o.toString());
                        Gson gson = new Gson();
                        AdModel model = gson.fromJson(o.toString(), AdModel.class);
                        if (model.getCode() == 0) {
                            getBaseView().showBannerData(model.getData());
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
                }
            });
        }
    }
}
