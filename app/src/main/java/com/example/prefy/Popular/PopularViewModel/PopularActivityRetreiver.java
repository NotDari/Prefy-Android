package com.example.prefy.Popular.PopularViewModel;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.example.prefy.Explore.ExplorePostSet;
import com.example.prefy.Popular.PopularActivity;
import com.example.prefy.R;
import com.example.prefy.Utils.CustomJsonMapper;
import com.example.prefy.Utils.ServerAdminSingleton;
import com.example.prefy.Utils.SharedPrefs;
import com.example.prefy.customClasses.FullPost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PopularActivityRetreiver {
    private DatabaseReference dbRef;
    private PopularActivityRetrieverInterface delegate;
    private Context context;

    public PopularActivityRetreiver(PopularActivityRetrieverInterface delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    public void init(){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {


                OkHttpClient client = new OkHttpClient();
                HttpUrl.Builder httpBuilder = HttpUrl.parse(context.getString(R.string.Server_base_address) + "/prefy/v1/Activity/GeneralActivity").newBuilder();
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
                            delegate.taskCompleted(true, CustomJsonMapper.getPopularActivityFromResponse(response));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }else {
                        delegate.taskCompleted(false, null);
                    }
                } catch (IOException e) {
                    delegate.taskCompleted(false, null);
                }
            }
        });
    }
    /**
    public void init(){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                dbRef = FirebaseDatabase.getInstance().getReference("activity").child(FirebaseAuth.getInstance().getUid());
                dbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            PopularActivity popularActivity = new PopularActivity();
                            Integer totalCount = task.getResult().child("newActivitiesCount").getValue(Integer.class);
                            if (totalCount == null){
                                totalCount = 0;
                            }
                            Integer commentCount = task.getResult().child("newCommentsCount").getValue(Integer.class);
                            if (commentCount == null){
                                commentCount = 0;
                            }
                            Integer voteCount = task.getResult().child("newVotesCount").getValue(Integer.class);
                            if (voteCount == null){
                                voteCount = 0;
                            }
                            popularActivity.setTotalActivities(totalCount);
                            popularActivity.setCommentsCount(commentCount);
                            popularActivity.setVotesCount(voteCount);
                            delegate.taskCompleted(true, popularActivity);
                        } else {
                            delegate.taskCompleted(false, null);
                        }



                    }
                });
            }
        });
    }
     */
}
