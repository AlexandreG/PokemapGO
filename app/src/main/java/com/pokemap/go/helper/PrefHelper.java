package com.pokemap.go.helper;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * Fetch and load in SharedPrefs
 */
public class PrefHelper {
    private final static String PREF_FILE = "globalpref";

    public static final String KEY_PREF_USER_LAT = "pref_user_lat";
    public static final String KEY_PREF_USER_LNG = "pref_user_lng";

    private static void saveFloat(Context context, String key, float f) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(key, f);
        editor.commit();
    }

    private static float getFloat(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getFloat(key, 0);
    }

    public static void saveUserLat(Context context, double d) {
        saveFloat(context, KEY_PREF_USER_LAT, (float) d);
    }

    public static double loadUserLat(Context context) {
        return (double) getFloat(context, KEY_PREF_USER_LAT);
    }

    public static void saveUserLng(Context context, double d) {
        saveFloat(context, KEY_PREF_USER_LNG, (float) d);
    }

    public static double loadUserLng(Context context) {
        return (double) getFloat(context, KEY_PREF_USER_LNG);
    }
}
