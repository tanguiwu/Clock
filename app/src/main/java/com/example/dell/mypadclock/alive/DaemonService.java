package com.example.dell.mypadclock.alive;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.dell.mypadclock.MainActivity;
import com.example.dell.mypadclock.R;

/**
 * 前台Service，使用startForeground
 * 这个Service尽量要轻，不要占用过多的系统资源，否则
 * 系统在资源紧张时，照样会将其杀死
 */
public class DaemonService extends Service {
    private static final String TAG = "tgwDaemonService";
    public static final int NOTICE_ID = 100;
    private static final String CHANNEL_ONE_ID = "channel";
    private static final CharSequence CHANNEL_ONE_NAME = "channelName";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "DaemonService---->onCreate被调用，启动前台service");

        //如果API大于18，需要弹出一个可见通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME,
                    NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false);//是否显示角标
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);

            NotificationManager notificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

            Notification.Builder builder=new Notification.Builder(getApplicationContext(),CHANNEL_ONE_ID);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setAutoCancel(true);
            builder.setChannelId(CHANNEL_ONE_ID);
            builder.setWhen(System.currentTimeMillis());
            builder.setContentTitle("题目闹钟");
            builder.setContentText("内容闹钟");
            builder.setNumber(3);





            startForeground(NOTICE_ID, builder.build());
            // 如果觉得常驻通知栏体验不好
            // 可以通过启动CancelNoticeService，将通知移除，oom_adj值不变
            Intent intent = new Intent(this, MainActivity.class);
            startService(intent);
        } else {
            startForeground(NOTICE_ID, new Notification());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 如果Service被终止
        // 当资源允许情况下，重启service
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 如果Service被杀死，干掉通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mManager.cancel(NOTICE_ID);
        }
        Log.d(TAG, "DaemonService---->onDestroy，前台service被杀死");

        // 重启自己

        Intent intent = new Intent(getApplicationContext(), DaemonService.class);
        startService(intent);


    }


}