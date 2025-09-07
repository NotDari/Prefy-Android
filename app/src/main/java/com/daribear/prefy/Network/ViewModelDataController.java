package com.daribear.prefy.Network;

import android.content.Context;

import com.daribear.prefy.Activity.ActivityRepository;
import com.daribear.prefy.Explore.ExploreRepository;
import com.daribear.prefy.Network.UploadController.UploadController;
import com.daribear.prefy.Popular.NewPopularRepository;
import com.daribear.prefy.Profile.CurrentUserRepository;

/**
 * Controller responsible for initialising the main repositories.
 */
public class ViewModelDataController {
    private Context ApplicationContext;

    public ViewModelDataController(Context applicationContext) {
        ApplicationContext = applicationContext;
    }

    /**
     * Initialises all the repositories, which are all singletons.
     * Starts the initial set ups.
     */
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
