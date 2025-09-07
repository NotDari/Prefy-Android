package com.daribear.prefy.Comments;

/**
 * Deleagate to be implemented by classes that need to handle deletion of a comment
 */
public interface CommentDeleted {
    /**
     * Called by the delegate when a comment is deleted
     * @param commentId the id of the comment being deleted
     */
    void deleteClicked(Long commentId);
}
