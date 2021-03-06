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

    // ????????????
    @BindView(R.id.clear_order_list)
    ListView clearOrderListView;
    // ????????????
    @BindView(R.id.tv_clear_order_finish)
    TextView tv_clear_order_finish;
    // ????????????
    @BindView(R.id.tv_clear_order_cancel)
    TextView tv_clear_order_cancel;
    // ????????????
    @BindView(R.id.iv_clear_user_logo)
    ImageView iv_clear_user_logo;
    // ?????????-??????
    @BindViews({R.id.tv_clear_user_money, R.id.tv_clear_nickname})
    List<TextView> tv_clear_user_params;

    // ??????????????????
    @BindView(R.id.ll_upload_clear_data)
    LinearLayout ll_upload_clear_data;

    // ????????????
    @BindView(R.id.clp)
    CircularLinesProgress clp;

    private ClearOrderAdapter clearOrderAdapter;
    private Handler mHandler = new Handler();
    private SerialPortUtils serialPortUtils;// ????????????
    private ClearPresenter clearPresenter;
    private SelfDialogBuilder clearUploadSuccessDialog, clearNetExceptionDialog, clearQrcodeDialog;
    //??????ID
    String rangeId;

    //???????????????(Bitmap?????????)
    private WeakReference<Bitmap> weakReference;

    //????????????????????????
    private CountDownTimer scanQrcodeDownTimer;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_clear_order;
    }

    @Override
    protected void initView() {

        // ????????????????????????
        UserModel userModel = UserModel.getInstance();
        Glide.with(this).load(userModel.getProfilePhoto()).centerCrop().transform(new GlideCircleTransform(this)).crossFade(2000).into(iv_clear_user_logo);
        String integral = userModel.getIntegral() + "";
        tv_clear_user_params.get(0).setText(integral);
        tv_clear_user_params.get(1).setText(TextUtils.isEmpty(userModel.getNickName()) ? "????????????" : userModel.getNickName());


        clearUploadSuccessDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_clear_upload_success);
        clearNetExceptionDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_clear_net_exception);
        clearQrcodeDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_clear_qrcode);

    }

    @Override
    protected void initData() {
        super.initData();

        // ????????????
        serialPortUtils = new SerialPortUtils();
        serialPortUtils.openPort();

        // ??????
        clearPresenter = new ClearPresenter(context);
        clearPresenter.attachView(this);

        // ???????????????
        clearOrderAdapter = new ClearOrderAdapter(context);
        clearOrderListView.setAdapter(clearOrderAdapter);
        clearOrderAdapter.setClearOrderListener(this);

        // ??????????????????
        tv_clear_order_finish.setOnClickListener(this);
        // ????????????
        tv_clear_order_cancel.setOnClickListener(this);

        rangeId = SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, "");

    }

    // ?????????????????????
    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (ClickUtils.isFastClick(500)) {
            return;
        }

        if (view.getId() == R.id.tv_clear_order_finish) {

            tv_clear_order_finish.setVisibility(View.INVISIBLE);
            tv_clear_order_cancel.setVisibility(View.INVISIBLE);
            // ????????????
            ll_upload_clear_data.setVisibility(View.VISIBLE);
            clp.start();
            isEmpty();
            // ????????????????????????????????????
            clearPresenter.createClearOrder(rangeId, plasticBottleModel.getWeight(), 0,
                    glassBottleModel.getWeight(), 0, 0, 0,
                    paperModel.getWeight(), 0, spinModel.getWeight(), 0,
                    plasticModel.getWeight(), 0, metalModel.getWeight(), 0);


        } else if (view.getId() == R.id.tv_clear_order_cancel) {
            finish();
        }
    }

    // ??????TerminalModel??????????????????????????????????????????
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


    // ???????????????
    @Override
    public void openDoor(final List<TerminalModel> terminalModelList) {
        if (terminalModelList == null || terminalModelList.size() == 0) {
            return;
        }

        Log.e(TAG, terminalModelList.toString());
        // ?????????????????????
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

    //????????????
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

    // ????????????
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

    // ????????????????????????
    @Override
    public void clearSuccessCallback() {

        clearUploadSuccessDialog.setOnClickListener(R.id.tv_clear_success_confirm, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDataAndJump();
            }
        }).setClickDismiss(true).show();

    }

    //????????????????????????
    @Override
    public void clearNetExceptionCallback(int flag) {

        Log.e(TAG, String.valueOf(flag));
        // ??????????????????
        cancelDialog();
        // ??????????????????
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

    //?????????????????????
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


    //???????????????????????????????????????
    private void clearDataAndJump() {
        // ??????????????????
        cancelDialog();
        // ???????????? ??????????????????
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
                dbManager.updateRecBucket(terminalModel.getID_D(), 1);// ????????????,???????????????????????????1 ???1???????????????0???????????????
                dbManager.updateCountException(terminalModel.getTerminalId(), 0);

            }
        }
        for (int i = 0; i < glassBottleList.size(); i++) {
            TerminalModel terminalModel = glassBottleList.get(i);
            if (terminalModel != null) {
                dbManager.updateDev(terminalModel.getId(), 0);
                dbManager.updateRecBucket(terminalModel.getID_D(), 1);// ????????????,???????????????????????????1 ???1???????????????0???????????????
                dbManager.updateCountException(terminalModel.getTerminalId(), 0);
            }
        }

        for (int i = 0; i < paperList.size(); i++) {
            TerminalModel terminalModel = paperList.get(i);
            if (terminalModel != null) {
                dbManager.updateDev(terminalModel.getId(), 0);
                dbManager.updateRecBucket(terminalModel.getID_D(), 1);// ????????????,???????????????????????????1 ???1???????????????0???????????????
            }
        }

        for (int i = 0; i < spinList.size(); i++) {
            TerminalModel terminalModel = spinList.get(i);
            if (terminalModel != null) {
                dbManager.updateDev(terminalModel.getId(), 0);
                dbManager.updateRecBucket(terminalModel.getID_D(), 1);// ????????????,???????????????????????????1 ???1???????????????0???????????????

            }
        }

        for (int i = 0; i < plasticList.size(); i++) {
            TerminalModel terminalModel = plasticList.get(i);
            if (terminalModel != null) {
                dbManager.updateDev(terminalModel.getId(), 0);
                dbManager.updateRecBucket(terminalModel.getID_D(), 1);// ????????????,???????????????????????????1 ???1???????????????0???????????????
            }
        }

        for (int i = 0; i < metalList.size(); i++) {
            TerminalModel terminalModel = metalList.get(i);
            if (terminalModel != null) {
                dbManager.updateDev(terminalModel.getId(), 0);
                dbManager.updateRecBucket(terminalModel.getID_D(), 1);// ????????????,???????????????????????????1 ???1???????????????0???????????????
            }
        }
        // ??????????????????
        if (clp != null) {
            clp.cancel();
        }
        ll_upload_clear_data.setVisibility(View.INVISIBLE);
        // ??????????????????????????????????????????????????????
        startActivity(new Intent(context, NextStepActivity.class));
        finish();
    }
}


