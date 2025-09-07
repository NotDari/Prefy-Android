package com.daribear.prefy.Utils.SharedPreferences

import android.content.Context
import android.content.SharedPreferences

/**
 * Helper class to help with storing shared prefs. Helps store values in Android Shared prefs with helper functions.
 * The Kotlin equivalend of Utils.
 */
class SharedPrefs(val context: Context) {
    val Sharedprefs:SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

    /**
     * Put a string shared pref with a specific key and inputText.
     */
    public fun putStringSharedPref(stringKey: String, inputText: String){
        val editor = Sharedprefs.edit()
        editor.apply(){
            putString(stringKey, inputText)
        }.commit()
    }

    /**
     * Put a long shared pref with a specific key and longValue.
     */
    public fun putLongSharedPref(stringKey: String,longValue: Long){
        val editor = Sharedprefs.edit()
        editor.apply(){
            putLong(stringKey, longValue)
        }.commit()
    }

    /**
     * Put a boolean shared pref with a specific key and value.
     */
    public fun putBooleanSharedPref(stringKey: String, value: Boolean){
        val editor = Sharedprefs.edit()
        editor.apply(){
            putBoolean(stringKey, value)
        }.commit()
    }
}