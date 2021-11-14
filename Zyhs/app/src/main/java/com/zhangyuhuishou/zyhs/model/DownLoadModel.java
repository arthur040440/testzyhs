package com.zhangyuhuishou.zyhs.model;

/**
 * 作者:created by author:tlh
 * 日期:2018/11/29 16:45
 * 邮箱:tianlihui2234@live.com
 * 描述:下载文件
 */
public class DownLoadModel {

    private int progress;
    private String tip;
    private boolean isDissmiss;

    public DownLoadModel() {
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public boolean isDissmiss() {
        return isDissmiss;
    }

    public void setDissmiss(boolean dissmiss) {
        isDissmiss = dissmiss;
    }
}
