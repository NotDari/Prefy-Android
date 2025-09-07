package com.daribear.prefy.Comments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;

import com.daribear.prefy.DeleteDialog.DeleteDialog;
import com.daribear.prefy.DeleteDialog.DeleteDialogDelegate;
import com.daribear.prefy.Network.UploadController.UploadController;
import com.daribear.prefy.R;
import com.daribear.prefy.Utils.SharedPreferences.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

/**
 * Popup dialog for handling additional comment actions
 * such as reply, view profile, report, or delete
 */
public class CommentMorePopUpDialog implements DeleteDialogDelegate {
    private FullComment fullComment;
    private BottomSheetDialog commentDialog;
    private Boolean changeCoordinates = false;
    private Integer changeCoordinatesX, changeCoordinatesY;
    private Activity ownerActivity;
    private CommentReplyClicked commentDelegate;
    private CommentDeleted commentDeleteDelegate;

    public CommentMorePopUpDialog(FullComment fullComment, Activity ownerActivity, CommentReplyClicked commentDelegate, CommentDeleted commentDeleteDelegate) {
        this.fullComment = fullComment;
        this.ownerActivity = ownerActivity;
        this.commentDelegate = commentDelegate;
        this.commentDeleteDelegate = commentDeleteDelegate;
    }

    /**
     * Set coordinates for custom positioning of the popup
     * @param x x offset
     * @param y y offset
     */
    public void setCoordinates(Integer x, Integer y){
        changeCoordinates = true ;
        this.changeCoordinatesX = x;
        this.changeCoordinatesY = y;
    }

    /**
     * Initialise and attach click listeners to all views in dialog
     * @param postDialog the dialog to initialise
     */
    private void setUpViews(Dialog postDialog){
        ConstraintLayout replyLayout = postDialog.findViewById(R.id.CommentDialogReplyLayout);
        ConstraintLayout profileLayout = postDialog.findViewById(R.id.CommentDialogProfileLayout);
        ConstraintLayout reportLayout = postDialog.findViewById(R.id.CommentDialogReportLayout);
        ConstraintLayout deleteLayout = postDialog.findViewById(R.id.CommentDialogDeleteLayout);
        Utils utils = new Utils(ownerActivity);
        View deleteView = postDialog.findViewById(R.id.CommentDialogDeleteView);
        // Show delete option only if current user is the comment author
        if (fullComment.getComment().getUser().getId().equals(utils.loadLong(ownerActivity.getString(R.string.save_user_id), 0))){
            deleteLayout.setVisibility(View.VISIBLE);
            deleteView.setVisibility(View.VISIBLE);
        } else {
            deleteLayout.setVisibility(View.GONE);
            deleteView.setVisibility(View.GONE);
        }
        //Reply click listener
        replyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentDelegate.mainReplyClicked(fullComment.getComment().getUser().getUsername(), fullComment.getComment().getCommentId());
                postDialog.dismiss();
            }
        });
        //Profile click listener

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putLong("id", fullComment.getComment().getUser().getId());
                bundle.putParcelable("user", fullComment.getComment().getUser());
                Navigation.findNavController(ownerActivity, R.id.FragmentContainerView).navigate(R.id.action_global_userProfile, bundle);
                postDialog.dismiss();
            }
        });
        //Report click listener
        reportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fullComment != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("Type", "Comment");
                    bundle.putParcelable("comment", fullComment.getComment());
                    Navigation.findNavController(ownerActivity, R.id.FragmentContainerView).navigate(R.id.action_global_reportFragment, bundle);
                    postDialog.dismiss();
                } else {
                    Toast.makeText(ownerActivity, "An error has occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Delete click listener
        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDialog.dismiss();
                DeleteDialog.getInstance(ownerActivity, CommentMorePopUpDialog.this::deleteClicked, "Comment").show();
            }
        });

    }

    /**
     * Initialise and show the popup dialog
     */
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


    /**
     * Called when delete is clicked in popup
     */
    @Override
    public void deleteClicked() {
        UploadController.saveDelete(commentDialog.getContext().getApplicationContext(), "Comment", fullComment.getComment().getCommentId());
        commentDeleteDelegate.deleteClicked(fullComment.getComment().getCommentId());
    }
}
