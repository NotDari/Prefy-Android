package com.daribear.prefy.Utils.AdConsentForm;

import android.app.Activity;

import androidx.annotation.Nullable;

import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

/**
 * The consent form for ads to comply with gdpr.
 * Checks if they are in a region which requires consent.
 * Shows the form if required.
 */
public class AdConsentForm {
    private ConsentInformation consentInformation;
    private ConsentForm consentForm;

    /**
     * Checks that state of the consent form for ads and GDPR compliance.
     * If the consent form is available it calls load form to show the form.
     * @param activity activity to show the ads in
     */
    public void checkState(Activity activity){
        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(activity);
        consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
                    @Override
                    public void onConsentInfoUpdateSuccess() {
                        if (consentInformation.isConsentFormAvailable()) {
                            loadForm(activity);
                        }
                    }
                },
                new ConsentInformation.OnConsentInfoUpdateFailureListener() {
                    @Override
                    public void onConsentInfoUpdateFailure(FormError formError) {

                    }
                });
    }

    /**
     * The loading of the consent form.
     * Shows the consent form if they are in a required region, and haven't already given consent.
     * @param activity the activity to shown the consent form in.
     */
    private void loadForm(Activity activity) {
        UserMessagingPlatform.loadConsentForm(
                activity,
                new UserMessagingPlatform.OnConsentFormLoadSuccessListener() {
                    @Override
                    public void onConsentFormLoadSuccess(ConsentForm consentForm) {
                        AdConsentForm.this.consentForm = consentForm;
                        //If they are in a region which requires consent.
                        if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                            consentForm.show(
                                    activity,
                                    new ConsentForm.OnConsentFormDismissedListener() {
                                        @Override
                                        public void onConsentFormDismissed(@Nullable FormError formError) {
                                            if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.OBTAINED) {
                                                // App can start requesting ads.
                                            }

                                            // Handle dismissal by reloading form.
                                            loadForm(activity);
                                        }
                                    });
                        }
                    }
                },
                new UserMessagingPlatform.OnConsentFormLoadFailureListener() {
                    @Override
                    public void onConsentFormLoadFailure(FormError formError) {
                        // Handle Error.
                    }
                }
        );
    }
}
