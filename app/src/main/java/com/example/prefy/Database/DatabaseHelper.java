package com.example.prefy.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper mInstance;

    private static final String DB_NAME = "Prefy";
    private static final int DB_VERSION = 1;

    public static DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return mInstance;
    }



    private DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlstatement = "CREATE TABLE PopularPostsUsers(id Long,username String, profileImageURL String, fullname String, postsNumber Long, votesNumber Long, prefsNumber Long, rating Long, bio String, vk String, instagram, String, twitter String, verified Integer);";
        db.execSQL(sqlstatement);
        sqlstatement = "CREATE TABLE PopularPostsStandardPosts(postId Long PRIMARY KEY, userId Long, leftVotes Integer, rightVotes Integer, imageURL String, question String, commentsNumber Integer, creationDate Double, allVotes Integer, currentVote String);";
        db.execSQL(sqlstatement);
        sqlstatement = "Create TABLE UploadTasks(Type String PRIMARY KEY, Count Integer)";
        db.execSQL(sqlstatement);
        sqlstatement = "Create TABLE UploadVotes(PostId String, Vote String)";
        db.execSQL(sqlstatement);
        sqlstatement = "Create TABLE UploadActivityClear(Type String)";
        db.execSQL(sqlstatement);
        sqlstatement = "Create TABLE UploadComments(PostId Long, ReplyId Long, ReplyUsername String, text String, UserId Long, CreationDate Double, replyCount Integer)";
        db.execSQL(sqlstatement);
        InitDatabaseTasks.init(db);
        //db.close();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
