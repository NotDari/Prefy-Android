package com.daribear.prefy.Activity.Followers;

import java.util.ArrayList;

/**
 * The interface which is used to alert that the followActivities retrieval has been completed and returns whether it was
 * successful and the data if it was.
 */
public interface followerRetrieverInterface {

    /**
     * Alert that the follow Activity retrieval has been completed
     * @param successful whether it was successful
     * @param followerActivityList the list of followActivity
     */
    void followerCompleted(Boolean successful, ArrayList<FollowerActivity> followerActivityList);
}
