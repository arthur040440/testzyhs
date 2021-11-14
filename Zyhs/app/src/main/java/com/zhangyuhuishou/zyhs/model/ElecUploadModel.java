package com.zhangyuhuishou.zyhs.model;

/**
 * 作者:created by author:tlh
 * 日期:2019/3/28 10:47
 * 邮箱:tianlihui2234@live.com
 * 描述:上传电表数据模型
 */

public class ElecUploadModel {

   private boolean isUploadOk;

   public ElecUploadModel(boolean isUploadOk){
       this.isUploadOk = isUploadOk;
   }

    public boolean isUploadOk() {
        return isUploadOk;
    }

    public void setUploadOk(boolean uploadOk) {
        isUploadOk = uploadOk;
    }
}
