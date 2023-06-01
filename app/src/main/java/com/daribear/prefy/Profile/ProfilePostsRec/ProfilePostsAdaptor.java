package com.daribear.prefy.Profile.ProfilePostsRec;

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

import com.daribear.prefy.R;
import com.daribear.prefy.customClasses.Posts.StandardPost;

import java.util.ArrayList;

public class ProfilePostsAdaptor extends RecyclerView.Adapter<ProfilePostsAdaptor.ViewHolder> {
    private ArrayList<StandardPost> postList;
    private String profileImageLink, username;
    private Boolean fromHomeProfile;
    private Integer TargetInt;

    public ProfilePostsAdaptor(ArrayList<StandardPost> postList, String profileImageLink, String username, Boolean fromHomeProfile) {
        this.postList = postList;
        this.profileImageLink = profileImageLink;
        this.username = username;
        this.fromHomeProfile = fromHomeProfile;
        TargetInt = 15;
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
        initGlide(holder.imageView, position);
        holder.leftVotesText.setText(postList.get(position).getLeftVotes().toString());
        holder.rightVotesText.setText(postList.get(position).getRightVotes().toString());
        ItemViewClicked(position, holder);
    }

    public void setPostList(ArrayList<StandardPost> postList) {
        this.postList = postList;
    }

    public void addData(ArrayList<StandardPost> tempPostList){
        for (StandardPost post :tempPostList){
            Integer size = postList.size();
            postList.add(post);
            notifyItemInserted(size);
        }
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView leftVotesText, rightVotesText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ProfilePostListItemImage);
            leftVotesText = itemView.findViewById(R.id.ProfilePostListItemLeftVote);
            rightVotesText = itemView.findViewById(R.id.ProfilePostListItemRightVote);
        }
    }

    private void initGlide(ImageView imageView, Integer pos){
        if (postList.get(pos).getImageURL() != null) {
            Glide
                    .with(imageView)
                    .load(postList.get(pos).getImageURL())
                    .into(imageView);
        }
    }

    private void ItemViewClicked(Integer position, ViewHolder holder){
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(holder.itemView);
                StandardPost[] postArray = new StandardPost[postList.size()];
                for (int i = 0 ; i < postList.size(); i ++){
                    postArray[i] = postList.get(i);
                }
                /**
                if (fromHomeProfile) {
                    HomeProfileFragmentDirections.ActionProfileFragmentToProfilePostListFragment action = HomeProfileFragmentDirections.actionProfileFragmentToProfilePostListFragment(postArray);
                    action.setUsername(username);
                    action.setProfileImageLink(profileImageLink);
                    action.setPositionClicked(position);
                    navController.navigate(action);
                } else {
                    UserProfileFragmentDirections.ActionUserProfileToProfilePostListFragment action = UserProfileFragmentDirections.actionUserProfileToProfilePostListFragment(postArray);
                    action.setUsername(username);
                    action.setProfileImageLink(profileImageLink);
                    action.setPositionClicked(position);
                    navController.navigate(action);
                }
                 */

            }
        });
    }



    public Long getId(){
        if (postList != null){
            if (postList.size() > 0){
                return postList.get(0).getUserId();
            } else return null;
        } else return null;
    }
    /**
    public String getFirstCreationData(){
        if (postList != null){
            if (postList.size() > 0){
                return postList.get(postList.size() - 1).getCreationDate_uid();
            } else return null;
        } else return null;
    }
     */





}
