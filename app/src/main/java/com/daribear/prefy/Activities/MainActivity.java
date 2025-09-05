package com.daribear.prefy.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.daribear.prefy.Ads.AdTracker;
import com.daribear.prefy.Ads.Interstitial;
import com.daribear.prefy.Database.DatabaseHelper;
import com.daribear.prefy.Explore.ExploreHostFragment;
import com.daribear.prefy.Network.ActivityViewModelSaver;
import com.daribear.prefy.Network.ViewModelDataController;
import com.daribear.prefy.Popular.PopularPageHostFragment;
import com.daribear.prefy.R;

import com.daribear.prefy.Report.ReportFragment;
import com.daribear.prefy.Settings.SettingsFragment;
import com.daribear.prefy.SubmitPost.PostStartActivity;
import com.daribear.prefy.SubmitPost.SubmitPostDialog;
import com.daribear.prefy.Utils.AdConsentForm.AdConsentForm;
import com.daribear.prefy.Utils.ErrorChecker;
import com.daribear.prefy.Utils.Permissions.PermissionChecker;
import com.daribear.prefy.Utils.Permissions.PermissionReceived;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.daribear.prefy.Utils.SharedPreferences.Utils;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.remoteconfig.ConfigUpdate;
import com.google.firebase.remoteconfig.ConfigUpdateListener;
import com.google.firebase.remoteconfig.ConfigUpdateListenerRegistration;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;

/**
 * The main activity of the app(the one post login).
 * Holds a bottom nav and fragment container.
 * The bottom nav is used to navigate between fragments and the fragment container displays that change.
 * This activity also handles the remote config and permission handling.
 */
public class MainActivity extends AppCompatActivity {
    private NavHostFragment navHostFragment;
    private BottomNavigationView bottomNav;
    private PostStartActivity postStartActivity;

    private PermissionChecker permissionChecker;

    private ConfigUpdateListenerRegistration remoteConfigListener;

    /**
     * Called when the activity is first created.
     * Sets up default classes such as the popular view model, permissions handling ,ads etc...
     *
     * @param savedInstanceState bundle containing activities previous state(if there is one)
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        postStartActivity = new PostStartActivity();
        postStartActivity.registerForActivityResult(MainActivity.this);
        initPermissions();
        //disablePersistence();
        super.onCreate(savedInstanceState);
        initAuthSaving();
        initDownload();
        getRemoteConfig();
        setContentView(R.layout.activity_main);
        handleBottomNav();
        onNavigationItemChanged();
        ErrorChecker.setActivity(this);
        loadAds();


    }

    /**
     * Initaites the ads. If a consent form has to be shown to the user, this does it.
     * Pre-loads an interstitial ad to be shown
     */
    private void loadAds(){
        AdConsentForm adConsentForm = new AdConsentForm();
        adConsentForm.checkState(this);
        MobileAds.initialize(this);
        Interstitial interstitial = new Interstitial();
        interstitial.loadAd(this);
        AdTracker.getInstance().setActivity(this);
    }

    /**
     * Saves the authToken to the Singleton to use when making requests
     */
    private void initAuthSaving(){
        Utils utils = new Utils(this);
        ServerAdminSingleton.getInstance().setServerAuthToken(utils.loadString(this.getString(R.string.save_auth_token_pref), ""));
        ServerAdminSingleton.getInstance().alterLoggedInUser(this);
    }

    /**
     * Starts the viewModels, which starts loading in data to decrease loading in time.
     */
    private void initDownload(){
        ViewModelDataController dataController = new ViewModelDataController(getApplicationContext());
        dataController.initViewModels();
    }

    /**
     * Function which detects when the navigation fragment is changed.
     * Alters the visibility of the bottom Navigation depending on which fragment loads in.
     */
    private void onNavigationItemChanged(){
        navHostFragment.getNavController().addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                switch (navDestination.getId()){
                    case (R.id.settingsFragment):
                    case (R.id.userProfile):
                    case (R.id.searchFragment):
                    case (R.id.reportFragment):
                    case (R.id.commentsFragment):
                    case (R.id.feedbackFragment):
                    case (R.id.submitPostDialog):
                    case (R.id.exploreCategoriesPostFragment):
                    case (R.id.exploreCategorySelectorFragment):
                    case (R.id.detailsEditFragment):
                    case (R.id.detailsEditGatewayFragment):
                    case (R.id.preferencesFragment):
                    case (R.id.profilePostListItemFragment):
                        alterBottomNavVisibility(false);
                        break;
                    default:
                        alterBottomNavVisibility(true);
                        break;
                }
            }
        });
    }

    /**
     * Handles the bottom navigation performing different actions based on what was clicked.
     * Navigates to the correct fragment if appropriate (e.g. popular page)
     * Creates the post dialog if post required
     */
    private void handleBottomNav(){
        bottomNav = findViewById(R.id.BottomNav);
        bottomNav.setItemIconTintList(null);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.FragmentContainerView);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNav, navController);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.popularPageHostFragment2:
                        item.setChecked(true);
                        NavigationUI.onNavDestinationSelected(item, navController);
                        break;
                    case R.id.submitPostDialog:
                        SubmitPostDialog postDialog = SubmitPostDialog.getInstance(MainActivity.this);
                        postDialog.displaySheet(MainActivity.this);
                        break;
                    case R.id.exploreHostFragment:
                        //Check if its already the exploreHostFragment, so that it scrolls to the top
                        if (navHostFragment.getChildFragmentManager().getFragments().get(0) instanceof ExploreHostFragment){
                            ((ExploreHostFragment) navHostFragment.getChildFragmentManager().getFragments().get(0)).initScrollToTop();
                        } else {

                            item.setChecked(true);
                            NavigationUI.onNavDestinationSelected(item, navController);
                        }
                        break;
                    default:
                        item.setChecked(true);
                        NavigationUI.onNavDestinationSelected(item, navController);
                        break;
                }
                return false;
            }
        });
    }


    /**
     * Initialises firebase remote config and sets the default values in the app
     * It also updates server addresses and ad frequencies dynamically.
     */
    private void getRemoteConfig(){
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        //Active listening
        remoteConfigListener = mFirebaseRemoteConfig.addOnConfigUpdateListener(new ConfigUpdateListener() {
            @Override
            public void onUpdate(ConfigUpdate configUpdate) {
                mFirebaseRemoteConfig.activate().addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        //Listen for api link
                        if (configUpdate.getUpdatedKeys().contains("Api_link")) {
                            ServerAdminSingleton.getInstance().setServerAddress(mFirebaseRemoteConfig.getString("Api_link"));
                        }
                        Integer adCounterChanged = 0;
                        //Listen for popular ad frequency
                        if (configUpdate.getUpdatedKeys().contains("interstitial_popular_frequency")){
                            AdTracker.getInstance().setPopularTotal((int) mFirebaseRemoteConfig.getLong("interstitial_popular_frequency"));
                            adCounterChanged = 1;
                        }
                        //Listen for other ad frequency
                        if (configUpdate.getUpdatedKeys().contains("interstitial_other_frequency")){
                            AdTracker.getInstance().setOtherTotal((int) mFirebaseRemoteConfig.getLong("interstitial_other_frequency"));
                            adCounterChanged = 1;
                        }
                        if (adCounterChanged == 1){
                            AdTracker.getInstance().resetCounts();
                        }
                    }
                });
            }

            @Override
            public void onError(FirebaseRemoteConfigException error) {
                Log.w("TAG", "Config update error with code: " + error.getCode(), error);
            }
        });
    }

    /**
     * Alters the bottom nav visibility.
     * @param visible whether the bottom nav should be visible
     */
    public void alterBottomNavVisibility(Boolean visible){
        if (visible){
            bottomNav.setVisibility(View.VISIBLE);
        }else {
            bottomNav.setVisibility(View.GONE);
        }
    }

    /**
     * Launches and sets the permission checker which is used to get permissions for the user.
     * (e.g. camera)
     */
    private void initPermissions(){
        permissionChecker = new PermissionChecker(MainActivity.this);
        permissionChecker.initLauncher();
    }

    /**
     * Request a permission from the user via the permission checker
     * @param delegate interface delegate which
     * @param permission permission that is being requested
     */
    public void requestPermission(PermissionReceived delegate,String permission){
        permissionChecker.checkPermission(delegate,permission, MainActivity.this);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * When the activity is resumed, resets necessary classes.
     */
    @Override
    protected void onResume() {
        ErrorChecker.setActivity(this);
        AdTracker.getInstance().setActivity(this);
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        ServerAdminSingleton.getInstance().setSqLiteDatabase(databaseHelper.getWritableDatabase());
        super.onResume();
    }

    /**
     * In order to stop errors, nulls the activity in classes when the app is paused.
     */
    @Override
    protected void onPause() {
        ActivityViewModelSaver viewModelSaver = new ActivityViewModelSaver(getApplicationContext());
        if (ServerAdminSingleton.getInstance().getLoggedInId() != null) {
            viewModelSaver.viewDestroyed();
        }
        ErrorChecker.setActivity(null);
        AdTracker.getInstance().setActivity(null);
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        ServerAdminSingleton.getInstance().setSqLiteDatabase(null);
        remoteConfigListener.remove();
        super.onDestroy();
    }

    /**
     * Override the back button to make it go to the previous navigation fragment on the fragment stack
     */
    @Override
    public void onBackPressed() {
        Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
        if (fragment instanceof SettingsFragment){
            bottomNav.setVisibility(View.VISIBLE);
            super.onBackPressed();
        } else if (fragment instanceof ReportFragment){
            ReportFragment reportFragment = (ReportFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
            reportFragment.onBackPressed(reportFragment.getView());
        }
        else {
            super.onBackPressed();
        }

    }


    /**
     * Launch post image retrieval flow
     */
    public void getPostImage(Intent intent){
        postStartActivity.launchImageRetreiver(intent);
    }

    /**
     * Launch post image cropping flow
     */
    public void getPostCrop(Intent intent){
        postStartActivity.launchUcropRetreiver(intent);
    }


}
