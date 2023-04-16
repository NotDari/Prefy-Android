package com.daribear.prefy.Comments;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daribear.prefy.Comments.ReplyComment.CommentReplyHandler;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.dateSinceSystem;
import com.daribear.prefy.customClasses.FullPost;

import java.util.ArrayList;

public class CommentListAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CommentDeleted{
    public final Integer COMMENT_HEADER = 0;
    public final Integer COMMENT_ITEMS = 1;
    public final Integer COMMENT_FOOTER = 2;
    private ArrayList<FullRecComment> commentList;
    private CommentHeaderItem commentHeaderItem;
    private CommentFooterViewHolder currentFooterViewHolder;
    private FullPost fullPost;
    private Boolean noInternetDefault, viewMoreVisible = false;
    private handleCommentsRecView handleCommentsRecView;
    private CommentViewMoreClicked commentViewMoreDelegate;
    private Activity parentActivity;
    private CommentReplyClicked commentDelegate;

    public CommentListAdaptor(handleCommentsRecView handleCommentsRecView,ArrayList<FullRecComment> commentList, FullPost fullPost, CommentViewMoreClicked commentViewMoreDelegate, Activity parentActivity, CommentReplyClicked commentDelegate) {
        this.handleCommentsRecView = handleCommentsRecView;
        this.commentList = commentList;
        this.fullPost = fullPost;
        this.commentViewMoreDelegate = commentViewMoreDelegate;
        this.parentActivity = parentActivity;
        this.commentDelegate = commentDelegate;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == COMMENT_HEADER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_header_item, parent, false);
            holder = new CommentHeaderViewHolder(view);
        }
        else if (viewType == COMMENT_ITEMS){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
            holder = new CommentItemViewHolder(view);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity)  ( ((CommentItemViewHolder)holder).userProfileImage.getContext())).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            ViewGroup.LayoutParams lp = ((CommentItemViewHolder)holder).userProfileImage.getLayoutParams();
            lp.width = (int) ((int) screenWidth * 0.15);
            lp.height = lp.width;
            ((CommentItemViewHolder)holder).userProfileImage.setLayoutParams(lp);
        }
        else if (viewType == COMMENT_FOOTER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_footer_item, parent, false);
            view.setForegroundGravity(View.TEXT_ALIGNMENT_CENTER);
            holder = new CommentFooterViewHolder(view);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity)  ( ((CommentFooterViewHolder)holder).itemView.getContext())).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels;
            ((ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams()).topMargin = (int) (screenHeight * .04);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
            holder = new CommentItemViewHolder(view);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == COMMENT_ITEMS){
            Integer refactoredPosition = holder.getAdapterPosition() - 1;
            CommentItemViewHolder commentHolder = (CommentItemViewHolder) holder;
            initGlide(commentHolder, refactoredPosition);
            handleMoreClick((CommentItemViewHolder) holder, refactoredPosition);
            //holder.usernameText.setText(commentList.get(position).getReplyUsername());
            SpannableStringBuilder builder = new SpannableStringBuilder();
            if (commentList.get(refactoredPosition).getFullComment().getComment().getReplyUsername() != null){
                String replyText = "@" + commentList.get(refactoredPosition).getFullComment().getComment().getReplyUsername() + ", ";
                SpannableString replySpannable= new SpannableString(replyText);
                replySpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, replyText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append(replySpannable);
            }
            builder.append(commentList.get(refactoredPosition).getFullComment().getComment().getText());
            commentHolder.commentText.setText(builder);
            commentHolder.usernameText.setText(commentList.get(refactoredPosition).getFullComment().getComment().getUser().getUsername());
            commentHolder.timeSinceText.setText(dateSinceSystem.getTimeSince(commentList.get(refactoredPosition).getFullComment().getComment().getCreationDate()));
            CommentReplyHandler commentReplyHandler = new CommentReplyHandler(commentList.get(refactoredPosition), commentHolder, parentActivity, refactoredPosition, commentDelegate);
            commentReplyHandler.init();
            handleClick(commentHolder, refactoredPosition);
        } else if (holder.getItemViewType() == COMMENT_HEADER){
            if (noInternetDefault == null){
                noInternetDefault = false;
            }
            commentHeaderItem = new CommentHeaderItem(handleCommentsRecView,this,holder, fullPost, noInternetDefault);
        }
        else if (holder.getItemViewType() == COMMENT_FOOTER){
            viewMoreClicked((CommentFooterViewHolder) holder);
            this.currentFooterViewHolder = (CommentFooterViewHolder) holder;
            if (!viewMoreVisible){
                holder.itemView.setVisibility(View.GONE);
                ((ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams()).topMargin = 0;
            } else {
                holder.itemView.setVisibility(View.VISIBLE);
            }
           // ((TextView)holder.itemView).setText("View More");
        }
    }
    public void addComments(ArrayList<FullRecComment> commentList){
        noInternetDefault = false;
        for (int i = this.commentList.size(); i < commentList.size(); i ++){
            this.commentList.add(commentList.get(i));
        }
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (commentList.size() > 0) {
                    commentHeaderItem.NoItemsCheck(false);
                } else {
                    commentHeaderItem.NoItemsCheck(true);
                }
            }
        });
    }

    public void setCommentList(ArrayList<FullRecComment> commentList) {
        this.commentList = commentList;
        noInternetDefault = false;
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (commentList.size() > 0) {
                    commentHeaderItem.NoItemsCheck(false);
                } else {
                    commentHeaderItem.NoItemsCheck(true);
                }
            }
        });

    }

    public ArrayList<FullRecComment> getCommentList() {
        return commentList;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return COMMENT_HEADER;
        } else if (position == (getItemCount() - 1))  {
            return COMMENT_FOOTER;
        } else {
            return COMMENT_ITEMS;
        }
    }

    @Override
    public int getItemCount() {
        return 2 + commentList.size();
    }


    @Override
    public void deleteClicked(FullComment comment) {
        this.commentList.remove(comment);
        notifyDataSetChanged();
    }


    public class CommentItemViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameText, commentText, timeSinceText, bottomView;
        public ImageView userProfileImage;
        public ImageButton moreButton;
        public LinearLayout replyLayout;

        public CommentItemViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.CommentListItemUsername);
            commentText = itemView.findViewById(R.id.CommentListItemCommentText);
            userProfileImage = itemView.findViewById(R.id.CommentListItemImage);
            timeSinceText = itemView.findViewById(R.id.CommentListItemTimeSince);
            moreButton = itemView.findViewById(R.id.CommentListItemMoreButton);
            bottomView = itemView.findViewById(R.id.CommentListItemBottomView);
            replyLayout = itemView.findViewById(R.id.CommentListItemReplyLinLay);
        }


    }

    public class CommentHeaderViewHolder extends RecyclerView.ViewHolder {


        public CommentHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }

    public class CommentFooterViewHolder extends RecyclerView.ViewHolder {
        TextView viewMoreButton;


        public CommentFooterViewHolder(@NonNull View itemView) {
            super(itemView);
            viewMoreButton = itemView.findViewById(R.id.CommentsViewMoreTextView);
        }
    }

    public void initNoInternet(){
        if (commentHeaderItem != null) {
            commentHeaderItem.initNoInternet();
        } else {
            noInternetDefault = true;
        }
    }

    private void initGlide(CommentItemViewHolder holder, int position){
        String image = commentList.get(position).getFullComment().getComment().getUser().getProfileImageURL();
        if (image != null){
            if (!image.equals("none")){
                Glide
                        .with(holder.userProfileImage)
                        .load(image)
                        .circleCrop()
                        .placeholder(R.drawable.user_photo)
                        .into(holder.userProfileImage);
            }
        }
    }

    private void handleClick(CommentItemViewHolder holder, int position){
        holder.userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putLong("id", commentList.get(position).getFullComment().getComment().getUserId());
                bundle.putParcelable("user", commentList.get(position).getFullComment().getComment().getUser());
                Navigation.findNavController(view).navigate(R.id.action_global_userProfile, bundle);
            }
        });
    }

    public void setViewMoreVisibility(Boolean visible){
        viewMoreVisible = visible;
        if (currentFooterViewHolder != null){
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (visible){
                        currentFooterViewHolder.itemView.setVisibility(View.VISIBLE);
                    }else {
                        currentFooterViewHolder.itemView.setVisibility(View.GONE);
                    }
                }
            });

        }
    }

    private void viewMoreClicked(CommentFooterViewHolder holder){
        holder.viewMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentViewMoreDelegate.viewClicked();
            }
        });
    }

    private void handleMoreClick(CommentItemViewHolder holder, int position){
        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullComment fullComment = commentList.get(position).getFullComment();
                CommentMorePopUpDialog dialog = new CommentMorePopUpDialog(fullComment, parentActivity, commentDelegate, CommentListAdaptor.this::deleteClicked);
                Integer bottomNavHeight = parentActivity.findViewById(R.id.BottomNav).getHeight();
                dialog.setCoordinates(0, bottomNavHeight);
                dialog.initDialog();
            }
        });
    }


}
