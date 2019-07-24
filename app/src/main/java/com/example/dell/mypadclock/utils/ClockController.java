package com.example.dell.mypadclock.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;

import com.example.dell.mypadclock.ClockPlayReceiver;
import com.example.dell.mypadclock.data.MusicDatas;
import com.example.dell.mypadclock.data.MyClockSQLiteOpenHelper;

import java.util.LinkedHashMap;

import static android.content.Context.ALARM_SERVICE;

/**
 * 对闹钟数据操作
 */
public class ClockController {


    public static final int MAIN_CLOCK_REQUEST_CODE_1000 = 1000;//进行闹钟参数设置 发送码
    public static final int CLOCK_DETAILS_RESULT_CODE_1001 = 1001;//闹钟参数设置完成保存返回码
//    public static final int CLOCK_DETAILS_REQUEST_CODE_1002 = 1002;//进入铃声发送码
//
//    public static final int CLOCK_REPEAT_DETAILS_RESULT_CODE_1003 = 1003;//重复天数返回码
//
//    public static final int CLOCK_MUSIC_DETAILS_RESULT_CODE_1004 = 1004;//选择音乐类型（文件，在线，系统）返回码
//    public static final int CLOCK_LOCAL_MUSIC_DETAILS_REQUEST_CODE_1002 = 1002;//本地音乐发送码
//
//    public static final int CLOCK_LOCAL_MUSIC_SELECT_REQUEST_CODE_1003 = 1003;//本地音乐返回码

    //点击的是 添加闹钟 1，更新原有闹钟 0
    public static final int CLOCK_ADD = 1;
    public static final int CLOCK_UPDATE = 0;

    //权限码
    public static final int REQUEST = 0;
    public static final int DIALOG_REQUEST = 10;


    public static MediaPlayer PLAYER =null;
    public static LinkedHashMap<Integer, MediaPlayer> listMediaPlay = new LinkedHashMap<>();//不同的服务存储不同的音乐播放
    public static LinkedHashMap<Integer, Integer> listIsOpen = new LinkedHashMap<>();//用于判断哪个服务的闹钟是关闭的，0关 1开
    public static int PENDING_INTENT_ID = 0; //获取toogleButton是否开启  0 关，1 开
    public static int PENDING_INTENT_REQUEST_ID = 0;//Service开启码,用于关闭判断

    public  static Vibrator vibrator;//震动
    public static boolean isEditClock = false;




    //获取手机中的所有音乐
    public static LinkedHashMap<Integer, MusicDatas> getAllMusicFile(Context context) {
        LinkedHashMap<Integer, MusicDatas> map = new LinkedHashMap<>();

        ContentResolver mContentResolver;
        mContentResolver = context.getContentResolver();
        Cursor c = null;
        try {
            c = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

            MusicDatas musicDatas = null;
            while (c.moveToNext()) {


                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));// 路径
                int musicId = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));// 歌曲的id
                String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)); // 歌曲名
                String album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)); // 专辑
                String artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)); // 作者
                long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));// 大小
                int duration = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));// 时长

                musicDatas = new MusicDatas(path, musicId, name, album, artist, size, duration);
                map.put(musicId, musicDatas);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return map;
    }


    public static void isClockTurnedOn(Context context, int requestCode, int musicId, String clockTimes, int isOpen,String clockRepeatDates,String clockTag) {

        Intent intent = new Intent(context, ClockPlayReceiver.class);
        intent.setAction("MY_CLOCK_RECEIVER");
        intent.putExtra("requestCode", requestCode);
        intent.putExtra("clockMusicId", musicId);
        intent.putExtra("clockTimes", clockTimes);
        intent.putExtra("isOpen", isOpen);
        intent.putExtra("clockRepeatDates",clockRepeatDates);
        intent.putExtra("clockTag",clockTag);
        //创建PendingIntent对象封装Intent，由于是使用广播，注意使用getBroadcast方法
        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        //获取AlarmManager对象
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        //设置闹钟从当前时间开始，每隔1分钟执行一次PendingIntent对象，注意第一个参数与第二个参数的关系
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //参数2是开始时间、参数3是允许系统延迟的时间
            Log.d("tgw19", "onTimeSet: sdk大于19" + requestCode);
            am.setWindow(AlarmManager.RTC, System.currentTimeMillis(), 10, pi);
        } else {
            am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 100, pi);
        }
    }


    //计算时间轴 与当前时间差，填充，闹钟剩余时间
    public static int[] calculationStartTime(int hour, int min,int currentHour,int currebtMin) {

        int time[] = new int[2];

        //离闹钟开始的剩余时间
        int overHour=0;
        int overMin = 0;
        if ( hour > currentHour){
            overHour =    hour - currentHour;
            if(min>=currebtMin){
                overMin  = min - currebtMin;
            }else {
                overHour -- ;
                overMin = min+60 - currebtMin;
            }
        }else if ( hour == currentHour) {
            overHour =    hour - currentHour;
            if(min>=currebtMin){
                overMin  = min - currebtMin;
            }else {
                overHour = 23;
                overMin = 60-(currebtMin-min);
            }
        }else {
            overHour =  24 - currentHour + hour;
            if(min>=currebtMin){
                overMin  =  min - currebtMin;
            }else {
                overHour -- ;
                overMin = min+60 - currebtMin;
            }
        }


        time[0] = overHour;
        time[1] = overMin;
//        if (overHour == 0){
//            tvWhenStartClock.setText("还剩余"+overMin+"分钟");
//        }else if (overMin == 0){
//            tvWhenStartClock.setText("还剩余"+overHour+"小时");
//        }else {
//            tvWhenStartClock.setText("还剩余" + overHour + "小时" + overMin + "分钟");
//        }
        return time;
    }
}
