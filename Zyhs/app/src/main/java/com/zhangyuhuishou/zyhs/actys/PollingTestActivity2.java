package com.zhangyuhuishou.zyhs.actys;

import android.os.Handler;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.widget.ImageTextButton;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import com.zhangyuhuishou.zyhs.base.BaseApplication;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.TerminalModel;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortUtils;
import com.zhangyuhuishou.zyhs.time.TimeQueryUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者:created by author:tlh
 * 日期:2019/1/30 14:52
 * 邮箱:tianlihui2234@live.com
 * 描述: 轮询测试
 */
public class PollingTestActivity2 extends BaseActivity {

    // 设备编号
    @BindView(R.id.dev_code)
    TextView dev_code;

    // 设备地址
    @BindView(R.id.dev_info)
    TextView dev_info;

    // 返回
    @BindView(R.id.tv_back)
    TextView tv_back;

    // 页面提示
    @BindView(R.id.itb_dev)
    ImageTextButton itb_dev;

    // 轮询按钮
    @BindView(R.id.sw_ctrl)
    SwitchCompat pollingSw;

    // 轮询提示
    @BindView(R.id.tv_ctrl)
    TextView pollingTv;

    // 日志提醒
    @BindView(R.id.tv_log)
    TextView tv_log;

    private SerialPortUtils serialPortUtils;// 串口工具

    private TimeQueryUtils pollingTimeQueryUtils;

    private Handler handler = new Handler();

    private List<TerminalModel> plasticBottleList;
    private List<TerminalModel> glassBottleList;
    private List<TerminalModel> paperList;
    private List<TerminalModel> spinList;
    private List<TerminalModel> metalList;
    private List<TerminalModel> plasticList;
    private List<TerminalModel> harmfulList;
    private DBManager dbManager;

    private long queryAllTime = 0;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_polling_test;
    }


    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        super.initData();

        EventBus.getDefault().register(this);
        itb_dev.setText("机构循环测试(投递门)");

        dbManager = DBManager.getInstance(BaseApplication.getContext());
        plasticBottleList = dbManager.getDevListByType(Constant.DEV_PLASTIC_TYPE);
        glassBottleList = dbManager.getDevListByType(Constant.DEV_GLASS_TYPE);
        paperList = dbManager.getDevListByType(Constant.DEV_PAPER_TYPE);
        spinList = dbManager.getDevListByType(Constant.DEV_SPIN_TYPE);
        metalList = dbManager.getDevListByType(Constant.DEV_METAL_TYPE);
        plasticList = dbManager.getDevListByType(Constant.DEV_CEMENT_TYPE);
        harmfulList = dbManager.getDevListByType(Constant.DEV_POISON_TYPE);

        long doorTimes = 0;

        if (plasticBottleList != null) {
            for (int i = 0; i < plasticBottleList.size(); i++) {
                doorTimes = doorTimes + 1;
            }
        }

        if (glassBottleList != null) {
            for (int i = 0; i < glassBottleList.size(); i++) {
                doorTimes = doorTimes + 1;
            }
        }

        if (paperList != null) {
            for (int i = 0; i < paperList.size(); i++) {
                doorTimes = doorTimes + 1;
            }
        }

        if (spinList != null) {
            for (int i = 0; i < spinList.size(); i++) {
                doorTimes = doorTimes + 1;
            }
        }

        if (metalList != null) {
            for (int i = 0; i < metalList.size(); i++) {
                doorTimes = doorTimes + 1;
            }
        }

        if (plasticList != null) {
            for (int i = 0; i < plasticList.size(); i++) {
                doorTimes = doorTimes + 1;
            }
        }

        queryAllTime = doorTimes * 2000 * 2;

        // 打开串口
        serialPortUtils = new SerialPortUtils();
        serialPortUtils.openPort();

        String currentLocationId = SPUtils.getString(context, Constant.CURRENT_LOCATION_NUM);
        if (!TextUtils.isEmpty(currentLocationId)) {
            dev_code.setText("编号：" + currentLocationId);
        }

        String location = SPUtils.getString(context, Constant.CURRENT_COMMUNITYNAME,"");
        if (!TextUtils.isEmpty(location)) {
            dev_info.setText(location);
        }

        pollingTimeQueryUtils = new TimeQueryUtils(handler,context);
        pollingSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    pollingTv.setText("开启轮询");
                    sendPollingPackage();
                    sendPolling();
                }else {
                    pollingTv.setText("关闭轮询");
                    tv_log.setText("");
                    if (mCompositeDisposable != null) {
                        mCompositeDisposable.clear();
                    }
                    handler.removeMessages(0);
                }
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        tv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (serialPortUtils != null) {
            serialPortUtils.closePort();
            serialPortUtils = null;
        }

        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }

        handler.removeMessages(0);

        EventBus.getDefault().unregister(this);
    }

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private void sendPolling() {
        DisposableObserver<Long> disposableObserver = getTimeDemoObserver();
        Observable.interval(queryAllTime, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    private DisposableObserver getTimeDemoObserver() {
        return new DisposableObserver<Object>() {
            @Override
            public void onNext(Object o) {
                sendPollingPackage();
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
        pollingTimeQueryUtils.setTime(2000);
        pollingTimeQueryUtils.initQueryTime();
        pollingTimeQueryUtils.doOpenSchedule();
        pollingTimeQueryUtils.doCloseSchedule();
    }

    // 测试返回数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String message) {
        if(pollingSw.isChecked()){
            tv_log.setText(message);
        }
        System.out.println(message);
    }
}
