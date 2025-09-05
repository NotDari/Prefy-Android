package com.daribear.prefy.Database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Helper class which initiates all the tasks with default values.
 *
 * Uploads default rows in the UploadTasks, which is how the Upload Controller systematically uploads data.
 */
public class InitDatabaseTasks {

    /**
     * Initialises all the potential upload tasks with default values(0).
     * Adds every uplaod tasks including Vote, ActivityClear, Comment, Report, Delete, Follow
     * @param db database to add to
     */
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
        ContentValues followContentValues = new ContentValues();
        followContentValues.put("Type", "Follow");
        followContentValues.put("Count", 0);
        db.insert("UploadTasks", null, followContentValues);
    }
}
