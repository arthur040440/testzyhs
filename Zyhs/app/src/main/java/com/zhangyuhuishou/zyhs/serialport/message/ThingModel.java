package com.zhangyuhuishou.zyhs.serialport.message;

// 串口返回物品信息（类别重量）
public class ThingModel {

    private String sourceId;
    private int num;
    private boolean isShow;// 是否显示一些东西（比如优惠券弹窗）

    public ThingModel(String sourceId, int num){
        this.sourceId = sourceId;
        this.num = num;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    @Override
    public String toString() {
        return "ThingModel{" +
                "sourceId='" + sourceId + '\'' +
                ", num=" + num +
                ", isShow=" + isShow +
                '}';
    }
}