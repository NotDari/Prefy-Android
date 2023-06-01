package com.daribear.prefy.Explore.ExploreCategories.ExploreCategoriesPosts;

import com.daribear.prefy.Explore.ExplorePostSet;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.Utils.JsonUtils.CustomJsonCreator;
import com.daribear.prefy.Utils.JsonUtils.CustomJsonMapper;
import com.daribear.prefy.Utils.DefaultCreator;
import com.daribear.prefy.Utils.ErrorChecker;
import com.daribear.prefy.Utils.GetFollowing.FollowingRetrieving;
import com.daribear.prefy.Utils.GetFollowing.GetFollowingDelegate;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.daribear.prefy.customClasses.Posts.FullPost;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExploreCategoriesRetreiver implements GetFollowingDelegate {
    private ExploreCategoryInterface delegate;
    //private Long lastCreationDate;
    private Integer pageNumber;
    private String categoryChoice;
    private Integer limitCount;
    private ExplorePostSet explorePostSet;
    private DatabaseReference mDatabase;
    private String serverAddress, authToken;

    private Boolean usersDone = false, votesDone = false, followingDone = false;

    private HashMap<Long, Boolean> followList;


    public ExploreCategoriesRetreiver(ExploreCategoryInterface delegate, Integer pageNumber, String categoryChoice, Integer limitCount) {
        this.delegate = delegate;
        this.pageNumber = pageNumber;
        this.categoryChoice = categoryChoice;
        this.limitCount = limitCount;
        serverAddress = ServerAdminSingleton.getInstance().getServerAddress();
        authToken = ServerAdminSingleton.getInstance().getServerAuthToken();
    }


    public void initExecutor(){
        getPosts();
    }



    private void getPosts(){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<FullPost> postList = new ArrayList<>();
                OkHttpClient client = new OkHttpClient();
                HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Posts/PostListByCategory").newBuilder();
                httpBuilder.addEncodedQueryParameter("category", categoryChoice);
                if (pageNumber == null){
                    pageNumber = 0;
                } else {
                    pageNumber += 1;
                }
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
                    System.out.println("Sdad oadsia" + response.isSuccessful());
                    if (response.isSuccessful()){
                        try {
                            JSONArray jsonArray = new JSONArray(response.body().string());
                            System.out.println("Sdad uwu:" + jsonArray.length());
                            for (int i = 0; i < jsonArray.length(); i ++){
                                JSONObject tempObject = jsonArray.getJSONObject(i);
                                FullPost fullPost = new FullPost();
                                fullPost.setStandardPost(CustomJsonMapper.getPostFromObject(tempObject));
                                postList.add(fullPost);
                            }
                            explorePostSet = new ExplorePostSet();
                            explorePostSet.setPostList(postList);
                            getUsers();
                            getUserCurrentVotes();
                            getUsersFollowing();
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    }else {
                        ErrorChecker.checkForStandardError(response);
                        delegate.Completed(false, null);
                    }
                } catch (IOException | JSONException e) {
                    delegate.Completed(false, null);
                }


            }
        });



    }


    private void getUsers(){
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
                    votesDone = true;
                    operationCompleted();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }else {
                ErrorChecker.checkForStandardError(response);
                delegate.Completed(false, null);
            }
        } catch (IOException | JSONException e) {
            delegate.Completed(false, null);
        }

    }


    private void getUserCurrentVotes(){
        ArrayList<Long> postIdList = new ArrayList<>();
        for (int i = 0; i < explorePostSet.getPostList().size(); i ++){
            postIdList.add(explorePostSet.getPostList().get(i).getStandardPost().getPostId());
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
                        explorePostSet.getPostList().get(i).getStandardPost().setCurrentVote(currentVote);

                    }
                    usersDone = true;
                    operationCompleted();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }else {
                ErrorChecker.checkForStandardError(response);
                delegate.Completed(false, null);
            }
        } catch (IOException | JSONException e) {
            delegate.Completed(false, null);
        }
    }

    private void getUsersFollowing(){
        if (explorePostSet.getPostList().size() == 0){
            followingDone = true;
            operationCompleted();
        } else {
            ArrayList<Long> idList = new ArrayList<>();
            for (int i = 0; i < explorePostSet.getPostList().size(); i++) {
                idList.add(explorePostSet.getPostList().get(i).getUser().getId());
            }
            FollowingRetrieving followingRetrieving = new FollowingRetrieving(idList, this, null);
        }
    }


    private void operationCompleted(){
        if (usersDone && votesDone && followingDone) {
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
            delegate.Completed(true, explorePostSet);
        }
    }

    @Override
    public void completed(Boolean successful, HashMap<Long, Boolean> followList, String type) {
        if (successful){
            followingDone = true;
            this.followList = followList;
            operationCompleted();
        }else {
            delegate.Completed(false, null);
        }
    }
}
