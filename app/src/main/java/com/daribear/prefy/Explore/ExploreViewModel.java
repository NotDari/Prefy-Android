package com.daribear.prefy.Explore;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.daribear.prefy.Profile.User;
import com.daribear.prefy.customClasses.Posts.FullPost;
import com.daribear.prefy.customClasses.Posts.StandardPost;

import java.util.List;

/**
 * The viewmodel which represents the explore data.
 * Contains the list of featured posts and recent posts.
 * Acts as the middleman between the ui and the repositories.
 */
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



    public void refresh(){
        exploreRepo.refreshData();
    }

    public void deleteItem(StandardPost standardPost){
        exploreRepo.deleteItem(standardPost);
    }

    public void itemVoted(Long postId, String vote){
        exploreRepo.itemVote(postId, vote);
    }
    public void userAltered(User user){
        exploreRepo.userAltered(user);
    }
}
