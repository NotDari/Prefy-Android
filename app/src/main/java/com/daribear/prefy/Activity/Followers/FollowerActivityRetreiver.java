package com.daribear.prefy.Activity.Followers;

import com.daribear.prefy.Activity.Comment.CommentActivity;
import com.daribear.prefy.Activity.Comment.commentRetreiverInterface;
import com.daribear.prefy.Utils.CustomJsonCreator;
import com.daribear.prefy.Utils.CustomJsonMapper;
import com.daribear.prefy.Utils.DefaultCreator;
import com.daribear.prefy.Utils.ErrorChecker;
import com.daribear.prefy.Utils.GetFollowing.FollowingRetrieving;
import com.daribear.prefy.Utils.GetFollowing.GetFollowingDelegate;
import com.daribear.prefy.Utils.ServerAdminSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FollowerActivityRetreiver implements GetFollowingDelegate {
    private followerRetrieverInterface delegate;
    private ArrayList<FollowerActivity> followerActivityList;
    private String serverAddress, authToken;

    private Boolean UsersComplete, UsersFollowingComplete;
    private HashMap<Long, Boolean> followList;

    public FollowerActivityRetreiver(followerRetrieverInterface delegate) {
        this.delegate = delegate;
    }

    public void initExecutor(){
        UsersComplete = false;
        UsersFollowingComplete = false;
        serverAddress = ServerAdminSingleton.getInstance().getServerAddress();
        authToken = ServerAdminSingleton.getInstance().getServerAuthToken();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("Sdad yoo");
                OkHttpClient client = new OkHttpClient();
                HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Activity/FollowersActivity").newBuilder();
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
                        followerActivityList = CustomJsonMapper.getFollowerActivityList(response);
                        getUserDetails();
                        getFollowing();
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
        if (followerActivityList != null) {
            ArrayList<Long> userList = new ArrayList<>();
            for (FollowerActivity followerActivity : followerActivityList) {
                userList.add(followerActivity.getUserId());
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
                                        followerActivityList.get(i).setUser(CustomJsonMapper.getUserFromObject(tempObject));
                                    }
                                } else {
                                    followerActivityList.get(i).setUser(DefaultCreator.createBlankUser());
                                }
                            }
                            UsersComplete = true;
                            completed();
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
                delegate.followerCompleted(true, followerActivityList);
            }

        }
    }

    private void getFollowing(){
        if (followerActivityList != null) {
            ArrayList<Long> userList = new ArrayList<>();
            for (FollowerActivity followerActivity : followerActivityList) {
                userList.add(followerActivity.getUserId());
            }
            FollowingRetrieving followingRetrieving = new FollowingRetrieving(userList, this, null);
        }
    }

    private void completed(){
        if (UsersFollowingComplete && UsersComplete){
            for (Map.Entry<Long, Boolean> entry : followList.entrySet()) {
               Long key = entry.getKey();
               for (int i =0; i < followerActivityList.size(); i ++){
                   if (Objects.equals(followerActivityList.get(i).getUser().getId(), key)){
                       followerActivityList.get(i).getUser().setFollowing(followList.get(key));
                   }
               }
            }
            delegate.followerCompleted(true, followerActivityList);
        }
    }

    private void requestfailed(){
        delegate.followerCompleted(false, null);
    }

    @Override
    public void completed(Boolean successful, HashMap<Long, Boolean> followList, String type) {
        if (successful){
            this.followList = followList;
            UsersFollowingComplete = true;
            completed();
        }
    }
}
