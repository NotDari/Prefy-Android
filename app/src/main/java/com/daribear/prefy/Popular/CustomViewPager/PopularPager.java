package com.daribear.prefy.Popular.CustomViewPager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.daribear.prefy.Activities.MainActivity;
import com.daribear.prefy.Popular.NewPopularViewModel;
import com.daribear.prefy.Popular.PopularPageFragment;
import com.daribear.prefy.Popular.PopularPostVote;
import com.daribear.prefy.R;
import com.daribear.prefy.customClasses.Posts.FullPost;

import java.util.ArrayList;

import lombok.Setter;

public class PopularPager extends FrameLayout{
    @Setter
    private ArrayList<FullPost> postList;
    private Fragment fragment;
    private Integer pos;
    private FragmentManager fm;
    private Boolean scrollActive, viewDestroyed;
    private NewPopularViewModel popViewModel;
    @Setter
    private PopNoPostsDelegate noPostsDelegate;



    public PopularPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        createViews(context);
        createVariables();
    }



    private void createViews(Context context){
    }

    private void createVariables(){
        pos = 0;
        scrollActive = false;
        viewDestroyed = false;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
        popViewModel = new ViewModelProvider(fragment.getActivity()).get(NewPopularViewModel.class);
        popViewModel.init(fragment.getContext().getApplicationContext());
    }

    public void init(){
        addFragment();
    }



    private void addFragment(){
        fm = fragment.getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if (postList.size() > 0){
            FullPost fullPost = postList.get(0);
            PopularPageFragment fragment = new PopularPageFragment();
            Bundle args = new Bundle();
            args.putParcelable("post", fullPost.getStandardPost());
            args.putParcelable("user", fullPost.getUser());
            fragment.setArguments(args);
            fragmentTransaction.add(PopularPager.this.getId(),fragment);
            fragmentTransaction.commit();
            fragmentTransaction.runOnCommit(new Runnable() {
                @Override
                public void run() {
                    scrollActive = false;
                }
            });
        }
    }

    private void alterFragment(){
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_in, 0);
        if (postList.size() > 1){
            FullPost fullPost = postList.get(1);
            PopularPageFragment fragment = new PopularPageFragment();
            Bundle args = new Bundle();
            args.putParcelable("post", fullPost.getStandardPost());
            args.putParcelable("user", fullPost.getUser());
            fragment.setArguments(args);
            if (!viewDestroyed) {
                fragmentTransaction.add(PopularPager.this.getId(),fragment);
                fragmentTransaction.commit();
            }
            removePost();
            preLoadImages();
        } else {
            removePost();
            if (!viewDestroyed) {
                noPostsDelegate.noMorePosts();
            }
        }
    }
    private void removePost(){
        System.out.println("Sdad size:" + postList.size());
        postList.remove(0);
        popViewModel.removeItem();
        if (postList.size() <= 5){
            popViewModel.getMoreData();
        }
        scrollActive = false;
    }

    private void preLoadImages(){
        if (postList.size() > 2) {
            String imageLink1 = postList.get(1).getStandardPost().getImageURL();
            String ppLink1 = postList.get(1).getUser().getProfileImageURL();
            glidePreload(imageLink1);
            glidePreload(ppLink1);
        }
        if (postList.size() > 3){
            String imageLink2 = postList.get(2).getStandardPost().getImageURL();
            String ppLink2 = postList.get(2).getUser().getProfileImageURL();
            glidePreload(imageLink2);
            glidePreload(ppLink2);
        }

    }

    private void glidePreload(String link){
        if (!viewDestroyed){
            Glide.with(PopularPager.this.fragment)
                    .load(link)
                    .timeout(30)
                    .preload();
        }
    }

    public void voted(Boolean cooldown, Boolean removeVote){
        System.out.println("Sdad voted!@!");
        if (!scrollActive) {
            if (removeVote) {
                scrollActive = true;
                if (cooldown) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!viewDestroyed) {
                                alterFragment();
                            }
                        }
                    }, 400);

                } else {
                    if (!viewDestroyed) {
                        alterFragment();
                    }
                }
            }
        }
    }

    public void viewDestroyed(){
        viewDestroyed = true;
    }


}
