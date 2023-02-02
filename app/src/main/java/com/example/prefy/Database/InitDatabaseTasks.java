package com.example.prefy.Database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class InitDatabaseTasks {

    public static void init(SQLiteDatabase db){
        ContentValues reportContentValues = new ContentValues();
        reportContentValues.put("Type", "Vote");
        reportContentValues.put("Count", 0);
        db.insert("UploadTasks", null, reportContentValues);
        ContentValues ActivityClearContentValues = new ContentValues();
        ActivityClearContentValues.put("Type", "ActivityClear");
        ActivityClearContentValues.put("Count", 0);
        db.insert("UploadTasks", null, ActivityClearContentValues);
        ContentValues CommentContentValues = new ContentValues();
        CommentContentValues.put("Type", "Comment");
        CommentContentValues.put("Count", 0);
        db.insert("UploadTasks", null, CommentContentValues);
    }
}
