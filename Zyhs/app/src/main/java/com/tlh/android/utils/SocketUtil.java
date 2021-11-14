package com.tlh.android.utils;


import android.content.Context;
import android.util.Log;
import com.tlh.android.config.Constant;
import com.zhangyuhuishou.zyhs.model.IsUpdateModel;
import com.zhangyuhuishou.zyhs.model.QrTimeModel;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * 作者:created by author:tlh
 * 日期:2018/11/13 14:51
 * 邮箱:tianlihui2234@live.com
 * 描述:Socket工具类
 */
public class SocketUtil {

    private volatile static Socket mSocket = null;
    private final static String TAG = SocketUtil.class.getSimpleName();

    private static long startTime;
    public static Socket getInstance(Context context){
        if(mSocket == null){
            long startTime2 = System.currentTimeMillis();
            synchronized (SocketUtil.class){
                if(mSocket == null){
                    try{
                        IO.Options opts = new IO.Options();
                        opts.forceNew = true;
                        opts.query = "rangeId=" + SPUtils.getString(context, Constant.CURRENT_LOCATION_ID)
                        + "&rangeSource=" + SPUtils.getString(context, Constant.CURRENT_LOCATION_SOURCE) ;
                        mSocket = IO.socket(Constant.BASE_URL_SOCKET + "/machine", opts);
//                        long costTime = System.currentTimeMillis() - startTime2;
//                        System.out.println("创建socket时间:" + costTime);
                        init();
//                        long costTime2 = System.currentTimeMillis() - startTime2;
//                        System.out.println("init时间:" + costTime2);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        startTime = 0;
        return mSocket;
    }

    // 初始化订阅触发事件
    private static void init() {

        try {
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    try {
                        mSocket.emit("MACHINE_CONNECT");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).on("MACHINE_QR", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
//                    long costTime = System.currentTimeMillis() - startTime;
                    try{
                        JSONObject obj = (JSONObject) args[0];
//                        Log.i(TAG, "收到Socket反馈信息（二维码）：" + obj.toString());
//                        System.out.println("二维码：时间" + costTime);
                        EventBus.getDefault().post(new QrTimeModel(obj.getString("qrCode"),obj.getInt("timeout")));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }).on("CLIENT_CONNECT", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    try{
                        JSONObject obj = (JSONObject) args[0];
                        Log.i(TAG, "收到Socket反馈信息（设备连接）：" + obj.toString());
                        EventBus.getDefault().post(obj);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    Log.i(TAG, "我关闭了，呵呵呵~~~~~");
                }
            }).on("MACHINE_READY", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i(TAG, "收到Socket反馈信息（版本升级）：" + args.toString());
                    try {
                        // 应用升级
                        JSONObject obj = (JSONObject) args[0];
                        boolean isUpgrade = obj.getBoolean("isUpgrade");
                        if(isUpgrade){
                            EventBus.getDefault().post(new IsUpdateModel(true,1));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        // 广告升级
                        JSONObject obj = (JSONObject) args[0];
                        boolean isUpdateAd = obj.getBoolean("isUpdateAd");
                        if(isUpdateAd){
                            EventBus.getDefault().post(new IsUpdateModel(true,2));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    try {
                        // 点位升级
                        JSONObject obj = (JSONObject) args[0];
                        boolean isUpdateInfo = obj.getBoolean("isUpdateInfo");
                        if(isUpdateInfo){
                            EventBus.getDefault().post(new IsUpdateModel(true,3));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
//            mSocket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 关闭socket
    public static void closeSocket(){
        if(mSocket != null && mSocket.connected()){
            mSocket.disconnect();
        }
    }

    // 获取socket连接状态
    private static boolean isLive(){
        long costTime = System.currentTimeMillis() - startTime;
        System.out.println("获取连接状态时间：" + costTime);
        return mSocket.connected();


    }

    // 发送消息
    public static void sendGetQrMessage(){
        startTime = System.currentTimeMillis();
        if(isLive()){
            mSocket.emit("MACHINE_CONNECT");
            System.out.println("已经连接");
        }else {
            mSocket.connect();
            System.out.println("没有连接");
        }

    }
}
