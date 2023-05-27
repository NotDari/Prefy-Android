package com.daribear.prefy.Profile;

import com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.ProfilePostsRetreiver.ProfileExecutor;
import com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.ProfilePostsRetreiver.ProfileHandlerInt;
import com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.WholeProfile;
import com.daribear.prefy.Utils.CustomJsonMapper;
import com.daribear.prefy.Utils.ErrorChecker;
import com.daribear.prefy.Utils.GetFollowing.FollowingRetrieving;
import com.daribear.prefy.Utils.GetFollowing.GetFollowingDelegate;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetUserDetailsExecutor implements ProfileHandlerInt, GetFollowingDelegate {
    private Long id;
    private ProfileHandlerInt delegate;
    private User user;
    private DatabaseReference databaseReference;
    private String serverAddress, authToken;

    private Boolean followingDone, profileDone, currentUser;
    private WholeProfile wholeProfile;

    private HashMap<Long, Boolean> followingList;

    public GetUserDetailsExecutor(Long userId, ProfileHandlerInt delegate) {
        this.id = userId;
        this.delegate = delegate;
    }

    public void initExecutor(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                profileDone = false;
                followingDone = false;
                currentUser = (Objects.equals(id, ServerAdminSingleton.getInstance().getLoggedInId()));
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
                        if (!currentUser){
                            FollowingRetrieving followingRetrieving = new FollowingRetrieving(new ArrayList<>(Arrays.asList(id)), GetUserDetailsExecutor.this::completed, null);
                        } else {
                            if (user != null) {
                                user.setFollowing(false);
                            }
                            followingDone = true;
                        }
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


    private void operationCompleted(){
        System.out.println("Sdad hi!!!" + profileDone + followingDone);
        if (profileDone && followingDone ) {
            if (!currentUser) {
                if (followingList != null) {
                    wholeProfile.getUser().setFollowing(followingList.get(id));
                }
            }
            System.out.println("Sdad hi!!!@" + wholeProfile);
            delegate.taskDone(true, wholeProfile);
        }
    }


    @Override
    public void taskDone(Boolean successful, WholeProfile wholeProfile) {
        if (successful) {
            profileDone = true;
            if (wholeProfile == null) {
                wholeProfile = new WholeProfile();
            }
            wholeProfile.setUser(user);
            GetUserDetailsExecutor.this.wholeProfile = wholeProfile;
            operationCompleted();
        } else {
            delegate.taskDone(false, null);
        }
    }

    @Override
    public void completed(Boolean successful, HashMap<Long, Boolean> followList, String type) {
        if (successful){
            followingDone = true;
            if (!currentUser){
                this.followingList = followList;
            }
            operationCompleted();
        } else {
            delegate.taskDone(false, null);
        }
    }
}
