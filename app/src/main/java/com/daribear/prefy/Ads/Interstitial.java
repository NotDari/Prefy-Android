package com.daribear.prefy.Ads;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

/**
 * Class which handles the loading and showing of interstitial ads.
 */
public class Interstitial {
    private static InterstitialAd interstitial;


    public Interstitial() {
    }

    /**
     * Loads an ad using your adUnitid, in preparation of it being shown later.
     * @param activity
     */
    public static void loadAd(Activity activity) {
        AdRequest adRequest = new AdRequest.Builder().build();
        //FILL WITH YOUR adUnitID
        InterstitialAd.load(activity, "", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        Interstitial.interstitial = interstitialAd;
                    }

                });
    }

    /**
     * Shows an interstitial ad
     * @param activity activity to show the ad in
     */
    public void showAd(Activity activity){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (interstitial != null) {
                    interstitial.show(activity);
                }
                loadAd(activity);
            }
        });

    }

}



