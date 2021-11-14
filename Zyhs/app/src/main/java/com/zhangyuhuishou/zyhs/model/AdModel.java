package com.zhangyuhuishou.zyhs.model;

import java.io.Serializable;
import java.util.List;

/**
 * 作者:created by author:tlh
 * 日期:2019/1/29 15:32
 * 邮箱:tianlihui2234@live.com
 * 描述:广告模型
 */

public class AdModel {

    /**
     * code : 0
     * message :
     * data : [{"adId":"3","adDetailId":"19","filePath":"http://zyhs-ad.oss-cn-hangzhou.aliyuncs.com/local/ic_banner_company-1548746296296.jpg","fileUrl":"http://zyhs-ad.oss-cn-hangzhou.aliyuncs.com/local/ic_banner_company-1548746296296.jpg","duration":15},{"adId":"3","adDetailId":"20","filePath":"http://zyhs-ad.oss-cn-hangzhou.aliyuncs.com/local/ic_banner_guide-1548746334960.jpg","fileUrl":"http://zyhs-ad.oss-cn-hangzhou.aliyuncs.com/local/ic_banner_guide-1548746334960.jpg","duration":10},{"adId":"3","adDetailId":"21","filePath":"http://zyhs-ad.oss-cn-hangzhou.aliyuncs.com/local/ic_banner_company_jb-1548746376799.jpg","fileUrl":"http://zyhs-ad.oss-cn-hangzhou.aliyuncs.com/local/ic_banner_company_jb-1548746376799.jpg","duration":2}]
     */

    private int code;
    private String message;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * adId : 3
         * adDetailId : 19
         * filePath : http://zyhs-ad.oss-cn-hangzhou.aliyuncs.com/local/ic_banner_company-1548746296296.jpg
         * fileUrl : http://zyhs-ad.oss-cn-hangzhou.aliyuncs.com/local/ic_banner_company-1548746296296.jpg
         * duration : 15
         */

        private int id;
        private String adId;
        private String adDetailId;
        private String filePath;
        private String fileUrl;
        private int duration;
        private String md5Name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getAdId() {
            return adId;
        }

        public void setAdId(String adId) {
            this.adId = adId;
        }

        public String getAdDetailId() {
            return adDetailId;
        }

        public void setAdDetailId(String adDetailId) {
            this.adDetailId = adDetailId;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getMd5Name() {
            return md5Name;
        }

        public void setMd5Name(String md5Name) {
            this.md5Name = md5Name;
        }
    }
}
