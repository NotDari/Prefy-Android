package com.daribear.prefy.Popular;

import com.daribear.prefy.customClasses.Posts.FullPost;
import com.daribear.prefy.customClasses.Posts.PopularPost;
import com.daribear.prefy.Popular.PopularPostSet;
import com.daribear.prefy.Popular.PopularViewModel.RetreivePopularDataInterface;
import com.daribear.prefy.Profile.User;
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
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebDataRetriever implements GetFollowingDelegate {
    Integer count = 10;

    private ArrayList<FullPost> fullPostList;

    private RetreivePopularDataInterface delegate;

    private String serverAddress, authToken;
    private OkHttpClient client;

    private ArrayList<Long> avoidList;
    private Boolean userDetailsDone = false, userFollowing = false;
    private String retrievalType;
    private HashMap<Long, Boolean> followList;




    public WebDataRetriever(RetreivePopularDataInterface delegate, ArrayList<Long> avoidList, String retrievalType) {
        this.delegate = delegate;
        this.avoidList = avoidList;
        this.retrievalType = retrievalType;
    }


    public void initExec(){
        serverAddress = ServerAdminSingleton.getInstance().getServerAddress();
        authToken = ServerAdminSingleton.getInstance().getServerAuthToken();
        client = new OkHttpClient();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if (ServerAdminSingleton.getInstance().getLoggedInId() != null) {
                    HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Posts/PopularPosts").newBuilder();
                    if (avoidList.size() == 0) {
                        avoidList.add(-1L);
                    }
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < avoidList.size(); i++) {
                        array.put(avoidList.get(i));
                    }
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("limit", count.toString());
                        jsonObject.put("userId", ServerAdminSingleton.getInstance().getLoggedInId().toString());
                        jsonObject.put("ignoreList", array);
                    } catch (JSONException e) {
                        delegate.taskComplete(false, null, null);
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
                        if (response.isSuccessful()) {
                            try {
                                JSONArray jsonArray = new JSONArray(response.body().string());
                                fullPostList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject tempObject = jsonArray.getJSONObject(i);
                                    FullPost fullPost = new FullPost();
                                    PopularPost popularPost = CustomJsonMapper.getPopularPostFromObject(tempObject);
                                    popularPost.setCurrentVote("none");
                                    fullPost.setStandardPost(popularPost);
                                    fullPostList.add(fullPost);
                                }


                                getUserDetails();
                                getUserFollowing();
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            ErrorChecker.checkForStandardError(response);
                            delegate.taskComplete(false, null, null);
                        }
                    } catch (IOException | JSONException e) {
                        delegate.taskComplete(false, null, null);
                    }
                } else {
                    delegate.taskComplete(false, null, null);
                }
            }
        });
    }

    private void getUserDetails(){
        if (fullPostList.size() == 0){
            userDetailsDone = true;
            operationCompleted();
        } else {
            ArrayList<Long> idList = new ArrayList<>();
            for (int i = 0; i < fullPostList.size(); i ++){
                idList.add(fullPostList.get(i).getStandardPost().getUserId());
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
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()){
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        for (int i = 0; i < jsonArray.length(); i ++) {
                            User user;
                            if (!jsonArray.isNull(i)){
                                JSONObject tempObject = jsonArray.getJSONObject(i);
                                user = CustomJsonMapper.getUserFromObject(tempObject);
                            } else {
                                user = DefaultCreator.createBlankUser();
                            }
                            for (int f = 0; f < fullPostList.size(); f++){
                                if (fullPostList.get(f).getStandardPost().getUserId().equals(user.getId())){
                                    fullPostList.get(i).setUser(user);
                                }
                            }


                        }
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



    private void getUserFollowing(){
        if (fullPostList.size() == 0){
            userFollowing = true;
            operationCompleted();
        } else {
            ArrayList<Long> idList = new ArrayList<>();
            for (int i = 0; i < fullPostList.size(); i++) {
                idList.add(fullPostList.get(i).getUser().getId());
            }
            FollowingRetrieving followingRetrieving = new FollowingRetrieving(idList, this, null);
        }
    }

    private void operationCompleted(){
        if (userDetailsDone  && userFollowing){
            if (followList != null) {
                for (Map.Entry<Long, Boolean> entry : followList.entrySet()) {
                    Long key = entry.getKey();
                    for (int i = 0; i < fullPostList.size(); i++) {
                        if (Objects.equals(fullPostList.get(i).getUser().getId(), key)) {
                            fullPostList.get(i).getUser().setFollowing(followList.get(key));
                        }
                    }
                }
            }
            delegate.taskComplete(true, fullPostList, retrievalType);
        }
    }

    @Override
    public void completed(Boolean successful, HashMap<Long, Boolean> followList, String type) {
        if (successful){
            userFollowing = true;
            this.followList = followList;
            operationCompleted();
        }else {
            delegate.taskComplete(false, null, null);
        }
    }
}
