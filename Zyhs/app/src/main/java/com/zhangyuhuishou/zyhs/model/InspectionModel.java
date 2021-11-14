package com.zhangyuhuishou.zyhs.model;

public class InspectionModel {

    private int id;
    private String rangeId;
    private String token;
    private String remark;
    private String report;

    public InspectionModel() {
    }

    public InspectionModel(int id, String rangeId, String token, String remark, String report) {
        this.id = id;
        this.rangeId = rangeId;
        this.token = token;
        this.remark = remark;
        this.report = report;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRangeId() {
        return rangeId;
    }

    public void setRangeId(String rangeId) {
        this.rangeId = rangeId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    @Override
    public String toString() {
        return "InspectionModel{" +
                "id='" + id + '\'' +
                ", rangeId='" + rangeId + '\'' +
                ", token='" + token + '\'' +
                ", remark='" + remark + '\'' +
                ", report='" + report + '\'' +
                '}';
    }
}
