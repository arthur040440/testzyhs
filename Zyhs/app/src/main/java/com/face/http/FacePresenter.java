package com.face.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.face.base.IFaceView;
import com.face.bean.FaceSearchInfo;
import com.face.bean.FaceppBean;
import com.face.util.NetworkUtils;
import com.face.util.Util;
import com.google.gson.Gson;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.utils.ToastUitls;
import com.zhangyuhuishou.zyhs.base.BasePresenter;
import com.zhangyuhuishou.zyhs.model.UserBean;
import com.zhangyuhuishou.zyhs.model.UserModel;
import com.zhangyuhuishou.zyhs.presenter.DealHttp;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import okhttp3.Headers;
import okhttp3.Response;

// 人脸相关
public class FacePresenter extends BasePresenter<IFaceView> {

    private Context context;

    public FacePresenter(Context context) {
        this.context = context;
    }

    // 检测人脸
    public void detectFace(final String image_base64) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }
            final long startTime = System.currentTimeMillis();

            FaceHttp.detectFace(image_base64, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long costTime = System.currentTimeMillis() - startTime;
                        System.out.println("检测时间：" + costTime + "\n" + o.toString());
                        FaceppBean bean = new Gson().fromJson(o.toString(), FaceppBean.class);
                        if (bean.getFaces().size() == 0) {
                            getBaseView().faceTip("未检测到人脸信息", Util.FACE_CALLBACE_NO_FACE, "");
                            return;
                        }
                        // 搜索人脸（没有进行存储，有获取用户详情）
                        getBaseView().detectSuccess(bean.getFaces().get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                        getBaseView().faceTip("数据解析错误", Util.FACE_CALLBACE_PARSE_ERROR, "");
                    }
                }


                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    getBaseView().faceTip("接口访问失败", Util.FACE_CALLBACE_ERROR, "");
                    Log.i("TAG", "接口访问成功异常信息：" + msg);
                }
            });
        }
    }


    // 当前小区是否有此人脸
    public void loginByFaceOfCommunity(final String face_token, String communityId) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            FaceHttp.loginByFaceOfCommunity(face_token, communityId, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {

                        JSONObject jsonObject = JSONObject.parseObject(o.toString());
                        int code = jsonObject.getInteger("code");

                        if (code == -1) {
                            String message = jsonObject.getString("message");
                            getBaseView().inputPhoneNumber(face_token, message);
                        } else if (code == 0) {

                            jsonObject = jsonObject.getJSONObject("data");
                            String token = jsonObject.getString("token");
                            UserModel model = UserModel.getInstance();
                            model.setToken(token);
                            SPUtils.putString(context, Constant.USER_TOKEN, token);
                            getUserInfo();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        getBaseView().faceTip("数据解析错误", Util.FACE_CALLBACE_PARSE_ERROR, "");
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);

                    getBaseView().faceTip("接口访问失败，稍后重试", Util.FACE_CALLBACE_ERROR, face_token);

                }
            });
        }
    }


    // 搜索人脸
    public void searchFace(final String face_token, String faceSet) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }
            final long startTime = System.currentTimeMillis();

            FaceHttp.searchFace(face_token, faceSet, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long costTime = System.currentTimeMillis() - startTime;
                        System.out.println("搜索时间：" + costTime + "\n" + o.toString());
                        FaceSearchInfo info = new Gson().fromJson(o.toString(), FaceSearchInfo.class);
                        FaceSearchInfo.ResultsBean resultsBean = info.getResults().get(0);
                        FaceSearchInfo.ThresholdsBean thresholdsBean = info.getThresholds();
                        double confidence = resultsBean.getConfidence();
                        String user_id = resultsBean.getUser_id();
                        String face_token = resultsBean.getFace_token();
                        // 置信度判断
                        if (confidence > thresholdsBean.get_$1e5()) {
                            // 为同一个人并且获取用户信息完成登录（参数user_id,face_token）
                            loginByFace(user_id, face_token);
                        } else {
                            // 不是同一个人（算没搜到）
                            getBaseView().faceTip("不是同一个人", Util.FACE_CALLBACE_NOT_SAME_PEOPLE, face_token);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        getBaseView().faceTip("数据解析错误", Util.FACE_CALLBACE_PARSE_ERROR, "");
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    // 未搜索到数据
                    getBaseView().faceTip("当前用户不存在", Util.FACE_CALLBACE_NOT_THE_PEOPLE, face_token);
                }
            });
        }
    }

    // 存储人脸
    public void saveFace(final String face_tokens, final String userId) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            final long startTime = System.currentTimeMillis();

            FaceHttp.saveFace(face_tokens, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long costTime = System.currentTimeMillis() - startTime;
                        System.out.println("存储人脸时间：" + costTime);
                        System.out.println(o.toString());
                        JSONObject jsonObject = (JSONObject) o;
                        try {
                            String error_message = jsonObject.getString("error_message");
                            Log.i("FaceHttp", error_message);
                        } catch (Exception e) {
                            int face_added = jsonObject.getInteger("face_added");
                            if (face_added == 1) {
                                // 添加成功
                                ToastUitls.toastMessage("添加成功" + userId);
                                // 一给当前用户设置UserId
                                setUserid(face_tokens, userId);
                                // 二保存用户（本地服务器）如果成功人脸登录/否二维码页面
                            } else {
                                // 添加失败
                                getBaseView().faceTip("添加人脸失败", Util.FACE_CALLBACE_ADD_FACE_FAILURE, face_tokens);
                            }
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

    // 创建人脸集合
    public void createFaceSet() {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            final long startTime = System.currentTimeMillis();

            FaceHttp.createFaceSet(new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long costTime = System.currentTimeMillis() - startTime;
                        System.out.println("创建人脸库时间：" + costTime);
                        System.out.println(o.toString());
                        JSONObject jsonObject = (JSONObject) o;
                        try {
                            String error_message = jsonObject.getString("error_message");
                            Log.i("FaceHttp", error_message);
                        } catch (Exception e) {
                            String facesetToken = jsonObject.getString("faceset_token");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    ToastUitls.toastMessage(errorCode + msg);
                }
            });
        }
    }

    // FaceSet 列表
    public void getFaceSets() {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }
            final long faceDetectTime_action = System.currentTimeMillis();

            FaceHttp.getfacesets(new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long algorithmTime = System.currentTimeMillis() - faceDetectTime_action;
                        System.out.println("搜索人脸库时间：" + algorithmTime);
                        System.out.println(o.toString());
                        JSONObject jsonObject = (JSONObject) o;
                        try {
                            String error_message = jsonObject.getString("error_message");
                            Log.i("FaceHttp", error_message);
                        } catch (Exception e) {

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

    // FaceSet详情
    public void getdetail() {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            FaceHttp.getdetail(new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        System.out.println(o.toString());
                        JSONObject jsonObject = (JSONObject) o;
                        try {
                            String error_message = jsonObject.getString("error_message");
                            Log.i("FaceHttp", error_message);
                        } catch (Exception e) {

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

    // 为检测出的某一个人脸添加标识信息，该信息会在Search接口结果中返回，用来确定用户身份。
    public void setUserid(String face_token, String userId) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }

            final long startTime = System.currentTimeMillis();
            System.out.println("设置用户ID：" + face_token);

            FaceHttp.setUserId(face_token, userId, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    long costTime = System.currentTimeMillis() - startTime;
                    System.out.println("设置用户Id时间：" + costTime);
                    JSONObject jsonObject = (JSONObject) o;
                    System.out.println(o.toString());
                    try {
                        String error_message = jsonObject.getString("error_message");
                        Log.i("FaceHttp", error_message);
                    } catch (Exception e) {
                        // 设置成功
                        // 调用本地服务器存储UserId
                        getBaseView().saveFaceOk();
                    }
                }

                @Override
                public void onResponse(Response httpResponse, String response, Headers headers) {
                    super.onResponse(httpResponse, response, headers);
                    System.out.println("设置用户:" + response);
                }

                @Override
                public void onResponse(String response, Headers headers) {
                    super.onResponse(response, headers);
                    System.out.println("设置用户:" + response);
                }

                @Override
                public void onFailure(int errorCode, String msg) {
                    super.onFailure(errorCode, msg);
                    ToastUitls.toastMessage(errorCode + msg);
                }
            });
        }
    }

    // 移除人脸数据
    public void removeFace() {

        // 判断网络状况
        if (com.tlh.android.utils.NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }
            final long startTime = System.currentTimeMillis();
            FaceHttp.removeFace(new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long costTime = System.currentTimeMillis() - startTime;
                        System.out.println("移除人脸数据时间：" + costTime);
                        System.out.println(o.toString());
                        JSONObject jsonObject = (JSONObject) o;
                        try {
                            String error_message = jsonObject.getString("error_message");
                            Log.i("FaceHttp", error_message);
                        } catch (Exception e) {

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUitls.toastMessage("接口访问成功异常信息：" + e.toString());
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

    // 创建新用户
    public void createUser(final String face_token) {

        // 判断网络状况
        if (com.tlh.android.utils.NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }
            System.out.println("创建新用户：" + face_token);
            final long startTime = System.currentTimeMillis();
            FaceHttp.createUserByFace(face_token, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long costTime = System.currentTimeMillis() - startTime;
                        System.out.println("创建用户时间：" + costTime);
                        System.out.println(o.toString());
                        JSONObject jsonObject = (JSONObject) o;
                        int code = jsonObject.getInteger("code");
                        String message = jsonObject.getString("message");
                        if (code == 0) {
                            String userId = jsonObject.getJSONObject("data").getString("userId");
                            getBaseView().getUserId(face_token, userId);
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

    // 人脸登录
    public void loginByFace(final String user_id, final String face_token) {

        // 判断网络状况
        if (NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }
            final long faceDetectTime_action = System.currentTimeMillis();

            FaceHttp.loginByFace(face_token, user_id, new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long algorithmTime = System.currentTimeMillis() - faceDetectTime_action;
                        System.out.println("登录时间：" + algorithmTime);
                        System.out.println(o.toString());
                        JSONObject jsonObject = (JSONObject) o;
                        jsonObject = jsonObject.getJSONObject("data");
                        String token = jsonObject.getString("token");
                        if (!TextUtils.isEmpty(token)) {
                            UserModel model = UserModel.getInstance();
                            model.setToken(token);
                            getUserInfo();
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

    // 获取用户详情
    public void getUserInfo() {

        // 判断网络状况
        if (com.tlh.android.utils.NetworkUtils.testNetworkStatus(context)) {

            if (!isViewAttached()) {
                return;
            }
            final long startTime = System.currentTimeMillis();
            DealHttp.getUserInfo(new BaseHttpRequestCallback() {
                @Override
                protected void onSuccess(Object o) {
                    super.onSuccess(o);
                    try {
                        long costTime = System.currentTimeMillis() - startTime;
                        System.out.println("获取用户详情：" + costTime);
                        System.out.println(o.toString());
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


