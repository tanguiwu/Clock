package com.example.dell.mypadclock.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.dell.mypadclock.R;
import com.example.dell.mypadclock.data.ClockParameterData;
import com.example.dell.mypadclock.utils.AddClockUtils;

import java.util.ArrayList;


/**
 * 闹钟选择设置参数列表adapter
 */

public class AddClockParameterListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<AddClockUtils> mListData;
    public SetClockDetailsLister listeren;
    private ClockParameterData clockParameterData;


    public AddClockParameterListAdapter(Context context, ArrayList<AddClockUtils> listData, ClockParameterData clockParameterData, SetClockDetailsLister listeren) {
        this.mContext = context;
        this.mListData = listData;
        this.listeren = listeren;
        this.clockParameterData = clockParameterData;
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
    public int getItemViewType(int position) {
        int viewType = super.getItemViewType(position);
        try {
            viewType = mListData.get(position).getViewType();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return viewType;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int viewType = getItemViewType(position);
        if (convertView == null) {
            holder = new ViewHolder();
            switch (viewType) {
                case AddClockUtils.SET_CLOCK_PERIOD:
                case AddClockUtils.SET_CLOCK_TAG:
                case AddClockUtils.SET_CLOCK_MUSIC:
                case AddClockUtils.SET_CLOCK_REMIND:
                    convertView = View.inflate(mContext, R.layout.listview_add_clock_parameter_item, null);
                    holder.tvParameterName = convertView.findViewById(R.id.add_clock_parameter_tv_name);
                    holder.llParameter = (LinearLayout) convertView.findViewById(R.id.add_clock_parameter_ll);
                    holder.tvStatus = (TextView) convertView.findViewById(R.id.add_clock_parameter_tv_status);
                    holder.editTag = convertView.findViewById(R.id.add_clock_parameter_edit_tag);
                    holder.llToggle = convertView.findViewById(R.id.add_clock_parameter_ll_toggleButton);
                    holder.toggleButton = (ToggleButton) convertView.findViewById(R.id.add_clock_parameter_toggleButton);
                    break;
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        AddClockUtils bean = mListData.get(position);
        //区别toogleButton 与 选歌等布局

        if (bean.getViewType() == AddClockUtils.SET_CLOCK_REMIND) {
            holder.llParameter.setVisibility(View.GONE);
            holder.llToggle.setVisibility(View.VISIBLE);
        } else {
            holder.llParameter.setVisibility(View.VISIBLE);
            holder.llToggle.setVisibility(View.GONE);
            if (bean.getViewType() == AddClockUtils.SET_CLOCK_TAG) {
                holder.editTag.setVisibility(View.VISIBLE);
                holder.tvStatus.setVisibility(View.GONE);
            } else {
                holder.editTag.setVisibility(View.GONE);
                holder.tvStatus.setVisibility(View.VISIBLE);
            }
        }
        holder.tvParameterName.setText(mListData.get(position).getDetailsKinds());

        initData(holder, bean.getViewType(), bean, position);


        return convertView;
    }

    private void initData(final ViewHolder holder, int viewType, AddClockUtils bean, final int position) {
        switch (viewType) {
            case AddClockUtils.SET_CLOCK_PERIOD:
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
                        holder.tvStatus.setText("永不");
                    } else {
                        if (isEveryDay) {
                            holder.tvStatus.setText("每天");
                        } else {
                            //去除末尾多余的 逗号
                            repeat = repeat.substring(0, repeat.length() - 1);
                            holder.tvStatus.setText(repeat);
                        }
                    }
                }
                break;
            case AddClockUtils.SET_CLOCK_TAG:
                if (clockParameterData.getClockTag() != null) {
                    holder.editTag.setText(clockParameterData.getClockTag());
                } else {
                    holder.editTag.setText("无");
                }

                holder.editTag.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (listeren != null) {
                            listeren.onGetTagContent(holder.editTag.getText().toString());
                        }
                        return false;
                    }
                });



                break;
            case AddClockUtils.SET_CLOCK_MUSIC:
                if (clockParameterData.getClockMusic() != null) {
                    holder.tvStatus.setText(clockParameterData.getClockMusic());
                } else {
                    holder.tvStatus.setText("无音乐");
                }
                break;
            case AddClockUtils.SET_CLOCK_REMIND:


                if (clockParameterData.getClockRemain() == 1) {
                    holder.toggleButton.setChecked(true);
                } else {
                    holder.toggleButton.setChecked(false);
                }
                holder.toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (listeren != null) {
                            listeren.onGetClockRemain(isChecked);
                        }
                    }
                });
                break;
        }
    }


    class ViewHolder {
        TextView tvParameterName, tvStatus;
        EditText editTag;
        ToggleButton toggleButton;
        LinearLayout llParameter, llToggle;
    }

    public interface SetClockDetailsLister {
        public void onGetClockRemain(boolean isChecked);

        void onGetTagContent(String tagContent);
    }
}
