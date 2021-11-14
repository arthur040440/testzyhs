package com.zhangyuhuishou.zyhs.model;

public class UserSocketBean {


    /**
     * userType : out
     * token : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI0Y2ZlNjc1Y2RhMjI0ZTMwOGM1YjVhNzdlY2I0OWE0ZCIsImNoYW5uZWwiOiJXRUNIQVRNSU5JIiwidXNlclR5cGUiOiJvdXQiLCJpYXQiOjE1Njg3MDM2OTUsImV4cCI6MTU2OTM5NDg5NX0.ZFuzzPNTuVK2-oOS0LjiHm0y7HlqjlbN4hnIwQ3zJSw
     * user : {"userId":"4cfe675cda224e308c5b5a77ecb49a4d","nickName":"琦琦","name":"","telphone":"18816284850","pwd":"","profilePhoto":"https://wx.qlogo.cn/mmopen/vi_32/vtXkPgYN4PbCTbfdicJbFXuwJPa8Gd7iba9ia6zSFFsF14dCvNkOJyrZQlhQvgib1V7micL2ksr4g67YAVYN2vTxEEA/132","sex":"","age":0,"province":"","city":"","area":"","faceId":"1","integral":62069,"experience":0,"orderCount":0,"level":0,"address":"","recommendUserId":""}
     */

    private String userType;
    private String token;
    private UserBean user;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public static class UserBean {
        /**
         * userId : 4cfe675cda224e308c5b5a77ecb49a4d
         * nickName : 琦琦
         * name :
         * telphone : 18816284850
         * pwd :
         * profilePhoto : https://wx.qlogo.cn/mmopen/vi_32/vtXkPgYN4PbCTbfdicJbFXuwJPa8Gd7iba9ia6zSFFsF14dCvNkOJyrZQlhQvgib1V7micL2ksr4g67YAVYN2vTxEEA/132
         * sex :
         * age : 0
         * province :
         * city :
         * area :
         * faceId : 1
         * integral : 62069
         * experience : 0
         * orderCount : 0
         * level : 0
         * address :
         * recommendUserId :
         */

        private String userId;
        private String nickName;
        private String name;
        private String telphone;
        private String pwd;
        private String profilePhoto;
        private String sex;
        private int age;
        private String province;
        private String city;
        private String area;
        private String faceId;
        private int integral;
        private int experience;
        private int orderCount;
        private int level;
        private String address;
        private String recommendUserId;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTelphone() {
            return telphone;
        }

        public void setTelphone(String telphone) {
            this.telphone = telphone;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        public String getProfilePhoto() {
            return profilePhoto;
        }

        public void setProfilePhoto(String profilePhoto) {
            this.profilePhoto = profilePhoto;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
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

        public int getExperience() {
            return experience;
        }

        public void setExperience(int experience) {
            this.experience = experience;
        }

        public int getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(int orderCount) {
            this.orderCount = orderCount;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getRecommendUserId() {
            return recommendUserId;
        }

        public void setRecommendUserId(String recommendUserId) {
            this.recommendUserId = recommendUserId;
        }
    }
}
