package com.example.dell.mypadclock.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.mypadclock.R;
import com.example.dell.mypadclock.utils.AddClockUtils;

import java.util.ArrayList;

/**
 * 选择系统音乐
 */

public class SelectClockSystemMusicAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<AddClockUtils> mListData;
    private int clockMusicId;
    private CheckCheckBoxStatusListener listener;



    public SelectClockSystemMusicAdapter(Context context, ArrayList<AddClockUtils> listData,int clockMusicId, CheckCheckBoxStatusListener listener) {
        this.mContext = context;
        this.mListData = listData;
        this.clockMusicId = clockMusicId;
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

        holder.tvTitle.setText(mListData.get(position).getSystemMusicName());
        holder.ivCheckBox.setImageResource(R.drawable.unselect_);
//        Log.d("tgw系统adapter", "getView: 系统adapter"+clockMusicId+"==="+position);
        if (clockMusicId == position){
            holder.ivCheckBox.setImageResource(R.drawable.select_);
        }else {
            holder.ivCheckBox.setImageResource(R.drawable.unselect_);
        }

        holder.ivCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onGetSystemMusicDatas(position);
                }
                holder.ivCheckBox.setImageResource(R.drawable.select_);
            }
        });



        return convertView;
    }

    class ViewHolder {

        TextView tvTitle;
        ImageView ivCheckBox;


    }

    public interface CheckCheckBoxStatusListener {
        void onGetSystemMusicDatas(int position);
    }
}
