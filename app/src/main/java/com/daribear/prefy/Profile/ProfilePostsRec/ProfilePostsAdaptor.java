package com.daribear.prefy.Profile.ProfilePostsRec;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;
import com.daribear.prefy.customClasses.Containers.PostListContainer;
import com.daribear.prefy.customClasses.Posts.StandardPost;

import java.util.ArrayList;

/**
 *
 */
public class ProfilePostsAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private PostListContainer postListContainer;
    private Boolean fromHomeProfile;
    public final Integer PROFILE_HEADER = 0;
    public final Integer PROFILE_RECTWOIMAGES = 1;
    private Integer spanCount;
    private User user;
    private RecyclerView.ViewHolder currentHolder;
    private Integer recyclercounter;
    private ProfileHeaderController profileHeaderController;

    public ProfilePostsAdaptor(PostListContainer postListContainer, User user, Boolean fromHomeProfile, Integer spanCount) {
        this.postListContainer = postListContainer;
        this.user = user;
        this.fromHomeProfile = fromHomeProfile;
        this.spanCount = spanCount;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == PROFILE_HEADER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_header_item, parent, false);
            holder = new HeaderViewHolder(view);
        } else if (viewType == PROFILE_RECTWOIMAGES){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_post_list_item, parent, false);
            holder = new TwoVoteViewHolder(view);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) holder.itemView.getRootView().getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = displaymetrics.widthPixels / 3;
            holder.itemView.getLayoutParams().height = width;
            holder.itemView.getLayoutParams().width = width;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_post_list_item, parent, false);
            holder = new TwoVoteViewHolder(view);
        }
        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0){
            recyclercounter = 0;
        }
        recyclercounter += 1;
        this.currentHolder = holder;

        if (holder.getItemViewType() == PROFILE_RECTWOIMAGES){
            TwoVoteViewHolder viewHolder = (TwoVoteViewHolder) holder;
            initGlide(viewHolder.imageView, position - 1);
            viewHolder.leftVotesText.setText(postListContainer.getPostList().get(position- 1).getLeftVotes().toString());
            viewHolder.rightVotesText.setText(postListContainer.getPostList().get(position- 1).getRightVotes().toString());
            ItemViewClicked(position- 1, viewHolder);
        } else if (holder.getItemViewType() == PROFILE_HEADER){
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            viewHolder.setIsRecyclable(false);
            profileHeaderController = new ProfileHeaderController(user, fromHomeProfile, viewHolder.itemView);
        }

    }

    public void initPostList(ArrayList<StandardPost> postList) {
        Integer originalSize = this.postListContainer.getPostList().size();
        this.postListContainer.setPostList(new ArrayList<>());
        if (originalSize > 0){
            notifyItemRangeRemoved(1 , originalSize + 1);
        }
        if (postList.size() > 0) {
            this.postListContainer.setPostList(postList);
            notifyItemRangeInserted(1, postList.size() + 1);
        }
    }

    public void addData(PostListContainer postListContainer){
        Integer size = postListContainer.getPostList().size();
        Integer extra = postListContainer.getPostList().size();
        for (StandardPost post : postListContainer.getPostList()){
            this.postListContainer.getPostList().add(post);
        }
        this.postListContainer.setPageNumber(postListContainer.getPageNumber());
        notifyItemRangeChanged(size, extra);

    }

    public void updateUserInfo(User user){
        this.user = user;
        if (profileHeaderController != null){
            profileHeaderController.updateUserInfo(user);
        }
    }




    @Override
    public int getItemCount() {
        return postListContainer.getPostList().size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return PROFILE_HEADER;
        } else {
            return PROFILE_RECTWOIMAGES;
        }
    }

    public class TwoVoteViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView leftVotesText, rightVotesText;

        public TwoVoteViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ProfilePostListItemImage);
            leftVotesText = itemView.findViewById(R.id.ProfilePostListItemLeftVote);
            rightVotesText = itemView.findViewById(R.id.ProfilePostListItemRightVote);

        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {


        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private void initGlide(ImageView imageView, Integer pos){
        if (postListContainer.getPostList().get(pos).getImageURL() != null) {
            Glide
                    .with(imageView)
                    .load(postListContainer.getPostList().get(pos).getImageURL())
                    .into(imageView);
        }
    }

    private void ItemViewClicked(Integer position, TwoVoteViewHolder holder){
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(holder.itemView);
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", user);
                bundle.putParcelable("post", postListContainer.getPostList().get(position));
                if (fromHomeProfile){
                    Navigation.findNavController(view).navigate(R.id.action_Profile_fragment_to_profilePostListItemFragment, bundle);
                }else {
                    Navigation.findNavController(view).navigate(R.id.action_userProfile_to_profilePostListItemFragment, bundle);

                }


            }
        });
    }

    public Integer getAdaptorPostition(){
        return currentHolder.getAdapterPosition();
    }



    public Long getUserId(){
        if (postListContainer.getPostList() != null){
            if (postListContainer.getPostList().size() > 0){
                return postListContainer.getPostList().get(0).getUserId();
            } else return null;
        } else return null;
    }

    public Integer getPageNumber(){
        return postListContainer.getPageNumber();
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder.getItemViewType() == PROFILE_RECTWOIMAGES) {
            TwoVoteViewHolder viewHolder = (TwoVoteViewHolder) holder;
            Glide.with(viewHolder.imageView).clear(viewHolder.imageView);
        } else {
        }
        super.onViewRecycled(holder);
    }


}

