package com.daribear.prefy.Activity.Comment;


import com.daribear.prefy.Profile.User;

import lombok.Getter;
import lombok.Setter;

/**
 * A class representing the commentsActivity which is when another user has commented on the active user's post.
 * It is essentially a notification
 */
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
