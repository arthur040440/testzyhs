package com.zhangyuhuishou.zyhs.actys;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.NetworkUtils;
import com.tlh.android.utils.SPUtils;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import java.util.List;
import butterknife.BindView;
import butterknife.BindViews;

/**
 * 作者:created by author:tlh
 * 日期:2018/8/10 17:31
 * 邮箱:tianlihui2234@live.com
 * 描述:设备状态（wifi）
 */

public class DevWifiStatusActivity extends BaseActivity {

    // 具体设备
    @BindView(R.id.itb_dev)
    TextView itb_dev;

    // IP, 掩码，网关
    @BindViews({R.id.tv_ip,R.id.tv_sub_net,R.id.tv_gateway})
    List<TextView> tvs;

    // 设备编号
    @BindView(R.id.dev_code)
    TextView dev_code;

    // 设备地址
    @BindView(R.id.dev_info)
    TextView dev_info;

    // 返回
    @BindView(R.id.tv_back_to_pre)
    TextView tv_back_to_pre;

    private Context context;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_dev_wifi_status;
    }

    @Override
    protected void initView() {
        context = DevWifiStatusActivity.this;
    }

    @Override
    protected void initData() {
        super.initData();
        itb_dev.setText("网络状态正常");
        tvs.get(0).setText("IP地址：    " + NetworkUtils.getNetworkInfo(DevWifiStatusActivity.this,0));
        tvs.get(1).setText("子网掩码："     + NetworkUtils.getNetworkInfo(DevWifiStatusActivity.this,1));
        tvs.get(2).setText("网关：        " + NetworkUtils.getNetworkInfo(DevWifiStatusActivity.this,2));
        String currentLocationId = SPUtils.getString(context, Constant.CURRENT_LOCATION_NUM);
        if(!TextUtils.isEmpty(currentLocationId)){
            dev_code.setText("编号：" + currentLocationId);
        }

        String location = SPUtils.getString(DevWifiStatusActivity.this,Constant.CURRENT_COMMUNITYNAME,"");
        if(!TextUtils.isEmpty(location)){
            dev_info.setText(location);
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        tv_back_to_pre.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_back_to_pre:
                finish();
                break;
        }
    }
}
