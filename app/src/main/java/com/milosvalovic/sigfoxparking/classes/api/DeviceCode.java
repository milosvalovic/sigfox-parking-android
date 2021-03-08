package com.milosvalovic.sigfoxparking.classes.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.milosvalovic.sigfoxparking.classes.Consts;

import java.util.Random;



public class DeviceCode {
    private static final String SHARED_ID = "device_unique_id";

    public static String getCode(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Consts.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String id = sharedPreferences.getString(SHARED_ID, "");

        if (id == null || id.equals(""))
        {
            id = generateId();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SHARED_ID, id);
            editor.apply();
        }

        return id;
    }

    private static String generateId()
    {
        Random rand = new Random();
        int random = rand.nextInt(1000000);
        int random2 = rand.nextInt(1000000);
        int random3 = rand.nextInt(1000000);
        String hashString = random + "S!@Devel" + random2 + Consts.APP_TAG + random3;
        return MD5(hashString);
    }


    private static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

}
