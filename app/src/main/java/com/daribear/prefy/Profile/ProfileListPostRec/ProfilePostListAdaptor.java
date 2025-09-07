package com.daribear.prefy.Profile.ProfileListPostRec;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daribear.prefy.DeleteDialog.DeleteDelegate;
import com.daribear.prefy.PostDropDownDialog;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.daribear.prefy.Utils.GeneralUtils.dateSinceSystem;
import com.daribear.prefy.customClasses.Posts.FullPost;
import com.daribear.prefy.customClasses.Posts.StandardPost;

import java.util.ArrayList;

/**
 * The recyclerview for the profile posts.
 * This adapter is responsible for displaying a list of StandardPost items on a user's profile.
 * Each item shows the post image, question, vote counts, user info, and comment counts.
 * It also handles interactions such as voting, opening the post dropdown menu, navigating to comments,
 * and adjusting the layout to match top and bottom bars.
 *
 */
public class ProfilePostListAdaptor extends RecyclerView.Adapter<ProfilePostListAdaptor.ViewHolder>{
    private ArrayList<StandardPost> postList;
    private String username, profileImageUrl;
    private Activity parentActivity;


    public ProfilePostListAdaptor(ArrayList<StandardPost> postList, String username, String profileImageUrl, Activity parentActivity) {
        this.postList = postList;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.parentActivity = parentActivity;
    }

    //Inflate the view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_post_list_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //Bind the viewholder for each posts
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Integer totalVotes = (postList.get(position).getLeftVotes() + postList.get(position).getRightVotes());
        holder.totalVotes.setText(totalVotes + " Votes");
        holder.usernameText.setText("@" + username);
        holder.questionText.setText(postList.get(position).getQuestion());
        holder.commentsText.setText(postList.get(position).getCommentsNumber().toString());
        holder.timeSinceText.setText(dateSinceSystem.getTimeSince(postList.get(position).getCreationDate()));
        initGlide(holder, position);
        initCommentsClick(holder, position);
        initVoteSystem(holder, position);
        initMoreButton(holder, position);
    }





    @Override
    public int getItemCount() {
        return postList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView totalVotes, usernameText, questionText, commentsText, timeSinceText;
        ConstraintLayout topbar, bottomBar;
        ImageView imageView, profileImage;
        View leftVote, rightVote;
        ImageButton moreButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            totalVotes = itemView.findViewById(R.id.ProfilePostListListItemTotalVotes);
            topbar = itemView.findViewById(R.id.ProfilePostListListItemTopBar);
            bottomBar = itemView.findViewById(R.id.ProfilePostListListItemBottomBar);
            imageView = itemView.findViewById(R.id.ProfilePostListListItemQuestionImage);
            usernameText = itemView.findViewById(R.id.ProfilePostListListItemUsername);
            questionText = itemView.findViewById(R.id.ProfilePostListListItemQuestionText);
            commentsText = itemView.findViewById(R.id.ProfilePostListListItemCommentsButton);
            profileImage = itemView.findViewById(R.id.ProfilePostListListItemUserImage);
            timeSinceText = itemView.findViewById(R.id.ProfilePostListListItemPostDate);
            leftVote = itemView.findViewById(R.id.ProfilePostListListItemImageLeftClicker);
            rightVote = itemView.findViewById(R.id.ProfilePostListListItemImageRightClicker);
            moreButton = itemView.findViewById(R.id.ProfilePostListListItemMoreButton);
            getBarHeights(topbar, bottomBar);
        }
    }

    private void initGlide(ViewHolder holder, int position){
        if (postList.get(position).getImageURL() != null) {
            Glide
                    .with(holder.imageView)
                    .load(postList.get(position).getImageURL())
                    .into(holder.imageView);
        }
        if (profileImageUrl != null) {
            if (!profileImageUrl.equals("none")) {
                Glide
                        .with(holder.profileImage)
                        .load(profileImageUrl)
                        .circleCrop()
                        .into(holder.profileImage);
            } else {
                defaultImage(holder);
            }
        } else {
            profileImageUrl = "none";
            defaultImage(holder);
        }
    }
    /**
     * Set the default image if the user has no profile photo
     */
    private void defaultImage(ViewHolder holder){
        Glide
                .with(holder.profileImage)
                .load(R.drawable.user_photo)
                .circleCrop()
                .into(holder.profileImage);
    }

    /**
     * Initaites the vote system to allow the user to vote on the post
     * @param holder the viewholder
     * @param position the position of the item in the datalist
     */
    private void initVoteSystem(ViewHolder holder, int position){
        Context context = holder.itemView.getContext();
        holder.leftVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Left vote" + postList.get(position).getLeftVotes(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.rightVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Right vote" + postList.get(position).getRightVotes(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * Initiates a click on the comments button, which will navigae to the comments page
     */
    private void initCommentsClick(ViewHolder holder, int position){
        holder.commentsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User();
                user.setUsername(username);
                user.setProfileImageURL(profileImageUrl);
                Bundle bundle = new Bundle();
                bundle.putParcelable("post", postList.get(position));
                bundle.putParcelable("user", user);
                Navigation.findNavController(view).navigate(R.id.action_global_commentsFragment, bundle);
            }
        });
    }

    /**
     * Gets the bar heights to use for the vote animation
     * @param topbar the topbar
     * @param bottomBar the bottom bar
     */
    private void getBarHeights(ConstraintLayout topbar, ConstraintLayout bottomBar){
        topbar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
            @Override
            public boolean onPreDraw()
            {
                if (topbar.getViewTreeObserver().isAlive())
                    topbar.getViewTreeObserver().removeOnPreDrawListener(this);



                Integer TopbarHeight = topbar.getMeasuredHeight();
                ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) bottomBar.getLayoutParams();
                lp.height = TopbarHeight;
                bottomBar.setLayoutParams(lp);

                return true;
            }
        });
    }

    /**
     * Initializes the "more" button for a post item in the RecyclerView.
     *
     * When clicked, this button opens a PostDropDownDialog with options for the post.
     * It sets up the dialog with:
     * The post details (StandardPost and current user)
     * Whether the current user owns the post
     * Screen coordinates for positioning the dialog
     * The post image to display in the dialog
     * @param holder the viewholder
     * @param position the position of the post in the datalist
     */
    private void initMoreButton(ViewHolder holder, int position){
        PostDropDownDialog dialog = new PostDropDownDialog(holder.moreButton.getContext(), parentActivity,null, null);
        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullPost fullPost = new FullPost();
                fullPost.setStandardPost(postList.get(position));
                User user = ServerAdminSingleton.getCurrentUser(parentActivity.getApplicationContext());
                fullPost.setUser(user);
                Boolean loggedUserPost = user.getId().equals(ServerAdminSingleton.getCurrentUser(parentActivity.getApplicationContext()).getId());

                int test1[] = new int[2];
                holder.moreButton.getLocationOnScreen(test1);
                Integer bottomNavHeight = parentActivity.findViewById(R.id.BottomNav).getHeight();
                dialog.setDetails(loggedUserPost, fullPost);
                dialog.setCoordinates(0, test1[1] + holder.moreButton.getHeight() / 2);
                dialog.setImageDrawable(holder.imageView.getDrawable());
                dialog.initDialog();
            }
        });
    }
}
