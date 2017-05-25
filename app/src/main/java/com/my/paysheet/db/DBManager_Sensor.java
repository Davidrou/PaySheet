package com.my.paysheet.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.my.paysheet.MyApplication;
import com.my.paysheet.utils.SensorNode;
import com.my.paysheet.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 管理传感器数据库的类
 */
public class DBManager_Sensor extends DBManager_Base {

    private final static String TAG = "DBManager_Sensor";

    private static DBManager_Base mInstance;

    public static DBManager_Base createDBManager() {
        if (null == mInstance) {
            mInstance = new DBManager_Sensor();
        }
        return mInstance;
    }

    public DBManager_Sensor() {
        if (null != mhmpCallbacks) {
            mhmpCallbacks.clear();
            mhmpCallbacks = null;
        }
        mhmpCallbacks = new HashMap<String, IDBCallback>();
        mDBHelper = new SQLite_Sensor(MyApplication.mApplication);
    }

    public static void release() {
        if (null != mInstance) {
            mInstance.unInit();
            mInstance = null;
        }
    }


    /**
     * 插入数据，如果ID已经存在，那么更新这条数据
     * @param node
     * @return
     */
    public synchronized int insertNode(SensorNode node) {
        Log.i(TAG, "[insertNode]");
        if (node == null) {
            Log.e(TAG, "invalid param");
            return 1;
        }

        mDBHelper.getReadableDatabase().beginTransaction();

        String tableName = SQLite_Sensor.TABLE_NAME;
        if (!isTableExist(tableName)) {
            createTable(tableName);
        }

        ContentValues values = makeCV(node);

        boolean isexist = false;
        Cursor cursor = null;
        String slelect = SQLite_Sensor.KEY_TYPE + "=?";
        String[] arg = new String[] {node.mType};

        cursor = mDBHelper.getReadableDatabase().query(
                tableName, null,
                slelect, arg, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            isexist = true;
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }

        if (isexist) {
            mDBHelper.getWritableDatabase().update(
                    tableName, values,
                    slelect, arg);
        } else {
            mDBHelper.getWritableDatabase().insert(tableName, null, values);
        }

        mDBHelper.getReadableDatabase().setTransactionSuccessful();
        mDBHelper.getReadableDatabase().endTransaction();

        return 0;

    }

    private ContentValues makeCV(SensorNode item) {
        ContentValues values = new ContentValues();
        values.put(SQLite_Sensor.KEY_ID, item.mID);
        values.put(SQLite_Sensor.KEY_IP, item.mIP);
        values.put(SQLite_Sensor.KEY_MCU, item.mMCU);
        values.put(SQLite_Sensor.KEY_GONGDIAN, item.mGongdian);
        values.put(SQLite_Sensor.KEY_WEIHU, item.mWeihu);
        values.put(SQLite_Sensor.KEY_WEIZHI, item.mWeizhi);
        values.put(SQLite_Sensor.KEY_SENSOR, item.mSensor);
        values.put(SQLite_Sensor.KEY_TYPE, item.mType);
        values.put(SQLite_Sensor.KEY_TONGXUN, item.mTongxun);
        values.put(SQLite_Sensor.KEY_ZUZHUANG, item.mZuzhuang);

        return values;
    }


    /**
     * 获取数据库中序号为index的数据
     * @param index
     * @return
     */
    public SensorNode getNode(int index) {

        SensorNode node = null;

        if (index >= 0 && null != mCursor && mCursor.moveToPosition(index)) {
            node = buildNode(mCursor);
        }

        return node;

    }

    private SensorNode buildNode(Cursor cur) {
        SensorNode tmp = null;
        if (null == cur) {
            Log.i(TAG, "[buildNode] illegal parameter");
        } else {
            try {
                int id = cur.getColumnIndexOrThrow(SQLite_Sensor.KEY_ID);
                int ip = cur.getColumnIndexOrThrow(SQLite_Sensor.KEY_IP);
                int model = cur.getColumnIndexOrThrow(SQLite_Sensor.KEY_MCU);
                int date = cur.getColumnIndexOrThrow(SQLite_Sensor.KEY_TYPE);
                int producer = cur.getColumnIndexOrThrow(SQLite_Sensor.KEY_TONGXUN);
                int manager = cur.getColumnIndexOrThrow(SQLite_Sensor.KEY_ZUZHUANG);
                int contactway = cur.getColumnIndexOrThrow(SQLite_Sensor.KEY_SENSOR);
                int gongdian = cur.getColumnIndexOrThrow(SQLite_Sensor.KEY_GONGDIAN);
                int weihu = cur.getColumnIndexOrThrow(SQLite_Sensor.KEY_WEIHU);
                int weizhi = cur.getColumnIndexOrThrow(SQLite_Sensor.KEY_WEIZHI);

                tmp = new SensorNode();
                tmp.mID = cur.getString(id);
                tmp.mIP = cur.getString(ip);
                tmp.mMCU = cur.getString(model);
                tmp.mTongxun = cur.getString(producer);
                tmp.mType = cur.getString(date);
                tmp.mZuzhuang = cur.getString(manager);
                tmp.mSensor = cur.getString(contactway);
                tmp.mGongdian = cur.getString(gongdian);
                tmp.mWeihu = cur.getString(weihu);
                tmp.mWeizhi = cur.getString(weizhi);


            } catch (IllegalArgumentException e) {

                return null;
            }
        }

        return tmp;
    }


    /**
     * 获取这个数据列表
     * @return
     */
    public ArrayList<SensorNode> getNodeList() {
        ArrayList<SensorNode> ctlist = null;
        if (isTableExist(mCurrentTableName)) {
            if (null != mCursor) {
                mCursor.close();
            }
            mCursor = mDBHelper.getReadableDatabase().query(
                    mCurrentTableName, null,
                    null, null, null, null, null);

            if (null != mCursor && mCursor.moveToFirst()) {
                ctlist = new ArrayList<SensorNode>();
                do {
                    SensorNode ct = buildNode(mCursor);
                    if (null != ct) {
                        ctlist.add(ct);
                    }
                } while (mCursor.moveToNext());
            }

        }
        return ctlist;
    }

    /**
     * 查找ID为id的数据
     * @param type
     * @return
     */
    public SensorNode getNode(String type) {
        if (Utils.isEmpty(type))
            return null;

        String tableName = getTableName();
        if (!isTableExist(tableName)) {
            return null;
        }


        SensorNode node = null;
        String select = SQLite_Sensor.KEY_TYPE + "=?";
        String arg = type;
        Cursor cursor = mDBHelper.getReadableDatabase().query(
                tableName, null,
                select,
                new String[]{arg}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            node = buildNode(cursor);
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }

        return node;
    }

    /**
     * 删除一条数据
     * @param info
     */
    public void deleteNode(SensorNode info) {

        if (null == info)
            return;
        String tableName = getTableName();
        if (!isTableExist(tableName)) {
            return;
        }

        String select = SQLite_Sensor.KEY_TYPE + "=?";
        String arg = info.mType;
        mDBHelper.getReadableDatabase().delete(tableName, select, new String[]{arg});

    }


    public void destroyTable() {
        List<String> list = mDBHelper.onQueryDBTable(mDBHelper.getReadableDatabase());
        String element = null;
        List<String> tmp = new ArrayList<String>();
        for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
            element = it.next();
            if (0 == element.compareTo("android_metadata")
                    || 0 == element.compareTo("sqlite_sequence")
                    ) {
                continue;
            } else {
                tmp.add(element);
            }
        }

        for (Iterator<String> it = tmp.iterator(); it.hasNext(); ) {
            element = it.next();
            deleteTable(element);
        }
        if (null != tmp) {
            tmp.clear();
            tmp = null;
        }
        if (null != list) {
            list.clear();
            list = null;
        }
    }


}
