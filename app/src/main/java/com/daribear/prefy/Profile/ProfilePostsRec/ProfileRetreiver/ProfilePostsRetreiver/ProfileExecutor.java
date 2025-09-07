package com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.ProfilePostsRetreiver;


import com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.WholeProfile;
import com.daribear.prefy.Utils.JsonUtils.CustomJsonCreator;
import com.daribear.prefy.Utils.JsonUtils.CustomJsonMapper;
import com.daribear.prefy.Utils.ErrorChecker;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.daribear.prefy.customClasses.Containers.PostListContainer;
import com.daribear.prefy.customClasses.Posts.StandardPost;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * The executor class which attempts to receive a specific user's posts from the database
 */
public class ProfileExecutor {
    private Long id;
    private ProfileHandlerInt delegate;
    private ArrayList<StandardPost> postList;
    private Boolean update;
    private Integer limit;
    private Integer pageNumber;

    private String serverAddress, authToken;


    public ProfileExecutor(Long id, ProfileHandlerInt delegate, Integer limit, Integer pageNumber, Boolean update) {
        this.id = id;
        this.delegate = delegate;
        this.limit = limit;
        this.update = update;
        this.pageNumber = pageNumber;
    }

    /**
     * Creates a thread which attempts to receive the post list from a specific user from the database
     */
    public void initExecutor(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                serverAddress = ServerAdminSingleton.getInstance().getServerAddress();
                authToken = ServerAdminSingleton.getInstance().getServerAuthToken();
                OkHttpClient client = new OkHttpClient();

                HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Posts/PostListById").newBuilder();
                httpBuilder.addEncodedQueryParameter("id", id.toString());
                if (!update) {
                    pageNumber = 0;
                } else {
                    pageNumber += 1;
                }
                httpBuilder.addEncodedQueryParameter("pageNumber", pageNumber.toString());
                httpBuilder.addEncodedQueryParameter("limit", limit.toString());
                Request request = new Request.Builder()
                        .url(httpBuilder.build())
                        .method("GET", null)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", authToken)
                        .build();
                postList = new ArrayList<>();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        postList = CustomJsonMapper.getPostListFromResponse(response);
                        getUserCurrentVotes();
                    }else {
                        ErrorChecker.checkForStandardError(response);
                        delegate.taskDone(false, null);
                    }
                } catch (IOException | JSONException e) {
                    delegate.taskDone(false, null);
                }
            }
        });
    }

    /**
     * Get the list of the current logged in user's votes on the posts.
     */
    private void getUserCurrentVotes(){
        ArrayList<Long> postIdList = new ArrayList<>();
        for (int i = 0; i < postList.size(); i ++){
            postIdList.add(postList.get(i).getPostId());
        }
        HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Votes/VoteList").newBuilder();
        httpBuilder.addEncodedQueryParameter("postIdList", CustomJsonCreator.createArrayStringFromLong(postIdList));
        httpBuilder.addEncodedQueryParameter("userId", ServerAdminSingleton.getInstance().getLoggedInId().toString());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .method("GET", null)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", authToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    for (int i = 0; i < jsonArray.length(); i ++){
                        String currentVote;
                        if (jsonArray.isNull(i)){
                            currentVote = "none";
                        } else {
                            currentVote = jsonArray.getJSONObject(i).getString("currentVote");
                        }
                        postList.get(i).setCurrentVote(currentVote);

                    }
                    OperationCompleted();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }else {
                ErrorChecker.checkForStandardError(response);
                delegate.taskDone(false, null);
            }
        } catch (IOException | JSONException e) {
            delegate.taskDone(false, null);
        }
    }


    /**
     * Called when the operation is completed.
     * Tells the delegate it is complete.
     */
    private void OperationCompleted(){
        WholeProfile wholeProfile = new WholeProfile();
        PostListContainer postListContainer = new PostListContainer();
        postListContainer.setPostList(postList);
        postListContainer.setPageNumber(pageNumber);
        wholeProfile.setPostListContainer(postListContainer);
        delegate.taskDone(true, wholeProfile);
    }
}
