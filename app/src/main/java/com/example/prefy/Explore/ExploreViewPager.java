package com.example.prefy.Explore;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prefy.Activity.Comment.ActivityCommentsFragment;
import com.example.prefy.Activity.Votes.ActivityVotesFragment;
import com.example.prefy.Explore.ExploreCollection.ExploreCollectionFragment;
import com.example.prefy.Explore.ExploreList.ExploreListFragment;
import com.example.prefy.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExploreViewPager {
    private Fragment fragment;
    private View view;
    private TabLayout tablayout;
    private ViewPager2 viewpager;
    private ArrayList<String> TabNames = new ArrayList<>(Arrays.asList("Collection", "List"));
    private Boolean destroyed;
    //private ScrollView scrollView;
    private NestedScrollView scrollView;


    public ExploreViewPager(Fragment fragment, View view) {
        this.fragment = fragment;
        this.view = view;
        destroyed = false;
    }

    public void init() {
        //getViews(view);
       // setUpTransition();
        //initResize();
    }
    private void getViews(View view){
        //tablayout = view.findViewById(R.id.ExploreTabLayout);
        //viewpager = view.findViewById(R.id.ExploreViewPager);
        //scrollView = view.findViewById(R.id.ExploreHostScrollView);
    }




    private void setUpTransition() {

        ExploreCollectionAdaptor activityCollectionAdaptor = new ExploreCollectionAdaptor(fragment);
        viewpager.setAdapter(activityCollectionAdaptor);
        new TabLayoutMediator(tablayout, viewpager,
                (tab, position) -> tab.setText(TabNames.get(position))
        ).attach();

    }

    private void initResize(){
        viewpager.setUserInputEnabled(false);
    }

    public void onDestroyFragment(){
        destroyed = true;
    }






class ExploreCollectionAdaptor extends FragmentStateAdapter {

        public ExploreCollectionAdaptor(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = new ExploreCollectionFragment();
                    break;
                case 1:
                    fragment = new ExploreListFragment();
                    break;
                default:
                    fragment = new ExploreListFragment();
                    break;

            }

            return fragment;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
