package com.example.prefy.Popular;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prefy.Activities.MainActivity;
import com.example.prefy.R;
import com.example.prefy.customClasses.StandardPost;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collection;

public class ViewPagerNewItemsHandler {
    private ArrayList<StandardPost>oldPopularPostList;
    private PopularPostSet newPopularPostSet;
    private ViewPager2 viewPager;
    private PopularPageHostFragment.PopularPagerAdaptor viewPagerAdaptor;
    private Boolean viewDestroyed;
    private Integer viewPagerPosition = 0;
    private ViewPager2.OnPageChangeCallback callback;
    private Activity activity;

    public ViewPagerNewItemsHandler(PopularPostSet updatedPopularPostList, ArrayList<StandardPost> oldPopularPostList, ViewPager2 viewPager, PopularPageHostFragment.PopularPagerAdaptor viewPagerAdaptor, Activity activity) {
        this.newPopularPostSet = updatedPopularPostList;
        this.oldPopularPostList = oldPopularPostList;
        this.viewPager = viewPager;
        this.viewPagerAdaptor = viewPagerAdaptor;
        viewDestroyed = false;
        this.activity = activity;
    }


    public void viewPagerChanged(String dataType){

        /**
        switch (dataType){
            case "override + NothingChanged":
                break;
            case "override + NewItem":
                onlyNewItem();
                break;
            case "override + itemRemove":
                break;
            case "override + NewItem + itemRemove":
                onlyNewItem();
                break;
        }
        System.out.println("Sdad dataType: " + dataType);
         */
        System.out.println("Sdad vP:" + dataType);
        if (!dataType.equals("override + NothingChanged")){
            System.out.println("Sdad vP" + (viewPager.getScrollState() == ViewPager2.SCROLL_STATE_IDLE));
            if (viewPager.getScrollState() != ViewPager2.SCROLL_STATE_IDLE) {
                callback = new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }

                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        super.onPageScrollStateChanged(state);
                        System.out.println("Sdad vP state:" + state);
                        if (state == 2) {
                            viewPager.unregisterOnPageChangeCallback(this);
                            completeTransactionNoCallback();

                        }
                    }
                };
                viewPager.registerOnPageChangeCallback(callback);
            } else {
                completeTransactionNoCallback();
            }

        }
    }



    private void completeTransactionNoCallback(){
        viewPagerPosition = viewPagerAdaptor.getAdaptorPosition();
        StandardPost post = oldPopularPostList.get(viewPagerPosition);
        Integer newPosition = newPopularPostSet.getPostList().indexOf(post);
        System.out.println("Sdad newPosition:" + newPosition);
        //viewPager.setAdapter(viewPagerAdaptor);
        viewPagerAdaptor.setPopularPostSet(newPopularPostSet);
        viewPagerAdaptor.notifyDataSetChanged();
        if (newPosition > viewPagerPosition){
            if (newPosition != -1) {
                showToast(newPosition);
            } else {
                newPosition = 0;
            }
        }
        viewPager.setCurrentItem(newPosition, false);
    }

    void showToast(Integer newPosition) {
        CardView toastview = activity.findViewById(R.id.PopularHostFakeToast);
        toastview.setVisibility(View.VISIBLE);
        toastview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0, true);
                toastview.setVisibility(View.GONE);
            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (position < newPosition){
                    viewPager.unregisterOnPageChangeCallback(this);
                    toastview.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });




    }



    public void viewDestroyed(){
        this.viewDestroyed = true;
    }

}
