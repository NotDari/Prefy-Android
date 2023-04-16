package com.daribear.prefy.Explore.ExploreCollection;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daribear.prefy.Explore.ExploreGateway;
import com.daribear.prefy.Explore.ExplorePostSet;
import com.daribear.prefy.Explore.ExploreViewModel;
import com.daribear.prefy.R;


public class ExploreCollectionFragment extends Fragment{
    private RecyclerView recView;
    private ExploreGateway exploreGateway;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore_collection, container, false);
        getViews(view);
        getData(view);
        return view;
    }

    private void getViews(View view){
        recView = view.findViewById(R.id.ExploreCollectionRecView);
    }

    private void getData(View view){
        exploreGateway = new ExploreGateway(recView, view, view.getContext(), R.id.ExploreCollectionRecView, getParentFragment());
        exploreGateway.initEmptyCollectionGateway(getActivity());
        ExploreViewModel exploreViewModel = new ViewModelProvider(ExploreCollectionFragment.this).get(ExploreViewModel.class);
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
                exploreGateway.updateExploreCollectionData(explorePostSet);
            }
        }
    }




}