package com.example.dell.mypadclock.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.dell.mypadclock.data.ClockParameterData;
import com.example.dell.mypadclock.data.MyClockSQLiteOpenHelper;

import java.util.ArrayList;

/**
 * 闹钟数据库操作
 *
 * */
public class ClockDbHelper {

    public  static MyClockSQLiteOpenHelper dbHelper;




        //查询闹钟
    public static ArrayList<ClockParameterData> getClockDatas(MyClockSQLiteOpenHelper dbHelper,ArrayList<ClockParameterData> listDatas) {


        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        ArrayList<ClockParameterData> listDatas1 = new ArrayList<>();
        Cursor cursor = db.query(ClockDbUtils.CLOCK_TABLE_NAME, null, null, null, null, null, null);

        ClockParameterData myClockData;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(ClockDbUtils.CLOCK_ID));
            String clockTime = cursor.getString(cursor.getColumnIndex(ClockDbUtils.CLOCK_TIME));
            String clockPeriod = cursor.getString(cursor.getColumnIndex(ClockDbUtils.CLOCK_PERIOD));
            String isAm = cursor.getString(cursor.getColumnIndex(ClockDbUtils.CLOCK_IS_AM));
            String clockTag = cursor.getString(cursor.getColumnIndex(ClockDbUtils.CLOCK_TAG));
            String clockMusic = cursor.getString(cursor.getColumnIndex(ClockDbUtils.CLOCK_MUSIC));
            int clovkMusicId = cursor.getInt(cursor.getColumnIndex(ClockDbUtils.CLOCK_MUSIC_ID));
            int clockRemain = cursor.getInt(cursor.getColumnIndex(ClockDbUtils.CLOCK_REMAIN));
            int clockIsOpen = cursor.getInt(cursor.getColumnIndex(ClockDbUtils.CLOCK_IS_OPEN));

            myClockData = new ClockParameterData(id, clockTime, clockPeriod,isAm, clockTag, clockMusic, clovkMusicId, clockRemain, clockIsOpen);
            listDatas.add(myClockData);
        }
        return listDatas;
    }

    //查询闹钟
    public static ClockParameterData getClockRowDatas(Integer id, MyClockSQLiteOpenHelper dbHelper) {


        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ClockParameterData clockParameterData = new ClockParameterData();
        Cursor cursor = db.query(ClockDbUtils.CLOCK_TABLE_NAME, null, "id like ?", new String[]{id.toString()}, null, null, null);


        while (cursor.moveToNext()) {
            int clockId = cursor.getInt(cursor.getColumnIndex(ClockDbUtils.CLOCK_ID));
            String clockTime = cursor.getString(cursor.getColumnIndex(ClockDbUtils.CLOCK_TIME));
            String clockPeriod= cursor.getString(cursor.getColumnIndex(ClockDbUtils.CLOCK_PERIOD));
            String isAm= cursor.getString(cursor.getColumnIndex(ClockDbUtils.CLOCK_IS_AM));
            String clockTag = cursor.getString(cursor.getColumnIndex(ClockDbUtils.CLOCK_TAG));
            String clockMusic = cursor.getString(cursor.getColumnIndex(ClockDbUtils.CLOCK_MUSIC));
            int clovkMusicId = cursor.getInt(cursor.getColumnIndex(ClockDbUtils.CLOCK_MUSIC_ID));
            int clockRemain = cursor.getInt(cursor.getColumnIndex(ClockDbUtils.CLOCK_REMAIN));
            int clockIsOpen = cursor.getInt(cursor.getColumnIndex(ClockDbUtils.CLOCK_IS_OPEN));

            clockParameterData = new ClockParameterData(clockId, clockTime, clockPeriod,isAm, clockTag, clockMusic, clovkMusicId, clockRemain, clockIsOpen);

        }
        return clockParameterData;
    }


    //更新 闹钟
    public static void updateClock(ClockParameterData clockParameterData, MyClockSQLiteOpenHelper dbOpenHelper) {


        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

        int id = clockParameterData.getId();

        String clockTime = clockParameterData.getClockTime();
        String clockPeriod = clockParameterData.getClockPeriod();
        String isAm = clockParameterData.getIsAm();
        String clockTag = clockParameterData.getClockTag();
        String clockMusic = clockParameterData.getClockMusic();
        int clockMusicId = clockParameterData.getClockMusicId();
        int clockRemain = clockParameterData.getClockRemain();
        int clockIsOpen = clockParameterData.getClockIsOpen();
        ContentValues values = new ContentValues();

        values.put(ClockDbUtils.CLOCK_ID, id);
        values.put(ClockDbUtils.CLOCK_TIME, clockTime);
        values.put(ClockDbUtils.CLOCK_PERIOD, clockPeriod);
        values.put(ClockDbUtils.CLOCK_IS_AM, isAm);
        values.put(ClockDbUtils.CLOCK_TAG, clockTag);
        values.put(ClockDbUtils.CLOCK_MUSIC, clockMusic);
        values.put(ClockDbUtils.CLOCK_MUSIC_ID, clockMusicId);
        values.put(ClockDbUtils.CLOCK_REMAIN, clockRemain);
        values.put(ClockDbUtils.CLOCK_IS_OPEN, clockIsOpen);


        db.update(ClockDbUtils.CLOCK_TABLE_NAME, values, "id=?", new String[]{id + ""});
    }

    //添加 闹钟
    public static void addClock(ClockParameterData clockParameterData, MyClockSQLiteOpenHelper dbOpenHelper) {


        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();


        String clockTime = clockParameterData.getClockTime();
        String clockRepeatDates = clockParameterData.getClockPeriod();
        String isAm = clockParameterData.getIsAm();
        String clockTag = clockParameterData.getClockTag();
        String clockMusic = clockParameterData.getClockMusic();
        int clockMusicId = clockParameterData.getClockMusicId();
        int clockRemain = clockParameterData.getClockRemain();
        int clockIsOpen = clockParameterData.getClockIsOpen();
        ContentValues values = new ContentValues();

//        values.put(ClockDbUtils.CLOCK_ID, );
        values.put(ClockDbUtils.CLOCK_TIME, clockTime);
        values.put(ClockDbUtils.CLOCK_PERIOD, clockRepeatDates);
        values.put(ClockDbUtils.CLOCK_IS_AM, isAm);
        values.put(ClockDbUtils.CLOCK_TAG, clockTag);
        values.put(ClockDbUtils.CLOCK_MUSIC, clockMusic);
        values.put(ClockDbUtils.CLOCK_MUSIC_ID, clockMusicId);
        values.put(ClockDbUtils.CLOCK_REMAIN, clockRemain);
        values.put(ClockDbUtils.CLOCK_IS_OPEN, clockIsOpen);
        db.insert(ClockDbUtils.CLOCK_TABLE_NAME, null, values);
    }


    //删除某一条数据
    public static boolean deleteClock(Integer clockId, MyClockSQLiteOpenHelper dbOpenHelper) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

        db.delete(ClockDbUtils.CLOCK_TABLE_NAME, "id=?", new String[]{clockId.toString()});
        return true;
    }


    //更新某一条只更新闹钟的当前状态（开启或关闭）
    public static boolean updateClock(Integer id, boolean isOpen, MyClockSQLiteOpenHelper dbOpenHelper) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        if (isOpen) {
            values.put(ClockDbUtils.CLOCK_IS_OPEN, 1);
        } else {
            values.put(ClockDbUtils.CLOCK_IS_OPEN, 0);
        }
        db.update(ClockDbUtils.CLOCK_TABLE_NAME, values, "id=?", new String[]{id.toString()});
        return true;
    }


    //查询闹钟是否存在
    public static boolean isExitClock(Integer id, MyClockSQLiteOpenHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ClockDbUtils.CLOCK_TABLE_NAME, null, "id like ?", new String[]{id.toString()}, null, null, null);
        return cursor.moveToNext();
    }


//    //发送广播断地循环判断闹钟能否开启
//    public static void setAlarmTime(Context context, long timeInMillis, String action, int time) {
//        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(action);
//        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        int interval = time;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //参数2是开始时间、参数3是允许系统延迟的时间
//            am.setWindow(AlarmManager.RTC, timeInMillis, interval, pi);
//        } else {
//            am.setRepeating(AlarmManager.RTC, timeInMillis, interval, pi);
//        }
//    }
}
