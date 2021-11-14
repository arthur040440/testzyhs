package com.face.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.face.activity.OpenglActivity;
import com.megvii.facepp.sdk.Facepp;
import com.megvii.licensemanager.sdk.LicenseManager;
import com.tlh.android.config.Constant;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.model.AuthModel;

import org.greenrobot.eventbus.EventBus;

public class FaceUtils {

    private Context mContext;
    private int mFaceType;
    private String phoneNum = "";

    public FaceUtils(Context context, int faceType) {
        this.mContext = context;
        this.mFaceType = faceType;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void network() {
        int type = Facepp.getSDKAuthType(ConUtil.getFileContent(mContext, R.raw.megviifacepp_0_5_2_model));
        if (type == 2) {
            Log.i("Face", "非联网授权");
            return;
        }

        final LicenseManager licenseManager = new LicenseManager(mContext);
        String uuid = ConUtil.getUUIDString(mContext);
        System.out.println("授权文件UUID：" + uuid);
        long apiName = Facepp.getApiName();
        licenseManager.setAuthTimeBufferMillis(0);
        licenseManager.takeLicenseFromNetwork(Util.CN_LICENSE_URL, uuid, Util.API_KEY, Util.API_SECRET, apiName, "365", new LicenseManager.TakeLicenseCallback() {
            @Override
            public void onSuccess() {
                authState(true, 0, "");
            }

            @Override
            public void onFailed(int i, byte[] bytes) {
                if (TextUtils.isEmpty(Util.API_KEY) || TextUtils.isEmpty(Util.API_SECRET)) {
                    if (!ConUtil.isReadKey(mContext)) {
                        authState(false, 1001, "");
                    } else {
                        authState(false, 1001, "");
                    }
                } else {
                    String msg = "";
                    if (bytes != null && bytes.length > 0) {
                        msg = new String(bytes);
                    }
                    authState(false, i, msg);
                }
            }
        });
    }

    private void authState(boolean isSuccess, int code, String msg) {
        if (isSuccess) {
            Log.i("Face", "授权成功");
            if (Util.FACE_TYPE_LOGIN == mFaceType) {
                Intent intent = new Intent(mContext, OpenglActivity.class);
                mContext.startActivity(intent);
                EventBus.getDefault().post(new AuthModel(true));
            }
        } else {
            EventBus.getDefault().post(new AuthModel(false));
            if (code == 1001) {
                Log.i("Face", "请到官网申请并填写API_KEY和API_SECRET");
            } else {
                Log.i("Face", "code=" + code + "，msg=" + msg);
            }
        }
    }
}
