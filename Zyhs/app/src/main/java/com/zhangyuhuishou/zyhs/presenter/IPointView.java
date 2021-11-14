package com.zhangyuhuishou.zyhs.presenter;

import com.zhangyuhuishou.zyhs.base.BaseView;
import com.zhangyuhuishou.zyhs.model.PointModel;
import com.zhangyuhuishou.zyhs.model.RangeInfoModel;
import com.zhangyuhuishou.zyhs.model.TerminalModel;

import java.util.List;

public interface IPointView extends BaseView {
    void getPoints(List<PointModel.DataBean> points);// 点位列表

    void activePoint(PointModel.DataBean model);// 激活点位

    void updatePoint(RangeInfoModel model);// 更新点位

    void clearError(String message);// 清理出错

    void clearSuccess(TerminalModel terminalModel);// 清理数据



}
