package com.daribear.prefy.Activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.daribear.prefy.Explore.ExploreHostFragment;
import com.daribear.prefy.Network.ActivityViewModelSaver;
import com.daribear.prefy.Network.ViewModelDataController;
import com.daribear.prefy.Popular.PopularPageHostFragment;
import com.daribear.prefy.R;

import com.daribear.prefy.Report.ReportFragment;
import com.daribear.prefy.Settings.SettingsFragment;
import com.daribear.prefy.SubmitPost.PostStartActivity;
import com.daribear.prefy.SubmitPost.SubmitPostDialog;
import com.daribear.prefy.Utils.ErrorChecker;
import com.daribear.prefy.Utils.Permissions.PermissionChecker;
import com.daribear.prefy.Utils.Permissions.PermissionReceived;
import com.daribear.prefy.Utils.ServerAdminSingleton;
import com.daribear.prefy.Utils.Utils;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private ArrayList<String> uidList;
    private ArrayList<String> emailList;
    private NavHostFragment navHostFragment;
    private BottomNavigationView bottomNav;
    private PostStartActivity postStartActivity;

    private PermissionChecker permissionChecker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //Utils sharedpreferences = new Utils(getApplicationContext());
        //Checking if nightmode
        //if (!sharedpreferences.loadDarkMode()) {
         //  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
       // } else if (sharedpreferences.loadDarkMode()) {
        //    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //}
        postStartActivity = new PostStartActivity();
        postStartActivity.registerForActivityResult(MainActivity.this);
        initPermissions();
        //disablePersistence();
        super.onCreate(savedInstanceState);
        initAuthSaving();
        initDownload();
        setContentView(R.layout.activity_main);
        handleBottomNav();
        onNavigationItemChanged();
        ErrorChecker.setActivity(this);
        loadAds();

    }

    private void loadAds(){
        MobileAds.initialize(this);
        Interstitial interstitial = new Interstitial();
        interstitial.loadAd(this);
        AdTracker.getInstance().setActivity(this);
    }

    private void initAuthSaving(){
        Utils utils = new Utils(this);
        ServerAdminSingleton.getInstance().setServerAuthToken(utils.loadString(this.getString(R.string.save_auth_token_pref), ""));
        ServerAdminSingleton.getInstance().alterLoggedInUser(this);
    }


    private void initDownload(){
        ViewModelDataController dataController = new ViewModelDataController(getApplicationContext());
        dataController.initViewModels();
    }

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
                        if (navHostFragment.getChildFragmentManager().getFragments().get(0) instanceof PopularPageHostFragment){
                            ((PopularPageHostFragment) navHostFragment.getChildFragmentManager().getFragments().get(0)).setViewPager0();
                        } else {
                            //NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.popularPageHostFragment, true).setRestoreState(true).build();
                            //navController.navigate(item.getItemId(), null, navOptions);
                            item.setChecked(true);
                            NavigationUI.onNavDestinationSelected(item, navController);
                        }
                        break;
                    case R.id.submitPostDialog:
                        SubmitPostDialog postDialog = SubmitPostDialog.getInstance(MainActivity.this);
                        postDialog.displaySheet(MainActivity.this);
                        break;
                    case R.id.exploreHostFragment:
                        if (navHostFragment.getChildFragmentManager().getFragments().get(0) instanceof ExploreHostFragment){
                            ((ExploreHostFragment) navHostFragment.getChildFragmentManager().getFragments().get(0)).initScrollToTop();
                        } else {
                            //NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.exploreHostFragment, true).setRestoreState(true).build();
                            //navController.navigate(item.getItemId(), null, navOptions);
                            //item.setChecked(true);
                            item.setChecked(true);
                            NavigationUI.onNavDestinationSelected(item, navController);
                        }
                        break;
                    default:
                        //NavOptions navOptions = new NavOptions.Builder().setPopUpTo(item.getItemId(), true).setRestoreState(true).build();
                        //navController.navigate(item.getItemId(), null, navOptions);
                        //item.setChecked(true);
                        item.setChecked(true);
                        NavigationUI.onNavDestinationSelected(item, navController);
                        break;
                }
                return false;
            }
        });
    }


    public void alterBottomNavVisibility(Boolean visible){
        if (visible){
            bottomNav.setVisibility(View.VISIBLE);
        }else {
            bottomNav.setVisibility(View.GONE);
        }
    }

    private void initPermissions(){
        permissionChecker = new PermissionChecker(MainActivity.this);
        permissionChecker.initLauncher();
    }

    public void requestPermission(PermissionReceived delegate,String permission){
        permissionChecker.checkPermission(delegate,permission, MainActivity.this);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        ErrorChecker.setActivity(this);
        AdTracker.getInstance().setActivity(this);
        super.onResume();
    }

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

    public void getPostImage(Intent intent){
        postStartActivity.launchImageRetreiver(intent);
    }

    public void getPostCrop(Intent intent){
        postStartActivity.launchUcropRetreiver(intent);
    }


}
