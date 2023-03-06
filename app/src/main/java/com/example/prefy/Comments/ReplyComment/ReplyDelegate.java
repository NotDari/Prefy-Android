package com.example.prefy.Comments.ReplyComment;

import com.example.prefy.Comments.Comment;

import java.util.ArrayList;

public interface ReplyDelegate {
    void complete(Boolean successful, ArrayList<Comment> commentList);
}
