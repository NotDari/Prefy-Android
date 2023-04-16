package com.daribear.prefy.Search;

import com.daribear.prefy.Profile.User;
import com.daribear.prefy.Utils.CustomJsonMapper;
import com.daribear.prefy.Utils.ErrorChecker;
import com.daribear.prefy.Utils.ServerAdminSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchDataRetriever {
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
                                    operationCompleted(searchUserArrayList);
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

                        /**
                        Query query = ff.collection("Users").orderBy("username").startAt(text).limit(limitTo);
                        query = query.endAt(originalString + "\uf8ff");
                        if (update){
                            //query = query.endAt(originalString + "\uf8ff");
                        }
                        query.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    System.out.println("Sdad search completed" + task.getResult().size());
                                    if (task.getResult().getDocuments().size() == 0) {
                                        delegate.stringCompleted(true, update,text, new ArrayList<>());
                                    } else {
                                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                                            User user = FirebaseUtils.retreiveUser(snapshot);
                                            userArrayList.add(user);
                                        }
                                        operationCompleted(userArrayList);
                                    }

                                } else{
                                    delegate.stringCompleted(false, update, null, null);
                                }
                            }
                        });
                         */
            }
        });
    }



    private void operationCompleted(ArrayList<User> userArrayList){
        Boolean update;
        update = (pageNumber > 0);
        delegate.stringCompleted(true, update, text,  userArrayList);
    }
}
