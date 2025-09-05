package com.daribear.prefy.Activity.Comment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daribear.prefy.NavGraphDirections;
import com.daribear.prefy.R;

import java.util.ArrayList;

/**
 * The recyclerview for the comments Activity
 * Displays the comments text, the user who posted it and the post image they cmmented on.
 * Uses Glide for image loading
 */
public class CommentRecAdaptor extends RecyclerView.Adapter<CommentRecAdaptor.ViewHolder> {
    private ArrayList<CommentActivity> commentActivityList;

    public CommentRecAdaptor(ArrayList<CommentActivity> commentActivityList) {
        this.commentActivityList = commentActivityList;
    }

    public void setCommentActivityList(ArrayList<CommentActivity> commentActivityList) {
        this.commentActivityList = commentActivityList;
    }


    /**
     * Creates and inflates the view holder for each comment item.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    /**
     * Binds the comment data to the given ViewHolder.
     *
     * @param holder   the ViewHolder representing a single comment item
     * @param position the index of the item in the dataset
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        initGlide(true, holder.profileImageView, commentActivityList.get(position).getUser().getProfileImageURL());
        initGlide(false, holder.postImageView, commentActivityList.get(position).getPostImageURL());
        initClickListener(holder.itemView, position);
        holder.textView.setText(getComment(commentActivityList.get(position).getUser().getUsername(), commentActivityList.get(position).getText(), holder.itemView.getContext()), TextView.BufferType.SPANNABLE);
    }

    /**
     * @return the total number of commentActivity items in the dataset
     */
    @Override
    public int getItemCount() {
        return commentActivityList.size();
    }

    /**
     * ViewHolder class holding references to the UI components for a comment item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView postImageView, profileImageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.ActivityListItemPostImage);
            profileImageView = itemView.findViewById(R.id.ActivityListItemProfileImage);
            textView = itemView.findViewById(R.id.ActivityListItemText);
        }
    }

    /**
     * Loads an image into the given ImageView using Glide.
     * If the image is a profile image, applies a circular crop and falls back to a default image if needed.
     *
     * @param profile   whether the image is a profile image
     * @param imageView the target ImageView
     * @param imageLink the URL of the image to load
     */
    private void initGlide(Boolean profile, ImageView imageView, String imageLink){
        if (profile){
            if (imageLink != null && !imageLink.isEmpty()){
                if (!imageLink.equals("none")) {
                    Glide.with(imageView)
                            .load(imageLink)
                            .circleCrop()
                            .into(imageView);
                } else {
                    defaultImage(imageView);
                }
            } else {
                defaultImage(imageView);
            }
        } else {
            Glide.with(imageView)
                    .load(imageLink)
                    .into(imageView);
        }
    }


    /**
     * Loads a default profile image into the given ImageView.
     */
    private void defaultImage(ImageView imageView){
        Glide.with(imageView)
                .load(R.drawable.user_photo)
                .circleCrop()
                .into(imageView);
    }

    /**
     * Builds a styled SpannableStringBuilder for the comment text.
     * The username and comment text are colored differently, and placeholders
     * are shown if either value is missing.
     *
     * @param username the username of the commenter
     * @param comment  the text of the comment
     * @param context  context for accessing colors
     * @return a styled SpannableStringBuilder for display
     */
    private SpannableStringBuilder getComment(String username, String comment, Context context){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String textUsername;
        if (username != null && !username.isEmpty()){
            textUsername = username;
            SpannableString usernameSpannable= new SpannableString(textUsername);
            usernameSpannable.setSpan(new ForegroundColorSpan(context.getColor(R.color.text_color)), 0, textUsername.length(), 0);
            builder.append(usernameSpannable);
        } else {
            textUsername = "INVALID USER";
            SpannableString usernameSpannable= new SpannableString(textUsername);
            usernameSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, textUsername.length(), 0);
            builder.append(usernameSpannable);
        }

        String commentExtra = " commented on your post: ";
        SpannableString commentExtraSpannable= new SpannableString(commentExtra);
        commentExtraSpannable.setSpan(new ForegroundColorSpan(context.getColor(R.color.grey)), 0, commentExtra.length(), 0);
        builder.append(commentExtraSpannable);


        String textComment;
        if (comment != null && !comment.isEmpty()){
            textComment = comment;
            SpannableString commentSpannable= new SpannableString(textComment);
            commentSpannable.setSpan(new ForegroundColorSpan(context.getColor(R.color.text_color)), 0, textComment.length(), 0);
            builder.append(commentSpannable);
        } else {
            textComment = "INVALID Comment";
            SpannableString commentSpannable= new SpannableString(textComment);
            commentSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, textComment.length(), 0);
            builder.append(commentSpannable);
        }
        return builder;
    }

    /**
     * Attaches a click listener to the item view that navigates to the user profile.
     *
     * @param itemview the item view being bound
     * @param position the index of the item in the dataset
     */
    private void initClickListener(View itemview, int position){
        itemview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putLong("id", commentActivityList.get(position).getUser().getId());
                bundle.putParcelable("user", commentActivityList.get(position).getUser());
                Navigation.findNavController(view).navigate(R.id.action_global_userProfile, bundle);
            }
        });
    }
}
