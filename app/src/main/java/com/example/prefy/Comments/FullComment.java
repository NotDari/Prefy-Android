package com.example.prefy.Comments;

import com.example.prefy.Profile.User;

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
