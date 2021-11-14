package com.zhangyuhuishou.zyhs.model;

/**
 * 作者:created by author:tlh
 * 日期:2018/11/21 9:53
 * 邮箱:tianlihui2234@live.com
 * 描述:用户数据
 */
public class UserBean {

    /**
     * code : 0
     * data : {"age":0,"area":"","city":"","createTime":"2018-08-18 13:39","experience":32827,"experienceCount":0,"faceId":"1","integral":8712593,"integralCount":0,"level":0,"loginCount":0,"modifyTime":"2018-11-20 18:25","name":"","nickName":"阿啸จุ๊บ","orderCount":585,"paypwd":"","profilePhoto":"https://wx.qlogo.cn/mmopen/vi_32/4BqTO1sHPXYR8fSU3re2SCzQtPdeyEtGmicPFgVaKmq7HibOscSDjHO7gdZpx2bN2aa8bSgHzVT9B0p8AOrUH85w/132","province":"","sex":"","status":"0","statusName":"未使用","telphone":"18668539268","userChannels":"WECHATMINI","userId":"7897A113E7A143E2B0FDF99E9A1B8363","userName":""}
     * message :
     */

    private int code;
    private DataBean data;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean {
        /**
         * age : 0
         * area :
         * city :
         * createTime : 2018-08-18 13:39
         * experience : 32827
         * experienceCount : 0
         * faceId : 1
         * integral : 8712593
         * integralCount : 0
         * level : 0
         * loginCount : 0
         * modifyTime : 2018-11-20 18:25
         * name :
         * nickName : 阿啸จุ๊บ
         * orderCount : 585
         * paypwd :
         * profilePhoto : https://wx.qlogo.cn/mmopen/vi_32/4BqTO1sHPXYR8fSU3re2SCzQtPdeyEtGmicPFgVaKmq7HibOscSDjHO7gdZpx2bN2aa8bSgHzVT9B0p8AOrUH85w/132
         * province :
         * sex :
         * status : 0
         * statusName : 未使用
         * telphone : 18668539268
         * userChannels : WECHATMINI
         * userId : 7897A113E7A143E2B0FDF99E9A1B8363
         * userName :
         */

        private int age;
        private String area;
        private String city;
        private String createTime;
        private int experience;
        private int experienceCount;
        private String faceId;
        private int integral;
        private int integralCount;
        private int level;
        private int loginCount;
        private String modifyTime;
        private String name;
        private String nickName;
        private int orderCount;
        private String paypwd;
        private String profilePhoto;
        private String province;
        private String sex;
        private String status;
        private String statusName;
        private String telphone;
        private String userChannels;
        private String userId;
        private String userName;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getExperience() {
            return experience;
        }

        public void setExperience(int experience) {
            this.experience = experience;
        }

        public int getExperienceCount() {
            return experienceCount;
        }

        public void setExperienceCount(int experienceCount) {
            this.experienceCount = experienceCount;
        }

        public String getFaceId() {
            return faceId;
        }

        public void setFaceId(String faceId) {
            this.faceId = faceId;
        }

        public int getIntegral() {
            return integral;
        }

        public void setIntegral(int integral) {
            this.integral = integral;
        }

        public int getIntegralCount() {
            return integralCount;
        }

        public void setIntegralCount(int integralCount) {
            this.integralCount = integralCount;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getLoginCount() {
            return loginCount;
        }

        public void setLoginCount(int loginCount) {
            this.loginCount = loginCount;
        }

        public String getModifyTime() {
            return modifyTime;
        }

        public void setModifyTime(String modifyTime) {
            this.modifyTime = modifyTime;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(int orderCount) {
            this.orderCount = orderCount;
        }

        public String getPaypwd() {
            return paypwd;
        }

        public void setPaypwd(String paypwd) {
            this.paypwd = paypwd;
        }

        public String getProfilePhoto() {
            return profilePhoto;
        }

        public void setProfilePhoto(String profilePhoto) {
            this.profilePhoto = profilePhoto;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatusName() {
            return statusName;
        }

        public void setStatusName(String statusName) {
            this.statusName = statusName;
        }

        public String getTelphone() {
            return telphone;
        }

        public void setTelphone(String telphone) {
            this.telphone = telphone;
        }

        public String getUserChannels() {
            return userChannels;
        }

        public void setUserChannels(String userChannels) {
            this.userChannels = userChannels;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
