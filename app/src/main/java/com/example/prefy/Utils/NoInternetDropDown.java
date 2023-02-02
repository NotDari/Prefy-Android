package com.example.prefy.Utils;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.prefy.R;



public class NoInternetDropDown {
    private Activity activity;
    private static NoInternetDropDown instance;
    private Boolean viewActive;
    private Boolean viewClicked;

    public NoInternetDropDown(Activity activity) {
        this.activity = activity;
        viewActive = false;
    }

    public static NoInternetDropDown getInstance(Activity activity){
        if (instance == null){
            instance = new NoInternetDropDown(activity);
        }
        return instance;
    }

    public void showDropDown() {
        TextView view = activity.findViewById(R.id.ActivityNoInternetText);
        if (!viewActive) {
            view.setVisibility(View.VISIBLE);
            viewActive = true;
            viewClicked = false;
            expand(view);
            Handler handler = new Handler();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewClicked = true;
                    collapse(view);

                }
            });
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!viewClicked) {
                        collapse(view);
                    }
                }
            }, 1500); //the time you want to delay in milliseconds
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
