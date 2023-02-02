package com.example.prefy.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.prefy.Explore.ExploreHostFragment;
import com.example.prefy.Network.ActivityViewModelSaver;
import com.example.prefy.Network.ViewModelDataController;
import com.example.prefy.Popular.PopularPageHostFragment;
import com.example.prefy.R;

import com.example.prefy.Report.ReportFragment;
import com.example.prefy.Settings.SettingsFragment;
import com.example.prefy.SubmitPost.PostStartActivity;
import com.example.prefy.SubmitPost.SubmitPostDialog;
import com.example.prefy.Utils.ServerAdminSingleton;
import com.example.prefy.Utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private ArrayList<String> uidList;
    private ArrayList<String> emailList;
    private NavHostFragment navHostFragment;
    private BottomNavigationView bottomNav;
    private PostStartActivity postStartActivity;

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
        //disablePersistence();
        super.onCreate(savedInstanceState);
        initAuthSaving();
        initDownload();
        setContentView(R.layout.activity_main);
        handleBottomNav();

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

    private void handleBottomNav(){
        System.out.println("Sdad uid:" + FirebaseAuth.getInstance().getUid());
        bottomNav = findViewById(R.id.BottomNav);
        bottomNav.setItemIconTintList(null);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.FragmentContainerView);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNav, navController);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.popularPageHostFragment:
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        ActivityViewModelSaver viewModelSaver = new ActivityViewModelSaver(getApplicationContext());
        if (ServerAdminSingleton.getInstance().getLoggedInId() != null) {
            viewModelSaver.viewDestroyed();
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (navHostFragment.getChildFragmentManager().getFragments().get(0) instanceof SettingsFragment){
            bottomNav.setVisibility(View.VISIBLE);
            super.onBackPressed();
        } else if (navHostFragment.getChildFragmentManager().getFragments().get(0) instanceof ReportFragment){
            ReportFragment reportFragment = (ReportFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
            reportFragment.onBackPressed(reportFragment.getView());
        } else {
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
