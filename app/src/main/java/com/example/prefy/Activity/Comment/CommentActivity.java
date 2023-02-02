package com.example.prefy.Activity.Comment;


import com.example.prefy.Profile.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentActivity {
    private User user;
    private Double creationDate;
    private Long userId;
    private String text;
    private String postImageURL;
    private Boolean isReply;
}
