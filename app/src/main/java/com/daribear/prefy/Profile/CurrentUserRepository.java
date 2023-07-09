package com.daribear.prefy.Profile;

import androidx.lifecycle.MutableLiveData;

import com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.ProfilePostsRetreiver.ProfileHandlerInt;
import com.daribear.prefy.Profile.ProfilePostsRec.ProfileRetreiver.WholeProfile;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.daribear.prefy.customClasses.Posts.StandardPost;

import java.util.ArrayList;

public class CurrentUserRepository implements ProfileHandlerInt {
    private static CurrentUserRepository instance;
    private MutableLiveData<WholeProfile> wholeProfileMutable;
    private MutableLiveData<Boolean> internetAvailable;

    public CurrentUserRepository() {
    }

    public static CurrentUserRepository getInstance(){
        if (instance == null){
            instance = new CurrentUserRepository();
        }
        return instance;
    }

    public static Boolean isInstanceNull(){
        return (instance == null);
    }

    public void deleteItem(StandardPost standardPost){
        WholeProfile wholeProfile = wholeProfileMutable.getValue();
        ArrayList<StandardPost> postList = wholeProfile.getPostListContainer().getPostList();
        for (int i = 0; i < postList.size(); i ++){
            if (standardPost.getPostId().equals(postList.get(i).getPostId())){
                postList.remove(postList.get(i));
            }
        }
        wholeProfileMutable.setValue(wholeProfile);
    }

    public void itemVote(Long postId, String vote){
        if (wholeProfileMutable.getValue() != null) {
            Boolean changed = false;
            WholeProfile wholeProfile = wholeProfileMutable.getValue();
            if (wholeProfile != null) {
                ArrayList<StandardPost> postList = wholeProfile.getPostListContainer().getPostList();
                for (int i = 0; i < postList.size(); i++) {
                    if (postId.equals(postList.get(i).getPostId())) {
                        if (vote.equals("right")){
                            changed = true;
                            postList.get(i).setRightVotes(postList.get(i).getRightVotes() + 1);
                        } else if (vote.equals("left")) {
                            changed = true;
                            postList.get(i).setLeftVotes(postList.get(i).getLeftVotes() + 1);
                        } else {
                            changed = false;
                        }
                        postList.get(i).setCurrentVote(vote);
                    }
                }
                if (changed) {
                    wholeProfileMutable.setValue(wholeProfile);
                }
            }
        }
    }

    public void getCurrentUserData(){
        wholeProfileMutable = new MutableLiveData<>();
        GetUserDetailsExecutor wholeProfileExecutor = new GetUserDetailsExecutor(ServerAdminSingleton.getInstance().getLoggedInId(), this);
        wholeProfileExecutor.initExecutor();
    }

    public MutableLiveData<WholeProfile> getWholeProfileMutable() {
        return wholeProfileMutable;
    }

    public void refreshData(){
        GetUserDetailsExecutor wholeProfileExecutor = new GetUserDetailsExecutor(ServerAdminSingleton.getInstance().getLoggedInId(), this);
        wholeProfileExecutor.initExecutor();
    }

    public MutableLiveData<Boolean> getInternetAvailable() {
        if (internetAvailable == null){
            internetAvailable = new MutableLiveData<>();
        }
        return internetAvailable;
    }

    public void reset(){
        instance = null;
    }

    @Override
    public void taskDone(Boolean successful, WholeProfile wholeProfile) {
        if (successful){
            this.wholeProfileMutable.postValue(wholeProfile);
        } else {
            if (internetAvailable == null){
                internetAvailable = new MutableLiveData<>();
            }
            internetAvailable.postValue(false);
        }
    }
}
