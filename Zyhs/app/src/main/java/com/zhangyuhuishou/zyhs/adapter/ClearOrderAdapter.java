package com.zhangyuhuishou.zyhs.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tlh.android.config.Constant;
import com.tlh.android.utils.ClickUtils;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.database.DBManager;
import com.zhangyuhuishou.zyhs.model.TerminalModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ClearOrderAdapter extends BaseAdapter {

    private Context mContext;
    private List<TerminalModel> mFactList = new ArrayList<>();// 真正的数据（除去有害）
    private List<TerminalModel> plasticBottleList;
    private List<TerminalModel> glassBottleList;
    private List<TerminalModel> paperList;
    private List<TerminalModel> spinList;
    private List<TerminalModel> metalList;
    private List<TerminalModel> plasticList;

    //开门类型
    private List<TerminalModel> openDoorTypeList;

    public ClearOrderAdapter(Context mContext) {

        this.mContext = mContext;
        DBManager dbManager = DBManager.getInstance(mContext);
        plasticBottleList = dbManager.getDevListByType(Constant.DEV_PLASTIC_TYPE);
        glassBottleList = dbManager.getDevListByType(Constant.DEV_GLASS_TYPE);
        paperList = dbManager.getDevListByType(Constant.DEV_PAPER_TYPE);
        spinList = dbManager.getDevListByType(Constant.DEV_SPIN_TYPE);
        metalList = dbManager.getDevListByType(Constant.DEV_METAL_TYPE);
        plasticList = dbManager.getDevListByType(Constant.DEV_CEMENT_TYPE);

        for (int i = 0; i < plasticBottleList.size(); i++) {
            TerminalModel terminalModel = plasticBottleList.get(i);
            if (terminalModel != null) {
                mFactList.add(terminalModel);
            }
        }
        for (int i = 0; i < glassBottleList.size(); i++) {
            TerminalModel terminalModel = glassBottleList.get(i);
            if (terminalModel != null) {
                mFactList.add(terminalModel);
            }
        }

        if (paperList != null && paperList.size() > 0) {
            mFactList.add(paperList.get(0));
        }

        if (spinList != null && spinList.size() > 0) {
            mFactList.add(spinList.get(0));

        }
        if (metalList != null && metalList.size() > 0) {
            mFactList.add(metalList.get(0));

        }
        if (plasticList != null && plasticList.size() > 0) {
            mFactList.add(plasticList.get(0));
        }
    }

    @Override
    public int getCount() {
        if (null == mFactList) {
            return 0;
        }
        return mFactList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            // 解析自定义的布局成我们需要的view
            convertView = View.inflate(mContext, R.layout.item_clear_order, null);
            viewHolder = new ViewHolder();
            viewHolder.imageType = convertView.findViewById(R.id.iv_item_order_type);
            viewHolder.textReceivable = convertView.findViewById(R.id.tv_item_order_receivable);
            viewHolder.textReceivableUnit = convertView.findViewById(R.id.tv_item_order_receivable_unit);
            viewHolder.textOpenDoor = convertView.findViewById(R.id.tv_item_order_open_door);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final TerminalModel terminalModel = mFactList.get(position);
        if (Constant.DEV_PLASTIC_TYPE.equals(terminalModel.getTerminalTypeId())) {
            viewHolder.imageType.setImageResource(R.mipmap.ic_class_drink);
            viewHolder.textReceivableUnit.setText("个");
        } else if (Constant.DEV_GLASS_TYPE.equals(terminalModel.getTerminalTypeId())) {
            viewHolder.imageType.setImageResource(R.mipmap.ic_class_glass);
            viewHolder.textReceivableUnit.setText("个");
        } else if (Constant.DEV_PAPER_TYPE.equals(terminalModel.getTerminalTypeId())) {
            viewHolder.imageType.setImageResource(R.mipmap.ic_class_paper);
            viewHolder.textReceivableUnit.setText("公斤");
        } else if (Constant.DEV_SPIN_TYPE.equals(terminalModel.getTerminalTypeId())) {
            viewHolder.imageType.setImageResource(R.mipmap.ic_class_spin);
            viewHolder.textReceivableUnit.setText("公斤");
        } else if (Constant.DEV_METAL_TYPE.equals(terminalModel.getTerminalTypeId())) {
            viewHolder.imageType.setImageResource(R.mipmap.ic_class_metal);
            viewHolder.textReceivableUnit.setText("公斤");
        } else if (Constant.DEV_CEMENT_TYPE.equals(terminalModel.getTerminalTypeId())) {
            viewHolder.imageType.setImageResource(R.mipmap.ic_class_plastic);
            viewHolder.textReceivableUnit.setText("公斤");
        } else if (Constant.DEV_POISON_TYPE.equals(terminalModel.getTerminalTypeId())) {
            viewHolder.imageType.setImageResource(R.mipmap.ic_class_garbage);
            viewHolder.textReceivableUnit.setText("公斤");
        }

        if ("01".equals(terminalModel.getID_D()) || "02".equals(terminalModel.getID_D())) {
            viewHolder.textReceivable.setText(String.valueOf(terminalModel.getWeight()));
            clearOrderListener.transmitData(terminalModel, terminalModel.getWeight());
        } else {
            int weight = 0;
            switch (terminalModel.getTerminalTypeId()) {
                case Constant.DEV_PAPER_TYPE:
                    for (int i = 0; i < paperList.size(); i++) {
                        weight += paperList.get(i).getWeight();
                    }
                    break;
                case Constant.DEV_SPIN_TYPE:
                    for (int i = 0; i < spinList.size(); i++) {
                        weight += spinList.get(i).getWeight();
                    }
                    break;
                case Constant.DEV_METAL_TYPE:
                    for (int i = 0; i < metalList.size(); i++) {
                        weight += metalList.get(i).getWeight();
                    }
                    break;
                case Constant.DEV_CEMENT_TYPE:
                    for (int i = 0; i < plasticList.size(); i++) {
                        weight += plasticList.get(i).getWeight();
                    }
                    break;
            }
            DecimalFormat otherDf = new DecimalFormat("0.00");
            viewHolder.textReceivable.setText(otherDf.format(Float.valueOf(weight + "f") / 1000));
            clearOrderListener.transmitData(terminalModel, weight);

        }


        viewHolder.textOpenDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtils.isFastClick(500)) {
                    return;
                }
                switch (terminalModel.getTerminalTypeId()) {
                    case Constant.DEV_PLASTIC_TYPE:
                        openDoorTypeList = plasticBottleList;
                        break;
                    case Constant.DEV_GLASS_TYPE:
                        openDoorTypeList = glassBottleList;
                        break;
                    case Constant.DEV_PAPER_TYPE:
                        openDoorTypeList = paperList;
                        break;
                    case Constant.DEV_SPIN_TYPE:
                        openDoorTypeList = spinList;
                        break;
                    case Constant.DEV_METAL_TYPE:
                        openDoorTypeList = metalList;
                        break;
                    case Constant.DEV_CEMENT_TYPE:
                        openDoorTypeList = plasticList;
                        break;
                }
                clearOrderListener.openDoor(openDoorTypeList);
            }
        });

        return convertView;
    }

    class ViewHolder {

        ImageView imageType;
        TextView textReceivable;
        TextView textReceivableUnit;
        TextView textOpenDoor;
    }


    public ClearOrderListener clearOrderListener;

    public interface ClearOrderListener {

        void openDoor(List<TerminalModel> terminalModelList);

        void transmitData(TerminalModel terminalModel, int weight);
    }

    public void setClearOrderListener(ClearOrderListener clearOrderListener) {
        this.clearOrderListener = clearOrderListener;
    }

}
