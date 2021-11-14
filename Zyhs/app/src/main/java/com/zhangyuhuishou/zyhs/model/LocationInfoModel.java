package com.zhangyuhuishou.zyhs.model;

// 点位信息
public class LocationInfoModel {

    public String id;
    public String name;

    public LocationInfoModel(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
