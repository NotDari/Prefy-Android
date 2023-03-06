package com.example.prefy.Database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class InitDatabaseTasks {

    public static void init(SQLiteDatabase db){
        ContentValues voteContentValues = new ContentValues();
        voteContentValues.put("Type", "Vote");
        voteContentValues.put("Count", 0);
        db.insert("UploadTasks", null, voteContentValues);
        ContentValues ActivityClearContentValues = new ContentValues();
        ActivityClearContentValues.put("Type", "ActivityClear");
        ActivityClearContentValues.put("Count", 0);
        db.insert("UploadTasks", null, ActivityClearContentValues);
        ContentValues CommentContentValues = new ContentValues();
        CommentContentValues.put("Type", "Comment");
        CommentContentValues.put("Count", 0);
        db.insert("UploadTasks", null, CommentContentValues);
        ContentValues reportContentValues = new ContentValues();
        reportContentValues.put("Type", "Report");
        reportContentValues.put("Count", 0);
        db.insert("UploadTasks", null, reportContentValues);
        ContentValues deleteContentValues = new ContentValues();
        deleteContentValues.put("Type", "Delete");
        deleteContentValues.put("Count", 0);
        db.insert("UploadTasks", null, deleteContentValues);
    }
}
