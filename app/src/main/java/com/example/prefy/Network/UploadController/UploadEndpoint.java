package com.example.prefy.Network.UploadController;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.prefy.Comments.Comment;
import com.example.prefy.Database.DatabaseHelper;
import com.example.prefy.Database.DatabaseUtils;
import com.example.prefy.Explore.ExplorePostSet;
import com.example.prefy.Explore.ExploreRepository;
import com.example.prefy.Network.CacheContentTools;
import com.example.prefy.Utils.CustomJsonMapper;
import com.example.prefy.Utils.ServerAdminSingleton;
import com.example.prefy.customClasses.FullPost;
import com.example.prefy.customClasses.StandardPost;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadEndpoint {
    private static UploadEndpoint instance;
    private Context appContext;
    private Boolean checking = false;
    private Integer IntegerCount, CompletedCount, SuccessfulCount;
    private String serverAddress, authToken;
    private OkHttpClient client;
    private Long userId;

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
                    String query = "Select Count FROM UploadTasks";
                    Cursor cursor = db.rawQuery(query, null);
                    if (cursor.moveToFirst()){
                        for (int i =0; i < cursor.getCount(); i ++) {
                            IntegerCount += cursor.getInt(cursor.getColumnIndexOrThrow("Count"));
                            cursor.moveToNext();
                        }
                    }
                    if (IntegerCount > 0){
                        Cursor voteCursor = db.rawQuery("Select * FROM UploadVotes", null);
                        if (voteCursor.moveToFirst()){
                            for (int i = 0; i < voteCursor.getCount(); i ++){
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
                                        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                                        Request request = new Request.Builder()
                                                .url(httpBuilder.build())
                                                .method("POST", body)
                                                .addHeader("Content-Type", "application/json")
                                                .addHeader("Authorization", authToken)
                                                .build();
                                        try {
                                            Response response = client.newCall(request).execute();

                                            if (response.isSuccessful()){
                                                CompletedCount += 1;
                                                SuccessfulCount += 1;
                                                removeVoteFromDb(db, PostId, Vote);
                                                checkCompleted();
                                            }else {
                                                CompletedCount += 1;
                                                checkCompleted();
                                            }
                                        } catch (IOException e) {
                                            CompletedCount += 1;
                                            checkCompleted();
                                        }
                                    }
                                });
                                /**
                                fDbVotes.child(FirebaseAuth.getInstance().getUid()).child(PostId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()){
                                            String currentVote = task.getResult().getValue(String.class);
                                            Boolean writeableValue = false;
                                            if (currentVote == null){
                                                writeableValue = true;
                                            } else {
                                                if (currentVote.equals("skip")){
                                                    writeableValue = true;
                                                }
                                            }
                                            System.out.println("Sdad writeable value:" + writeableValue);
                                            if (writeableValue){
                                                fDbVotes.child(FirebaseAuth.getInstance().getUid()).updateChildren(voteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        CompletedCount += 1;
                                                    if (task.isSuccessful()){
                                                            WriteBatch batch = FirebaseFirestore.getInstance().batch();
                                                            DocumentReference docRef = FirebaseFirestore.getInstance().collection("Posts").document(PostId);
                                                            String voteField;
                                                            if (Vote.equals("Right")){
                                                                voteField = "rightVotes";
                                                            }else if (Vote.equals("Left")){
                                                                voteField = "leftVotes";
                                                            } else {
                                                                voteField = null;
                                                            }
                                                            batch.update(docRef, voteField , FieldValue.increment(1));
                                                            batch.update(docRef, "allVotes" , FieldValue.increment(1));
                                                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    System.out.println("Sdad batchComitted:" +task.getResult());
                                                                }
                                                            });

                                                            SuccessfulCount += 1;
                                                            removeVoteFromDb(db, PostId, Vote);
                                                        }
                                                        checkCompleted();

                                                    }
                                                });
                                            } else {
                                                CompletedCount +=1;
                                                SuccessfulCount += 1;
                                                removeVoteFromDb(db, PostId, Vote);
                                                checkCompleted();
                                            }

                                        } else {
                                            System.out.println("Sdad task fail");
                                            CompletedCount += 1;
                                            checkCompleted();
                                        }
                                    }
                                });
                                */
                                voteCursor.moveToNext();
                            }
                        }
                                Cursor activityClearCursor = db.rawQuery("Select * FROM UploadActivityClear", null);
                        if (activityClearCursor.moveToFirst()){
                            for (int i =0; i < activityClearCursor.getCount(); i ++){
                                String type = activityClearCursor.getString(activityClearCursor.getColumnIndexOrThrow("Type"));
                                if (type != null ){
                                    String currentActivityName = null;
                                    if (type.equals("Comments")){
                                        currentActivityName = "newCommentsCount";
                                    } else if (type.equals("Votes")){
                                        currentActivityName = "newVotesCount";
                                    } else {
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
                                        }
                                        /**
                                        fDbActivity.child(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    Integer currentActivity = task.getResult().child(finalCurrentActivityName).getValue(Integer.class);
                                                    Integer totalActivity = task.getResult().child("newActivitiesCount").getValue(Integer.class);
                                                    if (currentActivity == null){
                                                        currentActivity = 0;
                                                    }
                                                    if (currentActivity != 0){
                                                        HashMap batchMap = new HashMap();
                                                        batchMap.put(finalCurrentActivityName, 0);
                                                        if (totalActivity != null){
                                                            if (totalActivity - currentActivity >= 0){
                                                                batchMap.put("newActivitiesCount", (totalActivity - currentActivity));
                                                            } else {
                                                                batchMap.put("newActivitiesCount", 0);
                                                            }
                                                        } else {
                                                            batchMap.put("newActivitiesCount", 0);
                                                        }
                                                        System.out.println("Sdad uploadStarted!");
                                                        fDbActivity.child(FirebaseAuth.getInstance().getUid()).updateChildren(batchMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    CompletedCount +=1;
                                                                    SuccessfulCount += 1;
                                                                    removeActivityFromDb(db, type);
                                                                    checkCompleted();
                                                                } else {
                                                                    CompletedCount += 1;
                                                                    checkCompleted();
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        CompletedCount +=1;
                                                        SuccessfulCount += 1;
                                                        removeActivityFromDb(db, type);
                                                        checkCompleted();
                                                    }
                                                } else {
                                                    CompletedCount += 1;
                                                    checkCompleted();
                                                }
                                            }
                                        });
                                         */
                                    }
                                }else {
                                    CompletedCount += 1;
                                    checkCompleted();
                                }
                                activityClearCursor.moveToNext();
                            }
                        }
                        Cursor commentCursor = db.rawQuery("Select * FROM UploadComments", null);
                        if (commentCursor.moveToFirst()){
                            for (int i = 0; i < commentCursor.getCount(); i++){
                                Executors.newSingleThreadExecutor().execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        Comment comment = new Comment();
                                        comment.setPostId(DatabaseUtils.getLongWithNull(commentCursor, "PostId"));
                                        comment.setReplyID(DatabaseUtils.getLongWithNull(commentCursor, "ReplyId"));
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
                                            jsonObject.put("userId", comment.getUserId());
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
                                        System.out.println("Sdad requestSENT:" + jsonObject);

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



    public void removeVoteFromDb(SQLiteDatabase db, Long PostId, String Vote){
        db.execSQL("UPDATE " + "UploadTasks" +
                " SET " + "Count" + " = " + "Count" + " - 1" +
                " WHERE " + "Type" + " = ?", new String[] {"Vote"});
        db.delete("UploadVotes","PostId=? and Vote=?",new String[]{PostId.toString(),Vote});
    }

    public void removeActivityFromDb(SQLiteDatabase db, String Type){
        db.execSQL("UPDATE " + "UploadTasks" +
                " SET " + "Count" + " = " + "Count" + " - 1" +
                " WHERE " + "Type" + " = ?", new String[] {"ActivityClear"});
        db.delete("UploadActivityClear","Type=?",new String[]{Type});
    }

    public void removeCommentFromDb(SQLiteDatabase db, Comment comment){
        db.execSQL("UPDATE " + "UploadTasks" +
                " SET " + "Count" + " = " + "Count" + " - 1" +
                " WHERE " + "Type" + " = ?", new String[] {"Comment"});
        db.delete("UploadComments","text=? and CreationDate=?",new String[]{comment.getText(), comment.getCreationDate().toString()});
    }

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
