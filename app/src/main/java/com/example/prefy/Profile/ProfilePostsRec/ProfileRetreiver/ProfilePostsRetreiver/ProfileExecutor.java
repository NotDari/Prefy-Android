package com.example.prefy.Profile.ProfilePostsRec.ProfileRetreiver.ProfilePostsRetreiver;

import androidx.annotation.NonNull;


import com.example.prefy.Profile.GetUserDetailsExecutor;
import com.example.prefy.Profile.ProfilePostsRec.ProfileRetreiver.WholeProfile;
import com.example.prefy.Profile.User;
import com.example.prefy.Utils.CustomJsonMapper;
import com.example.prefy.Utils.FirebaseUtils;
import com.example.prefy.Utils.ServerAdminSingleton;
import com.example.prefy.customClasses.PostListContainer;
import com.example.prefy.customClasses.StandardPost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileExecutor {
    private Long id;
    private ProfileHandlerInt delegate;
    private ArrayList<StandardPost> postList;
    private Boolean onlyPosts, update;
    private Integer limit;
    private String endPoint;
    private Integer pageNumber;

    private String serverAddress, authToken;


    public ProfileExecutor(Long id, ProfileHandlerInt delegate, Boolean onlyPosts, Integer limit, Integer pageNumber, Boolean update) {
        this.id = id;
        this.delegate = delegate;
        this.onlyPosts = onlyPosts;
        this.limit = limit;
        this.endPoint = endPoint;
        this.update = update;
        this.pageNumber = pageNumber;
    }

    public void initExecutor(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                serverAddress = ServerAdminSingleton.getInstance().getServerAddress();
                authToken = ServerAdminSingleton.getInstance().getServerAuthToken();
                OkHttpClient client = new OkHttpClient();

                HttpUrl.Builder httpBuilder = HttpUrl.parse(serverAddress + "/prefy/v1/Posts/PostListById").newBuilder();
                httpBuilder.addEncodedQueryParameter("id", id.toString());
                if (!update) {
                    pageNumber = 0;
                } else {
                    pageNumber += 1;
                }
                httpBuilder.addEncodedQueryParameter("pageNumber", pageNumber.toString());
                httpBuilder.addEncodedQueryParameter("limit", limit.toString());
                Request request = new Request.Builder()
                        .url(httpBuilder.build())
                        .method("GET", null)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", authToken)
                        .build();
                postList = new ArrayList<>();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        postList = CustomJsonMapper.getPostListFromResponse(response);
                        OperationCompleted();
                    }else {
                        delegate.taskDone(false, null);
                    }
                } catch (IOException e) {
                    delegate.taskDone(false, null);
                }
            }
        });
    }



    private void OperationCompleted(){
        WholeProfile wholeProfile = new WholeProfile();
        PostListContainer postListContainer = new PostListContainer();
        postListContainer.setPostList(postList);
        postListContainer.setPageNumber(pageNumber);
        wholeProfile.setPostListContainer(postListContainer);
        delegate.taskDone(true, wholeProfile);
    }
}
