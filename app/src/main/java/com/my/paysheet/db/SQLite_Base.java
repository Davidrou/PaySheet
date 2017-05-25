package com.my.paysheet.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class SQLite_Base extends SQLiteOpenHelper {

	private final static String TAG = "SQLite_Base";
			
	public SQLite_Base(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public SQLiteDatabase openReadableDB() {
		return getReadableDatabase();
	}

	public SQLiteDatabase openWritableDB() {
		return getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	public void onCreateTable(SQLiteDatabase db, String tableNmae) {
		Log.i(TAG, "[onCreateTable]");
	
	}

	public void onDeleteTable(SQLiteDatabase db, String table) {
		Log.i(TAG, "[onDeleteTable]");
		if (null != table && 0 != table.length()) {
			String statement = "DROP TABLE IF EXISTS " + table;
			db.execSQL(statement);
		}
	}

	public List<String> onQueryDBTable(SQLiteDatabase db) {
		List<String> tmp = new ArrayList<String>();
		String[] columns = new String[1];
		columns[0] = "name";
		String selection = "type=\"table\"";

		Cursor cur = db.query("sqlite_master", columns, selection, null, null, null, null);
		if (null == cur) {
			Log.e(TAG, "[onQueryTable] fail to query the table sqlite_master");
		} else if (cur.moveToFirst()) {
			do {
				tmp.add(cur.getString(0));
			} while (cur.moveToNext());

		}
		if (null != cur)
			cur.close();

		return tmp;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
