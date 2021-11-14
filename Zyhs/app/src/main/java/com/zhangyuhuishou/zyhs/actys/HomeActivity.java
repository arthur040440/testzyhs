package com.zhangyuhuishou.zyhs.actys;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.face.util.FaceUtils;
import com.face.util.Util;
import com.google.gson.Gson;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.ApkUtils;
import com.tlh.android.utils.ClickUtils;
import com.tlh.android.utils.ImageUtils;
import com.tlh.android.utils.MD5Utils;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.utils.SocketUtil;
import com.tlh.android.widget.BubbleLayout;
import com.tlh.android.widget.CircularLinesProgress;
import com.tlh.android.widget.NoPaddingTextView;
import com.tlh.android.widget.dialog.SelfDialogBuilder;
import com.tlh.android.widget.other.BannerImageViewWithTxt;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import com.zhangyuhuishou.zyhs.guide.activity.GuideActivity;
import com.zhangyuhuishou.zyhs.model.AuthModel;
import com.zhangyuhuishou.zyhs.model.IsUpdateModel;
import com.zhangyuhuishou.zyhs.model.QrTimeModel;
import com.zhangyuhuishou.zyhs.model.UserModel;
import com.zhangyuhuishou.zyhs.model.UserSocketBean;
import com.zhangyuhuishou.zyhs.presenter.IUserView;
import com.zhangyuhuishou.zyhs.presenter.UserPresenter;
import com.zhangyuhuishou.zyhs.receiver.netmonitor.NetUtil;
import com.zhangyuhuishou.zyhs.time.TimeThread;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;

/**
 * @author tlh
 * @time 2018/7/26 13:57
 * @desc 首页
 */

public class HomeActivity extends BaseActivity implements IUserView {

    private String TAG = HomeActivity.class.getSimpleName();

    // 倒计时
    @BindView(R.id.tv_countdown_time)
    TextView tv_countdown_time;

    // 人脸识别
    @BindView(R.id.go_to_face_auth)
    TextView go_to_face_auth;

    // 二维码
    @BindView(R.id.iv_qr)
    ImageView iv_qr;
    private boolean isShowQr = false;// 二维码是否出现

    // 加载动画
    @BindView(R.id.clp)
    CircularLinesProgress clp;

    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.tv_location)
    TextView tv_location;

    // 版本号
    @BindView(R.id.tv_version)
    TextView tv_version;

    // 操作指南
    @Nullable
    @BindView(R.id.tv_guide)
    NoPaddingTextView tv_guide;

    // 气泡
    @BindView(R.id.bubble)
    BubbleLayout bubble;

    //设置背景图片
    @BindView(R.id.hm_ctl_bg)
    ConstraintLayout constraintLayout;

    //设置logo图片
    @BindView(R.id.iv_logo_top)
    ImageView iv_logo_top;

    private TimeThread timeThread;// 时间处理器

    private CountDownTimer countDownTimer, exceptionCutDownTimer;

    private Handler handler = new Handler();

    private HomeBroadcastReceiver homeBroadcastReceiver;

    private UserPresenter userPresenter;// 用户相关

    private int i = 0;

    private FaceUtils mFaceUtils;// 人脸识别授权工具类
    private SelfDialogBuilder faceTipBuilder;// 提示框

    @Override
    protected int getLayoutResId() {
//        File bgFile = ApkUtils.getAdFileByFileUrl(Constant.BACKGROUND_PATH + "/", SPUtils.getString(context, Constant.SKIN_BACKGROUND_URL, ""));
//        if (bgFile.exists()) {
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
        return R.layout.activity_home;
    }

    @Override
    protected void initData() {
        super.initData();
        clp.start();

        mFaceUtils = new FaceUtils(context, Util.FACE_TYPE_LOGIN);
        faceTipBuilder = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_auth_fail);

        exceptionCutDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (tv_countdown_time.getVisibility() == View.GONE) {
                    tv_countdown_time.setVisibility(View.VISIBLE);
                }
                tv_countdown_time.setText(millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                cancleNetDialog();
                closeSocketService();
                finish();
            }
        };

        timeThread = new TimeThread(time, TAG);
        timeThread.start();

        // 版本
        String mobileVersion = ApkUtils.getVersion(this, getPackageName());
        tv_version.setText(getString(R.string.version, mobileVersion));

        openSocketService();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!isShowQr) {
                    exceptionCutDownTimer.start();
                }
            }
        }, 10000);

        homeBroadcastReceiver = new HomeBroadcastReceiver();
        IntentFilter filter = new IntentFilter(Constant.FINISH_HOME_PAGE);
        registerReceiver(homeBroadcastReceiver, filter);

        userPresenter = new UserPresenter(context);
        userPresenter.attachView(this);
        // 播放提示
        //   TTSUtils.speak(Constant.TTS_TIP_SCAN_QR);
    }

    @Override
    protected void initListener() {
        super.initListener();
        go_to_face_auth.setOnClickListener(this);
        time.setOnClickListener(this);
        tv_guide.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        // 设置小区名称和点位名称以及点位编号
        String communityName = SPUtils.getString(context, Constant.CURRENT_COMMUNITYNAME, "");
        String rangName = SPUtils.getString(context, Constant.CURRENT_RANGENAME, "");
        if (!TextUtils.isEmpty(communityName) && !TextUtils.isEmpty(rangName)) {
            tv_location.setText(communityName);
        }
//        File logoFile = ApkUtils.getAdFileByFileUrl(Constant.LOGO_PATH + "/", SPUtils.getString(context, Constant.SKIN_LOGO_URL, ""));
//        if (logoFile.exists()) {
//            Glide.with(getContext()).load(Constant.LOGO_PATH + "/" + MD5Utils.md5(SPUtils.getString(context, Constant.SKIN_LOGO_URL, "")) + ".jpg").override(315, 92).into(iv_logo_top);
//        }
//        //动态设置选择器
//        GradientDrawable pressed = new GradientDrawable();
//        pressed.setColor(Color.parseColor("#5ad4e9"));
//        pressed.setCornerRadius(8);
//        GradientDrawable normal = new GradientDrawable();
//        normal.setColor(Color.parseColor("#FF5fe8ff"));
//        normal.setCornerRadius(8);
//
//        StateListDrawable bg = new StateListDrawable();
//        bg.addState(new int[]{android.R.attr.state_pressed}, pressed);
//        bg.addState(new int[]{}, normal);
//        go_to_face_auth.setBackground(bg);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (ClickUtils.isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.go_to_face_auth:
                if (netMobile == -1) {
                    showNetDialog("", true);
                    return;
                }
                mFaceUtils.network();
                break;
            case R.id.time:
                i = i + 1;
                if (i == 5) {
                    finish();
                }
                break;
            case R.id.tv_guide:
                startActivity(new Intent(context, GuideActivity.class));
                break;
        }
    }

    @Override
    public void onNetChange(int netMobile) {
        super.onNetChange(netMobile);
        if (netMobile == NetUtil.NETWORK_NONE) {
            Log.i(TAG, "没有网络连接");
            closeSocketService();
            if (clp.getVisibility() == View.GONE) {
                iv_qr.setImageResource(0);
                clp.setVisibility(View.VISIBLE);
                clp.start();
                System.out.println("网络由有变为无");
            }
        } else {
            Log.i(TAG, "有网络连接");
            openSocketService();
        }
    }

    // 进行socket连接通讯
    private void openSocketService() {
        if (netMobile != -1) {
            // 启动服务
            SocketUtil.getInstance(context);
            SocketUtil.sendGetQrMessage();
        }
    }

    // 关闭socket连接通讯
    private void closeSocketService() {
        SocketUtil.closeSocket();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        bubble.startAnim();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        bubble.stopAnim();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clp.cancel();
        bubble.stopAnim();
        cancelDialog();
        if (homeBroadcastReceiver != null) {
            unregisterReceiver(homeBroadcastReceiver);
        }
        if (timeThread != null) {
            timeThread.removeTask();
        }
        if (exceptionCutDownTimer != null) {
            exceptionCutDownTimer.cancel();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        closeSocketService();

        handler.removeCallbacksAndMessages(null);
    }


    // socket返回数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketMessageEvent(QrTimeModel model) {
        if (model == null) {
            return;
        }
        clp.setVisibility(View.GONE);
        clp.cancel();
        exceptionCutDownTimer.cancel();
        isShowQr = true;
        iv_qr.setImageBitmap(ImageUtils.base64ToBitmap(model.getQrCode()));
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(model.getTimeout() * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (tv_countdown_time.getVisibility() == View.GONE) {
                    tv_countdown_time.setVisibility(View.VISIBLE);
                }
                String leftTime = millisUntilFinished / 1000 + "s";
                tv_countdown_time.setText(leftTime);
            }

            @Override
            public void onFinish() {
                tv_countdown_time.setVisibility(View.GONE);
                closeSocketService();
                finish();
            }
        };
        countDownTimer.start();
    }

    // socket返回数据（是否升级）
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateMessage(IsUpdateModel model) {
        if (model == null) {
            return;
        }

        switch (model.getType()) {
            case 1:// 应用升级
                sendBroadcast(new Intent(Constant.NEED_UPDATE_NOTIFY));
                closeSocketService();
                if (clp.getVisibility() == View.GONE) {
                    iv_qr.setImageResource(0);
                    clp.setVisibility(View.VISIBLE);
                    clp.start();
                    System.out.println("Socket应用升级");
                }
                break;
            case 2:// 广告升级
                sendBroadcast(new Intent(Constant.NEED_UPDATE_BANNER_NOTIFY));
                break;
            case 3:// 点位升级
                sendBroadcast(new Intent(Constant.NEED_UPDATE_LOCATION_NOTIFY));
                break;
        }
    }

    // socket返回数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketMessageEvent(JSONObject jsonObject) {
        Gson gson = new Gson();
        UserSocketBean bean = gson.fromJson(jsonObject.toString(), UserSocketBean.class);
        UserModel model = UserModel.getInstance();
        try {
            if (bean.getToken() != null && bean.getToken().length() > 0) {
                model.setToken(bean.getToken());
            }
            model.setId(bean.getUser().getUserId());
            model.setNickName(bean.getUser().getNickName());
            model.setProfilePhoto(bean.getUser().getProfilePhoto());
            model.setIntegral(bean.getUser().getIntegral());
            SPUtils.putString(context, Constant.USER_TOKEN, bean.getToken());
            if ("out".equals(bean.getUserType())) {
                closeSocketService();
                startActivity(new Intent(context, ThrowThingsActivity.class));
                finish();
            } else if ("inner".equals(bean.getUserType())) {
                userPresenter.getInnerUser(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, ""));
            }
        } catch (Exception e) {
            closeSocketService();
            finish();
        }
    }


    @Override
    public void outOfCurrentArea(String tips) {
        showNetDialog(tips, true);
    }

    // 角色分配
    @Override
    public void roleDistribute(String roleNames) {
        int clearPeople = roleNames.indexOf("清运");
        int maintainPeople = roleNames.indexOf("安装");
        if (clearPeople != -1 && maintainPeople != -1) {
            closeSocketService();
            startActivity(new Intent(context, RoleChooseActivity.class));
            finish();
        } else if (clearPeople != -1) {
            closeSocketService();
            startActivity(new Intent(context, ClearAndTransportActivity.class));
            finish();
        } else if (maintainPeople != -1) {
            closeSocketService();
            startActivity(new Intent(context, MaintainPeopleActivity.class));
            finish();
        } else {
            showNetDialog("未提供权限，请联系管理员", true);
        }
    }

    // 创建广播接收器
    private class HomeBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (Constant.FINISH_HOME_PAGE.equals(action)) {
                closeSocketService();
                finish();
            }
        }
    }


    // 旷世授权与否
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAuthEvent(AuthModel model) {
        if (model.getIsAuth()) {
//            finish();
        } else {
            showDialog("人脸授权失败");
        }
    }

    // 提示框
    private void showDialog(String message) {
        faceTipBuilder.show();
        NoPaddingTextView tip = (NoPaddingTextView) faceTipBuilder.findViewById(R.id.nptv_auth_tip);
        tip.setText(TextUtils.isEmpty(message) ? "验证失败" : message);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    faceTipBuilder.dismiss();
                }
            }
        }, 3000);
    }

    // 取消弹框
    private void cancelDialog() {

        if (!isFinishing() && faceTipBuilder != null && faceTipBuilder.getDialog() != null && faceTipBuilder.getDialog().isShowing()) {
            faceTipBuilder.dismiss();
        }
    }

}
