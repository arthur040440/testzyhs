package com.zhangyuhuishou.zyhs.adapter;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.tlh.android.utils.NumberUtils;
import com.tlh.android.utils.Utils;
import com.tlh.android.utils.ViewHolder;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.database.BucketStatusModel;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.TerminalModel;
import com.zhangyuhuishou.zyhs.serialport.constant.NormalConstant;
import com.zhangyuhuishou.zyhs.serialport.manager.SerialPortManager;
import com.zhangyuhuishou.zyhs.serialport.util.ByteUtil;
import com.zhangyuhuishou.zyhs.serialport.util.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * 控制设备开关
 *
 * @author tlh
 */
public class CtrlAdapter extends BaseAdapter {


    private List<BucketStatusModel> mList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mLInflater;

    private int devMask = 0;// 界面类型

    private DBManager dbManager;

    public CtrlAdapter(Context context, int devMask) {
        this.mContext = context;
        dbManager = DBManager.getInstance(context);
        this.devMask = devMask;
        switch (devMask) {
            case 1:
                this.mList = dbManager.getAllData();
                break;
            case 2:
            case 3:
                this.mList = dbManager.getAllDataExceptGlass();
                break;
            case 4:
                BucketStatusModel model = dbManager.getModel(NormalConstant.TYPE_PLASTIC);
                mList.add(model);
                break;
            case 6:
                this.mList = dbManager.getAllDataExceptGlass();
                break;

        }
        mLInflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (devMask == 6) {

            if (convertView == null) {
                convertView = mLInflater.inflate(R.layout.dev_temperature_show, null);
            }
            final BucketStatusModel model = (BucketStatusModel) getItem(position);
            TextView tv_device = ViewHolder.get(convertView, R.id.tv_device);
            TextView tv_temperature = ViewHolder.get(convertView, R.id.tv_temperature_show);

            if (model != null && !Utils.isEmpty(model.getCOLUMN_ID())) {
                if ("01".equals(model.getCOLUMN_ID())) {
                    tv_device.setText("主机：");
                    tv_temperature.setText(String.valueOf(model.getCOLUMN_WEIGHT_STA())+"℃");
                } else {
                    String COLUMN_ID = ByteUtil.makeChecksum2(model.getCOLUMN_ID()) + "";
                    String type = COLUMN_ID.substring(0, 1);
                    String num = "";
                    if (COLUMN_ID.length() == 2) {
                        num = Integer.valueOf(COLUMN_ID.substring(1, 2)) + 1 + "";
                    } else if (COLUMN_ID.length() == 1) {
                        num = 1 + "";
                    }
                    if ("1".equals(type)) {
                        // 纸柜
                        tv_device.setText(NumberUtils.toChineseNumber(num) + "号" + "纸张：");
                        tv_temperature.setText(String.valueOf(model.getCOLUMN_WEIGHT_STA())+"℃");

                    }
                    if ("2".equals(type)) {
                        // 织物
                        tv_device.setText(NumberUtils.toChineseNumber(num) + "号" + "织物：");
                        tv_temperature.setText(String.valueOf(model.getCOLUMN_WEIGHT_STA())+"℃");

                    }
                    if ("3".equals(type)) {
                        // 金属
                        tv_device.setText(NumberUtils.toChineseNumber(num) + "号" + "金属：");
                        tv_temperature.setText(String.valueOf(model.getCOLUMN_WEIGHT_STA())+"℃");

                    }
                    if ("4".equals(type)) {
                        // 塑料制品
                        tv_device.setText(NumberUtils.toChineseNumber(num) + "号" + "塑料制品：");
                        tv_temperature.setText(String.valueOf(model.getCOLUMN_WEIGHT_STA())+"℃");
                    }
                }
            }

        } else {
            if (convertView == null) {
                convertView = mLInflater.inflate(R.layout.activity_dev_ctrl_item, null);
            }
            final BucketStatusModel model = (BucketStatusModel) getItem(position);
            SwitchCompat sw_ctrl = ViewHolder.get(convertView, R.id.sw_ctrl);
            TextView tv_ctrl = ViewHolder.get(convertView, R.id.tv_ctrl);

            if (devMask == 1 && model != null && model.getCOLUMN_MAIN_DOOR_STA() == 1) {
                tv_ctrl.setTextColor(mContext.getResources().getColor(R.color.tip_red, null));
            }
            if (devMask == 1 && model != null && !Utils.isEmpty(model.getCOLUMN_ID())) { // 柜门
                if ("01".equals(model.getCOLUMN_ID())) {
                    tv_ctrl.setText("塑料(侧)");
                } else if ("02".equals(model.getCOLUMN_ID())) {
                    tv_ctrl.setText("玻璃(正)");
                } else {
                    String COLUMN_ID = ByteUtil.makeChecksum2(model.getCOLUMN_ID()) + "";
                    String type = COLUMN_ID.substring(0, 1);
                    String num = "";
                    if (COLUMN_ID.length() == 2) {
                        num = Integer.valueOf(COLUMN_ID.substring(1, 2)) + 1 + "";
                    } else if (COLUMN_ID.length() == 1) {
                        num = 1 + "";
                    }
                    if ("1".equals(type)) {
                        // 纸柜
                        tv_ctrl.setText(NumberUtils.toChineseNumber(num) + "号" + "纸张");
                    }
                    if ("2".equals(type)) {
                        // 织物
                        tv_ctrl.setText(NumberUtils.toChineseNumber(num) + "号" + "织物");
                    }
                    if ("3".equals(type)) {
                        // 金属
                        tv_ctrl.setText(NumberUtils.toChineseNumber(num) + "号" + "金属");
                    }
                    if ("4".equals(type)) {
                        // 塑料制品
                        tv_ctrl.setText(NumberUtils.toChineseNumber(num) + "号" + "塑料制品");
                    }
                }
            }

            if (devMask == 2 || devMask == 3) { // 灯箱/风扇
                if (model != null && !Utils.isEmpty(model.getCOLUMN_ID())) {
                    if ("01".equals(model.getCOLUMN_ID())) {
                        tv_ctrl.setText("主机");
                    } else {
                        String COLUMN_ID = ByteUtil.makeChecksum2(model.getCOLUMN_ID()) + "";
                        String type = COLUMN_ID.substring(0, 1);
                        String num = "";
                        if (COLUMN_ID.length() == 2) {
                            num = Integer.valueOf(COLUMN_ID.substring(1, 2)) + 1 + "";
                        } else if (COLUMN_ID.length() == 1) {
                            num = 1 + "";
                        }
                        if ("1".equals(type)) {
                            // 纸柜
                            tv_ctrl.setText(NumberUtils.toChineseNumber(num) + "号" + "纸张");
                        }
                        if ("2".equals(type)) {
                            // 织物
                            tv_ctrl.setText(NumberUtils.toChineseNumber(num) + "号" + "织物");
                        }
                        if ("3".equals(type)) {
                            // 金属
                            tv_ctrl.setText(NumberUtils.toChineseNumber(num) + "号" + "金属");
                        }
                        if ("4".equals(type)) {
                            // 塑料制品
                            tv_ctrl.setText(NumberUtils.toChineseNumber(num) + "号" + "塑料制品");
                        }
                    }
                }
            }

            if (devMask == 4 && model != null && !Utils.isEmpty(model.getCOLUMN_ID())) { // 消毒
                if ("01".equals(model.getCOLUMN_ID())) {
                    tv_ctrl.setText("主机");
                }
            }

            sw_ctrl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    switch (devMask) {
                        case 1:
                            if (model != null && !Utils.isEmpty(model.getCOLUMN_ID())) {
                                SerialPortManager.instance().sendCommand(Command.openDoor(model.getCOLUMN_ID()));
                            }
                            break;
                        case 2:
                            if (model != null && !Utils.isEmpty(model.getCOLUMN_ID())) {
                                SerialPortManager.instance().sendCommand(b ? Command.openLampBox(model.getCOLUMN_ID()) : Command.closeLampBox(model.getCOLUMN_ID()));
                            }
                            break;
                        case 3:
                            if (model != null && !Utils.isEmpty(model.getCOLUMN_ID())) {
                                SerialPortManager.instance().sendCommand(b ? Command.openFun(model.getCOLUMN_ID()) : Command.closeFun(model.getCOLUMN_ID()));
                            }
                            break;
                        case 4:
                            if (model != null && !Utils.isEmpty(model.getCOLUMN_ID())) {
                                SerialPortManager.instance().sendCommand(b ? Command.openShajun(model.getCOLUMN_ID()) : Command.closeShajun(model.getCOLUMN_ID()));
                            }
                            break;
                    }
                }
            });
        }
        return convertView;
    }
}
