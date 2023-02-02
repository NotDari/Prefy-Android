package com.example.prefy.Comments.ReplyComment;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prefy.Comments.CommentListAdaptor;
import com.example.prefy.Comments.FullRecComment;
import com.example.prefy.R;
import com.google.android.material.button.MaterialButton;

public class CommentReplyHandler {
    private FullRecComment fullRecComment;
    private CommentListAdaptor.CommentItemViewHolder viewHolder;
    private Activity parentActivity;
    private Integer position;


    public CommentReplyHandler(FullRecComment fullRecComment, CommentListAdaptor.CommentItemViewHolder viewHolder, Activity parentActivity, Integer position) {
        this.fullRecComment = fullRecComment;
        this.viewHolder = viewHolder;
        this.parentActivity = parentActivity;
        this.position = position;
    }

    public void init(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        (parentActivity).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        Integer replyCount = fullRecComment.getFullComment().getCommentReplyList().size();
        if (replyCount - fullRecComment.getRepliesShown() > 0){
            viewHolder.bottomView.getLayoutParams().height = (int) (18 * ((float) parentActivity.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
             viewHolder.bottomView.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.fragment_background));
             if (replyCount == 1){
                 viewHolder.bottomView.setText("Get 1 reply");
             } else {
                 viewHolder.bottomView.setText("Get " + replyCount + " replies");
             }
            LinearLayout replyLay = viewHolder.replyLayout;
            viewHolder.bottomView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     System.out.println("Sdad position: " + position + " text:" + fullRecComment.getFullComment().getComment().getText());
                     if (replyCount - fullRecComment.getRepliesShown() >= 5){

                     } else {
                         for (int i =0; i < replyCount; i ++){
                             CommentReplyItem commentReplyItem = new CommentReplyItem(replyLay.getContext(), fullRecComment.getFullComment().getCommentReplyList().get(i), displayMetrics);
                             replyLay.addView(commentReplyItem);

                             viewHolder.bottomView.setVisibility(View.GONE);
                             fullRecComment.setRepliesShown(fullRecComment.getRepliesShown() + 1);
                         }
                     }
                 }
             });

        }
    }




}
