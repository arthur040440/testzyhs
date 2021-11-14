package com.tlh.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.tlh.android.config.Constant;
import com.zhangyuhuishou.zyhs.actys.HomeActivity;
import com.zhangyuhuishou.zyhs.actys.MaintainPeopleActivity;
import com.zhangyuhuishou.zyhs.actys.NextStepActivity;
import com.zhangyuhuishou.zyhs.actys.ThrowThingsActivity;
import com.zhangyuhuishou.zyhs.model.UserModel;

// 登录相关
public class LoginUtils {

    // 退出登录
    public static void logout(Context context) {

        System.out.println("数据被清空了");
        // 登录信息置空
        UserModel model = UserModel.getInstance();
        model.setId("");
        model.setNickName("");
        model.setToken("");
        model.setIntegral(0);
        model.setProfilePhoto("");
        SPUtils.putString(context, Constant.USER_TOKEN, "");

        ((Activity) context).finish();// 结束当前页面

        // 跳转到登录页面
        if (context instanceof HomeActivity) {
            return;
        }
        if (context instanceof ThrowThingsActivity) {
            return;
        }
        if (context instanceof NextStepActivity) {
            return;
        }
        if (context instanceof MaintainPeopleActivity) {
            return;
        }


        context.startActivity(new Intent(context, HomeActivity.class));
    }

}
