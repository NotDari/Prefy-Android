package com.daribear.prefy.Activity.Votes;

import java.util.ArrayList;

/**
 * The interface which is used to alert that the votesActivities retrieval has been completed and returns whether it was
 * successful and the data if it was.
 */
public interface voteActivityRetreiverInterface {
    /**
     * Alert that the vote Activity retrieval has been completed
     * @param successful whether it was successful
     * @param voteActivityList the list of votesActivity
     */
    void votecompleted(Boolean successful, ArrayList<VoteActivity> voteActivityList);
}
