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

/**
 * Repository for handling activity-related data including comments, votes, and followers.
 * Acts as a central source for MutableLiveData for different activity types.
 * Implements interfaces to receive callback data from respective retrievers.
 */
public class ActivityRepository implements commentRetreiverInterface, voteActivityRetreiverInterface, followerRetrieverInterface {

    // Singleton instance
    private static ActivityRepository instance;

    // LiveData objects holding the activity lists
    private MutableLiveData<List<CommentActivity>> commentActivityMutable;
    private MutableLiveData<List<VoteActivity>> voteActivityMutable;
    @Getter
    private MutableLiveData<List<FollowerActivity>> followerActivityMutable;

    // LiveData to track internet availability and data refresh state
    private MutableLiveData<Boolean> internetAvailable;
    private MutableLiveData<Boolean> dataRefreshing;

    public ActivityRepository() {
        // Constructor can trigger initial data retrieval if needed
        // getCommentData();
        // getVoteData();
    }

    /**
     * Returns the singleton instance of ActivityRepository
     */
    public static ActivityRepository getInstance(){
        if (instance == null){
            instance = new ActivityRepository();
        }
        return instance;
    }

    /**
     * Check if the singleton instance is null
     */
    public static Boolean isInstanceNull(){
        return (instance == null);
    }

    /**
     * Initialise comment data retrieval.
     * @return MutableLiveData for CommentActivity (currently returns null; updates happen via callbacks)
     */
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

    /**
     * Initialise vote data retrieval.
     * @return MutableLiveData for VoteActivity (currently returns null; updates happen via callbacks)
     */
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

    /**
     * Initialise follower data retrieval.
     * @return MutableLiveData for FollowerActivity (currently returns null; updates happen via callbacks)
     */
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

    /**
     * Getter for comment activity LiveData
     */
    public MutableLiveData<List<CommentActivity>> getCommentActivityMutable() {
        return commentActivityMutable;
    }

    /**
     * Getter for vote activity LiveData
     */
    public MutableLiveData<List<VoteActivity>> getVoteActivityMutable() {
        return voteActivityMutable;
    }

    /**
     * Initialise internet availability LiveData
     */
    public MutableLiveData<Boolean> initInternet(){
        if (internetAvailable == null){
            internetAvailable = new MutableLiveData<>();
            internetAvailable.setValue(true);
        }
        return internetAvailable;
    }

    /**
     * Getter for data refreshing state
     */
    public MutableLiveData<Boolean> getDataRefreshing() {
        if (dataRefreshing == null){
            dataRefreshing = new MutableLiveData<>();
            dataRefreshing.setValue(false);
        }
        return dataRefreshing;
    }

    /**
     * Set data refreshing state
     */
    public void setDataRefreshing(Boolean dataRefreshing) {
        this.dataRefreshing.setValue(dataRefreshing);
    }

    /**
     * Reset the repository singleton instance
     */
    public void reset(){
        instance = null;
    }

    /**
     * Callback for comment retrieval completion
     */
    @Override
    public void completed(Boolean successful, ArrayList<CommentActivity> commentActivityList) {
        if (successful){
            this.commentActivityMutable.postValue(commentActivityList);
        }
        taskComplete(successful);
    }

    /**
     * Callback for vote retrieval completion
     */
    @Override
    public void votecompleted(Boolean successful, ArrayList<VoteActivity> voteActivityList) {
        if (successful){
            this.voteActivityMutable.postValue(voteActivityList);
        }
        taskComplete(successful);
    }

    /**
     * Updates LiveData related to internet availability and data refreshing after a task completes
     */
    private void taskComplete(Boolean successful){
        if (internetAvailable == null){
            internetAvailable = new MutableLiveData<>();
            internetAvailable.postValue(successful);
        } else {
            if (internetAvailable.getValue() != successful) {
                internetAvailable.postValue(successful);
            }
        }

        if (dataRefreshing.getValue()){
            dataRefreshing.postValue(false);
        }
    }

    /**
     * Callback for follower retrieval completion
     */
    @Override
    public void followerCompleted(Boolean successful, ArrayList<FollowerActivity> followerActivityList) {
        if (successful){
            this.followerActivityMutable.postValue(followerActivityList);
        }
        taskComplete(successful);
    }
}