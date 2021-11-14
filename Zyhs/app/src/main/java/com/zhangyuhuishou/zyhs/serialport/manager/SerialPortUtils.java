package com.zhangyuhuishou.zyhs.serialport.manager;

import android.text.TextUtils;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.ApkUtils;
import com.zhangke.zlog.ZLog;
import com.zhangyuhuishou.zyhs.serialport.message.CloseDoorModel;
import com.zhangyuhuishou.zyhs.serialport.message.Device;
import com.zhangyuhuishou.zyhs.serialport.util.Util;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by tlh on 2018/8/24.
 */
public class SerialPortUtils {

    private static final String TAG = "SerialPortUtils";

    private boolean mOpened = false;
    private Device mDevice;
    private String path = "/dev/ttyS2";
    private String baudrate = "9600";
    private int parity = 0;
    private int serialType = Util.SERIAL_PORT_ZYHS;

    public SerialPortUtils(){
        String deviceModel = ApkUtils.getDeviceModel();
        if(TextUtils.isEmpty(deviceModel)){
            return;
        }
        switch (deviceModel){
            case Constant.DEVICE_MODEL_RK3288:// 新设备
                path = "/dev/ttyS1";
                break;
            case Constant.DEVICE_MODEL_DS83X:
                path = "/dev/ttyS2";
                break;
        }
    }

    /**
     * 设置波特率和路径
     */
    public void setPathAndBaurate(String path,String baudrate){
        this.path = path;
        this.baudrate = baudrate;
    }

    /**
     * 设置校验位
     */
    public void setParity(int parity){
        this.parity = parity;
    }

    /**
     * 设置类型
     */
    public void setSerialType(int serialType ){
        this.serialType = serialType;
    }

    /**
     * 初始化设备
     */
    private void initDevice() {
        // 设备 波特率
        mDevice = new Device(path, baudrate);
        mDevice.setParity(this.parity);

    }

    /**
     * 打开或关闭串口
     */
    private void switchSerialPort() {
        switch (serialType){
            case Util.SERIAL_PORT_ZYHS:
                mOpened = SerialPortManager.instance().open(mDevice) != null;
                if (mOpened) {
                    ZLog.i("SerialPortManager","打开串口成功");
                    EventBus.getDefault().post(new CloseDoorModel("100"));
                } else {
                    ZLog.e("SerialPortManager","打开串口失败");
                    EventBus.getDefault().post(new CloseDoorModel("101"));
                }
                break;
            case Util.SERIAL_PORT_METER:
                mOpened = SerialPortManagerNew.instance().open(mDevice) != null;
                if(mOpened){
                    ZLog.i("SerialPortManager","打开电表串口成功");
                    EventBus.getDefault().post(new CloseDoorModel("102"));
                }else {
                    ZLog.e("SerialPortManager","打开电表串口失败");
                }
                break;
        }

    }

    /**
     * 打开设备
     */
    public void openPort(){
        initDevice();
        switchSerialPort();
    }

    /**
     * 关闭设备
     */
    public void closePort(){
        switch (serialType){
            case Util.SERIAL_PORT_ZYHS:
                SerialPortManager.instance().close();// 关闭串口
                break;
            case Util.SERIAL_PORT_METER:
                SerialPortManagerNew.instance().close();// 关闭串口
                break;
        }
    }
}
