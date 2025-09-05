package com.daribear.prefy.Database;

import android.database.Cursor;

/**
 * A class with helper static functions for use with the sql database.
 */
public class DatabaseUtils {

    /**
     * Gets a long from the database, or null if it's empty.
     * @param cursor Cursor pointing to the database row
     * @param field Column name to retrieve
     * @return long value or null
     */
    public static Long getLongWithNull(Cursor cursor, String field){
        return (cursor.isNull(cursor.getColumnIndexOrThrow(field))) ? null : cursor.getLong(cursor.getColumnIndexOrThrow(field));
    }

    /**
     * Gets a double from the database, or null if it's empty.
     * @param cursor Cursor pointing to the database row
     * @param field Column name to retrieve
     * @return double value or null
     */
    public static Double getDoubleWithNull(Cursor cursor, String field){
        return (cursor.isNull(cursor.getColumnIndexOrThrow(field))) ? null : cursor.getDouble(cursor.getColumnIndexOrThrow(field));
    }

    /**
     * Gets a string from the database, or null if it's empty.
     * @param cursor Cursor pointing to the database row
     * @param field Column name to retrieve
     * @return string value or null
     */
    public static String getStringWithNull(Cursor cursor, String field){
        return (cursor.isNull(cursor.getColumnIndexOrThrow(field))) ? null : cursor.getString(cursor.getColumnIndexOrThrow(field));
    }

    /**
     * Gets a integer from the database, or null if it's empty.
     * @param cursor Cursor pointing to the database row
     * @param field Column name to retrieve
     * @return integer value or null
     */
    public static Integer getIntegerWithNull(Cursor cursor, String field ){
        return (cursor.isNull(cursor.getColumnIndexOrThrow(field))) ? null : cursor.getInt(cursor.getColumnIndexOrThrow(field));
    }


}
