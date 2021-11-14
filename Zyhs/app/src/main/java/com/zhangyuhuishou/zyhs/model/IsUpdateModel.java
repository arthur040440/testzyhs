package com.zhangyuhuishou.zyhs.model;

/**
 * 作者:created by author:tlh
 * 日期:2019/2/27 19:51
 * 邮箱:tianlihui2234@live.com
 * 描述:是否升级（1应用升级2广告升级3点位升级）
 */
public class IsUpdateModel {

    private boolean isOk;
    private int type;

    public IsUpdateModel(boolean isOk, int type){
        this.isOk = isOk;
        this.type = type;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
