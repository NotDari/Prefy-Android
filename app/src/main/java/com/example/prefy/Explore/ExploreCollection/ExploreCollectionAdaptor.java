package com.example.prefy.Explore.ExploreCollection;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.prefy.Explore.ExplorePost.ExplorePostDialog;
import com.example.prefy.Explore.ExplorePostSet;
import com.example.prefy.Profile.HomeProfileFragmentDirections;
import com.example.prefy.Profile.ProfileListPostRec.ProfilePostListAdaptor;
import com.example.prefy.Profile.ProfilePostsRec.ProfilePostsAdaptor;
import com.example.prefy.Profile.OtherUsers.UserProfileFragmentDirections;
import com.example.prefy.R;
import com.example.prefy.Utils.dateSinceSystem;
import com.example.prefy.customClasses.StandardPost;

import java.util.ArrayList;

public class ExploreCollectionAdaptor extends RecyclerView.Adapter<ExploreCollectionAdaptor.ViewHolder> {
    private ExplorePostSet explorePostSet;
    private Activity parentActivity;
    private Integer loadedViews = 0;

    public ExploreCollectionAdaptor(ExplorePostSet explorePostSet, Activity parentActivity) {
        this.explorePostSet = explorePostSet;
        this.parentActivity = parentActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_post_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0){
            loadedViews = 0;
        }
        loadedViews +=1;
        initGlide(holder.imageView, position);
        holder.voteCountLay.setVisibility(View.GONE);
        ItemViewClicked(position, holder);
        System.out.println("Sdad recView + loadedViews:" + loadedViews);
    }

    public void setPosts(ExplorePostSet explorePostSet) {
        this.explorePostSet = explorePostSet;
    }

    @Override
    public int getItemCount() {
        return explorePostSet.getPostList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView leftVotesText, rightVotesText;
        private ConstraintLayout voteCountLay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ProfilePostListItemImage);
            leftVotesText = itemView.findViewById(R.id.ProfilePostListItemLeftVote);
            rightVotesText = itemView.findViewById(R.id.ProfilePostListItemRightVote);
            voteCountLay = itemView.findViewById(R.id.ProfilePostListVoteCountConsLay);
        }
    }

    private void initGlide(ImageView imageView, Integer pos){
        if (explorePostSet.getPostList().get(pos).getStandardPost().getImageURL() != null) {
            Glide
                    .with(imageView)
                    .load(explorePostSet.getPostList().get(pos).getStandardPost().getImageURL())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(imageView);
        }
    }

    private void ItemViewClicked(Integer position, ViewHolder holder){
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExplorePostDialog dialog = new ExplorePostDialog(parentActivity, explorePostSet.getPostList().get(position).getUser(),explorePostSet.getPostList().get(position).getStandardPost(), parentActivity);
                dialog.initDialog();
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

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        //holder.imageView.setImageDrawable(null);
        super.onViewRecycled(holder);
        loadedViews -= 1;
        Glide.with(holder.itemView.getContext()).clear(holder.imageView);

    }

    public void viewDestroyed(){
        this.parentActivity = null;
    }
}
