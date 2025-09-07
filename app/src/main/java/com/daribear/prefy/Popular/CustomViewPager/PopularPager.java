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

/**
 * The pager which handles the loading and changing of the popular posts main pager.
 */
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


    /**
     * Adds the first post in the list as a fragment on the screen.
     * Creates the fragment and sends it the post and user data.
     */
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

    /**
     * Changes the post in the pager by getting the next item in the list,
     * creating a fragment for it and then removing the first one from the list.
     */
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

    /**
     * Removes the first item in the post list, and calls for more if there are less than 5.
     */
    private void removePost(){
        postList.remove(0);
        popViewModel.removeItem();
        if (postList.size() <= 5){
            popViewModel.getMoreData();
        }
        scrollActive = false;
    }

    /**
     * Preloads the images for the next two posts using glide, for smoother transitioning
     */
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

    /**
     * Use glide to preload an image
     * @param link
     */
    private void glidePreload(String link){
        if (!viewDestroyed){
            Glide.with(PopularPager.this.fragment)
                    .load(link)
                    .timeout(30)
                    .preload();
        }
    }

    /**
     * Called when the user has voted on a post. Gets the next fragment after a cooldown.
     *
     * @param cooldown cooldown before transitioning to the next page
     * @param removeVote flag that confirms going to the next fragment
     */
    public void voted(Boolean cooldown, Boolean removeVote){
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
