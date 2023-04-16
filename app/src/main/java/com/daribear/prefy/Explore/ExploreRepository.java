package com.daribear.prefy.Explore;

import androidx.lifecycle.MutableLiveData;

import com.daribear.prefy.customClasses.FullPost;

import java.util.ArrayList;
import java.util.List;

public class ExploreRepository implements ExploreWholeInterface{
    private static ExploreRepository instance;
    private MutableLiveData<ExplorePostSet> explorePostSetMutable;
    private MutableLiveData<List<FullPost>> featuredPostList;
    private MutableLiveData<Boolean> internetAvailable;


    public ExploreRepository() {
        //getCommentData();
        //getVoteData();
    }

    public static ExploreRepository getInstance(){
        if (instance == null){
            instance = new ExploreRepository();
        }
        return instance;
    }

    public static Boolean isInstanceNull(){
        return (instance == null);
    }

    public void init(){
        explorePostSetMutable = new MutableLiveData<>();
        featuredPostList = new MutableLiveData<>();
        internetAvailable = new MutableLiveData<>();
        ExplorePageExecutor executor = new ExplorePageExecutor("All", this, 18, 0, false);
        executor.initExecutor();
    }

    public MutableLiveData<ExplorePostSet> getExplorePostSetMutable() {
        return explorePostSetMutable;
    }

    public void deleteItem(FullPost fullPost){
        ExplorePostSet explorePostSet = explorePostSetMutable.getValue();
        ArrayList<FullPost> fullPostList = explorePostSet.getPostList();
        for (int i = 0; i < fullPostList.size(); i ++){
            if (fullPostList.get(i).getStandardPost().getPostId().equals(fullPost.getStandardPost().getPostId())){
                fullPostList.remove(fullPostList.get(i));
            }
        }
        explorePostSetMutable.setValue(explorePostSet);
    }

    public void itemVote(Long postId, String vote){
        ExplorePostSet explorePostSet = explorePostSetMutable.getValue();
        ArrayList<FullPost> fullPostList = explorePostSet.getPostList();
        for (int i = 0; i < fullPostList.size(); i ++){
            if (fullPostList.get(i).getStandardPost().getPostId().equals(postId)){
                fullPostList.get(i).getStandardPost().setCurrentVote(vote);
            }
        }
        explorePostSetMutable.setValue(explorePostSet);
    }


    public MutableLiveData<List<FullPost>> getFeaturedPostList() {
        return featuredPostList;
    }

    public MutableLiveData<Boolean> getInternetAvailable() {
        if (internetAvailable == null){
            internetAvailable.postValue(null);
        }
        return internetAvailable;
    }

    public void updateData(Double lastCreationDate){
        if (lastCreationDate == null){
            lastCreationDate = 999999999999999999999999999999D;
        }
        System.out.println("Sdad update Requested");
        ExplorePageExecutor executor = new ExplorePageExecutor("All", this, 18, 0, true);
        executor.initExecutor();
    }

    public void refreshData(){
        ExplorePageExecutor executor = new ExplorePageExecutor("All", this, 18, 0, false);
        executor.initExecutor();
    }

    public void reset(){
        instance = null;
    }


    @Override
    public void completed(Boolean successful, Boolean update, ExplorePostSet explorePostSet, ArrayList<FullPost> fullFeaturedPostArrayList) {
        internetAvailable.postValue(successful);
        if (successful){
            if (!update){
                featuredPostList.postValue(fullFeaturedPostArrayList);
                explorePostSetMutable.postValue(explorePostSet);
            } else {
                if (explorePostSet != null){
                    if (explorePostSet.getPostList() != null){
                        for (int i = 0; i < explorePostSet.getPostList().size(); i ++){
                            ExplorePostSet previousExploreSet = explorePostSetMutable.getValue();
                            previousExploreSet.getPostList().add(explorePostSet.getPostList().get(i));
                            explorePostSetMutable.postValue(previousExploreSet);
                        }
                    }
                }
            }
        }
    }
}
