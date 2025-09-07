package com.daribear.prefy.Explore.ExploreCategories.ExploreCategoriesPosts;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daribear.prefy.PostDropDownDialog;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.daribear.prefy.Utils.GeneralUtils.dateSinceSystem;
import com.daribear.prefy.Votes.VoteHandler;
import com.daribear.prefy.customClasses.Posts.FullPost;
import com.daribear.prefy.customClasses.Posts.StandardPost;

import java.util.ArrayList;

/**
 * RecyclerView Adapter to display explore category posts
 * handles votes, comments navigation, images and options
 */
public class ExploreCategoriesPostRecAdaptor extends RecyclerView.Adapter<ExploreCategoriesPostRecAdaptor.ViewHolder> {
    private ArrayList<FullPost> postList;

    private Activity activity;

    public ExploreCategoriesPostRecAdaptor(Activity activity) {
        this.postList = new ArrayList<>();
        this.activity = activity;
    }

    public void updateData(ArrayList<FullPost> postList){
        this.postList = postList;
        notifyDataSetChanged();
    }

    // inflates view and creates viewholder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_post_list_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //bind post data to viewHolder
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
        VoteHandler.changeImage(post, holder.imageView, holder.leftVote, holder.rightVote, "Categories");
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    /**
     * viewHolder class to reuse item views
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView totalVotes, usernameText, questionText, commentsText, timeSinceText;
        ConstraintLayout topbar, bottomBar;
        ImageView imageView, profileImage, optionsButton;
        RelativeLayout leftVote, rightVote;


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

    /**
     * Initialise Glide for post and profile images
     */
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
                        .circleCrop()
                        .into(holder.profileImage);
            } else {
                defaultImage(holder);
            }
        } else {
            defaultImage(holder);
        }
    }

    /**
     * load default user photo
     * @param holder viewholder
     */
    private void defaultImage(ViewHolder holder){
        Glide
                .with(holder.profileImage)
                .load(R.drawable.user_photo)
                .into(holder.profileImage);
    }

    /**
     * initialise voting system click listeners
     * @param holder viewHolder
     * @param position position in adapter
     */
    private void initVoteSystem(ViewHolder holder, int position){
        Context context = holder.itemView.getContext();
        FullPost post = postList.get(position);
        holder.leftVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //submit left vote only if not voted yet
                if (post.getStandardPost().getCurrentVote().equals("none") || post.getStandardPost().getCurrentVote().equals("skip")){
                    VoteHandler.voteSubmitted(post.getStandardPost(), holder.imageView, holder.leftVote, holder.rightVote, "leftClick", "Categories");
                    VoteHandler.saveVote(view.getContext().getApplicationContext(),post.getStandardPost().getPostId(), "left", "Categories");
                    String text = holder.totalVotes.getText().toString().split(" ")[0];
                    Integer oldNumber = Integer.parseInt(text);
                    VoteHandler.numberAnimator(oldNumber, post.getStandardPost().getAllVotes() ,holder.totalVotes);
                }

            }
        });
        holder.rightVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //submit right vote only if not voted yet
                if (post.getStandardPost().getCurrentVote().equals("none") || post.getStandardPost().getCurrentVote().equals("skip")){
                    VoteHandler.voteSubmitted(post.getStandardPost(), holder.imageView, holder.leftVote, holder.rightVote, "rightClick", "Categories");
                    VoteHandler.saveVote(view.getContext().getApplicationContext(),post.getStandardPost().getPostId(), "right", "Categories");
                    String text = holder.totalVotes.getText().toString().split(" ")[0];
                    Integer oldNumber = Integer.parseInt(text);
                    VoteHandler.numberAnimator(oldNumber, post.getStandardPost().getAllVotes() ,holder.totalVotes);
                }
            }
        });
    }

    /**
     * Setup comment click listener to navigate to comment fragment
     * @param holder viewHolder
     * @param position position in adapter
     */
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

    /**
     * initialise options button for post dropdown
     * @param holder viewHolder
     * @param position position in adapter
     */
    private void initOptions(ViewHolder holder, int position){
        PostDropDownDialog dialog = new PostDropDownDialog(holder.optionsButton.getContext(), activity, null, null);
        holder.optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullPost fullPost = postList.get(position);
                Boolean loggedUserPost = fullPost.getUser().getId().equals(ServerAdminSingleton.getCurrentUser(holder.optionsButton.getContext().getApplicationContext()).getId());

                dialog.setDetails(loggedUserPost, fullPost);
                dialog.setImageDrawable(holder.imageView.getDrawable());
                dialog.initDialog();
            }
        });
    }

    /**
     * adjust bottomBar height to match topbar height
     * @param topbar constraintLayout topbar
     * @param bottomBar constraintLayout bottomBar
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
}
