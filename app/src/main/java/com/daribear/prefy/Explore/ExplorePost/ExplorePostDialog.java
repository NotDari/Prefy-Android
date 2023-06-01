package com.daribear.prefy.Explore.ExplorePost;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.daribear.prefy.DeleteDialog.DeleteDelegate;
import com.daribear.prefy.PostDropDownDialog;
import com.daribear.prefy.PostDropDownDialogDelegate;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.ItemAlterer;
import com.daribear.prefy.Utils.SharedPreferences.Utils;
import com.daribear.prefy.Utils.GeneralUtils.dateSinceSystem;
import com.daribear.prefy.Votes.VoteHandler;
import com.daribear.prefy.customClasses.Posts.FullPost;
import com.daribear.prefy.customClasses.Posts.StandardPost;

public class ExplorePostDialog implements PostDropDownDialogDelegate, DeleteDelegate {
    private Dialog dialog;
    private Context context;
    private FullPost fullFeaturedPost;
    private ImageView closeButton, profileImage, postImage, optionsButton;
    private TextView postQuestion, usernameText, postTime, voteCount;
    private RelativeLayout leftCLick, rightClick;
    private Activity parentActivity;
    private TextView commentsButton;

    public ExplorePostDialog(Context context, FullPost fullFeaturedPost, Activity parentActivity) {
        this.context = context;
        this.fullFeaturedPost = fullFeaturedPost;
        this.parentActivity = parentActivity;
    }
    public ExplorePostDialog(Context context, User user, StandardPost post, Activity parentActivity){
        this.context = context;
        this.parentActivity = parentActivity;
        fullFeaturedPost = new FullPost();
        fullFeaturedPost.setUser(user);
        fullFeaturedPost.setStandardPost(post);
    }


    public void initDialog(){
        dialog = new Dialog(context, android.R.style.Theme_DeviceDefault_NoActionBar);
        dialog.setContentView(R.layout.explore_item_dialog);
        setUpViews();
        initMethods();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getColor(R.color.black_highOpacity)));
        dialog.show();
    }

    private void setUpViews(){
        closeButton = dialog.findViewById(R.id.ExploreDialogCloseButton);
        profileImage = dialog.findViewById(R.id.ExploreDialogItemUserImage);
        postImage = dialog.findViewById(R.id.ExploreDialogItemQuestionImage);
        usernameText = dialog.findViewById(R.id.ExploreDialogItemUsername);
        postQuestion = dialog.findViewById(R.id.ExploreDialogItemQuestionText);
        postTime = dialog.findViewById(R.id.ExploreDialogItemPostDate);
        voteCount = dialog.findViewById(R.id.ExploreDialogItemTotalVotes);
        leftCLick = dialog.findViewById(R.id.ExploreDialogItemImageLeftClicker);
        rightClick = dialog.findViewById(R.id.ExploreDialogItemImageRightClicker);
        optionsButton = dialog.findViewById(R.id.ExploreDialogItemOptionsButton);
        commentsButton = dialog.findViewById(R.id.ExploreDialogItemCommentsButton);
    }

    private void initMethods(){
        initClose();
        initViews();
        initVotingSystem();
        initOptions();
        initComments();
    }

    public void initVotingSystem(){
        VoteHandler.changeImage(fullFeaturedPost.getStandardPost(), postImage, leftCLick, rightClick, "ExploreDialog");
        rightClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoteHandler.voteSubmitted(fullFeaturedPost.getStandardPost(), postImage, leftCLick, rightClick, "rightClick", "ExploreDialog");
            }
        });
        leftCLick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoteHandler.voteSubmitted(fullFeaturedPost.getStandardPost(), postImage, leftCLick, rightClick, "leftClick", "ExploreDialog");
            }
        });
    }

    private void initViews(){
        postQuestion.setText(fullFeaturedPost.getStandardPost().getQuestion());
        postTime.setText(dateSinceSystem.getTimeSince(fullFeaturedPost.getStandardPost().getCreationDate()));
        voteCount.setText(fullFeaturedPost.getStandardPost().getAllVotes().toString() + " votes");
        usernameText.setText(fullFeaturedPost.getUser().getUsername().toString());
        String commentsText = "0";
        if (fullFeaturedPost.getStandardPost().getCommentsNumber() != null){
            commentsText = fullFeaturedPost.getStandardPost().getCommentsNumber().toString();
        }
        commentsButton.setText(commentsText);
        initImages();
    }

    private void initImages(){
        if (fullFeaturedPost.getUser().getProfileImageURL() != null ) {
            if (!fullFeaturedPost.getUser().getProfileImageURL().isEmpty() && !fullFeaturedPost.getUser().getProfileImageURL().equals("none")){
                Glide.with(profileImage)
                        .load(fullFeaturedPost.getUser().getProfileImageURL())
                        .circleCrop()
                        .into(profileImage);
            } else {
                Glide.with(profileImage)
                        .load(R.drawable.user_photo)
                        .circleCrop()
                        .into(profileImage);
            }
        } else {
            Glide.with(profileImage)
                    .load(R.drawable.user_photo)
                    .circleCrop()
                    .into(profileImage);
        }
        Glide.with(postImage)
                .load(fullFeaturedPost.getStandardPost().getImageURL())
                .into(postImage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Bundle bundle = new Bundle();
                bundle.putLong("id", fullFeaturedPost.getUser().getId());
                bundle.putParcelable("user", fullFeaturedPost.getUser());
                Navigation.findNavController(parentActivity, R.id.FragmentContainerView).navigate(R.id.action_global_userProfile, bundle);
            }
        });
    }

    private void initClose(){
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void initComments(){
        commentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("post", fullFeaturedPost.getStandardPost());
                bundle.putParcelable("user", fullFeaturedPost.getUser());
                Navigation.findNavController(parentActivity, R.id.FragmentContainerView).navigate(R.id.action_global_commentsFragment, bundle);
                dialog.dismiss();
            }
        });
    }

    private void initOptions(){
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils utils = new Utils(parentActivity);
                FullPost fullPost = new FullPost();
                fullPost.setStandardPost(fullFeaturedPost.getStandardPost());
                fullPost.setUser(fullFeaturedPost.getUser());
                Boolean loggedUserPost = fullFeaturedPost.getUser().getId().equals(utils.loadLong(parentActivity.getString(R.string.save_user_id), 0));
                PostDropDownDialog dialog = new PostDropDownDialog(v.getContext(), loggedUserPost, parentActivity,fullPost, ExplorePostDialog.this, ExplorePostDialog.this);
                Integer bottomNavHeight = parentActivity.findViewById(R.id.BottomNav).getHeight();
                dialog.setCoordinates(0, bottomNavHeight);
                dialog.setImageDrawable(postImage.getDrawable());
                dialog.initDialog();
            }
        });
    }

    @Override
    public void replyClicked() {
        if (dialog.isShowing()){
            dialog.dismiss();
        }
    }

    @Override
    public void itemDeleted() {
        FullPost fullPost = new FullPost(fullFeaturedPost.getStandardPost(), fullFeaturedPost.getUser());
        ItemAlterer.deleteItem(fullPost, context.getApplicationContext());
    }
}
