package com.zhangyuhuishou.zyhs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tlh.android.utils.ViewHolder;
import com.zhangyuhuishou.zyhs.R;
import com.zhangyuhuishou.zyhs.model.PointModel;
import org.greenrobot.eventbus.EventBus;
import java.util.List;

public class PointAdapter extends BaseAdapter{

    private Context mContext;
    private LayoutInflater mLInflater;
    private List<PointModel.DataBean> list;

    public PointAdapter(Context context, List<PointModel.DataBean> list) {
        this.mContext = context;
        mLInflater = LayoutInflater.from(mContext);
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).hashCode();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = mLInflater.inflate(R.layout.activity_setting_item, null);
        }

        TextView tv_location_name = ViewHolder.get(convertView,R.id.tv_location_name);
        tv_location_name.setText(list.get(position).getRangeName());
        tv_location_name.setOnClickListener(new OnItemClick(position));
        return convertView;
    }

    private class OnItemClick implements View.OnClickListener{

        private int pos;

        public OnItemClick(int pos){
            this.pos = pos;
        }

        @Override
        public void onClick(View v) {
            EventBus.getDefault().post(list.get(pos));
        }
    }

}
