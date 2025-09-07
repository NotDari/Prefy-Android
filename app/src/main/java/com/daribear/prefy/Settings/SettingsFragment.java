package com.daribear.prefy.Settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daribear.prefy.R;
import com.daribear.prefy.Utils.LogOutUtil;
import com.daribear.prefy.Utils.SharedPreferences.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * The fragment which is responsible for handling the settings.
 * Gives the user a list of options to change. If they click on one, sends them to the details edit fragments.
 */
public class SettingsFragment extends Fragment {
    private TextView logoutButton;
    private ImageView cancelButton;
    private Utils utils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_settings, container, false);
        getDefaultData(view);
        getViews(view);
        setUpButtons(view);
        return view;
    }

    /**
     * Get the default settings data.
     */
    private void getDefaultData(View view){
        Context context = view.getContext();
        utils = new Utils(context);
    }

    /**
     * Creates the logout and cancel button to log out or leave this fragment.
     *
     * @param view baseview
     */
    private void getViews(View view){
        alterBottomNavVisibility(true);
        logoutButton = view.findViewById(R.id.SettingsLogoutButton);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        logoutButton.getLayoutParams().width = (int) (screenWidth * .98);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                if (getActivity() == null) {
                    Toast.makeText(getContext().getApplicationContext(), "Logout Failed", Toast.LENGTH_SHORT).show();
                } else {
                    LogOutUtil.Logout(getActivity());
                    LogOutUtil.changeActivity(getActivity());
                }
            }
        });
        cancelButton = view.findViewById(R.id.SettingsTopBarBack);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonvView) {
                Navigation.findNavController(view).navigateUp();
                alterBottomNavVisibility(false);
            }
        });
    }




    /**
     * Sets up the buttons to alter the settings
     * @param view baseviews
     */
    private void setUpButtons(View view){
        setUpPreferences(view);
        setUpFeedBack(view);
        setUpSocialNetworks(view);
        setUpPersonalInformation(view);
        setUpOther(view);
        initRatePrefy(view);
        setUpEmailPassword(view);
    }

    /**
     * Sets up the email password button to edit the email or password.
     * Goe to the details edit Fragment when clicked.
     * @param view baseview to find views
     */
    private void setUpEmailPassword(View view){
        Button button = view.findViewById(R.id.SettingsEmailPasswordButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("detail", "EmailPassword");
                Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_detailsEditGatewayFragment, bundle);
            }
        });
    }

    /**
     * Sets up the preferences button to edit the preferences
     * Goe to the preferences Fragment when clicked.
     * @param view baseview to find views
     */
    private void setUpPreferences(View view){
        Button preferencesButton = view.findViewById(R.id.SettingsPreferencesButton);
        preferencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_preferencesFragment);
            }
        });


    }

    /**
     * Sets up the rate prefy button which sends the user to the playstore to rate the app.
     * @param view baseview to find views
     */
    private void initRatePrefy(View view){
        Context context = view.getContext();
        Button ratePrefyButton = view.findViewById(R.id.SettingsRatePrefyButton);
        ratePrefyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.daribear.prefy");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                context.startActivity(shareIntent);
            }
        });
    }

    /**
     * Sets up the social networks button to edit the social networks.
     * Goe to the details edit Fragment when clicked, with specific params so the social networks will be edited.
     * @param view baseview to find views
     */
    private void setUpSocialNetworks(View view){
        Button socialNetworksButton = view.findViewById(R.id.SettingsSocialNetworksButton);
        socialNetworksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("detail", "SocialMedia");
                if (!isDetached()) {
                    Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_detailsEditGatewayFragment, bundle);
                }
            }
        });
    }
    /**
     * Sets up the social networks button to edit the social networks.
     * Goe to the details edit Fragment when clicked, with specific params so the personal information will be edited.
     * @param view baseview to find views
     */
    private void setUpPersonalInformation(View view){
        Button PersonalInformationButton = view.findViewById(R.id.SettingsAccountDetailsButton);
        PersonalInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("detail", "Personal");
                Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_detailsEditGatewayFragment, bundle);
            }
        });
    }

    /**
     * Sets up a listener for the feedback button which sends the user to the feedback fragment.
     * @param view baseview for finding other views
     */
    private void setUpFeedBack(View view){
        Button preferencesButton = view.findViewById(R.id.SettingsFeedbackButton);
        preferencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_feedbackFragment);
            }
        });
    }

    /**
     * Sets up the other button listener.
     * On click, sends the user to the settings other fragment.
     * @param view baseview to find other views
     */
    private void setUpOther(View view){
        Button otherButton = view.findViewById(R.id.SettingsOtherButton);
        otherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_settingsOtherFragment);
            }
        });
    }

    /**
     * Hides or shows the bottom nav
     * @param hide true to hide the bottom nav, false to show
     */
    private void alterBottomNavVisibility(Boolean hide){
        BottomNavigationView bottomNavigation = getActivity().findViewById(R.id.BottomNav);
        if (hide){
            bottomNavigation.setVisibility(View.GONE);
        }else {
            bottomNavigation.setVisibility(View.VISIBLE);
        }

    }


}