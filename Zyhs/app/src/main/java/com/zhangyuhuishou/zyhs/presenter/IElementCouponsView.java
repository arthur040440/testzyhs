package com.zhangyuhuishou.zyhs.presenter;

import com.zhangyuhuishou.zyhs.base.BaseView;
import com.zhangyuhuishou.zyhs.serialport.message.ThingModel;

public interface IElementCouponsView extends BaseView {


    void activityJoin(boolean isShowCouponsDialog, ThingModel model);// 礼品兑换接口回调

}
