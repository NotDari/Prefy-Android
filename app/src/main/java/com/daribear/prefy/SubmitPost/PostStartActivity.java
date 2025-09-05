package com.daribear.prefy.SubmitPost;

import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.daribear.prefy.Activities.MainActivity;

/**
 * Handles launching the activity for the submission of a post (image selection and cropping).
 *
 */
public class PostStartActivity {
    private ActivityResultLauncher<Intent> imageResultLauncher;
    private ActivityResultLauncher<Intent> uCropResultLauncher;

    /**
     * register ActivityResultLaunchers to handle activity results for image cropping/selection
     * @param activity activity used to register the launchers
     */
    public void registerForActivityResult(MainActivity activity){
        imageResultLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getData() != null){
                            if (SubmitPostDialog.getInstance(activity) != null){
                                SubmitPostDialog.getInstance(activity).OnImageActivityResult(activity,result.getResultCode(), result.getData());
                            }
                        }

                    }
                });
        uCropResultLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getData() != null){
                            if (SubmitPostDialog.getInstance(activity) != null){
                                SubmitPostDialog.getInstance(activity).OnUcropActivityResult(activity,result.getResultCode(), result.getData());
                            }
                        }

                    }
                });
    }

    /**
     * Launches the image selection activity.
     * @param intent to launch
     */
    public void launchImageRetreiver(Intent intent){
        imageResultLauncher.launch(intent);
    }

    /**
     * Launches the cropping activity.
     * @param intent intent to launch
     */
    public void launchUcropRetreiver(Intent intent){
        uCropResultLauncher.launch(intent);
    }


}
