package com.daribear.prefy.Activity;

import androidx.lifecycle.MutableLiveData;

import com.daribear.prefy.Activity.Comment.CommentActivity;
import com.daribear.prefy.Activity.Comment.CommentActivityRetreiver;
import com.daribear.prefy.Activity.Comment.commentRetreiverInterface;
import com.daribear.prefy.Activity.Followers.FollowerActivity;
import com.daribear.prefy.Activity.Followers.FollowerActivityRetreiver;
import com.daribear.prefy.Activity.Followers.followerRetrieverInterface;
import com.daribear.prefy.Activity.Votes.VoteActivity;
import com.daribear.prefy.Activity.Votes.VoteActivityRetreiver;
import com.daribear.prefy.Activity.Votes.voteActivityRetreiverInterface;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;



public class ActivityRepository implements commentRetreiverInterface, voteActivityRetreiverInterface, followerRetrieverInterface {
    private static ActivityRepository instance;
    private MutableLiveData<List<CommentActivity>> commentActivityMutable;
    private MutableLiveData<List<VoteActivity>> voteActivityMutable;
    @Getter
    private MutableLiveData<List<FollowerActivity>> followerActivityMutable;

    private MutableLiveData<Boolean> internetAvailable;
    private MutableLiveData<Boolean> dataRefreshing;

    public ActivityRepository() {
        //getCommentData();
        //getVoteData();
    }

    public static ActivityRepository getInstance(){
        if (instance == null){
            instance = new ActivityRepository();
        }
        return instance;
    }

    public static Boolean isInstanceNull(){
        return (instance == null);
    }

    public MutableLiveData<CommentActivity> getCommentData(){
        CommentActivityRetreiver commentExecutor = new CommentActivityRetreiver(this);
        commentExecutor.initExecutor();
        if (commentActivityMutable == null) {
            commentActivityMutable = new MutableLiveData<>();
        }
        if (dataRefreshing == null){
            dataRefreshing = new MutableLiveData<>();
            dataRefreshing.setValue(true);
        }
        return null;
    }
    public MutableLiveData<VoteActivity> getVoteData(){
        VoteActivityRetreiver voteExecutor = new VoteActivityRetreiver(this);
        voteExecutor.initExecutor();
        if (voteActivityMutable == null) {
            voteActivityMutable = new MutableLiveData<>();
        }
        if (dataRefreshing == null){
            dataRefreshing = new MutableLiveData<>();
            dataRefreshing.setValue(true);
        }
        return null;
    }

    public MutableLiveData<FollowerActivity> getFollowerData(){
        FollowerActivityRetreiver followerExecutor = new FollowerActivityRetreiver(this);
        followerExecutor.initExecutor();
        if (followerActivityMutable == null) {
            followerActivityMutable = new MutableLiveData<>();
        }
        if (dataRefreshing == null){
            dataRefreshing = new MutableLiveData<>();
            dataRefreshing.setValue(true);
        }
        return null;
    }

    public MutableLiveData<List<CommentActivity>> getCommentActivityMutable() {
        return commentActivityMutable;
    }

    public MutableLiveData<List<VoteActivity>> getVoteActivityMutable() {
        return voteActivityMutable;
    }

    public MutableLiveData<Boolean> initInternet(){
        if (internetAvailable == null){
            internetAvailable = new MutableLiveData<>();
            internetAvailable.setValue(true);
        }
        return internetAvailable;
    }

    public MutableLiveData<Boolean> getDataRefreshing() {
        if (dataRefreshing == null){
            dataRefreshing = new MutableLiveData<>();
            dataRefreshing.setValue(false);
        }
        return dataRefreshing;
    }

    public void setDataRefreshing(Boolean dataRefreshing) {
        this.dataRefreshing.setValue(dataRefreshing);
    }


    public void reset(){
        instance = null;
    }
    @Override
    public void completed(Boolean successful, ArrayList<CommentActivity> commentActivityList) {
        if (successful){
            this.commentActivityMutable.postValue(commentActivityList);
        }
        taskComplete(successful);
    }

    @Override
    public void votecompleted(Boolean successful, ArrayList<VoteActivity> voteActivityList) {
        if (successful){
            this.voteActivityMutable.postValue(voteActivityList);
        }
        taskComplete(successful);
    }

    private void taskComplete(Boolean successful){
        if (internetAvailable == null){
            internetAvailable = new MutableLiveData<>();
            internetAvailable.postValue(successful);
        } else {
            System.out.println("Sdad internetAvailable:" + internetAvailable.getValue());
            if (internetAvailable.getValue() != successful) {
                internetAvailable.postValue(successful);
            }
        }

        if (dataRefreshing.getValue()){
            dataRefreshing.postValue(false);
        }
    }


    @Override
    public void followerCompleted(Boolean successful, ArrayList<FollowerActivity> followerActivityList) {
        if (successful){
            this.followerActivityMutable.postValue(followerActivityList);
        }
        taskComplete(successful);
    }
}
