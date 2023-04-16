package com.daribear.prefy.Utils.Permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import com.daribear.prefy.Activities.MainActivity;

public class PermissionChecker {
    private MainActivity activity;

    private ActivityResultLauncher<String> requestPermissionLauncher;

    private PermissionReceived delegate;

    public PermissionChecker(MainActivity activity) {
        this.activity = activity;
    }

    public void initLauncher(){
       requestPermissionLauncher = activity.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (delegate != null){
                delegate.granted(isGranted);
            }
        });
    }

    public void checkPermission(PermissionReceived tempDelegate, String permission, Context context){
        this.delegate = tempDelegate;
        if (ContextCompat.checkSelfPermission(
                context, permission) ==
                PackageManager.PERMISSION_GRANTED) {
            delegate.granted(true);
        } else {
            requestPermissionLauncher.launch(
                    permission);
        }
    }

}
