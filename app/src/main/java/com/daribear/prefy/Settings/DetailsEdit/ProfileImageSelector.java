package com.daribear.prefy.Settings.DetailsEdit;

import static android.app.Activity.RESULT_OK;
import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daribear.prefy.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import lombok.Getter;

public class ProfileImageSelector {
    private final String UCROP_IMAGE_FILE_NAME = "UCROP_IMAGE_FILE";
    private ActivityResultLauncher<Intent> ImageResultLauncher;
    private ActivityResultLauncher<Intent> UcropResultLauncher;
    private ImageView imageView, saveButton;
    @Getter
    private Uri currentURI;


    public void registerForActivityResult(Activity activity, Fragment fragment){
        ImageResultLauncher = fragment.registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getData() != null){
                        initUCrop(activity, result.getData().getData());
                    }

                }
        });
        UcropResultLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getData() != null){
                            UcropActivityResult(activity, result.getResultCode(), result.getData());
                        }

                    }
                });
    }

    public void imageClicked(ImageView imageView, ImageView saveButton){
        this.imageView = imageView;
        this.saveButton = saveButton;
        GetImage();
    }



    private void GetImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ImageResultLauncher.launch(intent);
    }



    public void UcropActivityResult(Activity activity, int resultCode, @Nullable Intent data){
        if (resultCode == RESULT_OK) {
            final Uri resultUri = UCrop.getOutput(data);
            setImage(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    private void initUCrop(Activity activity ,Uri sourceUri){
        String destinationName = UCROP_IMAGE_FILE_NAME + ".png";
        UCrop.Options UcropOptions = new UCrop.Options();
        File file = new File(activity.getCacheDir(), destinationName);
        UcropOptions.setStatusBarColor(ContextCompat.getColor(activity, R.color.black));
        Intent intent = UCrop
                .of(sourceUri, Uri.fromFile(file))
                .withAspectRatio(8, 8)
                .withOptions(UcropOptions)
                .getIntent(activity);
        UcropResultLauncher.launch(intent);


    }

    private void setImage(Uri imageURI){
        Glide.with(imageView)
                .load(imageURI)
                .circleCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy((DiskCacheStrategy.NONE))
                .into(imageView);
        this.currentURI = imageURI;
        saveButton.setVisibility(View.VISIBLE);
    }




}
