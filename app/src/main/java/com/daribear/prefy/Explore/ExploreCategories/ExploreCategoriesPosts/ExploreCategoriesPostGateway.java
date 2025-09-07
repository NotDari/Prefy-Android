package com.daribear.prefy.Explore.ExploreCategories.ExploreCategoriesPosts;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daribear.prefy.customClasses.Posts.FullPost;

import java.util.ArrayList;

/**
 * Gateway class to manage posts for a specific explore category, it
 * handles recyclerView initialisation, adapter setup, and data updates
 */
public class ExploreCategoriesPostGateway {
    private Integer RecViewId;
    private View view;
    private Context context;
    private RecyclerView recView;

    private Activity activity;
    private ExploreCategoriesPostRecAdaptor adaptor;

    public ExploreCategoriesPostGateway(Integer recViewId, View view, Context context, Activity activity) {
        RecViewId = recViewId;
        this.view = view;
        this.context = context;
        this.activity = activity;
    }

    /**
     * Initialise the recyclerView, set adapter, layout manager and scrolling
     */
    public void displayView(){
        recView = view.findViewById(RecViewId);
        this.context = recView.getContext();
        adaptor = new ExploreCategoriesPostRecAdaptor(activity);
        recView.setAdapter(adaptor);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                //adjust height of each item to 90% of RecyclerView height
                lp.height = (int) (getHeight() * .9);
                return super.checkLayoutParams(lp);
            }
        };
        recView.setLayoutManager(linearLayoutManager);
        recView.setNestedScrollingEnabled(false);
    }

    public void updateData(ArrayList<FullPost> postList){
        adaptor.updateData(postList);
    }


}
