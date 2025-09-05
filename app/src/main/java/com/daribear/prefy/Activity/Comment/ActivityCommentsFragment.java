package com.daribear.prefy.Activity.Comment;

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
 * The fragment which displays the recent Comment Activity to the User.
 */
public class ActivityCommentsFragment extends Fragment {
    private ProgressBar progressBar;
    private RelativeLayout noActivity;
    private Boolean destroyed;
    private ActivityGatewayAdaptor gateway;
    private ArrayList<CommentActivity> commentActivityList;
    private TextView noInternet;
    private Boolean dataRefreshing, internetAvailable, initDataSet = false;
    private RecyclerView recView;
    private ActivityViewModel viewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_comments, container, false);
        getViews(view);
        getData(view);
        return view;
    }

    /**
     * Get the views of the fragment.
     * @param view base view
     */
    private void getViews(View view){
        destroyed = false;
        noActivity = view.findViewById(R.id.ActivityCommentsNoItems);
        progressBar = view.findViewById(R.id.ActivityCommentsProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        noActivity.setVisibility(View.GONE);
        noInternet = view.findViewById(R.id.ActivityCommentsNoInternet);
        recView = view.findViewById(R.id.ActivityCommentsRecView);
        gateway = new ActivityGatewayAdaptor(R.id.ActivityCommentsRecView, view, view.getContext(), recView);
        gateway.displayEmptyComment(new ArrayList<>());
    }

    /**
     * Clear and reset the activity as the user has seen it.
     * @param appContext the appContext
     */
    private void resetActivity(Context appContext){
        UploadController.saveActivityClear(appContext, "Comments");
    }

    /**
     * Retrieve the data from the viewModel.
     * If the data is refreshing, indicate that in the ui
     * @param view the base view
     */
    private void getData(View view){
        resetActivity(view.getContext().getApplicationContext());
        commentActivityList = new ArrayList<>();
        viewModel = new ViewModelProvider(ActivityCommentsFragment.this).get(ActivityViewModel.class);
        viewModel.init();
        viewModel.getCommentData().observe(getViewLifecycleOwner(), new Observer<List<CommentActivity>>() {
            @Override
            public void onChanged(List<CommentActivity> commentActivities) {
                if (commentActivities != null){
                    commentActivityList = (ArrayList<CommentActivity>) commentActivities;
                    setData();
                }
            }
        });
        viewModel.getInternetAvailable().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null) {
                    internetAvailable = aBoolean;
                }
            }
        });
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
     * Since the data is refreshing, show the progress bar to indicate that
     */
    private void dataRefreshing(){
        dataRefreshing = true;
        if (!initDataSet){
            progressBar.setVisibility(View.VISIBLE);
            recView.setVisibility(View.GONE);
            noInternet.setVisibility(View.GONE);
            noActivity.setVisibility(View.GONE);
        }

    }

    /**
     * Since data is not refreshing, alter the boolean and check if teh internet is not available.
     */
    private void dataNotRefreshing(){
        dataRefreshing = false;
        if (!internetAvailable){
            noInternet();
        }
    }

    /**
     * Since theres no internet, hide every other view including progress bars, and show the no internet sign
     */
    private void noInternet()   {
        if (!destroyed) {
            if (!initDataSet){
                progressBar.setVisibility(View.GONE);
                noActivity.setVisibility(View.GONE);
                recView.setVisibility(View.GONE);
                noInternet.setVisibility(View.VISIBLE);
                //Since no internet was clicked, attempt to refresh the internet.
                noInternet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        noInternet.setVisibility(View.GONE);
                        //viewModel.refreshData();

                        RefreshInternet.RefreshInternet(getContext());
                    }
                });
            }
            NoInternetDropDown.getInstance(getActivity()).showDropDown();

        }
    }

    /**
     * Set the data into the recycler view gateway, and hide no internet or progress bar if visible
     */
    private void setData(){
        if (!destroyed){
            initDataSet = true;
            noInternet.setVisibility(View.GONE);
            noActivity.setVisibility(View.GONE);
            if (commentActivityList.size() > 0){
                recView.setVisibility(View.VISIBLE);
                gateway.updateCommentData(commentActivityList);
            } else {
                noActivity.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        destroyed = true;
        gateway.destroyView();
        super.onDestroyView();

    }


}