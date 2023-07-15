package com.daribear.prefy.Popular;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.daribear.prefy.Ads.AdTracker;
import com.daribear.prefy.Network.RefreshInternet;
import com.daribear.prefy.Popular.CustomViewPager.PopNoPostsDelegate;
import com.daribear.prefy.Popular.CustomViewPager.PopularPager;
import com.daribear.prefy.customClasses.Posts.FullPost;
import com.daribear.prefy.customClasses.Posts.PopularPost;
import com.daribear.prefy.Popular.PopularViewModel.PopularModelPackage;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.NoInternetDropDown;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


public class PopularPageHostFragment extends Fragment implements PopularPostVote, PopNoPostsDelegate {
    Boolean destroyed;
    private ProgressBar progressBar;
    private TextView activityText;
    private NewPopularViewModel popViewModel;
    private Boolean initValuesSet;
    private Boolean dataRefreshing;
    private ImageView searchButton;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ConstraintLayout noMorePosts;

    private TextView noInternetText;

    private Boolean internetAvailable, firstLoadDone, voteLoading;
    private PopularPager popularPager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular_page_host, container, false);
        getViews(view);
        getData(view);
        initActivityButton(view);
        initSearchPage(view);
        return view;
    }

    public void getViews(View view) {
        destroyed = false;
        initValuesSet = false;
        voteLoading = false;


        progressBar = view.findViewById(R.id.PopularPageProgress);

        activityText = view.findViewById(R.id.PopularHostNewActivityText);


        swipeRefreshLayout = view.findViewById(R.id.PopularPageHostSwipeRefresh);
        initRefresh();

        searchButton = view.findViewById(R.id.PopularHostSearchButton);
        noMorePosts = view.findViewById(R.id.PopularHostFragmentNoMoreLay);

        noInternetText = view.findViewById(R.id.PopularNoInternet);
        popularPager = view.findViewById(R.id.PopularDariPager);
    }

    private void initRefresh(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                popViewModel.getMoreData();
            }
        });
    }

    private void getData(View view){
        progressBar.setVisibility(View.VISIBLE);
        popViewModel = new ViewModelProvider(getActivity()).get(NewPopularViewModel.class);
        popViewModel.init(getActivity().getApplicationContext());
        internetAvailable = true;
        firstLoadDone = false;
        popViewModel.getPostData().observe(getActivity(), new Observer<PopularModelPackage>() {
            @Override
            public void onChanged(PopularModelPackage popularModelPackage) {
                PopularPostSet popularPostSet = new PopularPostSet();
                if (popularModelPackage.getRetrievalType().equals("Failed")){
                    internetAvailable = false;
                }else {
                    internetAvailable = true;
                    firstLoadDone = true;
                }
                ArrayList<FullPost> fullPostList = popularModelPackage.getFullPostList();
                if (fullPostList != null) {
                    if (fullPostList.size() > 0) {
                        if (!initValuesSet) {
                            initViewPager(fullPostList);
                        } else {
                            if (!destroyed) {
                                popularPager.setPostList(fullPostList);
                            }
                        }
                    } else {
                        alterInternet();
                    }
                }
                if (swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
                printData(popularPostSet);
            }
        });

        popViewModel.getPopularActivityMutable().observe(getActivity(), new Observer<PopularActivity>() {
            @Override
            public void onChanged(PopularActivity popularActivity) {
                if (popularActivity != null){
                    if (popularActivity.getTotalActivities() != null){
                        activityText.setText(popularActivity.getTotalActivities().toString());
                    } else {
                        activityText.setText("0");
                    }
                } else {
                    activityText.setText("0");
                }
        }
        });


        activityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popViewModel.resetActivity();
            }
        });

        noInternetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popViewModel.getMoreData();
                noInternetText.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                RefreshInternet.RefreshInternet(getContext());
            }
        });
    }
    private void printData(PopularPostSet popularPostSet){
        if (popularPostSet != null) {
            if (popularPostSet.getPostList() != null) {
                System.out.println("\nSdad Starting: \n ------------------ \n ");
                System.out.println("Sdad postCount: " + popularPostSet.getPostList().size());
                System.out.println("Sdad userCount: " + popularPostSet.getUserList().size());
                for (int i = 0; i < popularPostSet.getPostList().size(); i++) {
                    System.out.println("Sdad post:" + popularPostSet.getPostList().get(i));
                }
            }
        }
    }

    private void initViewPager(ArrayList<FullPost> fullPostList){
        if (!destroyed) {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            popularPager.setVisibility(View.VISIBLE);
            noMorePosts.setVisibility(View.GONE);
            noInternetText.setVisibility(View.GONE);
            popularPager.setPostList(fullPostList);
            popularPager.setFragment(PopularPageHostFragment.this);
            popularPager.init();
            popularPager.setNoPostsDelegate(this);
            noInternetText.setVisibility(View.GONE);

            initValuesSet = true;
        }
    }

    private void alterInternet(){
        if (!destroyed) {
            progressBar.setVisibility(View.GONE);
            popularPager.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            popularPager.setVisibility(View.GONE);
            if (!firstLoadDone){
                if (!internetAvailable) {
                    noInternetText.setVisibility(View.VISIBLE);
                    noMorePosts.setVisibility(View.GONE);
                    NoInternetDropDown.getInstance(getActivity()).showDropDown();
                } else {
                    noMorePosts.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setEnabled(true);
                    noInternetText.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            else {
                if (!internetAvailable) {
                    NoInternetDropDown.getInstance(getActivity()).showDropDown();
                }
                noMorePosts.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setEnabled(true);
                noInternetText.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void initActivityButton(View view){
        activityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //UploadController.saveVote(view.getContext().getApplicationContext(), null);
                BottomNavigationView bottomNav = getActivity().findViewById(R.id.BottomNav);
                bottomNav.setSelectedItemId(R.id.activityFragment);
                popViewModel.resetActivity();
            }
        });
    }

    private void initSearchPage(View view){
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {

                Navigation.findNavController(view).navigate(R.id.action_global_searchFragment);

            }
        });
    }

    @Override
    public void onDestroyView(){
        destroyed = true;
        progressBar = null;
        popularPager.viewDestroyed();
        System.out.println("Sdad viewDestroyed");
        super.onDestroyView();
        noInternetText = null;
        noMorePosts = null;
        popularPager = null;
    }




    @Override
    public void noMorePosts() {
        alterInternet();
    }

    @Override
    public void voted(Boolean cooldown, Boolean removeVote) {
        popularPager.voted(cooldown, removeVote);
    }
}
