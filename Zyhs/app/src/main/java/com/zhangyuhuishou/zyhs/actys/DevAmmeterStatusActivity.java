package com.zhangyuhuishou.zyhs.actys;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.ClickUtils;
import com.tlh.android.utils.NetworkUtils;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.utils.ToastUitls;
import com.tlh.android.utils.Utils;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import com.zhangyuhuishou.zyhs.model.ElecUploadModel;
import com.zhangyuhuishou.zyhs.model.PointModel;
import com.zhangyuhuishou.zyhs.model.RangeInfoModel;
import com.zhangyuhuishou.zyhs.model.TerminalModel;
import com.zhangyuhuishou.zyhs.presenter.IPointView;
import com.zhangyuhuishou.zyhs.presenter.PointPresenter;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortUtils;
import com.zhangyuhuishou.zyhs.serialport.message.CloseDoorModel;
import com.zhangyuhuishou.zyhs.serialport.message.RecvMessage;
import com.zhangyuhuishou.zyhs.serialport.util.ByteUtil;
import com.zhangyuhuishou.zyhs.serialport.util.Util;
import com.zhangyuhuishou.zyhs.time.TimeQueryUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者:created by author:tlh
 * 日期:2018/8/10 17:31
 * 邮箱:tianlihui2234@live.com
 * 描述:设备状态（电表）
 */

public class DevAmmeterStatusActivity extends BaseActivity implements IPointView {

    // 具体设备
    @BindView(R.id.itb_dev)
    TextView itb_dev;

    // 设备编号
    @BindView(R.id.dev_code)
    TextView dev_code;

    // 设备地址
    @BindView(R.id.dev_info)
    TextView dev_info;

    // 返回
    @BindView(R.id.tv_back_to_pre)
    TextView tv_back_to_pre;

    // 上传电表数据
    @BindView(R.id.tv_update_data)
    TextView tv_update_data;

    // 当前电表读数
    @BindView(R.id.tv_degrees)
    TextView tv_degrees;

    private SerialPortUtils serialPortUtilsClock;// 串口工具
    private TimeQueryUtils timeQueryUtils;
    private Handler handler = new Handler();
    private AlertDialog.Builder builder;

    private PointPresenter pointPresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_dev_ammeter_status;
    }

    @Override
    protected void initView() {}

    @Override
    protected void initData() {
        super.initData();
        pointPresenter = new PointPresenter(context);
        pointPresenter.attachView(this);

        itb_dev.setText("电表状态异常");
        EventBus.getDefault().register(this);
        String currentLocationId = SPUtils.getString(context, Constant.CURRENT_LOCATION_NUM);
        if (!TextUtils.isEmpty(currentLocationId)) {
            String codeId = "编号：" + currentLocationId;
            dev_code.setText(codeId);
        }

        String location = SPUtils.getString(context, Constant.CURRENT_COMMUNITYNAME, "");
        if (!TextUtils.isEmpty(location)) {
            dev_info.setText(location);
        }

        timeQueryUtils = new TimeQueryUtils(handler,context);
        timeQueryUtils.setTime(2000);

        serialPortUtilsClock = new SerialPortUtils();
        serialPortUtilsClock.setPathAndBaurate("/dev/ttyS3", "9600");
        serialPortUtilsClock.setParity(1);
        serialPortUtilsClock.setSerialType(Util.SERIAL_PORT_METER);
        serialPortUtilsClock.openPort();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Utils.isEmpty(tv_degrees.getText().toString().trim())){
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
        },1000);
    }

    @Override
    protected void initListener() {
        super.initListener();
        tv_back_to_pre.setOnClickListener(this);
        tv_update_data.setOnClickListener(this);
    }

    private ProgressDialog uploadDialog;
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_back_to_pre:
                finish();
                break;
            case R.id.tv_update_data:
                if (pointPresenter != null && !Utils.isEmpty(meter)) {
                    String meterCode = SPUtils.getString(context, Constant.CURRENT_DAY, "");
                    pointPresenter.uploadElec(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, ""), meter,meterCode);
                }
                if (uploadDialog == null) {
                    uploadDialog = new ProgressDialog(context);
                    uploadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    uploadDialog.setMessage("正在上传电表数据，请稍等");
                }
                // 判断网络状况
                if (NetworkUtils.testNetworkStatus(context)) {
                    uploadDialog.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cancelUploadDialog();
                        }
                    },10000);
                }else {
                    ToastUitls.toastMessage("网络中断");
                }
                break;
        }
    }


    // 电表数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAmmeter(CloseDoorModel model) {
        // 关闭打开的桶门
        switch (model.getTerminalId()) {
            case "102":
                timeQueryUtils.sendClock();
                break;
        }
    }
    private String meter;// 电表数据
    // 串口返回数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RecvMessage message) {
        Log.i("SerialPortManager", "收到反馈信息：" + message.getCommand());
        if (message.getCommand().startsWith("010404")) {
            meter = ByteUtil.ieeeToFloat(message.getCommand().substring(6, 14)) + "";
            String currentElectricity = "当前电量：" + meter + "（kW·h）";
            System.out.println(currentElectricity);
            tv_degrees.setText(currentElectricity);
            itb_dev.setText("电表状态正常");
        }
    }

    @OnClick({R.id.tv_clear_data})
    public void onSelfClick(View v) {
        if (ClickUtils.isFastClick()) {
            return;
        }

        if (Utils.isEmpty(tv_degrees.getText().toString())) {
            return;
        }

        if ("当前电量：0.0（kW·h）".equals(tv_degrees.getText().toString())) {
            return;
        }

        if (builder == null) {
            builder = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_logo).setTitle("电表弹框")
                    .setMessage("是否清零？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            timeQueryUtils.clearClock();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
        }

        builder.create().show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        if (serialPortUtilsClock != null) {
            serialPortUtilsClock.closePort();
            serialPortUtilsClock = null;
        }
    }


    // 电表数据（上传）
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadAmmeter(ElecUploadModel model) {
        cancelUploadDialog();
        if (model.isUploadOk()) {
            ToastUitls.toastMessage("上传成功");
        } else {
            ToastUitls.toastMessage("上传失败");
        }
    }

    // 取消上传弹框
    private void cancelUploadDialog() {
        if (!isFinishing() && uploadDialog != null && uploadDialog.isShowing()) {
            uploadDialog.dismiss();
        }
    }


    @Override
    public void getPoints(List<PointModel.DataBean> points) {

    }

    @Override
    public void activePoint(PointModel.DataBean model) {

    }

    @Override
    public void updatePoint(RangeInfoModel model) {

    }

    @Override
    public void clearError(String message) {

    }

    @Override
    public void clearSuccess(TerminalModel terminalModel) {

    }
}
