package com.zhangyuhuishou.zyhs.model;

public class DeliveryTipModel {
    private String tips;
    private String terminalId;


    public DeliveryTipModel(String tips, String terminalId) {
        this.tips = tips;
        this.terminalId = terminalId;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    @Override
    public String toString() {
        return "DeliveryTipModel{" +
                "tips='" + tips + '\'' +
                ", terminalId='" + terminalId + '\'' +
                '}';
    }
}
