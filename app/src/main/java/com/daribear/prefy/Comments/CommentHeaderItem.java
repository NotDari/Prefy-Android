package com.daribear.prefy.Comments;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.GeneralUtils.dateSinceSystem;
import com.daribear.prefy.customClasses.Posts.FullPost;
import com.daribear.prefy.customClasses.Posts.StandardPost;

/**
 * Represents the header view for a comment section, which is the part abaove the comments. Is the post image and poster info,
 * and the comments are below this.
 * Also handles displaying the post, poster info, and "no comments"/no internet" states.
 */
public class CommentHeaderItem {
    private View view;
    private RecyclerView.ViewHolder holder;
    private StandardPost post;
    private User user;
    private RelativeLayout noItemsRelLay;
    private ProgressBar progressBar;
    private RecyclerView recView;
    private TextView noInternetText;
    private CommentListAdaptor adaptor;
    private handleCommentsRecView handleCommentsRecView;

    public CommentHeaderItem(handleCommentsRecView handleCommentsRecView,CommentListAdaptor adaptor,RecyclerView.ViewHolder holder, FullPost fullPost, Boolean noInternetDefault) {
        this.handleCommentsRecView = handleCommentsRecView;
        this.adaptor = adaptor;
        this.holder = holder;
        this.view = holder.itemView;
        this.post = fullPost.getStandardPost();
        this.user = fullPost.getUser();
        initViews(view);
        if (noInternetDefault){
            initNoInternet();
        }

    }

    /**
     * Initialise all views and set initial data for the post header
     * @param view the base view for the header
     */
    private void initViews(View view){
        noItemsRelLay = view.findViewById(R.id.NoCommentsRelLay);
        recView = view.findViewById(R.id.CommentsRecView);
        progressBar = view.findViewById(R.id.CommentsLoadingProgress);
        noInternetText = view.findViewById(R.id.CommentsNoInternet);

        //Adjust margins dynamically based on screen height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) noItemsRelLay.getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        ((ViewGroup.MarginLayoutParams) noItemsRelLay.getLayoutParams()).topMargin = (int) (screenHeight * .1);
        ((ViewGroup.MarginLayoutParams) noItemsRelLay.getLayoutParams()).bottomMargin = (int) (screenHeight * .1);
        ((ViewGroup.MarginLayoutParams) progressBar.getLayoutParams()).topMargin = (int) (screenHeight * .1);
        ((ViewGroup.MarginLayoutParams) progressBar.getLayoutParams()).bottomMargin = (int) (screenHeight * .1);
        ((ViewGroup.MarginLayoutParams) noInternetText.getLayoutParams()).topMargin = (int) (screenHeight * .1);
        ((ViewGroup.MarginLayoutParams) noInternetText.getLayoutParams()).bottomMargin = (int) (screenHeight * .1);

        //Set post image
        ImageView postImage = view.findViewById(R.id.CommentPostImageView);
        initGlide(postImage, post.getImageURL());

        //Set poster profile image
        ImageView posterProfileImage = view.findViewById(R.id.CommentsPosterProfileImage);
        if (!user.getProfileImageURL().equals("none")) {
            Glide.with(posterProfileImage)
                    .load(user.getProfileImageURL())
                    .circleCrop()
                    .into(posterProfileImage);
        } else{
            Glide.with(posterProfileImage)
                    .load(R.drawable.user_photo)
                    .circleCrop()
                    .into(posterProfileImage);
        }

        //Set poster username
        TextView posterUsernameText = view.findViewById(R.id.CommentsPosterProfileUsername);
        posterUsernameText.setText(user.getUsername());

        //Set post creation time
        TextView posterTime = view.findViewById(R.id.CommentsPosterPostDate);
        posterTime.setText(dateSinceSystem.getTimeSince(post.getCreationDate()));

        //Set question text
        TextView questionText = view.findViewById(R.id.CommentQuestionText);
        questionText.setText(post.getQuestion());
    }
    /**
     * Helper to load an image with Glide
     * @param imageView the ImageView to load
     * @param imageURL url of the image to be loaded
     */
    private void initGlide(ImageView imageView, String imageURL){
        Glide.with(imageView)
                .load(imageURL)
                .into(imageView);
    }
    /**
     * Show or hide the "no comments" layout
     * @param noItems true if no comments false otherwise
     */
    public void NoItemsCheck(Boolean noItems){

        if (noItems){
            noItemsRelLay.setVisibility(View.VISIBLE);
        } else {
            noItemsRelLay.setVisibility(View.GONE);
        }
        progressBar.setVisibility(View.GONE);
        noInternetText.setVisibility(View.GONE);
    }
    /**
     * Initialise "no internet" UI and refresh click
     */
    public void initNoInternet(){
        ((Activity)view.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                noInternetText.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                noInternetText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        noInternetText.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        handleCommentsRecView.refreshData();
                    }
                });
            }
        });

    }

}
