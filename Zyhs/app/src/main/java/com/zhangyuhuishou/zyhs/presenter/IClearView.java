package com.zhangyuhuishou.zyhs.presenter;

import com.zhangyuhuishou.zyhs.base.BaseView;

public interface IClearView extends BaseView {

    void clearSuccessCallback();

    void clearNetExceptionCallback(int flag);
}
