package com.zhangyuhuishou.zyhs.actys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.ClickUtils;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.utils.Utils;
import com.tlh.android.widget.CircularLinesProgress;
import com.tlh.android.widget.GlideCircleTransform;
import com.tlh.android.widget.dialog.SelfDialogBuilder;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.adapter.ClearOrderAdapter;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.TerminalModel;
import com.zhangyuhuishou.zyhs.model.UserModel;
import com.zhangyuhuishou.zyhs.presenter.ClearPresenter;
import com.zhangyuhuishou.zyhs.presenter.IClearView;
import com.zhangyuhuishou.zyhs.serialport.constant.NormalConstant;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortManager;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortUtils;
import com.zhangyuhuishou.zyhs.serialport.message.RecvMessage;
import com.zhangyuhuishou.zyhs.serialport.util.Command;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;

import static com.tlh.android.utils.ZXingUtils.createQRCodeBitmap;

public class ClearAndTransportActivity extends BaseActivity implements ClearOrderAdapter.ClearOrderListener, IClearView {


    private String TAG = ClearAndTransportActivity.class.getSimpleName();

    // 清运列表
    @BindView(R.id.clear_order_list)
    ListView clearOrderListView;
    // 完成清运
    @BindView(R.id.tv_clear_order_finish)
    TextView tv_clear_order_finish;
    // 退出登录
    @BindView(R.id.tv_clear_order_cancel)
    TextView tv_clear_order_cancel;
    // 用户头像
    @BindView(R.id.iv_clear_user_logo)
    ImageView iv_clear_user_logo;
    // 章鱼币-昵称
    @BindViews({R.id.tv_clear_user_money, R.id.tv_clear_nickname})
    List<TextView> tv_clear_user_params;

    // 旋转菊花布局
    @BindView(R.id.ll_upload_clear_data)
    LinearLayout ll_upload_clear_data;

    // 旋转动画
    @BindView(R.id.clp)
    CircularLinesProgress clp;

    private ClearOrderAdapter clearOrderAdapter;
    private Handler mHandler = new Handler();
    private SerialPortUtils serialPortUtils;// 串口工具
    private ClearPresenter clearPresenter;
    private SelfDialogBuilder clearUploadSuccessDialog, clearNetExceptionDialog, clearQrcodeDialog;
    //点位ID
    String rangeId;

    //二维码图片(Bitmap弱引用)
    private WeakReference<Bitmap> weakReference;

    //扫描二维码计时器
    private CountDownTimer scanQrcodeDownTimer;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_clear_order;
    }

    @Override
    protected void initView() {

        // 设置当前用户信息
        UserModel userModel = UserModel.getInstance();
        Glide.with(this).load(userModel.getProfilePhoto()).centerCrop().transform(new GlideCircleTransform(this)).crossFade(2000).into(iv_clear_user_logo);
        String integral = userModel.getIntegral() + "";
        tv_clear_user_params.get(0).setText(integral);
        tv_clear_user_params.get(1).setText(TextUtils.isEmpty(userModel.getNickName()) ? "章鱼游客" : userModel.getNickName());


        clearUploadSuccessDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_clear_upload_success);
        clearNetExceptionDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_clear_net_exception);
        clearQrcodeDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_clear_qrcode);

    }

    @Override
    protected void initData() {
        super.initData();

        // 打开串口
        serialPortUtils = new SerialPortUtils();
        serialPortUtils.openPort();

        // 清理
        clearPresenter = new ClearPresenter(context);
        clearPresenter.attachView(this);

        // 列表适配器
        clearOrderAdapter = new ClearOrderAdapter(context);
        clearOrderListView.setAdapter(clearOrderAdapter);
        clearOrderAdapter.setClearOrderListener(this);

        // 完成最终清运
        tv_clear_order_finish.setOnClickListener(this);
        // 退出登录
        tv_clear_order_cancel.setOnClickListener(this);

        rangeId = SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, "");

    }

    // 弹出清运对话框
    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (ClickUtils.isFastClick(500)) {
            return;
        }

        if (view.getId() == R.id.tv_clear_order_finish) {

            tv_clear_order_finish.setVisibility(View.INVISIBLE);
            tv_clear_order_cancel.setVisibility(View.INVISIBLE);
            // 旋转菊花
            ll_upload_clear_data.setVisibility(View.VISIBLE);
            clp.start();
            isEmpty();
            // 请求接口（生成清运数据）
            clearPresenter.createClearOrder(rangeId, plasticBottleModel.getWeight(), 0,
                    glassBottleModel.getWeight(), 0, 0, 0,
                    paperModel.getWeight(), 0, spinModel.getWeight(), 0,
                    plasticModel.getWeight(), 0, metalModel.getWeight(), 0);


        } else if (view.getId() == R.id.tv_clear_order_cancel) {
            finish();
        }
    }

    // 判断TerminalModel是否为空，否则会有空指针异常
    private void isEmpty() {
        if (null == plasticBottleModel) {
            plasticBottleModel = new TerminalModel();
        }

        if (null == glassBottleModel) {
            glassBottleModel = new TerminalModel();
        }

        if (null == paperModel) {
            paperModel = new TerminalModel();
        }

        if (null == spinModel) {
            spinModel = new TerminalModel();
        }

        if (null == metalModel) {
            metalModel = new TerminalModel();
        }

        if (null == plasticModel) {
            plasticModel = new TerminalModel();
        }
    }


    // 开启维护门
    @Override
    public void openDoor(final List<TerminalModel> terminalModelList) {
        if (terminalModelList == null || terminalModelList.size() == 0) {
            return;
        }

        Log.e(TAG, terminalModelList.toString());
        // 维护门延迟时间
        int openDoorDelayTime = 0;
        for (int i = 0; i < terminalModelList.size(); i++) {
            if (terminalModelList.get(i) != null && !Utils.isEmpty(terminalModelList.get(i).getID_D())) {
                final int index = i;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Constant.DEV_GLASS_TYPE.equals(terminalModelList.get(index).getTerminalTypeId())) {
                            SerialPortManager.instance().sendCommand(Command.openDoor(NormalConstant.TYPE_PLASTIC));
                        } else {
                            SerialPortManager.instance().sendCommand(Command.openDoor(terminalModelList.get(index).getID_D()));
                        }
                    }
                }, openDoorDelayTime);
                openDoorDelayTime += 2000;
            }
        }
    }

    private TerminalModel plasticBottleModel, glassBottleModel, paperModel, spinModel, metalModel, plasticModel;

    //获取数据
    @Override
    public void transmitData(TerminalModel terminalModel, int weight) {
        if (Constant.DEV_PLASTIC_TYPE.equals(terminalModel.getTerminalTypeId())) {
            plasticBottleModel = terminalModel;
            Log.e("Model", plasticBottleModel.toString());
        } else if (Constant.DEV_GLASS_TYPE.equals(terminalModel.getTerminalTypeId())) {
            glassBottleModel = terminalModel;
            Log.e("Model", glassBottleModel.toString());
        } else if (Constant.DEV_PAPER_TYPE.equals(terminalModel.getTerminalTypeId())) {
            paperModel = terminalModel;
            paperModel.setWeight(weight);
            Log.e("Model", paperModel.toString());
        } else if (Constant.DEV_SPIN_TYPE.equals(terminalModel.getTerminalTypeId())) {
            spinModel = terminalModel;
            spinModel.setWeight(weight);
            Log.e("Model", spinModel.toString());
        } else if (Constant.DEV_METAL_TYPE.equals(terminalModel.getTerminalTypeId())) {
            metalModel = terminalModel;
            metalModel.setWeight(weight);
            Log.e("Model", metalModel.toString());
        } else if (Constant.DEV_CEMENT_TYPE.equals(terminalModel.getTerminalTypeId())) {
            plasticModel = terminalModel;
            plasticModel.setWeight(weight);
            Log.e("Model", plasticModel.toString());
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (serialPortUtils != null) {
            serialPortUtils.closePort();
            serialPortUtils = null;
        }
        cancelDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clp != null) {
            clp.cancel();
        }
        if (weakReference != null) {
            weakReference.clear();
            weakReference = null;
        }

        if (scanQrcodeDownTimer != null) {
            scanQrcodeDownTimer.cancel();
            scanQrcodeDownTimer = null;
        }
    }

    // 取消弹框
    private void cancelDialog() {
        if (!isFinishing() && clearUploadSuccessDialog != null && clearUploadSuccessDialog.getDialog() != null && clearUploadSuccessDialog.getDialog().isShowing()) {
            clearUploadSuccessDialog.dismiss();
        }

        if (!isFinishing() && clearNetExceptionDialog != null && clearNetExceptionDialog.getDialog() != null && clearNetExceptionDialog.getDialog().isShowing()) {
            clearNetExceptionDialog.dismiss();
        }

        if (!isFinishing() && clearQrcodeDialog != null && clearQrcodeDialog.getDialog() != null && clearQrcodeDialog.getDialog().isShowing()) {
            clearQrcodeDialog.dismiss();
        }

    }

    // 清运数据上传成功
    @Override
    public void clearSuccessCallback() {

        clearUploadSuccessDialog.setOnClickListener(R.id.tv_clear_success_confirm, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDataAndJump();
            }
        }).setClickDismiss(true).show();

    }

    //清运数据上传失败
    @Override
    public void clearNetExceptionCallback(int flag) {

        Log.e(TAG, String.valueOf(flag));
        // 上传弹框消失
        cancelDialog();
        // 结束旋转动画
        if (clp != null) {
            clp.cancel();
        }
        ll_upload_clear_data.setVisibility(View.INVISIBLE);
        tv_clear_order_finish.setVisibility(View.VISIBLE);
        tv_clear_order_cancel.setVisibility(View.VISIBLE);

        clearNetExceptionDialog.setOnClickListener(R.id.tv_clear_confirm, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog();
                clearQrcodeDialogShow();
            }
        }).setClickDismiss(true).show();

        TextView tv_clear_back = (TextView) clearNetExceptionDialog.findViewById(R.id.tv_clear_back);

        tv_clear_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog();
            }
        });
    }

    //清运二维码弹窗
    private void clearQrcodeDialogShow() {

        clearQrcodeDialog.show();
        final TextView tv_clear_qrcode_confirm = (TextView) clearQrcodeDialog.findViewById(R.id.tv_clear_qrcode_confirm);
        ImageView iv_clear_qrcode = (ImageView) clearQrcodeDialog.findViewById(R.id.iv_clear_qrcode);
        String content = "https://www.zyhs.com/miniqr/zyhs/order?rangeId=" + rangeId + "& estimateGlassBottleCount =" + glassBottleModel.getWeight() + " & estimatePlasticBottleCount =" + plasticBottleModel.getWeight() + " & estimateCansCount = 0 & estimatePaperCount =" + paperModel.getWeight() + " & estimateSpinCount =" + spinModel.getWeight() + " & estimatePlasticCount =" + plasticModel.getWeight() + " & estimateMetalCount =" + metalModel.getWeight() + " & estimateHarmfulCount = 0";
        Bitmap bitmap = createQRCodeBitmap(content, 381, 381, "UTF-8", "L", "1", Color.BLACK, Color.WHITE);
        weakReference = new WeakReference<>(bitmap);
        if (weakReference.get() != null) {
            iv_clear_qrcode.setImageBitmap(weakReference.get());

        }
        tv_clear_qrcode_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog();
                clearDataAndJump();
            }
        });
        scanQrcodeDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                tv_clear_qrcode_confirm.setVisibility(View.VISIBLE);
            }
        };
        scanQrcodeDownTimer.start();
    }


    //清空数据并跳转到下一个页面
    private void clearDataAndJump() {
        // 上传弹框消失
        cancelDialog();
        // 清理成功 清空本地数据
        DBManager dbManager = DBManager.getInstance(context);
        List<TerminalModel> plasticBottleList = dbManager.getDevListByType(Constant.DEV_PLASTIC_TYPE);
        List<TerminalModel> glassBottleList = dbManager.getDevListByType(Constant.DEV_GLASS_TYPE);
        List<TerminalModel> paperList = dbManager.getDevListByType(Constant.DEV_PAPER_TYPE);
        List<TerminalModel> spinList = dbManager.getDevListByType(Constant.DEV_SPIN_TYPE);
        List<TerminalModel> metalList = dbManager.getDevListByType(Constant.DEV_METAL_TYPE);
        List<TerminalModel> plasticList = dbManager.getDevListByType(Constant.DEV_CEMENT_TYPE);

        for (int i = 0; i < plasticBottleList.size(); i++) {
            TerminalModel terminalModel = plasticBottleList.get(i);
            if (terminalModel != null) {
                dbManager.updateDev(terminalModel.getId(), 0);
                dbManager.updateRecBucket(terminalModel.getID_D(), 1);// 清空数据,硬件回收桶状态置为1 （1代表正常，0代表满了）
                dbManager.updateCountException(terminalModel.getTerminalId(), 0);

            }
        }
        for (int i = 0; i < glassBottleList.size(); i++) {
            TerminalModel terminalModel = glassBottleList.get(i);
            if (terminalModel != null) {
                dbManager.updateDev(terminalModel.getId(), 0);
                dbManager.updateRecBucket(terminalModel.getID_D(), 1);// 清空数据,硬件回收桶状态置为1 （1代表正常，0代表满了）
                dbManager.updateCountException(terminalModel.getTerminalId(), 0);
            }
        }

        for (int i = 0; i < paperList.size(); i++) {
            TerminalModel terminalModel = paperList.get(i);
            if (terminalModel != null) {
                dbManager.updateDev(terminalModel.getId(), 0);
                dbManager.updateRecBucket(terminalModel.getID_D(), 1);// 清空数据,硬件回收桶状态置为1 （1代表正常，0代表满了）
            }
        }

        for (int i = 0; i < spinList.size(); i++) {
            TerminalModel terminalModel = spinList.get(i);
            if (terminalModel != null) {
                dbManager.updateDev(terminalModel.getId(), 0);
                dbManager.updateRecBucket(terminalModel.getID_D(), 1);// 清空数据,硬件回收桶状态置为1 （1代表正常，0代表满了）

            }
        }

        for (int i = 0; i < plasticList.size(); i++) {
            TerminalModel terminalModel = plasticList.get(i);
            if (terminalModel != null) {
                dbManager.updateDev(terminalModel.getId(), 0);
                dbManager.updateRecBucket(terminalModel.getID_D(), 1);// 清空数据,硬件回收桶状态置为1 （1代表正常，0代表满了）
            }
        }

        for (int i = 0; i < metalList.size(); i++) {
            TerminalModel terminalModel = metalList.get(i);
            if (terminalModel != null) {
                dbManager.updateDev(terminalModel.getId(), 0);
                dbManager.updateRecBucket(terminalModel.getID_D(), 1);// 清空数据,硬件回收桶状态置为1 （1代表正常，0代表满了）
            }
        }
        // 结束旋转动画
        if (clp != null) {
            clp.cancel();
        }
        ll_upload_clear_data.setVisibility(View.INVISIBLE);
        // 下一步进行自检操作跳转到一个新的页面
        startActivity(new Intent(context, NextStepActivity.class));
        finish();
    }
}


