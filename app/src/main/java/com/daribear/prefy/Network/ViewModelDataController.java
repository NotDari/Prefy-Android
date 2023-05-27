package com.daribear.prefy.Network;

import android.content.Context;

import com.daribear.prefy.Activity.ActivityRepository;
import com.daribear.prefy.Explore.ExploreRepository;
import com.daribear.prefy.Network.UploadController.UploadController;
import com.daribear.prefy.Popular.NewPopularSystem.NewPopularRepository;
import com.daribear.prefy.Profile.CurrentUserRepository;

public class ViewModelDataController {
    private Context ApplicationContext;

    public ViewModelDataController(Context applicationContext) {
        ApplicationContext = applicationContext;
    }

    public void initViewModels(){
        NewPopularRepository popRepo = NewPopularRepository.getInstance(ApplicationContext);
        ActivityRepository activityRepo = ActivityRepository.getInstance();
        activityRepo.getCommentData();
        activityRepo.getVoteData();
        activityRepo.getFollowerData();
        ExploreRepository exploreRepo = ExploreRepository.getInstance();
        exploreRepo.init();
        CurrentUserRepository userRepo = CurrentUserRepository.getInstance();
        userRepo.getCurrentUserData();
        UploadController.attemptUpload(ApplicationContext);
    }
}
