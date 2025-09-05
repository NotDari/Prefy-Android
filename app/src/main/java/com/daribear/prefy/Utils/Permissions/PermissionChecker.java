package com.daribear.prefy.Utils.Permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import com.daribear.prefy.Activities.MainActivity;

/**
 * This class is used to check if a permission has been granted by the user.
 * If it hasn't, then it launches a request for the permission.
 */
public class PermissionChecker {
    private MainActivity activity;

    private ActivityResultLauncher<String> requestPermissionLauncher;

    private PermissionReceived delegate;

    public PermissionChecker(MainActivity activity) {
        this.activity = activity;
    }

    /**
     * Initiates the ActivityResultLauncher for getting permissions.
     */
    public void initLauncher(){
       requestPermissionLauncher = activity.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (delegate != null){
                delegate.granted(isGranted);
            }
        });
    }

    /**
     * Checks if a permission has been granted. If it hasn't launches the request to get the permission.
     * @param tempDelegate delegate to notify the result of the check
     * @param permission the permission to check/request
     * @param context the context used
     */
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
