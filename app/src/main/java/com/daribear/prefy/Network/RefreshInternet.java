package com.daribear.prefy.Network;

import android.content.Context;

import com.daribear.prefy.Activity.ActivityRepository;
import com.daribear.prefy.Explore.ExploreRepository;
import com.daribear.prefy.Popular.NewPopularRepository;
import com.daribear.prefy.Profile.CurrentUserRepository;

/**
 * Helper class which Refreshes the context in all of the view models.
 */
public class RefreshInternet {

    /**
     * Goes and refreshes all the viewmodels, to see if there is new internet.
     * @param appContext the context with which to use
     */
    public static void RefreshInternet(Context appContext){
        ExploreRepository exploreRepo = ExploreRepository.getInstance();
        if (exploreRepo.getInternetAvailable().getValue() != null) {
            if (!exploreRepo.getInternetAvailable().getValue()) {
                exploreRepo.refreshData();
            }
        } else {
            exploreRepo.refreshData();
        }
        CurrentUserRepository userRepo = CurrentUserRepository.getInstance();
        if (userRepo.getInternetAvailable().getValue() != null) {
            if (!userRepo.getInternetAvailable().getValue()) {
                userRepo.refreshData();
            }
        } else {
            userRepo.refreshData();
        }
        ActivityRepository activityRepository = ActivityRepository.getInstance();
        if (activityRepository.initInternet().getValue() != null) {
            if (!activityRepository.initInternet().getValue()) {
                activityRepository.setDataRefreshing(true);
                activityRepository.getVoteData();
                activityRepository.getCommentData();
                activityRepository.getFollowerData();
            }
        } else {
            activityRepository.setDataRefreshing(true);
            activityRepository.getVoteData();
            activityRepository.getCommentData();
            activityRepository.getFollowerData();
        }
        NewPopularRepository popularRepository = NewPopularRepository.getInstance(appContext);
        if (popularRepository.getPopularModelMutable().getValue() != null){
            if (popularRepository.getPopularModelMutable().getValue().getRetrievalType() != null){
                if (popularRepository.getPopularModelMutable().getValue().getRetrievalType().equals("Failed")){
                    popularRepository.getMoreData();
                }
            }
        }
    }
}
