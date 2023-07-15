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

public class CommentReplyHandler implements ReplyDelegate, CommentReplyDeletedDelegate {
    private FullRecComment fullRecComment;
    private CommentListAdaptor.CommentItemViewHolder viewHolder;
    private Activity parentActivity;
    private Integer position;
    private Integer pageNumber;
    private Boolean replyUpdateLoading;
    private Integer repliesAvailable, replyCount, commentsRemaining;
    private CommentReplyClicked commentDelegate;


    public CommentReplyHandler(FullRecComment fullRecComment, CommentListAdaptor.CommentItemViewHolder viewHolder, Activity parentActivity, Integer position, CommentReplyClicked commentDelegate) {
        this.fullRecComment = fullRecComment;
        this.viewHolder = viewHolder;
        this.parentActivity = parentActivity;
        this.position = position;
        this.commentDelegate = commentDelegate;
    }

    public void init(){
        pageNumber = 0;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        replyUpdateLoading = false;
        (parentActivity).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        repliesAvailable = fullRecComment.getFullComment().getReplyCount();
        replyCount = fullRecComment.getRepliesShown();
        commentsRemaining = repliesAvailable - fullRecComment.getRepliesShown();
        if (commentsRemaining > 0){
            viewHolder.bottomView.getLayoutParams().height = (int) (18 * ((float) parentActivity.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
            viewHolder.bottomView.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.fragment_background));
             if (repliesAvailable == 1){
                 viewHolder.bottomView.setText("Get 1 reply");
             } else {
                 viewHolder.bottomView.setText("Get " + repliesAvailable + " replies");
             }

        }
        else {
            viewHolder.bottomView.getLayoutParams().height = (int) (1 * ((float) parentActivity.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
            viewHolder.bottomView.setBackgroundColor(Color.parseColor("#F1F1F1"));
            viewHolder.bottomView.setText(null);

        }
        if (repliesAvailable != 0){
            LinearLayout replyLay = viewHolder.replyLayout;
            viewHolder.replyLayout.setVisibility(View.VISIBLE);
            if (fullRecComment.getRepliesShown() > 0){
                viewHolder.bottomView.setVisibility(View.GONE);
            }
            if (!fullRecComment.getMinimised()){
                for (int i = 0; i < fullRecComment.getRepliesShown(); i ++){
                    CommentReplyItem commentReplyItem = new CommentReplyItem(replyLay.getContext(), fullRecComment.getFullComment().getCommentReplyList().get(i), displayMetrics, parentActivity, commentDelegate, CommentReplyHandler.this::deletedReply);
                    replyLay.addView(commentReplyItem);
                }
            }

            viewHolder.bottomView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (repliesAvailable >= 10){
                        if (commentsRemaining < 10){
                            if (replyCount != repliesAvailable){
                                if (!replyUpdateLoading){
                                    getCommentRepliesExecutor getCommentRepliesExecutor = new getCommentRepliesExecutor(pageNumber, fullRecComment.getFullComment().getComment().getCommentId(), CommentReplyHandler.this);
                                    getCommentRepliesExecutor.initExecutor();
                                    replyUpdateLoading = true;
                                }
                            }
                        }
                        if (fullRecComment.getFullComment().getCommentReplyList().size() != replyCount) {
                            if ((repliesAvailable - replyCount) > 5) {
                                for (int i = replyCount; i < replyCount + 5; i++) {
                                    CommentReplyItem commentReplyItem = new CommentReplyItem(replyLay.getContext(), fullRecComment.getFullComment().getCommentReplyList().get(i), displayMetrics, parentActivity, commentDelegate, CommentReplyHandler.this::deletedReply);
                                    replyLay.addView(commentReplyItem);
                                    fullRecComment.setRepliesShown(fullRecComment.getRepliesShown() + 1);
                                }
                                replyCount = fullRecComment.getRepliesShown();
                            } else {
                                if (repliesAvailable - replyCount > 0) {
                                    for (int i = replyCount; i < replyCount + (repliesAvailable - replyCount); i++) {
                                        CommentReplyItem commentReplyItem = new CommentReplyItem(replyLay.getContext(), fullRecComment.getFullComment().getCommentReplyList().get(i), displayMetrics, parentActivity, commentDelegate, CommentReplyHandler.this::deletedReply);
                                        replyLay.addView(commentReplyItem);
                                        fullRecComment.setRepliesShown(fullRecComment.getRepliesShown() + 1);
                                    }
                                    replyCount = fullRecComment.getRepliesShown();
                                }
                            }
                            commentsRemaining = repliesAvailable - fullRecComment.getRepliesShown();
                            viewHolder.bottomView.setText("Get " + (repliesAvailable - replyCount) + " replies");
                        } else {
                            if (!replyUpdateLoading){
                                getCommentRepliesExecutor getCommentRepliesExecutor = new getCommentRepliesExecutor(pageNumber, fullRecComment.getFullComment().getComment().getCommentId(), CommentReplyHandler.this);
                                getCommentRepliesExecutor.initExecutor();
                                replyUpdateLoading = true;
                            }
                        }

                    }else {
                        for (int i =0; i < (commentsRemaining); i ++){
                            CommentReplyItem commentReplyItem = new CommentReplyItem(replyLay.getContext(), fullRecComment.getFullComment().getCommentReplyList().get(i), displayMetrics, parentActivity, commentDelegate, CommentReplyHandler.this::deletedReply);
                            replyLay.addView(commentReplyItem);
                            viewHolder.bottomView.setVisibility(View.GONE);
                            fullRecComment.setRepliesShown(fullRecComment.getRepliesShown() + 1);
                            replyCount += 1;
                        }
                        commentsRemaining = repliesAvailable - fullRecComment.getRepliesShown();
                    }
                    if ((repliesAvailable - replyCount) == 0){
                        viewHolder.bottomView.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            viewHolder.bottomView.setOnClickListener(null);
            viewHolder.bottomView.setVisibility(View.VISIBLE);
            viewHolder.replyLayout.setVisibility(View.GONE);
        }
    }


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





    @Override
    public void deletedReply(CommentReplyItem commentReplyItem) {

        LinearLayout replyLay = viewHolder.replyLayout;
        if (commentReplyItem != null) {
            replyLay.removeView(commentReplyItem);
        }
    }
}
