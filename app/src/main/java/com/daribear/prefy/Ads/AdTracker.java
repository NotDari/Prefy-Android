package com.daribear.prefy.Ads;

import android.app.Activity;

import com.google.android.ump.ConsentInformation;

import lombok.Setter;

public class AdTracker {
    private static AdTracker instance;
    private Integer popularTotal, popularCounter;
    private Integer otherTotal, otherCounter;
    @Setter

    private ConsentInformation consentInformation;
    @Setter
    private Activity activity;

    private Interstitial interstitial;

    public AdTracker() {
        popularCounter = 0;
        otherCounter = 0;
        interstitial = new Interstitial();
    }

    public void setTotals(Integer popularTotal, Integer otherTotal){
        this.popularTotal = popularTotal;
        this.otherTotal = otherTotal;
    }

    public void setPopularTotal(Integer popularTotal) {
        this.popularTotal = popularTotal;
    }

    public void setOtherTotal(Integer otherTotal) {
        System.out.println("Sdad otehrTotal: +" + otherTotal);
        this.otherTotal = otherTotal;
    }

    public static AdTracker getInstance(){
        if (instance == null){
            instance = new AdTracker();
        }

        return instance;
    }

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

    public void otherViewed(){
        System.out.println("sdad otherTotal:" + otherTotal);
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

    public void resetCounts(){
        this.popularCounter = 0;
        this.otherCounter = 0;
    }

    public void consentChanged(){
        instance = null;
    }
}
