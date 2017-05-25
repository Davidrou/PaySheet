package com.my.paysheet.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SQLite_Sensor extends SQLite_Base {
    public final static String TAG = "SQLite_Sensor";
    public static final String DATABASE_NAME = "sensor.db";
    public static final int DATABASE_VERSION = 1;


    public final static String TABLE_NAME = "sensor";

    public final static String KEY_TYPE = "type";
    public final static String KEY_ID = "sensorid";
    public final static String KEY_IP = "ip";
    public final static String KEY_MCU = "mcu";
    public final static String KEY_SENSOR = "sensor";
    public final static String KEY_WEIZHI = "weizhi";
    public final static String KEY_WEIHU = "weihu";
    public final static String KEY_ZUZHUANG = "zuzhuang";
    public final static String KEY_TONGXUN = "tongxun";
    public final static String KEY_GONGDIAN = "gongdian";

    private final static String TABLE_ITEMS = "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "type TEXT, " +
            "sensorid TEXT, " +
            "ip TEXT, " +
            "mcu TEXT, " +
            "sensor TEXT, " +
            "weizhi TEXT, " +
            "weihu TEXT, " +
            "zuzhuang TEXT, " +
            "tongxun TEXT, " +
            "gongdian TEXT" +
            ");";


    public SQLite_Sensor(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = getWritableDatabase();
        db.close();
    }

    public void onCreateTable(SQLiteDatabase db, String tableName) {
        Log.i(TAG, "[onCreateTable]");

        String statement = "CREATE  TABLE IF NOT EXISTS " + tableName + TABLE_ITEMS;
        db.execSQL(statement);

    }

}
