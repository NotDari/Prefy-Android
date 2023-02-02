package com.example.prefy.Explore.ExploreList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prefy.Explore.ExploreCollection.ExploreCollectionAdaptor;
import com.example.prefy.Explore.ExplorePostSet;
import com.example.prefy.Profile.ProfileListPostRec.ProfilePostListAdaptor;
import com.example.prefy.Profile.User;
import com.example.prefy.R;
import com.example.prefy.Utils.dateSinceSystem;
import com.example.prefy.customClasses.StandardPost;

import java.util.ArrayList;

public class ExploreListAdaptor extends RecyclerView.Adapter<ExploreListAdaptor.ViewHolder> {
    private ExplorePostSet explorePostSet;

    public ExploreListAdaptor(ExplorePostSet explorePostSet) {
        this.explorePostSet = explorePostSet;
    }

    public void setPostList(ExplorePostSet explorePostSet) {
        this.explorePostSet = explorePostSet;
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
        Integer totalVotes = (explorePostSet.getPostList().get(position).getStandardPost().getLeftVotes() + explorePostSet.getPostList().get(position).getStandardPost().getRightVotes());
        holder.totalVotes.setText(totalVotes + " Votes");
        holder.usernameText.setText("@" + "aaaa");
        holder.questionText.setText(explorePostSet.getPostList().get(position).getStandardPost().getQuestion());
        holder.commentsText.setText(explorePostSet.getPostList().get(position).getStandardPost().getCommentsNumber().toString());
        holder.timeSinceText.setText(dateSinceSystem.getTimeSince(explorePostSet.getPostList().get(position).getStandardPost().getCreationDate()));
        initGlide(holder, position);
        initCommentsClick(holder, position);
        initVoteSystem(holder, position);
    }

    @Override
    public int getItemCount() {
        return explorePostSet.getPostList().size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView totalVotes, usernameText, questionText, commentsText, timeSinceText;
        private ConstraintLayout topbar, bottomBar;
        private ImageView imageView, profileImage;
        private View leftVote, rightVote;

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
            getBarHeights(topbar, bottomBar);
        }
    }

    private void initGlide(ViewHolder holder, int position){
        if (explorePostSet.getPostList().get(position).getStandardPost().getImageURL() != null) {
            Glide
                    .with(holder.imageView)
                    .load(explorePostSet.getPostList().get(position).getStandardPost().getImageURL())
                    .centerCrop()
                    .fitCenter()
                    .into(holder.imageView);
        }
        if (explorePostSet.getPostList().get(position).getUser().getProfileImageURL() != null) {
            if (!explorePostSet.getPostList().get(position).getUser().getProfileImageURL().equals("none")) {
                Glide
                        .with(holder.profileImage)
                        .load(explorePostSet.getPostList().get(position).getUser().getProfileImageURL())
                        .circleCrop()
                        .into(holder.profileImage);
            } else {
                defaultImage(holder);
            }
        } else {
            defaultImage(holder);
        }

    }
    private void defaultImage(ViewHolder holder){
        Glide
                .with(holder.profileImage)
                .load(R.drawable.user_photo)
                .into(holder.profileImage);
    }

    private void initVoteSystem(ViewHolder holder, int position){
        Context context = holder.itemView.getContext();
        holder.leftVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Left vote" + explorePostSet.getPostList().get(position).getStandardPost().getLeftVotes(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.rightVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Right vote" + explorePostSet.getPostList().get(position).getStandardPost().getRightVotes(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initCommentsClick(ViewHolder holder, int position){
        holder.commentsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User();
                user.setUsername("null");
                user.setProfileImageURL("null");
                Bundle bundle = new Bundle();
                bundle.putParcelable("post", explorePostSet.getPostList().get(position).getStandardPost());
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
    public Double returnLastCreationDate(){
        if (explorePostSet != null){
            if (explorePostSet.getPostList()!= null){
                if (explorePostSet.getPostList().size() > 0){
                    return explorePostSet.getPostList().get(explorePostSet.getPostList().size() - 1).getStandardPost().getCreationDate();
                }
            }
        }
        return null;
    }


}
