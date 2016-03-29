package com.xlgzs.weather;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferUtils {

    private static final String PREF_NAME = "snow_pref";
    public static final String KEY_DEFAULT_CITY_INDEX = "default_city_index";

    public static boolean getBoolean(Context context, String key, boolean def) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(key, def);
    }

    public static int getInt(Context context, String key, int def) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(key, def);
    }

    public static void setInt(Context context, String key, int value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().putInt(key, value).apply();
    }
}
