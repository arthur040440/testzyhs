package com.zhangyuhuishou.zyhs.actys;

import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.baidu.tts.util.TTSUtils;
import com.bumptech.glide.Glide;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.ClickUtils;
import com.tlh.android.utils.InitThrowViewUtils;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.utils.Utils;
import com.tlh.android.widget.CircularLinesProgress;
import com.tlh.android.widget.GlideCircleTransform;
import com.tlh.android.widget.ImageTextButton;
import com.tlh.android.widget.ImageViewWithTxt;
import com.tlh.android.widget.dialog.SelfDialogBuilder;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import com.zhangyuhuishou.zyhs.database.BucketStatusModel;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.ClassificationModel;
import com.zhangyuhuishou.zyhs.model.OrderArrayModelTest;
import com.zhangyuhuishou.zyhs.model.OrderDetailModel;
import com.zhangyuhuishou.zyhs.model.PointModel;
import com.zhangyuhuishou.zyhs.model.RangeInfoModel;
import com.zhangyuhuishou.zyhs.model.TerminalModel;
import com.zhangyuhuishou.zyhs.model.UserModel;
import com.zhangyuhuishou.zyhs.presenter.IOrderView;
import com.zhangyuhuishou.zyhs.presenter.IPointView;
import com.zhangyuhuishou.zyhs.presenter.OrderPresenter;
import com.zhangyuhuishou.zyhs.presenter.PointPresenter;
import com.zhangyuhuishou.zyhs.serialport.constant.NormalConstant;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortManager;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortUtils;
import com.zhangyuhuishou.zyhs.serialport.message.CloseDoorModel;
import com.zhangyuhuishou.zyhs.serialport.message.RecvMessage;
import com.zhangyuhuishou.zyhs.serialport.message.ThingModel;
import com.zhangyuhuishou.zyhs.serialport.util.ByteUtil;
import com.zhangyuhuishou.zyhs.serialport.util.Command;
import com.zhangyuhuishou.zyhs.time.TimeThread;
import com.zhangyuhuishou.zyhs.utils.LocalShared;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import butterknife.BindView;
import butterknife.BindViews;
import pl.droidsonroids.gif.GifImageView;

public class TestActivity extends BaseActivity implements IOrderView, IPointView {

    private String TAG = TestActivity.class.getSimpleName();
    // ????????????
    @BindView(R.id.tv_dev_code)
    TextView tv_dev_code;
    // ?????????
    @BindView(R.id.iv_drink)
    ImageViewWithTxt iv_drink;
    // ??????
    @BindView(R.id.iv_paper)
    ImageViewWithTxt iv_paper;
    // ?????????
    @BindView(R.id.iv_spin)
    ImageViewWithTxt iv_spin;
    // ??????
    @BindView(R.id.iv_glass)
    ImageViewWithTxt iv_glass;
    // ????????????
    @BindView(R.id.iv_garbage)
    ImageViewWithTxt iv_garbage;
    // ????????????
    @BindView(R.id.iv_plastic)
    ImageViewWithTxt iv_plastic;
    // ??????
    @BindView(R.id.iv_metal)
    ImageViewWithTxt iv_metal;
    // ??????
    @BindView(R.id.iv_other)
    ImageViewWithTxt iv_other;
    // ????????????
    @BindView(R.id.tv_finish_throw)
    TextView tv_finish_throw;
    // ??????????????????
    @BindView(R.id.tv_countdown_time)
    TextView tv_countdown_time;
    // ????????????
    @BindView(R.id.iv_user_logo)
    ImageView iv_user_logo;
    // ?????????-??????
    @BindViews({R.id.tv_user_money, R.id.tv_nickname})
    List<TextView> tv_zy_nick;
    // ??????????????????
    @BindViews({R.id.ll_type_ylp, R.id.ll_type_bl, R.id.ll_type_paper, R.id.ll_type_spin, R.id.ll_type_metal, R.id.ll_type_plastic, R.id.ll_type_poison})
    List<LinearLayout> typePages;
    // ???????????????????????????????????????????????????
    @BindView(R.id.iv_last_view)
    ImageView iv_last_view;
    // ????????????
    @BindView(R.id.time)
    TextView time;
    private TimeThread timeThread;// ???????????????

    // ??????????????????????????????
    @BindView(R.id.cv_finish_throw)
    CardView cv_finish_throw;

    // ????????????
    @BindView(R.id.clp)
    CircularLinesProgress clp;

    private CountDownTimer countDownTimer, finalDownTimer, otherDownTimer, poisonBtnDownTimer;// ??????????????????/?????????????????????????????????/????????????????????????????????????????????????/?????????????????????????????????60s???/???????????????????????????60s???
    private CountDownTimer exceptionCountDownTimer;// ???????????????
    private SelfDialogBuilder throwDialog, poisonDialog, dialogFault, orderDialog;// ????????????????????????
    private OrderArrayModelTest orderDataUtils;// ?????????????????????
    private int errorStatus = 3;// 1 ?????? 2 ?????? 3 ???????????????????????????)
    private boolean isHasThrowDate = false;

    private OrderPresenter orderPresenter;
    private PointPresenter pointPresenter;// ????????????

    private SerialPortUtils serialPortUtils;// ????????????

    private Handler handler = new Handler();

    // ???????????????
    private ImageTextButton itb_tip;// ????????????
    private TextView tv_num;// ????????????
    private TextView tv_tip;// ?????????????????????
    private TextView tv_throw_finish;// ??????????????????
    private TextView tv_throw_ok;// ???????????????
    private GifImageView iv_gif_close;// ????????????
    private ConstraintLayout ll_throw;// ????????????
    private TextView tv_unit;// ????????????/?????????
    private TextView tv_throw_tip;// ????????????

    private String currentSourceId = "";// ????????????
    private InitThrowViewUtils initThrowViewUtils;// ?????????????????????

    private boolean isOverWeight = false;// ?????????????????????????????????????????????????????????????????????

    // ?????????????????????
    private ConstraintLayout v_plastic_bottle, v_glass_bottle, v_paper, v_spin, v_metal, v_plastic, v_poison;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_test;
    }

    @Override
    protected void initView() {
        // ????????????????????????
        UserModel userModel = UserModel.getInstance();
        Glide.with(this).load(userModel.getProfilePhoto()).centerCrop().transform(new GlideCircleTransform(this)).crossFade(2000).into(iv_user_logo);
        tv_zy_nick.get(0).setText(userModel.getIntegral() + "");
        tv_zy_nick.get(1).setText(TextUtils.isEmpty(userModel.getNickName()) ? "????????????" : userModel.getNickName());
        String currentLocationId = SPUtils.getString(context, Constant.CURRENT_LOCATION_NUM);
        String communityName = SPUtils.getString(context, Constant.CURRENT_COMMUNITYNAME, "");
        tv_dev_code.setText(TextUtils.isEmpty(currentLocationId) ? "" : "NO." + currentLocationId + " " + communityName);
        // ?????????????????????
        initThrowViewUtils = new InitThrowViewUtils(iv_drink, iv_glass, iv_paper, iv_spin, iv_metal, iv_plastic, iv_garbage, this, typePages, iv_last_view);
        initThrowViewUtils.initView(context);
    }

    @Override
    protected void initData() {
        super.initData();

        //??????????????????????????????????????????????????????
        TTSUtils.speak(Constant.TTS_TIP_CHOOSE_THROW);
        timeThread = new TimeThread(time, TAG);
        timeThread.start();

        orderDataUtils = new OrderArrayModelTest(context);

        // ????????????
        throwDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_throw_test);
        poisonDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_throw_harmful);
        dialogFault = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_fault);
        orderDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_order);

        // ?????????????????????
        pointPresenter = new PointPresenter(context);
        pointPresenter.attachView(this);

        // ????????????
        orderPresenter = new OrderPresenter(context);
        orderPresenter.attachView(this);

        // ??????EventBus
        EventBus.getDefault().register(this);

        // ??????????????????
        serialPortUtils = new SerialPortUtils();
        serialPortUtils.openPort();


        // ??????????????????
        tv_countdown_time.setVisibility(View.INVISIBLE);
        exceptionCountDownTimer = new CountDownTimer(5000, 1000) {

            TextView tv_up_fault = null;

            @Override
            public void onTick(long millisUntilFinished) {
                if (tv_up_fault == null) {
                    tv_up_fault = (TextView) dialogFault.findViewById(R.id.tv_up_fault);
                }
                tv_up_fault.setText("?????????????????????" + millisUntilFinished / 1000 + "???");
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
                iv_garbage.setEnabled(true);
            }
        };

    }

    @Override
    protected void initListener() {
        super.initListener();
        // ????????????
        tv_finish_throw.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (ClickUtils.isFastClick(1000)) {
            return;
        }
        // ??????????????????
        handler.removeCallbacks(poisonRunnable);
        cancelTime();
        tv_countdown_time.setVisibility(View.GONE);
        switch (view.getId()) {
            case R.id.iv_drink:// ?????????????????????
                initTypeOpen(1001);
                break;
            case R.id.iv_glass:// ??????
                initTypeOpen(1002);
                break;
            case R.id.iv_paper:// ??????
                initTypeOpen(1);
                break;
            case R.id.iv_spin:// ?????????
                initTypeOpen(2);
                break;
            case R.id.iv_metal:// ??????
                initTypeOpen(3);
                break;
            case R.id.iv_plastic:// ????????????
                initTypeOpen(4);
                break;
            case R.id.iv_garbage:// ????????????
                initTypeOpen(5);
                break;
            case R.id.tv_finish_throw:// ????????????
                LocalShared.getInstance(getApplicationContext()).clearLocalData();
                finish();
                break;
        }
    }

    // ?????????????????????
    private void initTypeOpen(final int classId) {
        if (classId == 5) {
            if (TextUtils.isEmpty(UserModel.getInstance().getToken())) {
                return;
            }
            poisonBtnDownTimer.start();// ????????????
            poisonDialog.setOnClickListener(R.id.tv_throw_finish_harmful, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    poisonBtnDownTimer.cancel();
                    handler.removeCallbacks(poisonRunnable);
                    iv_garbage.setEnabled(true);
                    poisonDialog.dismiss();

                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer.start();
                        tv_countdown_time.setVisibility(View.VISIBLE);
                    }

                }
            }).show();
            TextView tv_tip = (TextView) poisonDialog.findViewById(R.id.tv_tip_harmful);
            tv_tip.setVisibility(View.INVISIBLE);
            ImageTextButton itb_tip = (ImageTextButton) poisonDialog.findViewById(R.id.itb_tip_harmful);
            v_poison = (ConstraintLayout) poisonDialog.findViewById(R.id.include_poison_harmful);
            v_poison.setVisibility(View.VISIBLE);
            itb_tip.setText("????????????????????????");
            // ????????????
            handler.postDelayed(poisonRunnable, 30000);
            TextView tv_unit = (TextView) poisonDialog.findViewById(R.id.tv_unit_harmful);
            TextView tv_num = (TextView) poisonDialog.findViewById(R.id.tv_num_harmful);
            orderDataUtils.setTextView(tv_num, tv_unit);
            orderDataUtils.dealOrderData(new ThingModel("32", 1));
            orderDataUtils.setPoisonOrder();
            iv_garbage.setEnabled(false);
            isFinishBtn();
            return;
        }

        throwDialog.setOnClickListener(R.id.tv_throw_finish, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTime();
                removeRunnable();
                switch (classId) {
                    case 1001:
                        TTSUtils.speak(Constant.TTS_TIP_PLASTIC_BOTTLE_DOOR_CLOSING);
                        break;
                    case 1002:
                        TTSUtils.speak(Constant.TTS_TIP_GLASS_BOTTLE_DOOR_CLOSING);
                        break;
                    case 1:
                        TTSUtils.speak(Constant.TTS_TIP_PAPER_DOOR_CLOSING);
                        break;
                    case 2:
                        TTSUtils.speak(Constant.TTS_TIP_SPIN_DOOR_CLOSING);
                        break;
                    case 3:
                        TTSUtils.speak(Constant.TTS_TIP_METAL_DOOR_CLOSING);
                        break;
                    case 4:
                        TTSUtils.speak(Constant.TTS_TIP_PLASTIC_DOOR_CLOSING);
                        break;
                }
                closeDoor();
            }
        }).setClickDismiss(true).show();

        tv_throw_ok = (TextView) throwDialog.findViewById(R.id.tv_throw_ok);
        itb_tip = (ImageTextButton) throwDialog.findViewById(R.id.itb_tip);
        tv_throw_tip = (TextView) throwDialog.findViewById(R.id.tv_throw_tip);

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
        iv_gif_close = (GifImageView) throwDialog.findViewById(R.id.iv_gif_close);
        ll_throw = (ConstraintLayout) throwDialog.findViewById(R.id.ll_throw);
        iv_gif_close.setVisibility(View.GONE);
        tv_throw_ok.setVisibility(View.GONE);
        itb_tip.setText("??????????????????????????????");
        ll_throw.setVisibility(View.VISIBLE);
        tv_tip.setText("");
        tv_tip.setVisibility(View.VISIBLE);
        tv_throw_tip.setVisibility(View.VISIBLE);
        tv_throw_finish.setVisibility(View.INVISIBLE);
        // ??????????????????
        orderDataUtils.setTextView(tv_num, tv_unit);
        switch (classId) {
            case 1001:
                // ?????????????????????????????????????????????
                TTSUtils.speak(Constant.TTS_TIP_PLASTIC_BOTTLE_DOOR_OPENING);
                tv_num.setText(orderDataUtils.getPlasticNum() + "");
                tv_unit.setText("???");
                v_plastic_bottle.setVisibility(View.VISIBLE);
                v_glass_bottle.setVisibility(View.GONE);
                v_paper.setVisibility(View.GONE);
                v_spin.setVisibility(View.GONE);
                v_metal.setVisibility(View.GONE);
                v_plastic.setVisibility(View.GONE);
                v_poison.setVisibility(View.GONE);
                break;
            case 1002:
                // ?????????????????????????????????????????????
                TTSUtils.speak(Constant.TTS_TIP_GLASS_BOTTLE_DOOR_OPENING);
                tv_num.setText(orderDataUtils.getGlassNum() + "");
                tv_unit.setText("???");
                v_plastic_bottle.setVisibility(View.GONE);
                v_glass_bottle.setVisibility(View.VISIBLE);
                v_paper.setVisibility(View.GONE);
                v_spin.setVisibility(View.GONE);
                v_metal.setVisibility(View.GONE);
                v_plastic.setVisibility(View.GONE);
                v_poison.setVisibility(View.GONE);
                break;
            case 1:
                // ?????????????????????????????????????????????
                TTSUtils.speak(Constant.TTS_TIP_PAPER_DOOR_OPENING);
                tv_num.setText(orderDataUtils.dealWeightData(orderDataUtils.getLocalPaperNum()) + "");
                tv_unit.setText("??????");
                v_plastic_bottle.setVisibility(View.GONE);
                v_glass_bottle.setVisibility(View.GONE);
                v_paper.setVisibility(View.VISIBLE);
                v_spin.setVisibility(View.GONE);
                v_metal.setVisibility(View.GONE);
                v_plastic.setVisibility(View.GONE);
                v_poison.setVisibility(View.GONE);
                break;
            case 2:
                // ?????????????????????????????????????????????
                TTSUtils.speak(Constant.TTS_TIP_SPIN_DOOR_OPENING);
                tv_num.setText(orderDataUtils.dealWeightData(orderDataUtils.getLocalClothNum()) + "");
                tv_unit.setText("??????");
                v_plastic_bottle.setVisibility(View.GONE);
                v_glass_bottle.setVisibility(View.GONE);
                v_paper.setVisibility(View.GONE);
                v_spin.setVisibility(View.VISIBLE);
                v_metal.setVisibility(View.GONE);
                v_plastic.setVisibility(View.GONE);
                v_poison.setVisibility(View.GONE);
                break;
            case 3:
                // ?????????????????????????????????????????????
                TTSUtils.speak(Constant.TTS_TIP_METAL_DOOR_OPENING);
                tv_num.setText(orderDataUtils.dealWeightData(orderDataUtils.getLocalMetalNum()) + "");
                tv_unit.setText("??????");
                v_plastic_bottle.setVisibility(View.GONE);
                v_glass_bottle.setVisibility(View.GONE);
                v_paper.setVisibility(View.GONE);
                v_spin.setVisibility(View.GONE);
                v_metal.setVisibility(View.VISIBLE);
                v_plastic.setVisibility(View.GONE);
                v_poison.setVisibility(View.GONE);
                break;
            case 4:
                // ?????????????????????????????????????????????
                TTSUtils.speak(Constant.TTS_TIP_PLASTIC_DOOR_OPENING);
                tv_num.setText(orderDataUtils.dealWeightData(orderDataUtils.getLocalCementNum()) + "");
                tv_unit.setText("??????");
                v_plastic_bottle.setVisibility(View.GONE);
                v_glass_bottle.setVisibility(View.GONE);
                v_paper.setVisibility(View.GONE);
                v_spin.setVisibility(View.GONE);
                v_metal.setVisibility(View.GONE);
                v_plastic.setVisibility(View.VISIBLE);
                v_poison.setVisibility(View.GONE);
                break;
            case 5:
                tv_num.setText(orderDataUtils.getPoisonNum() + "");
                tv_unit.setText("???");
                v_plastic_bottle.setVisibility(View.GONE);
                v_glass_bottle.setVisibility(View.GONE);
                v_paper.setVisibility(View.GONE);
                v_spin.setVisibility(View.GONE);
                v_metal.setVisibility(View.GONE);
                v_plastic.setVisibility(View.GONE);
                v_poison.setVisibility(View.VISIBLE);
                break;
        }
        DBManager dbManager = DBManager.getInstance(context);
        List<TerminalModel> plasticBottleList = dbManager.getDevListByType(Constant.DEV_PLASTIC_TYPE);
        List<TerminalModel> glassBottleList = dbManager.getDevListByType(Constant.DEV_GLASS_TYPE);
        List<TerminalModel> paperList = dbManager.getDevListByType(Constant.DEV_PAPER_TYPE);
        List<TerminalModel> spinList = dbManager.getDevListByType(Constant.DEV_SPIN_TYPE);
        List<TerminalModel> metalList = dbManager.getDevListByType(Constant.DEV_METAL_TYPE);
        List<TerminalModel> plasticList = dbManager.getDevListByType(Constant.DEV_CEMENT_TYPE);
        List<TerminalModel> harmfulList = dbManager.getDevListByType(Constant.DEV_POISON_TYPE);
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

                    if(terminalModel.getWeight() > Constant.MAX_BUCKET_PAPER_WEIGHT && model.getCOLUMN_REC_BUCKET_STA() == 0){
                        // ??????
                        continue;
                    }

                    // ????????????
                    currentSourceId = ByteUtil.decimal2fitHex(Integer.valueOf("1" + i));
                    orderDataUtils.setPaperTerminalModel(paperList.get(i));
                    break;

                }
                if(Utils.isEmpty(currentSourceId)){
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

                    if(terminalModel.getWeight() > Constant.MAX_BUCKET_SPIN_WEIGHT && model.getCOLUMN_REC_BUCKET_STA() == 0){
                        // ??????
                        continue;
                    }

                    // ????????????
                    currentSourceId = ByteUtil.decimal2fitHex(Integer.valueOf("2" + i));
                    orderDataUtils.setSpinTerminalModel(spinList.get(i));
                    break;

                }
                if(Utils.isEmpty(currentSourceId)){
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

                    if(terminalModel.getWeight() > Constant.MAX_BUCKET_METAL_WEIGHT && model.getCOLUMN_REC_BUCKET_STA() == 0){
                        // ??????
                        continue;
                    }

                    // ????????????
                    currentSourceId = ByteUtil.decimal2fitHex(Integer.valueOf("3" + i));
                    orderDataUtils.setMetalTerminalModel(metalList.get(i));
                    break;

                }
                if(Utils.isEmpty(currentSourceId)){
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

                    if(terminalModel.getWeight() > Constant.MAX_BUCKET_PLASTIC_WEIGHT && model.getCOLUMN_REC_BUCKET_STA() == 0){
                        // ??????
                        continue;
                    }

                    // ????????????
                    currentSourceId = ByteUtil.decimal2fitHex(Integer.valueOf("4" + i));
                    orderDataUtils.setCementTerminalModel(plasticList.get(i));
                    break;

                }
                if(Utils.isEmpty(currentSourceId)){
                    currentSourceId = ByteUtil.decimal2fitHex(Integer.valueOf("4" + "0"));
                    orderDataUtils.setCementTerminalModel(plasticList.get(0));
                }
                SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(currentSourceId, NormalConstant.DOOR_DOING_OPEN));
                break;
        }
        handler.postDelayed(openDoorRunnable, 20000);
    }

    // ????????????
    private void closeDoor() {
        if (iv_gif_close != null) {
            iv_gif_close.setVisibility(View.VISIBLE);
        }

        if (tv_throw_ok != null) {
            tv_throw_ok.setVisibility(View.GONE);
        }

        if (itb_tip != null) {
            itb_tip.setText("??????????????????");
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

        if (tv_throw_finish != null) {
            tv_throw_finish.setVisibility(View.INVISIBLE);
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

        // ?????????????????????
        if (NormalConstant.TYPE_PLASTIC.equals(currentSourceId)) {
            SerialPortManager.instance().sendCommand(Command.recoveryPlasticCmd(NormalConstant.DOOR_DOING_CLOSE));
            if (finalDownTimer != null) {
                finalDownTimer.cancel();

            }
        } else if (NormalConstant.TYPE_GLASS.equals(currentSourceId)) {
            // ?????????????????????????????????????????????
            SerialPortManager.instance().sendCommand(Command.recoveryGlassCmd(NormalConstant.DOOR_DOING_CLOSE));

            if (finalDownTimer != null) {
                finalDownTimer.cancel();

            }
        } else {
            // ?????????????????????????????????????????????
            if (TextUtils.isEmpty(currentSourceId)) {
                return;
            }
            SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(currentSourceId, NormalConstant.DOOR_DOING_CLOSE));
            if (otherDownTimer != null) {
                otherDownTimer.cancel();
            }
        }
        isFromBack = true;
        handler.postDelayed(closeDoorLater, 20000);
    }

    // ????????????
    @Override
    public void showOrderDetail(OrderDetailModel model) {
        cancelTime();
        removeRunnable();
        // ?????????????????????15?????????
        handler.postDelayed(timeOutRunnable, 15000);
        if (orderDialog != null) {
            orderDialog.setOnClickListener(R.id.tv_submit_order, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.post(timeOutRunnable);
                }
            });
            orderDataUtils.showOrderDetail(model, orderDialog);
        } else {
            handler.post(timeOutRunnable);
        }
    }

    @Override
    public void getRecyclerPrice(List<ClassificationModel.DataBean> data) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timeThread != null) {
            timeThread.removeTask();
        }
        cancelTime();
        removeRunnable();
        if (serialPortUtils != null) {
            serialPortUtils.closePort();
        }
        EventBus.getDefault().unregister(this);
        cancelDialog();
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
    protected void onDestroy() {
        super.onDestroy();
        if (clp != null) {
            clp.cancel();
        }
        handler.removeMessages(0);
    }

    // ????????????
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
    }

    // ??????????????????
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RecvMessage message) {
        Log.i("SerialPortManager", "?????????????????????" + message.getCommand());
        Command.dataCallBack(message.getCommand());
    }

    // ?????????????????????????????????
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDialog(String type) {
        if (tv_tip != null) {
            tv_tip.setVisibility(View.VISIBLE);
        }
        // ???????????????
        resetTime();
        switch (type) {
            case NormalConstant.RECYCLE_TIP_NOTHING:
                tv_tip.setText("");
                errorStatus = 3;
                break;
            case NormalConstant.RECYCLE_TIP_TOO_HEAVY:
                TTSUtils.speak(Constant.TTS_TIP_TOO_HEAVY);
                tv_tip.setText(Constant.TTS_TIP_TOO_HEAVY);
                errorStatus = 1;
                break;
            case NormalConstant.RECYCLE_TIP_TOO_LIGHT:
                TTSUtils.speak(Constant.TTS_TIP_TOO_LIGHT);
                tv_tip.setText("?????????????????????");
                errorStatus = 2;
                break;
            case NormalConstant.RECYCLE_TIP_CLOSING:// ??????
                break;
            case NormalConstant.INTERFACE_ACCESS_FAILURE:// ??????????????????
                finish();
                break;
            case NormalConstant.INTERFACE_ACCESS_SUCCESS:// ??????????????????
                cv_finish_throw.setVisibility(View.VISIBLE);
                if (clp != null) {
                    clp.cancel();
                }
                break;
        }
    }

    // ????????????????????????
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderMessageEvent(ThingModel model) {
        tv_tip.setText("");// ?????????????????????????????????
        errorStatus = 3;
        // ???????????????
        resetTime();
        if (NormalConstant.TYPE_PLASTIC.equals(model.getSourceId()) || NormalConstant.TYPE_GLASS.equals(model.getSourceId())) {
            if (model.getNum() > 0) {
                orderDataUtils.dealOrderData(model);
            }
        } else {
            orderDataUtils.dealOrderData(model);
        }

        if (isHasThrowDate) {
            return;
        }

        isFinishBtn();
    }

    private void isFinishBtn() {
        if (orderDataUtils.getOrderLength() != 0) {
            isHasThrowDate = true;
            tv_finish_throw.setText("????????????");
        }
    }

    private int fingerCenter = 0;


    private CloseDoorModel closeDoorModel;// ????????????

    // ??????????????????
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseDoorMessageEvent(final CloseDoorModel model) {
        closeDoorModel = model;
        switch (model.getStatus()) {
            // ???????????????
            case 0:
                closeDoorModel = null;
                handler.removeCallbacks(closeDoorLater);
                isFromBack = false;
                if (throwDialog.getDialog().isShowing()) {
                    throwDialog.dismiss();
                    cancelTime();
                    countDownTimer.start();
                    if (NormalConstant.TYPE_PLASTIC.equals(model.getSourceId()) || NormalConstant.TYPE_GLASS.equals(model.getSourceId())) {
                        // ????????????????????????
                        currentSourceId = "";
                        if (isOverWeight) {
                            countDownTimer.cancel();
                            handler.post(timeOutRunnable);
                            return;
                        }
                    } else {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                orderDataUtils.saveData(model);
                                // ????????????????????????
                                currentSourceId = "";
                                if (isOverWeight) {
                                    countDownTimer.cancel();
                                    handler.post(timeOutRunnable);
                                    return;
                                }
                                isFinishBtn();
                            }
                        }, 100);
                    }
                }
                break;
            case 1:// ???????????????
                handler.removeCallbacks(openDoorRunnable);
                fingerCenter = 0;
                //    ????????????????????????
                if (tv_throw_finish != null && tv_throw_finish.getVisibility() == View.INVISIBLE && iv_gif_close != null && iv_gif_close.getVisibility() != View.VISIBLE) {
                    tv_throw_finish.setVisibility(View.VISIBLE);
                }
                // ???????????????
                resetTime();
                break;
            case 2:// ???????????????
                fingerCenter = fingerCenter + 1;
                if (fingerCenter == 5) {
                    handler.removeCallbacks(closeDoorLater);
                    throwDialog.dismiss();
                    if (NormalConstant.TYPE_PLASTIC.equals(model.getSourceId())) {// ?????????
                        SerialPortManager.instance().sendCommand(Command.recoveryPlasticCmd(NormalConstant.DOOR_DOING_OPEN));
                    } else if (NormalConstant.TYPE_GLASS.equals(model.getSourceId())) {// ?????????
                        SerialPortManager.instance().sendCommand(Command.recoveryGlassCmd(NormalConstant.DOOR_DOING_OPEN));
                    } else {
                        SerialPortManager.instance().sendCommand(Command.recoveryOtherCmd(model.getSourceId(), NormalConstant.DOOR_DOING_OPEN));
                    }
                    dialogFault.setOnClickListener(R.id.tv_up_fault, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            exceptionCountDownTimer.cancel();
                            finish();
                        }
                    }).show();
                    exceptionCountDownTimer.start();
                    isFromBack = true;
                }
                break;
            // ???????????????????????????
            case 3:
                handler.removeCallbacks(openDoorRunnable);
                dealFaultData(model,3);
                break;
            // ???????????????????????????
            case 4:
                handler.removeCallbacks(closeDoorLater);
                dealFaultData(model,4);
                break;
            // ???????????????????????????
            case 5:
                handler.removeCallbacks(openDoorRunnable);
                dealFaultData(model,5);
                break;
        }
    }

    // ??????????????????
    private void dealFaultData(final CloseDoorModel model,int recGateSta){
        if (!isFinishing() && throwDialog != null && throwDialog.getDialog() != null && throwDialog.getDialog().isShowing()) {
            throwDialog.dismiss();
        }
        dialogFault.setOnClickListener(R.id.tv_up_fault, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDoorModel = null;
                exceptionCountDownTimer.cancel();
                finish();
            }
        });
        if (!isFinishing()) {
            dialogFault.show();
        }
        exceptionCountDownTimer.start();
    }

    // ???????????????
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

    // ???????????????
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
    }

    // ????????????
    private void removeRunnable() {
        handler.removeCallbacks(timeOutRunnable);
        handler.removeCallbacks(poisonRunnable);
        handler.removeCallbacks(closeDoorLater);
        handler.removeCallbacks(openDoorRunnable);
    }

    // ??????????????????
    private Runnable openDoorRunnable = new Runnable() {
        @Override
        public void run() {
            dealFaultData(closeDoorModel,6);
        }
    };


    private boolean isFromBack = false;// ????????????????????????
    private Runnable closeDoorLater = new Runnable() {
        @Override
        public void run() {
            if (isFromBack) {
                isFromBack = false;
                dealFaultData(closeDoorModel,7);
            }
        }
    };

    // ????????????
    private Runnable timeOutRunnable = new Runnable() {
        @Override
        public void run() {
            cancelTime();
            cancelDialog();
            finish();
        }
    };

    private Runnable poisonRunnable = new Runnable() {
        @Override
        public void run() {
            poisonDialog.dismiss();
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer.start();
            }
            tv_countdown_time.setVisibility(View.VISIBLE);
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
}
