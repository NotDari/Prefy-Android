package com.example.prefy.Profile;

import androidx.lifecycle.MutableLiveData;

import com.example.prefy.Activity.ActivityRepository;
import com.example.prefy.Activity.Comment.CommentActivity;
import com.example.prefy.Activity.Votes.VoteActivity;
import com.example.prefy.Explore.ExplorePostSet;
import com.example.prefy.Profile.ProfilePostsRec.ProfileRetreiver.ProfilePostsRetreiver.ProfileHandlerInt;
import com.example.prefy.Profile.ProfilePostsRec.ProfileRetreiver.WholeProfile;
import com.example.prefy.Utils.ServerAdminSingleton;
import com.example.prefy.customClasses.FullPost;
import com.example.prefy.customClasses.StandardPost;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

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
