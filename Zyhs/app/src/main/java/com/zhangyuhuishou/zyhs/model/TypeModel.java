package com.zhangyuhuishou.zyhs.model;

public class TypeModel {

    private int devType;
    private int devNum;
    private double tv_num;

    public int getDevType() {
        return devType;
    }

    public void setDevType(int devType) {
        this.devType = devType;
    }

    public int getDevNum() {
        return devNum;
    }

    public void setDevNum(int devNum) {
        this.devNum = devNum;
    }

    public double getTv_num() {
        return tv_num;
    }

    public void setTv_num(double tv_num) {
        this.tv_num = tv_num;
    }

    @Override
    public String toString() {
        return "TypeModel{" +
                "devType=" + devType +
                ", devNum=" + devNum +
                ", tv_num=" + tv_num +
                '}';
    }
}
