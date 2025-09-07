package com.daribear.prefy.Popular;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Helper class to check whether the upload table has uploaded the post already or not
 */
public class checkVoteNotSubmitted {
    private final String uploadVotesTable = "UploadVotes";

    public Boolean exists(SQLiteDatabase db, Long id){
        String statement = "SELECT * FROM " + uploadVotesTable + " WHERE PostId=" + id.toString();
        Cursor cursor = db.rawQuery(statement, null);
        if (cursor.getCount() > 0){
            return true;
        }
        cursor.close();
        return false;


    }
}
