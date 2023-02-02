package com.example.prefy.Activity.Comment;

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
import com.example.prefy.Comments.CommentListAdaptor;
import com.example.prefy.NavGraphDirections;
import com.example.prefy.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CommentRecAdaptor extends RecyclerView.Adapter<CommentRecAdaptor.ViewHolder> {
    private ArrayList<CommentActivity> commentActivityList;

    public CommentRecAdaptor(ArrayList<CommentActivity> commentActivityList) {
        this.commentActivityList = commentActivityList;
    }

    public void setCommentActivityList(ArrayList<CommentActivity> commentActivityList) {
        this.commentActivityList = commentActivityList;
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

        initGlide(true, holder.profileImageView, commentActivityList.get(position).getUser().getProfileImageURL());
        initGlide(false, holder.postImageView, commentActivityList.get(position).getPostImageURL());
        initClickListener(holder.itemView, position);
        holder.textView.setText(getComment(commentActivityList.get(position).getUser().getUsername(), commentActivityList.get(position).getText(), holder.itemView.getContext()), TextView.BufferType.SPANNABLE);
    }

    @Override
    public int getItemCount() {
        return commentActivityList.size();
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
                .circleCrop()
                .into(imageView);
    }

    private SpannableStringBuilder getComment(String username, String comment, Context context){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String textUsername;
        if (username != null && !username.isEmpty()){
            textUsername = username;
            SpannableString usernameSpannable= new SpannableString(textUsername);
            usernameSpannable.setSpan(new ForegroundColorSpan(context.getColor(R.color.text_color)), 0, textUsername.length(), 0);
            builder.append(usernameSpannable);
        } else {
            textUsername = "INVALID USER";
            SpannableString usernameSpannable= new SpannableString(textUsername);
            usernameSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, textUsername.length(), 0);
            builder.append(usernameSpannable);
        }

        String commentExtra = " commented on your post: ";
        SpannableString commentExtraSpannable= new SpannableString(commentExtra);
        commentExtraSpannable.setSpan(new ForegroundColorSpan(context.getColor(R.color.grey)), 0, commentExtra.length(), 0);
        builder.append(commentExtraSpannable);


        String textComment;
        if (comment != null && !comment.isEmpty()){
            textComment = comment;
            SpannableString commentSpannable= new SpannableString(textComment);
            commentSpannable.setSpan(new ForegroundColorSpan(context.getColor(R.color.text_color)), 0, textComment.length(), 0);
            builder.append(commentSpannable);
        } else {
            textComment = "INVALID Comment";
            SpannableString commentSpannable= new SpannableString(textComment);
            commentSpannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, textComment.length(), 0);
            builder.append(commentSpannable);
        }
        return builder;
    }

    private void initClickListener(View itemview, int postion){
        itemview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putLong("id", commentActivityList.get(postion).getUser().getId());
                bundle.putParcelable("user", commentActivityList.get(postion).getUser());
                Navigation.findNavController(view).navigate(R.id.action_global_userProfile, bundle);
            }
        });
    }
}
