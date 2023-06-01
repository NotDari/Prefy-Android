package com.daribear.prefy.Search;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;

import java.util.ArrayList;

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
    //TODO Can optimise all RecView Setters and stuff throughout the whole application
    public void updateData(ArrayList<User> searchUserArrayList) {
        adaptor.setSearchUserArrayList(searchUserArrayList);
        adaptor.notifyDataSetChanged();
    }

    public void resetData(){
        Integer size = adaptor.getItemCount();
        adaptor.setSearchUserArrayList(new ArrayList<>());
        adaptor.notifyItemRangeRemoved(0, size);
    }





    private void addData(ArrayList<User> searchUserArrayList){

        adaptor.addUsers(searchUserArrayList);
    }


    public void destroyView(){
        this.view = null;
        //adaptor.viewDestroyed();
        adaptor = null;
        recView = null;
        this.context = null;

    }




    @Override
    public void topReached() {
        if (adaptor.getItemCount() > 10) {
            SearchViewModel searchViewModel = new SearchViewModel();
            searchViewModel.init();
            searchViewModel.viewScrolled();
            //viewScrolledTop(rating);
        }
    }


    @Override
    public void topReached(String lastUsername) {
        if (adaptor.getItemCount() > 10) {
            SearchViewModel searchViewModel = new SearchViewModel();
            searchViewModel.init();
            searchViewModel.viewScrolled();
            // viewScrolledSearch(lastUsername);
            System.out.println("Sdad string Reached");
        }
    }



}
