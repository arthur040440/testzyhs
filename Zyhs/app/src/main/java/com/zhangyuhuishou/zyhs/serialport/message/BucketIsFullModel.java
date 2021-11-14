package com.zhangyuhuishou.zyhs.serialport.message;

//  倒水桶的状态（是否满了）
public class BucketIsFullModel {

    private boolean isFull;
    private String terminalId;

    public BucketIsFullModel( boolean isFull,String terminalId){
        this.isFull = isFull;
        this.terminalId = terminalId;
    }

    public boolean isFull() {
        return isFull;
    }

    public String getTerminalId() {
        return terminalId;
    }
}