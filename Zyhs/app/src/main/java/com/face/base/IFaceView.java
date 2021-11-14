package com.face.base;

import com.face.bean.FaceppBean;
import com.zhangyuhuishou.zyhs.base.BaseView;

public interface IFaceView extends BaseView {

    void faceTip(String faceTipMessage, int callBaceCode, String face_token);

    void loginSuccess();// 登录成功

    void detectSuccess(FaceppBean.FacesBean bean);// 检测成功

    void saveFaceOk();// 存储人脸成功

    void getUserId(String face_token, String userId);// 获取用户id

    void inputPhoneNumber(String face_token, String message);//当前人脸库没有此人脸信息，需输入手机号码验证

}
