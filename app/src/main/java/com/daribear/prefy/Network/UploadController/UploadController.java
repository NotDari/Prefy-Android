package com.daribear.prefy.Network.UploadController;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.daribear.prefy.Comments.Comment;
import com.daribear.prefy.Database.DatabaseHelper;
import com.daribear.prefy.Report.Report;
import com.daribear.prefy.Utils.ServerAdminSingleton;

import java.util.HashMap;

public class UploadController {
    private static final String UploadTableName = "UploadTasks";
    private static final String VotesTableName = "UploadVotes";
    private static final String ActivityClearTableName = "UploadActivityClear";
    private static final String CommentTableName = "UploadComments";
    private static final String ReportTableName = "UploadReports";

    private static final String DeleteTableName = "UploadDeleteTable";

    private static final String FollowTableName = "UploadFollowTable";
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
            attemptUpload(appContext);
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
        }
        attemptUpload(appContext);
    }

    public static void saveComment(Context appContext, Comment comment){
        SQLiteDatabase db = DatabaseHelper.getInstance(appContext).getWritableDatabase();
        db.execSQL("UPDATE " + UploadTableName +
                " SET " + Count + " = " + Count + " + 1" +
                " WHERE " + "Type" + " = ?", new String[] {"Comment"});
        ContentValues contentValues = new ContentValues();
        contentValues.put("postId", comment.getPostId());
        contentValues.put("ReplyId", comment.getReplyID());
        contentValues.put("replyUsername", comment.getReplyUsername());
        contentValues.put("text", comment.getText());
        contentValues.put("UserID", comment.getUserId());
        contentValues.put("CreationDate", comment.getCreationDate());
        contentValues.put("subReplyID", comment.getSubReplyID());
        db.insert(CommentTableName, null, contentValues);
        attemptUpload(appContext);
        //contentValues.put("", comment.get);
    }

    public static void saveReport(Context appContext, Report report){
        SQLiteDatabase db = DatabaseHelper.getInstance(appContext).getWritableDatabase();
        db.execSQL("UPDATE " + UploadTableName +
                " SET " + Count + " = " + Count + " + 1" +
                " WHERE " + "Type" + " = ?", new String[] {"Report"});
        ContentValues contentValues = new ContentValues();
        contentValues.put("postId", report.getPostId());
        contentValues.put("userId", report.getUserId());
        contentValues.put("commentId", report.getCommentId());
        contentValues.put("repCategory", report.getRepCategory());
        contentValues.put("creationDate", report.getCreationDate());
        contentValues.put("Type", report.getType());
        db.insert(ReportTableName, null, contentValues);
        attemptUpload(appContext);
    }

    public static void saveDelete(Context appContext, String type, Long itemId){
        SQLiteDatabase db = DatabaseHelper.getInstance(appContext).getWritableDatabase();
        db.execSQL("UPDATE " + UploadTableName +
                " SET " + Count + " = " + Count + " + 1" +
                " WHERE " + "Type" + " = ?", new String[] {"Delete"});
        ContentValues contentValues = new ContentValues();
        contentValues.put("ItemId", itemId);
        contentValues.put("Type", type);
        contentValues.put("UserId", ServerAdminSingleton.getInstance().getLoggedInId());
        db.insert(DeleteTableName, null, contentValues);
        attemptUpload(appContext);
    }


    public static void saveFollow(Context appContext, Long FollowingUserId , Boolean Follow){
        System.out.println("Sdad follow: " + Follow);
        SQLiteDatabase db = DatabaseHelper.getInstance(appContext).getWritableDatabase();

        Cursor checkCursor = db.rawQuery("SELECT * FROM " + FollowTableName +
                " WHERE " + "FollowingUserId" + " = ?", new String[] {FollowingUserId.toString()});
        if (checkCursor.moveToFirst()){
            Boolean dataFollowing = (checkCursor.getInt(checkCursor.getColumnIndexOrThrow("Follow")) == 1);
            if (dataFollowing != Follow){
                Integer tempBool;
                if (Follow){
                    tempBool = 1;
                }else {
                    tempBool = 0;
                }
                db.execSQL("UPDATE " + FollowTableName +
                        " SET " + "Follow"  + " = " + tempBool +
                        " WHERE " + "FollowingUserId" + " = ?", new String[] {FollowingUserId.toString()});
            }
        } else {
            Integer tempBool;
            if (Follow){
                tempBool = 1;
            }else {
                tempBool = 0;
            }
            db.execSQL("UPDATE " + UploadTableName +
                    " SET " + Count + " = " + Count + " + 1" +
                    " WHERE " + "Type" + " = ?", new String[] {"Follow"});
            ContentValues contentValues = new ContentValues();
            contentValues.put("FollowingUserId", FollowingUserId);
            contentValues.put("Follow", tempBool);
            contentValues.put("UserId", ServerAdminSingleton.getInstance().getLoggedInId());
            contentValues.put("failedCount", 0);
            db.insert(FollowTableName, null, contentValues);
            attemptUpload(appContext);
        }
    }

    public static void attemptUpload(Context appContext){
        UploadEndpoint endpoint = UploadEndpoint.getInstance(appContext);
        endpoint.startUploads();
    }





}
