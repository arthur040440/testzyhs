package com.zhangyuhuishou.zyhs.actys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.MimeTypeMap;

import com.bumptech.glide.Glide;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.ApkUtils;
import com.tlh.android.utils.MD5Utils;
import com.tlh.android.utils.NetworkUtils;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.utils.SocketUtil;
import com.tlh.android.utils.SystemCmdUtils;
import com.tlh.android.utils.Utils;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.banner.Banner;
import com.zhangyuhuishou.zyhs.banner.IBannerView;
import com.zhangyuhuishou.zyhs.banner.OnBannerListener;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import com.zhangyuhuishou.zyhs.database.BucketStatusModel;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.download.savepics.DownImagePrensenter;
import com.zhangyuhuishou.zyhs.download.savepics.IDownFinish;
import com.zhangyuhuishou.zyhs.model.AdModel;
import com.zhangyuhuishou.zyhs.model.AlarmModel;
import com.zhangyuhuishou.zyhs.model.ClearDismissModel;
import com.zhangyuhuishou.zyhs.model.ClearMissModel;
import com.zhangyuhuishou.zyhs.model.ExceptionModel;
import com.zhangyuhuishou.zyhs.model.InspectionModel;
import com.zhangyuhuishou.zyhs.model.OrderMissModel;
import com.zhangyuhuishou.zyhs.model.PcbExceptionModel;
import com.zhangyuhuishou.zyhs.model.PointModel;
import com.zhangyuhuishou.zyhs.model.RangeInfoModel;
import com.zhangyuhuishou.zyhs.model.SkinModel;
import com.zhangyuhuishou.zyhs.model.TerminalModel;
import com.zhangyuhuishou.zyhs.presenter.BannerPresenter;
import com.zhangyuhuishou.zyhs.presenter.ClearPresenter;
import com.zhangyuhuishou.zyhs.presenter.DynamicSkinPresenter;
import com.zhangyuhuishou.zyhs.presenter.IClearView;
import com.zhangyuhuishou.zyhs.presenter.IDynamicSkinView;
import com.zhangyuhuishou.zyhs.presenter.IMaintainView;
import com.zhangyuhuishou.zyhs.presenter.IOrderView;
import com.zhangyuhuishou.zyhs.presenter.IPointView;
import com.zhangyuhuishou.zyhs.presenter.IYintaiCouponsView;
import com.zhangyuhuishou.zyhs.presenter.MaintainPresenter;
import com.zhangyuhuishou.zyhs.presenter.OrderPresenter;
import com.zhangyuhuishou.zyhs.presenter.PointPresenter;
import com.zhangyuhuishou.zyhs.presenter.YintaiCouponsPresenter;
import com.zhangyuhuishou.zyhs.receiver.netmonitor.NetUtil;
import com.zhangyuhuishou.zyhs.scanner.ScanGunKeyEventHelper;
import com.zhangyuhuishou.zyhs.serialport.constant.NormalConstant;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortUtils;
import com.zhangyuhuishou.zyhs.serialport.message.BucketIsFullModel;
import com.zhangyuhuishou.zyhs.serialport.message.CloseDoorModel;
import com.zhangyuhuishou.zyhs.serialport.message.RecvMessage;
import com.zhangyuhuishou.zyhs.serialport.util.ByteUtil;
import com.zhangyuhuishou.zyhs.serialport.util.Command;
import com.zhangyuhuishou.zyhs.serialport.util.Util;
import com.zhangyuhuishou.zyhs.service.UpdateService;
import com.zhangyuhuishou.zyhs.time.MyTimeTask;
import com.zhangyuhuishou.zyhs.time.TimeQueryUtils;
import com.zhangyuhuishou.zyhs.time.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import cn.finalteam.toolsfinal.CountDownTimer;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.tlh.android.config.Constant.BACKGROUND_PATH;
import static com.tlh.android.config.Constant.BOTTOM_FULL_NUM;
import static com.tlh.android.config.Constant.LOGO_PATH;
import static com.tlh.android.config.Constant.SKIN_BACKGROUND_URL;
import static com.tlh.android.config.Constant.SKIN_LOGO_URL;

public class MainActivity extends BaseActivity implements OnBannerListener, IPointView, IBannerView, IDownFinish, IMaintainView, IYintaiCouponsView, IDynamicSkinView {

    private final String TAG = MainActivity.class.getSimpleName();

    private PointPresenter pointPresenter;
    private YintaiCouponsPresenter yintaiCouponsPresenter;//银泰接口
    private OrderPresenter orderPresenter;
    private MaintainPresenter maintainPresenter; //运维巡检单接口
    private DynamicSkinPresenter dynamicSkinPresenter; //动态换肤接口
    private BannerPresenter mBannerPresenter;// 广告
    @BindView(R.id.banner)
    Banner banner;

    private SerialPortUtils serialPortUtils;// 串口工具
    private SerialPortUtils serialPortUtilsClock;// 串口工具（电表）

    private Handler handler = new Handler();
    private TimeQueryUtils timeQueryUtils;

    private Intent alarmIntent = null;// 闹钟升级服务

    private AudioManager mAudioManager;// 音频管理器

    private List<AdModel.DataBean> adsList = new ArrayList<>();// 广告列表数据

    private DownImagePrensenter downImagePrensenter;// 广告下载器

    private MainBroadCastReceiver mainBroadCastReceiver;// 广播接收器

    private DBManager dbManager;

    private String channelInfo;// 渠道信息

    private boolean isRequestCoupons;//银泰优惠券url是否下载成功

    private boolean getSkinUrl; //动态皮肤资源是否获取成功

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        super.initData();
//        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        int memorySize = activityManager.getMemoryClass();
//        Log.i(TAG, "分配内存大小:" + memorySize);

        // 设置声音
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }
        channelInfo = ApkUtils.getAppMetaData(context, "UMENG_CHANNEL");
        if (!"zyhs".equals(channelInfo)) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 6, AudioManager.FLAG_PLAY_SOUND);
        } else {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 7, AudioManager.FLAG_PLAY_SOUND);
        }
        dbManager = DBManager.getInstance(context);

        timeQueryUtils = new TimeQueryUtils(handler, context);
        timeQueryUtils.setTime(2000);
        // 偷偷上传丢失的订单
        orderPresenter = new OrderPresenter(context);
        pointPresenter = new PointPresenter(context);
        pointPresenter.attachView(this);

        maintainPresenter = new MaintainPresenter(context);
        maintainPresenter.attachView(this);
        yintaiCouponsPresenter = new YintaiCouponsPresenter(context);
        yintaiCouponsPresenter.attachView(this);

        dynamicSkinPresenter = new DynamicSkinPresenter(context);
        dynamicSkinPresenter.attachView(this);

        // 发送心跳
        sendPolling();

        mainBroadCastReceiver = new MainBroadCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.UPDATE_BANNER_NOTIFY);
        filter.addAction(Constant.NEED_UPDATE_NOTIFY);
        filter.addAction(Constant.NEED_UPDATE_LOCATION_NOTIFY);
        filter.addAction(Constant.NEED_UPDATE_BANNER_NOTIFY);
        registerReceiver(mainBroadCastReceiver, filter);


        if (NetworkUtils.testNetworkStatus(context)) {
            System.out.println("网络畅通");
            updateSysInfo();
        } else {
            System.out.println("网络阻塞");
            CountDownTimer updateSysInfoCountDownTimer = new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    System.out.println("网络阻塞执行");
                    updateSysInfo();
                }
            };
            updateSysInfoCountDownTimer.start();
        }
        File file = new File(Constant.VIDEO_PIC_PATH);
        if (!file.exists()) {
            file.mkdir();
        }

        File fileForPcb = new File(Constant.BIN_PATH);
        if (!fileForPcb.exists()) {
            file.mkdirs();
        }

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        showBanner();

        if ("yintai".equals(channelInfo)) {

            yintaiCouponsPresenter.getYintaiCoupons(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID));

        }
    }

    // 更新系统信息
    private void updateSysInfo() {
        updateLocation();
        String rangeSource = SPUtils.getString(context, Constant.CURRENT_LOCATION_SOURCE, "");
        if ("freshhema".equals(rangeSource) || "aliInternal".equals(rangeSource) || "yintai".equals(rangeSource) || "ikea".equals(rangeSource) || "chenghuangge".equals(rangeSource)) {
            updateApp();
        }
    }

    // 广告文件
    private void showBanner() {
        List<AdModel.DataBean> tempList = new ArrayList<>();
        AdModel.DataBean bean = new AdModel.DataBean();
        bean.setFileUrl("https://www.baidu.com");
        bean.setDuration(10000);
        tempList.add(bean);
        banner.setDataList(tempList);
        banner.setImgDelyed(2000);
        banner.setOnBannerListener(this);
        banner.startBanner();
        banner.startAutoPlay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == dbManager) {
            dbManager = DBManager.getInstance(context);
        }
        if (banner.getmAdapter() == null) {
            banner.startBanner();
        }
        banner.startAutoPlay();
        SocketUtil.closeSocket();// 关闭socket以防二维码界面没有关闭
        EventBus.getDefault().register(this);
        // 开启定时任务
        MyTimeTask.getInstance().start(timeQueryUtils);
        // 串口
        serialPortUtils = new SerialPortUtils();
        serialPortUtils.openPort();

        serialPortUtilsClock = new SerialPortUtils();
        serialPortUtilsClock.setPathAndBaurate("/dev/ttyS3", "9600");
        serialPortUtilsClock.setParity(1);
        serialPortUtilsClock.setSerialType(Util.SERIAL_PORT_METER);
        serialPortUtilsClock.openPort();

        List<OrderMissModel> list = dbManager.getDisOrderData();
        adsList = dbManager.getAdList();
        List<InspectionModel> inspectionModelList = dbManager.getDisInspectionInfo();// 丢失的巡检数据

        // 上传因网络丢失的丢单
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                OrderMissModel model = list.get(i);
                try {
                    if (!TextUtils.isEmpty(model.getToken())) {
                        orderPresenter.createOrderMiss(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID), model.getId(), model.getToken(), new JSONArray(model.getData()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 上传因网络丢失的巡检单信息
        if (inspectionModelList != null) {
            for (int i = 0; i < inspectionModelList.size(); i++) {
                InspectionModel inspectionModel = inspectionModelList.get(i);
                try {
                    maintainPresenter.uploadInspectionInfo(inspectionModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 广告资源信息
        List<AdModel.DataBean> beanList = banner.getmDataList();
        boolean isFileExist = false;
        for (int i = 0; i < adsList.size(); i++) {
            String url = adsList.get(i).getFileUrl();
            if (MimeTypeMap.getFileExtensionFromUrl(url).equals("mp4")) {
                File f = ApkUtils.getMovieFileByFileUrl(url);
                if (!f.exists()) {
                    isFileExist = true;
                    break;
                }
            } else if (MimeTypeMap.getFileExtensionFromUrl(url).equals("gif")) {
                File f = ApkUtils.getGifFileByFileUrl(url);
                if (!f.exists()) {
                    isFileExist = true;
                    break;
                }
            } else if (MimeTypeMap.getFileExtensionFromUrl(url).equals("jpg") || MimeTypeMap.getFileExtensionFromUrl(url).equals("png") ||
                    MimeTypeMap.getFileExtensionFromUrl(url).equals("jpeg")) {
                File f = ApkUtils.getAdFileByFileUrl(Constant.VIDEO_PIC_PATH + "/", url);
                if (!f.exists()) {
                    isFileExist = true;
                    break;
                }
            }
        }
        // 文件误删除
        if (isFileExist) {
            showBanner();
            updateBanner();
            return;
        }

        // 资源更新（后台下载）
        if (isUpdate) {
            banner.dataChange(adsList);
            isUpdate = false;
            return;
        }

        // 图片资源存在且占位图是章鱼回收
        if (beanList != null && beanList.size() > 0 && adsList.size() > 0) {
            if ("https://www.baidu.com".equals(beanList.get(0).getFileUrl())) {
                banner.dataChange(adsList);
                isUpdate = false;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        banner.stopBanner();
        EventBus.getDefault().unregister(this);
        if (serialPortUtils != null) {
            serialPortUtils.closePort();
            serialPortUtils = null;
        }
        if (serialPortUtilsClock != null) {
            serialPortUtilsClock.closePort();
            serialPortUtilsClock = null;
            electricMeterData = "";
        }

        MyTimeTask.getInstance().destroyed();// 取消定时任务
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alarmIntent != null) {
            stopService(alarmIntent);
        }
        if (serialPortUtils != null) {
            serialPortUtils.closePort();
            serialPortUtils = null;
        }
        if (serialPortUtilsClock != null) {
            serialPortUtilsClock.closePort();
            serialPortUtilsClock = null;
            electricMeterData = "";
        }

        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }

        if (mainBroadCastReceiver != null) {
            unregisterReceiver(mainBroadCastReceiver);
        }
    }

    @Override
    public void onNetChange(int netMobile) {
        super.onNetChange(netMobile);
        if (netMobile == NetUtil.NETWORK_NONE) {
            Log.i(TAG, "没有网络连接");
        } else {
            Log.i(TAG, "有网络连接");
        }
    }

    @Override
    public void OnBannerClick(int position) {
        if (alarmIntent != null) {
            stopService(alarmIntent);
        }
        banner.stopBanner();
        String channelInfo = ApkUtils.getAppMetaData(context, "UMENG_CHANNEL");
        if ("factory".equals(channelInfo)) {
            startActivity(new Intent(context, ManagerChooseActivity.class));
            return;
        }
        startActivity(new Intent(context, HomeActivity.class));
    }

    // 桶的状态
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseDoorEvent(CloseDoorModel model) {
        // 关闭打开的桶门
        switch (model.getTerminalId()) {
            case "100":
                // 串口打开成功可以查询设备在线状态
                // 查询回收桶门状态
                timeQueryUtils.initQueryTime();
                timeQueryUtils.doCloseScheduleByOpen();
                timeQueryUtils.doTimerSchedule();
                timeQueryUtils.doOpenLamp();
                timeQueryUtils.doIfOpenFan();
                timeQueryUtils.doQueryVersionInfo();
                break;
            case "102":
//                timeQueryUtils.sendClock();
                break;
        }
    }

    private String electricMeterData = "";


    // 串口返回数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final RecvMessage message) {
        Log.i("SerialPortManager", "收到反馈信息：" + message.getCommand());
        if (message.getCommand().startsWith("010404")) {
            electricMeterData = message.getCommand();
            String meter = ByteUtil.ieeeToFloat(message.getCommand().substring(6, 14)) + "";
            System.out.println("当前电量：" + meter);
            String meterCode = SPUtils.getString(context, Constant.CURRENT_DAY, "");
            pointPresenter.uploadElec(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, ""), meter, meterCode);
            return;
        }

        Command.dataCallBack(message.getCommand());

    }

    // 倒水桶满溢状态上报
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bucketStatus(BucketIsFullModel model) {
        SPUtils.putBoolean(context, Constant.DEV_BUCKET_STATUS, model.isFull());
        if (model.isFull() && !Utils.isEmpty(model.getTerminalId())) {
            pointPresenter.logUpload(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, ""), model.getTerminalId(), Constant.LOG_ALARM, Constant.EXCEPTION_WATER_BUCKET_FULL);
        }
    }

    // 故障上报
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void rangeException(ExceptionModel model) {
        pointPresenter.logUpload(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, ""), model.getTerminalId(), Constant.LOG_EXCEPTION, model.getDes());
    }

    // 告警上报
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void rangeAlarm(AlarmModel model) {
        pointPresenter.logUpload(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, ""), model.getTerminalId(), Constant.LOG_ALARM, model.getDes());
    }

    // 故障上报（PCB）
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void rangePcbException(PcbExceptionModel model) {
        pointPresenter.logUpload(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, ""), model.getTerminalId(), Constant.LOG_PCB_EXCEPTION, model.getDes());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(context).clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(context).clearMemory();
        }
        Glide.get(context).trimMemory(level);
    }

    @Override
    public void getPoints(List<PointModel.DataBean> points) {
    }

    @Override
    public void activePoint(PointModel.DataBean model) {
    }

    // 更新点位信息
    @Override
    public void updatePoint(RangeInfoModel model) {
        if (model == null || model.getData() == null) {
            // 更新广告
            updateBanner();
            return;
        }
        SPUtils.putString(context, Constant.CURRENT_LOCATION_ID, model.getData().getRangeId());
        SPUtils.putString(context, Constant.CURRENT_LOCATION_NUM, model.getData().getRangeNum());
        SPUtils.putString(context, Constant.CURRENT_COMMUNITYNAME, model.getData().getCommunityName());
        SPUtils.putString(context, Constant.CURRENT_RANGENAME, model.getData().getRangeName());
        SPUtils.putString(context, Constant.CURRENT_LOCATION_SOURCE, model.getData().getRangeSource());
        SPUtils.putString(context, Constant.CURRENT_COMMUNITY_ID, model.getData().getCommunityId());
        SPUtils.putString(context, Constant.CURRENT_COMMUNITY_ADCODE, model.getData().getAdCode());
        SPUtils.putString(context, Constant.CURRENT_POINT_ADCODE, model.getData().getRangeNum());

        updateBanner();

//        setSkinSource();
    }

    //动态换肤
    private void setSkinSource() {
        dynamicSkinPresenter.getSkinSource(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, ""), SPUtils.getString(context, Constant.CURRENT_LOCATION_SOURCE, ""));
    }

    //动态换肤接口回调
    @Override
    public void getSkinSource(SkinModel.DataBean dataBean, boolean getSkinUrl) {
        this.getSkinUrl = getSkinUrl;
        if (downImagePrensenter == null) {
            downImagePrensenter = new DownImagePrensenter(context, this);
        }
        if (getSkinUrl) {
            // 保存皮肤本地url
            SPUtils.putString(context, SKIN_BACKGROUND_URL, dataBean.getBackgroundUrl());
            SPUtils.putString(context, SKIN_LOGO_URL, dataBean.getLogoUrl());
            downImagePrensenter.skinSource(dataBean);
        }
    }

    @Override
    public void getSkinFinish(SkinModel.DataBean dataBean) {
        delSDcardData(BACKGROUND_PATH, dataBean.getBackgroundUrl());
        delSDcardData(LOGO_PATH, dataBean.getLogoUrl());
    }

    // 删除无用的本地数据
    private void delSDcardData(String pathName, String url) {
        File file = new File(pathName);
        // 获取目录下的所有文件或文件夹
        File[] folders = file.listFiles();
        try {
            for (File f : folders) {
                String folderName = f.getName();
                if (folderName.contains(".")) {
                    folderName = folderName.substring(0, folderName.lastIndexOf("."));
                }
                if (!folderName.equals(MD5Utils.md5(url))) {
                    f.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearError(String message) {

    }

    @Override
    public void clearSuccess(TerminalModel terminalModel) {

    }

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private void sendPolling() {
        mCompositeDisposable.clear();
        DisposableObserver<Long> disposableObserver = getTimeDemoObserver();
        Observable.interval(300, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    private DisposableObserver getTimeDemoObserver() {
        return new DisposableObserver<Object>() {
            @Override
            public void onNext(Object o) {
                try {
                    sendPollingPackage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }
        };
    }

    // 发送心跳包
    private void sendPollingPackage() {
        JSONArray terminalInfo = new JSONArray();
        List<TerminalModel> plasticBottleList = dbManager.getDevListByType(Constant.DEV_PLASTIC_TYPE);
        List<TerminalModel> glassBottleList = dbManager.getDevListByType(Constant.DEV_GLASS_TYPE);
        List<TerminalModel> paperList = dbManager.getDevListByType(Constant.DEV_PAPER_TYPE);
        List<TerminalModel> spinList = dbManager.getDevListByType(Constant.DEV_SPIN_TYPE);
        List<TerminalModel> metalList = dbManager.getDevListByType(Constant.DEV_METAL_TYPE);
        List<TerminalModel> plasticList = dbManager.getDevListByType(Constant.DEV_CEMENT_TYPE);
        List<TerminalModel> harmfulList = dbManager.getDevListByType(Constant.DEV_POISON_TYPE);

        BucketStatusModel plastic = dbManager.getModel(NormalConstant.TYPE_PLASTIC);
        BucketStatusModel glass = dbManager.getModel(NormalConstant.TYPE_GLASS);

        int tempI = 0;
        int bottleNum = 0;
        String terminalRunStatus = "";  //设备运行状态

        if (plasticBottleList != null && plasticBottleList.size() > 0) {
            for (int i = 0; i < plasticBottleList.size(); i++) {
                bottleNum += plasticBottleList.get(i).getWeight();
            }
        }

        if (glassBottleList != null && glassBottleList.size() > 0) {
            for (int i = 0; i < glassBottleList.size(); i++) {
                bottleNum += glassBottleList.get(i).getWeight();
            }
        }

        for (int i = 0; plasticBottleList != null && i < plasticBottleList.size(); i++) {
            if (plastic != null) {
                if (plastic.getCOLUMN_EQU_FAULT_STA() == 1) {
                    // 设备故障
                    terminalRunStatus = "fault";
                } else {
                    if (bottleNum >= BOTTOM_FULL_NUM && plastic.getCOLUMN_REC_BUCKET_STA() == 0) {
                        terminalRunStatus = "full";
                    } else {
                        terminalRunStatus = "running";
                    }
                }
            }
            // 后续会加上桶满判断（可能存在多个串联桶）
            TerminalModel model = plasticBottleList.get(i);
            try {
                JSONObject object = new JSONObject();
                object.put("terminalId", model.getTerminalId());
                object.put("sourceCount", model.getWeight());
                object.put("terminalRunStatus", terminalRunStatus);
                terminalInfo.put(tempI, object);
                tempI = tempI + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; glassBottleList != null && i < glassBottleList.size(); i++) {
            if (glass != null) {
                if (glass.getCOLUMN_EQU_FAULT_STA() == 1) {
                    // 设备故障
                    terminalRunStatus = "fault";
                } else {
                    if (bottleNum >= BOTTOM_FULL_NUM && glass.getCOLUMN_REC_BUCKET_STA() == 0) {
                        terminalRunStatus = "full";
                    } else {
                        terminalRunStatus = "running";
                    }
                }
            }

            TerminalModel model = glassBottleList.get(i);
            try {
                JSONObject object = new JSONObject();
                object.put("terminalId", model.getTerminalId());
                object.put("sourceCount", model.getWeight());
                object.put("terminalRunStatus", terminalRunStatus);
                terminalInfo.put(tempI, object);
                tempI = tempI + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < paperList.size(); i++) {
            TerminalModel terminalModel = paperList.get(i);
            BucketStatusModel bucketStatusModel = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("1" + i)));
            if (terminalModel == null || bucketStatusModel == null) {
                continue;
            }

            if (bucketStatusModel.getCOLUMN_EQU_FAULT_STA() == 1) {
                // 设备故障
                terminalRunStatus = "fault";
            } else {
                if (terminalModel.getWeight() > Constant.MAX_BUCKET_PAPER_WEIGHT && bucketStatusModel.getCOLUMN_REC_BUCKET_STA() == 0) {
                    // 满了
                    terminalRunStatus = "full";
                } else {
                    terminalRunStatus = "running";

                }
            }

            try {
                JSONObject object = new JSONObject();
                object.put("terminalId", terminalModel.getTerminalId());
                object.put("sourceCount", terminalModel.getWeight());
                object.put("terminalRunStatus", terminalRunStatus);
                terminalInfo.put(tempI, object);
                tempI = tempI + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        for (int i = 0; i < spinList.size(); i++) {
            TerminalModel terminalModel = spinList.get(i);
            BucketStatusModel bucketStatusModel = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("2" + i)));
            if (terminalModel == null || bucketStatusModel == null) {
                continue;
            }

            if (bucketStatusModel.getCOLUMN_EQU_FAULT_STA() == 1) {
                // 设备故障
                terminalRunStatus = "fault";
            } else {
                if (terminalModel.getWeight() > Constant.MAX_BUCKET_SPIN_WEIGHT && bucketStatusModel.getCOLUMN_REC_BUCKET_STA() == 0) {
                    // 满了
                    terminalRunStatus = "full";
                } else {
                    terminalRunStatus = "running";
                }
            }

            try {
                JSONObject object = new JSONObject();
                object.put("terminalId", terminalModel.getTerminalId());
                object.put("sourceCount", terminalModel.getWeight());
                object.put("terminalRunStatus", terminalRunStatus);
                terminalInfo.put(tempI, object);
                tempI = tempI + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < metalList.size(); i++) {
            TerminalModel terminalModel = metalList.get(i);
            BucketStatusModel bucketStatusModel = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("3" + i)));

            if (terminalModel == null || bucketStatusModel == null) {
                continue;
            }

            if (bucketStatusModel.getCOLUMN_EQU_FAULT_STA() == 1) {
                // 设备故障
                terminalRunStatus = "fault";
            } else {
                if (terminalModel.getWeight() > Constant.MAX_BUCKET_METAL_WEIGHT && bucketStatusModel.getCOLUMN_REC_BUCKET_STA() == 0) {
                    // 满了
                    terminalRunStatus = "full";
                } else {
                    terminalRunStatus = "running";
                }
            }

            try {
                JSONObject object = new JSONObject();
                object.put("terminalId", terminalModel.getTerminalId());
                object.put("sourceCount", terminalModel.getWeight());
                object.put("terminalRunStatus", terminalRunStatus);
                terminalInfo.put(tempI, object);
                tempI = tempI + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < plasticList.size(); i++) {
            TerminalModel terminalModel = plasticList.get(i);
            BucketStatusModel bucketStatusModel = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("4" + i)));

            if (terminalModel == null || bucketStatusModel == null) {
                continue;
            }

            if (bucketStatusModel.getCOLUMN_EQU_FAULT_STA() == 1) {
                // 设备故障
                terminalRunStatus = "fault";
            } else {
                if (terminalModel.getWeight() > Constant.MAX_BUCKET_PLASTIC_WEIGHT && bucketStatusModel.getCOLUMN_REC_BUCKET_STA() == 0) {
                    // 满了
                    terminalRunStatus = "full";
                } else {
                    terminalRunStatus = "running";
                }
            }
            try {
                JSONObject object = new JSONObject();
                object.put("terminalId", terminalModel.getTerminalId());
                object.put("sourceCount", terminalModel.getWeight());
                object.put("terminalRunStatus", terminalRunStatus);
                terminalInfo.put(tempI, object);
                tempI = tempI + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < harmfulList.size(); i++) {
            TerminalModel model = harmfulList.get(i);
            try {
                JSONObject object = new JSONObject();
                object.put("terminalId", model.getTerminalId());
                object.put("sourceCount", model.getWeight());
                object.put("terminalRunStatus", "running");
                terminalInfo.put(tempI, object);
                tempI = tempI + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 找出PCB最小版本号的值
        int pcbVersion = 1;
        List<Integer> pcbVersionList = new ArrayList<>();
        List<BucketStatusModel> mList = dbManager.getAllDataExceptGlass();
        for (int i = 0; i < mList.size(); i++) {
            BucketStatusModel bucketStatusModel = mList.get(i);
            if (Utils.isEmpty(bucketStatusModel.getVersion())) {
                pcbVersionList.add(0);
            } else {
                pcbVersionList.add(Integer.valueOf(bucketStatusModel.getVersion()));
            }
        }
        if (pcbVersionList.size() != 0) {
            pcbVersion = Collections.min(pcbVersionList);
        }

        System.out.println("心跳数据：\n" + terminalInfo.toString());
        String mobileVersion = ApkUtils.getVersion(context, getPackageName());
        pointPresenter.sendPolling(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID), terminalInfo, mobileVersion, pcbVersion + "");

        // 重新启动（06:00--06:30 或00:00 -- 00:30）
        if (TimeUtils.isRebootTime(context) || TimeUtils.isFirstRebootTime(context)) {
            Log.i(TAG, "重新启动设备");
            SystemCmdUtils.reboot();
            return;
        }

        // 设置是否静音（08:00--22:00）
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }

        if (Utils.isEmpty(channelInfo)) {
            channelInfo = ApkUtils.getAppMetaData(context, "UMENG_CHANNEL");
        }
        if (TimeUtils.isVolumeMute(context)) {
            Log.i(TAG, "设置声音");
            if (!"zyhs".equals(channelInfo)) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 6, AudioManager.FLAG_PLAY_SOUND);
            } else {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 7, AudioManager.FLAG_PLAY_SOUND);
            }
        } else {
            Log.i(TAG, "静音");
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
        }

        // 关闭Hdmi（00:30--01:00）
        if (TimeUtils.isBelongCloseHdmi(context)) {
            Log.i(TAG, "关闭HDMI");
            SystemCmdUtils.closeHdmi();
        }

//        // 6月1日清空电表数据（10:00--11:00）
//        if (TimeUtils.isBelong61_clear_electricmeter(context)) {
//            Log.i(TAG, "清空电表数据");
//            timeQueryUtils.clearClock();
//        }

        // 上传电表（01:00--02:00 或者11:00--12:00）
        if (TimeUtils.isBelongAmmeter(context) || TimeUtils.isBelongAmmeter_afternoon(context)) {
            // 电表数据上传
            if (timeQueryUtils != null) {
                Log.i(TAG, "上传电表数据");
                timeQueryUtils.sendClock();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Utils.isEmpty(electricMeterData)) {
                            if (serialPortUtilsClock != null) {
                                serialPortUtilsClock.closePort();
                                serialPortUtilsClock = null;
                            }
                            serialPortUtilsClock = new SerialPortUtils();
                            serialPortUtilsClock.setPathAndBaurate("/dev/ttyS1", "9600");
                            serialPortUtilsClock.setSerialType(Util.SERIAL_PORT_METER);
                            serialPortUtilsClock.openPort();
                        }
                    }
                }, 1000);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Utils.isEmpty(electricMeterData)) {
                            timeQueryUtils.sendClock();
                        }
                    }
                }, 2000);
            }
        }

        // 更新应用（02:00--03:00）
        if (TimeUtils.isBelongUpdate(context)) {
            Log.i(TAG, "应用升级");
            updateApp();
        }

        // 更新广告（03:00--04:00）
        if (TimeUtils.isUpdateBanner(context)) {
            updateBanner();
        }

        // 更新PCB（04:00--05:00）
        if (TimeUtils.isUpdatePCB(context)) {
            Intent intent = new Intent(context, PcbProgramUpdateActivity.class);
            intent.putExtra(Constant.IS_AUTO_UPDATE, true);
            startActivity(intent);
        }

        // 更新点位（08:00--09:00）
        if (TimeUtils.isUpdateLocation(context)) {
            Log.i(TAG, "更新点位");
            updateLocation();
        }

        // 系统维护期（00:00--08:00）（晚上不发送数据）
        if (TimeUtils.isSystemRestTime(context)) {
            Log.i(TAG, "系统维护期（不上传满溢信息）");
            return;
        }

        if ("yintai".equals(channelInfo) && !isRequestCoupons) {

            yintaiCouponsPresenter.getYintaiCoupons(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID));
        }

//        if (!getSkinUrl) {
//            setSkinSource();
//        }

        // 各个回收桶满溢状况
        for (int i = 0; i < plasticBottleList.size(); i++) {
            BucketStatusModel plasticModel = dbManager.getModel(NormalConstant.TYPE_PLASTIC);

            if (plasticModel != null) {
                if (plasticModel.getCOLUMN_REC_BUCKET_STA() == 0 && bottleNum > BOTTOM_FULL_NUM) {
                    EventBus.getDefault().post(new AlarmModel(Command.getTerminalId(plasticModel.getCOLUMN_ID()), Constant.EXCEPTION_RECYCLE_BUCKET_FULL));
                }
                int BUCKET_sta_weight = plasticModel.getCOLUMN_BUCKET_STA();
                if (BUCKET_sta_weight == 0 || BUCKET_sta_weight > Constant.BUCKET_FULL_NUM) {
                    EventBus.getDefault().post(new BucketIsFullModel(true, Command.getTerminalId(plasticModel.getCOLUMN_ID())));
                } else {
                    EventBus.getDefault().post(new BucketIsFullModel(false, Command.getTerminalId(plasticModel.getCOLUMN_ID())));
                }
            }

        }


        for (int i = 0; i < paperList.size(); i++) {
            TerminalModel terminalModel = paperList.get(i);
            BucketStatusModel model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("1" + i)));
            if (model == null || terminalModel == null) {
                continue;
            }

            if (model.getCOLUMN_REC_BUCKET_STA() == 0 && terminalModel.getWeight() > Constant.MAX_BUCKET_PAPER_WEIGHT) {// 回收桶已满
                EventBus.getDefault().post(new AlarmModel(Command.getTerminalId(model.getCOLUMN_ID()), Constant.EXCEPTION_RECYCLE_BUCKET_FULL));
            }

            if (model.getCOLUMN_REC_GATE_STA() == 1) {
                dbManager.update_species_fault(model.getCOLUMN_ID(), 1, 1);
                EventBus.getDefault().post(new ExceptionModel(Command.getTerminalId(model.getCOLUMN_ID()), Constant.EXCEPTION_CABINET_OPENING));
            }

            if (model.getCOLUMN_REC_GATE_STA() == 2) {
                dbManager.update_species_fault(model.getCOLUMN_ID(), 1, 2);
                EventBus.getDefault().post(new ExceptionModel(Command.getTerminalId(model.getCOLUMN_ID()), Constant.EXCEPTION_CABINET_FOLDERING));
            }
        }

        for (int i = 0; i < spinList.size(); i++) {
            TerminalModel terminalModel = spinList.get(i);
            BucketStatusModel model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("2" + i)));
            if (model == null || terminalModel == null) {
                continue;
            }
            if (model.getCOLUMN_REC_BUCKET_STA() == 0 && terminalModel.getWeight() > Constant.MAX_BUCKET_SPIN_WEIGHT) {// 回收桶已满
                EventBus.getDefault().post(new AlarmModel(Command.getTerminalId(model.getCOLUMN_ID()), Constant.EXCEPTION_RECYCLE_BUCKET_FULL));
            }

            if (model.getCOLUMN_REC_GATE_STA() == 1) {
                dbManager.update_species_fault(model.getCOLUMN_ID(), 1, 1);
                EventBus.getDefault().post(new ExceptionModel(Command.getTerminalId(model.getCOLUMN_ID()), Constant.EXCEPTION_CABINET_OPENING));
            }

            if (model.getCOLUMN_REC_GATE_STA() == 2) {
                dbManager.update_species_fault(model.getCOLUMN_ID(), 1, 2);
                EventBus.getDefault().post(new ExceptionModel(Command.getTerminalId(model.getCOLUMN_ID()), Constant.EXCEPTION_CABINET_FOLDERING));
            }
        }

        for (int i = 0; i < metalList.size(); i++) {
            TerminalModel terminalModel = metalList.get(i);
            BucketStatusModel model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("3" + i)));
            if (model == null || terminalModel == null) {
                continue;
            }
            if (model.getCOLUMN_REC_BUCKET_STA() == 0 && terminalModel.getWeight() > Constant.MAX_BUCKET_METAL_WEIGHT) {// 回收桶已满
                EventBus.getDefault().post(new AlarmModel(Command.getTerminalId(Command.getTerminalId(model.getCOLUMN_ID())), Constant.EXCEPTION_RECYCLE_BUCKET_FULL));
            }

            if (model.getCOLUMN_REC_GATE_STA() == 1) {
                dbManager.update_species_fault(model.getCOLUMN_ID(), 1, 1);
                EventBus.getDefault().post(new ExceptionModel(Command.getTerminalId(model.getCOLUMN_ID()), Constant.EXCEPTION_CABINET_OPENING));
            }

            if (model.getCOLUMN_REC_GATE_STA() == 2) {
                dbManager.update_species_fault(model.getCOLUMN_ID(), 1, 2);
                EventBus.getDefault().post(new ExceptionModel(Command.getTerminalId(model.getCOLUMN_ID()), Constant.EXCEPTION_CABINET_FOLDERING));
            }
        }

        for (int i = 0; i < plasticList.size(); i++) {
            TerminalModel terminalModel = plasticList.get(i);
            BucketStatusModel model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("4" + i)));
            if (model == null || terminalModel == null) {
                continue;
            }
            if (model.getCOLUMN_REC_BUCKET_STA() == 0 && terminalModel.getWeight() > Constant.MAX_BUCKET_PLASTIC_WEIGHT) {// 回收桶已满
                EventBus.getDefault().post(new AlarmModel(Command.getTerminalId(model.getCOLUMN_ID()), Constant.EXCEPTION_RECYCLE_BUCKET_FULL));
            }

            if (model.getCOLUMN_REC_GATE_STA() == 1) {
                dbManager.update_species_fault(model.getCOLUMN_ID(), 1, 1);
                EventBus.getDefault().post(new ExceptionModel(Command.getTerminalId(model.getCOLUMN_ID()), Constant.EXCEPTION_CABINET_OPENING));
            }

            if (model.getCOLUMN_REC_GATE_STA() == 2) {
                dbManager.update_species_fault(model.getCOLUMN_ID(), 1, 2);
                EventBus.getDefault().post(new ExceptionModel(Command.getTerminalId(model.getCOLUMN_ID()), Constant.EXCEPTION_CABINET_FOLDERING));
            }
        }
    }

    //银泰优惠券回调接口
    @Override
    public void getYintaiCoupons(String couponFileUrl, boolean isRequestCoupons) {

        this.isRequestCoupons = isRequestCoupons;
        SPUtils.putString(context, Constant.YINTAI_COUPONS_URL, couponFileUrl);
    }

    // 广告接口回调
    @Override
    public void showBannerData(List<AdModel.DataBean> bannerModels) {

        if (bannerModels == null || bannerModels.size() == 0) {
            return;
        }

        // 保存数据
        dbManager.clearAdList();
        for (AdModel.DataBean bean : bannerModels) {
            bean.setMd5Name(MD5Utils.md5(bean.getFileUrl()));
            dbManager.insertAd(bean);
        }
        downImagePrensenter.imageDown(bannerModels);
    }

    @Override
    public void overFinish(List<AdModel.DataBean> adList) {
        // 删除无用的本地数据
        File file = new File(Constant.VIDEO_PIC_PATH);
        File[] folders = file.listFiles();// 获取目录下的所有文件或文件夹
        try {
            for (File f : folders) {
                String folderName = f.getName();
                if (folderName.contains(".")) {
                    folderName = folderName.substring(0, folderName.lastIndexOf("."));
                }
                if (!dbManager.queryIsAdExist(folderName)) {
                    f.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        adsList = dbManager.getAdList();
        if (adsList.size() > 0 && banner != null) {
            banner.dataChange(adsList);
        }
    }

    private boolean isUpdate = false;// 是否更新过资源

    private class MainBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            adsList = dbManager.getAdList();
            if (Constant.UPDATE_BANNER_NOTIFY.equals(action) && banner != null && adsList != null && adsList.size() > 0) {
                isUpdate = true;
                return;
            }

            if (Constant.NEED_UPDATE_NOTIFY.equals(action)) {
                // 应用升级
                updateApp();
                return;
            }

            if (Constant.NEED_UPDATE_BANNER_NOTIFY.equals(action)) {
                // 广告升级
                updateBanner();
                return;
            }

            if (Constant.NEED_UPDATE_LOCATION_NOTIFY.equals(action)) {
                // 点位升级
                updateLocation();
            }
        }
    }

    // 广告升级
    private void updateBanner() {
        if (mBannerPresenter == null) {
            mBannerPresenter = new BannerPresenter(context);
            mBannerPresenter.attachView(this);
        }
        if (downImagePrensenter == null) {
            downImagePrensenter = new DownImagePrensenter(context, this);
        }
        mBannerPresenter.getAds(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID), SPUtils.getString(context, Constant.CURRENT_LOCATION_SOURCE));
    }

    // 应用升级
    private void updateApp() {
        if (alarmIntent == null) {
            alarmIntent = new Intent(this, UpdateService.class);
        }
        startService(alarmIntent);
    }

    // 点位升级
    private void updateLocation() {
        if (pointPresenter == null) {
            pointPresenter = new PointPresenter(context);
            pointPresenter.attachView(this);
        }
        pointPresenter.updateLocationInfo(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, ""));
    }


    // 巡检日志回调
    @Override
    public void inspectionCallback() {

    }
}
