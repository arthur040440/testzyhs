package com.zhangyuhuishou.zyhs.pcb;

public class PcbUpdateModel {

    private boolean isUpdate;
    private String machineAddress;

    public PcbUpdateModel(boolean isUpdate,String machineAddress){
        this.isUpdate = isUpdate;
        this.machineAddress = machineAddress;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public String getMachineAddress() {
        return machineAddress;
    }

    public void setMachineAddress(String machineAddress) {
        this.machineAddress = machineAddress;
    }
}
