package com.daribear.prefy.Activity.Comment;

import com.daribear.prefy.Activity.Votes.VoteActivity;
import com.daribear.prefy.Utils.CustomJsonCreator;
import com.daribear.prefy.Utils.CustomJsonMapper;
import com.daribear.prefy.Utils.DefaultCreator;
import com.daribear.prefy.Utils.ErrorChecker;
import com.daribear.prefy.Utils.ServerAdminSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommentActivityRetreiver {
    private commentRetreiverInterface delegate;
    private ArrayList<CommentActivity> commentActivityList;
    private String serverAddress, authToken;

    public CommentActivityRetreiver(commentRetreiverInterface delegate) {
        this.delegate = delegate;
    }

    public void initExecutor(){
        serverAddress = ServerAdminSingleton.getInstance().getServerAddress();
        authToken = ServerAdminSingleton.getInstance().getServerAuthToken();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Activity/CommentsActivity").newBuilder();
                httpBuilder.addEncodedQueryParameter("pageNumber", "0");
                Request request = new Request.Builder()
                        .url(httpBuilder.build())
                        .method("GET", null)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", authToken)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        commentActivityList = CustomJsonMapper.getCommentActivityList(response);
                        getUserDetails();

                    }else {
                        ErrorChecker.checkForStandardError(response);
                        requestfailed();
                    }
                } catch (IOException | JSONException e) {
                    requestfailed();
                }


                /**
                DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference();
                fDatabase.child("activity").child(uid).child("comments").orderByChild("creationDate").limitToLast(10).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            for (DataSnapshot dataValues : task.getResult().getChildren()){
                                CommentActivity commentActivity = dataValues.getValue(CommentActivity.class);
                                commentActivityList.add(commentActivity);
                            }
                            Collections.reverse(commentActivityList);
                            getUserDetails(commentActivityList);
                        } else{
                            delegate.completed(false, null);
                        }
                    }
                });
                 */
            }
        });
    }
    private void getUserDetails() {
        if (commentActivityList != null) {
            ArrayList<Long> userList = new ArrayList<>();
            for (CommentActivity commentActivity : commentActivityList) {
                userList.add(commentActivity.getUserId());
            }
            if (userList.size() > 0) {
                OkHttpClient client = new OkHttpClient();
                HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Users/GetUserByIdList").newBuilder();
                httpBuilder.addEncodedQueryParameter("idList", CustomJsonCreator.createArrayStringFromLong(userList));
                Request request = new Request.Builder()
                        .url(httpBuilder.build())
                        .method("GET", null)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", authToken)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        try {
                            JSONArray jsonArray = new JSONArray(response.body().string());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (!jsonArray.isNull(i)) {
                                    JSONObject tempObject = jsonArray.getJSONObject(i);
                                    if (tempObject != null) {
                                        commentActivityList.get(i).setUser(CustomJsonMapper.getUserFromObject(tempObject));
                                    }
                                } else {
                                    commentActivityList.get(i).setUser(DefaultCreator.createBlankUser());
                                }
                            }
                            delegate.completed(true, commentActivityList);
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        ErrorChecker.checkForStandardError(response);
                        requestfailed();
                    }
                } catch (IOException | JSONException e) {
                    requestfailed();
                }
            } else {
                delegate.completed(true, commentActivityList);
            }

        }
    }

    private void requestfailed(){
        delegate.completed(false, null);
    }
}
