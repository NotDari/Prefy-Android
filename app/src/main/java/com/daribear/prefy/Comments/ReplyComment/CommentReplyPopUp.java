package com.daribear.prefy.Comments.ReplyComment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;

import com.daribear.prefy.Comments.Comment;
import com.daribear.prefy.Comments.CommentDeleted;
import com.daribear.prefy.Comments.CommentReplyClicked;
import com.daribear.prefy.DeleteDialog.DeleteDialog;
import com.daribear.prefy.DeleteDialog.DeleteDialogDelegate;
import com.daribear.prefy.Network.UploadController.UploadController;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.SharedPreferences.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class CommentReplyPopUp implements DeleteDialogDelegate {
    private Comment comment;
    private BottomSheetDialog commentDialog;
    private Boolean changeCoordinates = false;
    private Integer changeCoordinatesX, changeCoordinatesY;
    private Activity ownerActivity;
    private CommentReplyClicked commentReplyDelegate;
    private CommentDeleted commentDeletedDelegate;



    public CommentReplyPopUp(Comment comment, Activity ownerActivity,CommentReplyClicked commentReplyDelegate, CommentDeleted commentDeletedDelegate) {
        this.comment = comment;
        this.ownerActivity = ownerActivity;
        this.commentReplyDelegate = commentReplyDelegate;
        this.commentDeletedDelegate = commentDeletedDelegate;
    }



    private void setUpViews(Dialog postDialog){
        ConstraintLayout replyLayout = postDialog.findViewById(R.id.CommentDialogReplyLayout);
        ConstraintLayout profileLayout = postDialog.findViewById(R.id.CommentDialogProfileLayout);
        ConstraintLayout reportLayout = postDialog.findViewById(R.id.CommentDialogReportLayout);
        ConstraintLayout deleteLayout = postDialog.findViewById(R.id.CommentDialogDeleteLayout);
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
                commentReplyDelegate.subReplyClicked(comment.getUser().getUsername(), comment.getReplyID(), comment.getCommentId());
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
                if (comment != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("Type", "Comment");
                    bundle.putParcelable("comment", comment);
                    Navigation.findNavController(ownerActivity, R.id.FragmentContainerView).navigate(R.id.action_global_reportFragment, bundle);
                    postDialog.dismiss();
                } else {
                    Toast.makeText(ownerActivity, "An error has occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDialog.dismiss();
                DeleteDialog.getInstance(ownerActivity, CommentReplyPopUp.this::deleteClicked, "Comment").show();
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

    @Override
    public void deleteClicked() {
        UploadController.saveDelete(commentDialog.getContext().getApplicationContext(), "Comment", comment.getCommentId());
        commentDeletedDelegate.deleteClicked(comment.getCommentId());
    }
}
