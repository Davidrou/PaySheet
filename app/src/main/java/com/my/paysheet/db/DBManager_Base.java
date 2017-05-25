package com.my.paysheet.db;

import android.database.Cursor;
import android.util.Log;

import com.my.paysheet.utils.SensorNode;
import com.my.paysheet.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;


/**
 * 数据库管理基类
 */
public abstract class DBManager_Base {

    private static final String TAG = "DBManager_Base";
    protected HashMap<String, IDBCallback> mhmpCallbacks = null;
    protected Cursor mCursor = null;
    protected String mCurrentTableName = null;
    protected SQLite_Base mDBHelper = null;

    public String registerCallback(IDBCallback callback) {
        String key = Utils.generateUUID();
        while (mhmpCallbacks.containsKey(key)) {
            key = Utils.generateUUID();
        }
        mhmpCallbacks.put(key, callback);
        return key;
    }

    public void unRegisterCallback(String strKey) {
        mhmpCallbacks.remove(strKey);
    }

    public void setTableName(String tableName) {
        mCurrentTableName = tableName;
    }

    public String getTableName() {
        return this.mCurrentTableName;
    }

    private void releaseData() {
        if (mCursor != null) {
            if (!mCursor.isClosed())
                mCursor.close();
            mCursor = null;
        }
    }

    public void unInit() {
        if (null != mDBHelper) {
            mDBHelper.close();
            mDBHelper = null;
        }
        if (null != mhmpCallbacks) {
            mhmpCallbacks.clear();
            mhmpCallbacks = null;
        }
        releaseData();
    }

    public int insertNode(SensorNode node) {
        return 0;
    }

    public int updateNode(int index, SensorNode node) {
        return 0;
    }

    public int removeNode(int index) {
        return 0;
    }

    public int search(int searchId, String itemID, int filter) {

        int result = 0;
        boolean tableExist = CurrentTableExist();

        if (tableExist) {
            if (null != mCursor) {
                mCursor.close();
                // mDBHelper.getReadableDatabase().close();
                mCursor = null;
            }

            mCursor = mDBHelper.getReadableDatabase().query(mCurrentTableName, null, null, null, null, null, null);

            Iterator<Entry<String, IDBCallback>> it = mhmpCallbacks.entrySet().iterator();
            IDBCallback iv = null;
            if (it.hasNext()) {
                Entry<String, IDBCallback> entry = it.next();
                iv = entry.getValue();
            }
            if (null != iv) {
                if (null == mCursor) {
                    Log.i(TAG, "[search]search none row in the db  " + mCurrentTableName);
                } else if (mCursor.moveToFirst()) {
                    result = mCursor.getCount();

                    Log.i(TAG, "[search] the current table name is " + mCurrentTableName);
                    Log.i(TAG, "[search] the searched total item is " + result);

                    do {
                        iv.onSearched(searchId, mCursor.getPosition(), false);
//						KasLog.i(TAG,"the node id is"+ mCursor.getInt(mCursor.getColumnIndex("ID")));
                    } while (mCursor.moveToNext());

                    iv.onSearched(searchId, 0, true);
                } else {
                    iv.onSearched(searchId, 0, true);
                }
            }
        } else {
            Log.i(TAG, "[search] " + mCurrentTableName + "doesn't exist");
        }

        return result;

    }

    public SensorNode getNode(int index) {
        return null;
    }

    public void removeAll() {

    }

    public void createTable() {
        mDBHelper.onCreateTable(mDBHelper.openWritableDB(), mCurrentTableName);
    }

    public void createTable(String tableName) {
        mDBHelper.onCreateTable(mDBHelper.openWritableDB(), tableName);
    }

    public void deleteTable(String table) {
        try {
            mDBHelper.onDeleteTable(mDBHelper.openWritableDB(), table);
        } catch (Exception e) {
            Log.e(TAG, "deleteTable table=" + table + "  exception=" + e.toString());
        }
    }

    public int getSearchedItemCount(String tableName) {
        int total = 0;
        boolean tableExist = isTableExist(tableName);
        if (tableExist) {
            Cursor cursor = mDBHelper.getReadableDatabase().query(tableName, null, null, null, null, null, null);
            if (null != cursor) {
                total = cursor.getCount();
                cursor.close();
            }
        }

        return total;
    }

    public int getSearchedItemCount() {
        int total = 0;
        boolean tableExist = CurrentTableExist();
        if (tableExist) {
            Cursor cursor = mDBHelper.getReadableDatabase().query(mCurrentTableName, null, null, null, null, null, null);
            if (null != cursor) {
                total = cursor.getCount();
                cursor.close();
            }
        }

        return total;
    }

    public boolean CurrentTableExist() {
        boolean ret = false;
        List<String> list = mDBHelper.onQueryDBTable(mDBHelper.getReadableDatabase());
        for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
            String element = it.next();
            if (null != element && null != mCurrentTableName
                    && 0 == element.compareTo(mCurrentTableName)) {
                ret = true;
                break;
            }
        }
        if (null != list) {
            list.clear();
            list = null;
        }
        return ret;
    }

    public boolean isTableExist(String tableName) {
        boolean ret = false;
        if (tableName != null) {
            List<String> list = mDBHelper.onQueryDBTable(mDBHelper.getReadableDatabase());
            for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
                String element = it.next();
                if (null != element && null != tableName
                        && 0 == element.compareTo(tableName)) {
                    ret = true;
                    break;
                }
            }
            if (null != list) {
                list.clear();
                list = null;
            }
        }
        return ret;
    }

    public void destroyTable() {
        List<String> list = mDBHelper.onQueryDBTable(mDBHelper.getReadableDatabase());
        String element = null;
        List<String> tmp = new ArrayList<String>();
        for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
            element = it.next();
            if (0 == element.compareTo("android_metadata")
                    || 0 == element.compareTo("sqlite_sequence")) {
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