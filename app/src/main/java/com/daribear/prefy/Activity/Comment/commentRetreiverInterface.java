package com.daribear.prefy.Activity.Comment;

import java.util.ArrayList;

/**
 * The interface which is used to alert that the commentsActivities retrieval has been completed and returns whether it was
 * successful and the data if it was.
 */
public interface commentRetreiverInterface {
    /**
     * Alert that the comments Activity retrieval has been completed
     * @param successful whether it was successful
     * @param commentActivityList the list of commentsActivity
     */
    void completed(Boolean successful, ArrayList<CommentActivity> commentActivityList);
}
