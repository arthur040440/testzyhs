package com.zhangyuhuishou.zyhs.actys;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.ApkUtils;
import com.tlh.android.utils.ClickUtils;
import com.tlh.android.utils.LoginUtils;
import com.tlh.android.utils.MD5Utils;
import com.tlh.android.utils.NetworkUtils;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.utils.ToastUitls;
import com.tlh.android.widget.DownHorizontalProgressBar;
import com.tlh.android.widget.GlideCircleTransform;
import com.tlh.android.widget.HorizontalProgressBar;
import com.tlh.android.widget.dialog.SelfDialogBuilder;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.banner.IBannerView;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import com.zhangyuhuishou.zyhs.database.BucketStatusModel;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.download.savepics.DownImagePrensenter;
import com.zhangyuhuishou.zyhs.download.savepics.IDownFinish;
import com.zhangyuhuishou.zyhs.model.AdModel;
import com.zhangyuhuishou.zyhs.model.DownLoadModel;
import com.zhangyuhuishou.zyhs.model.PcbExceptionModel;
import com.zhangyuhuishou.zyhs.model.PointModel;
import com.zhangyuhuishou.zyhs.model.RangeInfoModel;
import com.zhangyuhuishou.zyhs.model.SkinModel;
import com.zhangyuhuishou.zyhs.model.TerminalModel;
import com.zhangyuhuishou.zyhs.model.UserModel;
import com.zhangyuhuishou.zyhs.presenter.BannerPresenter;
import com.zhangyuhuishou.zyhs.presenter.IPointView;
import com.zhangyuhuishou.zyhs.presenter.PointPresenter;
import com.zhangyuhuishou.zyhs.serialport.constant.NormalConstant;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortUtils;
import com.zhangyuhuishou.zyhs.serialport.message.BucketIsFullModel;
import com.zhangyuhuishou.zyhs.serialport.message.RecvMessage;
import com.zhangyuhuishou.zyhs.serialport.util.ByteUtil;
import com.zhangyuhuishou.zyhs.serialport.util.Command;
import com.zhangyuhuishou.zyhs.service.UpdateService;
import com.zhangyuhuishou.zyhs.time.TimeQueryUtils;
import com.zhangyuhuishou.zyhs.time.TimeUtils;
import com.zhangyuhuishou.zyhs.utils.LocalShared;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import butterknife.BindView;

/**
 * ??????:created by author:tlh
 * ??????:2018/12/14 15:47
 * ??????:tianlihui2234@live.com
 * ??????:????????????
 */


public class MaintainPeopleActivity extends BaseActivity implements IPointView, IBannerView, IDownFinish {

    // ??????
    @BindView(R.id.iv_user_logo)
    ImageView iv_user_logo;

    // ??????
    @BindView(R.id.tv_nickname)
    TextView tv_nickname;

    // ????????????
    @BindView(R.id.tv_dev_code)
    TextView tv_dev_code;

    // ??????
    @BindView(R.id.tv_time)
    TextView tv_time;

    // ????????????
    @BindView(R.id.tv_logout)
    TextView tv_logout;

    // ??????
    @BindView(R.id.tv_ammeter)
    TextView tv_ammeter;

    // wifi
    @BindView(R.id.tv_wifi)
    TextView tv_wifi;

    // ????????????
    @BindView(R.id.tv_net)
    TextView tv_net;

    // ????????????
    @BindView(R.id.tv_self_check)
    TextView tv_self_check;

    // ?????????????????????
    @BindView(R.id.tv_cabinet)
    TextView tv_cabinet;

    // ??????
    @BindView(R.id.tv_lamp)
    TextView tv_lamp;

    // ??????
    @BindView(R.id.tv_temperature)
    TextView tv_temperature;

    // ??????
    @BindView(R.id.tv_fun)
    TextView tv_fun;

    // ??????
    @BindView(R.id.tv_xd)
    TextView tv_xd;

    // ??????
    @BindView(R.id.tv_sx)
    TextView tv_sx;

    // ????????????
    @BindView(R.id.tv_setting)
    TextView tv_setting;

    // ????????????
    @BindView(R.id.tv_sys_setting)
    TextView tv_sys_setting;

    // ??????
    @BindView(R.id.tv_res_save)
    TextView tv_res_save;

    // ??????
    @BindView(R.id.tv_update)
    TextView tv_update;

    // ????????????
    @BindView(R.id.tv_res_test)
    TextView tv_res_test;

    // ?????????
    @BindView(R.id.tv_inspection)
    TextView tv_res_inspection;

    //????????????
    @BindView(R.id.tv_name_company)
    TextView tv_name_company;

    //????????????
    @BindView(R.id.tv_point_description)
    TextView tv_point_description;

    // PCB??????
    @BindView(R.id.tv_pcb_update)
    TextView tv_pcb_update;

    // ????????????
    @BindView(R.id.tv_polling_test)
    TextView tv_polling_test;

    // ???????????????????????????
    @BindView(R.id.tv_polling_test2)
    TextView tv_polling_test2;

    private SerialPortUtils serialPortUtils;// ????????????
    private TimeQueryUtils timeQueryUtils;
    private Handler handler = new Handler();
    private SelfDialogBuilder checkDialog;// ?????????
    private HorizontalProgressBar horizontalProgressBar;
    private CountDownTimer countDownTimer;

    private SelfDialogBuilder downDialog;// ?????????
    private DownHorizontalProgressBar downHorizontalProgressBar;
    private TextView tv_normal_tip;

    private DBManager dbManager;
    private List<TerminalModel> plasticBottleList;
    private List<TerminalModel> glassBottleList;
    private List<TerminalModel> paperList;
    private List<TerminalModel> spinList;
    private List<TerminalModel> metalList;
    private List<TerminalModel> plasticList;
    private List<TerminalModel> harmfulList;
    private BucketStatusModel model;

    private int devNum = 0;// ????????????

    private boolean isDoorStatus = false;// ????????????

    private PointPresenter pointPresenter;

    private Intent updateIntent;// ??????serviceIntent

    private DownImagePrensenter downImagePrensenter;// ?????????????????????
    private BannerPresenter mBannerPresenter;// ??????????????????
    private Intent bannerUpdateIntent;// ????????????????????????

    private String channelInfo;// ????????????

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_maintain_people;
    }

    @Override
    protected void initView() {

    }

    //??????????????????????????????
    private boolean isShowDes;

    @Override
    protected void initData() {
        super.initData();

        channelInfo = ApkUtils.getAppMetaData(context, "UMENG_CHANNEL");
        if ("factory".equals(channelInfo)) {
            tv_update.setVisibility(View.GONE);
            tv_setting.setVisibility(View.GONE);
            tv_res_save.setVisibility(View.GONE);
            tv_polling_test.setVisibility(View.VISIBLE);
            tv_polling_test2.setVisibility(View.VISIBLE);
        }

        tv_name_company.setText(SPUtils.getString(context, Constant.CURRENT_COMMUNITYNAME, ""));
        tv_point_description.setText(SPUtils.getString(context, Constant.CURRENT_RANGENAME, ""));

        tv_point_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //?????????????????????
                if (isShowDes) {
                    ViewGroup.LayoutParams layoutParams = tv_point_description.getLayoutParams();
                    layoutParams.width = 600;
                    tv_point_description.setLayoutParams(layoutParams);

                    tv_point_description.setEllipsize(TextUtils.TruncateAt.END);//??????
                    tv_point_description.setLines(1);
                } else {

                    ViewGroup.LayoutParams layoutParams = tv_point_description.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    tv_point_description.setLayoutParams(layoutParams);

                    tv_point_description.setEllipsize(null);//??????
                    tv_point_description.setSingleLine(false);//???????????????????????????????????????????????????
                }
                isShowDes = !isShowDes;
            }
        });


        // ????????????????????????
        UserModel userModel = UserModel.getInstance();
        Glide.with(this).load(userModel.getProfilePhoto()).centerCrop().transform(new GlideCircleTransform(this)).crossFade(2000).into(iv_user_logo);
        tv_nickname.setText(TextUtils.isEmpty(userModel.getNickName()) ? "????????????" : userModel.getNickName());
        String currentLocationId = SPUtils.getString(context, Constant.CURRENT_LOCATION_NUM);
        String communityName = SPUtils.getString(context, Constant.CURRENT_COMMUNITYNAME, "");
        tv_dev_code.setText(TextUtils.isEmpty(currentLocationId) ? "" : "NO." + currentLocationId + " " + communityName);
        tv_time.setText(TimeUtils.getNowDate());

        // ?????????????????????????????????????????????
        timeQueryUtils = new TimeQueryUtils(handler, context);
        timeQueryUtils.setTime(2000);

        // ????????????
        checkDialog = new SelfDialogBuilder(context);
        checkDialog.setLayoutId(R.layout.dialog_self_check);

        // ?????????
        downDialog = new SelfDialogBuilder(context);
        downDialog.setLayoutId(R.layout.dialog_download);

        dbManager = DBManager.getInstance(context);
        plasticBottleList = dbManager.getDevListByType(Constant.DEV_PLASTIC_TYPE);
        glassBottleList = dbManager.getDevListByType(Constant.DEV_GLASS_TYPE);
        paperList = dbManager.getDevListByType(Constant.DEV_PAPER_TYPE);
        spinList = dbManager.getDevListByType(Constant.DEV_SPIN_TYPE);
        metalList = dbManager.getDevListByType(Constant.DEV_METAL_TYPE);
        plasticList = dbManager.getDevListByType(Constant.DEV_CEMENT_TYPE);
        harmfulList = dbManager.getDevListByType(Constant.DEV_POISON_TYPE);
        freshPageUI();

        devNum = plasticBottleList.size() + glassBottleList.size() + paperList.size() + spinList.size() + metalList.size() + plasticList.size();
        System.out.println("????????????:" + devNum);
        countDownTimer = new CountDownTimer(devNum * 2 * 2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                checkDialog.dismiss();
                freshPageUI();
            }
        };

        // ??????
        pointPresenter = new PointPresenter(context);
        pointPresenter.attachView(this);

        downImagePrensenter = new DownImagePrensenter(context, this);
        mBannerPresenter = new BannerPresenter(context);
        mBannerPresenter.attachView(this);
    }

    @Override
    protected void initListener() {
        super.initListener();
        tv_logout.setOnClickListener(this);
        tv_ammeter.setOnClickListener(this);
        tv_wifi.setOnClickListener(this);
        tv_net.setOnClickListener(this);
        tv_self_check.setOnClickListener(this);
        tv_cabinet.setOnClickListener(this);
        tv_lamp.setOnClickListener(this);
        tv_fun.setOnClickListener(this);
        tv_xd.setOnClickListener(this);
        tv_sys_setting.setOnClickListener(this);
        tv_res_test.setOnClickListener(this);
        tv_res_inspection.setOnClickListener(this);
        tv_pcb_update.setOnClickListener(this);
        tv_temperature.setOnClickListener(this);
        if ("factory".equals(channelInfo)) {
            tv_polling_test.setOnClickListener(this);
            tv_polling_test2.setOnClickListener(this);
        } else {
            tv_res_save.setOnClickListener(this);
            tv_update.setOnClickListener(this);
            tv_setting.setOnClickListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        // ????????????
        serialPortUtils = new SerialPortUtils();
        serialPortUtils.openPort();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (ClickUtils.isFastClick()) {
            return;
        }// ??????????????????
        switch (view.getId()) {
            case R.id.tv_polling_test:
                startActivity(new Intent(context, PollingTestActivity.class));
                break;
            case R.id.tv_polling_test2:
                startActivity(new Intent(context, PollingTestActivity2.class));
                break;
            case R.id.tv_res_save:
                if (mBannerPresenter == null) {
                    mBannerPresenter = new BannerPresenter(context);
                    mBannerPresenter.attachView(this);
                }
                mBannerPresenter.getAds(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID), SPUtils.getString(context, Constant.CURRENT_LOCATION_SOURCE));
                break;
            case R.id.tv_pcb_update:
                startActivity(new Intent(context, PcbProgramUpdateActivity.class));
                break;
            case R.id.tv_logout:
                LoginUtils.logout(context);
                break;
            case R.id.tv_sys_setting:
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                break;
            case R.id.tv_update:
                // ??????
                if (!NetworkUtils.testNetworkStatus(context)) {
                    showNetDialog("????????????", true);
                    return;
                }
                if (updateIntent == null) {
                    updateIntent = new Intent(context, UpdateService.class);
                }
                startService(updateIntent);
                break;
            case R.id.tv_cabinet:
                Intent cabinet = new Intent(context, DevCtrlActivity.class);
                cabinet.putExtra(Constant.DEV_STATUS_MARK, Constant.DEV_STATUS_DOOR);
                startActivity(cabinet);
                break;
            case R.id.tv_lamp:
                Intent lamp = new Intent(context, DevCtrlActivity.class);
                lamp.putExtra(Constant.DEV_STATUS_MARK, Constant.DEV_STATUS_BOX);
                startActivity(lamp);
                break;
            case R.id.tv_fun:
                Intent fun = new Intent(context, DevCtrlActivity.class);
                fun.putExtra(Constant.DEV_STATUS_MARK, Constant.DEV_STATUS_FUN);
                startActivity(fun);
                break;
            case R.id.tv_xd:
                Intent xd = new Intent(context, DevCtrlActivity.class);
                xd.putExtra(Constant.DEV_STATUS_MARK, Constant.DEV_STATUS_DISINFECT);
                startActivity(xd);
                break;
            case R.id.tv_temperature:
                Intent temperature = new Intent(context, DevCtrlActivity.class);
                temperature.putExtra(Constant.DEV_STATUS_MARK, Constant.DEV_STATUS_TEMPERATURE);
                startActivity(temperature);
                break;
            case R.id.tv_ammeter:
                startActivity(new Intent(context, DevAmmeterStatusActivity.class));
                break;
            case R.id.tv_wifi:
                startActivity(new Intent(context, DevWifiStatusActivity.class));
                break;
            case R.id.tv_net:// ????????????
                startActivity(new Intent(context, WifiSettingActivity.class));
                break;
            case R.id.tv_setting:
                Intent intent = new Intent(context, SettingActivity.class);
                intent.putExtra(Constant.LAUNCH_COME_FROM, Constant.LAUNCH_COME_FROM_AllVIEW);
                startActivity(intent);
                break;
            case R.id.tv_self_check:// ????????????
                //   pointPresenter.logUpload(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, ""), "", Constant.LOG_INSPECTION, "");
                if (plasticBottleList != null) {
                    for (int i = 0; i < plasticBottleList.size(); i++) {
                        if (DBManager.getInstance(context).queryIsExist(NormalConstant.TYPE_PLASTIC)) {
                            DBManager.getInstance(context).deleteById(NormalConstant.TYPE_PLASTIC);
                            dbManager.updateCountException(plasticBottleList.get(i).getTerminalId(), 0);

                        }
                    }
                }

                if (glassBottleList != null) {
                    for (int i = 0; i < glassBottleList.size(); i++) {
                        if (DBManager.getInstance(context).queryIsExist(NormalConstant.TYPE_GLASS)) {
                            DBManager.getInstance(context).deleteById(NormalConstant.TYPE_GLASS);
                            dbManager.updateCountException(glassBottleList.get(i).getTerminalId(), 0);
                        }
                    }
                }

                if (paperList != null) {
                    for (int i = 0; i < paperList.size(); i++) {
                        final int k = i;
                        if (DBManager.getInstance(context).queryIsExist(ByteUtil.decimal2fitHex(Integer.valueOf("1" + k)))) {
                            DBManager.getInstance(context).deleteById(ByteUtil.decimal2fitHex(Integer.valueOf("1" + k)));
                        }
                    }
                }

                if (spinList != null) {
                    for (int i = 0; i < spinList.size(); i++) {
                        final int k = i;
                        if (DBManager.getInstance(context).queryIsExist(ByteUtil.decimal2fitHex(Integer.valueOf("2" + k)))) {
                            DBManager.getInstance(context).deleteById(ByteUtil.decimal2fitHex(Integer.valueOf("2" + k)));
                        }
                    }
                }

                if (metalList != null) {
                    for (int i = 0; i < metalList.size(); i++) {
                        final int k = i;
                        if (DBManager.getInstance(context).queryIsExist(ByteUtil.decimal2fitHex(Integer.valueOf("3" + k)))) {
                            DBManager.getInstance(context).deleteById(ByteUtil.decimal2fitHex(Integer.valueOf("3" + k)));
                        }
                    }
                }

                if (plasticList != null) {
                    for (int i = 0; i < plasticList.size(); i++) {
                        final int k = i;
                        if (DBManager.getInstance(context).queryIsExist(ByteUtil.decimal2fitHex(Integer.valueOf("4" + k)))) {
                            DBManager.getInstance(context).deleteById(ByteUtil.decimal2fitHex(Integer.valueOf("4" + k)));
                        }
                    }
                }
                timeQueryUtils.initQueryTime();
                timeQueryUtils.setTime(2000);
                timeQueryUtils.doTimerSchedule();
                timeQueryUtils.doQueryVersionInfo();
                checkDialog.show();
                horizontalProgressBar = (HorizontalProgressBar) checkDialog.findViewById(R.id.hpb_progress);
                horizontalProgressBar.startAnim(devNum * 2 * 2000);
                countDownTimer.start();
                break;
            case R.id.tv_res_test:
                LocalShared.getInstance(context).setFlag(false);
                Intent testIntent = new Intent(context, TestActivity.class);
                startActivity(testIntent);
                break;
            case R.id.tv_inspection:
                startActivity(new Intent(context, MaintainLogActivity.class));
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        if (serialPortUtils != null) {
            serialPortUtils.closePort();
            serialPortUtils = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateIntent != null) {
            stopService(updateIntent);
        }

        if (checkDialog != null) {
            checkDialog.dismiss();
        }

        if (downDialog != null) {
            downDialog.dismiss();
        }
    }

    // ??????????????????
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RecvMessage message) {
        Log.i("SerialPortManager", "?????????????????????" + message.getCommand());
        Command.dataCallBack(message.getCommand());
    }

    private void disDialog() {
        if (!isFinishing() && downDialog != null && downDialog.getDialog() != null && downDialog.getDialog().isShowing()) {
            downDialog.dismiss();
        }
    }

    // ??????????????????
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageUpdate(DownLoadModel downLoadModel) {
        if (downLoadModel == null) {
            return;
        }
        if (downLoadModel.getProgress() == 200) {
            disDialog();
            showNetDialog("??????????????????????????????", true);
            return;
        }
        downDialog.show();
        downHorizontalProgressBar = (DownHorizontalProgressBar) downDialog.findViewById(R.id.hpb_progress);
        if (tv_normal_tip == null) {
            tv_normal_tip = (TextView) downDialog.findViewById(R.id.tv_normal_tip);
        }
        downHorizontalProgressBar.startAnim(downLoadModel.getProgress());
        if (downLoadModel.getProgress() >= 0 && downLoadModel.getProgress() < 100) {
            tv_normal_tip.setText(downLoadModel.getTip());
        } else if (downLoadModel.getProgress() == 100) {
            tv_normal_tip.setText("??????????????????????????????");
        } else {
            tv_normal_tip.setText(downLoadModel.getTip());
        }
        if (downLoadModel.isDissmiss()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    disDialog();
                }
            }, 3000);
        }
    }

    // ????????????UI??????
    private void freshPageUI() {
        for (int i = 0; i < plasticBottleList.size(); i++) {
            model = dbManager.getModel(NormalConstant.TYPE_PLASTIC);
            if (model != null) {
                if (model.getCOLUMN_BUCKET_STA() == 0 || model.getCOLUMN_BUCKET_STA() > Constant.BUCKET_FULL_NUM) {
                    // ???????????????
                    tv_sx.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_maintain_exception, 0, 0, 0);
                } else {
                    tv_sx.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_maintain_ok, 0, 0, 0);
                }

                if (model.getCOLUMN_MAIN_DOOR_STA() == 1) {
                    // ?????????
                    isDoorStatus = true;
                    tv_cabinet.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_maintain_exception, 0, 0, 0);
                } else {
                    // ?????????
                    isDoorStatus = false;
                    tv_cabinet.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_maintain_ok, 0, 0, 0);
                }
            }
        }

        for (int i = 0; i < glassBottleList.size(); i++) {
            model = dbManager.getModel(NormalConstant.TYPE_GLASS);
            if (isDoorStatus) {
                break;
            }

            if (model != null && model.getCOLUMN_MAIN_DOOR_STA() == 1) {
                // ?????????
                isDoorStatus = true;
                tv_cabinet.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_maintain_exception, 0, 0, 0);
            } else {
                // ?????????
                isDoorStatus = false;
                tv_cabinet.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_maintain_ok, 0, 0, 0);
            }
        }

        for (int i = 0; i < paperList.size(); i++) {
            model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("1" + i)));
            if (isDoorStatus) {
                break;
            }
            if (model != null && model.getCOLUMN_MAIN_DOOR_STA() == 1) {
                // ?????????
                isDoorStatus = true;
                tv_cabinet.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_maintain_exception, 0, 0, 0);
            } else {
                // ?????????
                isDoorStatus = false;
                tv_cabinet.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_maintain_ok, 0, 0, 0);
            }
        }

        for (int i = 0; i < spinList.size(); i++) {
            model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("2" + i)));
            if (isDoorStatus) {
                break;
            }
            if (model != null && model.getCOLUMN_MAIN_DOOR_STA() == 1) {
                // ?????????
                isDoorStatus = true;
                tv_cabinet.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_maintain_exception, 0, 0, 0);
            } else {
                // ?????????
                isDoorStatus = false;
                tv_cabinet.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_maintain_ok, 0, 0, 0);
            }
        }

        for (int i = 0; i < metalList.size(); i++) {
            model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("3" + i)));
            if (isDoorStatus) {
                break;
            }
            if (model != null && model.getCOLUMN_MAIN_DOOR_STA() == 1) {
                // ?????????
                isDoorStatus = true;
                tv_cabinet.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_maintain_exception, 0, 0, 0);
            } else {
                // ?????????
                isDoorStatus = false;
                tv_cabinet.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_maintain_ok, 0, 0, 0);
            }
        }

        for (int i = 0; i < plasticList.size(); i++) {
            model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("4" + i)));
            if (isDoorStatus) {
                break;
            }
            if (model != null && model.getCOLUMN_MAIN_DOOR_STA() == 1) {
                // ?????????
                isDoorStatus = true;
                tv_cabinet.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_maintain_exception, 0, 0, 0);
            } else {
                // ?????????
                isDoorStatus = false;
                tv_cabinet.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_maintain_ok, 0, 0, 0);
            }
        }
    }

    // ???????????????
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bucketStatus(BucketIsFullModel model) {
        SPUtils.putBoolean(context, Constant.DEV_BUCKET_STATUS, model.isFull());
        if (model.isFull()) {
            pointPresenter.logUpload(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, ""), model.getTerminalId(), Constant.LOG_ALARM, Constant.EXCEPTION_WATER_BUCKET_FULL);
        }
    }

    // ???????????????PCB???
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void rangePcbException(PcbExceptionModel model) {
        pointPresenter.logUpload(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, ""), model.getTerminalId(), Constant.LOG_PCB_EXCEPTION, model.getDes());
    }

    // ????????????
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
    // ????????????

    @Override
    public void showBannerData(List<AdModel.DataBean> bannerModels) {
        if (bannerModels == null || bannerModels.size() == 0) {
            return;
        }

        DBManager manager = DBManager.getInstance(context);
        // ????????????????????????????????????
        manager.clearAdList();
        for (AdModel.DataBean bean : bannerModels) {
            bean.setMd5Name(MD5Utils.md5(bean.getFileUrl()));
            manager.insertAd(bean);
        }
        if (downImagePrensenter == null) {
            downImagePrensenter = new DownImagePrensenter(context, this);
        }
        downImagePrensenter.imageSave(bannerModels);
    }

    @Override
    public void overFinish(List<AdModel.DataBean> adList) {
        // ???????????????????????????
        DBManager dbManager = DBManager.getInstance(context);
        File file = new File(Constant.VIDEO_PIC_PATH);
        File[] folders = file.listFiles();// ??????????????????????????????????????????
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
        if (bannerUpdateIntent == null) {
            bannerUpdateIntent = new Intent(Constant.UPDATE_BANNER_NOTIFY);
        }
        bannerUpdateIntent.putExtra(Constant.PACKAGE_NAME_PREFERENCES, (Serializable) adList);
        sendBroadcast(bannerUpdateIntent);
        ToastUitls.toastMessage("????????????????????????");
    }

    @Override
    public void getSkinFinish(SkinModel.DataBean dataBean) {


    }
}
