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
import com.daribear.prefy.Popular.NewPopularSystem.PopularSkipDelegate;
import com.daribear.prefy.Profile.User;
import com.daribear.prefy.Utils.Permissions.PermissionReceived;
import com.daribear.prefy.customClasses.FullPost;
import com.daribear.prefy.customClasses.StandardPost;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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

    public PostDropDownDialog(Context context, Boolean loggedUserPost, Activity ownerActivity, FullPost fullPost, PostDropDownDialogDelegate exploreDelegate, DeleteDelegate deleteDelegate) {
        this.context = context;
        this.loggedUserPost = loggedUserPost;
        this.ownerActivity = ownerActivity;
        this.user = fullPost.getUser();
        this.post = fullPost.getStandardPost();
        this.exploreDelegate = exploreDelegate;
        this.delegateDelegate = deleteDelegate;
        popular = false;
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
        /**
        postDialog = new Dialog(context, android.R.style.Theme_DeviceDefault_NoActionBar);
        postDialog.setContentView(R.layout.post_pop_up_dialog);
        postDialog.setCancelable(true);
        postDialog.setCanceledOnTouchOutside(true);
        setUpViews(postDialog);
        Window dialogWindow = postDialog.getWindow();

        // Setting the width of a dialog as a percentage of the screen
        int screenwidth = (int) (ownerActivity.getResources().getDisplayMetrics().widthPixels * 0.43);
        int screenheight = (int) (ownerActivity.getResources().getDisplayMetrics().heightPixels);
        postDialog.getWindow().setLayout(screenwidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        initLocation(screenheight,dialogWindow, postDialog);
        dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        postDialog.show();
         */

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bottomSheetDialog.setContentView(R.layout.post_pop_up_dialog);
        this.postDialog = bottomSheetDialog;
        setUpViews();
        bottomSheetDialog.show();

    }

    private void initLocation(Integer screenheight, Window dialogWindow, Dialog postDialog){
        if (changeCoordinates){
            LinearLayout PostDiaFullLay = postDialog.findViewById(R.id.PostDiaFullLay);
            WindowManager.LayoutParams wlp = dialogWindow.getAttributes();
            wlp.gravity = Gravity.BOTTOM | Gravity.RIGHT;

            PostDiaFullLay.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
            {
                @Override
                public boolean onPreDraw()
                {
                    if (PostDiaFullLay.getViewTreeObserver().isAlive())
                        PostDiaFullLay.getViewTreeObserver().removeOnPreDrawListener(this);



                    Integer PostDiaFullLayHeight = PostDiaFullLay.getMeasuredHeight();
                    wlp.x = changeCoordinatesX;
                    System.out.println("Sdad changeCoordinatesY" + changeCoordinatesY + " " + PostDiaFullLayHeight);
                    wlp.y =  screenheight - changeCoordinatesY - (PostDiaFullLayHeight / 2);
                    dialogWindow.setAttributes(wlp);
                    return true;
                }
            });


        }


    }


    private void setUpViews(){
        ConstraintLayout saveLayout = postDialog.findViewById(R.id.PostDialogSaveLayout);
        ConstraintLayout profileLayout = postDialog.findViewById(R.id.PostDialogProfileLayout);
        ConstraintLayout reportLayout = postDialog.findViewById(R.id.PostDialogReportLayout);
        ConstraintLayout deleteLayout = postDialog.findViewById(R.id.PostDialogDeleteLayout);
        ConstraintLayout skipLayout = postDialog.findViewById(R.id.PostDialogSkipLayout);

        View skipView = postDialog.findViewById(R.id.PostDialogSkipView);
        if (popular){
            skipLayout.setVisibility(View.VISIBLE);
            skipView.setVisibility(View.VISIBLE);
        } else {
            skipLayout.setVisibility(View.GONE);
            skipView.setVisibility(View.GONE);
        }
        View deleteView = postDialog.findViewById(R.id.PostDialogDeleteView);
        if (loggedUserPost){
            deleteLayout.setVisibility(View.VISIBLE);
            deleteView.setVisibility(View.VISIBLE);
        } else {
            deleteLayout.setVisibility(View.GONE);
            deleteView.setVisibility(View.GONE);
        }
        saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageDrawable != null){
                    imageBit = addWaterMark(imageDrawable);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                        saveImage();
                    } else{
                        ((MainActivity)ownerActivity).requestPermission(PostDropDownDialog.this::granted, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                } else {
                    Toast.makeText(saveLayout.getContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
                    postDialog.dismiss();
                }
            }
        });
        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putLong("id", user.getId());
                bundle.putParcelable("user", user);
                Navigation.findNavController(ownerActivity, R.id.FragmentContainerView).navigate(R.id.action_global_userProfile, bundle);
                postDialog.dismiss();
                changeVisibility();
            }
        });
        reportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("Type", "Post");
                    bundle.putParcelable("post", post);
                    Navigation.findNavController(ownerActivity, R.id.FragmentContainerView).navigate(R.id.action_global_reportFragment, bundle);
                    postDialog.dismiss();
                    changeVisibility();
                } else {
                    Toast.makeText(ownerActivity, "An error has occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDialog.dismiss();
                DeleteDialog deleteDialog = DeleteDialog.getInstance(context, PostDropDownDialog.this::deleteClicked, "Post");
                deleteDialog.show();
            }
        });

        skipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDialog.dismiss();
                if (popular){
                    popSkipDelegate.skipClicked();
                }
            }
        });
    }



    private void saveImage(){
        Double currentDate = (double) System.currentTimeMillis();
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

    @Override
    public void deleteClicked() {
        UploadController.saveDelete(context.getApplicationContext(), "Post" , post.getPostId());
        if (delegateDelegate != null){
            delegateDelegate.itemDeleted();
        }
        changeVisibility();
    }

    @Override
    public void granted(Boolean Granted) {
        if (Granted){
            saveImage();
        }
    }
}
