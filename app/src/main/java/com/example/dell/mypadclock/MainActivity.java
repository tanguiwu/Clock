package com.example.dell.mypadclock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.mypadclock.adapter.ClockDisPlayListAdapter;
import com.example.dell.mypadclock.alive.AliveJobService;
import com.example.dell.mypadclock.alive.DaemonService;
import com.example.dell.mypadclock.data.ClockParameterData;
import com.example.dell.mypadclock.data.MyClockSQLiteOpenHelper;
import com.example.dell.mypadclock.utils.ClockController;
import com.example.dell.mypadclock.utils.ClockDbHelper;
import com.example.dell.mypadclock.utils.ClockDbUtils;
import com.example.dell.mypadclock.utils.PermissionUtils;
import com.example.dell.mypadclock.view.EnsureDialog;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ClockDisPlayListAdapter.ClockDisPlayToogleBtIsOpen {

    private LinearLayout listViewParent;
    private TextView tvEdit, tvTitle, tvSave, tvIsExistClock;
    private ImageView ivAddClock;
    private ListView listView;
    private ClockDisPlayListAdapter adapter;
    private ArrayList<ClockParameterData> listDatas;
    private Runnable runnable;
    private Handler mainHandler;
    private AlertDialog.Builder normalDialog;
    private EnsureDialog ensureDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);



        //权限
        PermissionUtils.isGrantExternalRW(MainActivity.this, ClockController.REQUEST);
        PermissionUtils.GetWindowsAlert(MainActivity.this, ClockController.DIALOG_REQUEST);


        //开启保活服务
        Intent intentService = new Intent(this, AliveJobService.class);
        startService(intentService);
        Intent intentServiceSecond = new Intent(this, DaemonService.class);
        startService(intentServiceSecond);


        mainHandler = new Handler();

        tvEdit = findViewById(R.id.toolbar_tv_edit_or_cancel);
        tvTitle = findViewById(R.id.toolbar_title);
        tvSave = findViewById(R.id.toolbar_tv_save);
        ivAddClock = findViewById(R.id.toolbar_iv_add_clock);
        listViewParent = findViewById(R.id.list_view_parent);
        listView = findViewById(R.id.list_view);
        tvIsExistClock = findViewById(R.id.tv_exist_clock);
        //建立Toolbar
        initToolbar();
        //注册点击事件
        tvEdit.setOnClickListener(this);
        ivAddClock.setOnClickListener(this);


        // 创建SQLiteOpenHelper子类对象
        ClockDbHelper.dbHelper = new MyClockSQLiteOpenHelper(MainActivity.this, ClockDbUtils.MY_CLOCK_DB_NAME, null, ClockDbUtils.MY_CLOCK_DB_VERSION);
        //数据库实际上是没有被创建或者打开的，直到getWritableDatabase() 或者 getReadableDatabase() 方法中的一个被调用时才会进行创建或者打开
        listDatas = new ArrayList<>();
        listDatas = ClockDbHelper.getClockDatas(ClockDbHelper.dbHelper, listDatas);
        Log.d("tgw闹钟是否关闭333", "toogleBtIsOpen: " + listDatas.toString());
        adapter = new ClockDisPlayListAdapter(MainActivity.this, listDatas, this);
        listView.setAdapter(adapter);


        runnable = new Runnable() {
            @Override
            public void run() {
                updateAdapter();
                mainHandler.postDelayed(this, 30000);
            }
        };

        runnable.run();

        //杀死后重新开启，判断闹钟是否开启状态
        for (ClockParameterData clockParameterData : listDatas) {
            if (clockParameterData.getClockIsOpen() == 1) {
                ClockController.PENDING_INTENT_ID = 1;
                ClockController.isClockTurnedOn(MainActivity.this, clockParameterData.getId(), clockParameterData.getClockMusicId(), clockParameterData.getClockTime(), clockParameterData.getClockIsOpen(), clockParameterData.getClockPeriod(), clockParameterData.getClockTag());
            }
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddClockActivity.class);
                intent.putExtra("clockUpdateOrAdd", ClockController.CLOCK_UPDATE);
                intent.putExtra("clockParameterData", listDatas.get(position));
                startActivityForResult(intent, ClockController.MAIN_CLOCK_REQUEST_CODE_1000);
            }
        });


    }

    private void initToolbar() {
        tvEdit.setText("编辑");
        tvTitle.setText("闹钟");
        tvSave.setVisibility(View.GONE);
        ivAddClock.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            //编辑
            case R.id.toolbar_tv_edit_or_cancel:

                if (!ClockController.isEditClock) {
                    if (listDatas.size() == 0) {
                        Toast.makeText(MainActivity.this, "请先添加闹钟", Toast.LENGTH_LONG).show();
                    } else {
                        ClockController.isEditClock = true;
                        tvEdit.setText("取消");
                    }
                } else {
                    ClockController.isEditClock = false;
                    tvEdit.setText("编辑");
                }
                updateAdapter();
                break;
            //添加闹钟
            case R.id.toolbar_iv_add_clock:
                Intent intent = new Intent(MainActivity.this, AddClockActivity.class);
                //直接添加闹钟的话，传入默认值
                ClockParameterData clockParameterData = new ClockParameterData();
                //clock_update 判断更新闹钟，MyClockData 传递闹钟已有数据
                intent.putExtra("clockUpdateOrAdd", ClockController.CLOCK_ADD);
                intent.putExtra("clockParameterData", clockParameterData);
                startActivityForResult(intent, ClockController.MAIN_CLOCK_REQUEST_CODE_1000);
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ClockController.MAIN_CLOCK_REQUEST_CODE_1000 && resultCode == ClockController.CLOCK_DETAILS_RESULT_CODE_1001) {
            updateAdapter();
            for (int i = 0; i < listDatas.size(); i++) {
                ClockController.listIsOpen.put(i + 1, 1);
            }
            for (ClockParameterData clockParameterData : listDatas) {
                if (clockParameterData.getClockIsOpen() == 1) {
                    ClockController.PENDING_INTENT_ID = 1;
                    ClockController.isClockTurnedOn(MainActivity.this, clockParameterData.getId(), clockParameterData.getClockMusicId(), clockParameterData.getClockTime(), clockParameterData.getClockIsOpen(), clockParameterData.getClockPeriod(), clockParameterData.getClockTag());
                }
            }
        }
    }


    @Override
    public void toogleBtIsOpen(boolean isOpen, int clockId, int position) {
        ClockDbHelper.updateClock(clockId, isOpen, ClockDbHelper.dbHelper);
        updateAdapter();
        ClockParameterData clockParameterData = ClockDbHelper.getClockRowDatas(clockId, ClockDbHelper.dbHelper);
        if (isOpen) {
            //根据开启关闭toogleButton状态，停止相对应的service
//            ClockController.listIsOpen.put(clockId,1);
            ClockController.PENDING_INTENT_ID = 1;
            ClockController.PENDING_INTENT_REQUEST_ID = clockParameterData.getId();
        } else {
            ClockController.PENDING_INTENT_ID = 0;
            ClockController.listIsOpen.put(clockId, 0);

        }

        ClockController.isClockTurnedOn(MainActivity.this, clockParameterData.getId(), clockParameterData.getClockMusicId(), clockParameterData.getClockTime(), clockParameterData.getClockIsOpen(), clockParameterData.getClockPeriod(), clockParameterData.getClockTag());


    }

    @Override
    public void onIsDeletedClock(int position, int clockId) {
        isDeletedClock(clockId);
    }

    private void isDeletedClock(final int clockId) {

        normalDialog = new AlertDialog.Builder(MainActivity.this);
        normalDialog.setIcon(R.drawable.de_clock_alert);
        normalDialog.setTitle("删除");
        normalDialog.setMessage("是否删除该闹钟");
        normalDialog.setPositiveButton("是",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClockDbHelper.deleteClock(clockId, ClockDbHelper.dbHelper);
                        if (listDatas.size() == 1) {
                            listViewParent.setVisibility(View.GONE);
                            tvIsExistClock.setVisibility(View.VISIBLE);
                            tvEdit.setTextColor(Color.parseColor("#555555"));
                            ClockController.isEditClock = false;
                            tvEdit.setText("编辑");
                        } else {
                            listViewParent.setVisibility(View.VISIBLE);
                            tvIsExistClock.setVisibility(View.GONE);
                            tvEdit.setTextColor(Color.parseColor("#395dd7"));
                            ClockController.isEditClock = true;
                            tvEdit.setText("取消");
                        }
                        updateAdapter();
                    }
                });
        normalDialog.setNegativeButton("否",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        AlertDialog dialog = normalDialog.create();


        //Android 8.0 添加
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue_395dd7));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blue_395dd7));



//        ensureDialog = new EnsureDialog(MainActivity.this, R.drawable.de_clock_blue_alert,
//                "确定删除该闹钟？", "取消",
//                "确定", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ensureDialog.dismiss();
//            }
//        }, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ClockDbHelper.deleteClock(clockId, ClockDbHelper.dbHelper);
//                if (listDatas.size() == 1) {
//                    listViewParent.setVisibility(View.GONE);
//                    tvIsExistClock.setVisibility(View.VISIBLE);
//                    tvEdit.setTextColor(Color.parseColor("#555555"));
//                    ClockController.isEditClock = false;
//                    tvEdit.setText("编辑");
//                } else {
//                    listViewParent.setVisibility(View.VISIBLE);
//                    tvIsExistClock.setVisibility(View.GONE);
//                    tvEdit.setTextColor(Color.parseColor("#395dd7"));
//                    ClockController.isEditClock = true;
//                    tvEdit.setText("取消");
//                }
//                updateAdapter();
//                ensureDialog.dismiss();
//            }
//        });
//        ensureDialog.show();




    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ClockController.REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //检验是否获取权限，如果获取权限，外部存储会处于开放状态，会弹出一个toast提示获得授权
                    String sdCard = Environment.getExternalStorageState();
                    if (sdCard.equals(Environment.MEDIA_MOUNTED)) {
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "请开启存储权限", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case ClockController.DIALOG_REQUEST:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!Settings.canDrawOverlays(this)) {
                        // SYSTEM_ALERT_WINDOW permission not granted...
                        Toast.makeText(MainActivity.this, "没有弹窗权限，请开启", Toast.LENGTH_SHORT);
                    }
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置没有闹钟时的空样式
        if (listDatas.size() == 0) {
            listViewParent.setVisibility(View.GONE);
            tvIsExistClock.setVisibility(View.VISIBLE);
            tvEdit.setTextColor(Color.parseColor("#555555"));
        } else {
            listViewParent.setVisibility(View.VISIBLE);
            tvIsExistClock.setVisibility(View.GONE);
            tvEdit.setTextColor(Color.parseColor("#395dd7"));
        }
        //亮屏
        wakeUpAndUnlock(MainActivity.this);
    }

    //唤醒屏幕并解锁
    public void wakeUpAndUnlock(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "pad_clock_name:bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }


    private void updateAdapter() {

        listDatas.clear();
        listDatas = ClockDbHelper.getClockDatas(ClockDbHelper.dbHelper, listDatas);
        adapter.notifyDataSetChanged();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainHandler.removeCallbacks(runnable);
    }
}
