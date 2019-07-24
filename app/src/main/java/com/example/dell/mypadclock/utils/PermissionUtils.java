package com.example.dell.mypadclock.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.example.dell.mypadclock.MainActivity;

public class PermissionUtils {
    //这是要申请的权限
    private static String[] PERMISSIONS_CAMERA_AND_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     *
     * @param activity
     * @param requestCode
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int storagePermission = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            //检测是否有权限，如果没有权限，就需要申请
            if (storagePermission != PackageManager.PERMISSION_GRANTED ) {
                //申请权限
                activity.requestPermissions(PERMISSIONS_CAMERA_AND_STORAGE, requestCode);
                //返回false。说明没有授权
                return false;
            }
        }
        //说明已经授权
        return true;
    }



    public static boolean isGetWindowsAlert(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            if (!Settings.canDrawOverlays(context))
            {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
                return false;
            }
            return true;
        }
        return true;
    }


    public static void GetWindowsAlert(Activity context ,int requestCode){
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, requestCode);
            }
        }
    }

}


