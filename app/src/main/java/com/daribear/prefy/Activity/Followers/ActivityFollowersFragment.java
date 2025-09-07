package com.daribear.prefy.Activity.Followers;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daribear.prefy.Activity.ActivityGatewayAdaptor;
import com.daribear.prefy.Activity.ActivityViewModel;
import com.daribear.prefy.Activity.Votes.ActivityVotesFragment;
import com.daribear.prefy.Activity.Votes.VoteActivity;
import com.daribear.prefy.Network.RefreshInternet;
import com.daribear.prefy.Network.UploadController.UploadController;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.NoInternetDropDown;

import java.util.ArrayList;
import java.util.List;

/**
 * The fragment contains a recyclerview(list) of all the ActivityFollowers, which shows all the recent people who have followed the user.
 */
public class ActivityFollowersFragment extends Fragment {
    private ProgressBar progressBar;
    private RelativeLayout noActivity;
    private Boolean destroyed;
    private ActivityGatewayAdaptor gateway;
    private ArrayList<FollowerActivity> followActivityList;
    private Boolean internetAvailable;
    private TextView noInternet;
    private Boolean dataRefreshing, initDataSet = false;
    private ActivityViewModel viewModel;
    private RecyclerView recView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_followers, container, false);
        getViews(view);
        getData(view);
        return view;
    }

    /**
     * Get default views
     * @param view base view
     */
    private void getViews(View view){
        destroyed = false;
        noActivity = view.findViewById(R.id.ActivityFollowersNoItems);
        progressBar = view.findViewById(R.id.ActivityFollowersProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        noInternet = view.findViewById(R.id.ActivityFollowersNoInternet);
        noActivity.setVisibility(View.GONE);
        recView = view.findViewById(R.id.ActivityFollowersRecView);
        gateway = new ActivityGatewayAdaptor(R.id.ActivityFollowersRecView, view, view.getContext(), recView);
        gateway.displayEmptyFollower(new ArrayList<>());
    }

    /**
     * Show that the user has seen it, so there doesn't need to be an alert.
     * @param appContext the context to use
     */
    private void resetActivity(Context appContext){
        UploadController.saveActivityClear(appContext, "Followers");
    }

    /**
     * Gets the data from the viewModel, checking if the data is refreshing or if there is no internet.
     * @param view the baseview to use
     */
    private void getData(View view){
        resetActivity(view.getContext().getApplicationContext());
        viewModel = new ViewModelProvider(ActivityFollowersFragment.this).get(ActivityViewModel.class);
        viewModel.init();
        followActivityList = new ArrayList<>();
        if (internetAvailable == null){
            internetAvailable = false;
        }
        viewModel.getFollowerActivityData().observe(getViewLifecycleOwner(), new Observer<List<FollowerActivity>>() {
            @Override
            public void onChanged(List<FollowerActivity> followerActivities) {
                if (followerActivities != null){
                    followActivityList = (ArrayList<FollowerActivity>) followerActivities;
                    setData();
                }
            }
        });
        viewModel.getInternetAvailable().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null) {
                    internetAvailable = aBoolean;
                }
            }
        });
        viewModel.getDataRefreshing().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    dataRefreshing();
                } else {
                    dataNotRefreshing();
                }
            }
        });

    }

    /**
     * The data is refreshing so show the progress bar if there is no existing data.
     */
    private void dataRefreshing(){
        dataRefreshing = true;
        if (!initDataSet) {
            progressBar.setVisibility(View.VISIBLE);
            recView.setVisibility(View.GONE);
            noInternet.setVisibility(View.GONE);
            noActivity.setVisibility(View.GONE);
        }
    }

    /**
     * THe data has stopped refreshing, and check if internet available
     */
    private void dataNotRefreshing(){
        dataRefreshing = false;
        if (!internetAvailable){
            noInternet();
        }
    }

    /**
     * Since no internet is available, show the no internet text and detects clicks.
     * These clicks will attempt to connect to the internet again.
     */
    private void noInternet(){
        if (!destroyed) {
            if (!initDataSet) {
                progressBar.setVisibility(View.GONE);
                noActivity.setVisibility(View.GONE);
                recView.setVisibility(View.GONE);
                noInternet.setVisibility(View.VISIBLE);
                noInternet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        noInternet.setVisibility(View.GONE);
                        //viewModel.refreshData();

                        RefreshInternet.RefreshInternet(getContext());
                    }
                });
            }
            NoInternetDropDown.getInstance(getActivity()).showDropDown();
        }
    }

    /**
     * Use the data retrieved from the viewmodel to use for the recyclerview.
     */
    private void setData(){
        if (!destroyed){
            noInternet.setVisibility(View.GONE);
            noActivity.setVisibility(View.GONE);
            if (followActivityList.size() > 0){
                recView.setVisibility(View.VISIBLE);
                gateway.updateFollowerData(followActivityList);
            } else {
                noActivity.setVisibility(View.VISIBLE);
            }
            initDataSet = true;
            progressBar.setVisibility(View.GONE);
        }
    }



    @Override
    public void onDestroyView() {
        gateway.destroyView();
        destroyed = true;
        super.onDestroyView();
    }


}