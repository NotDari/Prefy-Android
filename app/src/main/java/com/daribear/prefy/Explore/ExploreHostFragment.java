package com.daribear.prefy.Explore;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daribear.prefy.Network.RefreshInternet;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.NoInternetDropDown;
import com.daribear.prefy.customClasses.Posts.FullPost;

import java.util.List;


/**
 * Fragment for the Explore page
 * Handles posts feed, featured posts , pull refresh  and no internet state
 */
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

    /**
     * Initialise views and RecyclerView
     */
    private void getViews(View view){
        recView = view.findViewById(R.id.ExploreRecView);
        refreshLayout = view.findViewById(R.id.ExploreHostSwipeRefresh);
        progressBar = view.findViewById(R.id.ExploreProgressBar);
        noInternetText = view.findViewById(R.id.ExploreNoInternet);
        setUpRecView(view);
        initRefresh();
    }


    /**
     * Fetch initial data and attach observers for LiveDat to get updates
     */
    private void getData(){
        initDataSet = false;
        progressBar.setVisibility(View.VISIBLE);
        viewModel = new ViewModelProvider(ExploreHostFragment.this).get(ExploreViewModel.class);
        viewModel.init();
        //observe featured posts LiveData
        viewModel.getFeaturedPostList().observe(getViewLifecycleOwner(), new Observer<List<FullPost>>() {
            @Override
            public void onChanged(List<FullPost> fullFeaturedPosts) {
                if (fullFeaturedPosts != null){
                    initDataSet = true;
                    gateway.alterFeaturedPosts(fullFeaturedPosts);
                }
            }
        });
        //observe explore posts LiveData
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
        //observe internet available LiveData
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

    /**
     * Initialise no internet state, it shows dropdown and retry option
     */
    private void initNoInternet(){
        NoInternetDropDown.getInstance(getActivity()).showDropDown();
        noInternetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //viewModel.refresh();
                noInternetText.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                RefreshInternet.RefreshInternet(getContext());
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

    /**
     * Setup pull refresh functionality
     */
    private void setUpRecView(View view){
        gateway = new NewExploreGateway(recView, view, getActivity());
        gateway.displayView();
    }


    //Scroll to Top of recyclerview
    public void initScrollToTop(){
        recView.smoothScrollToPosition(0);
    }








    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //viewPagerClass.onDestroyFragment();
    }
}