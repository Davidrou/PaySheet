package com.my.paysheet.utils;

import android.widget.Toast;

import com.my.paysheet.MyApplication;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

public class Utils {
    public static String generateUUID() {
        String strUuid = "";
        Random rd = new Random(new Date().getTime());
        for (int i = 0; i < 32; i++) {
            char nibble = (char) (rd.nextInt() % 16);
            strUuid += (char) ((nibble < 10) ? ('0' + nibble) : ('a' + (nibble - 10)));
            if (i == 7 || i == 11 || i == 15 || i == 19) {
                strUuid += "-";
            }
        }
        return strUuid;
    }

    /**
     * @param str string
     * @return whether the string is empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty() || str.equals("null");
    }

    /**
     * @param collection collection
     * @return whether the collection is empty
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static void showToast(String msg) {
        Toast.makeText(MyApplication.mApplication, msg, Toast.LENGTH_SHORT).show();
    }


    public static String timedate(String fromat, long time) {
        SimpleDateFormat sdr = new SimpleDateFormat(fromat);
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        String times = sdr.format(new Date(lcc));
        return times;

    }

}
