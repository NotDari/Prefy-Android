package com.daribear.prefy.Profile.OtherUsers;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daribear.prefy.Profile.GetUserDetailsExecutor;
import com.daribear.prefy.Profile.ProfilePostsRec.ProfilePostsGateway;
import com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.ProfilePostsRetreiver.ProfileHandlerInt;
import com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.WholeProfile;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;
import com.daribear.prefy.customClasses.Posts.StandardPost;

import java.util.ArrayList;

/**
 * Controller which handles the data of a user that is not the logged in user.
 */
public class OtherUserDataController implements ProfileHandlerInt {
    private View view;
    private ProfilePostsGateway gateway;
    private Context context;
    private Boolean destroyed;
    private User user;
    private ProgressBar progressBar;
    private TextView noInternetText;
    private RelativeLayout noPostsLayout;
    private Boolean firstLoadDone;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Activity parentActivity;

    public OtherUserDataController(View view, ProfilePostsGateway gateway, Context context, User user, Activity parentActivity) {
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


    /**
     * Refreshes the data.
     */
    public void refreshData(){
        GetUserDetailsExecutor executor = new GetUserDetailsExecutor(user.getId(), this);
        executor.initExecutor();
    }

    /**
     * Displays the no internet text in the fragment, as there is no internet.
     */
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

    /**
     * Init the no posts layout when the user has no posts
     */
    private void initNoPosts(){
        progressBar.setVisibility(View.GONE);
        noInternetText.setVisibility(View.GONE);
        noPostsLayout.setVisibility(View.VISIBLE);
    }


    /**
     * Loads the first set of posts. Uses the Search_load_count preset value to load that many posts.
     */
    private void loadPosts(){
        progressBar.setVisibility(View.VISIBLE);
        Integer limit = Integer.parseInt(context.getString(R.string.Search_Load_Count));
        GetUserDetailsExecutor getUserDetailsExecutor = new GetUserDetailsExecutor(user.getId(), this);
        getUserDetailsExecutor.initExecutor();
    }


    public void viewDestroyed(){
        destroyed = true;
        gateway = null;
    }

    /**
     * Called when the retrieving of profile posts has completed.
     *
     * @param successful whether it was successful
     * @param wholeProfile the data retrieved.
     */
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
                        }
                        gateway.updateUserInfo(wholeProfile.getUser());

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
