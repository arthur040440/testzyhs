package com.face.util;

public class Util {

    //在这边填写 API_KEY 和 API_SECRET
//    public static String API_KEY = "BYc4oqIrfDunwuPSKryw4lXZv9zgepVd";
//    public static String API_SECRET = "NvLAYNf5N86XeG6cdTiTmLv_D35kPzqr";

    public static String API_KEY = "O-Ee326zzhC1PbIM1d57-qYYHWap80aF";
    public static String API_SECRET = "oNQG7k5Mv4-UlDEMDZSDJKmWopkK06I_";

    public static String CN_LICENSE_URL = "https://api-cn.faceplusplus.com/sdk/v3/auth";
    public static String CN_DETECT_URL = "https://api-cn.faceplusplus.com/facepp/v3/detect";
    public static String CN_CREATE_URL = "https://api-cn.faceplusplus.com/facepp/v3/faceset/create";
    public static String CN_SERACH_URL = "https://api-cn.faceplusplus.com/facepp/v3/search";
    public static String CN_ADD_FACE_URL = "https://api-cn.faceplusplus.com/facepp/v3/faceset/addface";
    public static String CN_GET_FACESETS_URL = "https://api-cn.faceplusplus.com/facepp/v3/faceset/getfacesets";
    public static String CN_GET_DETAIL_URL = "https://api-cn.faceplusplus.com/facepp/v3/faceset/getdetail";
    public static String CN_SET_USER_ID_URL = "https://api-cn.faceplusplus.com/facepp/v3/face/setuserid";
    public static String CN_REMOVE_FACE_URL = "https://api-cn.faceplusplus.com/facepp/v3/faceset/removeface";

    public static final int FACE_TYPE_LOGIN = 1;// 人脸登录
    public static final int FACE_TYPE_COLLECTION = 2;// 人脸采集
    public static final int FACE_CALLBACE_NO_FACE = 1001;// 未检测到人脸信息
    public static final int FACE_CALLBACE_NOT_SAME_PEOPLE = 1002;// 不是同一个人
    public static final int FACE_CALLBACE_NOT_THE_PEOPLE = 1003;// 当前用户不存在
    public static final int FACE_CALLBACE_ADD_FACE_FAILURE = 1004;// 人脸添加失败
    public static final int FACE_CALLBACE_ERROR = 1005;// 接口访问失败
    public static final int FACE_CALLBACE_PARSE_ERROR = 1006;// 数据解析错误

    public static final int faceOffset = 30;// 人脸检测偏移值


}
