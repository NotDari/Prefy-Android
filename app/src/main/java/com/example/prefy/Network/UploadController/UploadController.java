package com.example.prefy.Network.UploadController;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.prefy.Comments.Comment;
import com.example.prefy.Database.DatabaseHelper;

import java.util.HashMap;

public class UploadController {
    private static final String UploadTableName = "UploadTasks";
    private static final String VotesTableName = "UploadVotes";
    private static final String ActivityClearTableName = "UploadActivityClear";
    private static final String CommentTableName = "UploadComments";
    private static final String Count = "Count";

    public static void saveVote(Context appContext, HashMap<String, Object> Vote){
        SQLiteDatabase db = DatabaseHelper.getInstance(appContext).getWritableDatabase();
        Cursor checkCursor = db.rawQuery("SELECT * FROM " + VotesTableName +
                " WHERE " + "PostId" + " = " +(Long) Vote.get("PostId"), null);
        if (checkCursor.getCount() > 0) {
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("PostId", (Long) Vote.get("PostId"));
            contentValues.put("Vote", Vote.get("Vote").toString());
            db.insert(VotesTableName, null, contentValues);
            db.execSQL("UPDATE " + UploadTableName +
                    " SET " + Count + " = " + Count + " + 1" +
                    " WHERE " + "Type" + " = ?", new String[]{"Vote"});
            attempUpload(appContext);
        }
    }

    public static void saveActivityClear(Context appContext, String type){
        SQLiteDatabase db = DatabaseHelper.getInstance(appContext).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Type", type);

        Cursor checkCursor = db.rawQuery("SELECT * FROM " + ActivityClearTableName +
                " WHERE " + "Type" + " = ?", new String[] {type});
        if (checkCursor.getCount() > 0){
        } else {
            db.execSQL("UPDATE " + UploadTableName +
                    " SET " + Count + " = " + Count + " + 1" +
                    " WHERE " + "Type" + " = ?", new String[] {"ActivityClear"});
            db.insert(ActivityClearTableName, null, contentValues);
            attempUpload(appContext);
        }
    }

    public static void saveComment(Context appContext, Comment comment){
        SQLiteDatabase db = DatabaseHelper.getInstance(appContext).getWritableDatabase();
        db.execSQL("UPDATE " + UploadTableName +
                " SET " + Count + " = " + Count + " + 1" +
                " WHERE " + "Type" + " = ?", new String[] {"Comment"});
        ContentValues contentValues = new ContentValues();
        contentValues.put("postId", comment.getPostId());
        contentValues.put("replyId", comment.getReplyID());
        contentValues.put("replyUsername", comment.getReplyUsername());
        contentValues.put("text", comment.getText());
        contentValues.put("UserID", comment.getUserId());
        contentValues.put("CreationDate", comment.getCreationDate());
        db.insert(CommentTableName, null, contentValues);
        attempUpload(appContext);
        //contentValues.put("", comment.get);
    }

    public static void attempUpload(Context appContext){
        UploadEndpoint endpoint = UploadEndpoint.getInstance(appContext);
        endpoint.startUploads();
    }



}
