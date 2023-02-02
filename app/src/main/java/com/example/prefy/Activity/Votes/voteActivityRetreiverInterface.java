package com.example.prefy.Activity.Votes;

import com.example.prefy.Activity.Comment.CommentActivity;

import java.util.ArrayList;

public interface voteActivityRetreiverInterface {
    void votecompleted(Boolean successful, ArrayList<VoteActivity> voteActivityList);
}
