package com.daribear.prefy.Comments.ReplyComment;

import com.daribear.prefy.Comments.Comment;

import java.util.ArrayList;

/**
 * The delegate is used for when the logged in user deletes a reply to a comment.
 */
public interface CommentReplyDeletedDelegate {
    /**
     * Alerts that the reply comment has been deleted
     * @param commentReplyItem item to be deleted
     */
    void deletedReply(CommentReplyItem commentReplyItem);
}
