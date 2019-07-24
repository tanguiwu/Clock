package com.example.dell.mypadclock.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dell.mypadclock.utils.ClockDbUtils;

/**
 * 数据库
 */
public class MyClockSQLiteOpenHelper extends SQLiteOpenHelper {


    /**
     * 参数说明context：上下文对象
     * name：数据库名称
     * param：一个可选的游标工厂（通常是 Null）
     * version：当前数据库的版本，值必须是整数并且是递增的状态
     * 必须通过super调用父类的构造函数
     */

    public MyClockSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                                   int version) {
        super(context, name, factory, version);
    }


    /**
     * 创建数据库1张表
     * 通过execSQL（）执行SQL语句（此处创建了1个名为person的表）
     * 注：数据库实际上是没被创建 / 打开的（因该方法还没调用）
     * 直到getWritableDatabase() / getReadableDatabase() 第一次被调用时才会进行创建 / 打开
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ClockDbUtils.CREAT_CLOCK_DB);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
