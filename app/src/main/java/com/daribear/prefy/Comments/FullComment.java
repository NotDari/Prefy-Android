package com.daribear.prefy.Comments;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FullComment implements Cloneable{
    private Comment comment;
    private ArrayList<Comment> commentReplyList;
    private Integer replyCount;

    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }
}
