package com.baidu.tts.util;


import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.baidu.tts.control.InitConfig;
import com.baidu.tts.control.MySyntherizer;
import com.baidu.tts.control.NonBlockSyntherizer;
import com.baidu.tts.listener.UiMessageListener;
import com.tlh.android.utils.SocketUtil;
import com.zhangyuhuishou.zyhs.base.BaseApplication;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tlh
 * @time 2018/12/29 14:52
 * @desc 百度语音工具
 */
public class TTSUtils {

    private final static String TAG = SocketUtil.class.getSimpleName();
    // ================== 初始化参数设置开始 ==========================
    /**
     * 发布时请替换成自己申请的appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
     */
    private static String appId = "15303310";
    private static String appKey = "xaZsGF9saZOgp0fTZrvjirxc";
    private static String secretKey = "HKxDQLCjsA9KxRKq53qfIzqFD0ZaE0Oa";
    public static boolean isInitOK = false;// 语音引擎是否初始化成功

    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    private static TtsMode ttsMode = TtsMode.MIX;
    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
    // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
    private static String offlineVoice = OfflineResource.VOICE_FEMALE;

    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================
    // 主控制类，所有合成控制方法从这个类开始
    private static Handler mainHandler = new Handler();

    private volatile static MySyntherizer synthesizer = null;

    public static MySyntherizer init(){
        if(synthesizer == null){
            synchronized (TTSUtils.class){
                if(synthesizer == null){
                    initialTts();
                }
            }
        }
        return synthesizer;
    }


    /**
     * 初始化引擎，需要的参数均在InitConfig类里
     * <p>
     * DEMO中提供了3个SpeechSynthesizerListener的实现
     * MessageListener 仅仅用log.i记录日志，在logcat中可以看见
     * UiMessageListener 在MessageListener的基础上，对handler发送消息，实现UI的文字更新
     * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
     */
    private static void initialTts() {
//        LoggerProxy.printable(true); // 日志打印在logcat中
        // 设置初始化参数
        // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
        SpeechSynthesizerListener listener = new UiMessageListener(mainHandler);
        Map<String, String> params = getParams();
        InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);
        synthesizer = new NonBlockSyntherizer(BaseApplication.getContext(), initConfig, mainHandler); // 此处可以改为MySyntherizer 了解调用过程
    }

    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     * @return
     */
    private static Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "5");
        // 设置合成的语速，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");
        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
        OfflineResource offlineResource = createOfflineResource(offlineVoice);
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        if(offlineResource != null){
            params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
            params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, offlineResource.getModelFilename());
        }
        return params;
    }

    private static OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(BaseApplication.getContext(), voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
            Log.i(TAG,"【error】:copy files from assets failed." + e.getMessage());
        }
        return offlineResource;
    }

    /**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    public static void speak(String tipMessage) {
        if(synthesizer == null || !isInitOK){
            return;
        }

        try{
            // 需要合成的文本text的长度不能超过1024个GBK字节。
            if(!TextUtils.isEmpty(tipMessage)){
                int result = synthesizer.speak(tipMessage);
                checkResult(result, "speak");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void checkResult(int result, String method) {
        if (result != 0) {
            Log.i(TAG,("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 "));
        }
    }

    /**
     * 暂停播放,仅调用speak后生效
     */
    public static void pause() {
        if(synthesizer == null){
            return;
        }
        int result = synthesizer.pause();
        checkResult(result, "pause");
    }

    /**
     * 继续播放,仅调用speak后生效，调用pause生效
     */
    public static void resume() {
        if(synthesizer == null){
            return;
        }
        int result = synthesizer.resume();
        checkResult(result, "resume");
    }

    /*
     * 停止合成引擎。即停止播放，合成，清空内部合成队列。
     */
    public static void stop() {
        if(synthesizer == null){
            return;
        }
        int result = synthesizer.stop();
        checkResult(result, "stop");
    }

    /*
     * 释放资源
     */
    public static void release() {
        if(synthesizer == null){
            return;
        }
        synthesizer.release();
    }

    public static MySyntherizer getSynthesizer(){
        return synthesizer;
    }

}
