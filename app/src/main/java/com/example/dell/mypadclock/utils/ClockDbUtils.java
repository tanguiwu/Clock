package com.example.dell.mypadclock.utils;


/**
 * 数据库名称参数
 */
public class ClockDbUtils {

    //保存创建了的数据item --pisition 根据此值索引删除数据库内容
    public static int CLOCK_POSITION = 0;
    //当前的详情状态
    public static final String CLOCK_ID = "id";
    public static final String CLOCK_TIME = "clockTime";
    public static final String CLOCK_PERIOD = "clockPeriod";
    public static final String CLOCK_IS_AM= "isAm";
    public static final String CLOCK_TAG = "clockTag";
    public static final String CLOCK_MUSIC = "musicAddress";
    public static final String CLOCK_MUSIC_ID = "musicId";
    public static final String CLOCK_REMAIN = "isRemain";
    public static final String CLOCK_IS_OPEN = "isOpen";



    //版本
    public static final int MY_CLOCK_DB_VERSION = 1;
    //库名
    public static final String MY_CLOCK_DB_NAME = "my_clock_db";
    //表名
    public static final String CLOCK_TABLE_NAME = "clock_name";

    //建表
    public final static String CREAT_CLOCK_DB = "create table " + CLOCK_TABLE_NAME + "(" +
            CLOCK_ID + " integer primary key autoincrement," +
            CLOCK_TIME + " varchar(64)," +
            CLOCK_PERIOD + " varchar(64)," +
            CLOCK_IS_AM + " varchar(64)," +
            CLOCK_TAG + " varchar(64)," +
            CLOCK_MUSIC + " varchar(64)," +
            CLOCK_MUSIC_ID + " int(64)," +
            CLOCK_REMAIN + " int(4)," +
            CLOCK_IS_OPEN + " int(4))";


}
