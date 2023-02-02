package com.example.prefy.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.prefy.Activity.Comment.ActivityCommentsFragment;
import com.example.prefy.Activity.Votes.ActivityVotesFragment;
import com.example.prefy.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;


public class ActivityHostFragment extends Fragment {
    private ViewPager2 viewpager;
    private TabLayout tablayout;
    private ArrayList<String> TabNames = new ArrayList<>(Arrays.asList("Votes", "Comments"));
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActivityViewModel viewModel;
    private Boolean refreshing = false;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        getViews(view);
        setUpTransition();
        return view;
    }

    public void getViews(View view){
        tablayout = view.findViewById(R.id.ActivityTabLayout);
        viewpager = view.findViewById(R.id.ActivitiyViewPager);
        initSwipeRefresh(view);
    }

    public void setUpTransition(){

        ActivityCollectionAdaptor activityCollectionAdaptor = new ActivityCollectionAdaptor(this);
        viewpager.setAdapter(activityCollectionAdaptor);
        new TabLayoutMediator(tablayout, viewpager,
                (tab, position) -> tab.setText(TabNames.get(position))
        ).attach();
        /**
         new TabLayoutMediator(tablayout, viewpager,
         new TabLayoutMediator.TabConfigurationStrategy() {
        @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {}
        }).attach();
         */


    }

    public void initSwipeRefresh(View view){
        viewModel = new ViewModelProvider(ActivityHostFragment.this).get(ActivityViewModel.class);
        viewModel.init();
        viewModel.getDataRefreshing().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null){
                    if (refreshing){
                        swipeRefreshLayout.setRefreshing(false);
                        refreshing = false;
                    }
                }
            }
        });
        swipeRefreshLayout = view.findViewById(R.id.ActivitySwipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refreshData();
                refreshing = true;
            }
        });
    }



}

@SuppressWarnings("ALL")
class ActivityCollectionAdaptor extends FragmentStateAdapter {

    public ActivityCollectionAdaptor(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new ActivityVotesFragment();
                break;
            case 1:
                fragment = new ActivityCommentsFragment();
                break;
            default:
                fragment = new ActivityVotesFragment();
                break;

        }

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
