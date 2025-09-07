package com.daribear.prefy.Utils;

import android.content.Context;

import com.daribear.prefy.Explore.ExploreViewModel;
import com.daribear.prefy.Popular.NewPopularViewModel;
import com.daribear.prefy.Profile.CurrentUserViewModel;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.Search.SearchViewModel;
import com.daribear.prefy.customClasses.Posts.StandardPost;

/**
 * A helper class to allow the altering of items from view models.
 * When a post is deleted or a vote happens, it has to be updated throughout every saved post in every repository.
 * This class helps with that.
 */
public class ItemAlterer {

    /**
     * Delete a post from the explore repository and the currentuser view model
     * @param standardPost post to be deleted
     * @param appContext context to use
     */
    public static void deleteItem(StandardPost standardPost, Context appContext){
        ExploreViewModel exploreViewModel = new ExploreViewModel();
        exploreViewModel.init();
        exploreViewModel.deleteItem(standardPost);

        CurrentUserViewModel currentUserViewModel = new CurrentUserViewModel();
        currentUserViewModel.init();
        currentUserViewModel.deleteItem(standardPost);
    }

    /**
     * Adds an item vote to every repository, except to the one where the user voted on it.
     * @param postId id of post the vote occurs on
     * @param vote what the vote was
     * @param appContext context to use
     * @param type type of post
     */
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

    /**
     * Called when a user changes. Changes it in every repository
     * @param user user to change
     * @param appContext context to use
     */
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
