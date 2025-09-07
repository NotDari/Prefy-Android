package com.daribear.prefy.Comments.ReplyComment;

import android.app.Activity;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.daribear.prefy.Comments.Comment;
import com.daribear.prefy.Comments.CommentDeleted;
import com.daribear.prefy.Comments.CommentListAdaptor;
import com.daribear.prefy.Comments.CommentReplyClicked;
import com.daribear.prefy.Comments.FullRecComment;
import com.daribear.prefy.R;

import java.util.ArrayList;

/**
 * Handles displaying and managing the reply comments, for a full comment.
 * Uses the ReplyDelegate to handle extra replies being added and CommentReplyDeletedDelegate to handle replies being deleted by the user.
 *
 */
public class CommentReplyHandler implements ReplyDelegate, CommentReplyDeletedDelegate {
    private FullRecComment fullRecComment;
    private CommentListAdaptor.CommentItemViewHolder viewHolder;
    private Activity parentActivity;
    private Integer position;
    private Integer pageNumber;
    private Boolean replyUpdateLoading;
    private Integer repliesAvailable, replyCount, commentsRemaining;
    private CommentReplyClicked commentDelegate;

    //Constructor
    public CommentReplyHandler(FullRecComment fullRecComment, CommentListAdaptor.CommentItemViewHolder viewHolder, Activity parentActivity, Integer position, CommentReplyClicked commentDelegate) {
        this.fullRecComment = fullRecComment;
        this.viewHolder = viewHolder;
        this.parentActivity = parentActivity;
        this.position = position;
        this.commentDelegate = commentDelegate;
    }

    /**
     * Initialises the reply section for a comment.
     * Sets up the text to get more replies and and adds the already received replies to the view.
     */
    public void init() {
        // start at page 0 for server requests
        pageNumber = 0;

        // get display metrics to calculate heights and scaling
        DisplayMetrics displayMetrics = new DisplayMetrics();
        replyUpdateLoading = false;
        parentActivity.getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        // total replies and currently shown replies
        repliesAvailable = fullRecComment.getFullComment().getReplyCount();
        replyCount = fullRecComment.getRepliesShown();
        commentsRemaining = repliesAvailable - fullRecComment.getRepliesShown();

        // configure bottom "Get X replies" view depending on replies remaining
        if (commentsRemaining > 0){
            viewHolder.bottomView.getLayoutParams().height =
                    (int) (18 * ((float) parentActivity.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
            viewHolder.bottomView.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.fragment_background));

            // text for bottom view depending on number of replies
            if (repliesAvailable == 1){
                viewHolder.bottomView.setText("Get 1 reply");
            } else {
                viewHolder.bottomView.setText("Get " + repliesAvailable + " replies");
            }

        } else {
            // no replies remaining so shrink bottom view
            viewHolder.bottomView.getLayoutParams().height =
                    (int) (1 * ((float) parentActivity.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
            viewHolder.bottomView.setBackgroundColor(Color.parseColor("#F1F1F1"));
            viewHolder.bottomView.setText(null);
        }

        // only show replies if there are any available
        if (repliesAvailable != 0){
            LinearLayout replyLay = viewHolder.replyLayout;
            viewHolder.replyLayout.setVisibility(View.VISIBLE);

            // hide bottom view if replies already shown
            if (fullRecComment.getRepliesShown() > 0){
                viewHolder.bottomView.setVisibility(View.GONE);
            }

            // populate already shown replies if comment is expanded
            if (!fullRecComment.getMinimised()){
                for (int i = 0; i < fullRecComment.getRepliesShown(); i ++){
                    CommentReplyItem commentReplyItem = new CommentReplyItem(
                            replyLay.getContext(),
                            fullRecComment.getFullComment().getCommentReplyList().get(i),
                            displayMetrics,
                            parentActivity,
                            commentDelegate,
                            CommentReplyHandler.this::deletedReply);
                    replyLay.addView(commentReplyItem);
                }
            }

            // bottom view click listener to load more replies
            viewHolder.bottomView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // logic to load replies in batches if repliesAvailable >= 10
                    if (repliesAvailable >= 10){
                        // check if we still have replies to fetch from server
                        if (commentsRemaining < 10 && replyCount != repliesAvailable){
                            if (!replyUpdateLoading){
                                getCommentRepliesExecutor getCommentRepliesExecutor =
                                        new getCommentRepliesExecutor(pageNumber, fullRecComment.getFullComment().getComment().getCommentId(), CommentReplyHandler.this);
                                getCommentRepliesExecutor.initExecutor();
                                replyUpdateLoading = true;
                            }
                        }

                        // add replies from already fetched list if available
                        if (fullRecComment.getFullComment().getCommentReplyList().size() != replyCount) {
                            int remaining = repliesAvailable - replyCount;
                            int batchSize = Math.min(5, remaining);
                            for (int i = replyCount; i < replyCount + batchSize; i++){
                                CommentReplyItem commentReplyItem = new CommentReplyItem(
                                        replyLay.getContext(),
                                        fullRecComment.getFullComment().getCommentReplyList().get(i),
                                        displayMetrics,
                                        parentActivity,
                                        commentDelegate,
                                        CommentReplyHandler.this::deletedReply);
                                replyLay.addView(commentReplyItem);
                                fullRecComment.setRepliesShown(fullRecComment.getRepliesShown() + 1);
                            }
                            replyCount = fullRecComment.getRepliesShown();
                            commentsRemaining = repliesAvailable - fullRecComment.getRepliesShown();
                            viewHolder.bottomView.setText("Get " + commentsRemaining + " replies");
                        } else {
                            // if no replies left locally, fetch more from server
                            if (!replyUpdateLoading){
                                getCommentRepliesExecutor getCommentRepliesExecutor =
                                        new getCommentRepliesExecutor(pageNumber, fullRecComment.getFullComment().getComment().getCommentId(), CommentReplyHandler.this);
                                getCommentRepliesExecutor.initExecutor();
                                replyUpdateLoading = true;
                            }
                        }
                    } else {
                        // if less than 10 replies, just show all remaining locally
                        for (int i = 0; i < commentsRemaining; i++){
                            CommentReplyItem commentReplyItem = new CommentReplyItem(
                                    replyLay.getContext(),
                                    fullRecComment.getFullComment().getCommentReplyList().get(i),
                                    displayMetrics,
                                    parentActivity,
                                    commentDelegate,
                                    CommentReplyHandler.this::deletedReply);
                            replyLay.addView(commentReplyItem);
                            viewHolder.bottomView.setVisibility(View.GONE);
                            fullRecComment.setRepliesShown(fullRecComment.getRepliesShown() + 1);
                            replyCount += 1;
                        }
                        commentsRemaining = repliesAvailable - fullRecComment.getRepliesShown();
                    }

                    // hide bottom view if no replies remaining
                    if (commentsRemaining == 0){
                        viewHolder.bottomView.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            // no replies exist, disable bottom view and hide reply layout
            viewHolder.bottomView.setOnClickListener(null);
            viewHolder.bottomView.setVisibility(View.VISIBLE);
            viewHolder.replyLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Callback invoked when a batch of comment replies has been fetched from the server
     * @param successful indicates if fetching the replies was successful
     * @param commentList the list of Comment objects retrieved
     */
    @Override
    public void complete(Boolean successful, ArrayList<Comment> commentList) {
        replyUpdateLoading = false;
        if (successful){
            for (int i =0; i < commentList.size(); i ++){
                fullRecComment.getFullComment().getCommentReplyList().add(commentList.get(i));
            }
            repliesAvailable = fullRecComment.getFullComment().getCommentReplyList().size();
            commentsRemaining = repliesAvailable - fullRecComment.getRepliesShown();
        }else {

        }
    }




    /**
     * Callback invoked when a reply comment is deleted
     * Removes the reply view from the reply layout
     * @param commentReplyItem the UI item representing the deleted reply
     */
    @Override
    public void deletedReply(CommentReplyItem commentReplyItem) {
        LinearLayout replyLay = viewHolder.replyLayout;
        if (commentReplyItem != null) {
            replyLay.removeView(commentReplyItem);
        }
    }
}
