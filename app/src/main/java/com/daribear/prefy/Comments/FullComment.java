package com.daribear.prefy.Comments;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FullComment {
    private Comment comment;
    private ArrayList<Comment> commentReplyList;
    private Integer replyCount;
}
