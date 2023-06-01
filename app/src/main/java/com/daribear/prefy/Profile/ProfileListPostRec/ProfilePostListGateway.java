package com.daribear.prefy.Profile.ProfileListPostRec;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daribear.prefy.customClasses.Posts.StandardPost;

import java.util.ArrayList;

public class ProfilePostListGateway {
    private ArrayList<StandardPost> postList;
    private Integer RecViewId;
    private View view;
    private Context context;
    private RecyclerView recView;
    private ProfilePostListAdaptor adaptor;
    private Integer positionClicked;
    private String username, profileImageLink;
    private Activity parentActivity;

    public ProfilePostListGateway(ArrayList<StandardPost> postList,Integer recViewId, View view, Context context, Integer positionClicked, String username, String profileImageLink, Activity parentActivity) {
        this.postList = postList;
        RecViewId = recViewId;
        this.view = view;
        this.context = context;
        this.positionClicked = positionClicked;
        this.username = username;
        this.profileImageLink = profileImageLink;
        this.parentActivity = parentActivity;
    }


    public void displayView(){
        recView = view.findViewById(RecViewId);
        this.context = recView.getContext();
        adaptor = new ProfilePostListAdaptor(postList, username, profileImageLink, parentActivity);
        recView.setAdapter(adaptor);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                lp.height = (int) (getHeight() * .9);
                return super.checkLayoutParams(lp);
            }
        };
        recView.setLayoutManager(linearLayoutManager);
        recView.scrollToPosition(positionClicked);
        recView.setNestedScrollingEnabled(false);
    }


    public void destroyView(){
        this.view = null;
        //adaptor.viewDestroyed();
        adaptor = null;
        recView = null;

    }
}
