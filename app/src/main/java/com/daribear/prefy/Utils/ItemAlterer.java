package com.daribear.prefy.Utils;

import android.content.Context;

import com.daribear.prefy.Explore.ExploreViewModel;
import com.daribear.prefy.Popular.NewPopularSystem.NewPopularRepository;
import com.daribear.prefy.Popular.OldPopularSystem.PopViewModel;
import com.daribear.prefy.Profile.CurrentUserViewModel;
import com.daribear.prefy.customClasses.FullPost;

public class ItemAlterer {


    public static void deleteItem(FullPost fullPost, Context appContext){
        ExploreViewModel exploreViewModel = new ExploreViewModel();
        exploreViewModel.init();
        exploreViewModel.deleteItem(fullPost);

        CurrentUserViewModel currentUserViewModel = new CurrentUserViewModel();
        currentUserViewModel.init();
        currentUserViewModel.deleteItem(fullPost.getStandardPost());

    }

    public static void itemVote(Long postId, String vote, Context appContext){
        NewPopularRepository newPopularRepository = new NewPopularRepository(appContext);
        newPopularRepository.itemVote(postId, vote);

        ExploreViewModel exploreViewModel = new ExploreViewModel();
        exploreViewModel.init();
        exploreViewModel.itemVoted(postId, vote);

        CurrentUserViewModel currentUserViewModel = new CurrentUserViewModel();
        currentUserViewModel.init();
        currentUserViewModel.itemVote(postId, vote);


    }
}
