package com.example.prefy.Popular;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.prefy.Network.UploadController.UploadController;
import com.example.prefy.Popular.PopularViewModel.PopViewModel;
import com.example.prefy.R;
import com.example.prefy.customClasses.StandardPost;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;


public class PopularPageHostFragment extends Fragment implements PopularPostVote{
    private ViewPager2 viewPager;
    Boolean destroyed;
    private ProgressBar progressBar;
    private TextView activityText;
    private PopViewModel popViewModel;
    private Boolean initValuesSet;
    private ArrayList<StandardPost> previousPostArrayList = new ArrayList<>();
    private PopularPagerAdaptor popularPagerAdaptor;
    private SwipeRefreshLayout swipeLayout;
    private Boolean dataRefreshing;
    private ImageView searchButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular_page_host, container, false);
        getViews(view);
        getData(view);
        initActivityButton(view);
        initBottomNavPress();
        initSearchPage(view);
        return view;
    }

    public void getViews(View view) {
        destroyed = false;
        initValuesSet = false;

        viewPager = view.findViewById(R.id.PopularPager);

        progressBar = view.findViewById(R.id.PopularPageProgress);

        activityText = view.findViewById(R.id.PopularHostNewActivityText);

        swipeLayout = view.findViewById(R.id.PopularPageHostSwipeRefresh);
        setUpSwipeRefreshListener();

        searchButton = view.findViewById(R.id.PopularHostSearchButton);
    }

    private void getData(View view){
        progressBar.setVisibility(View.VISIBLE);
        swipeLayout.setVisibility(View.GONE);
        swipeLayout.setEnabled(false);
        popViewModel = new ViewModelProvider(getActivity()).get(PopViewModel.class);
        popViewModel.init(getActivity().getApplicationContext());
        PopularPostSet tempPopularPostSet = new PopularPostSet();
        tempPopularPostSet.setPostList(new ArrayList<>());
        popularPagerAdaptor = new PopularPagerAdaptor(PopularPageHostFragment.this, tempPopularPostSet);
        RecyclerView recyclerView = (RecyclerView)viewPager.getChildAt(0);
        recyclerView.setItemAnimator(null);
        //System.out.println("Sdad recView " + recyclerView);
        //((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        viewPager.setAdapter(popularPagerAdaptor);
        popViewModel.getPostData().observe(getActivity(), new Observer<PopularPostSet>() {
            @Override
            public void onChanged(PopularPostSet popularPostSet) {
                if (popularPostSet != null) {
                    if (popularPostSet.getPostList() != null) {
                        if (popularPostSet.getPostList().size() > 0) {
                            if (!initValuesSet) {
                                initViewPager(popularPostSet);
                            } else {
                                updateViewPagerData(popularPostSet);
                            }
                        }

                    }
                }
            }
        });
        popViewModel.getActivity().observe(getActivity(), new Observer<PopularActivity>() {
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
        onPageChangeListener();
    }

    private void initViewPager(PopularPostSet popularPostSet){
        if (!destroyed) {
            swipeLayout.setVisibility(View.VISIBLE);
            swipeLayout.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            popularPagerAdaptor.setPopularPostSet(popularPostSet);
            popularPagerAdaptor.notifyItemRangeChanged(0, popularPostSet.getPostList().size());
            previousPostArrayList = (ArrayList<StandardPost>) popularPostSet.getPostList().clone();
            initValuesSet = true;
        }
    }

    private void setUpSwipeRefreshListener(){
        dataRefreshing = false;
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!dataRefreshing){
                    dataRefreshing = true;
                    popViewModel.getMoreData();
                }
            }
        });
    }

    private void initBottomNavPress(){
        /**
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.FragmentContainerView);
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNav = getActivity().findViewById(R.id.BottomNav);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.popularPageHostFragment:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.submitPostDialog:
                        SubmitPostDialog postDialog = SubmitPostDialog.getInstance();
                        postDialog.displaySheet((MainActivity) getActivity());
                        break;
                    default:
                        NavigationUI.onNavDestinationSelected(item, navController);
                        break;
                }
                return false;
            }
        });
         */
    }

    private void onPageChangeListener(){
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (popularPagerAdaptor.getItemCount() > position + 3){
                    System.out.println("Sdad preloading");
                    String imageLink1 = popularPagerAdaptor.popularPostSet.getPostList().get(position + 1).getImageURL();
                    String ppLink1 = popularPagerAdaptor.popularPostSet.getUserList().get(position + 1).getProfileImageURL();
                    String imageLink2 = popularPagerAdaptor.popularPostSet.getPostList().get(position + 2).getImageURL();
                    String ppLink2 = popularPagerAdaptor.popularPostSet.getUserList().get(position + 2).getProfileImageURL();
                    Glide.with(PopularPageHostFragment.this)
                            .load(imageLink1)
                            .preload();
                    Glide.with(PopularPageHostFragment.this)
                            .load(ppLink1)
                            .preload();
                    Glide.with(PopularPageHostFragment.this)
                            .load(imageLink2)
                            .preload();
                    Glide.with(PopularPageHostFragment.this)
                            .load(ppLink2)
                            .preload();
                }
                super.onPageSelected(position);
            }
        });
    }

    private void updateViewPagerData(PopularPostSet newPopularPostSet){
        if (!destroyed){
            if (dataRefreshing){
                swipeLayout.setRefreshing(false);
                dataRefreshing = false;
            }
            if (popViewModel.getDataType().contains("update")){
                popularPagerAdaptor.setPopularPostSet(newPopularPostSet);
                popularPagerAdaptor.notifyDataSetChanged();
                //popularPagerAdaptor.notifyItemRangeChanged(previousPopularPostSet.getPostList().size(), newPopularPostSet.getPostList().size());
            } else {
                ViewPagerNewItemsHandler viewPagerNewItemsHandler = new ViewPagerNewItemsHandler(newPopularPostSet, previousPostArrayList, viewPager, popularPagerAdaptor, getActivity());
                System.out.println("Sdad postQuestionnn " + newPopularPostSet.getPostList().get(0).getQuestion() + "  " + previousPostArrayList.get(0).getQuestion());
                viewPagerNewItemsHandler.viewPagerChanged(popViewModel.getDataType());
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
            }
        });
    }

    public void setViewPager0(){
        if (!destroyed){
            if (viewPager != null){
                if (popularPagerAdaptor != null){
                    if (popularPagerAdaptor.getItemCount() > 0) {
                        viewPager.setCurrentItem(0);
                    }
                }
            }
        }
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
    public void onDestroyView() {
        destroyed = true;
        viewPager.setAdapter(null);
        progressBar = null;
        super.onDestroyView();

    }


    @Override
    public void voted(Boolean autoScrollEnabled) {
        if (!destroyed){
            if (autoScrollEnabled){
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        }
    }




    public class PopularPagerAdaptor extends FragmentStateAdapter {
        private PopularPostSet popularPostSet;
        private Integer updateTriggerCount;
        private FragmentViewHolder holder;

        public PopularPagerAdaptor(@NonNull Fragment fragment, PopularPostSet popularPostSet) {
            super(fragment);
            this.popularPostSet = popularPostSet;
            updateTriggerCount = 10;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            PopularPageFragment fragment = new PopularPageFragment();
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

        public Integer getAdaptorPosition(){
            return holder.getAdapterPosition();
        }


        @Override
        public void onBindViewHolder(@NonNull FragmentViewHolder holder, int position, @NonNull List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);
            this.holder = holder;
            if (position >= updateTriggerCount) {
                updateTriggerCount += 15;
                popViewModel.getMoreData();
            }
        }
    }
}
