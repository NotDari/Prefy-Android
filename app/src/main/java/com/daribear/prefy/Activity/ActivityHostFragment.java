package com.daribear.prefy.Activity;

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

import com.daribear.prefy.Activity.Comment.ActivityCommentsFragment;
import com.daribear.prefy.Activity.Followers.ActivityFollowersFragment;
import com.daribear.prefy.Activity.Votes.ActivityVotesFragment;
import com.daribear.prefy.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The host Fragment for the new activities data.
 * Has 3 tabs, one for each of the different type of activity.
 */
public class ActivityHostFragment extends Fragment {
    private ViewPager2 viewpager;
    private TabLayout tablayout;
    private ArrayList<String> TabNames = new ArrayList<>(Arrays.asList("Votes", "Comments", "Followers"));
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActivityViewModel viewModel;
    private Boolean refreshing = false;


    //Create the fragment view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        getViews(view);
        setUpTransition();
        return view;
    }

    /**
     * Get the list of views in the fragment
     * @param view baseview
     */
    public void getViews(View view){
        tablayout = view.findViewById(R.id.ActivityTabLayout);
        viewpager = view.findViewById(R.id.ActivitiyViewPager);
        initSwipeRefresh(view);
    }

    /**
     * configure the pager adapter and attach tabs.
     * sets titles of tabs based on TabNames
     */
    public void setUpTransition(){

        ActivityCollectionAdaptor activityCollectionAdaptor = new ActivityCollectionAdaptor(this);
        viewpager.setAdapter(activityCollectionAdaptor);
        new TabLayoutMediator(tablayout, viewpager,
                (tab, position) -> tab.setText(TabNames.get(position))
        ).attach();



    }

    /**
     * Setup of enabling the swipeRefreshLayout to search for more data.
     * observes the viewmodel for updating ui
     * @param view baseview
     */
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

/**
 * Adapter for a viewpager2, which has fragments for each tab.
 * Uses the ActivityVotesFragment,ActivityCommentsFragment or ActivityFollowersFragment based on which tab is
 */
@SuppressWarnings("ALL")
class ActivityCollectionAdaptor extends FragmentStateAdapter {

    public ActivityCollectionAdaptor(Fragment fragment) {
        super(fragment);
    }

    /**
     * Create the fragment based on the position
     * @param position position of the tab layout.
     * @return the Fragment
     */
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
            case 2:
                fragment = new ActivityFollowersFragment();
                break;
            default:
                fragment = new ActivityVotesFragment();
                break;

        }

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
