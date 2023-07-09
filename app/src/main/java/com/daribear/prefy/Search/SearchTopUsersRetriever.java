package com.daribear.prefy.Search;

import com.daribear.prefy.Profile.User;
import com.daribear.prefy.Utils.DefaultCreator;
import com.daribear.prefy.Utils.JsonUtils.CustomJsonMapper;
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

public class SearchTopUsersRetriever implements GetFollowingDelegate {
    private Integer limitTo;
    private Long endAt;
    private ArrayList<User> searchUserArrayList;
    private SearchUsersTopDelegate delegate;
    private Integer pageNumber;


    public SearchTopUsersRetriever(Integer pageNumber, Integer limitTo, SearchUsersTopDelegate delegate) {
        this.pageNumber = pageNumber;
        this.limitTo = limitTo;
        this.delegate = delegate;
    }

    public void initExecutor(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("Sdad following starte:");
                searchUserArrayList = new ArrayList<>();
                OkHttpClient client = new OkHttpClient();
                HttpUrl.Builder httpBuilder = HttpUrl.parse(ServerAdminSingleton.getInstance().getServerAddress() + "/prefy/v1/Users/TopUsers").newBuilder();
                httpBuilder.addEncodedQueryParameter("pageNumber", pageNumber.toString());
                httpBuilder.addEncodedQueryParameter("limit", limitTo.toString());
                Request request = new Request.Builder()
                        .url(httpBuilder.build())
                        .method("GET", null)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", ServerAdminSingleton.getInstance().getServerAuthToken())
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        try {
                            JSONArray jsonArray = new JSONArray(response.body().string());
                            for (int i = 0; i < jsonArray.length(); i ++){
                                if (!jsonArray.isNull(i)) {
                                    JSONObject tempObject = jsonArray.getJSONObject(i);
                                    if (tempObject != null) {
                                        User user = CustomJsonMapper.getUserFromObject(tempObject);
                                        searchUserArrayList.add(user);
                                    }
                                } else {
                                    User user = DefaultCreator.createBlankUser();
                                    searchUserArrayList.add(user);
                                }
                            }
                            ArrayList<Long> idList = new ArrayList<>();
                            for (User user : searchUserArrayList){
                                idList.add(user.getId());
                            }
                            System.out.println("Sdad followingReady");
                            FollowingRetrieving followingRetrieving = new FollowingRetrieving(idList, SearchTopUsersRetriever.this::completed, null);
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    }else {
                        ErrorChecker.checkForStandardError(response);
                        delegate.topCompleted(false, false, null);
                    }
                } catch (IOException | JSONException e) {
                    delegate.topCompleted(false, false, null);
                }

            }
        });
    }





    private void completed(HashMap<Long, Boolean> followList){
        for (Map.Entry<Long, Boolean> entry : followList.entrySet()) {
            Long key = entry.getKey();
            for (int i =0; i < searchUserArrayList.size(); i ++){
                if (Objects.equals(searchUserArrayList.get(i).getId(), key)){
                    searchUserArrayList.get(i).setFollowing(followList.get(key));
                }
            }
        }
        Boolean update;
        update = (pageNumber > 0);
        delegate.topCompleted(true, update,searchUserArrayList);
    }

    @Override
    public void completed(Boolean successful, HashMap<Long, Boolean> followList, String type) {
        if (successful){
            completed(followList);
        } else {
            delegate.topCompleted(false, null, null);
        }
    }
}
