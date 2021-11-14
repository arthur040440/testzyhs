package com.zhangyuhuishou.zyhs.actys;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.tlh.android.config.Constant;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.utils.ToastUitls;
import com.tlh.android.utils.Utils;
import com.tlh.android.widget.ImageTextButton;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.adapter.PointAdapter;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.PointModel;
import com.zhangyuhuishou.zyhs.model.RangeInfoModel;
import com.zhangyuhuishou.zyhs.model.TerminalModel;
import com.zhangyuhuishou.zyhs.presenter.IPointView;
import com.zhangyuhuishou.zyhs.presenter.PointPresenter;
import com.zhangyuhuishou.zyhs.time.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;

/**
 * 作者:created by author:tlh
 * 日期:2018/8/9 19:26
 * 邮箱:tianlihui2234@live.com
 * 描述:设置页面
 */
public class SettingActivity extends BaseActivity implements IPointView, TextWatcher {

    private PointPresenter pointPresenter;
    private int comeFrom = 0;

    // 点位信息
    @BindView(R.id.btv_location_info)
    EditText btv_location_info;

    // 网络设置
    @BindView(R.id.itb_set)
    ImageTextButton itb_set;

    // 返回
    @BindView(R.id.tv_back)
    TextView tv_back;

    // 提交
    @BindView(R.id.tv_submit)
    TextView tv_submit;

    // 列表
    @BindView(R.id.lv_location)
    ListView lv_location;

    // 点位编码
    @BindView(R.id.tv_location_code)
    TextView tv_location_code;

    // 小区名称
    @BindView(R.id.tv_zone_name)
    TextView tv_zone_name;

    // 小区地址
    @BindView(R.id.tv_address)
    TextView tv_address;

    // 设备编码
    @BindView(R.id.tv_dev_code)
    TextView tv_dev_code;

    private String rangeName;// 点位名称
    private SearchTask mSearchTask;// 搜索任务
    private Handler mHandler = new Handler();
    private String currentLocationId;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        super.initData();
        EventBus.getDefault().register(this);
        comeFrom = getIntent().getExtras().getInt(Constant.LAUNCH_COME_FROM);
        pointPresenter = new PointPresenter(context);
        pointPresenter.attachView(this);
        mSearchTask = new SearchTask();
        // 判断是否设置过点位
        rangeId = currentLocationId = SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, "");
        if (!TextUtils.isEmpty(currentLocationId)) {
            SPUtils.getString(context, Constant.CURRENT_LOCATION_ID, "");
            tv_location_code.setText(getString(R.string.pt_code, SPUtils.getString(context, Constant.CURRENT_LOCATION_NUM, "")));
            tv_zone_name.setText(getString(R.string.living_areas, SPUtils.getString(context, Constant.CURRENT_COMMUNITYNAME, "")));
            tv_address.setText(SPUtils.getString(context, Constant.CURRENT_RANGENAME, ""));
            StringBuilder builder = new StringBuilder();
            DBManager dbManager = DBManager.getInstance(context);
            List<TerminalModel> terminalModelList = dbManager.getDevList();
            for (int i = 0; i < terminalModelList.size(); i++) {
                String terminalNum = terminalModelList.get(i).getTerminalNum();
                if (Utils.isEmpty(terminalNum)) {
                    terminalNum = "";
                }
                builder.append(terminalNum + "(" + terminalModelList.get(i).getTerminalTypeName() + ")");
                builder.append("\r\n");
            }
            tv_dev_code.setText(builder.toString());
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        itb_set.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
        tv_back.setOnClickListener(this);
        btv_location_info.addTextChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.itb_set:// 网络设置
                startActivity(new Intent(context, WifiSettingActivity.class));
                break;
            case R.id.tv_submit:// 激活
                if (TextUtils.isEmpty(rangeId)) {
                    ToastUitls.toastMessage("请输入点位进行选择");
                    return;
                }
                if (mPointModel == null) {
                    ToastUitls.toastMessage("请输入一个新的点位");
                    return;
                }
                pointPresenter.activePoint(mPointModel);
                break;
            case R.id.tv_back:// 返回上一页
                finishPage();
                break;
        }
    }

    private String rangeId = "";
    private List<PointModel.DataBean.TerminalListBean> terminals;
    private PointModel.DataBean mPointModel;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(PointModel.DataBean model) {
        mPointModel = model;
        // 初始化页面信息
        clearListData();
        tv_location_code.setText(getString(R.string.pt_code, model.getRangeNum()));
        tv_zone_name.setText(getString(R.string.living_areas, model.getCommunityName()));
        tv_address.setText(model.getRangeName());
        terminals = model.getTerminalList();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < terminals.size(); i++) {
            builder.append(terminals.get(i).getTerminalNum() + "(" + terminals.get(i).getTerminalTypeName() + ")");
            builder.append("\r\n");
        }
        tv_dev_code.setText(builder.toString());
        rangeId = model.getRangeId();
    }

    @Override
    public void getPoints(List<PointModel.DataBean> points) {
        if (points.size() == 0) {
            clearListData();
            return;
        }

        if (!TextUtils.isEmpty(currentLocationId)) {
            EventBus.getDefault().post(points.get(0));
            currentLocationId = "";
            return;
        }

        lv_location.setVisibility(View.VISIBLE);
        PointAdapter adapter = new PointAdapter(context, points);
        lv_location.setAdapter(adapter);
    }

    @Override
    public void activePoint(PointModel.DataBean model) {
        // 保存当前点位激活日期（后续电表数据需要）
        SPUtils.putString(context, Constant.CURRENT_DAY, TimeUtils.getDateByNow());
        // 存储点位Id
        SPUtils.putString(context, Constant.CURRENT_LOCATION_ID, model.getRangeId());
        SPUtils.putString(context, Constant.CURRENT_LOCATION_NUM, model.getRangeNum());
        SPUtils.putString(context, Constant.CURRENT_COMMUNITYNAME, model.getCommunityName());
        SPUtils.putString(context, Constant.CURRENT_RANGENAME, model.getRangeName());
        SPUtils.putString(context, Constant.CURRENT_LOCATION_SOURCE, model.getRangeSource());
        SPUtils.putString(context, Constant.CURRENT_COMMUNITY_ID, model.getCommunityId());
        SPUtils.putString(context, Constant.CURRENT_COMMUNITY_ADCODE, model.getAdCode());
        SPUtils.putString(context, Constant.CURRENT_POINT_ADCODE, model.getRangeNum());
        DBManager dbManager = DBManager.getInstance(context);
        // 重新设置数据
        dbManager.clearDevList();
        for (int i = 0; i < terminals.size(); i++) {
            dbManager.insertDev(terminals.get(i));
        }
        ToastUitls.toastMessage("激活成功");
        finishPage();
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


    // 结束页面
    private void finishPage() {
        switch (comeFrom) {
            case Constant.LAUNCH_COME_FROM_SPLASH:
                startActivity(new Intent(context, SplashActivity.class));
                break;
            case Constant.LAUNCH_COME_FROM_AllVIEW:
                break;
        }
        finish();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.e("输入前执行该方法", "文字变化");
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        Log.e("输入过程中执行该方法", "文字变化");
        rangeName = charSequence.toString();
        if (charSequence.length() > 0) {
            mHandler.removeCallbacks(mSearchTask);
            mHandler.postDelayed(mSearchTask, 500);
        } else {
            mHandler.removeCallbacks(mSearchTask);
            clearListData();
        }
    }

    // 清空（列表）数据
    private void clearListData() {
        lv_location.setVisibility(View.GONE);
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.e("输入后执行该方法", "文字变化");
    }

    /**
     * 搜索任务
     */
    private class SearchTask implements Runnable {

        @Override
        public void run() {
            Log.e("---SearchTask---", "开始查询");
            if (!TextUtils.isEmpty(rangeName)) {
                pointPresenter.getPoints(rangeName, "");
            }
        }
    }
}
