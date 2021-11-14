package com.zhangyuhuishou.zyhs.model;

/**
 * 作者:created by author:tlh
 * 日期:2018/11/19 9:34
 * 邮箱:tianlihui2234@live.com
 * 描述:升级bean
 */
public class UpdateModel {

    /**
     * code : 0
     * message :
     * data : {"id":"18","version":"1.0.2","filePath":"apk/1.0.2-1542360487928.apk","fileUrl":"http://192.168.1.169:7001/public/upload/apk/1.0.2-1542360487928.apk","remark":"","expectedTime":"","createTime":"2018-11-16 15:00"}
     */

    private int code;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 18
         * version : 1.0.2
         * filePath : apk/1.0.2-1542360487928.apk
         * fileUrl : http://192.168.1.169:7001/public/upload/apk/1.0.2-1542360487928.apk
         * remark :
         * expectedTime :
         * createTime : 2018-11-16 15:00
         */

        private String id;
        private String version;
        private String filePath;
        private String fileUrl;
        private String remark;
        private String expectedTime;
        private String createTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
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

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getExpectedTime() {
            return expectedTime;
        }

        public void setExpectedTime(String expectedTime) {
            this.expectedTime = expectedTime;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}
