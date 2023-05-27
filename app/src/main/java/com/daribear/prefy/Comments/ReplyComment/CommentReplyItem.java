package com.daribear.prefy.Comments.ReplyComment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.daribear.prefy.Comments.Comment;
import com.daribear.prefy.Comments.CommentDeleted;
import com.daribear.prefy.Comments.CommentReplyClicked;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.dateSinceSystem;

public class CommentReplyItem extends FrameLayout implements CommentDeleted {
    private Comment comment;

    private TextView usernameText, commentText, timeSinceText;
    private ImageView userProfileImage;
    private ImageButton moreButton;
    private View bottomView;
    private Activity ownerActivity;
    private CommentReplyClicked commentReplyDelegate;

    private CommentReplyDeletedDelegate commentReplyDeletedDelegate;



    public CommentReplyItem(Context context, Comment comment, DisplayMetrics displayMetrics, Activity ownerActivity, CommentReplyClicked commentReplyDelegate, CommentReplyDeletedDelegate commentReplyDeletedDelegate) {
        super(context);
        this.comment = comment;
        this.ownerActivity = ownerActivity;
        init(context);
        setImageSize((int) (displayMetrics.widthPixels * .1));
        resizeImageButon(displayMetrics);
        this.commentReplyDelegate = commentReplyDelegate;
        this.commentReplyDeletedDelegate = commentReplyDeletedDelegate;
    }

    private void resizeImageButon(DisplayMetrics displayMetrics){
        Integer value = (int)(displayMetrics.widthPixels * .075);
        this.moreButton.getLayoutParams().width = value;
    }

    private void setImageSize(Integer widthHeight){
        this.userProfileImage.getLayoutParams().height = widthHeight;
        this.userProfileImage.getLayoutParams().width = widthHeight;
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.comment_reply_item, this);
        getViews();
        setViews();
    }

    private void getViews(){
        usernameText = findViewById(R.id.CommentReplyItemUsername);
        commentText = findViewById(R.id.CommentReplyItemCommentText);
        userProfileImage = findViewById(R.id.CommentReplyItemImage);
        timeSinceText = findViewById(R.id.CommentReplyItemTimeSince);
        moreButton = findViewById(R.id.CommentReplyItemMoreButton);
        bottomView = findViewById(R.id.CommentReplyItemBottomView);

        handleViews();
    }
    private void handleViews(){
        moreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentReplyPopUp dialog = new CommentReplyPopUp(comment, ownerActivity, commentReplyDelegate, CommentReplyItem.this::deleteClicked);
                dialog.initDialog();
            }
        });
        userProfileImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putLong("id", comment.getUser().getId());
                bundle.putParcelable("user", comment.getUser());
                Navigation.findNavController(ownerActivity, R.id.FragmentContainerView).navigate(R.id.action_global_userProfile, bundle);
            }
        });
    }

    private void setViews(){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (comment.getReplyUsername() != null){
            String replyText = "@" + comment.getReplyUsername() + ", ";
            SpannableString replySpannable= new SpannableString(replyText);
            replySpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, replyText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(replySpannable);
        }
        builder.append(comment.getText());
        commentText.setText(builder);
        usernameText.setText(comment.getUser().getUsername());
        timeSinceText.setText(dateSinceSystem.getTimeSince(comment.getCreationDate()));
        initGlide();
    }

    private void initGlide(){
        String image = comment.getUser().getProfileImageURL();
        if (image != null){
            if (!image.equals("none")){
                Glide
                        .with(userProfileImage)
                        .load(image)
                        .circleCrop()
                        .placeholder(R.drawable.user_photo)
                        .into(userProfileImage);
            }
        }
    }


    @Override
    public void deleteClicked(Long commentId) {
        commentReplyDeletedDelegate.deletedReply(CommentReplyItem.this);
    }
}
