package com.daribear.prefy.Search;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;

import java.util.ArrayList;

/**
 * The gateway fragment for the search recyclerview.
 */
public class SearchGateway implements SearchRecViewTopTargetReached, SearchRecSearchTargetReached{
    private Integer RecViewId;
    private View view;
    private Context context;
    private RecyclerView recView;
    private SearchUserAdaptor adaptor;
    private LinearLayoutManager linearLayoutManager;
    private Activity parentActivity;

    public SearchGateway(Integer recViewId, View view, Context context, Activity parentActivity) {
        RecViewId = recViewId;
        this.view = view;
        this.context = context;
        this.parentActivity = parentActivity;
    }

    public void setType(String type) {
        adaptor.setType(type);
    }

    /**
     * Displays an empty view in the recyclerview
     * @param fragmentHeight the height of the fragment.(Used to dynamically calculate recyclerview entry height)
     */
    public void displayEmptyView(Integer fragmentHeight){
        recView = view.findViewById(RecViewId);
        this.context = recView.getContext();
        adaptor = new SearchUserAdaptor(this, this, recView);
        adaptor.setSearchUserArrayList(new ArrayList<>());
        recView.setAdapter(adaptor);
        linearLayoutManager = new LinearLayoutManager(context){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                lp.height = fragmentHeight / 7;
                return super.checkLayoutParams(lp);
            }
        };

        recView.setLayoutManager(linearLayoutManager);
        recView.setNestedScrollingEnabled(false);
    }

    public void setInitialData(ArrayList<User> searchUserArrayList){
        adaptor.setSearchUserArrayList(searchUserArrayList);
        adaptor.notifyDataSetChanged();

    }


    /**
     * Callback when the top of the recyclerview has been reached.
     * Tells the searchviewmodel to refresh the data
     */
    @Override
    public void endReached() {
        if (adaptor.getItemCount() > 10) {
            SearchViewModel searchViewModel = new SearchViewModel();
            searchViewModel.init();
            searchViewModel.viewScrolled();
            //viewScrolledTop(rating);
        }
    }

    /**
     * Callback when the bottom of the recyclerview has been reached.
     * Tells the searchviewmodel to get more data
     */
    @Override
    public void topReached(String lastUsername) {
        if (adaptor.getItemCount() > 10) {
            SearchViewModel searchViewModel = new SearchViewModel();
            searchViewModel.init();
            searchViewModel.viewScrolled();
            // viewScrolledSearch(lastUsername);
        }
    }



}
