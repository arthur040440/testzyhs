package com.zhangyuhuishou.zyhs.database;

public class BucketStatusModel {

    private int id;
    private String COLUMN_ID;
    private int COLUMN_RESP_STA;
    private int COLUMN_WEIGHT_STA;
    private int COLUMN_REC_BUCKET_STA;
    private int COLUMN_REC_GATE_STA;
    private int COLUMN_MAIN_DOOR_STA;
    private int COLUMN_LED_BOX_STA;
    private int COLUMN_FAN_STA;
    private int COLUMN_DEGERM_STA;
    private int COLUMN_BUCKET_STA;
    private int COLUMN_EQU_FAULT_STA;
    private String version;


    public BucketStatusModel() {
    }


    public String getCOLUMN_ID() {
        return COLUMN_ID;
    }

    public void setCOLUMN_ID(String COLUMN_ID) {
        this.COLUMN_ID = COLUMN_ID;
    }

    public int getCOLUMN_RESP_STA() {
        return COLUMN_RESP_STA;
    }

    public void setCOLUMN_RESP_STA(int COLUMN_RESP_STA) {
        this.COLUMN_RESP_STA = COLUMN_RESP_STA;
    }

    public int getCOLUMN_WEIGHT_STA() {
        return COLUMN_WEIGHT_STA;
    }

    public void setCOLUMN_WEIGHT_STA(int COLUMN_WEIGHT_STA) {
        this.COLUMN_WEIGHT_STA = COLUMN_WEIGHT_STA;
    }

    public int getCOLUMN_REC_GATE_STA() {
        return COLUMN_REC_GATE_STA;
    }

    public void setCOLUMN_REC_GATE_STA(int COLUMN_REC_GATE_STA) {
        this.COLUMN_REC_GATE_STA = COLUMN_REC_GATE_STA;
    }

    public int getCOLUMN_MAIN_DOOR_STA() {
        return COLUMN_MAIN_DOOR_STA;
    }

    public void setCOLUMN_MAIN_DOOR_STA(int COLUMN_MAIN_DOOR_STA) {
        this.COLUMN_MAIN_DOOR_STA = COLUMN_MAIN_DOOR_STA;
    }

    public int getCOLUMN_LED_BOX_STA() {
        return COLUMN_LED_BOX_STA;
    }

    public void setCOLUMN_LED_BOX_STA(int COLUMN_LED_BOX_STA) {
        this.COLUMN_LED_BOX_STA = COLUMN_LED_BOX_STA;
    }

    public int getCOLUMN_FAN_STA() {
        return COLUMN_FAN_STA;
    }

    public void setCOLUMN_FAN_STA(int COLUMN_FAN_STA) {
        this.COLUMN_FAN_STA = COLUMN_FAN_STA;
    }

    public int getCOLUMN_DEGERM_STA() {
        return COLUMN_DEGERM_STA;
    }

    public void setCOLUMN_DEGERM_STA(int COLUMN_DEGERM_STA) {
        this.COLUMN_DEGERM_STA = COLUMN_DEGERM_STA;
    }

    public int getCOLUMN_BUCKET_STA() {
        return COLUMN_BUCKET_STA;
    }

    public int getCOLUMN_REC_BUCKET_STA() {
        return COLUMN_REC_BUCKET_STA;
    }

    public void setCOLUMN_REC_BUCKET_STA(int COLUMN_REC_BUCKET_STA) {
        this.COLUMN_REC_BUCKET_STA = COLUMN_REC_BUCKET_STA;
    }

    public void setCOLUMN_BUCKET_STA(int COLUMN_BUCKET_STA) {
        this.COLUMN_BUCKET_STA = COLUMN_BUCKET_STA;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getCOLUMN_EQU_FAULT_STA() {
        return COLUMN_EQU_FAULT_STA;
    }

    public void setCOLUMN_EQU_FAULT_STA(int COLUMN_EQU_FAULT_STA) {
        this.COLUMN_EQU_FAULT_STA = COLUMN_EQU_FAULT_STA;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


}
