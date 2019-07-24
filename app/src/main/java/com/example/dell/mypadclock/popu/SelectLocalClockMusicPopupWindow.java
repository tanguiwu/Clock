package com.example.dell.mypadclock.popu;


import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.dell.mypadclock.R;
import com.example.dell.mypadclock.adapter.SelectLocalClockMusicAdapter;
import com.example.dell.mypadclock.data.MusicDatas;
import com.example.dell.mypadclock.utils.ClockController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 选择本地音乐PopupWindow
 */
public class SelectLocalClockMusicPopupWindow extends PopupWindow implements View.OnClickListener,SelectLocalClockMusicAdapter.CheckCheckBoxStatusListener{


    private TextView tvCancel, tvTitle, tvSave, tvMusicType;
    private ImageView ivAddClock, ivCheckedMusic;
    private ListView listView;
    private Context mContext = null;
    private onPopupListener listener = null;
    private SelectLocalClockMusicAdapter adapter;
    private ArrayList<MusicDatas> listDatas;
    private LinkedHashMap<Integer, MusicDatas> allMusic;
    private MusicDatas musicDatas;
    private String clockMusic;
    private int clockMusicId;
    //记录哪个音乐原先已经被选择
    private int checkedPosition = 0;

    private MediaPlayer player;
    private int screenHeight;
    private int screenWidth;



    public SelectLocalClockMusicPopupWindow(Context mContext, String clockMusic, int clockMusicId, onPopupListener listener) {
        super(mContext);
        this.mContext = mContext;
        this.listener = listener;
        this.clockMusic = clockMusic;
        this.clockMusicId = clockMusicId;
        initView(mContext);
    }

    private void initView(final Context mContext) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.select_local_clock_music_main, null);
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



        tvCancel.setOnClickListener(this);
        tvSave.setOnClickListener(this);

        listView = rootView.findViewById(R.id.list_view);
        initToolbar();

        listDatas = new ArrayList<>();
        allMusic = new LinkedHashMap<>();
        allMusic = ClockController.getAllMusicFile(mContext);
        int position = 0;
        //获取点击的是哪首本地音乐，获取position
        for (Map.Entry<Integer, MusicDatas> entry : allMusic.entrySet()) {
            //Map.entry<Integer,String> 映射项（键-值对）  有几个方法：用上面的名字entry
            musicDatas = new MusicDatas();
            int id = entry.getKey();
            musicDatas = entry.getValue();
            if (clockMusicId == id) {
                checkedPosition = position;
            } else {
                position++;
            }
            listDatas.add(musicDatas);
        }

        player = new MediaPlayer();
        adapter = new SelectLocalClockMusicAdapter(mContext, listDatas, checkedPosition, this);
        listView.setSelection(checkedPosition);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }


    public void show(View view) {
        showAtLocation(view, Gravity.BOTTOM, 0, screenHeight/6);//ApplicationInfo.getActionBarSize(mContext)
//        showAsDropDown(view, 0,
//                ApplicationInfo.getActionBarSize(mContext) / 2 - ApplicationInfo.getFontHeight(16.0f) / 2);

    }

    private void initToolbar() {
        tvCancel.setText("取消");
        tvTitle.setText("本地铃声");
        tvSave.setVisibility(View.VISIBLE);
        ivAddClock.setVisibility(View.GONE);
        tvSave.setText("完成");
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.toolbar_tv_edit_or_cancel) {
            if (player.isPlaying()) {
//                Log.d("tgw释放", "onGetMusicDatas: 释放");
                player.stop();
                player.release();
            }
            dismiss();
        } else if (viewId == R.id.toolbar_tv_save) {
            allMusic.clear();
            allMusic = ClockController.getAllMusicFile(mContext);
            for (Map.Entry<Integer, MusicDatas> entry : allMusic.entrySet()) {
                //Map.entry<Integer,String> 映射项（键-值对）  有几个方法：用上面的名字entry
                int id = entry.getKey();
                musicDatas = entry.getValue();
                if (clockMusicId == id) {
                    this.clockMusic = musicDatas.getName();
//                    Log.d("tgw所有音乐2", "onCreate: " + clockMusic + "---" + clockMusicId);
                }
            }
            if (listener != null) listener.onSave(clockMusic, clockMusicId,true);
            player.stop();
            player.release();
            dismiss();
        }

    }

    @Override
    public void onGetLocalMusicDatas(int position, int musicId, String musicAddress) {
        //从新获取选择的音乐
        this.clockMusicId = musicId;
        this.checkedPosition = position;
        adapter = new SelectLocalClockMusicAdapter(mContext, listDatas, checkedPosition, this);
        listView.setAdapter(adapter);
        listView.setSelection(checkedPosition);
        adapter.notifyDataSetChanged();


        //准备播放音乐
        try {
            if (player.isPlaying()) {
                player.stop();
            }
            //重置
            player.reset();
            player.setDataSource(musicAddress);
            player.prepare();
            //播放音乐
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public interface onPopupListener {
        //保存设置的周期
        void onSave(String clockMusic, int clockMusicId,boolean isSaveLocalMusic);
    }



}
