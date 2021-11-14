package com.zhangyuhuishou.zyhs.base;

import android.content.Context;

/**
 * 作者: create by tlh on 2018/6/14  13:59
 * 邮箱: tianlihui2234@live.com
 * 描述:
 */
public interface BaseView {

    /**
     * 显示正在加载view
     */
    void showLoading();

    /**
     * 关闭正在加载view
     */
    void hideLoading();
    /**
     * 显示提示
     * @param msg
     */

    void showToast(String msg);
    /**
     * 显示请求错误提示
     */

    void showErr();
    /**
     * 获取上下文
     * @return 上下文
     */
    Context getContext();
}
