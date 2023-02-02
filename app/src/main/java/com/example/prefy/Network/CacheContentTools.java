package com.example.prefy.Network;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.prefy.Profile.User;
import com.example.prefy.customClasses.StandardPost;

import java.util.ArrayList;

public class CacheContentTools {

    public static ContentValues getStandardPostContent(StandardPost standardPost){
        ContentValues standardPostContent = new ContentValues();
        standardPostContent.put("userId", standardPost.getUserId());
        standardPostContent.put("leftVotes", standardPost.getLeftVotes());
        standardPostContent.put("rightVotes", standardPost.getRightVotes());
        standardPostContent.put("imageURL", standardPost.getImageURL());
        standardPostContent.put("question", standardPost.getQuestion());
        standardPostContent.put("commentsNumber", standardPost.getCommentsNumber());
        standardPostContent.put("creationDate", standardPost.getCreationDate());
        standardPostContent.put("postId", standardPost.getPostId());
        standardPostContent.put("allVotes", standardPost.getAllVotes());
        standardPostContent.put("currentVote", standardPost.getCurrentVote());
        return standardPostContent;
    }
    public static ContentValues getUserContent(User user){
        ContentValues userContent = new ContentValues();
        userContent.put("username", user.getUsername());
        userContent.put("profileImageURL", user.getProfileImageURL());
        userContent.put("id", user.getId());
        userContent.put("fullname", user.getFullname());
        userContent.put("postsNumber", user.getPostsNumber());
        userContent.put("prefsNumber", user.getPrefsNumber());
        userContent.put("rating", user.getRating());
        userContent.put("bio", user.getBio());
        userContent.put("vk", user.getVk());
        userContent.put("instagram", user.getInstagram());
        userContent.put("twitter", user.getTwitter());
        if (user.getVerified()){
            userContent.put("verified", 1);
        } else {
            userContent.put("verified", 0);
        }
        return userContent;
    }

    public static ArrayList<User> getUserList(Cursor userCursor){
        ArrayList<User> userList = new ArrayList<>();
        if (userCursor.moveToFirst()){
            for (int i = 0; i < userCursor.getCount(); i ++){
                User user = new User();
                String username = userCursor.getString(userCursor.getColumnIndexOrThrow("username"));
                String profileImageURL = userCursor.getString(userCursor.getColumnIndexOrThrow("profileImageURL"));
                Long Id = userCursor.getLong(userCursor.getColumnIndexOrThrow("id"));

                String fullname = userCursor.getString(userCursor.getColumnIndexOrThrow("fullname"));
                Long postsNumber = userCursor.getLong(userCursor.getColumnIndexOrThrow("postsNumber"));
                Long votesNumber = userCursor.getLong(userCursor.getColumnIndexOrThrow("votesNumber"));
                Long prefsNumber = userCursor.getLong(userCursor.getColumnIndexOrThrow("prefsNumber"));
                Long rating = userCursor.getLong(userCursor.getColumnIndexOrThrow("rating"));
                String bio = userCursor.getString(userCursor.getColumnIndexOrThrow("bio"));
                String vk = userCursor.getString(userCursor.getColumnIndexOrThrow("vk"));
                String instagram = userCursor.getString(userCursor.getColumnIndexOrThrow("instagram"));
                String twitter = userCursor.getString(userCursor.getColumnIndexOrThrow("twitter"));
                Integer verifiedInt = userCursor.getInt(userCursor.getColumnIndexOrThrow("verified"));

                user.setFullname(fullname);
                user.setPostsNumber(postsNumber);
                user.setVotesNumber(votesNumber);
                user.setPrefsNumber(prefsNumber);
                user.setRating(rating);
                user.setBio(bio);
                user.setVk(vk);
                user.setInstagram(instagram);
                user.setTwitter(twitter);
                if (verifiedInt == 1){
                    user.setVerified(true);
                }else {
                    user.setVerified(false);
                }
                user.setUsername(username);
                user.setProfileImageURL(profileImageURL);
                user.setId(Id);
                userList.add(user);
                userCursor.moveToNext();
            }
        }
        userCursor.close();

        return userList;
    }
    public static ArrayList<StandardPost> getPostList(Cursor postCursor){
        ArrayList<StandardPost> postList = new ArrayList<>();
        if (postCursor.moveToFirst()){
            for (int i = 0; i < postCursor.getCount(); i ++){
                StandardPost post = new StandardPost();
                Long Id = postCursor.getLong(postCursor.getColumnIndexOrThrow("userId"));
                Integer leftVotes = postCursor.getInt(postCursor.getColumnIndexOrThrow("leftVotes"));
                Integer rightVotes = postCursor.getInt(postCursor.getColumnIndexOrThrow("rightVotes"));
                String imageURL = postCursor.getString(postCursor.getColumnIndexOrThrow("imageURL"));
                String question = postCursor.getString(postCursor.getColumnIndexOrThrow("question"));
                Integer commentsNumber = postCursor.getInt(postCursor.getColumnIndexOrThrow("commentsNumber"));
                Double creationDate = postCursor.getDouble(postCursor.getColumnIndexOrThrow("creationDate"));
                Long postId = postCursor.getLong(postCursor.getColumnIndexOrThrow("postId"));
                Integer allVotes = postCursor.getInt(postCursor.getColumnIndexOrThrow("allVotes"));
                String currentVote = postCursor.getString(postCursor.getColumnIndexOrThrow("currentVote"));
                post.setUserId(Id);
                post.setLeftVotes(leftVotes);
                post.setRightVotes(rightVotes);
                post.setImageURL(imageURL);
                post.setQuestion(question);
                post.setCommentsNumber(commentsNumber);
                post.setCreationDate(creationDate);
                post.setPostId(postId);
                post.setAllVotes(allVotes);
                post.setCurrentVote(currentVote);

                postList.add(post);
                postCursor.moveToNext();
            }
        }
        postCursor.close();

        return postList;
    }
}
