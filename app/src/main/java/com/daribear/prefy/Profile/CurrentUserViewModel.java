package com.daribear.prefy.Profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.WholeProfile;
import com.daribear.prefy.customClasses.Posts.StandardPost;

/**
 * The view model which acts as a gateway between the CurrentUserRepository and the UI.
 * Handles the data handling of the logged in user, for their profile.
 */
public class CurrentUserViewModel extends ViewModel {
    private CurrentUserRepository userRepo;
    private MutableLiveData<WholeProfile> wholeProfileMutable;
    private MutableLiveData<Boolean> internetAvailable;

    public void init(){
        userRepo = CurrentUserRepository.getInstance();
        this.wholeProfileMutable = userRepo.getWholeProfileMutable();
        this.internetAvailable = userRepo.getInternetAvailable();
    }

    public LiveData<WholeProfile> getWholeProfileMutable() {
        return wholeProfileMutable;
    }

    public void refreshData(){
        userRepo.refreshData();
    }

    public LiveData<Boolean> getInternetAvailable(){
        return internetAvailable;
    }

    public void deleteItem(StandardPost post){
        userRepo.deleteItem(post);
    }

    public void itemVote(Long postId, String vote){
        userRepo.itemVote(postId, vote);
    }
}
