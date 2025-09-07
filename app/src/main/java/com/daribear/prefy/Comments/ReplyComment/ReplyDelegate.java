package com.daribear.prefy.Comments.ReplyComment;

import com.daribear.prefy.Comments.Comment;

import java.util.ArrayList;

/**
 * Delegate which handles the completion of receiving replies to a comment
 */
public interface ReplyDelegate {
    /**
     * Called when the
     * @param successful whether the retrieval was successful
     * @param commentList the list of retrieved Comments
     */
    void complete(Boolean successful, ArrayList<Comment> commentList);
}
