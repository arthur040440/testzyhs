package com.zhangyuhuishou.zyhs.presenter;

import com.zhangyuhuishou.zyhs.base.BaseView;

public interface IYintaiCouponsView extends BaseView {

    void getYintaiCoupons(String couponFileUrl, boolean isRequestCoupons);// 获取优惠券

}
