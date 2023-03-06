package com.example.prefy.Utils;

import android.content.Context;

import com.example.prefy.Explore.ExploreViewModel;
import com.example.prefy.Popular.PopularViewModel.PopViewModel;
import com.example.prefy.Profile.CurrentUserViewModel;
import com.example.prefy.customClasses.FullPost;

public class ItemDeleter {


    public static void deleteItem(FullPost fullPost, Context appContext){
        ExploreViewModel exploreViewModel = new ExploreViewModel();
        exploreViewModel.init();
        exploreViewModel.deleteItem(fullPost);

        CurrentUserViewModel currentUserViewModel = new CurrentUserViewModel();
        currentUserViewModel.init();
        currentUserViewModel.deleteItem(fullPost.getStandardPost());

        PopViewModel popViewModel = new PopViewModel();
        popViewModel.init(appContext);
        popViewModel.deleteItem(fullPost.getStandardPost());
    }
}
