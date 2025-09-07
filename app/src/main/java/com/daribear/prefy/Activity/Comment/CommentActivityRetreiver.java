package com.daribear.prefy.Activity.Comment;

import com.daribear.prefy.Utils.JsonUtils.CustomJsonCreator;
import com.daribear.prefy.Utils.JsonUtils.CustomJsonMapper;
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

/**
 * The class for retrieving the commentsActivities from the server.
 * Also gets the associated user details of the users who commented on the post and whether the current user is following them.
 */
public class CommentActivityRetreiver implements GetFollowingDelegate {
    private commentRetreiverInterface delegate;
    private ArrayList<CommentActivity> commentActivityList;
    private String serverAddress, authToken;

    private Boolean UsersComplete, UsersFollowingComplete;
    private HashMap<Long, Boolean> followList;

    public CommentActivityRetreiver(commentRetreiverInterface delegate) {
        this.delegate = delegate;
    }

    /**
     * Creates the thread which retrieves a list of commentsActivity.
     */
    public void initExecutor(){
        UsersComplete = false;
        UsersFollowingComplete = false;
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
                        getFollowing();
                    }else {
                        ErrorChecker.checkForStandardError(response);
                        requestfailed();
                    }
                } catch (IOException | JSONException e) {
                    requestfailed();
                }


            }
        });
    }

    /**
     * Get the associated user details for each commentsActivity
     */
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
                delegate.completed(true, commentActivityList);
            }

        }
    }

    /**
     * Creates the following list for each comments Activity.
     * This is whether the logged in user is following each user.
     */
    private void getFollowing(){
        if (commentActivityList != null) {
            ArrayList<Long> userList = new ArrayList<>();
            for (CommentActivity commentActivity : commentActivityList) {
                userList.add(commentActivity.getUserId());
            }
            FollowingRetrieving followingRetrieving = new FollowingRetrieving(userList, this, null);
        }
    }

    /**
     * Creates the final list and alerts the delegate that the process has been completed
     */
    private void completed(){
        if (UsersFollowingComplete && UsersComplete){
            for (Map.Entry<Long, Boolean> entry : followList.entrySet()) {
               Long key = entry.getKey();
               for (int i =0; i < commentActivityList.size(); i ++){
                   if (Objects.equals(commentActivityList.get(i).getUser().getId(), key)){
                       commentActivityList.get(i).getUser().setFollowing(followList.get(key));
                   }
               }
            }
            delegate.completed(true, commentActivityList);
        }
    }

    /**
     * Alerts the delegate that the process has been failed
     */
    private void requestfailed(){
        delegate.completed(false, null);
    }

    /**
     * When the follow retrieval thread has completed, it alerts this class as to whether it was successful or not,
     * and provides the data if it was, using this function.
     * @param successful whether the retrieval was successful
     * @param followList list of user ids and whether the active user is following them
     * @param type retrieval type
     */
    @Override
    public void completed(Boolean successful, HashMap<Long, Boolean> followList, String type) {
        if (successful){
            this.followList = followList;
            UsersFollowingComplete = true;
            completed();
        }
    }
}
