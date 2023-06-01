package com.daribear.prefy.Popular.NewPopularSystem;

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
import com.daribear.prefy.Popular.PopularActivity;
import com.daribear.prefy.customClasses.Posts.PopularPost;
import com.daribear.prefy.Popular.PopularPostSet;
import com.daribear.prefy.Popular.PopularPostVote;
import com.daribear.prefy.Popular.PopularViewModel.PopularModelPackage;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.NoInternetDropDown;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;


public class PopularPageHostFragment2 extends Fragment implements PopularPostVote {
    private ViewPager2 viewPager;
    Boolean destroyed;
    private ProgressBar progressBar;
    private TextView activityText;
    private NewPopularViewModel popViewModel;
    private Boolean initValuesSet;
    private PopularPagerAdaptor2 popularPagerAdaptor;
    private Boolean dataRefreshing;
    private ImageView searchButton;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ConstraintLayout noMorePosts;

    private TextView noInternetText;

    private Boolean internetAvailable, firstLoadDone;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        viewPager = view.findViewById(R.id.PopularPager);

        progressBar = view.findViewById(R.id.PopularPageProgress);

        activityText = view.findViewById(R.id.PopularHostNewActivityText);


        swipeRefreshLayout = view.findViewById(R.id.PopularPageHostSwipeRefresh);
        initRefresh();

        searchButton = view.findViewById(R.id.PopularHostSearchButton);
        noMorePosts = view.findViewById(R.id.PopularHostFragmentNoMoreLay);

        noInternetText = view.findViewById(R.id.PopularNoInternet);
    }

    private void initRefresh(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("SdadQ reresh init");
                popViewModel.getMoreData();
            }
        });
    }

    private void getData(View view){
        progressBar.setVisibility(View.VISIBLE);
        popViewModel = new ViewModelProvider(getActivity()).get(NewPopularViewModel.class);
        popViewModel.init(getActivity().getApplicationContext());
        PopularPostSet tempPopularPostSet = new PopularPostSet();
        tempPopularPostSet.setPostList(new ArrayList<>());
        popularPagerAdaptor = new PopularPagerAdaptor2(PopularPageHostFragment2.this, tempPopularPostSet);
        RecyclerView recyclerView = (RecyclerView)viewPager.getChildAt(0);
        recyclerView.setItemAnimator(null);
        viewPager.setAdapter(popularPagerAdaptor);
        viewPager.setUserInputEnabled(false);
        internetAvailable = true;
        firstLoadDone = false;
        popViewModel.getPostData().observe(getActivity(), new Observer<PopularModelPackage>() {
            @Override
            public void onChanged(PopularModelPackage popularModelPackage) {
                PopularPostSet popularPostSet = new PopularPostSet();
                System.out.println("Sdad retrievalType:" + popularModelPackage.getRetrievalType());
                if (popularModelPackage.getRetrievalType().equals("Failed")){
                    internetAvailable = false;
                } else {
                    internetAvailable = true;
                    firstLoadDone = true;
                }
                if (popularModelPackage.getPopularPostSet() != null){
                    if (popularModelPackage.getPopularPostSet() != null){
                        if (popularModelPackage.getPopularPostSet().getPostList() != null && popularModelPackage.getPopularPostSet().getUserList() != null) {
                            popularPostSet.setPostList((ArrayList<PopularPost>) popularModelPackage.getPopularPostSet().getPostList().clone());
                            popularPostSet.setUserList((ArrayList<User>) popularModelPackage.getPopularPostSet().getUserList().clone());
                            if (popularPostSet.getPostList().size() > 0) {
                                System.out.println("SdadO size:" + popularPostSet.getPostList().size() + initValuesSet);
                                if (!initValuesSet) {
                                    initViewPager(popularPostSet);
                                } else {
                                    checkData(popularPostSet, popularModelPackage.getRetrievalType());
                                }
                            }else {
                                alterInternet();
                            }
                        }
                    }
                }
                if (swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
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
            }
        });
        onPageChangeListener();
    }

    public void checkData(PopularPostSet popularPostSet, String retrievalType){
        System.out.println("Sdad retrieval type:" + retrievalType);
        if (retrievalType.equals("override")){
            PopularPostSet tempSet = new PopularPostSet();
            tempSet.setPostList(new ArrayList<>());
            tempSet.setUserList(new ArrayList<>());
            for (int i =0; i < popularPostSet.getPostList().size(); i ++){
                if (!popularPagerAdaptor.getPopularPostSet().getPostList().contains(popularPostSet.getPostList().get(i))){
                    tempSet.getPostList().add(popularPostSet.getPostList().get(i));
                    tempSet.getUserList().add(popularPostSet.getUserList().get(i));
                }
            }
            popularPagerAdaptor.setPopularPostSet(tempSet);
            popularPagerAdaptor.notifyDataSetChanged();
        } else if (retrievalType.equals("Add")) {
            for (int i = 0; i < popularPostSet.getPostList().size(); i++){
                if (!popularPagerAdaptor.getPopularPostSet().getPostList().contains(popularPostSet.getPostList().get(i))){
                    popularPagerAdaptor.getPopularPostSet().getPostList().add(popularPostSet.getPostList().get(i));
                    popularPagerAdaptor.getPopularPostSet().getUserList().add(popularPostSet.getUserList().get(i));
                    popularPagerAdaptor.notifyDataSetChanged();
                }
            }
        }
    }

    private void initViewPager(PopularPostSet popularPostSet){
        if (!destroyed) {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            noInternetText.setVisibility(View.GONE);
            popularPagerAdaptor.setPopularPostSet(popularPostSet);

            popularPagerAdaptor.notifyItemRangeChanged(0, popularPostSet.getPostList().size());
            initValuesSet = true;
        }
    }


    private void onPageChangeListener(){
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (popularPagerAdaptor.getItemCount() > position + 3){
                    String imageLink1 = popularPagerAdaptor.popularPostSet.getPostList().get(position + 1).getImageURL();
                    String ppLink1 = popularPagerAdaptor.popularPostSet.getUserList().get(position + 1).getProfileImageURL();
                    String imageLink2 = popularPagerAdaptor.popularPostSet.getPostList().get(position + 2).getImageURL();
                    String ppLink2 = popularPagerAdaptor.popularPostSet.getUserList().get(position + 2).getProfileImageURL();
                    Glide.with(PopularPageHostFragment2.this)
                            .load(imageLink1)
                            .preload();
                    Glide.with(PopularPageHostFragment2.this)
                            .load(ppLink1)
                            .preload();
                    Glide.with(PopularPageHostFragment2.this)
                            .load(imageLink2)
                            .preload();
                    Glide.with(PopularPageHostFragment2.this)
                            .load(ppLink2)
                            .preload();
                }
                super.onPageSelected(position);
            }
        });
    }

    private void alterInternet(){
        if (!destroyed) {
            progressBar.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
            if (!firstLoadDone){
                if (!internetAvailable) {
                    noInternetText.setVisibility(View.VISIBLE);
                    noMorePosts.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    NoInternetDropDown.getInstance(getActivity()).showDropDown();
                } else {
                    noMorePosts.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
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
                /**
                popViewModel.printData();
                PopularPostSet popularPostSet = popularPagerAdaptor.popularPostSet;
                for (int i =0; i < popularPostSet.getPostList().size(); i++){
                    System.out.println("SdadA :" + popularPostSet.getPostList().get(i).getQuestion());
                }
                System.out.println("SdadQ refreshLayoutVisible: " + swipeRefreshLayout.isEnabled());

                 Toast.makeText(searchButton.getContext(), "Vote Count: " + TestTool.getInstance().getVoteCount(), Toast.LENGTH_SHORT).show();


                 */
                Navigation.findNavController(view).navigate(R.id.action_global_searchFragment);

            }
        });
    }


    @Override
    public void onDestroyView(){
        destroyed = true;
        viewPager.setAdapter(null);
        progressBar = null;
        super.onDestroyView();

    }


    @Override
    public void voted(Boolean saveVote, Boolean scroll, Boolean cooldown) {
        if (!destroyed){
            if (popularPagerAdaptor.getItemCount() >= viewPager.getCurrentItem() + 1) {
                if (saveVote){
                    popViewModel.removeItem();
                }
                if (scroll) {
                    if (cooldown) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!destroyed) {
                                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                                }
                            }
                        }, 300);
                    } else {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    }
                }

            } else {
                alterInternet();
            }
            AdTracker.getInstance().popViewed();
        }
    }




    public class PopularPagerAdaptor2 extends FragmentStateAdapter {
        @Getter
        private PopularPostSet popularPostSet;
        private FragmentViewHolder holder;


        public PopularPagerAdaptor2(@NonNull Fragment fragment, PopularPostSet popularPostSet) {
            super(fragment);
            this.popularPostSet = popularPostSet;
        }


        @NonNull
        @Override
        public Fragment createFragment(int position) {
            PopularPageFragment2 fragment = new PopularPageFragment2();
            Bundle args = new Bundle();
            if (popularPostSet.getPostList().size() > 0 && popularPostSet.getUserList().size() >0) {
                args.putParcelable("post", popularPostSet.getPostList().get(position));
                args.putParcelable("user", popularPostSet.getUserList().get(position));
                fragment.setArguments(args);
            }

            return fragment;
        }





        @Override
        public int getItemCount() {
            return popularPostSet.getPostList().size();
        }

        public void setPopularPostSet(PopularPostSet popularPostSet) {
            this.popularPostSet = popularPostSet;
        }





        @Override
        public void onBindViewHolder(@NonNull FragmentViewHolder holder, int position, @NonNull List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);
            this.holder = holder;




        }
    }
}
