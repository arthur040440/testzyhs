package com.face.base;

import com.zhangyuhuishou.zyhs.base.BaseView;

public interface IFaceSetView extends BaseView {

    void faceTip(String message);// 提示

    void faceSetToken(String faceset_token);// faceSetToken

    void errorTip(String message);// 错误提示

    void loginSuccess();// 登录成功

}
