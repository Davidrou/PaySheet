package com.my.paysheet.db;


public interface IDBCallback {
    public void onInserted(String strKey);

    public void onRemoved(String strKey);

    public void onSearched(int iSearchId, int index, boolean bCompleted);

}