package com.daribear.prefy.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.daribear.prefy.Activity.Comment.CommentActivity;
import com.daribear.prefy.Activity.Votes.VoteActivity;

import java.util.List;

public class ActivityViewModel extends ViewModel{
    private MutableLiveData<List<CommentActivity>> commentActivityData;
    private MutableLiveData<List<VoteActivity>> voteActivityData;
    private MutableLiveData<Boolean> internetAvailable;
    private MutableLiveData<Boolean> dataRefreshing;
    private ActivityRepository activityRepo;



        public void init(){
            activityRepo = ActivityRepository.getInstance();
            this.commentActivityData = activityRepo.getCommentActivityMutable();
            this.voteActivityData = activityRepo.getVoteActivityMutable();
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

    public void refreshData(){
        if (!activityRepo.getDataRefreshing().getValue()) {
            activityRepo.setDataRefreshing(true);
            activityRepo.getVoteData();
            activityRepo.getCommentData();
        }

    }






}
