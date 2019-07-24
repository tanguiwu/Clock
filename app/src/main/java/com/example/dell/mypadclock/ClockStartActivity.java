package com.example.dell.mypadclock;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.dell.mypadclock.utils.AddClockUtils;
import com.example.dell.mypadclock.utils.ClockController;

import java.util.Calendar;
import java.util.Date;

public class ClockStartActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvStartTime, tvCurentDay, tvCurrentWeek, tvTag, tvRemai, tvStopClock;
    private int year,month,day,week;
    private String clockTimes;
    private String  clockTag;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.clock_start_main);





        tvStartTime = findViewById(R.id.clock_start_time);
        tvCurentDay = findViewById(R.id.clock_curent_day);
        tvCurrentWeek = findViewById(R.id.clock_curent_week);
        tvTag = findViewById(R.id.clock_tag);
        tvRemai = findViewById(R.id.clock_remain);
        tvStopClock = findViewById(R.id.stop_clock);


        Calendar c = Calendar.getInstance();//日历对象
        c.setTime(new Date());//设置当前日期
        year = c.get(Calendar.YEAR);    //获取年
        month = c.get(Calendar.MONTH) + 1;   //获取月份，0表示1月份
        day = c.get(Calendar.DAY_OF_MONTH);    //获取当前天数
        week=c.get(Calendar.DAY_OF_WEEK);


        clockTimes = getIntent().getStringExtra("clockTimes");
        clockTag = getIntent().getStringExtra("clockTag");
        String times[] = clockTimes.split(",");
        String time = times[0] + ":" + times[1];
        tvStartTime.setText(time);

        tvCurentDay.setText(year+"年"+month+"月"+day+"日");
        tvCurrentWeek.setText(AddClockUtils.PERIOD_SELECT[week-1]);
        tvTag.setText(clockTag);

        tvRemai.setOnClickListener(this);
        tvStopClock.setOnClickListener(this);



        ClockController.PLAYER.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ClockController.vibrator.cancel();
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.clock_remain) {
            if (ClockController.PLAYER != null) {
                ClockController.PLAYER.stop();
            }
            ClockController.vibrator.cancel();
            finish();
        } else if (viewId == R.id.stop_clock) {
            if (ClockController.PLAYER != null) {
                ClockController.PLAYER.stop();
            }
            ClockController.PENDING_INTENT_ID = 0;
            ClockController.vibrator.cancel();
            finish();
        }
    }
}
