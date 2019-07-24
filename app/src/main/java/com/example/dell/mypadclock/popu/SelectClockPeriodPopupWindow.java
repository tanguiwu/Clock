package com.example.dell.mypadclock.popu;


import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.dell.mypadclock.R;
import com.example.dell.mypadclock.adapter.SelectClockPeriodAdapter;
import com.example.dell.mypadclock.utils.AddClockUtils;

import java.util.ArrayList;

/**
 * 周期选择PopupWindow
 */
public class SelectClockPeriodPopupWindow extends PopupWindow implements View.OnClickListener, SelectClockPeriodAdapter.CheckCheckBoxStatusListener {


    private TextView tvCancel, tvTitle, tvSave;
    private ImageView ivAddClock, ivAllPeriod;
    private ListView listView;
    private boolean isAllSelect = true;
    private Context mContext = null;
    private onPopupListener listener = null;
    private SelectClockPeriodAdapter adapter;
    private ArrayList<String> listDatas;
    private String periods;
    private String[] clockPeriods;
    private int screenHeight;
    private int screenWidth;


    public SelectClockPeriodPopupWindow(Context mContext, String periods, onPopupListener listener) {
        super(mContext);
        this.mContext = mContext;
        this.listener = listener;
        this.periods = periods;
        initView(mContext);
    }

    private void initView(Context mContext) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.add_clock_period_main, null);
        setContentView(rootView);
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
        setContentView(rootView);
        setWidth(screenWidth /5*3);
        setHeight(screenHeight / 3 * 2);

        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        tvCancel = rootView.findViewById(R.id.toolbar_tv_edit_or_cancel);
        tvTitle = rootView.findViewById(R.id.toolbar_title);
        tvSave = rootView.findViewById(R.id.toolbar_tv_save);
        ivAddClock = rootView.findViewById(R.id.toolbar_iv_add_clock);
        ivAllPeriod = rootView.findViewById(R.id.iv_period_all_checkbox);

        tvCancel.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        ivAllPeriod.setOnClickListener(this);

        listView = rootView.findViewById(R.id.list_view);
        initToolbar();
        clockPeriods = periods.split(",");
        listDatas = new ArrayList<>();
        for (int i = 0; i < AddClockUtils.VEERY_PERIOD_SELECT.length; i++) {
            listDatas.add(AddClockUtils.VEERY_PERIOD_SELECT[i]);
            if (clockPeriods[i].equals("false")) {
                isAllSelect = false;
            }
        }
        if (isAllSelect) {
            ivAllPeriod.setImageResource(R.drawable.select_);
        } else {
            ivAllPeriod.setImageResource(R.drawable.unselect_);
        }

        adapter = new SelectClockPeriodAdapter(mContext, listDatas, clockPeriods, this);
        listView.setAdapter(adapter);
    }

    public void show(View view) {
        showAtLocation(view, Gravity.BOTTOM, 0, screenHeight/6);//ApplicationInfo.getActionBarSize(mContext)

    }

    private void initToolbar() {
        tvCancel.setText("取消");
        tvTitle.setText("重复");
        tvSave.setVisibility(View.VISIBLE);
        ivAddClock.setVisibility(View.GONE);
        tvSave.setText("完成");
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.toolbar_tv_edit_or_cancel) {
            dismiss();
        } else if (viewId == R.id.toolbar_tv_save) {
            if (listener != null) listener.onSave(clockPeriods);
            dismiss();
        } else if (viewId == R.id.iv_period_all_checkbox) {
            if (isAllSelect) {
                for (int i = 0; i < 7; i++) {
                    clockPeriods[i] = "false";
                }
                isAllSelect = false;
                ivAllPeriod.setImageResource(R.drawable.unselect_);
            } else {
                for (int i = 0; i < 7; i++) {
                    clockPeriods[i] = "true";
                }
                isAllSelect = true;
                ivAllPeriod.setImageResource(R.drawable.select_);
            }
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onGetStatusDatas(String[] periods) {
        this.clockPeriods = periods;
        for (int i = 0; i < 7; i++) {
            if (clockPeriods[i].equals("false")) {
                isAllSelect = false;
                break;
            }else {
                isAllSelect = true;
            }
        }
//        Log.d("tgw是否全选", "onGetStatusDatas: "+clockPeriods.toString());
        if (isAllSelect) {
            ivAllPeriod.setImageResource(R.drawable.select_);
        } else {
            ivAllPeriod.setImageResource(R.drawable.unselect_);
        }

    }

    public interface onPopupListener {
        //保存设置的周期
        void onSave(String[] clockPeriods);

    }

}
