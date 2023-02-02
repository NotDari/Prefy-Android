package com.example.prefy.Activity.Votes;

import com.example.prefy.Profile.User;
import com.example.prefy.customClasses.StandardPost;

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
