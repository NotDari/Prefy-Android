package com.example.prefy.Profile.OtherUsers;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.prefy.Profile.GetUserDetailsExecutor;
import com.example.prefy.Profile.ProfilePostsRec.NewProfilePostsGateway;
import com.example.prefy.Profile.ProfilePostsRec.ProfileRetreiver.ProfilePostsRetreiver.ProfileExecutor;
import com.example.prefy.Profile.ProfilePostsRec.ProfileRetreiver.ProfilePostsRetreiver.ProfileHandlerInt;
import com.example.prefy.Profile.ProfilePostsRec.ProfileRetreiver.WholeProfile;
import com.example.prefy.Profile.User;
import com.example.prefy.R;
import com.example.prefy.customClasses.StandardPost;

import java.util.ArrayList;

public class OtherUserDataController implements ProfileHandlerInt {
    private View view;
    private NewProfilePostsGateway gateway;
    private Context context;
    private Boolean destroyed;
    private User user;
    private ProgressBar progressBar;
    private TextView noInternetText;
    private RelativeLayout noPostsLayout;
    private Boolean firstLoadDone;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Activity parentActivity;

    public OtherUserDataController(View view, NewProfilePostsGateway gateway, Context context,User user, Activity parentActivity) {
        this.view = view;
        this.gateway = gateway;
        this.context = context;
        this.user = user;
        this.parentActivity = parentActivity;
    }

    public void initRetrieveData(){
        destroyed = false;
        firstLoadDone = false;
        getViews();
        loadPosts();

    }

    private void getViews(){
        progressBar = view.findViewById(R.id.ProfileProgressBar);
        noPostsLayout = view.findViewById(R.id.ProfileFragmentNoPosts);
        noInternetText = view.findViewById(R.id.ProfileNoInternet);
        swipeRefreshLayout = view.findViewById(R.id.ProfileSwipeRefreshlayout);
    }



    public void refreshData(){
        GetUserDetailsExecutor executor = new GetUserDetailsExecutor(user.getId(), this);
        executor.initExecutor();
    }

    private void initNoInternet(){
        noInternetText.setVisibility(View.VISIBLE);
        noPostsLayout.setVisibility(View.GONE);
        noInternetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                noInternetText.setVisibility(View.GONE);
                refreshData();
            }
        });
    }

    private void initNoPosts(){
        progressBar.setVisibility(View.GONE);
        noInternetText.setVisibility(View.GONE);
        noPostsLayout.setVisibility(View.VISIBLE);
    }



    private void loadPosts(){
        progressBar.setVisibility(View.VISIBLE);
        Integer limit = Integer.parseInt(context.getString(R.string.Search_Load_Count));
        ProfileExecutor executor = new ProfileExecutor(user.getId(), this, true, limit, null, false);
        executor.initExecutor();
    }


    public void viewDestroyed(){
        destroyed = true;
        gateway = null;
    }

    @Override
    public void taskDone(Boolean successful, WholeProfile wholeProfile) {
        if (!destroyed){
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    if (successful){
                        ArrayList<StandardPost> postList = wholeProfile.getPostListContainer().getPostList();
                        if (!firstLoadDone) {
                            if (postList.size() == 0){
                                initNoPosts();
                            }
                            gateway.setInitPosts(postList);
                            firstLoadDone = true;
                        } else {
                            gateway.setInitPosts(postList);
                            gateway.updateUserInfo( user);

                        }
                    }
                    else {
                        if (!firstLoadDone){
                            initNoInternet();
                        }
                    }
                }
            });

        }
    }
}
