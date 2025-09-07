package com.daribear.prefy.Explore;

import androidx.lifecycle.MutableLiveData;

import com.daribear.prefy.Ads.AdTracker;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.customClasses.Posts.FullPost;
import com.daribear.prefy.customClasses.Posts.StandardPost;

import java.util.ArrayList;
import java.util.List;

/**
 * The repository which handles
 */
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

    /**
     * Removes a post from the explore repository
     * @param standardPost the psot to remove
     */
    public void deleteItem(StandardPost standardPost){
        ExplorePostSet explorePostSet = explorePostSetMutable.getValue();
        ArrayList<FullPost> fullPostList = explorePostSet.getPostList();
        for (int i = 0; i < fullPostList.size(); i ++){
            if (fullPostList.get(i).getStandardPost().getPostId().equals(standardPost.getPostId())){
                fullPostList.remove(fullPostList.get(i));
            }
        }
        explorePostSetMutable.setValue(explorePostSet);
    }

    /**
     * When the user votes on a post, this is called. Alters the post in the repository to represent the change the new vote causes
     * @param postId post that was voted on
     * @param vote what the user voted.
     */
    public void itemVote(Long postId, String vote){
        if (explorePostSetMutable.getValue() != null) {
            Boolean changed = false;
            ExplorePostSet explorePostSet = explorePostSetMutable.getValue();
            if (explorePostSet != null) {
                ArrayList<FullPost> fullPostList = explorePostSet.getPostList();
                for (int i = 0; i < fullPostList.size(); i++) {
                    if (fullPostList.get(i).getStandardPost().getPostId().equals(postId)) {
                        if (vote.equals("right")){
                            changed = true;
                            fullPostList.get(i).getStandardPost().setRightVotes(fullPostList.get(i).getStandardPost().getRightVotes() + 1);
                        } else if (vote.equals("left")) {
                            changed = true;
                            fullPostList.get(i).getStandardPost().setLeftVotes(fullPostList.get(i).getStandardPost().getLeftVotes() + 1);
                        } else {
                            changed = false;
                        }
                        fullPostList.get(i).getStandardPost().setCurrentVote(vote);
                    }
                }
                if (changed) {
                    explorePostSetMutable.setValue(explorePostSet);
                }
            }
        }
    }

    /**
     * Called when one of the users who owned a post, has changed details, so it updates the user.
     * @param user user which has been altered
     */
    public void userAltered(User user){
        if (explorePostSetMutable.getValue() != null) {
            Boolean changed = false;
            ExplorePostSet explorePostSet = explorePostSetMutable.getValue();
            if (explorePostSet != null) {
                ArrayList<FullPost> fullPostList = explorePostSet.getPostList();
                for (int i = 0; i < fullPostList.size(); i++) {
                    if (fullPostList.get(i).getUser().getId().equals(user.getId())) {
                        fullPostList.get(i).setUser(user);
                        changed = true;
                    }
                }
                if (changed) {
                    explorePostSetMutable.setValue(explorePostSet);
                }
            }
        }
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


    /**
     * Refresh the data in this repository
     */
    public void refreshData(){
        ExplorePageExecutor executor = new ExplorePageExecutor("All", this, 18, 0, false);
        executor.initExecutor();
    }

    public void reset(){
        instance = null;
    }


    /**
     * Called when the ExplorePageExecutor has completed, updates the data in the repository.
     * @param successful whether it was successful
     * @param update whether the data was an update or the initial setting of data
     * @param explorePostSet the data of recent posts
     * @param fullFeaturedPostArrayList the featured post data.
     */
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
            AdTracker.getInstance().otherViewed();
        }
    }
}
