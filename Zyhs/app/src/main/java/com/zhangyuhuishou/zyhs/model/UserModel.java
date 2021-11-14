package com.zhangyuhuishou.zyhs.model;

public class UserModel {

    private String id;
    private String nickName;
    private String token;
    private int integral;
    private String profilePhoto;

    private static UserModel ourInstance = null;

    public static UserModel getInstance() {
        if(ourInstance == null){
            synchronized (UserModel.class){
                if(ourInstance == null){
                    ourInstance = new UserModel();
                }
            }
        }
        return ourInstance;

    }

    public String getId() {
        return id;
    }

    public String getNickName() {
        return nickName;
    }



    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}
