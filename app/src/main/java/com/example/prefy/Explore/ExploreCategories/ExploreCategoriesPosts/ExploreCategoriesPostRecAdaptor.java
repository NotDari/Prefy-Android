package com.example.prefy.Explore.ExploreCategories.ExploreCategoriesPosts;

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
import com.example.prefy.PostDropDownDialog;
import com.example.prefy.Profile.User;
import com.example.prefy.R;
import com.example.prefy.Utils.dateSinceSystem;
import com.example.prefy.customClasses.FullPost;
import com.example.prefy.customClasses.StandardPost;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ExploreCategoriesPostRecAdaptor extends RecyclerView.Adapter<ExploreCategoriesPostRecAdaptor.ViewHolder> {
    private ArrayList<FullPost> postList;

    public ExploreCategoriesPostRecAdaptor() {
        this.postList = new ArrayList<>();
    }

    public void updateData(ArrayList<FullPost> postList){
        this.postList = postList;
        notifyDataSetChanged();
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
        StandardPost post = postList.get(position).getStandardPost();
        Integer totalVotes = (post.getLeftVotes() + post.getRightVotes());
        holder.totalVotes.setText(totalVotes + " Votes");
        holder.usernameText.setText("@" + postList.get(position).getUser().getUsername());
        holder.questionText.setText(post.getQuestion());
        holder.commentsText.setText(post.getCommentsNumber().toString());
        holder.timeSinceText.setText(dateSinceSystem.getTimeSince(post.getCreationDate()));
        initGlide(holder, position);
        initCommentsClick(holder, position);
        initVoteSystem(holder, position);
        initOptions(holder, position);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView totalVotes, usernameText, questionText, commentsText, timeSinceText;
        ConstraintLayout topbar, bottomBar;
        ImageView imageView, profileImage, optionsButton;
        View leftVote, rightVote;


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
            optionsButton = itemView.findViewById(R.id.ProfilePostListListItemMoreButton);
            getBarHeights(topbar, bottomBar);
        }
    }

    private void initGlide(ViewHolder holder, int position){
        if (postList.get(position).getStandardPost().getImageURL() != null) {
            Glide
                    .with(holder.imageView)
                    .load(postList.get(position).getStandardPost().getImageURL())
                    .into(holder.imageView);
        }
        if (postList.get(position).getUser().getProfileImageURL() != null) {
            if (!postList.get(position).getUser().getProfileImageURL().equals("none")) {
                Glide
                        .with(holder.profileImage)
                        .load(postList.get(position).getUser().getProfileImageURL())
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
                Toast.makeText(context, "Left vote" + postList.get(position).getStandardPost().getLeftVotes(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.rightVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Right vote" + postList.get(position).getStandardPost().getRightVotes(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initCommentsClick(ViewHolder holder, int position){
        holder.commentsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("post", postList.get(position).getStandardPost());
                bundle.putParcelable("user", postList.get(position).getUser());
                Navigation.findNavController(view).navigate(R.id.action_global_commentsFragment, bundle);
            }
        });
    }

    private void initOptions(ViewHolder holder, int position){
        holder.optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullPost fullPost = postList.get(position);
                //PostDropDownDialog dialog = new PostDropDownDialog(v.getContext(), loggedUserPost, ,fullPost);
                //Integer bottomNavHeight = parentActivity.findViewById(R.id.BottomNav).getHeight();
                //dialog.setCoordinates(0, bottomNavHeight);
                //dialog.setImageDrawable(postImage.getDrawable());
                //dialog.initDialog();
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
}
