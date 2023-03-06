package com.example.prefy.Profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prefy.Activity.ActivityRepository;
import com.example.prefy.Profile.ProfilePostsRec.ProfileRetreiver.WholeProfile;
import com.example.prefy.customClasses.StandardPost;

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
}
