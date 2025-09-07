package com.daribear.prefy.Network.UploadController;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;

import com.daribear.prefy.Comments.Comment;
import com.daribear.prefy.Database.DatabaseHelper;
import com.daribear.prefy.Database.DatabaseUtils;
import com.daribear.prefy.Report.Report;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.daribear.prefy.customClasses.Posts.FullPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * The endpoint for uploading data from the upload tables.
 * Uploads everything on a single thread.
 */
public class UploadEndpoint {
    private static UploadEndpoint instance;
    private Context appContext;
    private Boolean checking = false;
    private Integer IntegerCount, CompletedCount, SuccessfulCount;
    private String serverAddress, authToken;
    private OkHttpClient client;
    private Long userId;

    private final String uploadVotesTable = "UploadVotes";
    private final String uploadTasksTable = "UploadTasks";
    private final String uploadActivityClearTable = "UploadActivityClear";
    private final String uploadCommentsTable = "UploadComments";
    private final String uploadReportsTable = "UploadReports";
    private final String uploadDeleteTable = "UploadDeleteTable";
    private final String uploadFollowTable = "UploadFollowTable";


    public UploadEndpoint(Context appContext) {
        this.appContext = appContext;
        IntegerCount = 0;
        CompletedCount = 0;
        serverAddress = ServerAdminSingleton.getInstance().getServerAddress();
        authToken = ServerAdminSingleton.getInstance().getServerAuthToken();
        userId = ServerAdminSingleton.getInstance().getLoggedInId();
        client = new OkHttpClient();
    }

    public static UploadEndpoint getInstance(Context applicationContext){
        if (instance == null){
            instance = new UploadEndpoint(applicationContext);
        }
        return instance;
    }

    /**
     * Starts the upload process on a seperate thread, which goes through every upload table, and attempts to upload them to the database.
     * If successful it is removed from the database, else it stays there
     */
    public void startUploads(){
        if (!checking){
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    checking = true;
                    DatabaseHelper databaseHelper = DatabaseHelper.getInstance(appContext);
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    IntegerCount = 0;
                    CompletedCount = 0;
                    SuccessfulCount = 0;
                    client = new OkHttpClient();
                    String query = "Select Count FROM " + uploadTasksTable;
                    Cursor cursor = db.rawQuery(query, null);
                    if (cursor.moveToFirst()){
                        for (int i =0; i < cursor.getCount(); i ++) {
                            IntegerCount += cursor.getInt(cursor.getColumnIndexOrThrow("Count"));
                            cursor.moveToNext();
                        }
                    }
                    cursor.close();
                    if (IntegerCount > 0){
                        Cursor voteCursor = db.rawQuery("Select * FROM " + uploadVotesTable, null);
                        if (voteCursor.moveToFirst()){
                            for (int i = 0; i < voteCursor.getCount(); i ++){
                                Integer failedCount = DatabaseUtils.getIntegerWithNull(voteCursor, "failedCount");
                                Long PostId = voteCursor.getLong(voteCursor.getColumnIndexOrThrow("PostId"));
                                String Vote = voteCursor.getString(voteCursor.getColumnIndexOrThrow("Vote"));
                                Executors.newSingleThreadExecutor().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        ArrayList<FullPost> postList = new ArrayList<>();
                                        HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Posts/PostVote").newBuilder();
                                        JSONObject jsonObject = new JSONObject();
                                        try {
                                            jsonObject.put("userId", userId);
                                            jsonObject.put("id", PostId);
                                            jsonObject.put("vote", Vote);
                                        } catch (JSONException e) {
                                            CompletedCount += 1;
                                            checkCompleted();
                                        }
                                        if (failedCount <= 4) {
                                            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                                            Request request = new Request.Builder()
                                                    .url(httpBuilder.build())
                                                    .method("POST", body)
                                                    .addHeader("Content-Type", "application/json")
                                                    .addHeader("Authorization", authToken)
                                                    .build();
                                            try {
                                                Response response = client.newCall(request).execute();

                                                if (response.isSuccessful()) {
                                                    CompletedCount += 1;
                                                    SuccessfulCount += 1;
                                                    removeVoteFromDb(db, PostId, Vote);
                                                    checkCompleted();
                                                } else {
                                                    CompletedCount += 1;
                                                    checkCompleted();
                                                }
                                            } catch (IOException e) {
                                                CompletedCount += 1;
                                                checkCompleted();
                                            }
                                        } else {
                                            CompletedCount += 1;
                                            SuccessfulCount += 1;
                                            removeVoteFromDb(db, PostId, Vote);
                                            checkCompleted();
                                        }
                                    }
                                });
                                voteCursor.moveToNext();
                            }
                        }
                        voteCursor.close();
                        Cursor activityClearCursor = db.rawQuery("Select * FROM " + uploadActivityClearTable, null);
                        if (activityClearCursor.moveToFirst()){
                            for (int i =0; i < activityClearCursor.getCount(); i ++){
                                String type = activityClearCursor.getString(activityClearCursor.getColumnIndexOrThrow("Type"));
                                Integer failedCount = DatabaseUtils.getIntegerWithNull(activityClearCursor, "failedCount");


                                if (type != null ){
                                    String currentActivityName = null;
                                    if (type.equals("Comments")){
                                        currentActivityName = "newCommentsCount";
                                    } else if (type.equals("Votes")){
                                        currentActivityName = "newVotesCount";
                                    } else if (type.equals("Followers")){
                                        currentActivityName = "newFollowsCount";
                                    }
                                    else {
                                        CompletedCount += 1;
                                        checkCompleted();
                                    }
                                    if (currentActivityName != null){
                                        HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Activity/SetUserActivity").newBuilder();
                                        JSONObject jsonObject = new JSONObject();
                                        Boolean bodyComplete;
                                        try {
                                            jsonObject.put("id", userId);
                                            jsonObject.put(currentActivityName, "0");
                                            bodyComplete = true;
                                        } catch (JSONException e) {
                                            CompletedCount += 1;
                                            checkCompleted();
                                            bodyComplete = false;
                                        }
                                        if (bodyComplete) {
                                            if (failedCount <= 4) {
                                                RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                                                Request request = new Request.Builder()
                                                        .url(httpBuilder.build())
                                                        .method("POST", body)
                                                        .addHeader("Content-Type", "application/json")
                                                        .addHeader("Authorization", authToken)
                                                        .build();
                                                try {
                                                    Response response = client.newCall(request).execute();
                                                    if (response.isSuccessful()) {
                                                        CompletedCount += 1;
                                                        SuccessfulCount += 1;
                                                        removeActivityFromDb(db, type);
                                                        checkCompleted();
                                                    } else {
                                                        CompletedCount += 1;
                                                        checkCompleted();
                                                    }
                                                } catch (IOException e) {
                                                    CompletedCount += 1;
                                                    checkCompleted();
                                                }
                                            } else {
                                                CompletedCount += 1;
                                                SuccessfulCount += 1;
                                                removeActivityFromDb(db, type);
                                                checkCompleted();
                                            }
                                        }
                                    }
                                }else {
                                    CompletedCount += 1;
                                    checkCompleted();
                                }
                                activityClearCursor.moveToNext();
                            }
                        }
                        activityClearCursor.close();
                        Cursor commentCursor = db.rawQuery("Select * FROM " + uploadCommentsTable, null);
                        if (commentCursor.moveToFirst()){
                            for (int i = 0; i < commentCursor.getCount(); i++){
                                Executors.newSingleThreadExecutor().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Integer failedCount = DatabaseUtils.getIntegerWithNull(commentCursor, "failedCount");

                                        Comment comment = new Comment();
                                        comment.setPostId(DatabaseUtils.getLongWithNull(commentCursor, "PostId"));
                                        comment.setReplyID(DatabaseUtils.getLongWithNull(commentCursor, "ReplyId"));
                                        comment.setSubReplyID(DatabaseUtils.getLongWithNull(commentCursor, "subReplyID"));
                                        comment.setReplyUsername(DatabaseUtils.getStringWithNull(commentCursor, "ReplyUsername"));
                                        comment.setText(DatabaseUtils.getStringWithNull(commentCursor, "text"));
                                        comment.setUserId(DatabaseUtils.getLongWithNull(commentCursor, "UserId"));
                                        comment.setCreationDate(DatabaseUtils.getDoubleWithNull(commentCursor, "CreationDate"));



                                        HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Comments/SubmitComment").newBuilder();
                                        JSONObject jsonObject = new JSONObject();
                                        try {
                                            jsonObject.put("postId", comment.getPostId());
                                            jsonObject.put("creationDate", comment.getCreationDate());
                                            jsonObject.put("text", comment.getText());
                                            jsonObject.put("parentId", comment.getReplyID());
                                            jsonObject.put("subParentId", comment.getSubReplyID());
                                            jsonObject.put("userId", comment.getUserId());
                                        } catch (JSONException e) {
                                            CompletedCount += 1;
                                            checkCompleted();
                                        }
                                        if (failedCount <= 4) {
                                            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                                            Request request = new Request.Builder()
                                                    .url(httpBuilder.build())
                                                    .method("POST", body)
                                                    .addHeader("Content-Type", "application/json")
                                                    .addHeader("Authorization", authToken)
                                                    .build();

                                            try {
                                                Response response = client.newCall(request).execute();
                                                if (response.isSuccessful()) {
                                                    CompletedCount += 1;
                                                    SuccessfulCount += 1;
                                                    removeCommentFromDb(db, comment);
                                                    checkCompleted();
                                                } else {
                                                    CompletedCount += 1;
                                                    checkCompleted();
                                                }
                                            } catch (IOException e) {
                                                CompletedCount += 1;
                                                checkCompleted();
                                            }
                                        } else {
                                            CompletedCount += 1;
                                            SuccessfulCount += 1;
                                            removeCommentFromDb(db, comment);
                                            checkCompleted();
                                        }

                                    }
                                });
                            }
                        }
                        Cursor reportCursor = db.rawQuery("Select * FROM " + uploadReportsTable, null);
                        if (reportCursor.moveToFirst()){
                            for (int i =0; i < reportCursor.getCount(); i ++){
                                Executors.newSingleThreadExecutor().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Integer failedCount = DatabaseUtils.getIntegerWithNull(reportCursor, "failedCount");
                                        Report report = new Report();
                                        report.setPostId(DatabaseUtils.getLongWithNull(reportCursor, "postId"));
                                        report.setUserId(DatabaseUtils.getLongWithNull(reportCursor, "userId"));
                                        report.setCreationDate(DatabaseUtils.getDoubleWithNull(reportCursor, "creationDate"));
                                        report.setCommentId(DatabaseUtils.getLongWithNull(reportCursor, "commentId"));
                                        report.setRepCategory(DatabaseUtils.getStringWithNull(reportCursor, "repCategory"));
                                        report.setType(DatabaseUtils.getStringWithNull(reportCursor, "Type"));

                                        HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Reports/SubmitReport").newBuilder();
                                        JSONObject jsonObject = new JSONObject();
                                        try {
                                            jsonObject.put("postId", report.getPostId());
                                            jsonObject.put("userId", report.getUserId());
                                            jsonObject.put("creationDate", report.getCreationDate());
                                            jsonObject.put("commentId", report.getCommentId());
                                            jsonObject.put("repCategory", report.getRepCategory());
                                            jsonObject.put("type", report.getType());
                                        } catch (JSONException e) {
                                            CompletedCount += 1;
                                            checkCompleted();
                                        }
                                        if (failedCount <= 4) {
                                            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                                            Request request = new Request.Builder()
                                                    .url(httpBuilder.build())
                                                    .method("POST", body)
                                                    .addHeader("Content-Type", "application/json")
                                                    .addHeader("Authorization", authToken)
                                                    .build();

                                            try {
                                                Response response = client.newCall(request).execute();
                                                if (response.isSuccessful()) {
                                                    CompletedCount += 1;
                                                    SuccessfulCount += 1;
                                                    removeReportFromDb(db, report);
                                                    checkCompleted();
                                                } else {
                                                    CompletedCount += 1;
                                                    checkCompleted();
                                                }
                                            } catch (IOException e) {
                                                CompletedCount += 1;
                                                checkCompleted();
                                            }
                                        } else {
                                            CompletedCount += 1;
                                            SuccessfulCount += 1;
                                            removeReportFromDb(db, report);
                                            checkCompleted();
                                        }

                                    }
                                });
                            }
                        }
                        Cursor deleteCursor = db.rawQuery("Select * FROM " + uploadDeleteTable, null);
                        if (deleteCursor.moveToFirst()){
                            for (int i =0; i < deleteCursor.getCount(); i ++){
                                Executors.newSingleThreadExecutor().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Integer failedCount = DatabaseUtils.getIntegerWithNull(deleteCursor, "failedCount");
                                        String type = DatabaseUtils.getStringWithNull(deleteCursor, "Type");
                                        Long itemId = DatabaseUtils.getLongWithNull(deleteCursor, "ItemId");
                                        Long userID = DatabaseUtils.getLongWithNull(deleteCursor, "UserId");
                                        Boolean breakOut;
                                        HttpUrl.Builder httpBuilder;
                                        if (type.equals("Comment")){
                                            breakOut = false;
                                            httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Comments/DeleteComment").newBuilder();
                                        } else if (type.equals("Post")) {
                                            breakOut = false;
                                            httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Posts/DeletePost").newBuilder();
                                        } else {
                                            breakOut = true;
                                            httpBuilder = null;
                                            CompletedCount += 1;
                                            checkCompleted();
                                        }
                                        if (!breakOut) {
                                            JSONObject jsonObject = new JSONObject();
                                            try {
                                                jsonObject.put("itemId", itemId);
                                                jsonObject.put("userId", userID);
                                            } catch (JSONException e) {
                                                CompletedCount += 1;
                                                checkCompleted();
                                            }
                                            if (itemId == null || userID == null) {
                                                CompletedCount += 1;
                                                SuccessfulCount += 1;
                                                removeDeleteFromDb(db, type, itemId, userID);
                                                checkCompleted();
                                            } else {
                                                if (failedCount <= 4) {
                                                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                                                    Request request = new Request.Builder()
                                                            .url(httpBuilder.build())
                                                            .method("PUT", body)
                                                            .addHeader("Content-Type", "application/json")
                                                            .addHeader("Authorization", authToken)
                                                            .build();
                                                    try {
                                                        Response response = client.newCall(request).execute();
                                                        if (response.isSuccessful()) {
                                                            CompletedCount += 1;
                                                            SuccessfulCount += 1;
                                                            removeDeleteFromDb(db, type, itemId, userID);
                                                            checkCompleted();
                                                        } else {
                                                            CompletedCount += 1;
                                                            checkCompleted();
                                                        }
                                                    } catch (IOException e) {
                                                        CompletedCount += 1;
                                                        checkCompleted();
                                                    }
                                                } else {
                                                    CompletedCount += 1;
                                                    SuccessfulCount += 1;
                                                    removeDeleteFromDb(db, type, itemId, userID);
                                                    checkCompleted();
                                                }
                                            }
                                        }





                                    }
                                });
                            }
                        }
                        Cursor followCursor = db.rawQuery("Select * FROM " + uploadFollowTable, null);
                        if (followCursor.moveToFirst()){
                            for (int i = 0; i < followCursor.getCount(); i++){
                                Executors.newSingleThreadExecutor().execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        Integer failedCount = followCursor.getInt(followCursor.getColumnIndexOrThrow("failedCount"));
                                        Integer follow = DatabaseUtils.getIntegerWithNull(followCursor, "Follow");
                                        Boolean followBool = (follow == 1);
                                        Long followingUserId = DatabaseUtils.getLongWithNull(followCursor, "FollowingUserId");
                                        Long userID = DatabaseUtils.getLongWithNull(followCursor, "UserId");
                                        if (failedCount <= 4){
                                            HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Follows/Follow").newBuilder();
                                            JSONObject jsonObject = new JSONObject();
                                            try {
                                                jsonObject.put("followId", followingUserId);
                                                jsonObject.put("userId", userID);
                                                jsonObject.put("follow", followBool);
                                            } catch (JSONException e) {
                                                CompletedCount += 1;
                                                checkCompleted();
                                            }
                                            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                                            Request request = new Request.Builder()
                                                    .url(httpBuilder.build())
                                                    .method("POST", body)
                                                    .addHeader("Content-Type", "application/json")
                                                    .addHeader("Authorization", authToken)
                                                    .build();
                                            try {
                                                Response response = client.newCall(request).execute();
                                                if (response.isSuccessful()) {
                                                    CompletedCount += 1;
                                                    SuccessfulCount += 1;
                                                    removeFollowFromDb(db, userID, followingUserId);
                                                    checkCompleted();
                                                } else {
                                                    updateFollowFailedCount(db, failedCount, userID, followingUserId);
                                                    CompletedCount += 1;
                                                    checkCompleted();
                                                }
                                            } catch (IOException e) {
                                                CompletedCount += 1;
                                                checkCompleted();
                                            }
                                        } else {
                                            CompletedCount += 1;
                                            SuccessfulCount += 1;
                                            removeFollowFromDb(db, userID, followingUserId);
                                            checkCompleted();
                                        }

                                    }
                                });
                            }
                        }
                    } else {
                        checking = false;
                    }
                }
            });

        }
    }


    /**
     * Removes a vote from the local database as it has been uploaded successfully
     * @param db database
     * @param PostId post the vote is on
     * @param Vote vote
     */
    public void removeVoteFromDb(SQLiteDatabase db, Long PostId, String Vote){
        Cursor cursor = db.rawQuery("Select * FROM UploadVotes WHERE PostId = " + PostId, null);
        if (cursor.getCount() > 0){
            db.execSQL("UPDATE " + "UploadTasks" +
                    " SET " + "Count" + " = " + "Count" + " - 1" +
                    " WHERE " + "Type" + " = ?" +
                    "AND Count > 0"
                    , new String[] {"Vote"});
            db.delete("UploadVotes","PostId=? and Vote=?",new String[]{PostId.toString(),Vote});
        }
        cursor.close();
        Cursor popCursor = db.rawQuery("Select * FROM PopularPostsPopularPosts WHERE postId = " + PostId, null);
        if (popCursor.getCount() > 0){
            db.execSQL("DELETE from PopularPostsPopularPosts  WHERE postID = " + PostId);
        }
        popCursor.close();
    }

    /**
     * Remove an activity from the database as it has been uploaded successfully
     * @param db database
     * @param Type type of activityClear to remove from the database
     */
    public void removeActivityFromDb(SQLiteDatabase db, String Type){
        db.execSQL("UPDATE " + "UploadTasks" +
                " SET " + "Count" + " = " + "Count" + " - 1" +
                " WHERE " + "Type" + " = ?" +
                "AND Count > 0",
                new String[] {"ActivityClear"});
        db.delete("UploadActivityClear","Type=?",new String[]{Type});
    }

    /**
     * Removes a comment from the database as it has been uploaded successfully
     * @param db database
     * @param comment comment to remove from the database
     */
    public void removeCommentFromDb(SQLiteDatabase db, Comment comment){
        db.execSQL("UPDATE " + "UploadTasks" +
                " SET " + "Count" + " = " + "Count" + " - 1" +
                " WHERE " + "Type" + " = ?", new String[] {"Comment"});
        db.delete("UploadComments","text=? and CreationDate=?",new String[]{comment.getText(), comment.getCreationDate().toString()});
    }


    /**
     * Removes a report from the database as it has been uploaded successfully
     * @param db database
     * @param report report to remove from the database
     */
    public void removeReportFromDb(SQLiteDatabase db, Report report){
        db.execSQL("UPDATE " + "UploadTasks" +
                " SET " + "Count" + " = " + "Count" + " - 1" +
                " WHERE " + "Type" + " = ?", new String[] {"Report"});
        db.delete("UploadReports","userId=? and CreationDate=?",new String[]{report.getUserId().toString(), report.getCreationDate().toString()});
    }

    /**
     * Removes a comment/post delete from the database as it has been uploaded successfully
     * @param db database
     * @param type type of item (comment/post)
     * @param itemId id of the item
     * @param userId logged in user id
     */
    public void removeDeleteFromDb(SQLiteDatabase db, String type, Long itemId, Long userId){
        db.execSQL("UPDATE " + "UploadTasks" +
                " SET " + "Count" + " = " + "Count" + " - 1" +
                " WHERE " + "Type" + " = ?", new String[] {"Delete"});
        db.delete("UploadDeleteTable","UserId=? and ItemId=? and Type=?",new String[]{userId.toString(), itemId.toString(), type.toString()});
    }

    /**
     * Removes a follow from the database as it has been uploaded successfully
     * @param db database
     * @param userId id of the user
     * @param userFollowId id of the user to follow/unfollow
     */
    public void removeFollowFromDb(SQLiteDatabase db, Long userId, Long userFollowId){
        db.execSQL("UPDATE " + "UploadTasks" +
                " SET " + "Count" + " = " + "Count" + " - 1" +
                " WHERE " + "Type" + " = ?", new String[] {"Follow"});
        db.delete("UploadFollowTable","UserId=? and FollowingUserId=?",new String[]{userId.toString(), userFollowId.toString()});
    }

    /**
     * Updates and increments the follow failed count following a failed upload
     * @param db database
     * @param originalFailedCount the original failed count (to increment)
     * @param userId user id
     * @param userFollowId id of the user to follow
     */
    public void updateFollowFailedCount(SQLiteDatabase db, Integer originalFailedCount, Long userId, Long userFollowId){

        ContentValues cv = new ContentValues();
        cv.put("failedCount", originalFailedCount + 1);
        db.update(uploadFollowTable, cv, "UserId=? and FollowingUserId=?",new String[]{userId.toString(), userFollowId.toString()});
    }

    /**
     * Check if all the uploads have been completed succesfully.
     */
    private void checkCompleted(){
        if (IntegerCount == CompletedCount){
            Boolean delay = false;
            if (SuccessfulCount == IntegerCount){
                checking = false;
            } else {
               delay = true;
            }
            IntegerCount = 0;
            CompletedCount = 0;
            SuccessfulCount = 0;
            if (delay){
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checking = false;
                        startUploads();
                    }
                }, 5000);
            }

        } else {
            checking = false;
        }
    }


}
