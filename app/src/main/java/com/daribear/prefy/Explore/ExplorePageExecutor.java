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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExplorePageExecutor implements GetFollowingDelegate {
    private String type;
    private ArrayList<FullPost> fullFeaturedPostArrayList;
    private Boolean featuredPostUsers, featuredUsersFollowing,  recentPostUsers, recentUsersFollowing, userVotesDone;
    private ExplorePostSet explorePostSet;
    private ExploreWholeInterface delegate;
    private Integer limitCount, pageNumber;
    private Double lastCreationDate;
    private Boolean update;
    private String serverAddress, authToken;
    private OkHttpClient client;

    private HashMap<Long , Boolean> featuredFollowing, recentFollowing;


    public ExplorePageExecutor(String type, ExploreWholeInterface delegate, Integer limitCount, Integer pageNumber, Boolean update) {
        this.type = type;
        this.delegate = delegate;
        this.limitCount = limitCount;
        this.pageNumber = pageNumber;
        this.update = update;
    }

    public void initExecutor(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                setChecksToFalse();
                if (type.equals("All")) {
                    initFeaturedPosts();
                    initRecentPosts();
                }
            }
        });
    }
    private void setChecksToFalse(){
        featuredPostUsers = false;
        recentPostUsers = false;
        recentUsersFollowing = false;
        featuredUsersFollowing = false;
        userVotesDone = false;
        serverAddress = ServerAdminSingleton.getInstance().getServerAddress();
        authToken = ServerAdminSingleton.getInstance().getServerAuthToken();
        client = new OkHttpClient();
    }


    private void initFeaturedPosts(){
        fullFeaturedPostArrayList = new ArrayList<>();
        HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Posts/FeaturedPosts").newBuilder();
        httpBuilder.addEncodedQueryParameter("pageNumber", "0");
        httpBuilder.addQueryParameter("limit", "7");
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
                    fullFeaturedPostArrayList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i ++){
                        JSONObject tempObject = jsonArray.getJSONObject(i);
                        StandardPost standardPost = CustomJsonMapper.getPostFromObject(tempObject);
                        FullPost fullPost = new FullPost();
                        fullPost.setStandardPost(standardPost);
                        fullFeaturedPostArrayList.add(fullPost);
                    }
                    getFeaturedPostsUsers();
                    getFeaturedPostsFollowing();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }else {
                ErrorChecker.checkForStandardError(response);
                delegate.completed(false, update, explorePostSet, fullFeaturedPostArrayList);
            }
        } catch (IOException | JSONException e) {
            delegate.completed(false, update, explorePostSet, fullFeaturedPostArrayList);
        }


    }




    private void getFeaturedPostsUsers(){

        if (fullFeaturedPostArrayList.size() == 0){
            featuredPostUsers = true;
            operationCompleted();
        } else {
            ArrayList<Long> idList = new ArrayList<>();
            for (int i = 0; i < fullFeaturedPostArrayList.size(); i ++){
                idList.add(fullFeaturedPostArrayList.get(i).getStandardPost().getUserId());
            }
            System.out.println("Sdad oolo:" + idList);
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
                            JSONObject tempObject = jsonArray.getJSONObject(i);
                            User user = CustomJsonMapper.getUserFromObject(tempObject);
                            int[] indexList = IntStream.range(0, fullFeaturedPostArrayList.size())
                                    .filter(f -> fullFeaturedPostArrayList.get(f).getStandardPost().getUserId().equals(user.getId()))
                                    .toArray();
                            for (int z = 0; z < indexList.length; z++) {
                                fullFeaturedPostArrayList.get(indexList[z]).setUser(user);
                            }
                        }
                        featuredPostUsers = true;
                        operationCompleted();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                }else {

                    delegate.completed(false, update, explorePostSet, fullFeaturedPostArrayList);
                }
            } catch (IOException e) {
                delegate.completed(false, update, explorePostSet, fullFeaturedPostArrayList);
            }

        }
    }


    private void getFeaturedPostsFollowing(){
        if (fullFeaturedPostArrayList.size() == 0) {
            featuredUsersFollowing = true;
            operationCompleted();
        } else {
            ArrayList<Long> idList = new ArrayList<>();
            for (int i = 0; i < fullFeaturedPostArrayList.size(); i++) {
                idList.add(fullFeaturedPostArrayList.get(i).getUser().getId());
            }
            FollowingRetrieving followingRetrieving = new FollowingRetrieving(idList, this, "Featured");

        }
    }


    private void initRecentPosts(){
        client = new OkHttpClient();
        ArrayList<FullPost> postList = new ArrayList<>();
        HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Posts/ExploreRecentPosts").newBuilder();
        httpBuilder.addEncodedQueryParameter("limit", limitCount.toString());
        httpBuilder.addEncodedQueryParameter("pageNumber", pageNumber.toString());
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
                    getCurrentUserPostVotes();
                    getRecentPostsUsers();
                    getRecentPostsFollowing();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    delegate.completed(false, update, explorePostSet, null);
                }

            }else {
                delegate.completed(false, update, explorePostSet, fullFeaturedPostArrayList);
            }
        } catch (IOException e) {
            delegate.completed(false, update, explorePostSet, fullFeaturedPostArrayList);
        }
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
                        if (jsonArray.isNull(i)){
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
                delegate.completed(false, update, explorePostSet, fullFeaturedPostArrayList);
            }
        } catch (IOException e) {
            delegate.completed(false, update, explorePostSet, fullFeaturedPostArrayList);
        }
    }


    private void getRecentPostsUsers(){
        ArrayList<Long> idList = new ArrayList<>();
        for (int i = 0; i < explorePostSet.getPostList().size(); i ++){
            idList.add(explorePostSet.getPostList().get(i).getStandardPost().getUserId());
        }
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Users/GetUserByIdList").newBuilder();
        System.out.println("Sdad idList:" + CustomJsonCreator.createArrayStringFromLong(idList));
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
                delegate.completed(false, update, explorePostSet, fullFeaturedPostArrayList);
            }
        } catch (IOException | JSONException e) {
            delegate.completed(false, update, explorePostSet, fullFeaturedPostArrayList);
        }

    }

    private void getRecentPostsFollowing(){
        if (explorePostSet.getPostList().size() == 0) {
            recentUsersFollowing = true;
            operationCompleted();
        } else {
            ArrayList<Long> idList = new ArrayList<>();
            for (int i = 0; i < explorePostSet.getPostList().size(); i ++){
                idList.add(explorePostSet.getPostList().get(i).getStandardPost().getUserId());
            }
            FollowingRetrieving followingRetrieving = new FollowingRetrieving(idList, this, "Recent");

        }
    }



    private void operationCompleted(){
        if (featuredPostUsers && recentPostUsers && userVotesDone && recentUsersFollowing && featuredUsersFollowing){
            if (featuredFollowing != null) {
                for (Map.Entry<Long, Boolean> entry : featuredFollowing.entrySet()) {
                    Long key = entry.getKey();
                    for (int i = 0; i < fullFeaturedPostArrayList.size(); i++) {
                        if (Objects.equals(fullFeaturedPostArrayList.get(i).getUser().getId(), key)) {
                            fullFeaturedPostArrayList.get(i).getUser().setFollowing(featuredFollowing.get(key));
                        }
                    }
                }
            }
            if (recentFollowing != null) {
                for (Map.Entry<Long, Boolean> entry : recentFollowing.entrySet()) {
                    Long key = entry.getKey();
                    for (int i = 0; i < explorePostSet.getPostList().size(); i++) {
                        if (Objects.equals(explorePostSet.getPostList().get(i).getUser().getId(), key)) {
                            explorePostSet.getPostList().get(i).getUser().setFollowing(recentFollowing.get(key));
                        }
                    }
                }
            }
            delegate.completed(true, update, explorePostSet, fullFeaturedPostArrayList);
        }
    }


    @Override
    public void completed(Boolean successful, HashMap<Long, Boolean> followList, String type) {
        if (successful){
            if (type.equals("Featured")){
                this.featuredFollowing = followList;
                featuredUsersFollowing = true;
            } else if (type.equals("Recent")){
                this.recentFollowing = followList;
                recentUsersFollowing = true;
            }else {
                delegate.completed(false, null, null, null);
            }


            operationCompleted();
        } else {
            delegate.completed(false, null, null, null);
        }
    }
}
