package com.zhangyuhuishou.zyhs.model;

public class ClearMissModel {

    private int id;
    private String rangeId;
    private String terminalId;
    private String rangeLogType;
    private String content;
    private String oprSysUserId;

    public String getRangeId() {
        return rangeId;
    }

    public void setRangeId(String rangeId) {
        this.rangeId = rangeId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getRangeLogType() {
        return rangeLogType;
    }

    public void setRangeLogType(String rangeLogType) {
        this.rangeLogType = rangeLogType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOprSysUserId() {
        return oprSysUserId;
    }

    public void setOprSysUserId(String oprSysUserId) {
        this.oprSysUserId = oprSysUserId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
