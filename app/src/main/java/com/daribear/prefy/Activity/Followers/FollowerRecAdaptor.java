package com.daribear.prefy.Activity.Followers;

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
import com.daribear.prefy.R;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * The recycler view for the followActivities.
 * Displays the list of follow activities by showing the follower's profile image, username and the "followed you" text.
 */
@AllArgsConstructor
public class FollowerRecAdaptor extends RecyclerView.Adapter<FollowerRecAdaptor.ViewHolder> {
    @Setter
    private ArrayList<FollowerActivity> followerActivityList;

    /**
     * Inflates the item layout and returns a ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds the data at the given position to the ViewHolder.
     * @param holder The ViewHolder to bind data to
     * @param position The position of the item in the data list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        initGlide(true, holder.profileImageView, followerActivityList.get(position).getUser().getProfileImageURL());
        //holder.postImageView.setVisibility(View.GONE);
        initClickListener(holder.itemView, position);
        holder.textView.setText(getFollowerText(followerActivityList.get(position), holder.itemView.getContext()));
    }

    /**
     * Gets the number of items in the recyclerview
     * @return the number of items in the recyclerview.
     */
    @Override
    public int getItemCount() {
        return followerActivityList.size();
    }

    /**
     * ViewHolder class for holding references to the views in each item.
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
     * Load images using Glide library.
     * @param profile True if the profile image is a non default one
     * @param imageView The ImageView to load the image into
     * @param imageLink The URL of the image
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
     * @param imageView the image view to load the data into
     */
    private void defaultImage(ImageView imageView){
        Glide.with(imageView)
                .load(R.drawable.user_photo)
                .into(imageView);
    }

    /**
     * Creates the text saying "username followed you" in different colours.
     * @param followerActivity the followActivity to create the text from
     * @param context the context to use
     * @return
     */
    private SpannableStringBuilder getFollowerText(FollowerActivity followerActivity, Context context){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String textUsername;
        if (followerActivity.getUser().getUsername() != null && !followerActivity.getUser().getUsername().isEmpty()){
            textUsername = followerActivity.getUser().getUsername();
            SpannableString usernameSpannable= new SpannableString(textUsername);
            usernameSpannable.setSpan(new ForegroundColorSpan(context.getColor(R.color.text_color)), 0, textUsername.length(), 0);
            builder.append(usernameSpannable);
        } else {
            textUsername = "INVALID USER";
            SpannableString usernameSpannable= new SpannableString(textUsername);
            usernameSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, textUsername.length(), 0);
            builder.append(usernameSpannable);
        }
        String followerExtra = " followed you ";
        SpannableString followerExtraSpannable= new SpannableString(followerExtra);
        followerExtraSpannable.setSpan(new ForegroundColorSpan(context.getColor(R.color.grey)), 0, followerExtra.length(), 0);
        builder.append(followerExtraSpannable);
        return builder;
    }

    /**
     * Adds a click listener for the profile image, so that the logged in user can view the other user's profile
     * @param itemview the itemview that can be clicked on
     * @param postion position in the data list
     */
    private void initClickListener(View itemview, int postion){
        ImageView profileImage = itemview.findViewById(R.id.ActivityListItemProfileImage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putLong("id", followerActivityList.get(postion).getUser().getId());
                bundle.putParcelable("user", followerActivityList.get(postion).getUser());
                Navigation.findNavController(v).navigate(R.id.action_global_userProfile, bundle);
            }
        });
        itemview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
