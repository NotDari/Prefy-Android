package com.example.prefy.Network;

import android.content.Context;

import com.example.prefy.Activity.ActivityRepository;
import com.example.prefy.Explore.ExploreRepository;
import com.example.prefy.Explore.ExploreViewModel;
import com.example.prefy.Network.UploadController.UploadController;
import com.example.prefy.Popular.PopularViewModel.PopViewModel;
import com.example.prefy.Popular.PopularViewModel.PopularPostsRepository;
import com.example.prefy.Profile.CurrentUserRepository;

public class ViewModelDataController {
    private Context ApplicationContext;

    public ViewModelDataController(Context applicationContext) {
        ApplicationContext = applicationContext;
    }

    public void initViewModels(){
        PopularPostsRepository popRepo = PopularPostsRepository.getInstance(ApplicationContext);
        if (popRepo.getDataStatus() == 0) {
            popRepo.getIntitialData();
        }
        ActivityRepository activityRepo = ActivityRepository.getInstance();
        activityRepo.getCommentData();
        activityRepo.getVoteData();
        ExploreRepository exploreRepo = ExploreRepository.getInstance();
        exploreRepo.init();
        CurrentUserRepository userRepo = CurrentUserRepository.getInstance();
        userRepo.getCurrentUserData();
        UploadController.attempUpload(ApplicationContext);
    }
}
