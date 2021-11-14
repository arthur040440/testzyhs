package com.zhangyuhuishou.zyhs.banner;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者:created by author:Administrator
 * 日期:2018/7/29 002916:53
 * 邮箱:tianlihui2234@live.com
 * 描述:
 */
public class BannerViewAdapter extends PagerAdapter {

    private List<View> listBean;

    public BannerViewAdapter(List<View> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.listBean = list;
    }

    public void setDataList(List<View> list) {
        if (list != null && list.size() > 0) {
            this.listBean = list;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = listBean.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return listBean.size();
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
