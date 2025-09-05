package com.daribear.prefy.Activity.Comment;

import java.util.ArrayList;

/**
 * The interface which is used to alert that the commentsActivities retrieval has been successful.
 */
public interface commentRetreiverInterface {
    /**
     * Alert that the comments Activity has been successful
     * @param successful whether it was successful
     * @param commentActivityList the list of commentsActivity
     */
    void completed(Boolean successful, ArrayList<CommentActivity> commentActivityList);
}
