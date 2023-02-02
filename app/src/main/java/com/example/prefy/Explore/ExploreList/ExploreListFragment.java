package com.example.prefy.Explore.ExploreList;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.prefy.Explore.ExploreGateway;
import com.example.prefy.Explore.ExplorePostSet;
import com.example.prefy.Explore.ExploreViewModel;
import com.example.prefy.Profile.ProfilePostsRec.ProfileRetreiver.ProfilePostsRetreiver.ProfileExecutor;
import com.example.prefy.Profile.ProfilePostsRec.ProfileRetreiver.ProfilePostsRetreiver.ProfileHandlerInt;
import com.example.prefy.Profile.ProfilePostsRec.ProfileRetreiver.WholeProfile;
import com.example.prefy.R;
import com.google.firebase.auth.FirebaseAuth;


public class ExploreListFragment extends Fragment{
    private RecyclerView recView;
    private ExploreGateway exploreGateway;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore_list, container, false);
        getViews(view);
        getData(view);
        return view;
    }

    private void getViews(View view){
        recView = view.findViewById(R.id.FragmentExploreListRecView);
    }

    private void getData(View view){
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Integer fragmentHeight = display.getHeight();
        exploreGateway = new ExploreGateway(recView, view, view.getContext(), R.id.FragmentExploreListRecView, getParentFragment());
        exploreGateway.initEmptyListGateway(fragmentHeight);
        ExploreViewModel exploreViewModel = new ViewModelProvider(ExploreListFragment.this).get(ExploreViewModel.class);
        exploreViewModel.init();
        exploreViewModel.getExplorePostSetMutable().observe(getViewLifecycleOwner(), new Observer<ExplorePostSet>() {
            @Override
            public void onChanged(ExplorePostSet explorePostSet) {
                if (explorePostSet != null){
                    updateData(explorePostSet);
                }
            }
        });
    }

    @Override
    public void onResume() {
        recView.requestLayout();
        exploreGateway.viewResumed();
        super.onResume();
    }

    @Override
    public void onPause() {
        exploreGateway.viewPaused();
        super.onPause();
    }

    private void updateData(ExplorePostSet explorePostSet){
        if (!isDetached()){
            if (explorePostSet.getPostList() != null) {
                exploreGateway.updateExploreListData(explorePostSet);
            }
        }
    }




}