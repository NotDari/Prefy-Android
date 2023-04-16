package com.daribear.prefy.Profile;

import com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.ProfilePostsRetreiver.ProfileExecutor;
import com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.ProfilePostsRetreiver.ProfileHandlerInt;
import com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.WholeProfile;
import com.daribear.prefy.Utils.CustomJsonMapper;
import com.daribear.prefy.Utils.ErrorChecker;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetUserDetailsExecutor implements ProfileHandlerInt {
    private Long id;
    private ProfileHandlerInt delegate;
    private User user;
    private DatabaseReference databaseReference;
    private String serverAddress, authToken;

    public GetUserDetailsExecutor(Long userId, ProfileHandlerInt delegate) {
        this.id = userId;
        this.delegate = delegate;
    }

    public void initExecutor(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                serverAddress = ServerAdminSingleton.getInstance().getServerAddress();
                authToken = ServerAdminSingleton.getInstance().getServerAuthToken();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(serverAddress + "/prefy/v1/Users/GetUserById?id=" + id)
                        .method("GET", null)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", authToken)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        user = CustomJsonMapper.getUser(response);
                        ProfileExecutor profileExecutor = new ProfileExecutor(id, GetUserDetailsExecutor.this::taskDone, false, 18, null, false);
                        profileExecutor.initExecutor();
                    }else {
                        ErrorChecker.checkForStandardError(response);
                        delegate.taskDone(false, null);
                    }
                } catch (IOException | JSONException e) {
                    delegate.taskDone(false, null);
                }


            }
        });
    }


    @Override
    public void taskDone(Boolean successful, WholeProfile wholeProfile) {
        if (wholeProfile == null){
            wholeProfile = new WholeProfile();
        }
        wholeProfile.setUser(user);
        delegate.taskDone(successful, wholeProfile);
    }
}
