package com.daribear.prefy.Comments;


/**
 * Delegate interface for handling reply clicks in comments
 * Supports both main replies and sub-replies
 */
public interface CommentReplyClicked {
    /**
     * Called when user clicks reply on main comment
     * @param replyUsername username of the user being replied to
     * @param replyId id of the comment being replied to
     */
    void mainReplyClicked(String replyUsername, Long replyId);

    /**
     * Called when user clicks reply on a sub-reply (nested reply)
     * @param replyUsername username of the user being replied to
     * @param parentID id of the parent comment
     * @param subParentID id of the sub-reply comment
     */
    void subReplyClicked(String replyUsername, Long parentID, Long subParentID);
}
