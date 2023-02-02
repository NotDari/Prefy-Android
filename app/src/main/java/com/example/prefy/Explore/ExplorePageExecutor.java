package com.example.prefy.Explore;

import androidx.annotation.NonNull;

import com.example.prefy.Profile.User;
import com.example.prefy.Utils.CustomJsonCreator;
import com.example.prefy.Utils.CustomJsonMapper;
import com.example.prefy.Utils.FirebaseUtils;
import com.example.prefy.Utils.ServerAdminSingleton;
import com.example.prefy.customClasses.FullPost;
import com.example.prefy.customClasses.StandardPost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ExplorePageExecutor {
    private String type;
    private ArrayList<FullPost> fullFeaturedPostArrayList;
    private Boolean featuredPostUsers, recentPostUsers, userVotesDone;
    private ExplorePostSet explorePostSet;
    private ExploreWholeInterface delegate;
    private Integer limitCount, pageNumber;
    private Double lastCreationDate;
    private Boolean update;
    private String serverAddress, authToken;
    private OkHttpClient client;

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




    private void getFeaturedPostsUsers(){
        System.out.println("Sdad adsad!" + fullFeaturedPostArrayList.size());
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
                        JSONObject tempObject = jsonArray.getJSONObject(i);
                        User user = CustomJsonMapper.getUserFromObject(tempObject);
                        int[] indexList = IntStream.range(0, explorePostSet.getPostList().size())
                                .filter(f -> explorePostSet.getPostList().get(f).getStandardPost().getUserId().equals(user.getId()))
                                .toArray();
                        for (int z = 0; z < indexList.length; z++) {
                            explorePostSet.getPostList().get(indexList[z]).setUser(user);
                        }

                    }
                    recentPostUsers = true;
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



    private void operationCompleted(){
        System.out.println("Sdad awooga:" + featuredPostUsers + recentPostUsers + userVotesDone);
        if (featuredPostUsers && recentPostUsers && userVotesDone){
            delegate.completed(true, update, explorePostSet, fullFeaturedPostArrayList);
        }
    }




}
