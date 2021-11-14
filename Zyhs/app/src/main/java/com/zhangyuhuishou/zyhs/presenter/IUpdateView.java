package com.zhangyuhuishou.zyhs.presenter;

import com.zhangyuhuishou.zyhs.base.BaseView;
import com.zhangyuhuishou.zyhs.model.UpdateModel;
import com.zhangyuhuishou.zyhs.pcb.PcbModel;

public interface IUpdateView extends BaseView{

    void updateInfo(UpdateModel model);// 升级信息反馈
    void updateErrorInfo(String message);// 升级错误信息反馈
    void updatePcbInfo(PcbModel model);// pcb升级信息反馈
    void downPcbOk(boolean isOk);// 下载二进制文件是否成功

}
