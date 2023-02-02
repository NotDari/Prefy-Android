package com.example.prefy.Popular.PopularViewModel;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.prefy.Popular.PopularPostSet;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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

public class WebPopularDataExecutor {
    private Integer count = 15;
    private PopularPostSet popularPostSet;
    private Boolean userDetailsDone, userVotesDone;
    private Long tempTime, finalTime;
    private RetreivePopularDataInterface delegate;
    private Integer pageNumber;
    private Boolean update;
    private Integer userCompletedCounter = 0;
    private String serverAddress, authToken;
    private OkHttpClient client;

    public WebPopularDataExecutor(RetreivePopularDataInterface delegate, Integer pageNumber, Boolean update) {
        this.delegate = delegate;
        this.pageNumber = pageNumber;
        this.update = update;
    }


    public void initExecutor(){
        tempTime = System.nanoTime();
        userDetailsDone = false;
        userVotesDone = false;
        serverAddress = ServerAdminSingleton.getInstance().getServerAddress();
        authToken = ServerAdminSingleton.getInstance().getServerAuthToken();
        client = new OkHttpClient();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                /**
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("pageNumber", pageNumber);
                    jsonObject.put("limit", count);
                } catch (JSONException e) {
                    delegate.taskComplete(false, null, null);
                }
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                 */
                HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Posts/PopularPosts").newBuilder();
                httpBuilder.addEncodedQueryParameter("pageNumber", pageNumber.toString());
                httpBuilder.addEncodedQueryParameter("limit", count.toString());
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
                            ArrayList<StandardPost> postList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i ++){
                                JSONObject tempObject = jsonArray.getJSONObject(i);
                                StandardPost standardPost = CustomJsonMapper.getPostFromObject(tempObject);
                                postList.add(standardPost);
                            }
                            popularPostSet = new PopularPostSet();
                            popularPostSet.setPostList(postList);
                            getUserDetails();
                            getCurrentUserPostVotes();
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    }else {
                        delegate.taskComplete(false, null, null);
                    }
                } catch (IOException e) {
                    delegate.taskComplete(false, null, null);
                }

            }
        });
    }


    private void getUserDetails(){
        if (popularPostSet.getPostList().size() == 0){
            userDetailsDone = true;
            operationCompleted();
        } else {
            ArrayList<Long> idList = new ArrayList<>();
            for (int i = 0; i < popularPostSet.getPostList().size(); i ++){
                idList.add(popularPostSet.getPostList().get(i).getUserId());
            }
            HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Users/GetUserByIdList").newBuilder();
            httpBuilder.addEncodedQueryParameter("idList", CustomJsonCreator.createArrayStringFromLong(idList));
            Request request = new Request.Builder()
                    .url(httpBuilder.build())
                    .method("GET", null)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", authToken)
                    .build();
            try {
                ArrayList<User> userList = new ArrayList<>();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()){
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        for (int i = 0; i < jsonArray.length(); i ++){
                            JSONObject tempObject = jsonArray.getJSONObject(i);
                            User user = CustomJsonMapper.getUserFromObject(tempObject);
                            userList.add(user);
                        }
                        popularPostSet.setUserList(userList);
                        userDetailsDone = true;
                        operationCompleted();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                }else {
                    delegate.taskComplete(false, null, null);
                }
            } catch (IOException e) {
                delegate.taskComplete(false, null, null);
            }

        }
    }



    private void getCurrentUserPostVotes(){
        JSONObject jsonObject = new JSONObject();
        ArrayList<Long> postIdList = new ArrayList<>();
        for (int i = 0; i < popularPostSet.getPostList().size(); i ++){
            postIdList.add(popularPostSet.getPostList().get(i).getPostId());
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
                            JSONObject tempObject = jsonArray.getJSONObject(i);
                            currentVote = tempObject.getString("currentVote");
                        }
                        popularPostSet.getPostList().get(i).setCurrentVote(currentVote);

                    }

                    userVotesDone = true;
                    operationCompleted();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }else {
                delegate.taskComplete(false, null, null);
            }
        } catch (IOException e) {
            delegate.taskComplete(false, null, null);
        }
    }



    private void operationCompleted(){
        System.out.println("Sdad helop:" + userDetailsDone + userVotesDone);
        if (userDetailsDone  && userVotesDone){
            finalTime = System.nanoTime() - tempTime;
            if (!update) {
                delegate.taskComplete(true, popularPostSet, false);
            } else {
                System.out.println("Sdad firstItem:" + popularPostSet.getPostList().get(0).getQuestion() + " and first poster: " + popularPostSet.getUserList().get(0).getUsername());
                delegate.taskComplete(true, popularPostSet, true);
            }
        }
    }



}
