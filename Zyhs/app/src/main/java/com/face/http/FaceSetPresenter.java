package com.face.http;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.face.base.IFaceSetView;
import com.face.util.NetworkUtils;
import com.google.gson.Gson;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.utils.ToastUitls;
import com.zhangyuhuishou.zyhs.base.BasePresenter;
import com.zhangyuhuishou.zyhs.model.FaceSetModel;
import com.zhangyuhuishou.zyhs.model.UserBean;
import com.zhangyuhuishou.zyhs.model.UserModel;
import com.zhangyuhuishou.zyhs.presenter.DealHttp;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;

// 人脸库相关
public class FaceSetPresenter extends BasePresenter<IFaceSetView> {

    private Context context;

    public FaceSetPresenter(Context context) {
        this.context = context;
    }

    // 检测人脸
    public void getFaceset(final String telphone) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }
            final long startTime = System.currentTimeMillis();

            FaceHttp.getFaceSet(telphone, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long costTime = System.currentTimeMillis() - startTime;
                        System.out.println("获取本地人脸库时间：" + costTime + "\n" + o.toString());
                        Gson gson = new Gson();
                        FaceSetModel model = gson.fromJson(o.toString(), FaceSetModel.class);
                        if (model.getCode() == 0) {
                            getBaseView().faceSetToken(model.getData().getFaceSetToken());
                        } else {
                            getBaseView().faceTip(model.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    getBaseView().errorTip("接口访问失败，稍后重试");
                }
            });
        } else {
            getBaseView().errorTip("网络不稳定，稍后重试");
        }
    }


    // 手机号码是否和人脸绑定/手机号码绑定的人脸是否和登录人脸匹配
    public void loginByFaceOfTelphone(String faceToken, String communityId, String telphone) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            FaceHttp.loginByFaceOfTelphone(faceToken, communityId, telphone, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {

                        JSONObject jsonObject = (JSONObject) o;
                        int code = jsonObject.getInteger("code");

                        if (code == 0) {
                            jsonObject = jsonObject.getJSONObject("data");
                            String token = jsonObject.getString("token");
                            UserModel model = UserModel.getInstance();
                            model.setToken(token);
                            SPUtils.putString(context, Constant.USER_TOKEN, token);
                            getUserInfo();
                        } else if (code == -1) {

                            getBaseView().faceTip(jsonObject.getString("message"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    getBaseView().errorTip("接口访问失败，稍后重试");
                }
            });
        } else {
            getBaseView().errorTip("网络不稳定，稍后重试");
        }
    }


    // 获取用户详情
    public void getUserInfo() {

        // 判断网络状况
        if (com.tlh.android.utils.NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }
            DealHttp.getUserInfo(new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {

                        Gson gson = new Gson();
                        UserBean bean = gson.fromJson(o.toString(), UserBean.class);
                        if (bean.getCode() == 0) {
                            UserModel model = UserModel.getInstance();
                            if (TextUtils.isEmpty(model.getToken())) {
                                System.out.println("token为空");
                                return;
                            }
                            model.setId(bean.getData().getUserId());
                            model.setNickName(bean.getData().getNickName());
                            model.setProfilePhoto(bean.getData().getProfilePhoto());
                            model.setIntegral(bean.getData().getIntegral());
                            getBaseView().loginSuccess();
                        } else {
                            System.out.println(bean.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    ToastUitls.toastMessage("接口访问失败信息：" + errorCode + ":" + msg);
                }
            });
        }
    }
}


