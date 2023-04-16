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
import com.daribear.prefy.PostDropDownDialog;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.daribear.prefy.Utils.dateSinceSystem;
import com.daribear.prefy.customClasses.FullPost;
import com.daribear.prefy.customClasses.StandardPost;

import java.util.ArrayList;

public class ProfilePostListAdaptor extends RecyclerView.Adapter<ProfilePostListAdaptor.ViewHolder> {
    private ArrayList<StandardPost> postList;
    private String username, profileImageUrl;
    private Activity parentActivity;


    public ProfilePostListAdaptor(ArrayList<StandardPost> postList, String username, String profileImageUrl, Activity parentActivity) {
        this.postList = postList;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.parentActivity = parentActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_post_list_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

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
    private void defaultImage(ViewHolder holder){
        Glide
                .with(holder.profileImage)
                .load(R.drawable.user_photo)
                .circleCrop()
                .into(holder.profileImage);
    }

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

    private void initMoreButton(ViewHolder holder, int position){
        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullPost fullPost = new FullPost();
                fullPost.setStandardPost(postList.get(position));
                User user = ServerAdminSingleton.getCurrentUser(parentActivity.getApplicationContext());
                fullPost.setUser(user);
                Boolean loggedUserPost = user.getId().equals(ServerAdminSingleton.getCurrentUser(parentActivity.getApplicationContext()).getId());
                PostDropDownDialog dialog = new PostDropDownDialog(view.getContext(), loggedUserPost, parentActivity,fullPost, null, null);
                int test1[] = new int[2];
                holder.moreButton.getLocationOnScreen(test1);
                Integer bottomNavHeight = parentActivity.findViewById(R.id.BottomNav).getHeight();
                dialog.setCoordinates(0, test1[1] + holder.moreButton.getHeight() / 2);

                dialog.setImageDrawable(holder.imageView.getDrawable());
                dialog.initDialog();
            }
        });
    }
}
