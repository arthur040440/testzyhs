package com.zhangyuhuishou.zyhs.banner;

import com.zhangyuhuishou.zyhs.base.BaseView;
import com.zhangyuhuishou.zyhs.model.AdModel;
import java.util.List;

public interface IBannerView extends BaseView{
    void showBannerData(List<AdModel.DataBean> bannerModels);
}
