package com.zhangyuhuishou.zyhs.base;


/**
 * 作者: create by tlh on 2018/6/14 14:01
 * 邮箱: tianlihui2234@live.com
 * 描述:
 */
public class BasePresenter<T extends BaseView> {

    private T mBaseView;

    public void attachView(T baseView){
        this.mBaseView = baseView;
    }

    public void detachView(){
        this.mBaseView = null;
    }

    public boolean isViewAttached(){
        return this.mBaseView != null;
    }

    public T getBaseView(){
        return this.mBaseView;
    }

}
