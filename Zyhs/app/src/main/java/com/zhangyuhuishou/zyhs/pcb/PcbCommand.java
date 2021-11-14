package com.zhangyuhuishou.zyhs.pcb;

import android.text.TextUtils;
import android.util.Log;
import com.zhangyuhuishou.zyhs.serialport.util.ByteUtil;
import com.zhangyuhuishou.zyhs.serialport.util.CRC16M;
import org.greenrobot.eventbus.EventBus;
import static com.zhangyuhuishou.zyhs.pcb.PcbConstant.COMMON_REGISTER_ADDRESS;

/**
 * 作者:created by author:tlh
 * 日期:2019/4/25 11:19
 * 邮箱:tianlihui2234@live.com
 * 描述:命令工具类（PCB控制版）
 */

public class PcbCommand {

    private static final String TAG = PcbCommand.class.getSimpleName();

    // 回收数据处理
    public static void dataCallBack(String callBackMessage) {

        if (TextUtils.isEmpty(callBackMessage)) {
            return;
        }

        if (callBackMessage.length() < 5) {
            return;
        }

        // 读到开始升级指令反馈
        if("9600".equals(COMMON_REGISTER_ADDRESS)){
            dealStartUpdate(callBackMessage);
            return;
        }

        byte[] sbuf = CRC16M.getSendBuf(callBackMessage.substring(0, callBackMessage.length() - 4));
        String crcCode = CRC16M.getBufHexStr(sbuf);
        crcCode = crcCode.substring(crcCode.length() - 4);
        String lastCode = callBackMessage.substring(callBackMessage.length() - 4, callBackMessage.length());
        if (!crcCode.equals(lastCode)) {
            return;
        }

        String functionCode = callBackMessage.substring(2, 4);
        if ("84".equals(functionCode) || "90".equals(functionCode)) {
            // 回收柜状态（错误）
            String errorCode = callBackMessage.substring(4,6);
            dealDevError(errorCode);
            return;
        }

        // 解析软件版本
        if("0100".equals(COMMON_REGISTER_ADDRESS)){
            dealPcbVersion(callBackMessage);
            return;
        }

        // 写入升级状态
        if("0300".equals(COMMON_REGISTER_ADDRESS)){
            dealWriteUpdateStatus(callBackMessage);
            return;
        }

        // flash编程
        if("0305".equals(COMMON_REGISTER_ADDRESS)){
            dealFlashProgram(callBackMessage);
            return;
        }

        // 读收到字节个数和校验码
        if("0104".equals(COMMON_REGISTER_ADDRESS)){
            dealReadByteAndCode(callBackMessage);
        }
    }

    // 读取软件版本
    public static String getPcbVersion(String ID_S) {
        String functionCode = "04";
        String registerAddress = "0100";
        String readNum = "0004";
        COMMON_REGISTER_ADDRESS = registerAddress;
        String code = ID_S + functionCode + registerAddress + readNum;
        byte[] sbuf = CRC16M.getSendBuf(code);
        return CRC16M.getBufHexStr(sbuf);
    }

    // 解析软件版本
    public static void dealPcbVersion(String callBackMessage) {
        COMMON_REGISTER_ADDRESS = "0000";
        String version = "0000";
        try{
            version = callBackMessage.substring(6,10);
        }catch (Exception e){
            e.printStackTrace();
        }
        float hardVersion = ByteUtil.ieeeToFloat(callBackMessage.substring(10,14) + callBackMessage.substring(6,10));
        float softwareVersion = ByteUtil.ieeeToFloat(callBackMessage.substring(18,22) + callBackMessage.substring(14,18));
        System.out.println("硬件版本信息：" + hardVersion);
        System.out.println("软件版本信息：" + softwareVersion);
    }

    // 开始升级指令
    public static String startUpdate(String ID_S) {
        String maker = "FE";
        String firstReverse = "01";
        String originalAddress = "FF";
        String length = "10";
        String cmd = "51";
        String writeData = "00004B0000011770";
        COMMON_REGISTER_ADDRESS = "9600";
        String code =  maker + ID_S + firstReverse + originalAddress + length + cmd + writeData;
        String checkCode = ByteUtil.getCheckHex(code) + "0D";
        return  code + checkCode;
    }

    // 解析开始升级指令
    public static void dealStartUpdate(String callBackMessage) {
        COMMON_REGISTER_ADDRESS = "0000";
        String readData = "";
        String machineAddress = "";
        try{
            machineAddress = callBackMessage.substring(6,8);
            readData = callBackMessage.substring(10,12);
        }catch (Exception e){
            e.printStackTrace();
        }
        if("52".equals(readData)){
            Log.i(TAG,"解析升级指令OK");
//            EventBus.getDefault().post(new PcbStartUpdateModel(true,machineAddress));
        }else {
            Log.i(TAG,"解析升级指令BAD");
        }
    }

    // flash编程
    public static String flashProgram(String ID_S,String flashData,String sendDataSize) {
        String functionCode = "10";
        String registerAddress = "0305";
        String registerNum = "";
        String byteNum = "";
        registerNum = ByteUtil.decimal2fitHex(ByteUtil.hexStr2decimal(sendDataSize) + 4,4);
        byteNum = ByteUtil.decimal2fitHex((ByteUtil.hexStr2decimal(sendDataSize) + 4) * 2,2);
        COMMON_REGISTER_ADDRESS = registerAddress;
        String code = ID_S + functionCode + registerAddress + registerNum + byteNum + flashData;
        byte[] sbuf = CRC16M.getSendBuf(code);
        return CRC16M.getBufHexStr(sbuf);
    }

    // 解析flash编程
    public static void dealFlashProgram(String callBackMessage) {
        COMMON_REGISTER_ADDRESS = "0000";
        String registerNum = "0000";
        String machineAddress = "";
        boolean isAnalysisOk = false;
        try{
            machineAddress = callBackMessage.substring(0,2);
            registerNum = callBackMessage.substring(8,12);
            int registerNumData = ByteUtil.makeChecksum2(registerNum);
            // "0044"是68
            if(registerNumData <= 68){
                isAnalysisOk = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        EventBus.getDefault().post(new PcbUpdateModel(isAnalysisOk,machineAddress));
    }

    // 写入升级状态（硬件程序升级失败与否）
    public static String writeUpdateStatus(String ID_S,String receiveByteNum) {
        String functionCode = "10";
        String registerAddress = "0300";
        String registerNum = "0004";
        String byteNum = "08";
        String dataAddress = "08020000";
        COMMON_REGISTER_ADDRESS = registerAddress;
        String code = ID_S + functionCode + registerAddress + registerNum + byteNum + dataAddress + receiveByteNum;
        byte[] sbuf = CRC16M.getSendBuf(code);
        return CRC16M.getBufHexStr(sbuf);
    }

    // 解析升级状态
    public static void dealWriteUpdateStatus(String callBackMessage) {
        COMMON_REGISTER_ADDRESS = "0000";
        String updateCode = "0002";
        boolean isUpdateOk = false;
        try{
            updateCode = callBackMessage.substring(8,12);
            if("0001".equals(updateCode)){
                isUpdateOk = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(isUpdateOk){
            Log.i(TAG,"升级成功");
        }
    }

    // 读收到字节个数和校验码
    public static String readByteAndCode(String ID_S) {
        String functionCode = "04";
        String registerAddress = "0104";
        String readNum = "0002";
        COMMON_REGISTER_ADDRESS = registerAddress;
        String code = ID_S + functionCode + registerAddress + readNum;
        byte[] sbuf = CRC16M.getSendBuf(code);
        return CRC16M.getBufHexStr(sbuf);
    }

    // 解析读收到字节个数和校验码
    public static String dealReadByteAndCode(String callBackMessage) {
        COMMON_REGISTER_ADDRESS = "0000";
        String readData = "";
        String machineAddress = "";
        try{
            machineAddress = callBackMessage.substring(0,2);
            readData = callBackMessage.substring(6,14);
            EventBus.getDefault().post(new PcbByteNumModel(readData,machineAddress));
        }catch (Exception e){
            e.printStackTrace();
        }
        return readData;
    }

    // 设备错误码
    public static void dealDevError(String errorCode) {
        switch (errorCode) {
            case PcbConstant.ILLEGAL_FUNCTION:
                Log.i(TAG,"非法功能");
                break;
            case PcbConstant.ILLEGAL_DATA_ADDRESS:
                Log.i(TAG,"非法数据地址");
                break;
            case PcbConstant.ILLEGAL_DATA_VALUE:
                Log.i(TAG,"非法数据值");
                break;
            case PcbConstant.SLAVE_FAILURE:
                Log.i(TAG,"从站设备故障");
                break;
            case PcbConstant.CONFIRM:
                Log.i(TAG,"确认");
                break;
            case PcbConstant.SLAVE_DEVICE_BUSY:
                Log.i(TAG,"从属设备忙");
                break;
            case PcbConstant.STORAGE_PARITY_ERROR:
                Log.i(TAG,"存储奇偶性差错");
                break;
            case PcbConstant.UNAVAILABLE_GATEWAY_PATH:
                Log.i(TAG,"不可用网关路径");
                break;
            case PcbConstant.GATEWAY_TARGET_DEVICE_RESPONSE_FAILED:
                Log.i(TAG,"网关目标设备响应失败");
                break;
        }
    }
}

