package com.zhangyuhuishou.zyhs.actys;

import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.SPUtils;
import com.tlh.android.widget.ImageTextButton;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.adapter.CtrlAdapter;
import com.zhangyuhuishou.zyhs.base.BaseActivity;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortUtils;
import butterknife.BindView;

/**
 * 作者:created by author:tlh
 * 日期:2018/12/15 18:54
 * 邮箱:tianlihui2234@live.com
 * 描述: 设备控制
 */
public class DevCtrlActivity extends BaseActivity {

    // 设备编号
    @BindView(R.id.dev_code)
    TextView dev_code;

    // 设备地址
    @BindView(R.id.dev_info)
    TextView dev_info;

    // 返回
    @BindView(R.id.tv_back)
    TextView tv_back;

    // 页面提示
    @BindView(R.id.itb_dev)
    ImageTextButton itb_dev;

    private int devMask;// 设备标识

    private SerialPortUtils serialPortUtils;// 串口工具

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_dev_ctrl;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        super.initData();

        devMask = getIntent().getExtras().getInt(Constant.DEV_STATUS_MARK);
        switch (devMask){
            case Constant.DEV_STATUS_DOOR:
                itb_dev.setText("柜门控制");
                break;
            case Constant.DEV_STATUS_BOX:
                itb_dev.setText("灯箱控制");
                break;
            case Constant.DEV_STATUS_FUN:
                itb_dev.setText("风扇控制");
                break;
            case Constant.DEV_STATUS_DISINFECT:
                itb_dev.setText("消毒控制");
                break;
            case Constant.DEV_STATUS_TEMPERATURE:
                itb_dev.setText("温度显示");
                break;
        }
        // 打开串口
        serialPortUtils = new SerialPortUtils();
        serialPortUtils.openPort();

        String currentLocationId = SPUtils.getString(context, Constant.CURRENT_LOCATION_NUM);
        if (!TextUtils.isEmpty(currentLocationId)) {
            dev_code.setText("编号：" + currentLocationId);
        }

        String location = SPUtils.getString(context,Constant.CURRENT_COMMUNITYNAME,"");
        if (!TextUtils.isEmpty(location)) {
            dev_info.setText(location);
        }

        CtrlAdapter adapter = new CtrlAdapter(context,devMask);
        GridView gridView = findViewById(R.id.gridview);
        gridView.setAdapter(adapter);

    }

    @Override
    protected void initListener() {
        super.initListener();
        tv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (serialPortUtils != null) {
            serialPortUtils.closePort();
            serialPortUtils = null;
        }
    }
}
