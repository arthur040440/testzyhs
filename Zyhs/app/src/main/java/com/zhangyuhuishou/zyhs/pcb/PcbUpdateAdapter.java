package com.zhangyuhuishou.zyhs.pcb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tlh.android.config.Constant;
import com.tlh.android.utils.ClickUtils;
import com.tlh.android.utils.ViewHolder;
import com.tlh.android.widget.ImageViewWithTxt;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.database.BucketStatusModel;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.TerminalModel;
import com.zhangyuhuishou.zyhs.serialport.util.ByteUtil;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 作者:created by author:tlh
 * 日期:2019/4/26 14:11
 * 邮箱:tianlihui2234@live.com
 * 描述: 硬件程序升级适配器
 */
public class PcbUpdateAdapter extends BaseAdapter {

    private List<TerminalModel> mList;
    private List<TerminalModel> mEffectiveList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mLInflater;
    private DBManager dbManager;

    public PcbUpdateAdapter(Context context) {
        this.mContext = context;
        dbManager = DBManager.getInstance(context);
        mList = dbManager.getDevList();
        for (int i = 0;i < mList.size();i ++){
            TerminalModel model = mList.get(i);
            BucketStatusModel bucketStatusModel = dbManager.getModel(model.getID_D());
            if(!"02".equals(model.getID_D()) && bucketStatusModel != null && !Constant.DEV_POISON_TYPE.equals(model.getTerminalTypeId())){
                mEffectiveList.add(model);
            }
        }
        Collections.sort(mEffectiveList, new Comparator<TerminalModel>() {
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
    }

    // 刷新数据
    public void freshDataList() {
        mList = dbManager.getDevList();
        mEffectiveList.clear();
        for (int i = 0;i < mList.size();i ++){
            TerminalModel model = mList.get(i);
            BucketStatusModel bucketStatusModel = dbManager.getModel(model.getID_D());
            if(!"02".equals(model.getID_D()) && bucketStatusModel != null && !Constant.DEV_POISON_TYPE.equals(model.getTerminalTypeId())){
                mEffectiveList.add(model);
            }
        }
        Collections.sort(mEffectiveList, new Comparator<TerminalModel>() {
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
    }

    @Override
    public int getCount() {
        return mEffectiveList.size();
    }

    @Override
    public Object getItem(int position) {
        return mEffectiveList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mEffectiveList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLInflater.inflate(R.layout.pcb_update_normal_item, null);
        }

        TerminalModel terminalModel = (TerminalModel) getItem(position);
        BucketStatusModel bucketStatusModel = dbManager.getModel(terminalModel.getID_D());
        ImageViewWithTxt iv_update = ViewHolder.get(convertView, R.id.iv_update);
        TextView tv_version = ViewHolder.get(convertView, R.id.tv_version);
        iv_update.setOnClickListener(new OnMyClickListener(position));
        if (Constant.DEV_PLASTIC_TYPE.equals(terminalModel.getTerminalTypeId())) {
            iv_update.setImageResource(R.mipmap.ic_class_drink);
        } else if (Constant.DEV_PAPER_TYPE.equals(terminalModel.getTerminalTypeId())) {
            iv_update.setImageResource(R.mipmap.ic_class_paper);
        } else if (Constant.DEV_SPIN_TYPE.equals(terminalModel.getTerminalTypeId())) {
            iv_update.setImageResource(R.mipmap.ic_class_spin);
        } else if (Constant.DEV_METAL_TYPE.equals(terminalModel.getTerminalTypeId())) {
            iv_update.setImageResource(R.mipmap.ic_class_metal);
        } else if (Constant.DEV_CEMENT_TYPE.equals(terminalModel.getTerminalTypeId())) {
            iv_update.setImageResource(R.mipmap.ic_class_plastic);
        } else if (Constant.DEV_POISON_TYPE.equals(terminalModel.getTerminalTypeId())) {
            iv_update.setImageResource(R.mipmap.ic_class_garbage);
        }
        if(bucketStatusModel == null){
            iv_update.setEnabled(false);
        }else {
            iv_update.setDoorStatus(3);
            tv_version.setText("当前版本：" + bucketStatusModel.getVersion());
        }
        return convertView;
    }

    private class OnMyClickListener implements View.OnClickListener {

        private int pos;

        private OnMyClickListener(int pos) {
            this.pos = pos;
        }

        @Override
        public void onClick(View v) {
            if(ClickUtils.isFastClick(2000)){
                return;
            }
            TerminalModel model = (TerminalModel) getItem(pos);
            switch (v.getId()) {
                case R.id.iv_update:
                    EventBus.getDefault().post(model);
                    break;
            }
        }
    }
}
