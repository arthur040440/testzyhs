package com.zhangyuhuishou.zyhs.actys;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.tts.util.TTSUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import com.tlh.android.config.Constant;
import com.tlh.android.utils.ApkUtils;
import com.tlh.android.utils.ClickUtils;
import com.tlh.android.utils.InitThrowViewUtils;
import com.tlh.android.utils.LoginUtils;
import com.tlh.android.utils.MD5Utils;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.utils.Utils;
import com.tlh.android.widget.CircularLinesProgress;
import com.tlh.android.widget.GlideCircleTransform;
import com.tlh.android.widget.ImageTextButton;
import com.tlh.android.widget.ImageViewWithTxt;
import com.tlh.android.widget.dialog.SelfDialogBuilder;
import com.tlh.android.widget.dialog.ThrowDialog;
import com.tlh.android.widget.dialog.ThrowDialogBuilder;
import com.zhangke.zlog.ZLog;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.barrage.BarrageView;
import com.zhangyuhuishou.zyhs.barrage.BarrageViewBean;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import com.zhangyuhuishou.zyhs.base.BaseApplication;
import com.zhangyuhuishou.zyhs.database.BucketStatusModel;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.ClassificationModel;
import com.zhangyuhuishou.zyhs.model.DeliveryTipModel;
import com.zhangyuhuishou.zyhs.model.OrderArrayModel;
import com.zhangyuhuishou.zyhs.model.OrderDetailModel;
import com.zhangyuhuishou.zyhs.model.PointModel;
import com.zhangyuhuishou.zyhs.model.PollingPackage;
import com.zhangyuhuishou.zyhs.model.RangeInfoModel;
import com.zhangyuhuishou.zyhs.model.TerminalModel;
import com.zhangyuhuishou.zyhs.model.UserModel;
import com.zhangyuhuishou.zyhs.presenter.IElementCouponsPresenter;
import com.zhangyuhuishou.zyhs.presenter.IElementCouponsView;
import com.zhangyuhuishou.zyhs.presenter.IOrderView;
import com.zhangyuhuishou.zyhs.presenter.IPointView;
import com.zhangyuhuishou.zyhs.presenter.OrderPresenter;
import com.zhangyuhuishou.zyhs.presenter.PointPresenter;
import com.zhangyuhuishou.zyhs.scanner.ScanGunKeyEventHelper;
import com.zhangyuhuishou.zyhs.serialport.constant.NormalConstant;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortManager;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortUtils;
import com.zhangyuhuishou.zyhs.serialport.message.CloseDoorModel;
import com.zhangyuhuishou.zyhs.serialport.message.RecvMessage;
import com.zhangyuhuishou.zyhs.serialport.message.ThingModel;
import com.zhangyuhuishou.zyhs.serialport.util.ByteUtil;
import com.zhangyuhuishou.zyhs.serialport.util.Command;
import com.zhangyuhuishou.zyhs.time.TimeThread;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.BindViews;
import pl.droidsonroids.gif.GifImageView;

/**
 * 作者:created by author:tlh
 * 日期:2018/8/18 11:42
 * 邮箱:tianlihui2234@live.com
 * 描述:分类投递页面
 */
public class ThrowThingsActivity extends BaseActivity implements IOrderView, IPointView {

    private String TAG = ThrowThingsActivity.class.getSimpleName();
    // 设备编号
    @BindView(R.id.tv_dev_code)
    TextView tv_dev_code;
    // 饮料瓶
    @BindView(R.id.iv_drink)
    ImageViewWithTxt iv_drink;
    // 纸张
    @BindView(R.id.iv_paper)
    ImageViewWithTxt iv_paper;
    // 纺织品
    @BindView(R.id.iv_spin)
    ImageViewWithTxt iv_spin;
    // 玻璃
    @BindView(R.id.iv_glass)
    ImageViewWithTxt iv_glass;
    // 有害物质
    @BindView(R.id.iv_garbage)
    ImageViewWithTxt iv_garbage;
    // 塑料制品
    @BindView(R.id.iv_plastic)
    ImageViewWithTxt iv_plastic;
    // 金属
    @BindView(R.id.iv_metal)
    ImageViewWithTxt iv_metal;
    // 其它
    @BindView(R.id.iv_other)
    ImageViewWithTxt iv_other;
    // 结束投递
    @BindView(R.id.tv_finish_throw)
    TextView tv_finish_throw;
    // 主页面倒计时
    @BindView(R.id.tv_countdown_time)
    TextView tv_countdown_time;
    // 用户头像
    @BindView(R.id.iv_user_logo)
    ImageView iv_user_logo;
    // 章鱼币-昵称
    @BindViews({R.id.tv_user_money, R.id.tv_nickname})
    List<TextView> tv_zy_nick;
    // 页面投递布局
    @BindViews({R.id.ll_type_ylp, R.id.ll_type_bl, R.id.ll_type_paper, R.id.ll_type_spin, R.id.ll_type_metal, R.id.ll_type_plastic, R.id.ll_type_poison})
    List<LinearLayout> typePages;
    // 最后一个分类（章鱼或者鱼或者河马）
    @BindView(R.id.iv_last_view)
    ImageView iv_last_view;
    // 页面时间
    @BindView(R.id.time)
    TextView time;
    private TimeThread timeThread;// 时间处理器

    // 结束投递（退出登录）
    @BindView(R.id.cv_finish_throw)
    CardView cv_finish_throw;

    // 加载动画
    @BindView(R.id.clp)
    CircularLinesProgress clp;

    // 背景图片
//    @BindView(R.id.th_ctl_bg)
//    ConstraintLayout constraintLayout;

    //设置logo图片
    @BindView(R.id.iv_logo_top)
    ImageView iv_logo_top;

    private CountDownTimer countDownTimer, finalDownTimer, otherDownTimer, poisonBtnDownTimer, couponsDownTimer, noOrderCountDownTimer, orderExceptionCountDownTimer;// 主页面倒计时/展示完成投递按钮倒计时/最终超时倒计时（饮料玻璃柜超时）/其它柜子（称重）超时（60s）/有害物质按钮失活（60s）/优惠券倒计时
    private CountDownTimer exceptionCountDownTimer;// 异常倒计时
    private SelfDialogBuilder throwDialog, poisonDialog, dialogFault, orderDialog, orderAndNetDialog, orderAndNoDialog, couponsDialog, yitaiCouponsDialog, orderExceptionDialog;// （各种弹框）投掷
    private OrderArrayModel orderDataUtils;// 订单数据工具类
    private int errorStatus = 3;// 1 过重 2 过轻 3 没有投递（默认状态) 9计数异常
    private boolean isHasThrowDate = false;

    private OrderPresenter orderPresenter;
    private PointPresenter pointPresenter;// 故障信息

    private SerialPortUtils serialPortUtils;// 串口工具

    private Handler handler = new Handler();

    // 投递框参数
    private ImageTextButton itb_tip;// 弹框提示
    private TextView tv_num;// 投递数目
    private TextView tv_tip;// 物品不合格提示
    private TextView tv_throw_finish, tv_coupons_receive, tv_coupons_end_delivery, tv_coupons_prompt;
    private TextView tv_throw_ok;// 倒计时文字
    private TextView tv_throw_price;//回收物价格
    private GifImageView iv_gif_close;// 关门动画
    private ConstraintLayout ll_throw;// 投递布局
    private TextView tv_unit;// 单位（个/公斤）
    private TextView tv_throw_tip;// 投递提示

    private String currentSourceId = "";// 当前投递
    private InitThrowViewUtils initThrowViewUtils;// 页面初始化工具

    private boolean isOverWeight = false;// 是否超重（纸柜等设备用户长时间没有投掷时使用）

    // 投递框显示参数
    private ConstraintLayout v_plastic_bottle, v_glass_bottle, v_paper, v_spin, v_metal, v_plastic, v_poison;

    //发送心跳包
    private PollingPackage pollingPackage;

    // 渠道信息
    private String channelInfo;

    //银泰优惠券url
    private String couponFileUrl;

    // 投递章鱼币兑换比例
    private List<ClassificationModel.DataBean> priceList = new ArrayList<ClassificationModel.DataBean>();

    //点位编码
    private String pointCode;

    //地区编码
    private String adCode;

    //计数柜用户投递超时时间
    private int countCabinetMillisFuture = 45000;

    //计数柜用户投递超时提醒
    private int countCabinetRemind = 15;

    //称重柜用户投递超时时间
    private int weightCabinetMillisFuture = 60000;

    //称重柜用户投递超时提醒
    private int weightCabinetRemind = 30;

    List<TerminalModel> plasticBottleList;
    List<TerminalModel> glassBottleList;
    List<TerminalModel> paperList;
    List<TerminalModel> spinList;
    List<TerminalModel> metalList;
    List<TerminalModel> plasticList;
    List<TerminalModel> harmfulList;
    DBManager dbManager;

    private String scanBarCode = "";// 扫码枪扫到的内容

    //用户token
    private String token = "";

    @Override
    protected int getLayoutResId() {
//        File file = ApkUtils.getAdFileByFileUrl(Constant.BACKGROUND_PATH + "/", SPUtils.getString(context, Constant.SKIN_BACKGROUND_URL, ""));
//        if (file.exists()) {
//            Glide.with(context)
//                    .load(Constant.BACKGROUND_PATH + "/" + MD5Utils.md5(SPUtils.getString(context, Constant.SKIN_BACKGROUND_URL, "")) + ".jpg")
//                    .asBitmap()
//                    .into(new SimpleTarget<Bitmap>(1080, 1920) { // 括号里可以指定图片宽高
//                        @Override
//                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                            Drawable drawable = new BitmapDrawable(getResources(), resource);
//                            constraintLayout.setBackground(drawable);// 设置背景
//                        }
//                    });
//        }
        return R.layout.activity_throw_things;
    }

    @Override
    protected void initView() {
//        File logoFile = ApkUtils.getAdFileByFileUrl(Constant.LOGO_PATH + "/", SPUtils.getString(context, Constant.SKIN_LOGO_URL, ""));
//        if (logoFile.exists()) {
//            Glide.with(getContext()).load(Constant.LOGO_PATH + "/" + MD5Utils.md5(SPUtils.getString(context, Constant.SKIN_LOGO_URL, "")) + ".jpg").override(315, 92).into(iv_logo_top);
//        }
        // 设置当前用户信息
        UserModel userModel = UserModel.getInstance();
        Glide.with(this).load(userModel.getProfilePhoto()).centerCrop().transform(new GlideCircleTransform(this)).crossFade(2000).into(iv_user_logo);
        tv_zy_nick.get(0).setText(String.valueOf(userModel.getIntegral()));
        tv_zy_nick.get(1).setText(TextUtils.isEmpty(userModel.getNickName()) ? "章鱼游客" : userModel.getNickName());
        String communityName = SPUtils.getString(context, Constant.CURRENT_COMMUNITYNAME, "");
        tv_dev_code.setText(TextUtils.isEmpty(communityName) ? "" : communityName);
        // 初始化页面元素
        initThrowViewUtils = new InitThrowViewUtils(iv_drink, iv_glass, iv_paper, iv_spin, iv_metal, iv_plastic, iv_garbage, this, typePages, iv_last_view);
        initThrowViewUtils.initView(context);
    }

    @Override
    protected void initData() {
        super.initData();

        //（语音提示）请选择您要投递的物品分类
        TTSUtils.speak(Constant.TTS_TIP_CHOOSE_THROW);

        timeThread = new TimeThread(time, TAG);
        timeThread.start();

        channelInfo = ApkUtils.getAppMetaData(BaseApplication.getContext(), "UMENG_CHANNEL");

        orderDataUtils = new OrderArrayModel(context);

        //投递弹框
        throwDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_throw);

        couponsDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_coupons);

//        yitaiCouponsDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_coupons_yintai);

        poisonDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_throw_harmful);

        dialogFault = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_fault);

        orderDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_order);

        orderAndNetDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_order_yes_net_no);// 有订单无网络

        orderAndNoDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_order_no);// 无订单

        orderExceptionDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_order_exception);// 订单异常


        // 点位和异常处理
        pointPresenter = new PointPresenter(context);
        pointPresenter.attachView(this);

        // 订单处理
        orderPresenter = new OrderPresenter(context);
        orderPresenter.attachView(this);

        pollingPackage = new PollingPackage(context);

        // 注册EventBus
        EventBus.getDefault().register(this);

        // 串口（打开）
        serialPortUtils = new SerialPortUtils();
        serialPortUtils.openPort();


        // 如果串口未打开1秒钟尝试再次打开
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ZLog.e(TAG, "是否已经打开串口：" + isOpenPort);
                if (!isOpenPort) {
                    serialPortUtils.openPort();
                }
            }
        }, 500);

        // 主页面倒计时
        countDownTimer = new CountDownTimer(180000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_countdown_time.setVisibility(View.VISIBLE);
                String leftTime = millisUntilFinished / 1000 + "s";
                if (tv_countdown_time != null) {
                    tv_countdown_time.setText(leftTime);
                }
            }

            @Override
            public void onFinish() {
                if (tv_countdown_time != null) {
                    tv_countdown_time.setVisibility(View.GONE);
                }
                if (orderDataUtils.getOrderLength() > 0) {
                    httpOrder(true);
                }
                handler.post(timeOutRunnable);
            }
        };
        countDownTimer.start();

        // 重置命令超时倒计时（超时：关闭门）计数柜
        finalDownTimer = new CountDownTimer(countCabinetMillisFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i("主机超时倒计时", millisUntilFinished / 1000 + "");
                if (millisUntilFinished / 1000 == countCabinetRemind) {
                    if (errorStatus == 3 && tv_tip != null) {
                        tv_tip.setText("投掷品类不正确或者你没有放置物品");
                    } else if (errorStatus == 9) {
                        tv_tip.setText("当前设备已满，请终止投递");
                    }
                }
            }

            @Override
            public void onFinish() {
                if (tv_tip != null) {
                    tv_tip.setText("");
                }
                isOverWeight = true;
                isFault = true;
                closeDoor();// 超时关门
            }
        };

        // 重置命令超时倒计时（超时：关闭门）称重柜
        otherDownTimer = new CountDownTimer(weightCabinetMillisFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i("其它超时倒计时", millisUntilFinished / 1000 + "");
                if (millisUntilFinished / 1000 == weightCabinetRemind) {
                    if (errorStatus == 3) {
                        if (tv_tip != null) {
                            tv_tip.setText("投掷品类不正确或者你没有放置物品");
                        }
                    }
                }
            }

            @Override
            public void onFinish() {
                if (tv_tip != null) {
                    tv_tip.setText("");
                }
                isOverWeight = true;
                isFault = true;
                closeDoor();// 超时关门
            }
        };

        exceptionCountDownTimer = new CountDownTimer(20000, 1000) {

            TextView tv_up_fault = null;

            @Override
            public void onTick(long millisUntilFinished) {
                if (tv_up_fault == null) {
                    tv_up_fault = (TextView) dialogFault.findViewById(R.id.tv_up_fault);
                }
                String leftTimeTip = "即将退出登录（" + millisUntilFinished / 1000 + "）";
                tv_up_fault.setText(leftTimeTip);
            }

            @Override
            public void onFinish() {
                handler.postDelayed(timeOutRunnable, 1);
            }
        };

        poisonBtnDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (iv_garbage != null) {
                    iv_garbage.setEnabled(true);
                }
            }
        };

        orderPresenter.getList(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID), SPUtils.getString(context, Constant.CURRENT_COMMUNITY_ID));

        couponFileUrl = SPUtils.getString(context, Constant.YINTAI_COUPONS_URL, "");

        pointCode = SPUtils.getString(context, Constant.CURRENT_POINT_ADCODE, "");
        adCode = SPUtils.getString(context, Constant.CURRENT_COMMUNITY_ADCODE, "");

        dbManager = DBManager.getInstance(context);
        plasticBottleList = dbManager.getDevListByType(Constant.DEV_PLASTIC_TYPE);
        glassBottleList = dbManager.getDevListByType(Constant.DEV_GLASS_TYPE);
        paperList = dbManager.getDevListByType(Constant.DEV_PAPER_TYPE);
        spinList = dbManager.getDevListByType(Constant.DEV_SPIN_TYPE);
        metalList = dbManager.getDevListByType(Constant.DEV_METAL_TYPE);
        plasticList = dbManager.getDevListByType(Constant.DEV_CEMENT_TYPE);
        harmfulList = dbManager.getDevListByType(Constant.DEV_POISON_TYPE);

        if (UserModel.getInstance().getToken() != null) {
            token = UserModel.getInstance().getToken();
        } else {
            token = SPUtils.getString(context, Constant.USER_TOKEN, "");
        }

    }

    @Override
    public void getRecyclerPrice(List<ClassificationModel.DataBean> data) {
        priceList = data;

    }

    @Override
    protected void initListener() {
        super.initListener();
        // 结束投递
        tv_finish_throw.setOnClickListener(this);
    }

    // 当前投递设备类型
    private int delivery_type;

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (ClickUtils.isFastClick(1000)) {
            return;
        }// 防止快速点击



        handler.removeCallbacks(poisonRunnable);
        cancelTime();
        tv_countdown_time.setVisibility(View.GONE);
        switch (view.getId()) {
            case R.id.iv_drink:// 饮料（塑料）瓶
                initTypeOpen(1001);
                delivery_type = 1001;
                break;
            case R.id.iv_glass:// 玻璃
                initTypeOpen(1002);
                delivery_type = 1002;
                break;
            case R.id.iv_paper:// 纸张
                initTypeOpen(1);
                delivery_type = 1;
                break;
            case R.id.iv_spin:// 纺织品
                initTypeOpen(2);
                delivery_type = 2;
                break;
            case R.id.iv_metal:// 金属
                initTypeOpen(3);
                delivery_type = 3;
                break;
            case R.id.iv_plastic:// 塑料制品
                initTypeOpen(4);
                delivery_type = 4;
                break;
            case R.id.iv_garbage:// 有害垃圾
                initTypeOpen(5);
                delivery_type = 5;
                break;
            // 结束投递
            case R.id.tv_finish_throw:
                // 存在投递数据数据
                if (isHasThrowDate) {
                    closePort();
                    cv_finish_throw.setVisibility(View.GONE);
                    clp.start();
                    // 创建订单提交信息（请求接口）
                    httpOrder(false);
//                    TTSUtils.speak(Constant.TTS_TIP_THANK_YOU_FOR_THIS_DELIVERY);
                } else {
                    // 无订单（和网络无关）
                    orderAndNoDialog.setOnClickListener(R.id.tv_no_order_confirm, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LoginUtils.logout(context);
                        }
                    });
                    orderAndNoDialog.show();// 无订单
                    final TextView tv_no_order_confirm = (TextView) orderAndNoDialog.findViewById(R.id.tv_no_order_confirm);
                    CountDownTimer noOrderCountDownTimer = new CountDownTimer(6000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            String leftTime = "确定（" + millisUntilFinished / 1000 + "）";
                            if (tv_no_order_confirm != null) {
                                tv_no_order_confirm.setText(leftTime);
                            }
                        }

                        @Override
                        public void onFinish() {
                            LoginUtils.logout(context);
                        }
                    };
                    noOrderCountDownTimer.start();
                }
                break;
        }
    }

//    @Override
//    public void onScanResult(String scanValue) {
//        scanBarCode = scanValue;
//        Log.e("scanBarCode", scanBarCode);
//        if (scanBarCode != null && scanBarCode.length() > 0) {
//            Log.e("命令", Command.recoveryBeltPlasticCmd(NormalConstant.DOOR_DOING_OPEN));
//            SerialPortManager.instance().sendCommand(Command.recoveryBeltPlasticCmd(NormalConstant.DOOR_DOING_OPEN));
//        }
//    }

    // 初始化分类打开
    private void initTypeOpen(final int classId) {
        isFault = true;
        if (classId == 5) {
            if (TextUtils.isEmpty(UserModel.getInstance().getToken())) {
                return;
            }
            poisonBtnDownTimer.start();// 按钮失活
            poisonDialog.setOnClickListener(R.id.tv_throw_finish_harmful, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    poisonBtnDownTimer.cancel();
                    handler.removeCallbacks(poisonRunnable);
                    iv_garbage.setEnabled(true);
                    poisonDialog.dismiss();
                    countDownTimer.cancel();
                    countDownTimer.start();
                    if (tv_countdown_time != null) {
                        tv_countdown_time.setVisibility(View.VISIBLE);
                    }
                }
            }).show();
            TextView tv_tip = (TextView) poisonDialog.findViewById(R.id.tv_tip_harmful);
            tv_tip.setVisibility(View.INVISIBLE);
            ImageTextButton itb_tip = (ImageTextButton) poisonDialog.findViewById(R.id.itb_tip_harmful);
            v_poison = (ConstraintLayout) poisonDialog.findViewById(R.id.include_poison_harmful);
            v_poison.setVisibility(View.VISIBLE);
            itb_tip.setText("请至有害物桶投递");
            // 特殊处理
            handler.postDelayed(poisonRunnable, 30000);
            TextView tv_unit = (TextView) poisonDialog.findViewById(R.id.tv_unit_harmful);
            TextView tv_num = (TextView) poisonDialog.findViewById(R.id.tv_num_harmful);
            orderDataUtils.setTextView(tv_num, tv_unit);
            orderDataUtils.dealOrderData(new ThingModel("32", 1));
            orderDataUtils.setPoisonOrder();
            iv_garbage.setEnabled(false);
            isFinishBtn();
            TextView tv_throw_price_harmful = (TextView) poisonDialog.findViewById(R.id.tv_throw_price_harmful);
            if (priceList != null && priceList.size() > 0) {
                String poisonTip = priceList.get(7).getIntegral() + "章鱼币/次";
                tv_throw_price_harmful.setText(poisonTip);
            }
            return;
        }

        throwDialog.setOnClickListener(R.id.tv_throw_finish, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFault = true;
                cancelTime();
                removeRunnable();
                closeDoor();
            }
        }).setClickDismiss(true).show();

//        mThrowDialog.setOnScanDialogListener(this);

        tv_throw_ok = (TextView) throwDialog.findViewById(R.id.tv_throw_ok);
        itb_tip = (ImageTextButton) throwDialog.findViewById(R.id.itb_tip);
        tv_throw_tip = (TextView) throwDialog.findViewById(R.id.tv_throw_tip);

        tv_throw_price = (TextView) throwDialog.findViewById(R.id.tv_throw_price);

        v_plastic_bottle = (ConstraintLayout) throwDialog.findViewById(R.id.include_plastic_bottle);
        v_glass_bottle = (ConstraintLayout) throwDialog.findViewById(R.id.include_glass_bottle);
        v_paper = (ConstraintLayout) throwDialog.findViewById(R.id.include_paper);
        v_spin = (ConstraintLayout) throwDialog.findViewById(R.id.include_spin);
        v_metal = (ConstraintLayout) throwDialog.findViewById(R.id.include_metal);
        v_plastic = (ConstraintLayout) throwDialog.findViewById(R.id.include_plastic);
        v_poison = (ConstraintLayout) throwDialog.findViewById(R.id.include_poison);

        tv_unit = (TextView) throwDialog.findViewById(R.id.tv_unit);
        tv_num = (TextView) throwDialog.findViewById(R.id.tv_num);
        tv_tip = (TextView) throwDialog.findViewById(R.id.tv_tip);

        tv_throw_finish = (TextView) throwDialog.findViewById(R.id.tv_throw_finish);
        tv_coupons_receive = (TextView) throwDialog.findViewById(R.id.tv_coupons_receive);
        tv_coupons_end_delivery = (TextView) throwDialog.findViewById(R.id.tv_coupons_end_delivery);
        tv_coupons_prompt = (TextView) throwDialog.findViewById(R.id.tv_coupons_prompt);

        iv_gif_close = (GifImageView) throwDialog.findViewById(R.id.iv_gif_close);
        ll_throw = (ConstraintLayout) throwDialog.findViewById(R.id.ll_throw);
        iv_gif_close.setVisibility(View.GONE);
        tv_throw_ok.setVisibility(View.GONE);
        itb_tip.setText("门已开启，快去投递吧");
        ll_throw.setVisibility(View.VISIBLE);
        tv_tip.setText("");
        tv_tip.setVisibility(View.VISIBLE);
        tv_throw_tip.setVisibility(View.VISIBLE);
        tv_throw_finish.setVisibility(View.INVISIBLE);
        tv_throw_price.setVisibility(View.VISIBLE);

        tv_coupons_receive.setVisibility(View.GONE);
        tv_coupons_end_delivery.setVisibility(View.GONE);
        tv_coupons_prompt.setVisibility(View.INVISIBLE);

        tv_coupons_end_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCouponsBtnShow = false;
                isFault = true;
                cancelTime();
                removeRunnable();
                closeDoor();
            }
        });

        tv_coupons_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCouponsBtnShow = false;
                isFault = true;
                cancelTime();
                removeRunnable();
                closeDoor();
                if ("yintai".equals(channelInfo)) {
//                    yintaiCouponsDialogShow();
                } else if ("hippo".equals(channelInfo)) {
//                    couponsDialogShow();
                }
                pointPresenter.logUpload(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, ""), Command.getTerminalId(currentSourceId), Constant.LOG_COUPON_COUNT, "1");
            }
        });

        // 设置投递数据
        orderDataUtils.setTextView(tv_num, tv_unit);
        switch (classId) {
            case 1001:
                // 语音提示（左侧投递门正在开启）
                TTSUtils.speak(Constant.TTS_TIP_PLASTIC_BOTTLE_DOOR_OPENING);
                tv_num.setText(orderDataUtils.getPlasticNum() + "");
                tv_unit.setText("个");
                v_plastic_bottle.setVisibility(View.VISIBLE);
                v_glass_bottle.setVisibility(View.GONE);
                v_paper.setVisibility(View.GONE);
                v_spin.setVisibility(View.GONE);
                v_metal.setVisibility(View.GONE);
                v_plastic.setVisibility(View.GONE);
                v_poison.setVisibility(View.GONE);

                if (priceList != null && priceList.size() > 0) {
                    tv_throw_price.setText(priceList.get(1).getIntegral() + "章鱼币/个");
                }

                break;
            case 1002:
                // 语音提示（左侧投递门正在开启）
                TTSUtils.speak(Constant.TTS_TIP_GLASS_BOTTLE_DOOR_OPENING);
                tv_num.setText(orderDataUtils.getGlassNum() + "");
                tv_unit.setText("个");
                v_plastic_bottle.setVisibility(View.GONE);
                v_glass_bottle.setVisibility(View.VISIBLE);
                v_paper.setVisibility(View.GONE);
                v_spin.setVisibility(View.GONE);
                v_metal.setVisibility(View.GONE);
                v_plastic.setVisibility(View.GONE);
                v_poison.setVisibility(View.GONE);

                if (priceList != null && priceList.size() > 0) {
                    tv_throw_price.setText(priceList.get(0).getIntegral() + "章鱼币/个");
                }

                break;
            case 1:
                // 语音提示（右侧投递门正在开启）
                TTSUtils.speak(Constant.TTS_TIP_PAPER_DOOR_OPENING);
                if (orderDataUtils.getLocalPaperNum() < 0) {
                    tv_num.setText("0");
                } else {
                    tv_num.setText(orderDataUtils.dealWeightData(orderDataUtils.getLocalPaperNum()));
                }
                tv_unit.setText("公斤");
                v_plastic_bottle.setVisibility(View.GONE);
                v_glass_bottle.setVisibility(View.GONE);
                v_paper.setVisibility(View.VISIBLE);
                v_spin.setVisibility(View.GONE);
                v_metal.setVisibility(View.GONE);
                v_plastic.setVisibility(View.GONE);
                v_poison.setVisibility(View.GONE);

                if (priceList != null && priceList.size() > 0) {
                    tv_throw_price.setText(priceList.get(3).getIntegral() + "章鱼币/公斤");
                }
                break;
            case 2:
                // 语音提示（右侧投递门正在开启）
                TTSUtils.speak(Constant.TTS_TIP_SPIN_DOOR_OPENING);
                if (orderDataUtils.getLocalClothNum() < 0) {
                    tv_num.setText("0");
                } else {
                    tv_num.setText(orderDataUtils.dealWeightData(orderDataUtils.getLocalClothNum()));
                }
                tv_unit.setText("公斤");
                v_plastic_bottle.setVisibility(View.GONE);
                v_glass_bottle.setVisibility(View.GONE);
                v_paper.setVisibility(View.GONE);
                v_spin.setVisibility(View.VISIBLE);
                v_metal.setVisibility(View.GONE);
                v_plastic.setVisibility(View.GONE);
                v_poison.setVisibility(View.GONE);

                if (priceList != null && priceList.size() > 0) {
                    tv_throw_price.setText(priceList.get(4).getIntegral() + "章鱼币/公斤");
                }
                break;
            case 3:
                // 语音提示（右侧投递门正在开启）
                TTSUtils.speak(Constant.TTS_TIP_METAL_DOOR_OPENING);
                if (orderDataUtils.getLocalMetalNum() < 0) {
                    tv_num.setText("0");
                } else {
                    tv_num.setText(orderDataUtils.dealWeightData(orderDataUtils.getLocalMetalNum()));
                }
                tv_unit.setText("公斤");
                v_plastic_bottle.setVisibility(View.GONE);
                v_glass_bottle.setVisibility(View.GONE);
                v_paper.setVisibility(View.GONE);
                v_spin.setVisibility(View.GONE);
                v_metal.setVisibility(View.VISIBLE);
                v_plastic.setVisibility(View.GONE);
                v_poison.setVisibility(View.GONE);

                if (priceList != null && priceList.size() > 0) {
                    tv_throw_price.setText(priceList.get(6).getIntegral() + "章鱼币/公斤");
                }
                break;
            case 4:
                // 语音提示（右侧投递门正在开启）
                TTSUtils.speak(Constant.TTS_TIP_PLASTIC_DOOR_OPENING);
                if (orderDataUtils.getLocalCementNum() < 0) {
                    tv_num.setText("0");
                } else {
                    tv_num.setText(orderDataUtils.dealWeightData(orderDataUtils.getLocalCementNum()));
                }
                tv_unit.setText("公斤");
                v_plastic_bottle.setVisibility(View.GONE);
                v_glass_bottle.setVisibility(View.GONE);
                v_paper.setVisibility(View.GONE);
                v_spin.setVisibility(View.GONE);
                v_metal.setVisibility(View.GONE);
                v_plastic.setVisibility(View.VISIBLE);
                v_poison.setVisibility(View.GONE);

                if (priceList != null && priceList.size() > 0) {
                    tv_throw_price.setText(priceList.get(5).getIntegral() + "章鱼币/公斤");
                }
                break;
            case 5:
                tv_num.setText(orderDataUtils.getPoisonNum() + "");
                tv_unit.setText("次");
                v_plastic_bottle.setVisibility(View.GONE);
                v_glass_bottle.setVisibility(View.GONE);
                v_paper.setVisibility(View.GONE);
                v_spin.setVisibility(View.GONE);
                v_metal.setVisibility(View.GONE);
                v_plastic.setVisibility(View.GONE);
                v_poison.setVisibility(View.VISIBLE);
                break;
        }
        switch (classId) {
            case 1001:
                currentSourceId = NormalConstant.TYPE_PLASTIC;
                SerialPortManager.instance().sendCommand(Command.recoveryPlasticCmd(NormalConstant.DOOR_DOING_OPEN));
                break;
            case 1002:
                currentSourceId = NormalConstant.TYPE_GLASS;
                SerialPortManager.instance().sendCommand(Command.recoveryGlassCmd(NormalConstant.DOOR_DOING_OPEN));
                break;
            case 1:
                for (int i = 0; i < paperList.size(); i++) {
                    TerminalModel terminalModel = paperList.get(i);
                    BucketStatusModel model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("1" + i)));
                    if (model == null || terminalModel == null) {
                        continue;
                    }

                    if (terminalModel.getWeight() > Constant.MAX_BUCKET_PAPER_WEIGHT && model.getCOLUMN_REC_BUCKET_STA() == 0) {
                        // 满溢
                        continue;
                    }

                    // 可以使用
                    currentSourceId = ByteUtil.decimal2fitHex(Integer.valueOf("1" + i));
                    orderDataUtils.setPaperTerminalModel(paperList.get(i));
                    break;
                }
                if (Utils.isEmpty(currentSourceId)) {
                    currentSourceId = ByteUtil.decimal2fitHex(Integer.valueOf("1" + "0"));
                    orderDataUtils.setPaperTerminalModel(paperList.get(0));
                }
                SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(currentSourceId, NormalConstant.DOOR_DOING_OPEN));

                break;
            case 2:
                for (int i = 0; i < spinList.size(); i++) {
                    BucketStatusModel model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("2" + i)));
                    TerminalModel terminalModel = spinList.get(i);
                    if (model == null || terminalModel == null) {
                        continue;
                    }

                    if (terminalModel.getWeight() > Constant.MAX_BUCKET_SPIN_WEIGHT && model.getCOLUMN_REC_BUCKET_STA() == 0) {
                        // 满溢
                        continue;
                    }

                    // 可以使用
                    currentSourceId = ByteUtil.decimal2fitHex(Integer.valueOf("2" + i));
                    orderDataUtils.setSpinTerminalModel(spinList.get(i));
                    break;
                }
                if (Utils.isEmpty(currentSourceId)) {
                    currentSourceId = ByteUtil.decimal2fitHex(Integer.valueOf("2" + "0"));
                    orderDataUtils.setSpinTerminalModel(spinList.get(0));
                }
                SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(currentSourceId, NormalConstant.DOOR_DOING_OPEN));
                break;
            case 3:
                for (int i = 0; i < metalList.size(); i++) {
                    BucketStatusModel model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("3" + i)));
                    TerminalModel terminalModel = metalList.get(i);
                    if (model == null || terminalModel == null) {
                        continue;
                    }

                    if (terminalModel.getWeight() > Constant.MAX_BUCKET_METAL_WEIGHT && model.getCOLUMN_REC_BUCKET_STA() == 0) {
                        // 满溢
                        continue;
                    }

                    // 可以使用
                    currentSourceId = ByteUtil.decimal2fitHex(Integer.valueOf("3" + i));
                    orderDataUtils.setMetalTerminalModel(metalList.get(i));
                    break;
                }
                if (Utils.isEmpty(currentSourceId)) {
                    currentSourceId = ByteUtil.decimal2fitHex(Integer.valueOf("3" + "0"));
                    orderDataUtils.setMetalTerminalModel(metalList.get(0));
                }
                SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(currentSourceId, NormalConstant.DOOR_DOING_OPEN));
                break;
            case 4:
                for (int i = 0; i < plasticList.size(); i++) {
                    BucketStatusModel model = dbManager.getModel(ByteUtil.decimal2fitHex(Integer.valueOf("4" + i)));
                    TerminalModel terminalModel = plasticList.get(i);
                    if (model == null || terminalModel == null) {
                        continue;
                    }

                    if (terminalModel.getWeight() > Constant.MAX_BUCKET_PLASTIC_WEIGHT && model.getCOLUMN_REC_BUCKET_STA() == 0) {
                        // 满溢
                        continue;
                    }

                    // 可以使用
                    currentSourceId = ByteUtil.decimal2fitHex(Integer.valueOf("4" + i));
                    orderDataUtils.setCementTerminalModel(plasticList.get(i));
                    break;
                }
                if (Utils.isEmpty(currentSourceId)) {
                    currentSourceId = ByteUtil.decimal2fitHex(Integer.valueOf("4" + "0"));
                    orderDataUtils.setCementTerminalModel(plasticList.get(0));
                }
                SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(currentSourceId, NormalConstant.DOOR_DOING_OPEN));
                break;
        }
        handler.postDelayed(openDoorRunnable, 20000);
    }

    // 盒马优惠券展示弹框
//    private void couponsDialogShow() {
//        couponsDialog.setOnClickListener(R.id.tv_throw_receive, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                couponsDialog.dismiss();
//
//            }
//        }).setClickDismiss(true).show();
//
//        final TextView tv_throw_receive = (TextView) couponsDialog.findViewById(R.id.tv_throw_receive);
//        TextView tv_coupons_introduce = (TextView) couponsDialog.findViewById(R.id.tv_coupons_introduce);
//        ImageView iv_coupons_code = (ImageView) couponsDialog.findViewById(R.id.iv_coupons_code);
//
//
//        if (isHemaCouponsActivity) {
//            tv_coupons_introduce.setText("扫码领取盒马10元电子优惠券\n仅限盒马鲜生APP扫码");
//            iv_coupons_code.setImageDrawable(getDrawable(R.mipmap.hema_activity));
//        }
//
//        couponsDownTimer = new CountDownTimer(46000, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                String leftTime = "我已领取（" + millisUntilFinished / 1000 + "s）";
//                if (tv_throw_receive != null) {
//                    tv_throw_receive.setText(leftTime);
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                couponsDialog.dismiss();
//
//            }
//        };
//        couponsDownTimer.start();
//    }

    //    BarrageView barrageView;
    MediaPlayer mediaPlayer;

   /* // 银泰优惠券展示弹框
    private void yintaiCouponsDialogShow() {

        yitaiCouponsDialog.setOnClickListener(R.id.tv_throw_yintai_receive, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yitaiCouponsDialog.dismiss();
//                stopPlay();

            }
        }).setClickDismiss(true).show();

        final TextView tv_throw_receive = (TextView) yitaiCouponsDialog.findViewById(R.id.tv_throw_yintai_receive);
        ImageView iv_coupons_yintai_code = (ImageView) yitaiCouponsDialog.findViewById(R.id.iv_coupons_yintai_code);
//        TextView tv_yintai_yiliao_tips = (TextView) yitaiCouponsDialog.findViewById(R.id.tv_yintai_yiliao_tips);
//        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/hwhb.ttf");
//        tv_yintai_yiliao_tips.setTypeface(typeface);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        List<BarrageViewBean> barrageViews = new ArrayList<>();
//                        barrageView = (BarrageView) yitaiCouponsDialog.findViewById(R.id.barrageview);
//                        barrageViews.add(new BarrageViewBean(getString(R.string.yintai_yiliao_text_1)));
//                        barrageViews.add(new BarrageViewBean(getString(R.string.yintai_yiliao_text_2)));
//                        barrageViews.add(new BarrageViewBean(getString(R.string.yintai_yiliao_text_3)));
//                        barrageViews.add(new BarrageViewBean(getString(R.string.yintai_yiliao_text_4)));
//                        barrageViews.add(new BarrageViewBean(getString(R.string.yintai_yiliao_text_5)));
//                        barrageViews.add(new BarrageViewBean(getString(R.string.yintai_yiliao_text_6)));
//                        barrageViews.add(new BarrageViewBean(getString(R.string.yintai_yiliao_text_7)));
//                        barrageViews.add(new BarrageViewBean(getString(R.string.yintai_yiliao_text_8)));
//                        barrageViews.add(new BarrageViewBean(getString(R.string.yintai_yiliao_text_9)));
//                        barrageViews.add(new BarrageViewBean(getString(R.string.yintai_yiliao_text_10)));
//                        barrageView.setData(barrageViews);
//                        barrageView.start();
//                    }
//                });
//            }
//        }).start();
//
//        Random random = new Random();
//        int num = random.nextInt(10) + 1;
//        try {
//
//            switch (num) {
//                case 1:
//                    tv_yintai_yiliao_tips.setText(getString(R.string.yintai_yiliao_text_1));
//                    fd = context.getApplicationContext().getAssets().openFd("yiliao_1.mp3");
//                    break;
//                case 2:
//                    tv_yintai_yiliao_tips.setText(getString(R.string.yintai_yiliao_text_2));
//                    fd = context.getApplicationContext().getAssets().openFd("yiliao_2.mp3");
//                    break;
//                case 3:
//                    tv_yintai_yiliao_tips.setText(getString(R.string.yintai_yiliao_text_3));
//                    fd = context.getApplicationContext().getAssets().openFd("yiliao_3.mp3");
//                    break;
//                case 4:
//                    tv_yintai_yiliao_tips.setText(getString(R.string.yintai_yiliao_text_4));
//                    fd = context.getApplicationContext().getAssets().openFd("yiliao_4.mp3");
//
//                    break;
//                case 5:
//                    tv_yintai_yiliao_tips.setText(getString(R.string.yintai_yiliao_text_5));
//                    fd = context.getApplicationContext().getAssets().openFd("yiliao_5.mp3");
//
//                    break;
//                case 6:
//                    tv_yintai_yiliao_tips.setText(getString(R.string.yintai_yiliao_text_6));
//                    fd = context.getApplicationContext().getAssets().openFd("yiliao_6.mp3");
//
//                    break;
//                case 7:
//                    tv_yintai_yiliao_tips.setText(getString(R.string.yintai_yiliao_text_7));
//                    fd = context.getApplicationContext().getAssets().openFd("yiliao_7.mp3");
//
//                    break;
//                case 8:
//                    tv_yintai_yiliao_tips.setText(getString(R.string.yintai_yiliao_text_8));
//                    fd = context.getApplicationContext().getAssets().openFd("yiliao_8.mp3");
//
//                    break;
//                case 9:
//                    tv_yintai_yiliao_tips.setText(getString(R.string.yintai_yiliao_text_9));
//                    fd = context.getApplicationContext().getAssets().openFd("yiliao_9.mp3");
//
//                    break;
//                case 10:
//                    tv_yintai_yiliao_tips.setText(getString(R.string.yintai_yiliao_text_10));
//                    fd = context.getApplicationContext().getAssets().openFd("yiliao_10.mp3");
//
//                    break;
//                default:
//                    break;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }


        if (couponFileUrl.length() > 0) {
            Glide.with(this).load(couponFileUrl).into(iv_coupons_yintai_code);
        }

        couponsDownTimer = new CountDownTimer(61000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String leftTime = "我已领取（" + millisUntilFinished / 1000 + "s）";
                if (tv_throw_receive != null) {
                    tv_throw_receive.setText(leftTime);
                }
            }

            @Override
            public void onFinish() {
                yitaiCouponsDialog.dismiss();
//                stopPlay();
            }
        };
        couponsDownTimer.start();

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    mediaPlayer = new MediaPlayer();
//                    mediaPlayer.reset();
//                    mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
//                    mediaPlayer.prepareAsync();
//                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                        @Override
//                        public void onPrepared(MediaPlayer mediaPlayer) {
//                            mediaPlayer.start();
//                        }
//                    });
//                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//                            stopPlay();
//                        }
//                    });
//                } catch (Exception e) {
//                    try {
//                        if (fd != null) {
//                            fd.close();
//                        }
//                    } catch (Exception e1) {
//                    }
//                } finally {
//                    if (fd != null) {
//                        try {
//                            fd.close();
//                        } catch (IOException e) {
//                        }
//                    }
//                }
//            }
//        }, 1000);

    }*/


    /**
     * 结束播放
     */
    private void stopPlay() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = null;
    }


    // 关闭箱门
    private void closeDoor() {
        if (iv_gif_close != null) {
            iv_gif_close.setVisibility(View.VISIBLE);
        }

        if (tv_throw_ok != null) {
            tv_throw_ok.setVisibility(View.GONE);
        }

        if (itb_tip != null) {
            itb_tip.setText("正在关闭箱门");
        }

        if (tv_tip != null) {
            tv_tip.setVisibility(View.GONE);
            tv_tip.setText("");
        }

        if (ll_throw != null) {
            ll_throw.setVisibility(View.GONE);
        }

        if (tv_throw_tip != null) {
            tv_throw_tip.setVisibility(View.GONE);
        }

        if (tv_throw_price != null) {
            tv_throw_price.setVisibility(View.INVISIBLE);
        }

        if (tv_throw_finish != null) {
            tv_throw_finish.setVisibility(View.INVISIBLE);
        }

        if (tv_coupons_receive != null) {
            tv_coupons_receive.setVisibility(View.INVISIBLE);
        }

        if (tv_coupons_end_delivery != null) {
            tv_coupons_end_delivery.setVisibility(View.GONE);
        }

        if (tv_coupons_prompt != null) {
            tv_coupons_prompt.setVisibility(View.GONE);
        }


        if (v_plastic_bottle != null) {
            v_plastic_bottle.setVisibility(View.GONE);
        }
        if (v_glass_bottle != null) {
            v_glass_bottle.setVisibility(View.GONE);
        }
        if (v_paper != null) {
            v_paper.setVisibility(View.GONE);
        }
        if (v_spin != null) {
            v_spin.setVisibility(View.GONE);
        }

        if (v_metal != null) {
            v_metal.setVisibility(View.GONE);
        }

        if (v_plastic != null) {
            v_plastic.setVisibility(View.GONE);
        }

        if (v_poison != null) {
            v_poison.setVisibility(View.GONE);
        }

        // 关闭打开的桶门
        if (NormalConstant.TYPE_PLASTIC.equals(currentSourceId)) {
            SerialPortManager.instance().sendCommand(Command.recoveryPlasticCmd(NormalConstant.DOOR_DOING_CLOSE));
            finalDownTimer.cancel();
        } else if (NormalConstant.TYPE_GLASS.equals(currentSourceId)) {
            // 语音提示（左侧投递门正在关闭）
            SerialPortManager.instance().sendCommand(Command.recoveryGlassCmd(NormalConstant.DOOR_DOING_CLOSE));
            finalDownTimer.cancel();
        } else {
            // 语音提示（右侧投递门正在关闭）
            if (TextUtils.isEmpty(currentSourceId)) {
                return;
            }
            SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(currentSourceId, NormalConstant.DOOR_DOING_CLOSE));
            otherDownTimer.cancel();
        }
        isFromBack = true;
        handler.postDelayed(closeDoorLater, 20000);
    }

    // 订单详情
    @Override
    public void showOrderDetail(OrderDetailModel model) {
        cancelTime();
        removeRunnable();
        pageCancel(model, false);
    }

    //退出当前页面
    private void pageCancel(OrderDetailModel model, Boolean isReview) {
        // 退出当前页面（15秒钟）
        pageJump(15000);
        if (orderDialog != null) {
            orderDialog.setOnClickListener(R.id.tv_submit_order, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pageJump(0);
                }
            });
            orderDataUtils.showOrderDetail(model, orderDialog, isReview);
        } else {
            pageJump(0);
        }

    }

    // 投递结束页面跳转
    private void pageJump(long time) {
        final String rangeSource = SPUtils.getString(context, Constant.CURRENT_LOCATION_SOURCE, "");
        if (Constant.RANGE_SOURCE_NAME_ALI.equals(rangeSource)) {
            handler.postDelayed(jumpToAliRunnable, time);
        } else {
            handler.postDelayed(timeOutRunnable, time);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (countDownTimer != null) {
            countDownTimer.start();
        }
        EventBus.getDefault().register(this);
        if (serialPortUtils == null) {
            serialPortUtils = new SerialPortUtils();
        }
        serialPortUtils.openPort();
        closeDoor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timeThread != null) {
            timeThread.removeTask();
        }
        cancelTime();
        if (couponsDownTimer != null) {
            couponsDownTimer.cancel();
        }
        removeRunnable();
        closePort();
        EventBus.getDefault().unregister(this);
        cancelDialog();
    }

    //关闭串口
    private void closePort() {
        if (serialPortUtils != null) {
            serialPortUtils.closePort();
            serialPortUtils = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clp != null) {
            clp.cancel();
        }
//        barrageView.onDestroy();
        handler.removeMessages(0);
    }

    // 取消弹框
    private void cancelDialog() {
        if (!isFinishing() && throwDialog != null && throwDialog.getDialog() != null && throwDialog.getDialog().isShowing()) {
            throwDialog.dismiss();
        }
        if (!isFinishing() && poisonDialog != null && poisonDialog.getDialog() != null && poisonDialog.getDialog().isShowing()) {
            poisonDialog.dismiss();
        }
        if (!isFinishing() && dialogFault != null && dialogFault.getDialog() != null && dialogFault.getDialog().isShowing()) {
            dialogFault.dismiss();
        }

        if (!isFinishing() && orderDialog != null && orderDialog.getDialog() != null && orderDialog.getDialog().isShowing()) {
            orderDialog.dismiss();
        }

        if (!isFinishing() && orderAndNetDialog != null && orderAndNetDialog.getDialog() != null && orderAndNetDialog.getDialog().isShowing()) {
            orderAndNetDialog.dismiss();
        }

        if (!isFinishing() && orderAndNoDialog != null && orderAndNoDialog.getDialog() != null && orderAndNoDialog.getDialog().isShowing()) {
            orderAndNoDialog.dismiss();
        }

        if (!isFinishing() && couponsDialog != null && couponsDialog.getDialog() != null && couponsDialog.getDialog().isShowing()) {
            couponsDialog.dismiss();
        }

        if (!isFinishing() && yitaiCouponsDialog != null && yitaiCouponsDialog.getDialog() != null && yitaiCouponsDialog.getDialog().isShowing()) {
            yitaiCouponsDialog.dismiss();
        }

        if (!isFinishing() && orderExceptionDialog != null && orderExceptionDialog.getDialog() != null && orderExceptionDialog.getDialog().isShowing()) {
            orderExceptionDialog.dismiss();
        }
    }

    // 串口返回数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RecvMessage message) {
        Log.i("SerialPortManager", "收到反馈信息：" + message.getCommand());
        if (message.getCommand().startsWith("FE")) {
            if (message.getCommand().length() < 12) {
                closePort();
                return;
            }
            if ("02".equals(message.getCommand().substring(10, 12))) {
                // 塑料及玻璃瓶桶上传指令
                if (message.getCommand().length() < 32) {
                    closePort();
                    return;
                }
            } else if ("04".equals(message.getCommand().substring(10, 12))) {
                // 纸张/金属/有害物质/塑料制品/布料上传指令
                if (message.getCommand().length() < 32) {
                    closePort();
                    return;
                }
            } else if ("31".equals(message.getCommand().substring(10, 12))) {
                // 回收桶上传状态
                if (message.getCommand().length() < 34) {
                    closePort();
                    return;
                }
            } else if ("22".equals(message.getCommand().substring(10, 12))) {
                // 维护人员打开面门反馈处理
                if (message.getCommand().length() < 18) {
                    closePort();
                    return;
                }
            } else if ("3F".equals(message.getCommand().substring(10, 12))) {
                // 主板版本信息
                if (message.getCommand().length() < 20) {
                    closePort();
                    return;
                }
            }
        }
        Command.dataCallBack(message.getCommand());
    }

    // 弹框提示（投递过程中）
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDialog(DeliveryTipModel deliveryTipModel) {
        if (tv_tip != null) {
            tv_tip.setVisibility(View.VISIBLE);
        }
        // 重置超时器
        resetTime();
        switch (deliveryTipModel.getTips()) {
            case NormalConstant.RECYCLE_TIP_NOTHING:
                tv_tip.setText("");
                errorStatus = 3;
                break;
            case NormalConstant.RECYCLE_TIP_TOO_HEAVY:
                tv_tip.setText(Constant.TTS_TIP_TOO_HEAVY);
                errorStatus = 1;
                break;
            case NormalConstant.RECYCLE_TIP_TOO_LIGHT:
                tv_tip.setText("物品类型不符合");
                errorStatus = 2;
                break;
            case NormalConstant.RECYCLE_TIP_COUNT_EXCEPTION:
                tv_tip.setText("当前设备已满，请终止投递");
                errorStatus = 9;
                dbManager.updateCountException(deliveryTipModel.getTerminalId(), 1);
                break;
        }
    }


    // 订单接口返回成功或失败
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void orderBack(String tips) {
        switch (tips) {
            case NormalConstant.INTERFACE_ACCESS_FAILURE:// 接口访问失败
                // 有订单（无网络）
                orderAndNetDialog.setOnClickListener(R.id.tv_no_net_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoginUtils.logout(context);
                    }
                });
                orderAndNetDialog.show();
                final TextView tv_no_net_confirm = (TextView) orderAndNetDialog.findViewById(R.id.tv_no_net_confirm);
                noOrderCountDownTimer = new CountDownTimer(30000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        String leftTime = "确定（" + millisUntilFinished / 1000 + "）";
                        if (tv_no_net_confirm != null) {
                            tv_no_net_confirm.setText(leftTime);
                        }
                    }

                    @Override
                    public void onFinish() {
                        LoginUtils.logout(context);
                    }
                };
                noOrderCountDownTimer.start();
                cv_finish_throw.setVisibility(View.VISIBLE);
                if (clp != null) {
                    clp.cancel();
                }
                break;
            case NormalConstant.INTERFACE_ACCESS_SUCCESS:// 接口访问成功
                cv_finish_throw.setVisibility(View.VISIBLE);
                if (clp != null) {
                    clp.cancel();
                }
                break;
            case NormalConstant.STEAL_RECYCLE_EXCEPTION:// 盗取回收物异常处理
                orderExceptionDialog.setOnClickListener(R.id.tv_order_exception_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderExceptionDialog.dismiss();
                        cv_finish_throw.setVisibility(View.VISIBLE);
                        if (clp != null) {
                            clp.cancel();
                        }
                        pageJump(0);
                    }
                });
                orderExceptionDialog.show();
                TextView tv_order_exception = (TextView) orderExceptionDialog.findViewById(R.id.tv_order_exception);
                tv_order_exception.setText(getString(R.string.steal_recycle_exception));
                final TextView tv_order_exception_confirm = (TextView) orderExceptionDialog.findViewById(R.id.tv_order_exception_confirm);
                orderExceptionCountDownTimer = new CountDownTimer(30000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        String leftTime = "确定（" + millisUntilFinished / 1000 + "）";
                        if (tv_order_exception_confirm != null) {
                            tv_order_exception_confirm.setText(leftTime);
                        }
                    }

                    @Override
                    public void onFinish() {
                        orderExceptionDialog.dismiss();
                        cv_finish_throw.setVisibility(View.VISIBLE);
                        if (clp != null) {
                            clp.cancel();
                        }
                        pageJump(0);
                    }
                };
                orderExceptionCountDownTimer.start();
                break;
        }
    }

    // 订单异常
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void orderException(final OrderDetailModel orderDetailModel) {
        orderExceptionDialog.setOnClickListener(R.id.tv_order_exception_confirm, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderExceptionDialog.dismiss();
                cv_finish_throw.setVisibility(View.VISIBLE);
                if (clp != null) {
                    clp.cancel();
                }
                pageCancel(orderDetailModel, true);
            }
        });
        orderExceptionDialog.show();
        final TextView tv_order_exception_confirm = (TextView) orderExceptionDialog.findViewById(R.id.tv_order_exception_confirm);
        orderExceptionCountDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String leftTime = "确定（" + millisUntilFinished / 1000 + "）";
                if (tv_order_exception_confirm != null) {
                    tv_order_exception_confirm.setText(leftTime);
                }
            }

            @Override
            public void onFinish() {
                orderExceptionDialog.dismiss();
                cv_finish_throw.setVisibility(View.VISIBLE);
                if (clp != null) {
                    clp.cancel();
                }
                pageJump(0);
            }
        };
        orderExceptionCountDownTimer.start();

    }


    //是否显示优惠券投递按钮
    private boolean isCouponsBtnShow = true;
    //是否显示投递按钮
    private boolean isBtnShow = true;
    //辨识盒马优惠券两个活动
    private boolean isHemaCouponsActivity;
    //投递数量
    private String orderCount = "";

    // 组装串口返回数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderMessageEvent(ThingModel model) {
        tv_tip.setText("");// 有投递数据红色提示消失
        errorStatus = 3;
        // 重置超时器
        resetTime();
        if (NormalConstant.TYPE_PLASTIC.equals(model.getSourceId()) || NormalConstant.TYPE_GLASS.equals(model.getSourceId())) {
            if (model.getNum() > 0) {
                orderCount = orderDataUtils.dealOrderData(model);
            }
        } else {
            orderCount = orderDataUtils.dealOrderData(model);
        }


        if (NormalConstant.TYPE_GLASS.equals(model.getSourceId()) || NormalConstant.TYPE_PLASTIC.equals(model.getSourceId())) {

            if ("yintai".equals(channelInfo) && model.getNum() > 0 && Integer.parseInt(orderCount) > 0 && isBtnShow) {
                chooseCoupons();
            }
        }

//        if (model.isShow() && isBtnShow && "hippo".equals(channelInfo) && adCode.length() > 2 && "11".equals(adCode.substring(0, 2))) {
//            isHemaCouponsActivity = false;
//            postType(model);
//        }
        if ("hippo".equals(channelInfo) && pointCode.length() > 2 && "hc".equals(pointCode.substring(pointCode.length() - 2, pointCode.length())) && model.getNum() > 0 && isBtnShow) {
            isHemaCouponsActivity = true;
            postType(model);
        }

        if (isHasThrowDate) {
            return;
        }
        isFinishBtn();
    }

    //用户投递的类型
    private void postType(ThingModel model) {
        if (NormalConstant.TYPE_PLASTIC.equals(model.getSourceId()) || NormalConstant.TYPE_GLASS.equals(model.getSourceId())) {
            if (Integer.parseInt(orderCount) > 0 && isBtnShow) {
                chooseCoupons();
            }
        } else {
            if (Float.parseFloat(orderCount) > 0 && isBtnShow) {
                chooseCoupons();
            }
        }
    }

    //优惠券按钮弹出
    private void chooseCoupons() {
        tv_throw_finish.setVisibility(View.INVISIBLE);
        tv_coupons_receive.setVisibility(View.VISIBLE);
        tv_coupons_end_delivery.setVisibility(View.VISIBLE);
        tv_coupons_prompt.setVisibility(View.VISIBLE);
        if ("hippo".equals(channelInfo) && isHemaCouponsActivity) {
            tv_coupons_prompt.setText("您有一张盒马鲜生优惠券快去领取吧！");
        }
        isBtnShow = false;
    }

    private void isFinishBtn() {
        if (orderDataUtils.getOrderLength() != 0) {
            isHasThrowDate = true;
            tv_finish_throw.setText("完成投递");
        }
    }

    private int fingerCenter = 0;// 夹手次数，初始值为0
    private CloseDoorModel closeDoorModel;// 关门数据
    private boolean isOpenPort = false;
    private boolean isHttpOrder = false;
    private boolean isFault;// 开门状态

    // 关门返回数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseDoorMessageEvent(final CloseDoorModel model) {
        if ("100".equals(model.getTerminalId())) {
            // 串口打开成功
            isOpenPort = true;
            return;
        }
        closeDoorModel = model;
        switch (model.getStatus()) {
            // 回收门关闭
            case 0:
                closeDoorModel = null;
                handler.removeCallbacks(closeDoorLater);
                isFromBack = false;
                if (throwDialog.getDialog().isShowing()) {
                    throwDialog.dismiss();
                    cancelTime();
                    countDownTimer.start();
                    if (NormalConstant.TYPE_PLASTIC.equals(model.getSourceId()) || NormalConstant.TYPE_GLASS.equals(model.getSourceId())) {
                        pollingPackage.sendPollingPackage(currentSourceId, pointPresenter);
                        // 门关闭之后置为空
                        currentSourceId = "";
                        if (isOverWeight) {
                            countDownTimer.cancel();
                            if (orderDataUtils.getOrderLength() > 0) {
                                httpOrder(true);
                            }
                            handler.post(timeOutRunnable);
                            return;
                        }
                    } else {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                orderDataUtils.saveData(model, priceList);
                                pollingPackage.sendPollingPackage(currentSourceId, pointPresenter);
                                // 门关闭之后置为空
                                currentSourceId = "";
                                if (isOverWeight) {
                                    countDownTimer.cancel();
                                    if (orderDataUtils.getOrderLength() > 0) {
                                        httpOrder(true);
                                    }
                                    handler.post(timeOutRunnable);
                                    return;
                                }
                                isFinishBtn();
                            }
                        }, 100);
                    }
                }
                break;
            case 1:// 回收门打开
                isFault = false;
                handler.removeCallbacks(openDoorRunnable);
                fingerCenter = 0;
//                if ("hippo".equals(channelInfo) && adCode.length() > 2 && "11".equals(adCode.substring(0, 2))) {
//                    upFinishBtn();
//                }
                if ("hippo".equals(channelInfo) && pointCode.length() > 2 && "hc".equals(pointCode.substring(pointCode.length() - 2, pointCode.length())) && isHemaCouponsActivity) {
                    upFinishBtn();
                } else if ("yintai".equals(channelInfo)) {
                    upFinishBtn();
                } else {
                    //显示结束投递按钮
                    if (tv_throw_finish != null && tv_throw_finish.getVisibility() == View.INVISIBLE && iv_gif_close != null && iv_gif_close.getVisibility() != View.VISIBLE) {
                        tv_throw_finish.setVisibility(View.VISIBLE);
                    }
                }
                // 重置计时器
                resetTime();
                break;
            case 2:// 回收门夹手
                if (isFault) {
                    fingerCenter = fingerCenter + 1;
                    handler.removeCallbacks(closeDoorLater);
                    if (fingerCenter == 5) {
                        dealFaultData(model, 2);
                        if (NormalConstant.TYPE_PLASTIC.equals(model.getSourceId())) {// 塑料瓶
                            SerialPortManager.instance().sendCommand(Command.recoveryPlasticCmd(NormalConstant.DOOR_DOING_OPEN));
                        } else if (NormalConstant.TYPE_GLASS.equals(model.getSourceId())) {// 玻璃瓶
                            SerialPortManager.instance().sendCommand(Command.recoveryGlassCmd(NormalConstant.DOOR_DOING_OPEN));
                        } else {
                            SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(model.getSourceId(), NormalConstant.DOOR_DOING_OPEN));
                        }
                    }
                }
                break;
            // 回收门（开门超时）
            case 3:
                handler.removeCallbacks(openDoorRunnable);
                dealFaultData(model, 3);
                break;
            // 回收门（关门超时）
            case 4:
                handler.removeCallbacks(closeDoorLater);
                dealFaultData(model, 4);
                break;
            // 回收门（夹手开门）
            case 5:
                handler.removeCallbacks(openDoorRunnable);
                dealFaultData(model, 5);
                break;
        }

    }

    //更新界面
    private void upFinishBtn() {
        if (isBtnShow) {
            tv_throw_finish.setVisibility(View.VISIBLE);
        }
        if (!isCouponsBtnShow) {
            tv_throw_finish.setVisibility(View.VISIBLE);
        }
    }

    // 故障统一处理
    private void dealFaultData(final CloseDoorModel model, int recGateSta) {

        String faultTip = "";
        String throwDoor = "";
        if (model != null && !Utils.isEmpty(model.getSourceId())) {
            throwDoor = model.getSourceId();
        }
        switch (recGateSta) {
            case 2:
                ZLog.e(TAG, throwDoor + ":投递门夹手异常");
                faultTip = Constant.EXCEPTION_CABINET_FOLDER;
                break;
            case 3:
                ZLog.e(TAG, throwDoor + ":投递门开门异常");
                faultTip = Constant.EXCEPTION_CABINET_OPEN;
                break;
            case 4:
                ZLog.e(TAG, throwDoor + ":投递门关门异常");
                faultTip = Constant.EXCEPTION_CABINET_CLOSE;
                break;
            case 5:
                ZLog.e(TAG, throwDoor + ":投递门夹手开门异常");
                faultTip = Constant.EXCEPTION_CABINET_FOLDER_OPEN;
                break;
            case 6:
                ZLog.e(TAG, throwDoor + ":投递门开门异常（未收到PCB板反馈）");
                faultTip = Constant.EXCEPTION_OPEN_DOOR;
                break;
            case 7:
                ZLog.e(TAG, throwDoor + ":投递门关门异常（未收到PCB板反馈）");
                faultTip = Constant.EXCEPTION_CLOSE_DOOR;
                break;
        }
        if (!Utils.isEmpty(faultTip)) {
            pointPresenter.logUpload(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, ""), Command.getTerminalId(currentSourceId), Constant.LOG_EXCEPTION, faultTip);
        }

        if (!isHttpOrder) {
            orderDataUtils.saveData(model, priceList);
            httpOrder(true);
            isHttpOrder = true;
        }
        closeDoorModel = null;
        dialogFault.setOnClickListener(R.id.tv_up_fault, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDoorModel = null;
                exceptionCountDownTimer.cancel();
                LoginUtils.logout(context);
            }
        });

        if (!isFinishing() && throwDialog != null && throwDialog.getDialog() != null && throwDialog.getDialog().isShowing()) {
            throwDialog.dismiss();
        }
        if (!isFinishing()) {
            dialogFault.show();
        }
        exceptionCountDownTimer.start();
        equipmentFault(recGateSta);
    }

    // 设备出现故障
    private void equipmentFault(int recGateSta) {
        switch (delivery_type) {
            case 1001:
                DBManager.getInstance(context).update_species_fault(NormalConstant.TYPE_PLASTIC, 1, recGateSta);
                break;
            case 1002:
                DBManager.getInstance(context).update_species_fault(NormalConstant.TYPE_GLASS, 1, recGateSta);
                break;
            case 1:
                List<TerminalModel> paperList = DBManager.getInstance(context).getDevListByType(Constant.DEV_PAPER_TYPE);
                for (int i = 0; i < paperList.size(); i++) {
                    DBManager.getInstance(context).update_species_fault(ByteUtil.decimal2fitHex(Integer.valueOf("1" + i)), 1, recGateSta);
                }
                break;
            case 2:
                List<TerminalModel> spinList = DBManager.getInstance(context).getDevListByType(Constant.DEV_SPIN_TYPE);
                for (int i = 0; i < spinList.size(); i++) {
                    DBManager.getInstance(context).update_species_fault(ByteUtil.decimal2fitHex(Integer.valueOf("2" + i)), 1, recGateSta);
                }
                break;
            case 3:
                List<TerminalModel> metalList = DBManager.getInstance(context).getDevListByType(Constant.DEV_METAL_TYPE);
                for (int i = 0; i < metalList.size(); i++) {
                    DBManager.getInstance(context).update_species_fault(ByteUtil.decimal2fitHex(Integer.valueOf("3" + i)), 1, recGateSta);
                }
                break;
            case 4:
                List<TerminalModel> plasticList = DBManager.getInstance(context).getDevListByType(Constant.DEV_CEMENT_TYPE);
                for (int i = 0; i < plasticList.size(); i++) {
                    DBManager.getInstance(context).update_species_fault(ByteUtil.decimal2fitHex(Integer.valueOf("4" + i)), 1, recGateSta);
                }
                break;
        }
    }

    // 重置定时器
    private void resetTime() {
        if (!isFinishing() && throwDialog != null && throwDialog.getDialog() != null && !throwDialog.getDialog().isShowing()) {
            return;
        }
        if (NormalConstant.TYPE_PLASTIC.equals(currentSourceId) || NormalConstant.TYPE_GLASS.equals(currentSourceId)) {
            if (otherDownTimer != null) {
                otherDownTimer.cancel();
            }
            if (finalDownTimer != null) {
                finalDownTimer.cancel();
                finalDownTimer.start();
            }
        } else {
            if (finalDownTimer != null) {
                finalDownTimer.cancel();
            }
            if (otherDownTimer != null) {
                otherDownTimer.cancel();
                otherDownTimer.start();
            }
        }
    }

    // 取消定时器
    private void cancelTime() {
        if (finalDownTimer != null) {
            finalDownTimer.cancel();
        }

        if (otherDownTimer != null) {
            otherDownTimer.cancel();
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();

        }

        if (exceptionCountDownTimer != null) {
            exceptionCountDownTimer.cancel();
        }

        if (noOrderCountDownTimer != null) {
            noOrderCountDownTimer.cancel();
        }

        if (orderExceptionCountDownTimer != null) {
            orderExceptionCountDownTimer.cancel();
        }
    }

    // 移除任务
    private void removeRunnable() {
        handler.removeCallbacks(timeOutRunnable);
        handler.removeCallbacks(jumpToAliRunnable);
        handler.removeCallbacks(poisonRunnable);
        handler.removeCallbacks(openDoorRunnable);
        handler.removeCallbacks(closeDoorLater);
    }

    // 超时任务
    private Runnable timeOutRunnable = new Runnable() {
        @Override
        public void run() {
            cancelTime();
            cancelDialog();
            LoginUtils.logout(context);
        }
    };

    // 跳转到阿里广告页面
    private final Runnable jumpToAliRunnable = new Runnable() {
        @Override
        public void run() {
            cancelTime();
            cancelDialog();
            LoginUtils.logout(context);
            startActivity(new Intent(context, AliBannerActivity.class));
        }
    };

    private Runnable poisonRunnable = new Runnable() {
        @Override
        public void run() {
            poisonDialog.dismiss();
            countDownTimer.cancel();
            countDownTimer.start();
            tv_countdown_time.setVisibility(View.VISIBLE);
        }
    };

    // 开门超时
    private Runnable openDoorRunnable = new Runnable() {
        @Override
        public void run() {
            dealFaultData(closeDoorModel, 6);
        }
    };

    private boolean isFromBack = false;// 关门超时
    private Runnable closeDoorLater = new Runnable() {
        @Override
        public void run() {
            if (isFromBack) {
                isFromBack = false;
                dealFaultData(closeDoorModel, 7);
            }
        }
    };


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

    // 请求订单
    private void httpOrder(boolean isDisplayOrder) {
        orderPresenter.createOrderNew(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID), orderDataUtils.getOrderArray(), isDisplayOrder, token);
    }

}
