package com.zhangyuhuishou.zyhs.serialport.message;

// 关门
public class CloseDoorModel {

    private String terminalId;
    private int status;
    private String des;
    private String sourceId;

    public CloseDoorModel(String terminalId){
        this.terminalId = terminalId;
    }

    public CloseDoorModel(String terminalId, int status, String des){
        this.terminalId = terminalId;
        this.status = status;
        this.des = des;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public int getStatus() {
        return status;
    }

    public String getDes() {
        return des;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Override
    public String toString() {
        return "CloseDoorModel{" +
                "terminalId='" + terminalId + '\'' +
                ", status=" + status +
                ", des='" + des + '\'' +
                ", sourceId='" + sourceId + '\'' +
                '}';
    }
}