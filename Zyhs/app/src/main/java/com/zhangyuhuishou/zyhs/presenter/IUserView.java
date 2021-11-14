package com.zhangyuhuishou.zyhs.presenter;

import com.zhangyuhuishou.zyhs.base.BaseView;

public interface IUserView extends BaseView {
    void roleDistribute(String roleNames);// 角色分配

    void outOfCurrentArea(String tips); //运维清运人员不在当前区域内
}
