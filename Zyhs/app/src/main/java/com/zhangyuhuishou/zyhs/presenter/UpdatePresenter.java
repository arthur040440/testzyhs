package com.zhangyuhuishou.zyhs.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.tlh.android.utils.ApkUtils;
import com.tlh.android.utils.NetworkUtils;
import com.zhangyuhuishou.zyhs.base.BasePresenter;
import com.zhangyuhuishou.zyhs.model.UpdateModel;
import com.zhangyuhuishou.zyhs.pcb.PcbModel;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;

public class UpdatePresenter extends BasePresenter<IUpdateView> {

    private Context context;

    public UpdatePresenter(Context context) {
        this.context = context;
    }

    // 检测升级
    public void checkUpdate(String rangeId, String rangeSource) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            DealHttp.checkUpdate(rangeId, rangeSource, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        Gson gson = new Gson();
                        UpdateModel model = gson.fromJson(o.toString(), UpdateModel.class);
                        if (model.getCode() == 0) {
                            getBaseView().updateInfo(model);
                        } else {
                            getBaseView().updateErrorInfo(model.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    getBaseView().updateErrorInfo("获取升级信息失败");
                }
            });
        }
    }

    // 检测升级（Pcb）
    public void checkUpdateForPcb(String rangeId, String rangeSource) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            final long startTime = System.currentTimeMillis();

            DealHttp.checkUpdateForPcb(rangeId, rangeSource, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long costTime = System.currentTimeMillis() - startTime;
                        System.out.println("获取PCB程序：" + costTime + "\n" + o.toString());
                        Gson gson = new Gson();
                        PcbModel model = gson.fromJson(o.toString(), PcbModel.class);
                        if (model.getCode() == 0) {
                            getBaseView().updatePcbInfo(model);
                        } else {
                            getBaseView().updateErrorInfo(model.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    getBaseView().updateErrorInfo("获取PCB升级信息失败");
                }
            });
        }
    }

    // 下载二进制文件
    public void downBinFile(String fileUrl) {
        new BinFileDownTask(fileUrl).execute();
    }

    private class BinFileDownTask extends AsyncTask<Void, Integer, Void> {

        private String fileUrl;
        private boolean isDownOk;
        private long startTime = 0;

        public BinFileDownTask(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                startTime = System.currentTimeMillis();
                isDownOk = ApkUtils.downloadBinFileToSDCard(context, fileUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            long costTime = System.currentTimeMillis() - startTime;
            System.out.println("下载PCB程序：" + costTime);
            getBaseView().downPcbOk(isDownOk);
        }
    }

}
