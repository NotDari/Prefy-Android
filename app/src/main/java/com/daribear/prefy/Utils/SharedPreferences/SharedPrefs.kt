package com.daribear.prefy.Utils.SharedPreferences

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(val context: Context) {
    val Sharedprefs:SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

    public fun putStringSharedPref(stringKey: String, inputText: String){
        val editor = Sharedprefs.edit()
        editor.apply(){
            putString(stringKey, inputText)
        }.commit()
    }

    public fun getStringSharedPref(stringKey: String): String?{
        val savedString: String? = Sharedprefs.getString(stringKey, null)
        return savedString
    }

    public fun putLongSharedPref(stringKey: String,longValue: Long){
        val editor = Sharedprefs.edit()
        editor.apply(){
            putLong(stringKey, longValue)
        }.commit()
    }

    public fun putBooleanSharedPref(stringKey: String, value: Boolean){
        val editor = Sharedprefs.edit()
        editor.apply(){
            putBoolean(stringKey, value)
        }.commit()
    }
}