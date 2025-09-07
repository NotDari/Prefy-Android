package com.daribear.prefy.Search;

import com.daribear.prefy.Profile.User;
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

/**
 * Retriever class to retrieve the search data. Uses a seperate thread
 */
public class SearchDataRetriever implements GetFollowingDelegate {
    private String text, originalString;
    private ArrayList<User> searchUserArrayList;
    private SearchUsersStringDelegate delegate;
    private Integer limitTo;
    private OkHttpClient client;
    private Integer pageNumber;

    public SearchDataRetriever(String text, Integer pageNumber, SearchUsersStringDelegate delegate, Integer limitTo) {
        this.text = text;
        this.pageNumber = pageNumber;
        this.delegate = delegate;
        this.limitTo = limitTo;
    }

    /**
     * Creates the thread which contacts the endpoint for getting the search data.
     */
    public void initExecutor(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                        searchUserArrayList = new ArrayList<>();

                        client = new OkHttpClient();
                        HttpUrl.Builder httpBuilder = HttpUrl.parse(ServerAdminSingleton.getInstance().getServerAddress() + "/prefy/v1/Users/UserSearch").newBuilder();
                        httpBuilder.addPathSegment(text);
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
                                        JSONObject tempObject = jsonArray.getJSONObject(i);
                                        if (tempObject != null){
                                            User user = CustomJsonMapper.getUserFromObject(tempObject);
                                            searchUserArrayList.add(user);
                                        }
                                    }
                                    ArrayList<Long> idList = new ArrayList<>();
                                    for (User user : searchUserArrayList){
                                        idList.add(user.getId());
                                    }
                                    FollowingRetrieving followingRetrieving = new FollowingRetrieving(idList, SearchDataRetriever.this::completed, null);
                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }

                            }else {
                                ErrorChecker.checkForStandardError(response);
                                delegate.stringCompleted(false, false, null, null);
                            }
                        } catch (IOException | JSONException e) {
                            delegate.stringCompleted(false, false, null, null);
                        }
            }
        });
    }

    /**
     * The following list has been compeleted.
     * @param successful whether the retrieval was successful
     * @param followList list of user ids and whether the active user is following them
     * @param type retrieval type
     */
    @Override
    public void completed(Boolean successful, HashMap<Long, Boolean> followList, String type) {
        if (successful) {
            operationCompleted(followList);

        } else {
            delegate.stringCompleted(false, false, null, null);
        }
    }

    /**
     * The operation has been completed so return
     */
    private void operationCompleted(HashMap<Long, Boolean> followList){
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
        delegate.stringCompleted(true, update, text,  searchUserArrayList);
    }


}
