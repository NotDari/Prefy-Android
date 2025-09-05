package com.daribear.prefy;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;

import com.daribear.prefy.Activities.MainActivity;
import com.daribear.prefy.DeleteDialog.DeleteDelegate;
import com.daribear.prefy.DeleteDialog.DeleteDialog;
import com.daribear.prefy.DeleteDialog.DeleteDialogDelegate;
import com.daribear.prefy.Network.UploadController.UploadController;
import com.daribear.prefy.Popular.PopularSkipDelegate;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.Utils.CurrentTime;
import com.daribear.prefy.Utils.Permissions.PermissionReceived;
import com.daribear.prefy.customClasses.Posts.FullPost;
import com.daribear.prefy.customClasses.Posts.StandardPost;
import com.google.android.material.bottomsheet.BottomSheetDialog;

/**
 * The drop down dialog for an individual post.
 * Acts as an options where the user can save the post, report etc.
 */
public class PostDropDownDialog implements DeleteDialogDelegate , PermissionReceived {
    private Dialog postDialog;
    private Context context;
    private Boolean loggedUserPost;
    private Boolean changeCoordinates = false;
    private Integer changeCoordinatesX, changeCoordinatesY;
    private Activity ownerActivity;
    private User user;
    private StandardPost post;
    private Drawable imageDrawable;
    private Bitmap imageBit;

    private PostDropDownDialogDelegate exploreDelegate;

    private DeleteDelegate delegateDelegate;

    private Boolean popular;
    private PopularSkipDelegate popSkipDelegate;
    private BottomSheetDialog bottomSheetDialog;

    public PostDropDownDialog(Context context, Activity ownerActivity, PostDropDownDialogDelegate exploreDelegate, DeleteDelegate deleteDelegate) {
        this.context = context;
        this.ownerActivity = ownerActivity;
        this.exploreDelegate = exploreDelegate;
        this.delegateDelegate = deleteDelegate;
        popular = false;
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bottomSheetDialog.setContentView(R.layout.post_pop_up_dialog);
    }

    public void setDetails(Boolean loggedUserPost, FullPost fullPost){
        this.loggedUserPost = loggedUserPost;
        this.user = fullPost.getUser();
        this.post = fullPost.getStandardPost();
    }

    public void setPopular(PopularSkipDelegate popSkipDelegate){
        popular = true;
        this.popSkipDelegate = popSkipDelegate;
    }


    public void setImageDrawable(Drawable imageDrawable) {
        this.imageDrawable = imageDrawable;
    }

    public void setCoordinates(Integer x, Integer y){
        changeCoordinates = true ;
        this.changeCoordinatesX = x;
        this.changeCoordinatesY = y;
    }

    public void initDialog(){

        if (!bottomSheetDialog.isShowing()) {
            this.postDialog = bottomSheetDialog;
            setUpViews();
            bottomSheetDialog.show();
        }

    }


    /**
     * Set up all the views for this dialog by calling the sub functions
     */
    private void setUpViews() {
        ConstraintLayout saveLayout = postDialog.findViewById(R.id.PostDialogSaveLayout);
        ConstraintLayout profileLayout = postDialog.findViewById(R.id.PostDialogProfileLayout);
        ConstraintLayout reportLayout = postDialog.findViewById(R.id.PostDialogReportLayout);
        ConstraintLayout deleteLayout = postDialog.findViewById(R.id.PostDialogDeleteLayout);
        ConstraintLayout skipLayout = postDialog.findViewById(R.id.PostDialogSkipLayout);

        handleSkipLayout(skipLayout);
        handleDeleteLayout(deleteLayout);
        initSaveLayout(saveLayout);
        initProfileLayout(profileLayout);
        initReportLayout(reportLayout);
    }

    /**
     * Handles visibility and click for the skip button if the post is popular
     */
    private void handleSkipLayout(ConstraintLayout skipLayout) {
        View skipView = postDialog.findViewById(R.id.PostDialogSkipView);
        if (popular) {
            skipLayout.setVisibility(View.VISIBLE);
            skipView.setVisibility(View.VISIBLE);
            skipLayout.setOnClickListener(view -> {
                postDialog.dismiss();
                if (popular && popSkipDelegate != null) popSkipDelegate.skipClicked();
            });
        } else {
            skipLayout.setVisibility(View.GONE);
            skipView.setVisibility(View.GONE);
        }
    }

    /**
     * Handles visibility and click for the delete button if it's the user's post
     */
    private void handleDeleteLayout(ConstraintLayout deleteLayout) {
        View deleteView = postDialog.findViewById(R.id.PostDialogDeleteView);
        if (loggedUserPost) {
            deleteLayout.setVisibility(View.VISIBLE);
            deleteView.setVisibility(View.VISIBLE);
            deleteLayout.setOnClickListener(view -> {
                postDialog.dismiss();
                DeleteDialog deleteDialog = DeleteDialog.getInstance(context, this::deleteClicked, "Post");
                deleteDialog.show();
            });
        } else {
            deleteLayout.setVisibility(View.GONE);
            deleteView.setVisibility(View.GONE);
        }
    }

    /**
     * Initializes the save button with its click listener for saving images
     */
    private void initSaveLayout(ConstraintLayout saveLayout) {
        saveLayout.setOnClickListener(view -> {
            if (imageDrawable != null) {
                imageBit = addWaterMark(imageDrawable);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveImage();
                } else {
                    ((MainActivity) ownerActivity).requestPermission(PostDropDownDialog.this::granted,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            } else {
                Toast.makeText(saveLayout.getContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
                postDialog.dismiss();
            }
        });
    }

    /**
     * Initializes the profile button to navigate to the user's profile
     */
    private void initProfileLayout(ConstraintLayout profileLayout) {
        profileLayout.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putLong("id", user.getId());
            bundle.putParcelable("user", user);
            Navigation.findNavController(ownerActivity, R.id.FragmentContainerView)
                    .navigate(R.id.action_global_userProfile, bundle);
            postDialog.dismiss();
            changeVisibility();
        });
    }

    /**
     * Initializes the report button to open the report fragment for the post
     */
    private void initReportLayout(ConstraintLayout reportLayout) {
        reportLayout.setOnClickListener(view -> {
            if (post != null) {
                Bundle bundle = new Bundle();
                bundle.putString("Type", "Post");
                bundle.putParcelable("post", post);
                Navigation.findNavController(ownerActivity, R.id.FragmentContainerView)
                        .navigate(R.id.action_global_reportFragment, bundle);
                postDialog.dismiss();
                changeVisibility();
            } else {
                Toast.makeText(ownerActivity, "An error has occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Saves the image to the device.
     * Calls addWaterMark to add the prefy watermark to the image.
     */
    private void saveImage(){
        Double currentDate = (double) CurrentTime.getCurrentTime();
        MediaStore.Images.Media.insertImage(context.getContentResolver(), imageBit, ("Prefy-" + currentDate), "test");
        Toast.makeText(ownerActivity, "Image Saved", Toast.LENGTH_SHORT).show();
        postDialog.dismiss();
        changeVisibility();
    }

    private void changeVisibility(){
        if (this.exploreDelegate != null){
            exploreDelegate.replyClicked();
        }
    }

    /**
     * Add the prefy watermark to the saved image
     * @param image image to add the watermark to
     * @return BITMAP of the image with the watermark
     */
    private Bitmap addWaterMark(Drawable image){
        Bitmap waterMark = BitmapFactory.decodeResource(context.getResources(), R.drawable.prefy_water_mark);
        Bitmap imageBit = ((BitmapDrawable)image).getBitmap();
        Bitmap combinedBitmap;
        int width = imageBit.getWidth();
        int newHeight = (int)(waterMark.getHeight() * (((float)imageBit.getWidth()) / waterMark.getWidth()));
        waterMark = Bitmap.createScaledBitmap(waterMark, imageBit.getWidth(), newHeight, true);
        int height = waterMark.getHeight() + imageBit.getHeight();
        combinedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(combinedBitmap);

        comboImage.drawBitmap(imageBit, 0f, 0f, null);
        comboImage.drawBitmap(waterMark, 0f, imageBit.getHeight(), null);
        return combinedBitmap;
    }

    /**
     * The delete post is clicked.
     */
    @Override
    public void deleteClicked() {
        UploadController.saveDelete(context.getApplicationContext(), "Post" , post.getPostId());
        if (delegateDelegate != null){
            delegateDelegate.itemDeleted();
        }
        changeVisibility();
    }

    /**
     * When the permission to access storage is granted
     * @param Granted whether the permission is granted
     */
    @Override
    public void granted(Boolean Granted) {
        if (Granted){
            saveImage();
        }
    }
}
