package com.daribear.prefy.Explore;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.daribear.prefy.customClasses.Posts.FullPost;

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

    public void deleteItem(FullPost fullPost){
        exploreRepo.deleteItem(fullPost);
    }

    public void itemVoted(Long postId, String vote){
        exploreRepo.itemVote(postId, vote);
    }
}
