package com.zhangyuhuishou.zyhs.model;

/**
 * 作者:created by author:tlh
 * 日期:2018/11/29 16:45
 * 邮箱:tianlihui2234@live.com
 * 描述:旷世人脸是否授权成功
 */
public class AuthModel {

    private boolean isAuth;

    public AuthModel() {
    }

    public AuthModel(boolean isAuth){
        this.isAuth = isAuth;
    }

    public boolean getIsAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        isAuth = auth;
    }
}
