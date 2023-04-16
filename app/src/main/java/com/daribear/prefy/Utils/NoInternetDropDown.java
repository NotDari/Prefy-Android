package com.daribear.prefy.Utils;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.daribear.prefy.R;
import com.google.android.material.snackbar.Snackbar;


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

    public void showDropDown() {
        if (!snackbar.isShown()){
            snackbar.show();
        }
    }





    private void expand(View v) {
        v.setVisibility(View.VISIBLE);
        Animation animSlidedown = AnimationUtils.loadAnimation(v.getContext(),R.anim.internet_slide_down);
        v.startAnimation(animSlidedown);
    }

    private void collapse(final View v) {
        Animation animSlidedown = AnimationUtils.loadAnimation(v.getContext(),R.anim.internet_slide_up);
        v.startAnimation(animSlidedown);
        animSlidedown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
                initCooldown();
                animSlidedown.setAnimationListener(null);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void initCooldown(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewActive = false;
            }
        }, 30000); //
    }




}
