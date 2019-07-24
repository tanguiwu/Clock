package com.example.dell.mypadclock.utils;

public class AddClockUtils {

    private int viewType;
    private String detailsKinds;//参数种类，1.重复，标签，铃声
    private String systemMusicName;//参数种类，1.重复，标签，铃声

    public static final int SET_CLOCK_PERIOD = 1;//设置闹钟周期天数
    public static final int SET_CLOCK_TAG = 2;//设置闹钟的标签
    public static final int SET_CLOCK_MUSIC = 3;//设置闹钟音乐
    public static final int SET_CLOCK_REMIND = 4;//是否稍后提醒
    public static final String[] PERIOD_SELECT = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    public static final String[] VEERY_PERIOD_SELECT = {"每周日", "每周一", "每周二", "每周三", "每周四", "每周五", "每周六"};
    public static final String[] SYSTEM_MUSIC_NAME = {"无","日出","平缓","希望","光波"};

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getDetailsKinds() {
        return detailsKinds;
    }

    public void setDetailsKinds(String detailsKinds) {
        this.detailsKinds = detailsKinds;
    }

    public String getSystemMusicName() {
        return systemMusicName;
    }

    public void setSystemMusicName(String systemMusicName) {
        this.systemMusicName = systemMusicName;
    }
}
