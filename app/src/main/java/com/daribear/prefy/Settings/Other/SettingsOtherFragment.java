package com.daribear.prefy.Settings.Other;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.daribear.prefy.R;


public class SettingsOtherFragment extends Fragment {
    private ImageView backButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_other, container, false);
        getViews(view);
        initTermsAndPrivacy(view);
        return view;
    }

    private void getViews(View view){
        backButton = view.findViewById(R.id.SettingsOtherTopBarBack);

        initExit(view);
    }

    private void initExit(View view){
        Context context = view.getContext();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

    }

    private void initTermsAndPrivacy(View view){
        Context context = view.getContext();
        Button termsButton, privacyButton, cookieButton, acceptableUseButton;
        termsButton = view.findViewById(R.id.SettingsOtherTermsConditions);
        privacyButton = view.findViewById(R.id.SettingsOtherPrivacyPolicy);
        cookieButton = view.findViewById(R.id.SettingsOtherCookiePolicy);
        acceptableUseButton = view.findViewById(R.id.SettingsOtherAcceptableUsePolicy);
        termsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl(getString(R.string.terms_and_conditions));
            }
        });
        privacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl(getString(R.string.privacy_policy));
            }
        });
        cookieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl(getString(R.string.cookie_policy));
            }
        });
        acceptableUseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUrl(getString(R.string.acceptable_use));
            }
        });
    }

    private void openUrl(String url){
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


}