package com.example.prefy.Utils;

import android.content.Context;

import com.example.prefy.Comments.Comment;
import com.example.prefy.Profile.User;
import com.example.prefy.R;
import com.example.prefy.customClasses.StandardPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

public class FirebaseUtils {

    /**
    public static StandardPost retreiveStandardPost(DocumentSnapshot documentSnapshot){
        StandardPost post = new StandardPost();
        post.setCurrentVote(documentSnapshot.getString("currentVote"));
        post.setKey(documentSnapshot.getId());
        if (documentSnapshot.get("commentsNumber") != null) {
            post.setCommentsNumber(Integer.valueOf(Math.toIntExact(((Long) (documentSnapshot.get("commentsNumber"))))));
        }
        if (documentSnapshot.get("leftVotes") != null) {
            post.setLeftVotes(Integer.valueOf(Math.toIntExact(((Long) (documentSnapshot.get("leftVotes"))))));
        }
        if (documentSnapshot.get("rightVotes") != null) {
            post.setRightVotes(Integer.valueOf(Math.toIntExact(((Long) (documentSnapshot.get("rightVotes"))))));
        }
        if (documentSnapshot.get("allVotes") != null) {
            post.setAllVotes(Integer.valueOf(Math.toIntExact(((Long) (documentSnapshot.get("allVotes"))))));
        }
        post.setQuestion(documentSnapshot.getString("question"));
        post.setUid(documentSnapshot.getString("uid"));
        post.setImageURL(documentSnapshot.getString("imageURL"));
        post.setCreationDate(documentSnapshot.getDouble("creationDate"));
        if (post.getCommentsNumber() == null) {
            post.setCommentsNumber(0);
        }
        if (post.getRightVotes() == null) {
            post.setRightVotes(0);
        }
        if (post.getLeftVotes() == null) {
            post.setLeftVotes(0);
        }
        return post;
    }

    public static User retreiveUser(DocumentSnapshot documentSnapshot){
        User user = new User();
        user.setUID(documentSnapshot.getId());
        user.setUsername(documentSnapshot.getString("username"));
        user.setBio(documentSnapshot.getString("bio"));
        user.setFullname(documentSnapshot.getString("fullname"));
        user.setProfileImageURL(documentSnapshot.getString("profileImageURL"));
        user.setInstagram(documentSnapshot.getString("instagram"));
        user.setPostsNumber(documentSnapshot.getLong("postsNumber"));
        user.setPrefsNumber(documentSnapshot.getLong("prefsNumber"));
        user.setRating(documentSnapshot.getLong("rating"));
        user.setTwitter(documentSnapshot.getString("twitter"));
        user.setVerified(documentSnapshot.getBoolean("verified"));
        user.setVk(documentSnapshot.getString("vk"));
        user.setVotesNumber(documentSnapshot.getLong("votesNumber"));
        if (user.getVerified() == null){
            user.setVerified(false);
        }
        return user;
    }

    public static Comment retreiveComment(DocumentSnapshot documentSnapshot){
        Comment comment = new Comment();
        comment.setCreationDate(documentSnapshot.getDouble("creationDate"));
        comment.setUid(documentSnapshot.getString("uid"));
        comment.setText(documentSnapshot.getString("text"));
        comment.setReplyUID(documentSnapshot.getString("replyUserUid"));
        comment.setReplyUsername(documentSnapshot.getString("replyUsername"));
        comment.setKey(documentSnapshot.getId());
        System.out.println("Sdad commentId:" + comment.getKey());
        comment.setPostId(documentSnapshot.getString("postUID"));
        return comment;
    }

    public static User getCurrentUser(Context appContext){
        Utils utils = new Utils(appContext);
        User user = new User();
        user.setProfileImageURL(utils.loadString(appContext.getString(R.string.save_profileP_pref), ""));
        user.setUsername(utils.loadString(appContext.getString(R.string.save_username_pref), ""));
        user.setUID(FirebaseAuth.getInstance().getUid());
        user.setVerified(utils.loadBoolean(appContext.getString(R.string.save_verified_pref), false));
        user.setVk(utils.loadString(appContext.getString(R.string.save_vk_pref), ""));
        user.setInstagram(utils.loadString(appContext.getString(R.string.save_instagram_pref), ""));
        user.setTwitter(utils.loadString(appContext.getString(R.string.save_twitter_pref), ""));
        user.setPrefsNumber(utils.loadLong(appContext.getString(R.string.save_prefCount_pref), 0));
        user.setVotesNumber(utils.loadLong(appContext.getString(R.string.save_voteCount_pref), 0));
        user.setPostsNumber(utils.loadLong(appContext.getString(R.string.save_postCount_pref), 0));
        user.setFullname(utils.loadString(appContext.getString(R.string.save_fullname_pref), ""));
        return user;
    }


}


     */
}