package com.daribear.prefy.Activity.Votes;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daribear.prefy.Activity.ActivityGatewayAdaptor;
import com.daribear.prefy.Activity.ActivityViewModel;
import com.daribear.prefy.Network.RefreshInternet;
import com.daribear.prefy.Network.UploadController.UploadController;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.NoInternetDropDown;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment displaying the list of vote activities.
 * Observes vote activity data and internet availability via ActivityViewModel.
 * Handles UI states: loading, no data, and no internet.
 */
public class ActivityVotesFragment extends Fragment {

    // UI components
    private ProgressBar progressBar;
    private RelativeLayout noActivity;
    private TextView noInternet;
    private RecyclerView recView;

    // State tracking
    private Boolean destroyed;
    private Boolean internetAvailable;
    private Boolean dataRefreshing;
    private Boolean initDataSet = false;

    // Adapter for displaying vote activity items
    private ActivityGatewayAdaptor gateway;

    // Data
    private ArrayList<VoteActivity> voteActivityList;
    private ActivityViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout and initialise views & data
        View view = inflater.inflate(R.layout.fragment_activity_votes, container, false);
        getViews(view);
        getData(view);
        return view;
    }

    /**
     * Initialise all views and the adapter.
     * @param view Fragment root view
     */
    private void getViews(View view){
        destroyed = false;
        noActivity = view.findViewById(R.id.ActivityVotesNoItems);
        progressBar = view.findViewById(R.id.ActivityVotesProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        noInternet = view.findViewById(R.id.ActivityVotesNoInternet);
        noActivity.setVisibility(View.GONE);
        recView = view.findViewById(R.id.ActivityVotesRecView);
        gateway = new ActivityGatewayAdaptor(R.id.ActivityVotesRecView, view, view.getContext(), recView);
        gateway.displayEmptyVote(new ArrayList<>());
    }

    /**
     * Resets the activity clear counter for votes in the backend.
     * @param appContext Application context
     */
    private void resetActivity(Context appContext){
        UploadController.saveActivityClear(appContext, "Votes");
    }

    /**
     * Initialises the ViewModel and observers for vote data, internet availability,
     * and data refreshing state.
     * @param view Fragment root view
     */
    private void getData(View view){
        resetActivity(view.getContext().getApplicationContext());
        viewModel = new ViewModelProvider(ActivityVotesFragment.this).get(ActivityViewModel.class);
        viewModel.init();
        voteActivityList = new ArrayList<>();
        if (internetAvailable == null){
            internetAvailable = false;
        }

        // Observe vote activity list updates
        viewModel.getVoteData().observe(getViewLifecycleOwner(), new Observer<List<VoteActivity>>() {
            @Override
            public void onChanged(List<VoteActivity> voteActivities) {
                if (voteActivities != null){
                    voteActivityList = (ArrayList<VoteActivity>) voteActivities;
                    setData();
                }
            }
        });

        // Observe internet availability changes
        viewModel.getInternetAvailable().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null) {
                    internetAvailable = aBoolean;
                }
            }
        });

        // Observe data refreshing state
        viewModel.getDataRefreshing().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    dataRefreshing();
                } else {
                    dataNotRefreshing();
                }
            }
        });
    }

    /**
     * Updates UI to indicate that data is currently refreshing.
     */
    private void dataRefreshing(){
        dataRefreshing = true;
        if (!initDataSet) {
            progressBar.setVisibility(View.VISIBLE);
            recView.setVisibility(View.GONE);
            noInternet.setVisibility(View.GONE);
            noActivity.setVisibility(View.GONE);
        }
    }

    /**
     * Updates UI when data is not refreshing.
     * Shows no internet message if offline.
     */
    private void dataNotRefreshing(){
        dataRefreshing = false;
        if (!internetAvailable){
            noInternet();
        }
    }

    /**
     * Displays a "no internet" message and sets a click listener to retry.
     */
    private void noInternet(){
        if (!destroyed) {
            if (!initDataSet) {
                progressBar.setVisibility(View.GONE);
                noActivity.setVisibility(View.GONE);
                recView.setVisibility(View.GONE);
                noInternet.setVisibility(View.VISIBLE);
                noInternet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        noInternet.setVisibility(View.GONE);
                        RefreshInternet.RefreshInternet(getContext());
                    }
                });
            }
            NoInternetDropDown.getInstance(getActivity()).showDropDown();
        }
    }

    /**
     * Updates the RecyclerView with the current vote activity data.
     * Handles empty and non-empty states.
     */
    private void setData(){
        if (!destroyed){
            noInternet.setVisibility(View.GONE);
            noActivity.setVisibility(View.GONE);
            if (voteActivityList.size() > 0){
                recView.setVisibility(View.VISIBLE);
                gateway.updateVoteData(voteActivityList);
            } else {
                noActivity.setVisibility(View.VISIBLE);
            }
            initDataSet = true;
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Re-initialises ViewModel data. Can be called to force refresh.
     */
    public void FullDataRefresh(){
        if (!destroyed){
            if (viewModel != null){
                viewModel.init();
            }
        }
    }

    /**
     * Clean up adapter and mark fragment as destroyed.
     */
    @Override
    public void onDestroyView() {
        gateway.destroyView();
        destroyed = true;
        super.onDestroyView();
    }
}