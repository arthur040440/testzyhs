package com.zhangyuhuishou.zyhs.actys;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.ClickUtils;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.utils.ToastUitls;
import com.tlh.android.utils.Utils;
import com.tlh.android.widget.GlideCircleTransform;
import com.tlh.android.widget.dialog.SelfDialogBuilder;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.adapter.ClearAdapter;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.PointModel;
import com.zhangyuhuishou.zyhs.model.RangeInfoModel;
import com.zhangyuhuishou.zyhs.model.TerminalModel;
import com.zhangyuhuishou.zyhs.model.UserModel;
import com.zhangyuhuishou.zyhs.presenter.IPointView;
import com.zhangyuhuishou.zyhs.presenter.PointPresenter;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortManager;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortUtils;
import com.zhangyuhuishou.zyhs.serialport.message.ClearThingModel;
import com.zhangyuhuishou.zyhs.serialport.message.RecvMessage;
import com.zhangyuhuishou.zyhs.serialport.util.Command;
import com.zhangyuhuishou.zyhs.time.TimeUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.text.DecimalFormat;
import java.util.List;
import butterknife.BindView;

/**
 * 作者:created by author:tlh
 * 日期:2018/12/14 15:47
 * 邮箱:tianlihui2234@live.com
 * 描述:清运人员
 */

public class ClearPeopleActivity extends BaseActivity implements IPointView {

    // 下一步
    @BindView(R.id.tv_next_step)
    TextView tv_next_step;

    // 头像
    @BindView(R.id.iv_user_logo)
    ImageView iv_user_logo;

    // 昵称
    @BindView(R.id.tv_nickname)
    TextView tv_nickname;

    // 设备信息
    @BindView(R.id.tv_dev_code)
    TextView tv_dev_code;

    // 时间
    @BindView(R.id.tv_time)
    TextView tv_time;

    //小区名称
    @BindView(R.id.tv_name_company)
    TextView tv_name_company;

    //点位信息
    @BindView(R.id.tv_point_description)
    TextView tv_point_description;


    private ClearAdapter adapter;// 清运适配器
    private DBManager dbManager;
    private PointPresenter pointPresenter;
    private SerialPortUtils serialPortUtils;// 串口工具

    // 重新打开维护门，数据处理中
    private TextView tv_re_open, tv_data_deal_doing;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_clear_people;
    }

    @Override
    protected void initView() {

    }


    //是否展开显示点位信息
    private boolean isShowDes;

    @Override
    protected void initData() {
        super.initData();

        tv_name_company.setText(SPUtils.getString(context, Constant.CURRENT_COMMUNITYNAME, ""));

        tv_point_description.setText(SPUtils.getString(context, Constant.CURRENT_RANGENAME, ""));


        tv_point_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //这是点击的代码
                if (isShowDes) {
                    ViewGroup.LayoutParams layoutParams = tv_point_description.getLayoutParams();
                    layoutParams.width = 600;
                    tv_point_description.setLayoutParams(layoutParams);

                    tv_point_description.setEllipsize(TextUtils.TruncateAt.END);//收起
                    tv_point_description.setLines(1);
                } else {

                    ViewGroup.LayoutParams layoutParams = tv_point_description.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    tv_point_description.setLayoutParams(layoutParams);

                    tv_point_description.setEllipsize(null);//展开
                    tv_point_description.setSingleLine(false);//这个方法是必须设置的，否则无法展开
                }
                isShowDes = !isShowDes;


            }
        });


        // 设置当前用户信息
        UserModel userModel = UserModel.getInstance();
        Glide.with(this).load(userModel.getProfilePhoto()).centerCrop().transform(new GlideCircleTransform(this)).crossFade(2000).into(iv_user_logo);
        tv_nickname.setText(TextUtils.isEmpty(userModel.getNickName()) ? "章鱼游客" : userModel.getNickName());
        String currentLocationId = SPUtils.getString(context, Constant.CURRENT_LOCATION_NUM);
        String communityName = SPUtils.getString(context, Constant.CURRENT_COMMUNITYNAME, "");
        tv_dev_code.setText(TextUtils.isEmpty(currentLocationId) ? "" : "NO." + currentLocationId + " " + communityName);
        tv_time.setText(TimeUtils.getNowDate());

        EventBus.getDefault().register(this);
        // 打开串口
        serialPortUtils = new SerialPortUtils();
        serialPortUtils.openPort();

        GridView gridView = findViewById(R.id.gridview);
        adapter = new ClearAdapter(context);
        gridView.setAdapter(adapter);

        // 日志
        pointPresenter = new PointPresenter(context);
        pointPresenter.attachView(this);

    }

    @Override
    protected void initListener() {
        super.initListener();
        tv_next_step.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(ClickUtils.isFastClick(500)){
            return;
        }
        switch (view.getId()) {
            case R.id.tv_next_step:
                startActivity(new Intent(context, NextStepActivity.class));
                finish();
                break;
            case R.id.tv_re_open:// 重新开门
                if (terminalModel != null && !Utils.isEmpty(terminalModel.getID_D())) {
                    SerialPortManager.instance().sendCommand(Command.openDoor(terminalModel.getID_D()));
                }
                break;
            case R.id.tv_re_close:// 手动关门
                if (terminalModel != null && !Utils.isEmpty(terminalModel.getID_D())) {
                    recoveryData(terminalModel.getID_D());
                } else {
                    freshData(false);
                    showNetDialog("清理数据异常，自检后重新清理", true);
                }
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

    // 串口返回数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RecvMessage message) {
        Log.i("SerialPortManager", "收到反馈信息：" + message.getCommand());
        Command.dataCallBack(message.getCommand());
    }

    private TerminalModel terminalModel;

    // 清理图标点击事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClickMessage(TerminalModel terminalModel) {
        this.terminalModel = terminalModel;
        if (this.terminalModel == null) {
            ToastUitls.toastMessage("当前点击数据不存在！！！！！");
            return;
        }
        initClearWindow(terminalModel);
    }

    private SelfDialogBuilder bottleDialog;// 饮料瓶清理弹框
    private TextView tv_clear_name;// 清理名称
    private TextView tv_clear_num;// 清理数目
    private TextView tv_clear_unit;// 清理单元
    private List<TerminalModel> glassBottleList;
    private int glassNum = 0;

    // 初始化清理弹框
    private void initClearWindow(TerminalModel terminalModel) {
        if (bottleDialog == null) {
            bottleDialog = new SelfDialogBuilder(context);
            bottleDialog.setLayoutId(R.layout.dialog_clear_with_btn);
            bottleDialog.setOnClickListener(R.id.tv_re_open, this);
            bottleDialog.setOnClickListener(R.id.tv_re_close, this);
        }
        bottleDialog.show();
        bottleDialog.setClickDismiss(true);
        if (tv_clear_name == null) {
            tv_clear_name = (TextView) bottleDialog.findViewById(R.id.tv_clear_name);
        }
        if (tv_clear_num == null) {
            tv_clear_num = (TextView) bottleDialog.findViewById(R.id.tv_clear_num);
        }
        if (tv_clear_unit == null) {
            tv_clear_unit = (TextView) bottleDialog.findViewById(R.id.tv_clear_unit);
        }
        if (tv_re_open == null) {
            tv_re_open = (TextView) bottleDialog.findViewById(R.id.tv_re_open);
        }
        tv_re_open.setVisibility(View.VISIBLE);
        if (tv_data_deal_doing == null) {
            tv_data_deal_doing = (TextView) bottleDialog.findViewById(R.id.tv_data_deal_doing);
        }
        tv_data_deal_doing.setVisibility(View.GONE);
        DecimalFormat otherDf = new DecimalFormat("0.000");
        if (Constant.DEV_PLASTIC_TYPE.equals(terminalModel.getTerminalTypeId())) {
            dbManager = DBManager.getInstance(context);
            glassBottleList = dbManager.getDevListByType(Constant.DEV_GLASS_TYPE);
            for (int i = 0; i < glassBottleList.size(); i++) {
                glassNum = glassBottleList.get(i).getWeight();
            }
            tv_clear_name.setText("饮料瓶");
            tv_clear_num.setText(terminalModel.getWeight() + glassNum + "");
            tv_clear_unit.setText("个");
        } else if (Constant.DEV_PAPER_TYPE.equals(terminalModel.getTerminalTypeId())) {
            tv_clear_name.setText(terminalModel.getTerminalTypeName());
            tv_clear_num.setText(otherDf.format(Float.valueOf(terminalModel.getWeight() + "f") / 1000) + "");
            tv_clear_unit.setText("公斤");
        } else if (Constant.DEV_SPIN_TYPE.equals(terminalModel.getTerminalTypeId())) {
            tv_clear_name.setText(terminalModel.getTerminalTypeName());
            tv_clear_num.setText(otherDf.format(Float.valueOf(terminalModel.getWeight() + "f") / 1000) + "");
            tv_clear_unit.setText("公斤");
        } else if (Constant.DEV_METAL_TYPE.equals(terminalModel.getTerminalTypeId())) {
            tv_clear_name.setText(terminalModel.getTerminalTypeName());
            tv_clear_num.setText(otherDf.format(Float.valueOf(terminalModel.getWeight() + "f") / 1000) + "");
            tv_clear_unit.setText("公斤");
        } else if (Constant.DEV_CEMENT_TYPE.equals(terminalModel.getTerminalTypeId())) {
            tv_clear_name.setText(terminalModel.getTerminalTypeName());
            tv_clear_num.setText(otherDf.format(Float.valueOf(terminalModel.getWeight() + "f") / 1000) + "");
            tv_clear_unit.setText("公斤");
        } else if (Constant.DEV_POISON_TYPE.equals(terminalModel.getTerminalTypeId())) {
            tv_clear_name.setText(terminalModel.getTerminalTypeName());
            tv_clear_num.setText(terminalModel.getWeight());
            tv_clear_unit.setText("个");
        }

        // 发送命令（开启维护面门）
        SerialPortManager.instance().sendCommand(Command.openDoor(terminalModel.getID_D()));
    }

    // 维护面门关闭
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clearThingEvent(ClearThingModel model) {
        recoveryData(model.getSourceId());
        if (tv_re_open == null) {
            tv_re_open = (TextView) bottleDialog.findViewById(R.id.tv_re_open);
        }
        tv_re_open.setVisibility(View.GONE);
        if (tv_data_deal_doing == null) {
            tv_data_deal_doing = (TextView) bottleDialog.findViewById(R.id.tv_data_deal_doing);
        }
        tv_data_deal_doing.setVisibility(View.VISIBLE);
    }

    // 清空本地数据
    private void recoveryData(String sourceId) {
        if (!sourceId.equals(terminalModel.getID_D())) {
            return;
        }

        if (Constant.DEV_PLASTIC_TYPE.equals(terminalModel.getTerminalTypeId())) {
            if (terminalModel.getWeight() + glassNum > 0) {
                pointPresenter.logUpload(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, ""), terminalModel, Constant.LOG_CLEAR, "塑料瓶" + terminalModel.getWeight() + "个，玻璃瓶" + glassNum + "个");
            } else {
                freshData(false);
            }
        } else if (Constant.DEV_POISON_TYPE.equals(terminalModel.getTerminalTypeId())) {
            if (terminalModel.getWeight() > 0) {
                pointPresenter.logUpload(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, ""), terminalModel, Constant.LOG_CLEAR, terminalModel.getWeight() + "次");
            } else {
                freshData(false);
            }
        } else {
            if (terminalModel.getWeight() > 0) {
                pointPresenter.logUpload(SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, ""), terminalModel, Constant.LOG_CLEAR, terminalModel.getWeight() + "克");
            } else {
                freshData(false);
            }
        }
    }

    // 刷新数据
    private void freshData(boolean isFresh) {
        // 刷新页面
        if (isFresh) {
            adapter.freshDataList();
            adapter.notifyDataSetChanged();
        }
        if (!isFinishing() && bottleDialog != null && bottleDialog.getDialog() != null && bottleDialog.getDialog().isShowing()) {
            bottleDialog.dismiss();
        }
    }


    // 啥也没做
    @Override
    public void getPoints(List<PointModel.DataBean> points) {
    }

    @Override
    public void activePoint(PointModel.DataBean model) {
    }

    @Override
    public void updatePoint(RangeInfoModel model) {

    }
    // 啥也没做

    // 清理数据出现异常
    @Override
    public void clearError(String message) {
        freshData(false);
        showNetDialog(message, true);
        clearSuccess(this.terminalModel);
    }

    @Override
    public void clearSuccess(TerminalModel terminalModel) {
        if (terminalModel == null) {
            freshData(false);
            return;
        }
        dbManager = DBManager.getInstance(context);
        dbManager.updateDev(terminalModel.getId(), 0);
        dbManager.updateRecBucket(terminalModel.getID_D(), 1);// 清空数据,硬件回收桶状态置为1 （1代表正常，0代表满了）
        if (Constant.DEV_PLASTIC_TYPE.equals(terminalModel.getTerminalTypeId())) {
            glassBottleList = dbManager.getDevListByType(Constant.DEV_GLASS_TYPE);
            for (int i = 0; i < glassBottleList.size(); i++) {
                // 清空本地的玻璃瓶数量
                dbManager.updateDev(glassBottleList.get(i).getId(), 0);
                glassNum = 0;
            }
        }
        freshData(true);
        this.terminalModel = null;
    }
}
