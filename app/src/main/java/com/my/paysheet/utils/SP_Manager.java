package com.my.paysheet.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.my.paysheet.MyApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;


public class SP_Manager {
    private static final String TAG = "SP_Manager";


    public static final String PREFERENCE_FILE = "com_my_paysheet_prefs";

    private static SP_Manager mInstance;


    public static SP_Manager Instance() {
        if (null == mInstance) {
            synchronized (SP_Manager.class) {
                if (null == mInstance) {
                    mInstance = new SP_Manager();
                }
            }
        }
        return mInstance;
    }

    public SP_Manager() {

    }

    public static void Release() {
        if (null != mInstance) {
            mInstance = null;
        }
    }


    public float getMoney() {
        SharedPreferences sp = MyApplication.mApplication.getSharedPreferences(PREFERENCE_FILE, 0);
        return sp.getFloat("money", 0);
    }

    public void writeMoney(float money) {

        SharedPreferences mPreferences = MyApplication.mApplication.getSharedPreferences(PREFERENCE_FILE, 0);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putFloat("money", money + getMoney());
        editor.commit();
    }

    public <T> T getObject(String key) {
        SharedPreferences sp = MyApplication.mApplication.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        if (sp.contains(key)) {
            String objectVal = sp.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void setObject(String key, Object object) {
        SharedPreferences sp = MyApplication.mApplication.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {

            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, objectVal);
            editor.commit();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
