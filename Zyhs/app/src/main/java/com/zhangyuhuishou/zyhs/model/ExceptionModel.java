package com.zhangyuhuishou.zyhs.model;

public class ExceptionModel {

    private String terminalId;
    private String des;

    public ExceptionModel(String terminalId, String des) {
        this.terminalId = terminalId;
        this.des = des;

    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
