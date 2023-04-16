package com.daribear.prefy.Comments.ReplyComment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.daribear.prefy.Comments.Comment;
import com.daribear.prefy.Comments.CommentReplyClicked;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class CommentReplyPopUp {
    private Comment comment;
    private BottomSheetDialog commentDialog;
    private Boolean changeCoordinates = false;
    private Integer changeCoordinatesX, changeCoordinatesY;
    private Activity ownerActivity;
    private CommentReplyClicked commentDelegate;


    public CommentReplyPopUp(Comment comment, Activity ownerActivity,CommentReplyClicked commentDelegate ) {
        this.comment = comment;
        this.ownerActivity = ownerActivity;
        this.commentDelegate = commentDelegate;
    }



    private void setUpViews(Dialog postDialog){
        RelativeLayout replyLayout = postDialog.findViewById(R.id.CommentDialogReplyLayout);
        RelativeLayout profileLayout = postDialog.findViewById(R.id.CommentDialogProfileLayout);
        RelativeLayout reportLayout = postDialog.findViewById(R.id.CommentDialogReportLayout);
        RelativeLayout deleteLayout = postDialog.findViewById(R.id.CommentDialogDeleteLayout);
        Utils utils = new Utils(ownerActivity);
        View deleteView = postDialog.findViewById(R.id.CommentDialogDeleteView);
        if (comment.getUser().getId().equals(utils.loadLong(ownerActivity.getString(R.string.save_user_id), 0))){
            deleteLayout.setVisibility(View.VISIBLE);
            deleteView.setVisibility(View.VISIBLE);
        } else {
            deleteLayout.setVisibility(View.GONE);
            deleteView.setVisibility(View.GONE);
        }
        replyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ownerActivity, "Reply" + comment.getReplyID() + " " + comment.getCommentId(), Toast.LENGTH_SHORT).show();
                commentDelegate.subReplyClicked(comment.getUser().getUsername(), comment.getReplyID(), comment.getCommentId());
                postDialog.dismiss();
            }
        });
        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putLong("id", comment.getUser().getId());
                bundle.putParcelable("user", comment.getUser());
                Navigation.findNavController(ownerActivity, R.id.FragmentContainerView).navigate(R.id.action_global_userProfile, bundle);
                postDialog.dismiss();
            }
        });
        reportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                if (fullComment != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("Type", "Comment");
                    bundle.putParcelable("comment", fullComment.getComment());
                    Navigation.findNavController(ownerActivity, R.id.FragmentContainerView).navigate(R.id.action_global_reportFragment, bundle);
                    postDialog.dismiss();
                } else {
                    Toast.makeText(ownerActivity, "An error has occurred", Toast.LENGTH_SHORT).show();
                }
                 */
            }
        });
        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Fix deleteLAY
                /**
                 FirebaseFirestore ff = FirebaseFirestore.getInstance();
                 WriteBatch batch = ff.batch();
                 DocumentReference commentRef = ff.collection("Comments").document(fullComment.getComment().getKey());
                 batch.delete(commentRef);
                 DocumentReference postRef = ff.collection("Posts").document(fullComment.getComment().getPostId());
                 batch.update(postRef, "commentsNumber", FieldValue.increment(-1));
                 batch.commit();
                 postDialog.dismiss();
                 commentDeleteDelegate.deleteClicked(fullComment);
                 */
            }
        });
    }

    public void initDialog(){
        commentDialog = new BottomSheetDialog(ownerActivity, R.style.BottomSheetDialog);
        commentDialog.setContentView(R.layout.comment_pop_up_dialog);
        commentDialog.setCancelable(true);
        commentDialog.setCanceledOnTouchOutside(true);
        setUpViews(commentDialog);

        // Setting the width of a dialog as a percentage of the screen
        //int screenwidth = (int) (ownerActivity.getResources().getDisplayMetrics().widthPixels * 0.43);
        //commentDialog.getWindow().setLayout(screenwidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        //nitLocation(dialogWindow, commentDialog);
        //dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        commentDialog.show();

    }

}
