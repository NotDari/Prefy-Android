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

    private void resetActivity(Context appContext){
        UploadController.saveActivityClear(appContext, "Followers");
    }

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
    private void dataRefreshing(){
        dataRefreshing = true;
        if (!initDataSet) {
            progressBar.setVisibility(View.VISIBLE);
            recView.setVisibility(View.GONE);
            noInternet.setVisibility(View.GONE);
            noActivity.setVisibility(View.GONE);
        }
    }
    private void dataNotRefreshing(){
        dataRefreshing = false;
        if (!internetAvailable){
            noInternet();
        }
    }


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

    public void FullDataRefresh(){
        if (!destroyed){
            if (viewModel != null){
                viewModel.init();
            }
        }
    }


    @Override
    public void onDestroyView() {
        gateway.destroyView();
        destroyed = true;
        super.onDestroyView();
    }


}