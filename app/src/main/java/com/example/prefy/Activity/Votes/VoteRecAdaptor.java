package com.example.prefy.Activity.Votes;

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
import com.example.prefy.R;

import java.util.ArrayList;

public class VoteRecAdaptor extends RecyclerView.Adapter<VoteRecAdaptor.ViewHolder> {
    private ArrayList<VoteActivity> voteActivityList;

    public VoteRecAdaptor(ArrayList<VoteActivity> voteActivityList) {
        this.voteActivityList = voteActivityList;
    }

    public void setVoteActivityList(ArrayList<VoteActivity> voteActivityList) {
        this.voteActivityList = voteActivityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        initGlide(true, holder.profileImageView, voteActivityList.get(position).getUser().getProfileImageURL());
        initGlide(false, holder.postImageView, voteActivityList.get(position).getPost().getImageURL());
        initClickListener(holder.itemView, position);
        holder.textView.setText(getVoteText(voteActivityList.get(position), holder.itemView.getContext()));
    }

    @Override
    public int getItemCount() {
        return voteActivityList.size();
    }

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

    private void defaultImage(ImageView imageView){
        Glide.with(imageView)
                .load(R.drawable.user_photo)
                .into(imageView);
    }

    private SpannableStringBuilder getVoteText(VoteActivity voteActivity, Context context){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String textUsername;
        if (voteActivity.getUser().getUsername() != null && !voteActivity.getUser().getUsername().isEmpty()){
            textUsername = voteActivity.getUser().getUsername();
            SpannableString usernameSpannable= new SpannableString(textUsername);
            usernameSpannable.setSpan(new ForegroundColorSpan(context.getColor(R.color.text_color)), 0, textUsername.length(), 0);
            builder.append(usernameSpannable);
        } else {
            textUsername = "INVALID USER";
            SpannableString usernameSpannable= new SpannableString(textUsername);
            usernameSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, textUsername.length(), 0);
            builder.append(usernameSpannable);
        }
        String voteExtra = " and " + (voteActivity.getPost().getAllVotes() - 1) + " other people voted on your post: ";
        SpannableString voteExtraSpannable= new SpannableString(voteExtra);
        voteExtraSpannable.setSpan(new ForegroundColorSpan(context.getColor(R.color.grey)), 0, voteExtra.length(), 0);
        builder.append(voteExtraSpannable);


        String textQuestion;
        if (voteActivity.getPost().getQuestion() != null && !voteActivity.getPost().getQuestion().isEmpty()){
            textQuestion = voteActivity.getPost().getQuestion();
            SpannableString questionSpannable= new SpannableString(textQuestion);
            questionSpannable.setSpan(new ForegroundColorSpan(context.getColor(R.color.text_color)), 0, textQuestion.length(), 0);
            builder.append(questionSpannable);
        } else {
            textQuestion = "INVALID Comment";
            SpannableString commentSpannable= new SpannableString(textQuestion);
            commentSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, textQuestion.length(), 0);
            builder.append(commentSpannable);
        }
        return builder;
    }

    private void initClickListener(View itemview, int postion){
        ImageView profileImage = itemview.findViewById(R.id.ActivityListItemProfileImage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putLong("id", voteActivityList.get(postion).getUser().getId());
                bundle.putParcelable("user", voteActivityList.get(postion).getUser());
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
