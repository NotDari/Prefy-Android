package com.example.prefy.Explore.ExploreCategories.ExploreCategoriesPosts;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prefy.Profile.User;
import com.example.prefy.customClasses.FullPost;
import com.example.prefy.customClasses.StandardPost;

import java.util.ArrayList;

public class ExploreCategoriesPostGateway {
    private Integer RecViewId;
    private View view;
    private Context context;
    private RecyclerView recView;
    private ExploreCategoriesPostRecAdaptor adaptor;

    public ExploreCategoriesPostGateway(Integer recViewId, View view, Context context) {
        RecViewId = recViewId;
        this.view = view;
        this.context = context;
    }


    public void displayView(){
        recView = view.findViewById(RecViewId);
        this.context = recView.getContext();
        adaptor = new ExploreCategoriesPostRecAdaptor();
        recView.setAdapter(adaptor);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
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


    public void destroyView(){
        this.view = null;
        //adaptor.viewDestroyed();
        adaptor = null;
        recView = null;

    }
}
