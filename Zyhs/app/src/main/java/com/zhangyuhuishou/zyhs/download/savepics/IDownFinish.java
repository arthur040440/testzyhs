package com.zhangyuhuishou.zyhs.download.savepics;

import com.zhangyuhuishou.zyhs.model.AdModel;
import com.zhangyuhuishou.zyhs.model.SkinModel;

import java.util.List;

/**
 * 接口调用成功回调方法
 *
 * @author tlh
 * @time 2016/12/28 14:22
 */
public interface IDownFinish {

    void overFinish(List<AdModel.DataBean> adList);

    void getSkinFinish(SkinModel.DataBean dataBean);

}
