package com.daribear.prefy.Ads;

import android.app.Activity;

import com.google.android.ump.ConsentInformation;

import lombok.Setter;

/**
 * Class which counts when a new ad should be shown and how many should be.
 */
public class AdTracker {
    private static AdTracker instance;
    private Integer popularTotal, popularCounter;
    private Integer otherTotal, otherCounter;
    @Setter

    private ConsentInformation consentInformation;
    @Setter
    private Activity activity;

    private Interstitial interstitial;

    //Default constructor
    public AdTracker() {
        popularCounter = 0;
        otherCounter = 0;
        interstitial = new Interstitial();
    }

    //Set the totals for ad tracking
    public void setTotals(Integer popularTotal, Integer otherTotal){
        this.popularTotal = popularTotal;
        this.otherTotal = otherTotal;
    }

    public void setPopularTotal(Integer popularTotal) {
        this.popularTotal = popularTotal;
    }

    public void setOtherTotal(Integer otherTotal) {
        this.otherTotal = otherTotal;
    }

    public static AdTracker getInstance(){
        if (instance == null){
            instance = new AdTracker();
        }

        return instance;
    }

    /**
     * Called when a popular item is viewed, increments the counter.
     * If the counter equals the total, reset the counter and show the ad.
     */
    public void popViewed(){
        if (popularTotal != null){
            if (popularTotal != 0){
                popularCounter += 1;
                if (popularCounter.equals(popularTotal)){
                    popularCounter = 0;
                    if (activity != null) {
                        interstitial.showAd(activity);
                    }
                }
            }
        }
    }
    /**
     * Called when a popular item is viewed, increments the counter.
     * If the counter equals the total, reset the counter and show the ad.
     */
    public void otherViewed(){
        if (otherTotal != null){
            if (otherTotal != 0){
                otherCounter += 1;
                if (otherCounter.equals(otherTotal)){
                    otherCounter = 0;
                    if (activity != null) {
                        interstitial.showAd(activity);
                    }
                }
            }
        }
    }

    //Reset the counters
    public void resetCounts(){
        this.popularCounter = 0;
        this.otherCounter = 0;
    }

    //User changed the consent choices so reset this class
    public void consentChanged(){
        instance = null;
    }
}
