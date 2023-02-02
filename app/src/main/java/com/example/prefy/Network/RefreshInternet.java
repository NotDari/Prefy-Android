package com.example.prefy.Network;

import android.content.Context;

import com.example.prefy.Activity.ActivityRepository;
import com.example.prefy.Explore.ExploreRepository;
import com.example.prefy.Popular.PopularViewModel.PopularPostsRepository;
import com.example.prefy.Profile.CurrentUserRepository;

public class RefreshInternet {

    public static void RefreshInternet(){
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
            }
        } else {
            activityRepository.setDataRefreshing(true);
            activityRepository.getVoteData();
            activityRepository.getCommentData();
        }
    }
}
