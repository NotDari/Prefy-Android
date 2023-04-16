package com.daribear.prefy.Comments;

public interface CommentReplyClicked {
    void mainReplyClicked(String replyUsername, Long replyId);

    void subReplyClicked(String replyUsername, Long parentID, Long subParentID);
}
