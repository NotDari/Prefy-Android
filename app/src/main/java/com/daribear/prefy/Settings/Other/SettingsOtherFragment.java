package com.daribear.prefy.Settings.Other;

import android.content.Context;
import android.content.Intent;
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
        Button termsButton, privacyButton;
        termsButton = view.findViewById(R.id.SettingsOtherTermsConditions);
        privacyButton = view.findViewById(R.id.SettingsOtherPrivacyPolicy);
        termsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://daribear.com");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                context.startActivity(shareIntent);
            }
        });
        privacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://daribear.com");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                context.startActivity(shareIntent);
            }
        });
    }


}