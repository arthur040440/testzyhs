package com.zhangyuhuishou.zyhs.base;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.baidu.tts.util.TTSUtils;
import com.tlh.android.utils.ToastUitls;
import com.tlh.android.widget.NoPaddingTextView;
import com.tlh.android.widget.dialog.SelfDialogBuilder;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.receiver.netmonitor.NetBroadcastReceiver;
import com.zhangyuhuishou.zyhs.receiver.netmonitor.NetUtil;

import java.util.List;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author tlh
 * @time 2017/11/22 17:01
 * @desc 通用抽象类
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks, NetBroadcastReceiver.NetEvent, BaseView {

    public static NetBroadcastReceiver.NetEvent event;
    private NetBroadcastReceiver netBroadcastReceiver;
    /**
     * 网络类型
     */
    protected int netMobile;

    protected Handler baseHandler = new Handler();
    protected SelfDialogBuilder netTipDialog;

    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置页面全屏并且无状态栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setBackgroundDrawable(null);
        // 设置通用提示对话框
        context = this;
        netTipDialog = new SelfDialogBuilder(BaseActivity.this).setLayoutId(R.layout.dialog_self_check);
        event = this;
        inspectNet();

        setContentView(getLayoutResId());// 把设置布局文件的操作交给继承的子类

        ButterKnife.bind(this);

        initView();
        initData();
        initListener();

        // Android 7.0需要动态注册广播
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            netBroadcastReceiver = new NetBroadcastReceiver();
            registerReceiver(netBroadcastReceiver, filter);
        }

        Log.e("=======", "onCreate");

    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.e("=======", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("=======", "onResume");

    }

    // 返回当前Activity布局文件的id
    protected abstract int getLayoutResId();

    protected void initData() {
    }

    protected void initListener() {
    }

    // 初始化view
    protected abstract void initView();

    @Override
    public void onClick(View view) {
    }

    // 保证页面字体大小不随系统设置变化
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    // 权限请求
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    // 授予权限
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    // 拒绝权限
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        //检查用户拒绝权限的时候是否选择了“不再提醒”的情况,这里将弹出对话框引导用户去系统设置
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            // 设置一
//            new AppSettingsDialog.Builder(this).setTitle("权限申请").build().show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // 禁止展开下滑栏
        sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }

    /**
     * 初始化时判断有没有网络
     */

    public int inspectNet() {
        this.netMobile = NetUtil.getNetWorkState(BaseActivity.this);
        return this.netMobile;
    }

    /**
     * 网络变化之后的类型
     */
    @Override
    public void onNetChange(int netMobile) {
        this.netMobile = netMobile;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showErr() {

    }

    @Override
    public void showToast(String msg) {
        ToastUitls.toastMessage(msg);
    }

    @Override
    public Context getContext() {
        return BaseActivity.this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 停止播放语音提示
        TTSUtils.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (netTipDialog.getDialog() != null && isShow()) {
            netTipDialog.dismiss();
        }
        if (netBroadcastReceiver != null) {
            unregisterReceiver(netBroadcastReceiver);
        }
    }





    // 提示框是否显示
    protected boolean isShow() {
        if (netTipDialog == null && netTipDialog.getDialog() != null) {
            return false;
        } else {
            return netTipDialog.isShowing();
        }
    }


    // 无网络提示
    protected void showNetDialog(String tipMessage, boolean isControlDismiss) {
        netTipDialog.show();
        TextView tv = (TextView) netTipDialog.findViewById(R.id.tv_normal_tip);
        if (TextUtils.isEmpty(tipMessage)) {
            tv.setText("网络中断,稍后重试~~~");
        } else {
            tv.setText(tipMessage);
        }
        if (isControlDismiss) {
            baseHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    cancleNetDialog();
                }
            }, 3000);
        }
    }

    // 无网络消失
    protected void cancleNetDialog() {
        if (!isFinishing() && netTipDialog != null && netTipDialog.getDialog() != null && netTipDialog.isShowing()) {
            netTipDialog.dismiss();
        }
    }

}
