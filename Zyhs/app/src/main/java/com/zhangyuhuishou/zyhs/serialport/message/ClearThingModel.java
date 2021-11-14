package com.zhangyuhuishou.zyhs.serialport.message;

//  维护面门关闭
public class ClearThingModel {

    private String IDS;

    public ClearThingModel(String IDS){
        this.IDS = IDS;
    }

    public String getSourceId() {
        return IDS;
    }
}