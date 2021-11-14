package com.zhangyuhuishou.zyhs.model;

/**
 * 作者:created by author:tlh
 * 日期:2018/11/27 16:56
 * 邮箱:tianlihui2234@live.com
 * 描述:硬件设备
 */
public class TerminalModel {

    private int id;// 设备默认序号
    private String terminalId;// 设备id
    private String terminalNum;// 设备编号
    private String terminalTypeId;// 设备类型
    private String terminalTypeName;// 设备类型名称
    private int weight;// 当前设备下的数据
    private String ID_D;
    private int countException;//设备红外线计数遮挡异常


    public int getCountException() {
        return countException;
    }

    public void setCountException(int countException) {
        this.countException = countException;
    }

    public TerminalModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public String getTerminalTypeId() {
        return terminalTypeId;
    }

    public String getTerminalTypeName() {
        return terminalTypeName;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public void setTerminalTypeId(String terminalTypeId) {
        this.terminalTypeId = terminalTypeId;
    }

    public void setTerminalTypeName(String terminalTypeName) {
        this.terminalTypeName = terminalTypeName;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getID_D() {
        return ID_D;
    }

    public void setID_D(String ID_D) {
        this.ID_D = ID_D;
    }

    public String getTerminalNum() {
        return terminalNum;
    }

    public void setTerminalNum(String terminalNum) {
        this.terminalNum = terminalNum;
    }

    @Override
    public String toString() {
        return "TerminalModel{" +
                "id=" + id +
                ", terminalId='" + terminalId + '\'' +
                ", terminalNum='" + terminalNum + '\'' +
                ", terminalTypeId='" + terminalTypeId + '\'' +
                ", terminalTypeName='" + terminalTypeName + '\'' +
                ", weight=" + weight +
                ", ID_D='" + ID_D + '\'' +
                ", countException=" + countException +
                '}';
    }
}
