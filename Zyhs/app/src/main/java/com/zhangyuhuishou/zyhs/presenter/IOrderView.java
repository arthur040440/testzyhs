package com.zhangyuhuishou.zyhs.presenter;

import com.zhangyuhuishou.zyhs.base.BaseView;
import com.zhangyuhuishou.zyhs.model.ClassificationModel;
import com.zhangyuhuishou.zyhs.model.OrderDetailModel;
import java.util.List;

public interface IOrderView extends BaseView {

    void showOrderDetail(OrderDetailModel model);// 订单明细

    void getRecyclerPrice(List<ClassificationModel.DataBean> data);// 获取回收物价格
}
