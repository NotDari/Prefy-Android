package com.daribear.prefy.Utils.GetFollowing;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Called when the thread to get a list of user's and the status of whether the user is following them or not.
 */
public interface GetFollowingDelegate {
    /**
     * Called when the following retrieval thread is complete.
     *
     * @param successful whether the retrieval was successful
     * @param followList list of user ids and whether the active user is following them
     * @param type retrieval type
     */
    void completed(Boolean successful, HashMap<Long, Boolean> followList, String type);
}
