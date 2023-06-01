package com.daribear.prefy.Utils.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
    public static final String SHARED_PREFS = "sharedPrefs";
    private boolean temporary_boolean;
    private String temporary_string;
    private long temporary_long;


    private final Context temp_context;


    public Utils(Context context) {
        this.temp_context = context;
    }

    public void saveBoolean(String saveName, boolean value ) {
        SharedPreferences sharedPreferences = temp_context.getSharedPreferences(SHARED_PREFS,temp_context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(saveName, value);
        editor.commit();
    }

    public boolean loadBoolean(String saveName, Boolean DefaultValue) {
        SharedPreferences sharedPreferences = temp_context.getSharedPreferences(SHARED_PREFS, temp_context.MODE_PRIVATE);
        temporary_boolean = sharedPreferences.getBoolean(saveName, DefaultValue);
        return temporary_boolean;
    }

    public String loadString(String saveName, String DefaultValue) {
        SharedPreferences sharedPreferences = temp_context.getSharedPreferences(SHARED_PREFS, temp_context.MODE_PRIVATE);
        temporary_string = sharedPreferences.getString(saveName, DefaultValue);
        return temporary_string;
    }

    public void saveString(String saveName, String value ) {
        SharedPreferences sharedPreferences = temp_context.getSharedPreferences(SHARED_PREFS,temp_context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(saveName, value);
        editor.commit();
    }


    public void saveLong(String saveName, long value ) {
        SharedPreferences sharedPreferences = temp_context.getSharedPreferences(SHARED_PREFS,temp_context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(saveName, value);
        editor.commit();
    }

    public long loadLong(String saveName, long defaultValue) {
        SharedPreferences sharedPreferences = temp_context.getSharedPreferences(SHARED_PREFS, temp_context.MODE_PRIVATE);
        temporary_long = sharedPreferences.getLong(saveName, defaultValue);
        return temporary_long;
    }

    public void removeSharedPreference(String sharedPref){
        SharedPreferences sharedPreferences = temp_context.getSharedPreferences(SHARED_PREFS,temp_context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(sharedPref);
        editor.commit();
    }




}
