package com.example.dell.mypadclock.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.mypadclock.R;

import java.util.ArrayList;

/**
 * 选择重复天数adapter（周日---周六）
 */

public class SelectClockPeriodAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mListData;
    private CheckCheckBoxStatusListener listener;
    private String[] period;


    public SelectClockPeriodAdapter(Context context, ArrayList<String> listData, String[] clockPeriods, CheckCheckBoxStatusListener listener) {
        this.mContext = context;
        this.mListData = listData;
        this.period = clockPeriods;
        this.listener = listener;
    }


    @Override
    public int getCount() {
        return mListData == null ? 0 : mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 30;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.add_clock_period_item, null);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_period_name);
            holder.ivCheckBox = (ImageView) convertView.findViewById(R.id.iv_period_all_checkbox);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tvTitle.setText(mListData.get(position));

        if (period[position].equals("true")) {
            holder.ivCheckBox.setImageResource(R.drawable.select_);
            period[position] = "true";
        } else {
            holder.ivCheckBox.setImageResource(R.drawable.unselect_);
            period[position] = "false";
        }
        //点击选择重复天数事件
        holder.ivCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("tgwrepeatcheckbox", "onClick: " + position);
                if (period[position].equals("true")) {
                    holder.ivCheckBox.setImageResource(R.drawable.unselect_);
                    period[position] = "false";
                } else {
                    holder.ivCheckBox.setImageResource(R.drawable.select_);
                    period[position] = "true";
                }

                if (listener != null) {
                    listener.onGetStatusDatas(period);
                }
            }
        });


        return convertView;
    }

    class ViewHolder {

        TextView tvTitle;
        ImageView ivCheckBox;


    }

    public interface CheckCheckBoxStatusListener {
        void onGetStatusDatas(String[] listDatas);
    }
}
