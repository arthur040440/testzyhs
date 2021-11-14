package com.zhangyuhuishou.zyhs.model;

/**
 * 作者:created by author:tlh
 * 日期:2018/11/13 16:03
 * 邮箱:tianlihui2234@live.com
 * 描述:二维码/超时
 */
public class QrTimeModel {

    private String qrCode;
    private int timeout;

    public QrTimeModel(String qrCode,int timeout){
        this.qrCode = qrCode;
        this.timeout = timeout;
    }

    public String getQrCode() {
        return qrCode;
    }

    public int getTimeout() {
        return timeout;
    }
}
