package com.example.prefy.Comments.ReplyComment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.prefy.Comments.Comment;
import com.example.prefy.Comments.CommentListAdaptor;
import com.example.prefy.Comments.FullRecComment;
import com.example.prefy.R;
import com.example.prefy.Utils.dateSinceSystem;

public class CommentReplyItem extends FrameLayout {
    private Comment comment;

    private TextView usernameText, commentText, timeSinceText;
    private ImageView userProfileImage;
    private ImageButton moreButton;
    private View bottomView;

    public CommentReplyItem(Context context, Comment comment) {
        super(context);
        this.comment = comment;
        init(context);
    }
    public CommentReplyItem(Context context, Comment comment, DisplayMetrics displayMetrics) {
        super(context);
        this.comment = comment;
        init(context);
        setImageSize((int) (displayMetrics.widthPixels * .1));
        resizeImageButon(displayMetrics);
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
    }

    private void setViews(){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        commentText.setText(comment.getText());
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



}
