package com.zhangyuhuishou.zyhs.model;

public class FaceSetModel {

    /**
     * code : 0
     * message :
     * data : {"userFaceSetId":2,"telphoneGroup":"18668539","faceSetToken":"f768f078e56bf657210d6aa7c57a8238","createTime":"2018-11-29 16:01"}
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
         * userFaceSetId : 2
         * telphoneGroup : 18668539
         * faceSetToken : f768f078e56bf657210d6aa7c57a8238
         * createTime : 2018-11-29 16:01
         */

        private int userFaceSetId;
        private String telphoneGroup;
        private String faceSetToken;
        private String createTime;

        public int getUserFaceSetId() {
            return userFaceSetId;
        }

        public void setUserFaceSetId(int userFaceSetId) {
            this.userFaceSetId = userFaceSetId;
        }

        public String getTelphoneGroup() {
            return telphoneGroup;
        }

        public void setTelphoneGroup(String telphoneGroup) {
            this.telphoneGroup = telphoneGroup;
        }

        public String getFaceSetToken() {
            return faceSetToken;
        }

        public void setFaceSetToken(String faceSetToken) {
            this.faceSetToken = faceSetToken;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}
