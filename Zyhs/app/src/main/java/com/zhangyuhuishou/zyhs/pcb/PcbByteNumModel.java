package com.zhangyuhuishou.zyhs.pcb;

public class PcbByteNumModel {

    private String byteNum;
    private String machineAddress;

    public PcbByteNumModel( String byteNum,String machineAddress){
        this.byteNum = byteNum;
        this.machineAddress =  machineAddress;
    }

    public String getByteNum() {
        return byteNum;
    }

    public void setByteNum(String byteNum) {
        this.byteNum = byteNum;
    }

    public String getMachineAddress() {
        return machineAddress;
    }

    public void setMachineAddress(String machineAddress) {
        this.machineAddress = machineAddress;
    }
}
