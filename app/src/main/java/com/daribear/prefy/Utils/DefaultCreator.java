package com.daribear.prefy.Utils;

import com.daribear.prefy.Profile.User;
import com.daribear.prefy.customClasses.Posts.StandardPost;

public class DefaultCreator {


    public static User createBlankUser(){
        User user = new User();
        user.setFullname("Deleted User");
        user.setId(-1L);
        user.setPostsNumber(0L);
        user.setPrefsNumber(0L);
        user.setVotesNumber(0L);
        user.setFollowingNumber(0L);
        user.setFollowerNumber(0L);
        user.setProfileImageURL("none");
        user.setUsername("Deleted User");
        user.setBio("This user has been deleted");
        user.setVerified(false);
        return user;
    }

    public static StandardPost createBlankStandardPost(){
        StandardPost standardPost = new StandardPost();
        standardPost.setPostId(-1L);
        standardPost.setUserId(-1L);
        standardPost.setCreationDate((double)CurrentTime.getCurrentTime());
        standardPost.setAllVotes(0);
        standardPost.setCurrentVote("other");
        standardPost.setCommentsNumber(0);
        standardPost.setLeftVotes(0);
        standardPost.setRightVotes(0);
        standardPost.setImageURL("none");
        standardPost.setQuestion("This post has been deleted");
        return standardPost;
    }
}

