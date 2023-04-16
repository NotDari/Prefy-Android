package com.daribear.prefy.Utils;

import com.daribear.prefy.Profile.User;

public class DefaultCreator {


    public static User createBlankUser(){
        User user = new User();
        user.setFullname("Deleted User");
        user.setId(-1L);
        user.setPostsNumber(0L);
        user.setPrefsNumber(0L);
        user.setVotesNumber(0L);
        user.setProfileImageURL("none");
        user.setUsername("Deleted User");
        user.setBio("This user has been deleted");
        user.setVerified(false);
        return user;
    }
}

