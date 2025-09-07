package com.daribear.prefy.Activity.Followers;

import com.daribear.prefy.Profile.User;

import lombok.Data;

/**
 * The data entity class which represents a followActivity, which is when a user follows the logged in user.
 */
@Data
public class FollowerActivity {
    private User user;
    private Long userId;
    private Long followerId;
    private Double occurrenceDate;
    private Boolean followed;
}
