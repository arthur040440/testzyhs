package com.zhangyuhuishou.zyhs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tlh.android.config.Constant;
import com.tlh.android.utils.ViewHolder;
import com.tlh.android.widget.ClearImageViewWithTxt;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.database.BucketStatusModel;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.TerminalModel;
import com.zhangyuhuishou.zyhs.serialport.util.ByteUtil;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 清理
 *
 * @author tlh
 */
public class ClearAdapter extends BaseAdapter {

    private List<TerminalModel> mList;
    private List<TerminalModel> mFactList = new ArrayList<>();// 真正的数据（除去有害）
    private Context mContext;
    private LayoutInflater mLInflater;
    private List<TerminalModel> plasticBottleList;
    private List<TerminalModel> glassBottleList;
    private int bottleNum = 0;
    private int glassNum = 0;
    private DBManager dbManager;


    public ClearAdapter(Context context) {
        this.mContext = context;
        dbManager = DBManager.getInstance(context);
        mList = dbManager.getDevList();
        for (int i = 0; i < mList.size(); i++) {
            TerminalModel terminalModel = mList.get(i);
            if (terminalModel != null && !Constant.DEV_POISON_TYPE.equals(terminalModel.getTerminalTypeId())) {
                mFactList.add(terminalModel);
            }
        }
        Collections.sort(mFactList, new Comparator<TerminalModel>() {
            /*
             * int compare(TerminalModel o1, TerminalModel o2) 返回一个基本类型的整型，
             * 返回负数表示：o1 小于o2，
             * 返回0 表示：o1和o2相等，
             * 返回正数表示：o1大于o2。
             */
            public int compare(TerminalModel o1, TerminalModel o2) {

                //按照设备ID大小
                if (ByteUtil.makeChecksum2(o1.getID_D()) > ByteUtil.makeChecksum2(o2.getID_D())) {
                    return 1;
                }
                if (ByteUtil.makeChecksum2(o1.getID_D()) == ByteUtil.makeChecksum2(o2.getID_D())) {
                    return 0;
                }
                return -1;
            }
        });
        mLInflater = LayoutInflater.from(mContext);

        plasticBottleList = dbManager.getDevListByType(Constant.DEV_PLASTIC_TYPE);
        glassBottleList = dbManager.getDevListByType(Constant.DEV_GLASS_TYPE);
        for (int i = 0; i < plasticBottleList.size(); i++) {
            bottleNum += plasticBottleList.get(i).getWeight();
        }
        for (int i = 0; i < glassBottleList.size(); i++) {
            glassNum = glassBottleList.get(i).getWeight();
        }
    }

    public void freshDataList() {
        mList = dbManager.getDevList();
        mFactList.clear();
        for (int i = 0; i < mList.size(); i++) {
            TerminalModel terminalModel = mList.get(i);
            if (terminalModel != null && !Constant.DEV_POISON_TYPE.equals(terminalModel.getTerminalTypeId())) {
                mFactList.add(terminalModel);
            }
        }
        Collections.sort(mFactList, new Comparator<TerminalModel>() {
            /*
             * int compare(TerminalModel o1, TerminalModel o2) 返回一个基本类型的整型，
             * 返回负数表示：o1 小于o2，
             * 返回0 表示：o1和o2相等，
             * 返回正数表示：o1大于o2。
             */
            public int compare(TerminalModel o1, TerminalModel o2) {

                //按照设备ID大小
                if (ByteUtil.makeChecksum2(o1.getID_D()) > ByteUtil.makeChecksum2(o2.getID_D())) {
                    return 1;
                }
                if (ByteUtil.makeChecksum2(o1.getID_D()) == ByteUtil.makeChecksum2(o2.getID_D())) {
                    return 0;
                }
                return -1;
            }
        });
        bottleNum = 0;
        glassNum = 0;
        plasticBottleList = dbManager.getDevListByType(Constant.DEV_PLASTIC_TYPE);
        glassBottleList = dbManager.getDevListByType(Constant.DEV_GLASS_TYPE);
        for (int i = 0; i < plasticBottleList.size(); i++) {
            bottleNum += plasticBottleList.get(i).getWeight();
        }
        for (int i = 0; i < glassBottleList.size(); i++) {
            glassNum = glassBottleList.get(i).getWeight();
        }
    }

    @Override
    public int getCount() {
        return mFactList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mFactList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLInflater.inflate(R.layout.clear_normal_item, null);
        }

        TerminalModel terminalModel = (TerminalModel) getItem(position);
        BucketStatusModel bucketStatusModel = dbManager.getModel(terminalModel.getID_D());
        ClearImageViewWithTxt iv_clear = ViewHolder.get(convertView, R.id.iv_clear);
        TextView tv_clear_num = ViewHolder.get(convertView, R.id.tv_clear_num);
        TextView tv_clear_tip = ViewHolder.get(convertView, R.id.tv_clear_tip);
        iv_clear.setOnClickListener(new OnMyClickListener(position));
        int maxWeight = 0;
        if (Constant.DEV_PLASTIC_TYPE.equals(terminalModel.getTerminalTypeId())) {
            iv_clear.setImageResource(R.mipmap.ic_class_drink);
        } else if (Constant.DEV_GLASS_TYPE.equals(terminalModel.getTerminalTypeId())) {
            iv_clear.setImageResource(R.mipmap.ic_class_glass);
            iv_clear.setEnabled(false);
        } else if (Constant.DEV_PAPER_TYPE.equals(terminalModel.getTerminalTypeId())) {
            iv_clear.setImageResource(R.mipmap.ic_class_paper);
            maxWeight = Constant.MAX_BUCKET_PAPER_WEIGHT;
        } else if (Constant.DEV_SPIN_TYPE.equals(terminalModel.getTerminalTypeId())) {
            iv_clear.setImageResource(R.mipmap.ic_class_spin);
            maxWeight = Constant.MAX_BUCKET_SPIN_WEIGHT;
        } else if (Constant.DEV_METAL_TYPE.equals(terminalModel.getTerminalTypeId())) {
            iv_clear.setImageResource(R.mipmap.ic_class_metal);
            maxWeight = Constant.MAX_BUCKET_METAL_WEIGHT;
        } else if (Constant.DEV_CEMENT_TYPE.equals(terminalModel.getTerminalTypeId())) {
            iv_clear.setImageResource(R.mipmap.ic_class_plastic);
            maxWeight = Constant.MAX_BUCKET_PLASTIC_WEIGHT;
        } else if (Constant.DEV_POISON_TYPE.equals(terminalModel.getTerminalTypeId())) {
            iv_clear.setImageResource(R.mipmap.ic_class_garbage);
        }
        if (bucketStatusModel == null) {
            iv_clear.setCabinetStatus(4);
            tv_clear_num.setText("暂不可用");
            tv_clear_tip.setText("未装配");
            iv_clear.setEnabled(false);
        } else {
            if (terminalModel.getID_D().equals("01") || terminalModel.getID_D().equals("02")) {
                if (bucketStatusModel.getCOLUMN_REC_BUCKET_STA() == 0 && bottleNum + glassNum >= Constant.BOTTOM_FULL_NUM) {
                    iv_clear.setCabinetStatus(3);
                    tv_clear_num.setText(terminalModel.getWeight() + "个");
                    tv_clear_tip.setText("已满");
                    tv_clear_tip.setTextColor(mContext.getResources().getColor(R.color.tip_red, null));
                } else {
                    if (bottleNum + glassNum == 0) {
                        iv_clear.setCabinetStatus(1);
                        tv_clear_num.setText("");
                    } else {
                        iv_clear.setCabinetStatus(2);
                        tv_clear_num.setText(terminalModel.getWeight() + "个");
                    }
                    float a = Float.valueOf((bottleNum + glassNum) + "f") / Constant.BOTTOM_FULL_NUM;
                    DecimalFormat df = new DecimalFormat("0.00");
                    a = Float.valueOf(df.format(a));
                    tv_clear_tip.setText("已使用" + a * 100 + "%");
                    tv_clear_tip.setTextColor(mContext.getResources().getColor(R.color.black, null));
                }
            } else {
                DecimalFormat otherDf = new DecimalFormat("0.00");

                if (bucketStatusModel.getCOLUMN_REC_BUCKET_STA() == 0 && terminalModel.getWeight() > maxWeight) {
                    iv_clear.setCabinetStatus(3);
                    tv_clear_num.setText(otherDf.format(Float.valueOf(terminalModel.getWeight() + "f") / 1000) + "公斤");
                    tv_clear_tip.setText("已满");
                    tv_clear_tip.setTextColor(mContext.getResources().getColor(R.color.tip_red, null));
                } else {
                    if (terminalModel.getWeight() == 0) {
                        iv_clear.setCabinetStatus(1);
                        tv_clear_num.setText("");
                    } else {
                        iv_clear.setCabinetStatus(2);
                        tv_clear_num.setText(otherDf.format(Float.valueOf(terminalModel.getWeight() + "f") / 1000) + "公斤");
                    }
                    float a = Float.valueOf(terminalModel.getWeight() / 1000 + "f") / Constant.BUCKET_FULL_OTHER_NUM;
                    DecimalFormat df = new DecimalFormat("0.00");
                    a = Float.valueOf(df.format(a));
                    tv_clear_tip.setText("已使用" + a * 100 + "%");
                    tv_clear_tip.setTextColor(mContext.getResources().getColor(R.color.black, null));
                }
            }
        }
        return convertView;
    }

    private class OnMyClickListener implements View.OnClickListener {

        private int pos;

        public OnMyClickListener(int pos) {
            this.pos = pos;
        }

        @Override
        public void onClick(View v) {
            TerminalModel model = (TerminalModel) getItem(pos);
            switch (v.getId()) {
                case R.id.iv_clear:
                    EventBus.getDefault().post(model);
                    break;
            }
        }
    }
}
