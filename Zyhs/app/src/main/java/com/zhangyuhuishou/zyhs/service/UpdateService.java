package com.zhangyuhuishou.zyhs.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tlh.android.config.Constant;
import com.tlh.android.utils.SPUtils;
import com.zhangyuhuishou.zyhs.appupdate.AppUpdate;
import com.zhangyuhuishou.zyhs.model.UpdateModel;
import com.zhangyuhuishou.zyhs.pcb.PcbModel;
import com.zhangyuhuishou.zyhs.presenter.IUpdateView;
import com.zhangyuhuishou.zyhs.presenter.UpdatePresenter;

/**
 * 作者:created by author:tlh
 * 日期:2018/11/19 15:25
 * 邮箱:tianlihui2234@live.com
 * 描述:后台升级服务
 */
public class UpdateService extends Service implements IUpdateView {

    private UpdatePresenter updatePresenter;
    private AppUpdate appUpdate;// 升级检测

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (updatePresenter == null) {
            updatePresenter = new UpdatePresenter(this);
            updatePresenter.attachView(this);
        }

        if (!Constant.IS_DOWNLOAD_APP) {
            String rangeSource = SPUtils.getString(this, Constant.CURRENT_LOCATION_SOURCE, "");
            updatePresenter.checkUpdate(SPUtils.getString(this, Constant.CURRENT_LOCATION_NUM), rangeSource);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    @Override
    public void updateInfo(UpdateModel model) {
        if (appUpdate == null) {
            appUpdate = new AppUpdate(this);
        }
        appUpdate.checkUpdate(model.getData().getFileUrl(), model.getData().getVersion());
    }

    @Override
    public void updateErrorInfo(String message) {

    }

    @Override
    public void updatePcbInfo(PcbModel model) {

    }

    @Override
    public void downPcbOk(boolean isOk) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public void showErr() {

    }

    @Override
    public Context getContext() {
        return null;
    }
}
