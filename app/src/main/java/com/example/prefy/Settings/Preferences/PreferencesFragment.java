package com.example.prefy.Settings.Preferences;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.prefy.R;
import com.example.prefy.Utils.Utils;


public class PreferencesFragment extends Fragment {
    private ImageView backButton;
    private TextView textBack;
    private Switch darkModeSwitch, autoScrollSwitch;
    private Utils utils;
    private Boolean temporaryDarkMode, temporaryAutoScroll;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);
        getViews(view);
        initSwitches(view);
        return view;
    }

    private void getViews(View view){
        utils = new Utils(getActivity().getApplicationContext());
        backButton = view.findViewById(R.id.PreferencesTopBarBack);
        textBack = view.findViewById(R.id.PreferencesTopBarTextBack);
        darkModeSwitch = view.findViewById(R.id.settingsDarkMode);
        autoScrollSwitch = view.findViewById(R.id.settingsAutoScroll);
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
        textBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });
    }

    private void initSwitches(View view){
        Context context = view.getContext();
        temporaryDarkMode = utils.loadBoolean(context.getString(R.string.dark_mode_pref), false);
        darkModeSwitch.setChecked(temporaryDarkMode);
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                temporaryDarkMode = b;
                if (temporaryDarkMode){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                utils.saveBoolean(context.getString(R.string.dark_mode_pref), temporaryDarkMode);
            }
        });
        temporaryAutoScroll = utils.loadBoolean(context.getString(R.string.auto_scroll_pref), false);
        autoScrollSwitch.setChecked(temporaryAutoScroll);
        autoScrollSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                temporaryAutoScroll = b;
                utils.saveBoolean(context.getString(R.string.auto_scroll_pref), temporaryAutoScroll);
            }
        });
    }

}