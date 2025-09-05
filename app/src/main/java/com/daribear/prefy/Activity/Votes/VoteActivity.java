package com.daribear.prefy.Activity.Votes;

import com.daribear.prefy.Profile.User;
import com.daribear.prefy.customClasses.Posts.StandardPost;

import lombok.Getter;
import lombok.Setter;

/**
 * The activity which represents a VoteActivity, where it contains the last userId to vote on the post, and
 * the details of the post itself.
 */
@Getter
@Setter
public class VoteActivity {
    private User user;
    private Long lastUserId;
    private Double postCreationDate;
    private Long postKey;
    private StandardPost post;
}
