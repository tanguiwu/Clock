package com.example.dell.mypadclock.popu;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.mypadclock.AddClockActivity;
import com.example.dell.mypadclock.R;
import com.example.dell.mypadclock.adapter.SelectClockPeriodAdapter;
import com.example.dell.mypadclock.adapter.SelectClockSystemMusicAdapter;
import com.example.dell.mypadclock.utils.AddClockUtils;

import java.util.ArrayList;

/**
 * 选择音乐类型PopupWindow
 */
public class SelectClockMusicTypePopupWindow extends PopupWindow implements View.OnClickListener, SelectClockSystemMusicAdapter.CheckCheckBoxStatusListener {


    private View rootView;
    private View parentView;
    private TextView tvCancel, tvTitle, tvSave, tvMusicType, tvMusicNmae;
    private ImageView ivSystemChecked;
    private LinearLayout llLocalMusic;
    private ImageView ivAddClock, ivCheckedMusic;
    private ListView listView;
    private Context mContext = null;
    private onPopupListener listener = null;
    private SelectClockSystemMusicAdapter adapter;
    private ArrayList<AddClockUtils> listDatas;
    private String clockMusic;
    private int clockMusicId;
    private SelectLocalClockMusicPopupWindow localClockMusicPopupWindow;
    private MediaPlayer player = null;
    private int screenHeight;
    private int screenWidth;


    public SelectClockMusicTypePopupWindow(Context mContext, String clockMusic, int clockMusicId, onPopupListener listener) {
        super(mContext);
        this.mContext = mContext;
        this.listener = listener;
        this.clockMusic = clockMusic;
        this.clockMusicId = clockMusicId;
        initView(mContext);
    }

    private void initView(final Context mContext) {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.add_clock_music_type_main, null);
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
        setContentView(rootView);
        setWidth(screenWidth /5*3);
        setHeight(screenHeight / 3 * 2);
//        Log.d("tgw屏幕大小", "initView: "+screenWidth+"---"+screenHeight);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        tvCancel = rootView.findViewById(R.id.toolbar_tv_edit_or_cancel);
        tvTitle = rootView.findViewById(R.id.toolbar_title);
        tvSave = rootView.findViewById(R.id.toolbar_tv_save);
        ivAddClock = rootView.findViewById(R.id.toolbar_iv_add_clock);
        ivCheckedMusic = rootView.findViewById(R.id.iv_period_all_checkbox);
        tvMusicType = rootView.findViewById(R.id.tv_period_name);
        llLocalMusic = rootView.findViewById(R.id.ll_select_local_music);
        ivSystemChecked = rootView.findViewById(R.id.iv_system_checkbox);

        if (clockMusicId <= 5 && clockMusicId >= 0) {
            tvMusicType.setText("从本地文件中选择");
        } else {
            tvMusicType.setText("本地音乐-" + clockMusic);
        }
        ivCheckedMusic.setImageResource(R.drawable.office_right_arrow);


        tvCancel.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        ivCheckedMusic.setOnClickListener(this);
        llLocalMusic.setOnClickListener(this);
        ivSystemChecked.setOnClickListener(this);
//        listView = rootView.findViewById(R.id.list_view);
        initToolbar();

//        //建立系统音乐选择
//        initSystemMusicAdapter();
//
//
//        adapter = new SelectClockSystemMusicAdapter(mContext, listDatas, clockMusicId, this);
//        listView.setAdapter(adapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(mContext, "日出点击" + position, Toast.LENGTH_LONG).show();
//            }
//        });
    }

    private void initSystemMusicAdapter() {
        listDatas = new ArrayList<>();
        AddClockUtils addClockUtils = new AddClockUtils();
        addClockUtils.setSystemMusicName("无");
        listDatas.add(addClockUtils);
        addClockUtils = new AddClockUtils();
        addClockUtils.setSystemMusicName("日出");
        listDatas.add(addClockUtils);
        addClockUtils = new AddClockUtils();
        addClockUtils.setSystemMusicName("平缓");
        listDatas.add(addClockUtils);
        addClockUtils = new AddClockUtils();
        addClockUtils.setSystemMusicName("希望");
        listDatas.add(addClockUtils);
        addClockUtils = new AddClockUtils();
        addClockUtils.setSystemMusicName("声波");
        listDatas.add(addClockUtils);

    }

    public void show(View view) {
        parentView = view;
        showAtLocation(view, Gravity.BOTTOM, 0, screenHeight/6);//ApplicationInfo.getActionBarSize(mContext)

    }

    private void initToolbar() {
        tvCancel.setText("取消");
        tvTitle.setText("铃声");
        tvSave.setVisibility(View.VISIBLE);
        ivAddClock.setVisibility(View.GONE);
        tvSave.setText("完成");
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.toolbar_tv_edit_or_cancel) {
            if (player != null && player.isPlaying()) {
                player.stop();
                player.release();
            }
            dismiss();
        } else if (viewId == R.id.toolbar_tv_save) {
//            Log.d("tgw返回的音乐", "onClick: " + getClockMusic() + "---" + getClockMusicId());
            if (listener != null) listener.onSave(getClockMusic(), getClockMusicId());
            if (player != null && player.isPlaying()) {
                player.stop();
                player.release();
            }
            dismiss();
        } else if (viewId == R.id.ll_select_local_music) {
            //选择本地音乐
            if (player != null && player.isPlaying()) {
                player.stop();
            }
            localClockMusicPopupWindow = new SelectLocalClockMusicPopupWindow(mContext, clockMusic, clockMusicId, new SelectLocalClockMusicPopupWindow.onPopupListener() {

                @Override
                public void onSave(String clockMusic, int clockMusicId, boolean isSaveLocalMusic) {
                    setClockMusic(clockMusic);
                    setClockMusicId(clockMusicId);
                    tvMusicType.setText("本地音乐-" + clockMusic);
                    if (isSaveLocalMusic) {
                        ivSystemChecked.setImageResource(R.drawable.unselect_);
                    }
                }
            });
            localClockMusicPopupWindow.show(parentView);
        } else if (viewId == R.id.iv_system_checkbox) {
            this.clockMusic = "系统音乐";
            this.clockMusicId = 0;//系统音乐为0
            if (player != null && player.isPlaying()) {
                player.stop();
            }
            ivSystemChecked.setImageResource(R.drawable.select_);
            tvMusicType.setText("从本地文件中选择");
            player = MediaPlayer.create(mContext, R.raw.gentle);
            if (player != null) {
                player.start();
            }


        }


    }

    @Override
    public void onGetSystemMusicDatas(int position) {
//        this.clockMusicId = position;
//        for (int i = 0; i < AddClockUtils.SYSTEM_MUSIC_NAME.length; i++) {
//            if (i == clockMusicId) {
//                clockMusic = AddClockUtils.SYSTEM_MUSIC_NAME[clockMusicId];
//            }
//        }
//
//        if (player != null && player.isPlaying()) {
//            player.stop();
//        }
//        if (clockMusicId == 0){
//            Log.d("tgw系统音乐", "无音乐 ");
//        }else if (clockMusicId == 1){
//            Log.d("tgw系统音乐", "无音乐 日出");
//            player = MediaPlayer.create(mContext, R.raw.run);
//        }else if (clockMusicId == 2){
//            Log.d("tgw系统音乐", "无音乐 平缓");
//            player = MediaPlayer.create(mContext, R.raw.gentle);
//        }
//        else if (clockMusicId == 3){
//            Log.d("tgw系统音乐", "无音乐 希望");
//            player = MediaPlayer.create(mContext, R.raw.hope);
//        }
//        else if (clockMusicId == 4){
//            Log.d("tgw系统音乐", "无音乐 光波");
//            player = MediaPlayer.create(mContext, R.raw.light);
//        }
//        if (player != null) {
//            player.start();
//        }
//        adapter = new SelectClockSystemMusicAdapter(mContext, listDatas, clockMusicId, this);
//        listView.setAdapter(adapter);
//        listView.setSelection(position);
//        adapter.notifyDataSetChanged();
//        tvMusicType.setText("从本地文件中选择");
    }


    public interface onPopupListener {
        //保存设置的周期
        void onSave(String clockMusic, int clockMusicId);

        //废弃该方法
        void onSelectLocalMusic();
    }

    public String getClockMusic() {
        return clockMusic;
    }

    public void setClockMusic(String clockMusic) {
        this.clockMusic = clockMusic;
    }

    public int getClockMusicId() {
        return clockMusicId;
    }

    public void setClockMusicId(int clockMusicId) {
        this.clockMusicId = clockMusicId;
    }
}
