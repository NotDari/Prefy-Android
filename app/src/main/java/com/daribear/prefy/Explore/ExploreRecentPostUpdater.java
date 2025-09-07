package com.daribear.prefy.Explore;

import com.daribear.prefy.Profile.User;
import com.daribear.prefy.Utils.JsonUtils.CustomJsonCreator;
import com.daribear.prefy.Utils.JsonUtils.CustomJsonMapper;
import com.daribear.prefy.Utils.DefaultCreator;
import com.daribear.prefy.Utils.ErrorChecker;
import com.daribear.prefy.Utils.GetFollowing.FollowingRetrieving;
import com.daribear.prefy.Utils.GetFollowing.GetFollowingDelegate;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.daribear.prefy.customClasses.Posts.FullPost;
import com.daribear.prefy.customClasses.Posts.StandardPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.IntStream;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 */
public class ExploreRecentPostUpdater implements GetFollowingDelegate {
    private Integer pageNumber;
    private Integer limitCount;
    private ExploreRecentUpdateInterface delegate;
    private ExplorePostSet explorePostSet;
    private Boolean recentPostUsers, userVotesDone, userFollowingDone;
    private String serverAddress, authToken;
    private OkHttpClient client;

    private HashMap<Long, Boolean> followList;
    private ThreadPoolExecutor executor;

    public ExploreRecentPostUpdater(Integer limitCount, Integer pageNumber, ExploreRecentUpdateInterface delegate) {
        this.limitCount = limitCount;
        this.pageNumber = pageNumber;
        this.delegate = delegate;
    }


    public void initExecutor(){
        recentPostUsers = false;
        userVotesDone = false;
        userFollowingDone = false;
        serverAddress = ServerAdminSingleton.getInstance().getServerAddress();
        authToken = ServerAdminSingleton.getInstance().getServerAuthToken();
        initRecentPosts();
    }

    private void initRecentPosts(){
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                client = new OkHttpClient();
                ArrayList<FullPost> postList = new ArrayList<>();
                HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Posts/ExploreRecentPosts").newBuilder();
                httpBuilder.addEncodedQueryParameter("pageNumber", pageNumber.toString());
                httpBuilder.addEncodedQueryParameter("limit", limitCount.toString());
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
                                JSONObject tempObject = jsonArray.getJSONObject(i);
                                if (tempObject != null){
                                    StandardPost post = CustomJsonMapper.getPostFromObject(tempObject);
                                    FullPost fullPost = new FullPost();
                                    fullPost.setStandardPost(post);
                                    postList.add(fullPost);
                                }
                            }
                            explorePostSet = new ExplorePostSet();
                            explorePostSet.setPostList(postList);
                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    getCurrentUserPostVotes();
                                }
                            });
                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    getRecentPostsUsers();
                                }
                            });
                            executor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    getUsersFollowing();
                                }
                            });

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    }else {
                        ErrorChecker.checkForStandardError(response);
                        delegate.completed(false, null);
                    }
                } catch (IOException | JSONException e) {
                    delegate.completed(false, null);
                }
            }
        });

    }

    private void getCurrentUserPostVotes(){
        ArrayList<Long> postIdList = new ArrayList<>();
        for (int i = 0; i < explorePostSet.getPostList().size(); i ++){
            postIdList.add(explorePostSet.getPostList().get(i).getStandardPost().getPostId());
        }
        HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Votes/VoteList").newBuilder();
        httpBuilder.addEncodedQueryParameter("postIdList", CustomJsonCreator.createArrayStringFromLong(postIdList));
        httpBuilder.addEncodedQueryParameter("userId", ServerAdminSingleton.getInstance().getLoggedInId().toString());
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
                        if  (jsonArray.isNull(i)){
                            currentVote = "none";
                        } else {
                            currentVote = jsonArray.getJSONObject(i).getString("currentVote");
                        }
                        explorePostSet.getPostList().get(i).getStandardPost().setCurrentVote(currentVote);

                    }
                    userVotesDone = true;
                    operationCompleted();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }else {
                ErrorChecker.checkForStandardError(response);
                delegate.completed(false, null);
            }
        } catch (IOException | JSONException e) {
            delegate.completed(false, null);
        }

    }

    /**
     * Gets the list of the users of the recent posts previously retrieved.
     */
    private void getRecentPostsUsers(){
        ArrayList<Long> idList = new ArrayList<>();
        for (int i = 0; i < explorePostSet.getPostList().size(); i ++){
            idList.add(explorePostSet.getPostList().get(i).getStandardPost().getUserId());
        }
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Users/GetUserByIdList").newBuilder();
        httpBuilder.addEncodedQueryParameter("idList", CustomJsonCreator.createArrayStringFromLong(idList));
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
                        if (!jsonArray.isNull(i)) {
                            JSONObject tempObject = jsonArray.getJSONObject(i);
                            User user = CustomJsonMapper.getUserFromObject(tempObject);
                            int[] indexList = IntStream.range(0, explorePostSet.getPostList().size())
                                    .filter(f -> explorePostSet.getPostList().get(f).getStandardPost().getUserId().equals(user.getId()))
                                    .toArray();
                            for (int z = 0; z < indexList.length; z++) {
                                explorePostSet.getPostList().get(indexList[z]).setUser(user);
                            }
                        } else {
                            explorePostSet.getPostList().get(i).setUser(DefaultCreator.createBlankUser());
                        }
                    }
                    recentPostUsers = true;
                    operationCompleted();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }else {
                ErrorChecker.checkForStandardError(response);
                delegate.completed(false, null);
            }
        } catch (IOException | JSONException e) {
            delegate.completed(false, null);
        }

    }

    /**
     * Gets whether the user is following the
     */
    private void getUsersFollowing(){
        ArrayList<Long> idList = new ArrayList<>();
        for (int i = 0; i < explorePostSet.getPostList().size(); i ++){
            idList.add(explorePostSet.getPostList().get(i).getStandardPost().getUserId());
        }
        FollowingRetrieving followingRetrieving = new FollowingRetrieving(idList, ExploreRecentPostUpdater.this, null);
    }
    private synchronized void operationCompleted(){
        if (recentPostUsers && userVotesDone && userFollowingDone){
            if (followList != null) {
                for (Map.Entry<Long, Boolean> entry : followList.entrySet()) {
                    Long key = entry.getKey();
                    for (int i = 0; i < explorePostSet.getPostList().size(); i++) {
                        if (Objects.equals(explorePostSet.getPostList().get(i).getUser().getId(), key)) {
                            explorePostSet.getPostList().get(i).getUser().setFollowing(followList.get(key));
                        }
                    }
                }
            }
            delegate.completed(true, explorePostSet);
        }
    }

    @Override
    public void completed(Boolean successful, HashMap<Long, Boolean> followList, String type) {
        if (successful){
            userFollowingDone = true;
            this.followList = followList;
            operationCompleted();
        } else {
            delegate.completed(false, null);
        }
    }
}
