package com.daribear.prefy.Utils.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Wrapper class for android studio's shared prefs.
 * Contains function to delete a shared pref, or receive/save certain shared prefs.
 */
public class Utils {
    public static final String SHARED_PREFS = "sharedPrefs";
    private boolean temporary_boolean;
    private String temporary_string;
    private long temporary_long;


    private final Context temp_context;


    public Utils(Context context) {
        this.temp_context = context;
    }

    /**
     * Saves a boolean shared pref to a specific key.
     * @param saveName key to save the value to
     * @param value value to be saved
     */
    public void saveBoolean(String saveName, boolean value ) {
        SharedPreferences sharedPreferences = temp_context.getSharedPreferences(SHARED_PREFS,temp_context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(saveName, value);
        editor.commit();
    }

    /**
     * Loads a boolean shared pref
     * @param saveName shared pref key
     * @param defaultValue default value if one couldn't be found.
     * @return the boolean retrieved
     */
    public boolean loadBoolean(String saveName, Boolean defaultValue) {
        SharedPreferences sharedPreferences = temp_context.getSharedPreferences(SHARED_PREFS, temp_context.MODE_PRIVATE);
        temporary_boolean = sharedPreferences.getBoolean(saveName, defaultValue);
        return temporary_boolean;
    }


    /**
     * Loads a string shared pref
     * @param saveName shared pref key
     * @param defaultValue default value if one couldn't be found.
     * @return the string retrieved
     */
    public String loadString(String saveName, String defaultValue) {
        SharedPreferences sharedPreferences = temp_context.getSharedPreferences(SHARED_PREFS, temp_context.MODE_PRIVATE);
        temporary_string = sharedPreferences.getString(saveName, defaultValue);
        return temporary_string;
    }

    /**
     * Saves a string shared pref to a specific key.
     * @param saveName key to save the value to
     * @param value value to be saved
     */
    public void saveString(String saveName, String value ) {
        SharedPreferences sharedPreferences = temp_context.getSharedPreferences(SHARED_PREFS,temp_context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(saveName, value);
        editor.commit();
    }

    /**
     * Saves a long shared pref to a specific key.
     * @param saveName key to save the value to
     * @param value value to be saved
     */
    public void saveLong(String saveName, long value ) {
        SharedPreferences sharedPreferences = temp_context.getSharedPreferences(SHARED_PREFS,temp_context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(saveName, value);
        editor.commit();
    }

    /**
     * Loads a long shared pref
     * @param saveName shared pref key
     * @param defaultValue default value if one couldn't be found.
     * @return the long retrieved
     */
    public long loadLong(String saveName, long defaultValue) {
        SharedPreferences sharedPreferences = temp_context.getSharedPreferences(SHARED_PREFS, temp_context.MODE_PRIVATE);
        temporary_long = sharedPreferences.getLong(saveName, defaultValue);
        return temporary_long;
    }

    /**
     * Deletes a shared pref
     * @param sharedPref shared pref to delete
     */
    public void removeSharedPreference(String sharedPref){
        SharedPreferences sharedPreferences = temp_context.getSharedPreferences(SHARED_PREFS,temp_context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(sharedPref);
        editor.commit();
    }




}
