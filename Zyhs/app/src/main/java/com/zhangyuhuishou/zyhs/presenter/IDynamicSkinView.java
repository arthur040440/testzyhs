package com.zhangyuhuishou.zyhs.presenter;

import com.zhangyuhuishou.zyhs.base.BaseView;
import com.zhangyuhuishou.zyhs.model.SkinModel;

public interface IDynamicSkinView extends BaseView {

    //获取动态皮肤图片url
    void getSkinSource(SkinModel.DataBean dataBean, boolean getSkinUrl);
}
