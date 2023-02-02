package com.example.prefy.Comments;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.constraintlayout.motion.utils.ViewSpline;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prefy.Profile.User;
import com.example.prefy.R;
import com.example.prefy.Utils.dateSinceSystem;
import com.example.prefy.customClasses.FullPost;
import com.example.prefy.customClasses.StandardPost;

import java.util.ArrayList;

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

    private void initViews(View view){
        noItemsRelLay = view.findViewById(R.id.NoCommentsRelLay);
        recView = view.findViewById(R.id.CommentsRecView);
        progressBar = view.findViewById(R.id.CommentsLoadingProgress);
        noInternetText = view.findViewById(R.id.CommentsNoInternet);

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

        ImageView postImage = view.findViewById(R.id.CommentPostImageView);
        initGlide(postImage, post.getImageURL());
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
        TextView posterUsernameText = view.findViewById(R.id.CommentsPosterProfileUsername);
        posterUsernameText.setText(user.getUsername());
        TextView posterTime = view.findViewById(R.id.CommentsPosterPostDate);
        posterTime.setText(dateSinceSystem.getTimeSince(post.getCreationDate()));
        RecyclerView recyclerView = view.findViewById(R.id.CommentsRecView);

        TextView questionText = view.findViewById(R.id.CommentQuestionText);
        questionText.setText(post.getQuestion());
    }

    private void initGlide(ImageView imageView, String imageURL){
        Glide.with(imageView)
                .load(imageURL)
                .into(imageView);
    }

    public void NoItemsCheck(Boolean noItems){

        if (noItems){
            noItemsRelLay.setVisibility(View.VISIBLE);
        } else {
            noItemsRelLay.setVisibility(View.GONE);
        }
        progressBar.setVisibility(View.GONE);
        noInternetText.setVisibility(View.GONE);
    }

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
