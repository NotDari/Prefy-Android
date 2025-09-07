package com.daribear.prefy.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.daribear.prefy.Activity.Comment.CommentActivity;
import com.daribear.prefy.Activity.Followers.FollowerActivity;
import com.daribear.prefy.Activity.Votes.VoteActivity;

import java.util.List;

import lombok.Getter;


/**
 * The activity view model which controls the data, between the repository, which retrieves the database from the server,
 * and the ui
 */
public class ActivityViewModel extends ViewModel{
    private MutableLiveData<List<CommentActivity>> commentActivityData;
    private MutableLiveData<List<VoteActivity>> voteActivityData;
    @Getter
    private MutableLiveData<List<FollowerActivity>> followerActivityData;
    private MutableLiveData<Boolean> internetAvailable;
    private MutableLiveData<Boolean> dataRefreshing;
    private ActivityRepository activityRepo;



        public void init(){
            activityRepo = ActivityRepository.getInstance();
            this.commentActivityData = activityRepo.getCommentActivityMutable();
            this.voteActivityData = activityRepo.getVoteActivityMutable();
            this.followerActivityData = activityRepo.getFollowerActivityMutable();
            this.internetAvailable = activityRepo.initInternet();
            this.dataRefreshing = activityRepo.getDataRefreshing();
        }

    public LiveData<List<VoteActivity>> getVoteData(){
            return voteActivityData;
        }

    public LiveData<List<CommentActivity>> getCommentData(){
        return commentActivityData;
    }

    public LiveData<Boolean> getInternetAvailable(){
        return internetAvailable;
    }

    public LiveData<Boolean> getDataRefreshing(){
        return dataRefreshing;
    }

    /**
     * Indicate to the repository to refresh the data
     */
    public void refreshData(){
        if (!activityRepo.getDataRefreshing().getValue()) {
            activityRepo.setDataRefreshing(true);
            activityRepo.getVoteData();
            activityRepo.getCommentData();
            activityRepo.getFollowerData();
        }

    }






}
