package com.zhangyuhuishou.zyhs.download.savepics;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.tlh.android.config.Constant;
import com.tlh.android.utils.ApkUtils;
import com.tlh.android.utils.ToastUitls;
import com.zhangyuhuishou.zyhs.model.AdModel;
import com.zhangyuhuishou.zyhs.model.SkinModel;

import java.io.File;
import java.util.List;

import static com.tlh.android.config.Constant.BACKGROUND_PATH;
import static com.tlh.android.config.Constant.LOGO_PATH;

/**
 * @author tlh
 * @time 2016/12/28 14:25
 */

public class DownImagePrensenter {

    private Context context;


    private IDownFinish iDownFinish;

    public DownImagePrensenter(Context context, IDownFinish iDownFinish) {
        this.context = context;
        this.iDownFinish = iDownFinish;
    }


    public void imageDown(List<AdModel.DataBean> adList) {
        new ImageDownTask(adList).execute();
    }

    public void imageSave(List<AdModel.DataBean> adList) {
        new ImageSaveTask(adList).execute();
    }

    public void skinSource(SkinModel.DataBean dataBean) {
        new SkinSourceTask(dataBean).execute();

    }

    public class SkinSourceTask extends AsyncTask<Void, Integer, Void> {

        private SkinModel.DataBean dataBean;

        private SkinSourceTask(SkinModel.DataBean dataBean) {
            this.dataBean = dataBean;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ApkUtils.downloadPicToSDCard(Constant.BACKGROUND_PATH, context, dataBean.getBackgroundUrl());
            ApkUtils.downloadPicToSDCard(Constant.LOGO_PATH, context, dataBean.getLogoUrl());
            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            iDownFinish.getSkinFinish(dataBean);

        }
    }

    private class ImageDownTask extends AsyncTask<Void, Integer, Void> {

        private List<AdModel.DataBean> adList;

        private ImageDownTask(List<AdModel.DataBean> adList) {
            this.adList = adList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                for (int i = 0; i < adList.size(); i++) {
                    ApkUtils.downloadPicToSDCard(Constant.VIDEO_PIC_PATH, context, adList.get(i).getFileUrl());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            iDownFinish.overFinish(adList);
        }
    }

    private class ImageSaveTask extends AsyncTask<Void, Integer, Void> {

        private ProgressDialog mProgressDialog;
        private List<AdModel.DataBean> adList;

        private ImageSaveTask(List<AdModel.DataBean> adList) {
            this.adList = adList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("广告资源下载中");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                for (int i = 0; i < adList.size(); i++) {
                    ApkUtils.downloadPicToSDCard(Constant.VIDEO_PIC_PATH, context, adList.get(i).getFileUrl());
                    publishProgress(i);
                }
            } catch (Exception e) {
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(adList.size());
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.dismiss();
            iDownFinish.overFinish(adList);
        }
    }

}
