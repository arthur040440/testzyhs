package com.zhangyuhuishou.zyhs.actys;

import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.suke.widget.SwitchButton;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.ClickUtils;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.utils.Utils;
import com.tlh.android.widget.CircularLinesProgress;
import com.tlh.android.widget.GlideCircleTransform;
import com.tlh.android.widget.MyScrollView;
import com.tlh.android.widget.dialog.SelfDialogBuilder;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import com.zhangyuhuishou.zyhs.model.UserModel;
import com.zhangyuhuishou.zyhs.presenter.IMaintainView;
import com.zhangyuhuishou.zyhs.presenter.MaintainPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;

public class MaintainLogActivity extends BaseActivity implements SwitchButton.OnCheckedChangeListener, IMaintainView {


    // 竖向滚动View
    @BindView(R.id.sv_maintain_form)
    MyScrollView sv_maintain_form;

    // 提交巡检信息
    @BindView(R.id.tv_maintain_form_finish)
    TextView tv_maintain_form_finish;

    // 返回按钮
    @BindView(R.id.tv_maintain_form_cancel)
    TextView tv_maintain_form_cancel;

    // 用户头像
    @BindView(R.id.iv_maintain_user_logo)
    ImageView iv_maintain_user_logo;

    // 章鱼币-昵称
    @BindViews({R.id.tv_maintain_user_money, R.id.tv_maintain_nickname})
    List<TextView> tv_maintain_user_params;

    //巡检开关
    @BindViews({R.id.sb_cabinet_rust, R.id.sb_cabinet_broken, R.id.sb_cabinet_loose, R.id.sb_external_pipeline_broken,
            R.id.sb_door_glass_bottle, R.id.sb_door_plastic_bottle, R.id.sb_door_paper, R.id.sb_door_spin, R.id.sb_door_metal, R.id.sb_door_plastic,
            R.id.sb_door_glass_drive, R.id.sb_door_plastic_drive,
            R.id.sb_infrared_detection_bottle, R.id.sb_infrared_detection_paper, R.id.sb_infrared_detection_spin, R.id.sb_infrared_detection_metal, R.id.sb_infrared_detection_plastic,
            R.id.sb_maintain_fun_host, R.id.sb_maintain_fun_paper, R.id.sb_maintain_fun_spin, R.id.sb_maintain_fun_metal, R.id.sb_maintain_fun_plastic,
            R.id.sb_maintain_lamp_host, R.id.sb_maintain_lamp_paper, R.id.sb_maintain_lamp_spin, R.id.sb_maintain_lamp_metal, R.id.sb_maintain_lamp_plastic,
            R.id.sb_maintain_lock_host, R.id.sb_maintain_lock_paper, R.id.sb_maintain_lock_spin, R.id.sb_maintain_lock_side, R.id.sb_maintain_lock_metal,
            R.id.sb_maintain_screen,
            R.id.sb_maintain_ammeter,
            R.id.sb_front_camera})
    List<SwitchButton> list_maintain_switch;

    // 巡检备注
    @BindView(R.id.et_maintain_note)
    EditText et_maintain_note;

    private MyScrollView.OnMyScrollListener listener;
    private SelfDialogBuilder maintainOrderDialog;
    private MaintainPresenter maintainPresenter;
    private LinearLayout ll_upload_clear_data;// 旋转菊花布局
    // 旋转动画
    CircularLinesProgress clp;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_maintain_form;
    }

    @Override
    protected void initView() {

        // 设置当前用户信息
        UserModel userModel = UserModel.getInstance();
        Glide.with(this).load(userModel.getProfilePhoto()).centerCrop().transform(new GlideCircleTransform(this)).crossFade(2000).into(iv_maintain_user_logo);
        String integral = userModel.getIntegral() + "";
        tv_maintain_user_params.get(0).setText(integral);
        tv_maintain_user_params.get(1).setText(TextUtils.isEmpty(userModel.getNickName()) ? "章鱼游客" : userModel.getNickName());

        tv_maintain_form_finish.setBackgroundResource(R.drawable.shape_clear_finish_enable);
        tv_maintain_form_finish.setEnabled(false);
    }

    @Override
    protected void initData() {
        super.initData();
        // 清理
        maintainPresenter = new MaintainPresenter(context);
        maintainPresenter.attachView(this);
    }

    @Override
    protected void initListener() {
        super.initListener();
        et_maintain_note.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                EditText _v = (EditText) v;
                if (hasFocus) {
                    String hint = _v.getHint().toString();
                    _v.setTag(hint);
                    _v.setHint("");
                }
            }
        });

        listener = new MyScrollView.OnMyScrollListener() {
            @Override
            public void onScrollStateChanged(MyScrollView view, int state) {
            }

            @Override
            public void onScroll(MyScrollView view, int y) {
            }

            @Override
            public void onScrollToTop() {
            }

            @Override
            public void onScrollToBottom() {
                tv_maintain_form_finish.setBackgroundResource(R.drawable.sec_btn_theme_bg);
                tv_maintain_form_finish.setEnabled(true);
            }
        };

        maintainOrderDialog = new SelfDialogBuilder(context).setLayoutId(R.layout.dialog_maintain_log);
        tv_maintain_form_finish.setOnClickListener(this);
        tv_maintain_form_cancel.setOnClickListener(this);

        for (int i = 0; i < list_maintain_switch.size(); i++) {
            list_maintain_switch.get(i).setOnCheckedChangeListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sv_maintain_form.addOnScrollListner(listener);
    }

    //巡检单信息
    private StringBuilder report = new StringBuilder();

    //巡检单备注
    private String remark;

    // 提交巡检日志弹框
    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (ClickUtils.isFastClick(500)) {
            return;
        }

        if (view.getId() == R.id.tv_maintain_form_finish) {
            maintainOrderDialog.setOnClickListener(R.id.tv_order_back, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelDialog();
                }
            }).setClickDismiss(true).show();

            TextView clearOrderConfirm = (TextView) maintainOrderDialog.findViewById(R.id.tv_order_confirm);
            clp = (CircularLinesProgress) maintainOrderDialog.findViewById(R.id.clp);
            ll_upload_clear_data = (LinearLayout) maintainOrderDialog.findViewById(R.id.ll_upload_clear_data);

            clearOrderConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ClickUtils.isFastClick(500)) {
                        return;
                    }
                    // 旋转菊花
                    ll_upload_clear_data.setVisibility(View.VISIBLE);
                    clp.start();
                    dealReportMessages();
                }
            });
        } else if (view.getId() == R.id.tv_maintain_form_cancel) {
            finish();
        }
    }

    //处理巡检单数据
    private void dealReportMessages() {
        if (!Utils.isEmpty(sb_cabinet_rust) || !Utils.isEmpty(sb_cabinet_broken) || !Utils.isEmpty(sb_cabinet_loose) || !Utils.isEmpty(sb_external_pipeline_broken)) {
            report.append("外观检查：");
        }
        stringAppend(sb_cabinet_rust);
        stringAppend(sb_cabinet_broken);
        stringAppend(sb_cabinet_loose);
        stringAppend(sb_external_pipeline_broken);
        if (!Utils.isEmpty(sb_cabinet_rust) || !Utils.isEmpty(sb_cabinet_broken) || !Utils.isEmpty(sb_cabinet_loose) || !Utils.isEmpty(sb_external_pipeline_broken)) {
            report.append("；");
        }

        if (!Utils.isEmpty(sb_door_glass_bottle) || !Utils.isEmpty(sb_door_plastic_bottle) || !Utils.isEmpty(sb_door_paper) || !Utils.isEmpty(sb_door_spin) || !Utils.isEmpty(sb_door_metal) || !Utils.isEmpty(sb_door_plastic)) {
            report.append("开门机构：");
        }
        stringAppend(sb_door_glass_bottle);
        stringAppend(sb_door_plastic_bottle);
        stringAppend(sb_door_paper);
        stringAppend(sb_door_spin);
        stringAppend(sb_door_metal);
        stringAppend(sb_door_plastic);
        if (!Utils.isEmpty(sb_door_glass_bottle) || !Utils.isEmpty(sb_door_plastic_bottle) || !Utils.isEmpty(sb_door_paper) || !Utils.isEmpty(sb_door_spin) || !Utils.isEmpty(sb_door_metal) || !Utils.isEmpty(sb_door_plastic)) {
            report.append("；");
        }

        if (!Utils.isEmpty(sb_door_glass_drive) || !Utils.isEmpty(sb_door_plastic_drive)) {
            report.append("传动机构：");
        }
        stringAppend(sb_door_glass_drive);
        stringAppend(sb_door_plastic_drive);
        if (!Utils.isEmpty(sb_door_glass_drive) || !Utils.isEmpty(sb_door_plastic_drive)) {
            report.append("；");
        }

        if (!Utils.isEmpty(sb_infrared_detection_bottle) || !Utils.isEmpty(sb_infrared_detection_paper) || !Utils.isEmpty(sb_infrared_detection_spin) || !Utils.isEmpty(sb_infrared_detection_metal) || !Utils.isEmpty(sb_infrared_detection_plastic)) {
            report.append("红外检测：");
        }
        stringAppend(sb_infrared_detection_bottle);
        stringAppend(sb_infrared_detection_paper);
        stringAppend(sb_infrared_detection_spin);
        stringAppend(sb_infrared_detection_metal);
        stringAppend(sb_infrared_detection_plastic);
        if (!Utils.isEmpty(sb_infrared_detection_bottle) || !Utils.isEmpty(sb_infrared_detection_paper) || !Utils.isEmpty(sb_infrared_detection_spin) || !Utils.isEmpty(sb_infrared_detection_metal) || !Utils.isEmpty(sb_infrared_detection_plastic)) {
            report.append("；");
        }

        if (!Utils.isEmpty(sb_maintain_fun_host) || !Utils.isEmpty(sb_maintain_fun_paper) || !Utils.isEmpty(sb_maintain_fun_spin) || !Utils.isEmpty(sb_maintain_fun_metal) || !Utils.isEmpty(sb_maintain_fun_plastic)) {
            report.append("风扇：");
        }
        stringAppend(sb_maintain_fun_host);
        stringAppend(sb_maintain_fun_paper);
        stringAppend(sb_maintain_fun_spin);
        stringAppend(sb_maintain_fun_metal);
        stringAppend(sb_maintain_fun_plastic);
        if (!Utils.isEmpty(sb_maintain_fun_host) || !Utils.isEmpty(sb_maintain_fun_paper) || !Utils.isEmpty(sb_maintain_fun_spin) || !Utils.isEmpty(sb_maintain_fun_metal) || !Utils.isEmpty(sb_maintain_fun_plastic)) {
            report.append("；");
        }

        if (!Utils.isEmpty(sb_maintain_lamp_host) || !Utils.isEmpty(sb_maintain_lamp_paper) || !Utils.isEmpty(sb_maintain_lamp_spin) || !Utils.isEmpty(sb_maintain_lamp_metal) || !Utils.isEmpty(sb_maintain_lamp_plastic)) {
            report.append("灯带灯箱：");
        }
        stringAppend(sb_maintain_lamp_host);
        stringAppend(sb_maintain_lamp_paper);
        stringAppend(sb_maintain_lamp_spin);
        stringAppend(sb_maintain_lamp_metal);
        stringAppend(sb_maintain_lamp_plastic);
        if (!Utils.isEmpty(sb_maintain_lamp_host) || !Utils.isEmpty(sb_maintain_lamp_paper) || !Utils.isEmpty(sb_maintain_lamp_spin) || !Utils.isEmpty(sb_maintain_lamp_metal) || !Utils.isEmpty(sb_maintain_lamp_plastic)) {
            report.append("；");
        }

        if (!Utils.isEmpty(sb_maintain_lock_host) || !Utils.isEmpty(sb_maintain_lock_paper) || !Utils.isEmpty(sb_maintain_lock_spin) || !Utils.isEmpty(sb_maintain_lock_metal) || !Utils.isEmpty(sb_maintain_lock_side)) {
            report.append("柜门电磁锁：");
        }
        stringAppend(sb_maintain_lock_host);
        stringAppend(sb_maintain_lock_paper);
        stringAppend(sb_maintain_lock_spin);
        stringAppend(sb_maintain_lock_side);
        stringAppend(sb_maintain_lock_metal);
        if (!Utils.isEmpty(sb_maintain_lock_host) || !Utils.isEmpty(sb_maintain_lock_paper) || !Utils.isEmpty(sb_maintain_lock_spin) || !Utils.isEmpty(sb_maintain_lock_metal) || !Utils.isEmpty(sb_maintain_lock_side)) {
            report.append("；");
        }

        if (!Utils.isEmpty(sb_maintain_screen)) {
            report.append("显示屏触摸：");
        }
        stringAppend(sb_maintain_screen);
        if (!Utils.isEmpty(sb_maintain_screen)) {
            report.append("；");
        }


        if (!Utils.isEmpty(sb_maintain_ammeter)) {
            report.append("电表：");
        }
        stringAppend(sb_maintain_ammeter);
        if (!Utils.isEmpty(sb_maintain_ammeter)) {
            report.append("；");
        }

        if (!Utils.isEmpty(sb_front_camera)) {
            report.append("摄像头：");
        }
        stringAppend(sb_front_camera);
        if (!Utils.isEmpty(sb_front_camera)) {
            report.append("；");
        }

        if (!TextUtils.isEmpty(et_maintain_note.getText().toString())) {
            remark = et_maintain_note.getText().toString();
        }
        String rangeId = SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, "");
        // 请求接口（提交运维巡检单）
        maintainPresenter.submitInspectionLog(rangeId, report.toString(), remark);

    }

    @Override
    public void inspectionCallback() {

        // 结束旋转动画
        if (clp != null) {
            clp.cancel();
        }
        ll_upload_clear_data.setVisibility(View.INVISIBLE);
        cancelDialog();
        report.delete(0, report.length());
        remark = null;
        finish();
    }


    private void stringAppend(String reportType) {
        if (reportType != null && reportType.length() > 0) {
            report.append(reportType).append(" ");
        }
    }

    private String sb_cabinet_rust, sb_cabinet_broken, sb_cabinet_loose, sb_external_pipeline_broken, sb_door_glass_bottle, sb_door_plastic_bottle,
            sb_door_paper, sb_door_spin, sb_door_metal, sb_door_plastic, sb_door_glass_drive, sb_door_plastic_drive, sb_infrared_detection_bottle, sb_infrared_detection_paper, sb_infrared_detection_spin, sb_infrared_detection_metal, sb_infrared_detection_plastic,
            sb_maintain_fun_host, sb_maintain_fun_paper, sb_maintain_fun_spin, sb_maintain_fun_metal, sb_maintain_fun_plastic, sb_maintain_lamp_host, sb_maintain_lamp_paper, sb_maintain_lamp_spin, sb_maintain_lamp_metal, sb_maintain_lamp_plastic,
            sb_maintain_lock_host, sb_maintain_lock_paper, sb_maintain_lock_spin, sb_maintain_lock_side, sb_maintain_lock_metal, sb_maintain_screen, sb_maintain_ammeter, sb_front_camera;

    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.sb_cabinet_rust:
                if (isChecked) {
                    sb_cabinet_rust = getString(R.string.cabinet_rust);
                } else {
                    sb_cabinet_rust = "";
                }
                break;
            case R.id.sb_cabinet_broken:
                if (isChecked) {
                    sb_cabinet_broken = getString(R.string.cabinet_broken);
                } else {
                    sb_cabinet_broken = "";
                }
                break;
            case R.id.sb_cabinet_loose:
                if (isChecked) {
                    sb_cabinet_loose = getString(R.string.cabinet_loose);
                } else {
                    sb_cabinet_loose = "";
                }
                break;
            case R.id.sb_external_pipeline_broken:
                if (isChecked) {
                    sb_external_pipeline_broken = getString(R.string.external_pipeline_broken);
                } else {
                    sb_external_pipeline_broken = "";
                }
                break;
            case R.id.sb_door_glass_bottle:
                if (isChecked) {
                    sb_door_glass_bottle = getString(R.string.door_glass_bottle);
                } else {
                    sb_door_glass_bottle = "";
                }
                break;
            case R.id.sb_door_plastic_bottle:
                if (isChecked) {
                    sb_door_plastic_bottle = getString(R.string.door_plastic_bottle);
                } else {
                    sb_door_plastic_bottle = "";
                }
                break;
            case R.id.sb_door_paper:
                if (isChecked) {
                    sb_door_paper = getString(R.string.door_paper);
                } else {
                    sb_door_paper = "";
                }
                break;
            case R.id.sb_door_spin:
                if (isChecked) {
                    sb_door_spin = getString(R.string.door_door_spin);
                } else {
                    sb_door_spin = "";
                }
                break;
            case R.id.sb_door_metal:
                if (isChecked) {
                    sb_door_metal = getString(R.string.door_metal);
                } else {
                    sb_door_metal = "";
                }
                break;
            case R.id.sb_door_plastic:
                if (isChecked) {
                    sb_door_plastic = getString(R.string.door_plastic);
                } else {
                    sb_door_plastic = "";
                }
                break;
            case R.id.sb_door_glass_drive:
                if (isChecked) {
                    sb_door_glass_drive = getString(R.string.door_glass_drive);
                } else {
                    sb_door_glass_drive = "";
                }
                break;
            case R.id.sb_door_plastic_drive:
                if (isChecked) {
                    sb_door_plastic_drive = getString(R.string.door_plastic_drive);
                } else {
                    sb_door_plastic_drive = "";
                }
                break;
            case R.id.sb_infrared_detection_bottle:
                if (isChecked) {
                    sb_infrared_detection_bottle = getString(R.string.infrared_detection_bottle);
                } else {
                    sb_infrared_detection_bottle = "";
                }
                break;
            case R.id.sb_infrared_detection_paper:
                if (isChecked) {
                    sb_infrared_detection_paper = getString(R.string.infrared_detection_paper);
                } else {
                    sb_infrared_detection_paper = "";
                }
                break;
            case R.id.sb_infrared_detection_spin:
                if (isChecked) {
                    sb_infrared_detection_spin = getString(R.string.infrared_detection_spin);
                } else {
                    sb_infrared_detection_spin = "";
                }
                break;
            case R.id.sb_infrared_detection_metal:
                if (isChecked) {
                    sb_infrared_detection_metal = getString(R.string.infrared_detection_metal);
                } else {
                    sb_infrared_detection_metal = "";
                }
                break;
            case R.id.sb_infrared_detection_plastic:
                if (isChecked) {
                    sb_infrared_detection_plastic = getString(R.string.infrared_detection_plastic);
                } else {
                    sb_infrared_detection_plastic = "";
                }
                break;
            case R.id.sb_maintain_fun_host:
                if (isChecked) {
                    sb_maintain_fun_host = getString(R.string.maintain_fun_host);
                } else {
                    sb_maintain_fun_host = "";
                }
                break;
            case R.id.sb_maintain_fun_paper:
                if (isChecked) {
                    sb_maintain_fun_paper = getString(R.string.maintain_fun_paper);
                } else {
                    sb_maintain_fun_paper = "";
                }
                break;
            case R.id.sb_maintain_fun_spin:
                if (isChecked) {
                    sb_maintain_fun_spin = getString(R.string.maintain_fun_spin);
                } else {
                    sb_maintain_fun_spin = "";
                }
                break;
            case R.id.sb_maintain_fun_metal:
                if (isChecked) {
                    sb_maintain_fun_metal = getString(R.string.maintain_fun_metal);
                } else {
                    sb_maintain_fun_metal = "";
                }
                break;
            case R.id.sb_maintain_fun_plastic:
                if (isChecked) {
                    sb_maintain_fun_plastic = getString(R.string.maintain_fun_plastic);
                } else {
                    sb_maintain_fun_plastic = "";
                }
                break;
            case R.id.sb_maintain_lamp_host:
                if (isChecked) {
                    sb_maintain_lamp_host = getString(R.string.maintain_lamp_host);
                } else {
                    sb_maintain_lamp_host = "";
                }
                break;
            case R.id.sb_maintain_lamp_paper:
                if (isChecked) {
                    sb_maintain_lamp_paper = getString(R.string.maintain_lamp_paper);
                } else {
                    sb_maintain_lamp_paper = "";
                }
                break;
            case R.id.sb_maintain_lamp_spin:
                if (isChecked) {
                    sb_maintain_lamp_spin = getString(R.string.maintain_lamp_spin);
                } else {
                    sb_maintain_lamp_spin = "";
                }
                break;
            case R.id.sb_maintain_lamp_metal:
                if (isChecked) {
                    sb_maintain_lamp_metal = getString(R.string.maintain_lamp_metal);
                } else {
                    sb_maintain_lamp_metal = "";
                }
                break;
            case R.id.sb_maintain_lamp_plastic:
                if (isChecked) {
                    sb_maintain_lamp_plastic = getString(R.string.maintain_lamp_plastic);
                } else {
                    sb_maintain_lamp_plastic = "";
                }
                break;
            case R.id.sb_maintain_lock_host:
                if (isChecked) {
                    sb_maintain_lock_host = getString(R.string.maintain_lock_host);
                } else {
                    sb_maintain_lock_host = "";
                }
                break;
            case R.id.sb_maintain_lock_paper:
                if (isChecked) {
                    sb_maintain_lock_paper = getString(R.string.maintain_lock_paper);
                } else {
                    sb_maintain_lock_paper = "";
                }
                break;
            case R.id.sb_maintain_lock_spin:
                if (isChecked) {
                    sb_maintain_lock_spin = getString(R.string.maintain_lock_spin);
                } else {
                    sb_maintain_lock_spin = "";
                }
                break;
            case R.id.sb_maintain_lock_side:
                if (isChecked) {
                    sb_maintain_lock_side = getString(R.string.maintain_lock_side);
                } else {
                    sb_maintain_lock_side = "";
                }
                break;
            case R.id.sb_maintain_lock_metal:
                if (isChecked) {
                    sb_maintain_lock_metal = getString(R.string.maintain_lock_metal);
                } else {
                    sb_maintain_lock_metal = "";
                }
                break;
            case R.id.sb_maintain_screen:
                if (isChecked) {
                    sb_maintain_screen = getString(R.string.maintain_screen_calibration);
                } else {
                    sb_maintain_screen = "";
                }
                break;
            case R.id.sb_maintain_ammeter:
                if (isChecked) {
                    sb_maintain_ammeter = getString(R.string.maintain_ammeter_read);
                } else {
                    sb_maintain_ammeter = "";
                }
                break;
            case R.id.sb_front_camera:
                if (isChecked) {
                    sb_front_camera = getString(R.string.front_camera);
                } else {
                    sb_front_camera = "";
                }
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (Utils.isShouldHideKeyboard(v, ev)) {
                boolean res = Utils.hideKeyboard(this, v.getWindowToken());
                if (res) {
                    //隐藏了输入法，则不再分发事件
                    return true;
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onLocalVoiceInteractionStopped() {
        super.onLocalVoiceInteractionStopped();
        sv_maintain_form.removeOnScrollListener(listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelDialog();
    }

    // 取消弹框
    private void cancelDialog() {
        if (!isFinishing() && maintainOrderDialog != null && maintainOrderDialog.getDialog() != null && maintainOrderDialog.getDialog().isShowing()) {
            maintainOrderDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clp != null) {
            clp.cancel();
        }
    }

}
