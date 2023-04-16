package com.daribear.prefy.Activity.Comment;


import com.daribear.prefy.Profile.User;

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
