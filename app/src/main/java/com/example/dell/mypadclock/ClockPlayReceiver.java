package com.example.dell.mypadclock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.dell.mypadclock.data.MusicDatas;
import com.example.dell.mypadclock.utils.AddClockUtils;
import com.example.dell.mypadclock.utils.ClockController;
import com.example.dell.mypadclock.utils.ClockDbHelper;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.dell.mypadclock.utils.ClockController.listMediaPlay;

public class ClockPlayReceiver extends BroadcastReceiver {


    private int clockId;
    private int requestCode;
    private int musicId;
    private int isOpen;
    private String clockRepeatDates;//重复天数
    private boolean isOpenRepeat;//用于判断哪天可以重复
    private String clockTag;
    private String musicAddress = null;

    private String clockTimes;
    private int hour, minmute, se, week;
    private AlertDialog.Builder normalDialog;
    private boolean isRequest = false;
    private MediaPlayer player;


    @Override
    public void onReceive(Context context, Intent intent) {

        requestCode = intent.getIntExtra("requestCode", 0);
        musicId = intent.getIntExtra("clockMusicId", 0);
        clockTimes = intent.getStringExtra("clockTimes");
        isOpen = intent.getIntExtra("isOpen", 0);
        clockRepeatDates = intent.getStringExtra("clockRepeatDates");
        clockTag = intent.getStringExtra("clockTag");
//        Bundle bundle = new Bundle();
//        bundle = intent.getBundleExtra("bundle");
//        player = bundle.getParcelable("mediaPlay");
        String[] repeats = clockRepeatDates.split(",");
        String[] times = clockTimes.split(",");

        hour = Integer.parseInt(times[0]);
        minmute = Integer.parseInt(times[1]);
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);       //获取当前小时
        int currentMinute = calendar.get(Calendar.MINUTE);
        int currentSe = calendar.get(Calendar.SECOND);
        int currentWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (repeats[currentWeek - 1].equals("true")) {
            isOpenRepeat = true;
        } else {
            isOpenRepeat = false;
        }

        //判断闹钟是否存在，不存在直接关闭服务
        if (ClockDbHelper.dbHelper != null && !ClockDbHelper.isExitClock(requestCode, ClockDbHelper.dbHelper)) {

            context.stopService(intent);
            return;
        }


        LinkedHashMap<Integer, MusicDatas> allMusic = new LinkedHashMap<>();
        allMusic = ClockController.getAllMusicFile(context);
        MusicDatas musicDatas = new MusicDatas();
        for (Map.Entry<Integer, MusicDatas> entry : allMusic.entrySet()) {
            //Map.entry<Integer,String> 映射项（键-值对）  有几个方法：用上面的名字entry
            int id = entry.getKey();
            musicDatas = entry.getValue();
            if (musicId == id) {
                musicAddress = musicDatas.getPath();
                break;
            }

        }
        //系统音乐
//        if (musicAddress == null){
//            for (int i =0;i< AddClockUtils.SYSTEM_MUSIC_NAME.length;i++){
//                if (musicId == i){
//                    musicAddress =  AddClockUtils.SYSTEM_MUSIC_NAME[i];
//                }
//            }
//        }
        //系统音乐
        if (musicAddress == null) {
            musicAddress = "系统音乐";

        }
        //新的闹钟响了关闭之前正在播放的并且停止服务
        for (Map.Entry<Integer, Integer> entry : ClockController.listIsOpen.entrySet()) {
            if (requestCode == entry.getKey() && entry.getValue() == 0 && ClockController.PENDING_INTENT_ID == 0) {
                for (Map.Entry<Integer, MediaPlayer> entry1 : listMediaPlay.entrySet()) {
                    if (requestCode == entry1.getKey() && entry1.getValue() != null) {
                        entry1.getValue().stop();
                        ClockController.vibrator.cancel();
                        break;
                    }
                }
                context.stopService(intent);
                return;
            }
        }


        if (currentHour == hour && currentMinute == minmute && currentSe <= 6 && isOpenRepeat) {

            if (musicAddress != null) {

                try {
                    if (ClockController.listMediaPlay != null) {
                        //利用map存放不同的mediaplay 根据requestCode的不同控制音乐
                        for (Map.Entry<Integer, MediaPlayer> entry : listMediaPlay.entrySet()) {
                            if (requestCode == entry.getKey()) {
                                isRequest = true;
                                player = entry.getValue();
                                break;
                            }
                        }

                    }

                    if (isRequest) {
                        //如果已经存在该音频
                    } else {
                        player = new MediaPlayer();
                        listMediaPlay.put(requestCode, player);
                    }


                    for (Map.Entry<Integer, MediaPlayer> entry : listMediaPlay.entrySet()) {
                        if (requestCode != entry.getKey()) {
                            //关闭之前通过服务开启的闹钟
                            if (entry.getValue().isPlaying()) {
                                entry.getValue().stop();
                            }
                        } else {

                        }
                    }

                    //重置
                    if (player != null) {
                        if (musicId >= 0 && musicId <= 5) {//系统音乐
//                            if (musicId == 1) {
//                                player = MediaPlayer.create(context, R.raw.run);
//                            }else if (musicId == 2){
//                                Log.d("tgw服务系统音乐", "无音乐 平缓");
//                                player = MediaPlayer.create(context, R.raw.gentle);
//                            }
//                            else if (musicId == 3){
//                                Log.d("tgw系统音乐", "无音乐 希望");
//                                player = MediaPlayer.create(context, R.raw.hope);
//                            }
//                            else if (musicId == 4){
//                                Log.d("tgw系统音乐", "无音乐 光波");
//                                player = MediaPlayer.create(context, R.raw.light);
//                            }
                            if (musicId == 0) {
                                player = MediaPlayer.create(context, R.raw.gentle);
                            }
                            //再存一次系统音乐地址，因为 MediaPlayer.create(context, R.raw.light)方式创建时会创建新对象
                            listMediaPlay.put(requestCode, player);
                        } else {//本地音乐
                            player.reset();
                            player.setDataSource(musicAddress);
                            player.prepare();
                        }
                        player.start();
//                        showNormalDialog(context, player);
                        showClockActivity(context, player, clockTimes, clockTag);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } else {


            //因为setWindow只执行一次，所以要重新定义闹钟实现循环。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ClockController.isClockTurnedOn(context, requestCode, musicId, clockTimes, isOpen, clockRepeatDates, clockTag);
            }
//                }


        }
//        }
    }

    private void showClockActivity(Context context, MediaPlayer player, String clockTimes, String clockTag) {

        ClockController.vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        long[] patter = {1000, 1000};
        ClockController.vibrator.vibrate(patter, 0);//0 表示从数组哪里开始循环震动

        //启动一个闹钟响应Activity
        Intent activityIntent = new Intent(context, ClockStartActivity.class);
        //  要想在Service中启动Activity，必须设置如下标志
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ClockController.PLAYER = player;
        activityIntent.putExtra("clockTimes", clockTimes);
        activityIntent.putExtra("clockTag", clockTag);
        context.startActivity(activityIntent);
    }


    private void showNormalDialog(Context context, final MediaPlayer player) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         *
         */

        Intent activityIntent = new Intent(context, MainActivity.class);
        //  要想在Service中启动Activity，必须设置如下标志
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activityIntent);

        ClockController.vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        long[] patter = {1000, 1000};
        ClockController.vibrator.vibrate(patter, 0);//0 表示从数组哪里开始循环震动


        normalDialog = new AlertDialog.Builder(context);
        normalDialog.setIcon(R.mipmap.ic_launcher);
        normalDialog.setTitle("闹钟");
        normalDialog.setMessage("是否关闭闹钟");
        normalDialog.setPositiveButton("是",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        player.stop();
                        ClockController.PENDING_INTENT_ID = 0;
                        ClockController.vibrator.cancel();
                    }
                });
        normalDialog.setNegativeButton("否",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        Dialog dialog = normalDialog.create();

//        PermissionUtils.isGetWindowsAlert(context);
//        Log.d("tgw权限 ", PermissionUtils.isGetWindowsAlert(context) + "---");
//
//        int LAYOUT_FLAG;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        } else {
//            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
//        }
//        dialog.getWindow().setType(LAYOUT_FLAG);


        //Android 8.0 添加
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }


        dialog.show();
        Log.d("tgw 闹钟弹窗", "showNormalDialog: 闹钟弹窗svn");
    }


    public interface closeClockListener {
        void onGetToogleButtonStatus(boolean isOpen);
    }


}

