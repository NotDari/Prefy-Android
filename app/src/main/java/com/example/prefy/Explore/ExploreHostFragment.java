package com.example.prefy.Explore;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.prefy.Activity.ActivityViewModel;
import com.example.prefy.Activity.Comment.CommentActivity;
import com.example.prefy.Explore.ExplorePost.ExplorePostDialog;
import com.example.prefy.Network.RefreshInternet;
import com.example.prefy.R;
import com.example.prefy.Utils.NoInternetDropDown;
import com.example.prefy.customClasses.FullPost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ExploreHostFragment extends Fragment {
    private RecyclerView recView;
    private ExploreViewModel viewModel;
    private NewExploreGateway gateway;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar progressBar;
    private TextView noInternetText;
    private Boolean initDataSet;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore_host, container, false);
        getViews(view);
        getData();
        //setUpImages();
        //initSponsoredPage(view);
        //initViewPager(view);
        //setUpFeaturedImages(view);
        //setUpFeaturedClicks();
        return view;
    }

    private void getViews(View view){
        recView = view.findViewById(R.id.ExploreRecView);
        refreshLayout = view.findViewById(R.id.ExploreHostSwipeRefresh);
        progressBar = view.findViewById(R.id.ExploreProgressBar);
        noInternetText = view.findViewById(R.id.ExploreNoInternet);
        setUpRecView(view);
        initRefresh();
    }


    private void getData(){
        initDataSet = false;
        progressBar.setVisibility(View.VISIBLE);
        viewModel = new ViewModelProvider(ExploreHostFragment.this).get(ExploreViewModel.class);
        viewModel.init();
        viewModel.getFeaturedPostList().observe(getViewLifecycleOwner(), new Observer<List<FullPost>>() {
            @Override
            public void onChanged(List<FullPost> fullFeaturedPosts) {
                if (fullFeaturedPosts != null){
                    initDataSet = true;
                    gateway.alterFeaturedPosts(fullFeaturedPosts);
                }
            }
        });
        viewModel.getExplorePostSetMutable().observe(getViewLifecycleOwner(), new Observer<ExplorePostSet>() {
            @Override
            public void onChanged(ExplorePostSet explorePostSet) {
                if (explorePostSet != null){
                    initDataSet = true;
                    progressBar.setVisibility(View.GONE);
                    refreshLayout.setRefreshing(false);
                    gateway.initData(explorePostSet);
                }
            }
        });
        viewModel.getInternetAvailable().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null){
                    if (!aBoolean){
                        refreshLayout.setRefreshing(false);
                    }
                    if (!initDataSet){
                        if (!aBoolean) {
                            initNoInternet();
                            progressBar.setVisibility(View.GONE);
                            noInternetText.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

    }

    private void initNoInternet(){
        NoInternetDropDown.getInstance(getActivity()).showDropDown();
        noInternetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //viewModel.refresh();
                noInternetText.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                RefreshInternet.RefreshInternet();
            }
        });
    }

    private void initRefresh(){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refresh();
                gateway.refreshCategories();
            }
        });
    }


    private void setUpRecView(View view){
        gateway = new NewExploreGateway(recView, view, getActivity());
        gateway.displayView();
    }



    public void initScrollToTop(){
        recView.smoothScrollToPosition(0);
    }








    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //viewPagerClass.onDestroyFragment();
    }
}