package com.daribear.prefy.Utils;

import android.content.Context;

import com.daribear.prefy.Explore.ExploreViewModel;
import com.daribear.prefy.Popular.NewPopularSystem.NewPopularViewModel;
import com.daribear.prefy.Profile.CurrentUserViewModel;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.Search.SearchViewModel;
import com.daribear.prefy.customClasses.Posts.FullPost;
import com.daribear.prefy.customClasses.Posts.StandardPost;

public class ItemAlterer {


    public static void deleteItem(StandardPost standardPost, Context appContext){
        ExploreViewModel exploreViewModel = new ExploreViewModel();
        exploreViewModel.init();
        exploreViewModel.deleteItem(standardPost);

        CurrentUserViewModel currentUserViewModel = new CurrentUserViewModel();
        currentUserViewModel.init();
        currentUserViewModel.deleteItem(standardPost);
    }

    public static void itemVote(Long postId, String vote, Context appContext, String type){
        if (!type.equals("Popular")) {
            NewPopularViewModel newPopularViewModel = new NewPopularViewModel();
            newPopularViewModel.init(appContext);
            newPopularViewModel.itemVote(postId, vote);
        }
        if (!type.equals("Explore")) {
            ExploreViewModel exploreViewModel = new ExploreViewModel();
            exploreViewModel.init();
            exploreViewModel.itemVoted(postId, vote);
        }

        if (!type.equals("CurrentUser")) {
            CurrentUserViewModel currentUserViewModel = new CurrentUserViewModel();
            currentUserViewModel.init();
            currentUserViewModel.itemVote(postId, vote);
        }


    }


    public static void userChange(User user, Context appContext){
        ExploreViewModel exploreViewModel = new ExploreViewModel();
        exploreViewModel.init();
        exploreViewModel.userAltered(user);


        NewPopularViewModel newPopularViewModel = new NewPopularViewModel();
        newPopularViewModel.init(appContext);
        newPopularViewModel.userAltered(user);



        SearchViewModel searchViewModel = new SearchViewModel();
        searchViewModel.init();
        searchViewModel.userAltered(user);



    }


}
