package com.daribear.prefy.Comments;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * Class representing a full comment and its replies
 * Stores main comment, list of replies, and reply count
 */
@Getter
@Setter
public class FullComment implements Cloneable{
    private Comment comment;
    private ArrayList<Comment> commentReplyList;
    private Integer replyCount;

    //Creates a clone of the object
    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }
}
