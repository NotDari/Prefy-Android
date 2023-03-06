package com.example.prefy.Explore.ExploreCategories.ExploreCategoriesPosts;

import androidx.annotation.NonNull;

import com.example.prefy.Comments.FullComment;
import com.example.prefy.Explore.ExplorePostSet;
import com.example.prefy.Profile.User;
import com.example.prefy.Utils.CustomJsonCreator;
import com.example.prefy.Utils.CustomJsonMapper;
import com.example.prefy.Utils.FirebaseUtils;
import com.example.prefy.Utils.ServerAdminSingleton;
import com.example.prefy.customClasses.FullPost;
import com.example.prefy.customClasses.StandardPost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import io.grpc.Server;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ExploreCategoriesRetreiver {
    private ExploreCategoryInterface delegate;
    //private Long lastCreationDate;
    private Integer pageNumber;
    private String categoryChoice;
    private Integer limitCount;
    private ExplorePostSet explorePostSet;
    private DatabaseReference mDatabase;
    private String serverAddress, authToken;


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
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    }else {
                        delegate.Completed(false, null);
                    }
                } catch (IOException e) {
                    delegate.Completed(false, null);
                }


                //"\uf8ff" is one of the last characters in Unicode so by ending at this, we get all the posts with just the desired uid
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
                        JSONObject tempObject = jsonArray.getJSONObject(i);
                        User user = CustomJsonMapper.getUserFromObject(tempObject);
                        int[] indexList = IntStream.range(0, explorePostSet.getPostList().size())
                                .filter(f -> explorePostSet.getPostList().get(f).getStandardPost().getUserId().equals(user.getId()))
                                .toArray();
                        for (int z = 0; z < indexList.length; z++) {
                            explorePostSet.getPostList().get(indexList[z]).setUser(user);
                        }
                    }
                    operationCompleted();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }else {
                delegate.Completed(false, null);
            }
        } catch (IOException e) {
            delegate.Completed(false, null);
        }

    }


    private void operationCompleted(){
        delegate.Completed(true, explorePostSet);
    }
}
