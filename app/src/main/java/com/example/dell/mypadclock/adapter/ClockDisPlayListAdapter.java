package com.example.dell.mypadclock.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.dell.mypadclock.R;
import com.example.dell.mypadclock.data.ClockParameterData;
import com.example.dell.mypadclock.utils.AddClockUtils;
import com.example.dell.mypadclock.utils.ClockController;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * 闹钟列表adapter
 */

public class ClockDisPlayListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ClockParameterData> mListData;
    public ClockDisPlayToogleBtIsOpen listeren;
    private boolean isEditClock;


    public ClockDisPlayListAdapter(Context context, ArrayList<ClockParameterData> listData, ClockDisPlayToogleBtIsOpen listeren) {
        this.mContext = context;
        this.mListData = listData;
        this.listeren = listeren;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.listview_main_clock_display_item, null);
            holder.tvStartTime = convertView.findViewById(R.id.list_item_display_start_time);
            holder.tvDate = (TextView) convertView.findViewById(R.id.list_item_display_date);
            holder.tvTag = (TextView) convertView.findViewById(R.id.list_item_display_tips_tag);
            holder.tvPeriod = convertView.findViewById(R.id.list_item_display_tips_period);
            holder.tvWhenStart = convertView.findViewById(R.id.list_item_display_tips_when_start);
            holder.toggleButton = (ToggleButton) convertView.findViewById(R.id.list_item_display_toggleButton);
            holder.ivDeleClock = convertView.findViewById(R.id.list_item_display_deleted);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        Log.d("tgw是否全部杀出222", "onIsDeletedClock: " +mListData.get(position).toString()+"---"+mListData.toString());
        ClockParameterData clockParameterData = mListData.get(position);


        if (clockParameterData.getClockTag() != null) {
            holder.tvTag.setText(clockParameterData.getClockTag());
        } else {
            //默认
            holder.tvTag.setText("闹钟");
        }
        //闹钟开启时间
        if (clockParameterData.getClockTime() != null) {
            String times[] = clockParameterData.getClockTime().split(",");
            String time = times[0] + ":" + times[1];
            holder.tvStartTime.setText(time);
            int hour = Integer.parseInt(times[0]);
            int min = Integer.parseInt(times[1]);
            if (hour >= 6 && hour < 12) {
                holder.tvDate.setText("上午");
            } else if (hour >= 12 && hour <= 14) {
                holder.tvDate.setText("中午");
            } else if (hour > 14 && hour <= 19) {
                holder.tvDate.setText("下午");
            } else if (hour > 19 && hour <= 24) {
                holder.tvDate.setText("晚上");
            } else if (hour >= 0 && hour < 6) {
                holder.tvDate.setText("凌晨");
            }
        } else {
            holder.tvStartTime.setText("没有设置时间");
        }

        if (clockParameterData.getClockPeriod() != null) {
            String repeat = "";
            String[] repeats = clockParameterData.getClockPeriod().split(",");
            boolean isEveryDay = true;//如果一周七天都选择了
            for (int i = 0; i < repeats.length; i++) {
                if (repeats[i].equals("true")) {
                    repeat = repeat + AddClockUtils.PERIOD_SELECT[i] + ",";
                } else {
                    isEveryDay = false;
                }
            }
            //首次
            if (repeat.equals("")) {
                holder.tvPeriod.setText("永不");
            } else {
                if (isEveryDay) {
                    holder.tvPeriod.setText("每天");
                } else {
                    //去除末尾多余的 逗号
                    repeat = repeat.substring(0, repeat.length() - 1);
                    holder.tvPeriod.setText(repeat);
                }
            }
        }

        //获取数据库中时间，计算闹钟剩余开启时间
        String times[] = clockParameterData.getClockTime().split(",");
        int[] time = ClockController.calculationStartTime(Integer.parseInt(times[0]), Integer.parseInt(times[1]), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE));
        if (time[0] == 0) {
            holder.tvWhenStart.setText( time[1] + "分钟后响铃");
        } else if (time[1] == 0) {
            holder.tvWhenStart.setText( time[0] + "小时后响铃");
        } else {
            holder.tvWhenStart.setText(time[0] + "小时" + time[1] + "分钟后响铃");
        }


        //数据库中的值为1开启闹钟
        if (clockParameterData.getClockIsOpen() == 1) {
            holder.toggleButton.setChecked(true);
            holder.tvStartTime.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.tvDate.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.tvTag.setTextColor(mContext.getResources().getColor(R.color.black_999999));
            holder.tvPeriod.setTextColor(mContext.getResources().getColor(R.color.black_999999));
            holder.tvWhenStart.setTextColor(mContext.getResources().getColor(R.color.black_999999));
        } else {
            holder.toggleButton.setChecked(false);
            holder.tvStartTime.setTextColor(mContext.getResources().getColor(R.color.black_555555));
            holder.tvDate.setTextColor(mContext.getResources().getColor(R.color.black_555555));
            holder.tvTag.setTextColor(mContext.getResources().getColor(R.color.black_555555));
            holder.tvPeriod.setTextColor(mContext.getResources().getColor(R.color.black_555555));
            holder.tvWhenStart.setTextColor(mContext.getResources().getColor(R.color.black_555555));
        }


        holder.toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.toggleButton.isChecked()) {
                    holder.tvStartTime.setTextColor(mContext.getResources().getColor(R.color.white));
                    holder.tvDate.setTextColor(mContext.getResources().getColor(R.color.white));
                    holder.tvTag.setTextColor(mContext.getResources().getColor(R.color.black_999999));
                    holder.tvPeriod.setTextColor(mContext.getResources().getColor(R.color.black_999999));
                    holder.tvWhenStart.setTextColor(mContext.getResources().getColor(R.color.black_999999));
                    if (listeren != null) {
                        listeren.toogleBtIsOpen(true, mListData.get(position).getId(), position);
                    }
                } else {
                    holder.tvStartTime.setTextColor(mContext.getResources().getColor(R.color.black_555555));
                    holder.tvDate.setTextColor(mContext.getResources().getColor(R.color.black_555555));
                    holder.tvTag.setTextColor(mContext.getResources().getColor(R.color.black_555555));
                    holder.tvPeriod.setTextColor(mContext.getResources().getColor(R.color.black_555555));
                    holder.tvWhenStart.setTextColor(mContext.getResources().getColor(R.color.black_555555));
                    if (listeren != null) {
                        listeren.toogleBtIsOpen(false, mListData.get(position).getId(), position);
                    }
                }
            }
        });



        holder.ivDeleClock.setVisibility(View.GONE);
        //删除闹钟
        if (ClockController.isEditClock) {
            holder.ivDeleClock.setVisibility(View.VISIBLE);
            holder.ivDeleClock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listeren != null) {
                        listeren.onIsDeletedClock(position, mListData.get(position).getId());
                    }
                }
            });
        } else {
            holder.ivDeleClock.setVisibility(View.GONE);
        }

        return convertView;
    }


    public void update(int position, ListView listview) {
        //得到第一个可见item项的位置
        int visiblePosition = listview.getFirstVisiblePosition();
        //得到指定位置的视图，对listview的缓存机制不清楚的可以去了解下
        View view = listview.getChildAt(position - visiblePosition);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.tvWhenStart = (TextView) view.findViewById(R.id.list_item_display_tips_when_start);
        setData(holder, position);
    }

    private void setData(ViewHolder holder, int position) {
        ClockParameterData data = mListData.get(position);
        //获取数据库中时间，计算闹钟剩余开启时间
        String times[] = data.getClockTime().split(",");
        int[] time = ClockController.calculationStartTime(Integer.parseInt(times[0]), Integer.parseInt(times[1]), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE));
        if (time[0] == 0) {
            holder.tvWhenStart.setText( time[1] + "分钟后响铃");
        } else if (time[1] == 0) {
            holder.tvWhenStart.setText( time[0] + "小时后响铃");
        } else {
            holder.tvWhenStart.setText(time[0] + "小时" + time[1] + "分钟后响铃");
        }

    }

    class ViewHolder {
        TextView tvStartTime, tvDate, tvTag, tvPeriod, tvWhenStart;
        ToggleButton toggleButton;
        ImageView ivDeleClock;
    }

    public interface ClockDisPlayToogleBtIsOpen {
        public void toogleBtIsOpen(boolean isOpen, int clockId, int position);

        void onIsDeletedClock(int position, int clockId);
    }
}
