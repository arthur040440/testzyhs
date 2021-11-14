package com.zhangyuhuishou.zyhs.appupdate;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.tlh.android.config.Constant;
import com.tlh.android.utils.ApkUtils;
import com.zhangyuhuishou.zyhs.model.DownLoadModel;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import top.wuhaojie.installerlibrary.AutoInstaller;

/**
 * 作者:created by author:tlh
 * 日期:2018/11/5 14:06
 * 邮箱:tianlihui2234@live.com
 * 描述:应用升级
 */
public class AppUpdate {

    private Context mContext;
    private String Tag = AppUpdate.class.getSimpleName();
    private final String CACHE_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";// 应用更新包存储路径
    private AutoInstaller installer;// 升级器
    private DownLoadModel downLoadModel;
    private int mFileLength = 0;


    public AppUpdate(Context context) {
        this.mContext = context;
        downLoadModel = new DownLoadModel();
    }

    // 检测更新
    public void checkUpdate(String APK_URL, String serverVersion) {

        String mobileVersion = ApkUtils.getVersion(this.mContext, this.mContext.getPackageName());
        if (isUpdate(mobileVersion, serverVersion)) {
            download(APK_URL);
        } else {
            if (downLoadModel == null) {
                downLoadModel = new DownLoadModel();
            }
            downLoadModel.setTip("当前版本是最新版本");
            downLoadModel.setProgress(101);
            downLoadModel.setDissmiss(true);
            EventBus.getDefault().post(downLoadModel);
        }
    }

    // 下载
    public void download(String APK_URL) {
        if (Constant.IS_DOWNLOAD_APP) {
            return;
        }
        final File file = new File(CACHE_FILE_PATH + File.separator + "update.apk");
        if (file != null && file.isFile() && file.exists()) {
            file.delete();
        }
        Constant.IS_DOWNLOAD_APP = true;

        final long startTime = System.currentTimeMillis();
        installer = new AutoInstaller.Builder(this.mContext)
                .setMode(AutoInstaller.MODE.ROOT_ONLY)
                .setCacheDirectory(CACHE_FILE_PATH)
                .build();
//        installer.install(APK_FILE_PATH);
        installer.installFromUrl(APK_URL);
        installer.setOnStateChangedListener(new AutoInstaller.OnStateChangedListener() {
            @Override
            public void onStart() {
                Log.i(Tag, "开始下载");
                if (downLoadModel == null) {
                    downLoadModel = new DownLoadModel();
                }
                downLoadModel.setTip("开始下载，请稍等~~~");
                downLoadModel.setDissmiss(false);
                EventBus.getDefault().post(downLoadModel);
            }

            @Override
            public void onComplete() {
                Constant.IS_DOWNLOAD_APP = false;
                Log.i(Tag, "完成下载");
                long costTime = System.currentTimeMillis() - startTime;
                Log.i(Tag, "下载所需时间:" + costTime);
                if (downLoadModel == null) {
                    downLoadModel = new DownLoadModel();
                }
                File file = new File(CACHE_FILE_PATH + File.separator + "update.apk");
                if (file == null) {
                    downLoadModel.setProgress(200);
                    EventBus.getDefault().post(downLoadModel);
                    return;
                }

                long tempLength = file.length();
                System.out.println("临时文件大小：" + tempLength);
                System.out.println("下载文件大小：" + mFileLength);
                if (tempLength < mFileLength) {
                    downLoadModel.setProgress(200);
                    EventBus.getDefault().post(downLoadModel);
                } else {
                    downLoadModel.setDissmiss(false);
                    downLoadModel.setTip("正在安装，即将重启！");
                    EventBus.getDefault().post(downLoadModel);
                }
            }

            @Override
            public void onNeed2OpenService() {

            }

            @Override
            public void onDownloadProgress(int progress) {
                if (downLoadModel == null) {
                    downLoadModel = new DownLoadModel();
                }
                downLoadModel.setTip("正在下载，请稍等~~~");
                downLoadModel.setDissmiss(false);
                downLoadModel.setProgress(progress);
                EventBus.getDefault().post(downLoadModel);
            }

            @Override
            public void onFileLength(int fileLength) {
                mFileLength = fileLength;
            }
        });
    }

    // 安装更新
    public void update() {
        try {
            File file = new File(CACHE_FILE_PATH + File.separator + "update.apk");
            if (file == null)
                return;
            String serverVersion = ApkUtils.getVersionNameFromApk(this.mContext, CACHE_FILE_PATH + File.separator + "update.apk");
            String mobileVersion = ApkUtils.getVersion(this.mContext, this.mContext.getPackageName());
            if (mobileVersion.equals(serverVersion)) {
                // 删除本地文件
                if (file.isFile()) {
                    file.delete();
                }
                return;
            }
            if (installer == null) {
                installer = new AutoInstaller.Builder(this.mContext)
                        .setMode(AutoInstaller.MODE.ROOT_ONLY)
                        .setCacheDirectory(CACHE_FILE_PATH)
                        .build();
            }

            if (isUpdate(mobileVersion, serverVersion)) {
                installer.install(file.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 判断是否升级版本
    public boolean isUpdate(String mobileVersion, String serverVersion) {

//        当前版本
        String mobileVersionArray[] = mobileVersion.split("\\.");

//        服务器版本
        String serverVersionArray[] = serverVersion.split("\\.");

        String mobileVersionArrayNew[] = null;
        String serverVersionArrayNew[] = null;

        int length = Math.max(mobileVersionArray.length, serverVersionArray.length);
        if (mobileVersionArray.length < length) {
            mobileVersionArrayNew = new String[mobileVersionArray.length + 1];
            for (int i = 0; i < mobileVersionArray.length; i++) {
                mobileVersionArrayNew[i] = mobileVersionArray[i];
            }
            mobileVersionArrayNew[mobileVersionArray.length] = "0";
        } else {
            mobileVersionArrayNew = new String[mobileVersionArray.length];
            for (int i = 0; i < mobileVersionArray.length; i++) {
                mobileVersionArrayNew[i] = mobileVersionArray[i];
            }
        }

        if (serverVersionArray.length < length) {
            serverVersionArrayNew = new String[serverVersionArray.length + 1];
            for (int j = 0; j < serverVersionArray.length; j++) {
                serverVersionArrayNew[j] = serverVersionArray[j];
            }
            serverVersionArrayNew[serverVersionArray.length] = "0";
        } else {
            serverVersionArrayNew = new String[serverVersionArray.length];
            for (int j = 0; j < serverVersionArray.length; j++) {
                serverVersionArrayNew[j] = serverVersionArray[j];
            }
        }

        for (int i = 0; i < length; i++) {
            int mobile = Integer.valueOf(mobileVersionArrayNew[i]);
            int server = Integer.valueOf(serverVersionArrayNew[i]);
//            1 1 0   mobile  1 0 9
//            1 0 24  server  1 0 10
            if (server > mobile) {
                return true;
            } else if (server < mobile) {
                return false;
            }
        }
        return false;
    }

}
