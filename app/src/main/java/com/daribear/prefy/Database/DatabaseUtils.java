package com.daribear.prefy.Database;

import android.database.Cursor;

public class DatabaseUtils {


    public static Long getLongWithNull(Cursor cursor, String field){
        return (cursor.isNull(cursor.getColumnIndexOrThrow(field))) ? null : cursor.getLong(cursor.getColumnIndexOrThrow(field));
    }

    public static Double getDoubleWithNull(Cursor cursor, String field){
        return (cursor.isNull(cursor.getColumnIndexOrThrow(field))) ? null : cursor.getDouble(cursor.getColumnIndexOrThrow(field));
    }

    public static String getStringWithNull(Cursor cursor, String field){
        return (cursor.isNull(cursor.getColumnIndexOrThrow(field))) ? null : cursor.getString(cursor.getColumnIndexOrThrow(field));
    }


}
