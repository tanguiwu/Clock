package com.example.dell.mypadclock;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.dell.mypadclock.adapter.AddClockParameterListAdapter;
import com.example.dell.mypadclock.popu.SelectClockMusicTypePopupWindow;
import com.example.dell.mypadclock.popu.SelectClockPeriodPopupWindow;
import com.example.dell.mypadclock.data.ClockParameterData;
import com.example.dell.mypadclock.data.MusicDatas;
import com.example.dell.mypadclock.utils.AddClockUtils;
import com.example.dell.mypadclock.utils.ClockController;
import com.example.dell.mypadclock.utils.ClockDbHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 闹钟添加编辑列表
 */
public class AddClockActivity extends AppCompatActivity implements View.OnClickListener, AddClockParameterListAdapter.SetClockDetailsLister {


    private LinearLayout rootView;
    private TextView tvCancel, tvTitle, tvSave, tvWhenStartClock;
    private ImageView ivAddClock;
    private ListView listView;
    //标签的内容
    private String tagContent;
    private EditText editTag;
    private ClockParameterData clockParameterData;
    private ArrayList<AddClockUtils> listDatas;
    private String clockPeriod;
    private String clockTime;
    private AddClockParameterListAdapter adapter;
    private int clockUpdateOrAdd;
    private int musicId;
    private String musicAddress;
    private String musicName;
    private LinkedHashMap<Integer, MusicDatas> allMusic;
    private MusicDatas musicDatas;
    private SelectClockPeriodPopupWindow periodPopupWindow;
    private SelectClockMusicTypePopupWindow musicTypePopupWindow;

    private TimePicker timePicker;
    //svnadd获取年,获取月份，0表示1月份,获取当前天数,获取本月最小天数,获取本月最大天数,获取当前小时,获取当前分钟,获取当前秒,
    int year, month, day, first, last, hour, min, xx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_clock_main);



        rootView = findViewById(R.id.root_view);
        tvCancel = findViewById(R.id.toolbar_tv_edit_or_cancel);
        tvTitle = findViewById(R.id.toolbar_title);
        tvSave = findViewById(R.id.toolbar_tv_save);
        ivAddClock = findViewById(R.id.toolbar_iv_add_clock);
        tvWhenStartClock = findViewById(R.id.tv_when_start_clock);
        listView = findViewById(R.id.list_view);
        timePicker = findViewById(R.id.time_picker);
        //建立Toolbar
        initToolbar();

        tvCancel.setOnClickListener(this);
        tvSave.setOnClickListener(this);


        //点击的是闹钟的item方式设置闹钟参数
        Intent intent = getIntent();
        clockParameterData = new ClockParameterData();
        clockParameterData = intent.getParcelableExtra("clockParameterData");
        clockUpdateOrAdd = intent.getIntExtra("clockUpdateOrAdd", -1);
        clockPeriod = clockParameterData.getClockPeriod();

        //时间选择器控件 设置其显示方式为24小时（如果参数为false则分为AM与PM显示）
        timePicker.setIs24HourView(true);
        //自动获取焦点
        timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);


        //首次添加闹钟设置默认数据
        initDefaultData();

        //添加布局
//        listView.setDivider(null);
        initAdapterData();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //选择建立哪种闹钟的参数
                initClockParameter(position);
            }
        });

    }

    private void initAdapterData() {
        listDatas = new ArrayList<>();
        //周期
        AddClockUtils addClockUtils = new AddClockUtils();
        addClockUtils.setViewType(AddClockUtils.SET_CLOCK_PERIOD);
        addClockUtils.setDetailsKinds("重复");
        listDatas.add(addClockUtils);


        addClockUtils = new AddClockUtils();
        addClockUtils.setViewType(AddClockUtils.SET_CLOCK_TAG);
        addClockUtils.setDetailsKinds("标签");
        listDatas.add(addClockUtils);

        addClockUtils = new AddClockUtils();
        addClockUtils.setViewType(AddClockUtils.SET_CLOCK_MUSIC);
        addClockUtils.setDetailsKinds("铃声");
        listDatas.add(addClockUtils);

        addClockUtils = new AddClockUtils();
        addClockUtils.setViewType(AddClockUtils.SET_CLOCK_REMIND);
        addClockUtils.setDetailsKinds("稍后提醒");
        listDatas.add(addClockUtils);

        adapter = new AddClockParameterListAdapter(AddClockActivity.this, listDatas, clockParameterData, this);
        listView.setAdapter(adapter);
    }

    private void initToolbar() {
        tvCancel.setText("取消");
        tvTitle.setText("修改闹钟");
        tvSave.setVisibility(View.VISIBLE);
        ivAddClock.setVisibility(View.GONE);
    }

    //首次添加闹钟的话
    private void initDefaultData() {

        if (clockPeriod == null) {
            clockPeriod = "true,true,true,true,true,true,true";
            clockParameterData.setClockPeriod(clockPeriod);
        }
        musicDatas = new MusicDatas();
        if (clockParameterData.getClockMusic() == null) {
            allMusic = new LinkedHashMap<>();
            allMusic = ClockController.getAllMusicFile(AddClockActivity.this);
            boolean first = true;
            for (Map.Entry<Integer, MusicDatas> entry : allMusic.entrySet()) {
                //Map.entry<Integer,String> 映射项（键-值对）  有几个方法：用上面的名字entry
                musicId = entry.getKey();
                musicDatas = entry.getValue();

                if (first) {
                    clockParameterData.setClockMusic(musicDatas.getName());
                    clockParameterData.setClockMusicId(musicId);
                    first = false;
                }
            }

        }
        if (clockParameterData.getClockTag() == null) {
            clockParameterData.setClockTag("闹钟");
        }
        clockTime = clockParameterData.getClockTime();


        //设置初始值（即00:00）
        //获取默认选中的日期的年月日星期的值，并赋值
        Calendar c = Calendar.getInstance();//日历对象
        c.setTime(new Date());//设置当前日期
        year = c.get(Calendar.YEAR);    //获取年
        month = c.get(Calendar.MONTH) + 1;   //获取月份，0表示1月份
        day = c.get(Calendar.DAY_OF_MONTH);    //获取当前天数
        first = c.getActualMinimum(c.DAY_OF_MONTH);    //获取本月最小天数
        last = c.getActualMaximum(c.DAY_OF_MONTH);    //获取本月最大天数


        xx = c.get(Calendar.SECOND);          //获取当前秒
        if (clockTime != null) {
            String time[] = clockTime.split(",");
            hour = Integer.parseInt(time[0]);
            min = Integer.parseInt(time[1]);
        } else {
            hour = c.get(Calendar.HOUR_OF_DAY);       //获取当前小时
            min = c.get(Calendar.MINUTE);          //获取当前分钟
        }
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(min);

        //刚进去时，离闹钟开始的剩余时间,
        int[] time = ClockController.calculationStartTime(hour, min, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        if (time[0] == 0) {
            tvWhenStartClock.setText("还剩余" + time[1] + "分钟");
        } else if (time[1] == 0) {
            tvWhenStartClock.setText("还剩余" + time[0] + "小时");
        } else {
            tvWhenStartClock.setText("还剩余" + time[0] + "小时" + time[1] + "分钟");
        }

        //给小于10的分钟,小时添加0 比如 9 换为09 方便后面数据库取值
        if (min <= 9) {
            clockParameterData.setClockTime(hour + "," + "0" + min);
            if (hour <= 9) {
                clockParameterData.setClockTime("0" + hour + "," + "0" + min);
            }
        } else {
            clockParameterData.setClockTime(hour + "," + min);
            if (hour <= 9) {
                clockParameterData.setClockTime("0" + hour + "," + min);
            }

        }


        //通过OnTimeChangedListener来得到当前的时与分
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                final int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);       //获取当前小时
                final int currebtMin = Calendar.getInstance().get(Calendar.MINUTE);//获取当前分钟

                hour = hourOfDay;
                min = minute;
                //设置时间
                if (min <= 9) {
                    clockParameterData.setClockTime(hour + "," + "0" + min);
                    if (hour <= 9) {
                        clockParameterData.setClockTime("0" + hour + "," + "0" + min);
                    }
                } else {
                    clockParameterData.setClockTime(hour + "," + min);
                    if (hour <= 9) {
                        clockParameterData.setClockTime("0" + hour + "," + min);
                    }
                }
                //离闹钟开始的剩余时间
                int[] time = ClockController.calculationStartTime(hour, min, currentHour, currebtMin);
                if (time[0] == 0) {
                    tvWhenStartClock.setText("还剩余" + time[1] + "分钟");
                } else if (time[1] == 0) {
                    tvWhenStartClock.setText("还剩余" + time[0] + "小时");
                } else {
                    tvWhenStartClock.setText("还剩余" + time[0] + "小时" + time[1] + "分钟");
                }
            }
        });


    }


    private void initClockParameter(int position) {
        switch (position) {
            //周期
            case 0:
                periodPopupWindow = new SelectClockPeriodPopupWindow(AddClockActivity.this, clockPeriod,
                        new SelectClockPeriodPopupWindow.onPopupListener() {

                            @Override
                            public void onSave(String[] clockPeriods) {
                                clockPeriod = "";
                                for (int i = 0; i < clockPeriods.length; i++) {
                                    clockPeriod = clockPeriod + clockPeriods[i] + ",";
                                }
                                clockPeriod = clockPeriod.substring(0, clockPeriod.length() - 1);
                                clockParameterData.setClockPeriod(clockPeriod);
                                adapter.notifyDataSetChanged();
                            }

                        });
                periodPopupWindow.show(rootView);
                break;
            //标签
            case 1:
                clockParameterData.setClockTag(tagContent);
                adapter.notifyDataSetChanged();
                break;
            //铃声
            case 2:
                musicTypePopupWindow = new SelectClockMusicTypePopupWindow(AddClockActivity.this,
                        clockParameterData.getClockMusic(), clockParameterData.getClockMusicId(),
                        new SelectClockMusicTypePopupWindow.onPopupListener() {

                            @Override
                            public void onSave(String clockMusic, int clockMusicId) {
                                clockParameterData.setClockMusic(clockMusic);
                                clockParameterData.setClockMusicId(clockMusicId);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onSelectLocalMusic() {
                                //废弃该方法
                            }
                        });
                musicTypePopupWindow.show(rootView);
                break;
            //稍后提醒
            case 3:

                break;
        }
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            //取消
            case R.id.toolbar_tv_edit_or_cancel:
                finish();
                break;
            //保存闹钟参数
            case R.id.toolbar_tv_save:
                insertDataToDb();
                break;
        }

    }

    @Override
    public void onGetClockRemain(boolean isChecked) {
        //如果true 设置稍后提醒
        if (isChecked) {
            clockParameterData.setClockRemain(1);
        } else {
            clockParameterData.setClockRemain(0);
        }
    }

    @Override
    public void onGetTagContent(String tagContent) {


        this.tagContent = tagContent;
        if (tagContent.equals("")) {
            tagContent = "无";
        }
        clockParameterData.setClockTag(tagContent);
        adapter.notifyDataSetChanged();
        //隐藏软键盘
        View view = AddClockActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) AddClockActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null)
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //
    private void insertDataToDb() {

        //默认设置完成闹钟开启
        clockParameterData.setClockIsOpen(1);

        //判断参数是否设置好
        String status = null;
        if (clockParameterData.getClockTime() == null) {
            status = "日期";
        } else if (clockParameterData.getClockPeriod() == null) {
            status = "重复";
        } else if (clockParameterData.getClockTag() == null) {
            status = "标签";
        } else if (clockParameterData.getClockMusic() == null) {
            status = "铃声";
        }

        //判断参数是否已经设置完成
        if (status != null) {
            Toast.makeText(AddClockActivity.this, "请先设置" + status, Toast.LENGTH_LONG).show();

        } else {

            //判断是新建的闹钟，还是在原有的上面修改
            if (clockUpdateOrAdd == ClockController.CLOCK_ADD) {
                ClockDbHelper.addClock(clockParameterData, ClockDbHelper.dbHelper);

            } else if (clockUpdateOrAdd == ClockController.CLOCK_UPDATE) {
                ClockDbHelper.updateClock(clockParameterData, ClockDbHelper.dbHelper);

            }
            setResult(ClockController.CLOCK_DETAILS_RESULT_CODE_1001);
            finish();
        }

    }

}