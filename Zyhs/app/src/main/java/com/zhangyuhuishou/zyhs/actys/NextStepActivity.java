package com.zhangyuhuishou.zyhs.actys;

import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.LoginUtils;
import com.tlh.android.utils.NumberUtils;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.widget.GlideCircleTransform;
import com.tlh.android.widget.HorizontalProgressBar;
import com.tlh.android.widget.ImageTextButton;
import com.tlh.android.widget.dialog.SelfDialogBuilder;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import com.zhangyuhuishou.zyhs.database.BucketStatusModel;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.PcbExceptionModel;
import com.zhangyuhuishou.zyhs.model.PointModel;
import com.zhangyuhuishou.zyhs.model.PollingPackage;
import com.zhangyuhuishou.zyhs.model.RangeInfoModel;
import com.zhangyuhuishou.zyhs.model.TerminalModel;
import com.zhangyuhuishou.zyhs.model.UserModel;
import com.zhangyuhuishou.zyhs.presenter.IPointView;
import com.zhangyuhuishou.zyhs.presenter.PointPresenter;
import com.zhangyuhuishou.zyhs.serialport.constant.NormalConstant;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortUtils;
import com.zhangyuhuishou.zyhs.serialport.message.CloseDoorModel;
import com.zhangyuhuishou.zyhs.serialport.message.RecvMessage;
import com.zhangyuhuishou.zyhs.serialport.util.ByteUtil;
import com.zhangyuhuishou.zyhs.serialport.util.Command;
import com.zhangyuhuishou.zyhs.time.TimeQueryUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;


/**
 * 作者:created by author:tlh
 * 日期:2018/12/14 15:47
 * 邮箱:tianlihui2234@live.com
 * 描述:清运人员(下一步)
 */


public class NextStepActivity extends BaseActivity implements IPointView {

    private String TAG = NextStepActivity.class.getSimpleName();
    // 头像
    @BindView(R.id.iv_user_logo)
    ImageView iv_user_logo;

    // 昵称
    @BindView(R.id.tv_nickname)
    TextView tv_nickname;

    // 设备信息
    @BindView(R.id.tv_dev_code)
    TextView tv_dev_code;

    // 退出登录
    @BindView(R.id.tv_logout)
    TextView tv_logout;

    // 系统自检
    @BindView(R.id.tv_self_check)
    TextView tv_self_check;

    // 柜门状态
    @BindView(R.id.ll_welcome_use)
    ImageTextButton ll_welcome_use;

    // 故障描述
    @BindView(R.id.tv_default_detail)
    TextView tv_default_detail;

    // 故障与否图标
    @BindView(R.id.iv_zhys)
    ImageView iv_zhys;

    private SerialPortUtils serialPortUtils;// 串口工具

    private SelfDialogBuilder checkDialog;// 自检框
    private HorizontalProgressBar horizontalProgressBar;
    private TimeQueryUtils timeQueryUtils;
    private Handler handler = new Handler();

    private DBManager dbManager;
    private List<TerminalModel> plasticBottleList;
    private List<TerminalModel> glassBottleList;
    private List<TerminalModel> paperList;
    private List<TerminalModel> spinList;
    private List<TerminalModel> metalList;
    private List<TerminalModel> plasticList;
    private List<TerminalModel> harmfulList;
    private BucketStatusModel model;

    private PointPresenter pointPresenter;
    private PollingPackage pollingPackage;

    private int devNum = 0;// 设备数量

    private boolean isDoorStatus = false;// 柜门状态
    private boolean isWaterDoorStatus = false;// 倒水桶状态

    private CountDownTimer countDownTimer, pageCountDownTimer;// 自检倒计时/页面倒计时

    // 倒计时
    @BindView(R.id.tv_countdown_time)
    TextView tv_countdown_time;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_clear_people_next_step;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        super.initData();

        // 设置当前用户信息
        UserModel userModel = UserModel.getInstance();
        Glide.with(this).load(userModel.getProfilePhoto()).centerCrop().transform(new GlideCircleTransform(this)).crossFade(2000).into(iv_user_logo);
        tv_nickname.setText(TextUtils.isEmpty(userModel.getNickName()) ? "章鱼游客" : userModel.getNickName());
        String currentLocationId = SPUtils.getString(context, Constant.CURRENT_LOCATION_NUM);
        String communityName = SPUtils.getString(context, Constant.CURRENT_COMMUNITYNAME, "");
        tv_dev_code.setText(TextUtils.isEmpty(currentLocationId) ? "" : "NO." + currentLocationId + " " + communityName);

        // 自检弹框
        checkDialog = new SelfDialogBuilder(context);
        checkDialog.setLayoutId(R.layout.dialog_self_check);
        dbManager = DBManager.getInstance(context);
        plasticBottleList = dbManager.getDevListByType(Constant.DEV_PLASTIC_TYPE);
        glassBottleList = dbManager.getDevListByType(Constant.DEV_GLASS_TYPE);
        paperList = dbManager.getDevListByType(Constant.DEV_PAPER_TYPE);
        spinList = dbManager.getDevListByType(Constant.DEV_SPIN_TYPE);
        metalList = dbManager.getDevListByType(Constant.DEV_METAL_TYPE);
        plasticList = dbManager.getDevListByType(Constant.DEV_CEMENT_TYPE);
        harmfulList = dbManager.getDevListByType(Constant.DEV_POISON_TYPE);
        devNum = plasticBottleList.size() + glassBottleList.size() + paperList.size() + spinList.size() + metalList.size() + plasticList.size();
        System.out.println("设备数量:" + devNum);


        pointPresenter = new PointPresenter(context);
        pointPresenter.attachView(this);
        pollingPackage = new PollingPackage(context);

        countDownTimer = new CountDownTimer(devNum * 2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                checkDialog.dismiss();
                freshPageUI();
                pollingPackage.sendPollingPackage(pointPresenter);
            }
        };

        EventBus.getDefault().register(this);
        // 初始化命令工具（硬件交互有关）
        timeQueryUtils = new TimeQueryUtils(handler, context);
        timeQueryUtils.setTime(2000);
        // 打开串口
        serialPortUtils = new SerialPortUtils();
        serialPortUtils.openPort();

        pageCountDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String leftTime = millisUntilFinished / 1000 + "s";
                tv_countdown_time.setText(leftTime);
            }

            @Override
            public void onFinish() {
                LoginUtils.logout(context);
            }
        };
    }

    @Override
    protected void initListener() {
        super.initListener();
        tv_logout.setOnClickListener(this);
        tv_self_check.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_logout:
                LoginUtils.logout(context);
                break;
            case R.id.tv_self_check:
                timeQueryUtils.initQueryTime();
                timeQueryUtils.setTime(2000);
                timeQueryUtils.doTimerSchedule();
                checkDialog.show();
                horizontalProgressBar = (HorizontalProgressBar) checkDialog.findViewById(R.id.hpb_progress);
                horizontalProgressBar.startAnim(devNum * 2000);
                countDownTimer.start();
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
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        if (pageCountDownTimer != null) {
            pageCountDownTimer.cancel();
        }
    }

    // 串口返回数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RecvMessage message) {
        Log.i("SerialPortManager", "收到反馈信息：" + message.getCommand());
        Command.dataCallBack(message.getCommand());
    }

    // 桶的状态
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseDoorEvent(CloseDoorModel model) {
        // 关闭打开的桶门
        switch (model.getTerminalId()) {
            case "100":// 串口打开成功可以查询设备在线状态
                timeQueryUtils.initQueryTime();
                timeQueryUtils.setTime(2000);
                timeQueryUtils.doTimerSchedule();
                checkDialog.show();
                horizontalProgressBar = (HorizontalProgressBar) checkDialog.findViewById(R.id.hpb_progress);
                horizontalProgressBar.startAnim(devNum * 2000);
                countDownTimer.start();
                break;
        }
    }


    // 故障上报（PCB）
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void rangePcbException(PcbExceptionModel model) {
        pointPresenter.logUpload(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, ""), model.getTerminalId(), Constant.LOG_PCB_EXCEPTION, model.getDes());
    }

    // 刷新界面UI效果
    private void freshPageUI() {
        isWaterDoorStatus = false;
        isDoorStatus = false;
        tv_default_detail.setText("");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < plasticBottleList.size(); i++) {
            model = dbManager.getModel(NormalConstant.TYPE_PLASTIC);
            if (model != null) {
                if (model.getCOLUMN_BUCKET_STA() == 0 || model.getCOLUMN_BUCKET_STA() > Constant.BUCKET_FULL_NUM) {
                    // 倒水桶状态
                    isWaterDoorStatus = true;
                    builder.append("倒水桶满溢");
                    builder.append("\r\n");
                }

                if (model.getCOLUMN_MAIN_DOOR_STA() == 1) {
                    // 维护门
                    isDoorStatus = true;
                    builder.append("主机侧门没有关好");
                    builder.append("\r\n");
                }

            } else {
                isWaterDoorStatus = true;
                builder.append("倒水桶状态不可知");
                builder.append("\r\n");

                // 维护门
                isDoorStatus = true;
                builder.append("主机侧门状态不可知");
                builder.append("\r\n");
            }
        }

//        for (int i = 0; i < glassBottleList.size(); i++) {
//            model = dbManager.getModel(NormalConstant.TYPE_GLASS);
//            if (isDoorStatus) {
//                break;
//            }
//            if (model.getCOLUMN_MAIN_DOOR_STA() == 1) {
//                // 维护门
//                isDoorStatus = true;
//            }
//        }

        for (int i = 0; i < paperList.size(); i++) {
            model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("1" + i)));
            String num = i + 1 + "";
            if (model != null) {
                if (model.getCOLUMN_MAIN_DOOR_STA() == 1) {
                    // 维护门
                    isDoorStatus = true;
                    builder.append(NumberUtils.toChineseNumber((num)) + "号纸柜门没有关好");
                    builder.append("\r\n");
                }

                if (model.getCOLUMN_REC_BUCKET_STA() == 0) {
                    // 回收桶满溢
                    isDoorStatus = true;
                    builder.append(NumberUtils.toChineseNumber((num)) + "号纸柜门回收桶满溢（检查红外线是否挡住）");
                    builder.append("\r\n");
                }
            } else {
                // 维护门
                isDoorStatus = true;
                builder.append(NumberUtils.toChineseNumber((num)) + "号纸柜门状态不可知");
                builder.append("\r\n");
            }
        }

        for (int i = 0; i < spinList.size(); i++) {
            model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("2" + i)));
            String num = i + 1 + "";
            if (model != null) {
                if (model.getCOLUMN_MAIN_DOOR_STA() == 1) {
                    // 维护门
                    isDoorStatus = true;
                    builder.append(NumberUtils.toChineseNumber((num)) + "号织物柜门没有关好");
                    builder.append("\r\n");
                }
                if (model.getCOLUMN_REC_BUCKET_STA() == 0) {
                    // 回收桶满溢
                    isDoorStatus = true;
                    builder.append(NumberUtils.toChineseNumber((num)) + "号织物柜门回收桶满溢（检查红外线是否挡住）");
                    builder.append("\r\n");
                }
            } else {
                isDoorStatus = true;
                builder.append(NumberUtils.toChineseNumber((num)) + "号织物柜门状态不可知");
                builder.append("\r\n");
            }
        }

        for (int i = 0; i < metalList.size(); i++) {
            model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("3" + i)));
            String num = i + 1 + "";
            if (model != null) {
                if (model.getCOLUMN_MAIN_DOOR_STA() == 1) {
                    // 维护门
                    isDoorStatus = true;
                    builder.append(NumberUtils.toChineseNumber((num)) + "号金属柜门没有关好");
                    builder.append("\r\n");
                }
                if (model.getCOLUMN_REC_BUCKET_STA() == 0) {
                    // 回收桶满溢
                    isDoorStatus = true;
                    builder.append(NumberUtils.toChineseNumber((num)) + "号金属柜门回收桶满溢（检查红外线是否挡住）");
                    builder.append("\r\n");
                }
            } else {
                // 维护门
                isDoorStatus = true;
                builder.append(NumberUtils.toChineseNumber((num)) + "号金属柜门状态不可知");
                builder.append("\r\n");
            }
        }

        for (int i = 0; i < plasticList.size(); i++) {
            model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("4" + i)));
            String num = i + 1 + "";
            if (model != null) {
                if (model.getCOLUMN_MAIN_DOOR_STA() == 1) {
                    // 维护门
                    isDoorStatus = true;
                    builder.append(NumberUtils.toChineseNumber((num)) + "号塑料制品柜门没有关好");
                    builder.append("\r\n");
                }
                if (model.getCOLUMN_REC_BUCKET_STA() == 0) {
                    // 回收桶满溢
                    isDoorStatus = true;
                    builder.append(NumberUtils.toChineseNumber((num)) + "号塑料制品柜门回收桶满溢（检查红外线是否挡住）");
                    builder.append("\r\n");
                }
            } else {
                isDoorStatus = true;
                builder.append(NumberUtils.toChineseNumber((num)) + "号塑料制品柜门状态不可知");
                builder.append("\r\n");
            }
        }

        if (isDoorStatus || isWaterDoorStatus) {
            ll_welcome_use.setText("当前机柜异常");
            tv_self_check.setVisibility(View.VISIBLE);
            tv_default_detail.setText(builder);
            iv_zhys.setImageResource(R.mipmap.ic_fault);
        } else {
            ll_welcome_use.setText("当前机柜正常");
            tv_self_check.setVisibility(View.GONE);
            tv_countdown_time.setVisibility(View.VISIBLE);
            pageCountDownTimer.start();
            iv_zhys.setImageResource(R.mipmap.ic_maintain);
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
