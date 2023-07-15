package com.daribear.prefy.Search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.R;

import java.util.ArrayList;

public class SearchUserAdaptor extends RecyclerView.Adapter<SearchUserAdaptor.ViewHolder> {
    private SearchRecViewTopTargetReached topDelegate;
    private SearchRecSearchTargetReached stringDelegate;
    private ArrayList<User> searchUserArrayList;
    private Integer targetSize;
    private String type;
    private RecyclerView recView;

    public SearchUserAdaptor(SearchRecViewTopTargetReached topDelegate, SearchRecSearchTargetReached stringDelegate, RecyclerView recView) {
        this.topDelegate = topDelegate;
        this.stringDelegate = stringDelegate;
        this.recView = recView;
    }

    public void setType(String type) {
        this.type = type;
        targetSize = 10;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        initGlide(holder, position, searchUserArrayList.get(position).getProfileImageURL());
        initCounts(holder, position);
        initTexts(holder, position);
        initClickListener(holder, position);
        initTargetReached(position);
    }

    public void setSearchUserArrayList(ArrayList<User> searchUserArrayList) {
        targetSize = searchUserArrayList.size();
        this.searchUserArrayList = searchUserArrayList;
    }

    @Override
    public int getItemCount() {
        return searchUserArrayList.size();
    }






    public void addUsers(ArrayList<User> searchUserArrayList){
        for (User user: searchUserArrayList){
            if (this.searchUserArrayList != null) {
                Integer size = this.searchUserArrayList.size();
                this.searchUserArrayList.add(user);
                notifyItemInserted(size);
            }
        }
        targetSize = this.searchUserArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView profileImage;
        private TextView FollowerCount, PostCount;
        private TextView bio, username, fullname;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.SearchItemProfileImageView);
            FollowerCount = itemView.findViewById(R.id.SearchItemFollowersCount);
            PostCount = itemView.findViewById(R.id.SearchItemPostsCount);
            fullname = itemView.findViewById(R.id.SearchItemFullnameText);
            username = itemView.findViewById(R.id.SearchItemUsernameText);
            bio = itemView.findViewById(R.id.SearchItemBioText);
        }
    }

    private void initCounts(ViewHolder holder, int position){
        String followeradditionalText;
        String postadditionalText;
        if (searchUserArrayList.get(position).getFollowerNumber() == 1){
            followeradditionalText = " follower";
        } else {
            followeradditionalText = " followers";
        }
        if (searchUserArrayList.get(position).getPostsNumber() == 1){
            postadditionalText = " post";
        } else {
            postadditionalText = " posts";
        }
        holder.FollowerCount.setText(searchUserArrayList.get(position).getFollowerNumber() + followeradditionalText);
        holder.PostCount.setText(searchUserArrayList.get(position).getPostsNumber() + postadditionalText);
    }


    private void initGlide(ViewHolder holder, int position, String imageUrl){
        if (imageUrl != null) {
            if (!imageUrl.equals("none")) {
                Glide
                        .with(holder.profileImage)
                        .load(imageUrl)
                        .circleCrop()
                        .into(holder.profileImage);
            } else {
                defaultImage(holder);
            }
        } else {
            imageUrl = "none";
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

    private void initTexts(ViewHolder holder, int position){
        holder.bio.setText(searchUserArrayList.get(position).getBio());
        holder.username.setText(searchUserArrayList.get(position).getUsername());
        holder.fullname.setText(searchUserArrayList.get(position).getFullname());
    }

    private void initClickListener(ViewHolder holder, int position){
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", searchUserArrayList.get(position));
                Navigation.findNavController(view).navigate(R.id.action_global_userProfile, bundle);
            }
        });
    }

    private void initTargetReached(Integer adaptorPosition){
        if (adaptorPosition == targetSize - 1) {
            if (type != null){
                if (type.equals("Top")) {
                    topDelegate.topReached();
                }else if (type.equals("String")){
                    stringDelegate.topReached(searchUserArrayList.get(searchUserArrayList.size()- 1).getUsername());
                }
                targetSize += 15;
            }
        }
    }
}
