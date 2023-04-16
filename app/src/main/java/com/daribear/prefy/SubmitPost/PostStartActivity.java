package com.daribear.prefy.SubmitPost;

import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.daribear.prefy.Activities.MainActivity;

public class PostStartActivity {
    private ActivityResultLauncher<Intent> imageResultLauncher;
    private ActivityResultLauncher<Intent> uCropResultLauncher;

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

    public void launchImageRetreiver(Intent intent){
        imageResultLauncher.launch(intent);
    }
    public void launchUcropRetreiver(Intent intent){
        uCropResultLauncher.launch(intent);
    }


}
