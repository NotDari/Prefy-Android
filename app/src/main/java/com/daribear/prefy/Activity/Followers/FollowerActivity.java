package com.daribear.prefy.Activity.Followers;

import com.daribear.prefy.Profile.User;

import lombok.Data;

@Data
public class FollowerActivity {
    private User user;
    private Long userId;
    private Long followerId;
    private Double occurrenceDate;
    private Boolean followed;
}
