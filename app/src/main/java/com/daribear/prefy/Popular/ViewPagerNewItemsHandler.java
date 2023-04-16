package com.daribear.prefy.Popular;

import android.app.Activity;
import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import com.daribear.prefy.R;
import com.daribear.prefy.customClasses.StandardPost;

import java.util.ArrayList;

public class ViewPagerNewItemsHandler {
    private ArrayList<PopularPost>oldPopularPostList;
    private PopularPostSet newPopularPostSet;
    private ViewPager2 viewPager;
    private PopularPageHostFragment.PopularPagerAdaptor viewPagerAdaptor;
    private Boolean viewDestroyed;
    private Integer viewPagerPosition = 0;
    private ViewPager2.OnPageChangeCallback callback;
    private Activity activity;

    public ViewPagerNewItemsHandler(PopularPostSet updatedPopularPostList, ArrayList<PopularPost> oldPopularPostList, ViewPager2 viewPager, PopularPageHostFragment.PopularPagerAdaptor viewPagerAdaptor, Activity activity) {
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
        if (!dataType.equals("override + NothingChanged")){
            if (viewPager.getScrollState() != ViewPager2.SCROLL_STATE_IDLE) {
                if (callback == null) {
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
                            if (state == 2) {
                                //viewPager.unregisterOnPageChangeCallback(this);
                                if (!dataType.equals("Update")) {
                                    completeTransactionNoCallback();
                                }

                            }
                        }
                    };
                    viewPager.registerOnPageChangeCallback(callback);
                }
            } else {
                if (!dataType.equals("Update")) {
                    completeTransactionNoCallback();
                }
            }

        }
    }



    private void completeTransactionNoCallback(){
        viewPagerPosition = viewPagerAdaptor.getAdaptorPosition();
        StandardPost post = oldPopularPostList.get(viewPagerPosition);
        System.out.println("Sdad pos0 question:" + newPopularPostSet.getPostList().get(0).getQuestion());
        Integer newPosition = newPopularPostSet.getPostList().indexOf(post);
        System.out.println("Sdad oldPosition: " + viewPagerPosition + "newPosition:" + newPosition);
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
