package com.daribear.prefy.Utils.JsonUtils;

import com.daribear.prefy.Activity.Comment.CommentActivity;
import com.daribear.prefy.Activity.Followers.FollowerActivity;
import com.daribear.prefy.Activity.Votes.VoteActivity;
import com.daribear.prefy.Comments.Comment;
import com.daribear.prefy.Comments.FullComment;
import com.daribear.prefy.Popular.PopularActivity;
import com.daribear.prefy.customClasses.Posts.PopularPost;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.customClasses.CustomError;
import com.daribear.prefy.customClasses.Posts.StandardPost;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Response;

public class CustomJsonMapper {

    public static User getUser(Response response){
        try {
            String responseString = response.body().string();
            JSONObject jsonObject = new JSONObject(responseString);
            Long id = jsonObject.getLong("id");
            String username = jsonObject.optString("username");
            String profileImageURL = jsonObject.optString("profileImageURL");
            String fullname = jsonObject.optString("fullname");
            Long postsNumber = jsonObject.optLong("postsNumber");
            Long votesNumber = jsonObject.optLong("votesNumber");
            Long prefsNumber = jsonObject.optLong("prefsNumber");
            Long followerNumber = jsonObject.optLong("followerNumber");
            Long followingNumber = jsonObject.optLong("followingNumber");
            String bio = jsonObject.optString("bio");
            String vk = jsonObject.optString("vk");
            String instagram = jsonObject.optString("instagram");
            String twitter = jsonObject.optString("twitter");
            Boolean verified = jsonObject.optBoolean("verified");

            User user = new User();
            user.setId(id);
            user.setUsername(username);
            user.setProfileImageURL(profileImageURL);
            user.setFullname(fullname);
            user.setPostsNumber(postsNumber);
            user.setVotesNumber(votesNumber);
            user.setPrefsNumber(prefsNumber);
            user.setFollowerNumber(followerNumber);
            user.setFollowingNumber(followingNumber);
            user.setBio(bio);
            user.setVk(vk);
            user.setInstagram(instagram);
            user.setTwitter(twitter);
            user.setVerified(verified);
            response.body().close();
            return user;
        }catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User getUserFromObject(JSONObject jsonObject){
        try {

            Long id = jsonObject.getLong("id");
            String username = jsonObject.optString("username");
            String profileImageURL = jsonObject.optString("profileImageURL");
            String fullname = jsonObject.optString("fullname");
            Long postsNumber = jsonObject.optLong("postsNumber");
            Long votesNumber = jsonObject.optLong("votesNumber");
            Long prefsNumber = jsonObject.optLong("prefsNumber");
            Long followerNumber = jsonObject.optLong("followerNumber");
            Long followingNumber = jsonObject.optLong("followingNumber");
            String bio = jsonObject.optString("bio");
            String vk = jsonObject.optString("vk");
            String instagram = jsonObject.optString("instagram");
            String twitter = jsonObject.optString("twitter");
            Boolean verified = jsonObject.optBoolean("verified");



            User user = new User();
            user.setId(id);
            user.setUsername(username);
            user.setProfileImageURL(profileImageURL);
            user.setFullname(fullname);
            user.setPostsNumber(postsNumber);
            user.setVotesNumber(votesNumber);
            user.setPrefsNumber(prefsNumber);
            user.setFollowerNumber(followerNumber);
            user.setFollowingNumber(followingNumber);
            user.setBio(bio);
            user.setVk(vk);
            user.setInstagram(instagram);
            user.setTwitter(twitter);
            user.setVerified(verified);

            return user;
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getJWTToken(Response response){
        return response.header("Authorization");
    }

    public static String getEmail(Response response){
        try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            return jsonObject.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<CommentActivity> getCommentActivityList(Response response){
        try {
            String responseString = response.body().string();
            JSONArray jsonArray = new JSONArray(responseString);
            ArrayList<CommentActivity> commentActivityList= new ArrayList<>();
            for (int i =0; i < jsonArray.length(); i++){
                JSONObject jsonObject = (jsonArray.getJSONObject(i));
                CommentActivity commentActivity = new CommentActivity();
                commentActivity.setText(jsonObject.getString("text"));
                commentActivity.setPostImageURL(jsonObject.getString("postImageURL"));
                commentActivity.setCreationDate(jsonObject.getDouble("creationDate"));
                commentActivity.setIsReply(jsonObject.getBoolean("isReply"));
                commentActivity.setUserId(jsonObject.getLong("userId"));
                commentActivityList.add(commentActivity);
            }

            response.body().close();
            return commentActivityList;
        }catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<FollowerActivity> getFollowerActivityList(Response response){
        try {
            String responseString = response.body().string();
            JSONArray jsonArray = new JSONArray(responseString);
            ArrayList<FollowerActivity> followerActivityList= new ArrayList<>();
            for (int i =0; i < jsonArray.length(); i++){
                JSONObject jsonObject = (jsonArray.getJSONObject(i));
                FollowerActivity followerActivity = new FollowerActivity();
                followerActivity.setFollowerId(jsonObject.getLong("followerId"));
                followerActivity.setUserId(jsonObject.getLong("userId"));
                followerActivity.setOccurrenceDate(jsonObject.getDouble("occurrenceDate"));
                followerActivity.setFollowed(jsonObject.getBoolean("followed"));
                followerActivityList.add(followerActivity);
            }

            response.body().close();
            return followerActivityList;
        }catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Only gets part of the VoteActivity, the other part is a post and a user
    public static ArrayList<VoteActivity> getPartialVoteActivityList(Response response){
        ArrayList<VoteActivity> voteActivityList= new ArrayList<>();
        try {
            String responseString = response.body().string();
            JSONArray jsonArray = new JSONArray(responseString);
            for (int i =0; i < jsonArray.length(); i++){
                JSONObject jsonObject = (jsonArray.getJSONObject(i));
                VoteActivity voteActivity = new VoteActivity();
                voteActivity.setLastUserId(jsonObject.getLong("lastUserId"));
                voteActivity.setPostCreationDate(jsonObject.getDouble("lastVoteDate"));
                voteActivity.setPostKey(jsonObject.getLong("postId"));
                voteActivityList.add(voteActivity);
            }

            response.body().close();
            return voteActivityList;
        }catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return voteActivityList;
    }

    public static StandardPost getPostFromObject(JSONObject jsonObject){
        try {
            StandardPost standardPost = new StandardPost();
            standardPost.setCreationDate(jsonObject.getDouble("creationDate"));
            standardPost.setAllVotes(jsonObject.getInt("allVotes"));
            standardPost.setCommentsNumber(jsonObject.getInt("commentsNumber"));
            standardPost.setPostId(jsonObject.getLong("id"));
            standardPost.setUserId(jsonObject.getLong("userId"));
            standardPost.setRightVotes(jsonObject.getInt("rightVotes"));
            standardPost.setLeftVotes(jsonObject.getInt("leftVotes"));
            standardPost.setImageURL(jsonObject.getString("imageURL"));
            standardPost.setQuestion(jsonObject.getString("question"));
            return standardPost;
        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static PopularPost getPopularPostFromObject(JSONObject jsonObject){
        try {
            PopularPost popularPost = new PopularPost();
            popularPost.setCreationDate(jsonObject.getDouble("creationDate"));
            popularPost.setAllVotes(jsonObject.getInt("allVotes"));
            popularPost.setCommentsNumber(jsonObject.getInt("commentsNumber"));
            popularPost.setPostId(jsonObject.getLong("id"));
            popularPost.setUserId(jsonObject.getLong("userId"));
            popularPost.setRightVotes(jsonObject.getInt("rightVotes"));
            popularPost.setLeftVotes(jsonObject.getInt("leftVotes"));
            popularPost.setImageURL(jsonObject.getString("imageURL"));
            popularPost.setQuestion(jsonObject.getString("question"));
            popularPost.setPopularDate(jsonObject.getDouble("popularDate"));
            return popularPost;
        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static Comment getCommentFromObject(JSONObject jsonObject){
        try {
            Comment comment = new Comment();
            comment.setCommentId(jsonObject.getLong("id"));
            comment.setPostId(jsonObject.getLong("postId"));
            comment.setCreationDate(jsonObject.getDouble("creationDate"));
            comment.setText(jsonObject.getString("text"));
            comment.setReplyID(jsonObject.isNull("parentId") ? null :jsonObject.getLong("parentId"));
            comment.setUser(getUserFromObject(jsonObject.getJSONObject("user")));
            comment.setSubReplyID(jsonObject.isNull("subParentId") ? null :jsonObject.getLong("subParentId"));
            comment.setReplyUsername(jsonObject.isNull("replyUsername") ? null :jsonObject.getString("replyUsername"));

            //comment.setReplyCount(jsonObject.getString("replyCount")); Currently doesn't retreive replyCount
            return comment;
        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }


    public static FullComment getFullCommentFromObject(JSONObject jsonObject){
        try {
            FullComment fullComment = new FullComment();
            fullComment.setComment(getCommentFromObject(jsonObject));
            ArrayList<Comment> commentReplyList = new ArrayList<>();
            JSONArray commentReplyJson = jsonObject.getJSONArray("replyList");
            for (int i =0; i < commentReplyJson.length(); i ++){
                commentReplyList.add(getCommentFromObject(commentReplyJson.getJSONObject(i)));
            }
            fullComment.setReplyCount(jsonObject.getInt("replyCount"));
            fullComment.setCommentReplyList(commentReplyList);
            return fullComment;
        } catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static CustomError getCustomError(Response response){
        try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            response.body().close();
            return new CustomError(jsonObject.getInt("customCode"), jsonObject.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<StandardPost> getPostListFromResponse(Response response){
        try {
            String responseString = response.body().string();
            JSONArray jsonArray = new JSONArray(responseString);
            ArrayList<StandardPost> postList = new ArrayList<>();
            for (int i =0; i < jsonArray.length(); i++){
                postList.add(getPostFromObject((jsonArray.getJSONObject(i))));
            }
            response.body().close();
            return postList;
        }catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PopularActivity getPopularActivityFromResponse(Response response){
        try {
            PopularActivity popularActivity = new PopularActivity();
            JSONObject totalObject = new JSONObject(response.body().string());
            popularActivity.setTotalActivities((int) totalObject.getLong("newActivitiesCount"));
            popularActivity.setCommentsCount((int) totalObject.getLong("newCommentsCount"));
            popularActivity.setVotesCount((int) totalObject.getLong("newVotesCount"));
            response.body().close();
            return popularActivity;
        } catch (JSONException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
