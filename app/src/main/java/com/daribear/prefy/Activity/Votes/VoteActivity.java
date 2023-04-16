package com.daribear.prefy.Activity.Votes;

import com.daribear.prefy.Profile.User;
import com.daribear.prefy.customClasses.StandardPost;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoteActivity {
    private User user;
    private Long lastUserId;
    private Double postCreationDate;
    private Long postKey;
    private StandardPost post;
}
