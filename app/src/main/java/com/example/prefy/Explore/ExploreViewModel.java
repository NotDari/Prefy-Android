package com.example.prefy.Explore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.prefy.Activity.ActivityRepository;
import com.example.prefy.Activity.Comment.CommentActivity;
import com.example.prefy.Activity.Votes.VoteActivity;
import com.example.prefy.customClasses.FullPost;

import java.util.List;

public class ExploreViewModel extends ViewModel {
    private MutableLiveData<ExplorePostSet> explorePostSetMutable;
    private MutableLiveData<List<FullPost>> featuredPostList;
    private MutableLiveData<Boolean> internetAvailable;
    private ExploreRepository exploreRepo;



    public void init(){
        exploreRepo = ExploreRepository.getInstance();
        this.featuredPostList = exploreRepo.getFeaturedPostList();
        this.explorePostSetMutable = exploreRepo.getExplorePostSetMutable();
        this.internetAvailable = exploreRepo.getInternetAvailable();
    }

    public MutableLiveData<ExplorePostSet> getExplorePostSetMutable() {
        return explorePostSetMutable;
    }

    public MutableLiveData<List<FullPost>> getFeaturedPostList() {
        return featuredPostList;
    }

    public MutableLiveData<Boolean> getInternetAvailable() {
        return internetAvailable;
    }

    public void updateData(Double lastCreationDate){
        exploreRepo.updateData(lastCreationDate);
    }

    public void refresh(){
        exploreRepo.refreshData();
    }
}
