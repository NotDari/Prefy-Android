package com.daribear.prefy.Utils;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.daribear.prefy.R;
import com.google.android.material.snackbar.Snackbar;

/**
 * A dropdown that shows when there is no internet.
 * Is a singleton to stop multiple being shown at once
 */
public class NoInternetDropDown {
    private Activity activity;
    private static NoInternetDropDown instance;
    private Boolean viewActive;
    private Boolean viewClicked;
    private Snackbar snackbar;

    public NoInternetDropDown(Activity activity) {
        this.activity = activity;
        viewActive = false;
        snackbar = Snackbar.make(activity.findViewById(R.id.FragmentContainerView), "No internet", Snackbar.LENGTH_SHORT);
    }

    public static NoInternetDropDown getInstance(Activity activity){
        if (instance == null){
            instance = new NoInternetDropDown(activity);
        }
        return instance;
    }

    /**
     * Show the dropdown
     */
    public void showDropDown() {
        if (!snackbar.isShown()){
            snackbar.show();
        }
    }












}
