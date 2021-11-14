package com.face.http;

import android.util.Log;

import com.face.util.Util;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.SPUtils;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.RequestParams;
import okhttp3.CacheControl;

public class FaceHttp {

    // 检测人脸
    public static void detectFace(final String image_base64, final BaseHttpRequestCallback callback) {
        RequestParams params = new RequestParams();
        params.setCacheControl(CacheControl.FORCE_NETWORK);
        params.addFormDataPart("api_key", Util.API_KEY);
        params.addFormDataPart("api_secret", Util.API_SECRET);
        params.addFormDataPart("image_base64", image_base64);
        params.addFormDataPart("return_landmark", 0);
        params.addFormDataPart("return_attributes", "gender,age,smiling,headpose,facequality,blur,eyestatus,beauty,mouthstatus");
        HttpRequest.post(Util.CN_DETECT_URL, params, callback);
    }

    // 储存人脸
    public static void saveFace(final String face_tokens, final BaseHttpRequestCallback callback) {
        RequestParams params = new RequestParams();
        params.setCacheControl(CacheControl.FORCE_NETWORK);
        params.addFormDataPart("api_key", Util.API_KEY);
        params.addFormDataPart("api_secret", Util.API_SECRET);
        params.addFormDataPart("outer_id", "byzyhs");
        params.addFormDataPart("face_tokens", face_tokens);
        HttpRequest.post(Util.CN_ADD_FACE_URL, params, callback);
    }

    // 搜索人脸
    public static void searchFace(final String face_token, final String faceSet, final BaseHttpRequestCallback callback) {
        RequestParams params = new RequestParams();
        params.setCacheControl(CacheControl.FORCE_NETWORK);
        params.addFormDataPart("api_key", Util.API_KEY);
        params.addFormDataPart("api_secret", Util.API_SECRET);
        params.addFormDataPart("face_token", face_token);
//        params.addFormDataPart("outer_id", "byzyhs");
        params.addFormDataPart("faceset_token", faceSet);
        HttpRequest.post(Util.CN_SERACH_URL, params, callback);
    }


    // 当前小区是否有此人脸
    public static void loginByFaceOfCommunity(final String face_token, String communityId, final BaseHttpRequestCallback callback) {
        RequestParams params = new RequestParams();
        params.setCacheControl(CacheControl.FORCE_NETWORK);
        params.addFormDataPart("faceToken", face_token);
        params.addFormDataPart("communityId", communityId);
        HttpRequest.post(Constant.BASE_URL_NEW + Constant.FACECOMMUNITY, params, callback);
    }


    // 创建faceSet
    public static void createFaceSet(final BaseHttpRequestCallback callback) {
        RequestParams params = new RequestParams();
        params.setCacheControl(CacheControl.FORCE_NETWORK);
        params.addFormDataPart("api_key", Util.API_KEY);
        params.addFormDataPart("api_secret", Util.API_SECRET);
        params.addFormDataPart("display_name", "byzyhs");
        params.addFormDataPart("outer_id", "byzyhs");
        HttpRequest.post(Util.CN_CREATE_URL, params, callback);
    }

    // 获取某一 API Key 下的 FaceSet 列表及其 faceset_token、outer_id、display_name 和 tags 等信息。
    public static void getfacesets(final BaseHttpRequestCallback callback) {
        RequestParams params = new RequestParams();
        params.setCacheControl(CacheControl.FORCE_NETWORK);
        params.addFormDataPart("api_key", Util.API_KEY);
        params.addFormDataPart("api_secret", Util.API_SECRET);
        params.addFormDataPart("tags", "byzyhs");
        HttpRequest.post(Util.CN_GET_FACESETS_URL, params, callback);
    }

    // 获取一个 FaceSet 的所有信息
    public static void getdetail(final BaseHttpRequestCallback callback) {
        RequestParams params = new RequestParams();
        params.setCacheControl(CacheControl.FORCE_NETWORK);
        params.addFormDataPart("api_key", Util.API_KEY);
        params.addFormDataPart("api_secret", Util.API_SECRET);
        params.addFormDataPart("outer_id", "byzyhs");
        HttpRequest.post(Util.CN_GET_DETAIL_URL, params, callback);
    }

    // 为检测出的某一个人脸添加标识信息，该信息会在Search接口结果中返回，用来确定用户身份。
    public static void setUserId(String face_token, String user_id, final BaseHttpRequestCallback callback) {
        RequestParams params = new RequestParams();
        params.setCacheControl(CacheControl.FORCE_NETWORK);
        params.addFormDataPart("api_key", Util.API_KEY);
        params.addFormDataPart("api_secret", Util.API_SECRET);
        params.addFormDataPart("face_token", face_token);
        params.addFormDataPart("user_id", user_id);
        HttpRequest.post(Util.CN_SET_USER_ID_URL, params, callback);
    }


    // 移除一个FaceSet中的某些或者全部face_token
    public static void removeFace(final BaseHttpRequestCallback callback) {
        RequestParams params = new RequestParams();
        params.setCacheControl(CacheControl.FORCE_NETWORK);
        params.addFormDataPart("api_key", Util.API_KEY);
        params.addFormDataPart("api_secret", Util.API_SECRET);
        params.addFormDataPart("outer_id", "byzyhs");
        params.addFormDataPart("face_tokens", "RemoveAllFaceTokens");
        HttpRequest.post(Util.CN_REMOVE_FACE_URL, params, callback);
    }

    // 通过旷世人脸创建新用户
    public static void createUserByFace(String face_token, final BaseHttpRequestCallback callback) {
        RequestParams params = new RequestParams();
        params.setCacheControl(CacheControl.FORCE_NETWORK);
        params.addFormDataPart("faceToken", face_token);
        HttpRequest.post(Constant.BASE_URL_NEW + Constant.CREATE_USER, params, callback);
    }

    // 通过旷世人脸登录
    public static void loginByFace(String face_token, String userId, final BaseHttpRequestCallback callback) {
        RequestParams params = new RequestParams();
        params.setCacheControl(CacheControl.FORCE_NETWORK);
        params.addFormDataPart("faceToken", face_token);
        params.addFormDataPart("userId", userId);
        HttpRequest.post(Constant.BASE_URL_NEW + Constant.LOGIN_BY_FACE, params, callback);
    }

    // 获取人脸库
    public static void getFaceSet(String telphone, final BaseHttpRequestCallback callback) {
        RequestParams params = new RequestParams();
        params.setCacheControl(CacheControl.FORCE_NETWORK);
        params.addFormDataPart("telphone", telphone);
        HttpRequest.post(Constant.BASE_URL_NEW + Constant.GET_FACESET, params, callback);
    }

    // 手机号码是否和人脸绑定/手机号码绑定的人脸是否和登录人脸匹配
    public static void loginByFaceOfTelphone(String faceToken, String communityId, String telphone, final BaseHttpRequestCallback callback) {
        RequestParams params = new RequestParams();
        params.setCacheControl(CacheControl.FORCE_NETWORK);
        params.addFormDataPart("faceToken", faceToken);
        params.addFormDataPart("communityId", communityId);
        params.addFormDataPart("telphone", telphone);
        HttpRequest.post(Constant.BASE_URL_NEW + Constant.FACETELEPHONE, params, callback);
    }

}
