package com.daribear.prefy.Comments.ReplyComment;

import com.daribear.prefy.Comments.Comment;

import java.util.ArrayList;

public interface ReplyDelegate {
    void complete(Boolean successful, ArrayList<Comment> commentList);
}
